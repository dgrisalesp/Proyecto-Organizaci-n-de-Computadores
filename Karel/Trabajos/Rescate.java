import kareltherobot.*;
import java.awt.Color;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class RescueRobot extends Robot implements Runnable {
    private static final int MAX_PEOPLE = Config.MAX_PEOPLE;
    private int rescuedPeople = 0;
    private int robotId;
    private int currentStreet;
    private int currentAvenue;
    private RobotState currentState;
    private static final RescueManager manager = new RescueManager();

    public RescueRobot(int robotId, int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
        this.robotId = robotId;
        this.currentAvenue = avenue;
        this.currentStreet = street;
        this.currentState = new InitializingState();
    }

    @Override
    public void run() {
        while (true) {
            currentState.handle(this);
        }
    }

    public void setState(RobotState newState) {
        this.currentState = newState;
    }

    public void log(String message) {
        System.out.println("Robot " + robotId + ": " + message);
    }

    public void move() {
        super.move();
        updatePosition();
    }

    private void updatePosition() {
        if (facingNorth()) {
            currentStreet++;
        } else if (facingSouth()) {
            currentStreet--;
        } else if (facingEast()) {
            currentAvenue++;
        } else if (facingWest()) {
            currentAvenue--;
        }
    }

    public int getStreet() {
        return currentStreet;
    }

    public int getAvenue() {
        return currentAvenue;
    }

    public void moveToPosition(int targetStreet, int targetAvenue) {
        if (getStreet() != targetStreet) {
            if (getStreet() < targetStreet) alignNorth();
            else alignSouth();
            while (getStreet() != targetStreet) move();
        }
        if (getAvenue() != targetAvenue) {
            if (getAvenue() < targetAvenue) alignEast();
            else alignWest();
            while (getAvenue() != targetAvenue) move();
        }
    }

    private void alignNorth() {
        while (!facingNorth()) {
            turnLeft();
        }
    }

    private void alignSouth() {
        while (!facingSouth()) {
            turnLeft();
        }
    }

    private void alignEast() {
        while (!facingEast()) {
            turnLeft();
        }
    }

    private void alignWest() {
        while (!facingWest()) {
            turnLeft();
        }
    }

    public void rescuePeople() {
        synchronized (manager) {
            while (nextToABeeper() && rescuedPeople < MAX_PEOPLE) {
                pickBeeper();
                rescuedPeople++;
                log("Rescued a person. Total rescued: " + rescuedPeople);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || !args[0].matches("\\d+")) {
            throw new IllegalArgumentException("Debe proporcionar un número válido de robots como argumento.");
        }

        int robotCount = Integer.parseInt(args[0]);
        World.setDelay(Config.WORLD_DELAY);
        World.readWorld(Config.WORLD_FILE);
        World.setVisible(true);

        for (int i = 0; i < robotCount; i++) {
            RescueRobot robot = new RescueRobot(i + 1, i + 1, 1, East, 0, Config.getColor());
            Thread thread = new Thread(robot);
            thread.start();
        }
    }
}

interface RobotState {
    void handle(RescueRobot robot);
}

class InitializingState implements RobotState {
    public void handle(RescueRobot robot) {
        robot.log("Initializing position...");
        robot.moveToPosition(1, 1);
        robot.setState(new ApproachingState());
    }
}

class ApproachingState implements RobotState {
    public void handle(RescueRobot robot) {
        robot.log("Approaching to entry...");
        robot.moveToPosition(1, 5);
        robot.setState(new SearchingState());
    }
}

class SearchingState implements RobotState {
    public void handle(RescueRobot robot) {
        robot.log("Searching for people...");
        robot.moveToPosition(9, 15);
        robot.rescuePeople();
        robot.setState(new ReturningState());
    }
}

class ReturningState implements RobotState {
    public void handle(RescueRobot robot) {
        robot.log("Returning to base...");
        robot.moveToPosition(1, 1);
        while (robot.anyBeepersInBeeperBag()) {
            robot.putBeeper();
        }
        robot.log("Completed rescue mission.");
        robot.turnOff();
    }
}

class RescueManager {
    public static volatile boolean isAreaEmpty = false;
    public static final ConcurrentMap<String, Boolean> entrances = new ConcurrentHashMap<>();
}

class Config {
    public static final int MAX_PEOPLE = 4;
    public static final int WORLD_DELAY = 15;
    public static final String WORLD_FILE = "MundoEvacuacion.kwld";

    public static Color getColor() {
        Color[] colors = { 
            Color.blue, 
            Color.red, 
            Color.green, 
            Color.yellow, 
            Color.orange 
        };
        return colors[(int) (Math.random() * colors.length)];
    }
}
