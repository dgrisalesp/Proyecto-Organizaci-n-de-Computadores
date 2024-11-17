import kareltherobot.*;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.print.attribute.standard.PrinterResolution;

class RescueRobot extends Robot implements Runnable {
    private final SharedGrid sharedGrid;
    private static final int MAX_PEOPLE = Config.MAX_PEOPLE;
    private int rescuedPeople = 0;
    private int robotId;
    private int currentStreet;
    private int currentAvenue;
    private RobotState currentState;
    private static final RescueManager manager = new RescueManager();
    private byte[][] matrix;
    private boolean destiny;
    private int maxI;
    private int maxJ;
    private int minI;
    private int minJ;

    public RescueRobot(int robotId, int street, int avenue, Direction direction, int beepers, Color color, SharedGrid sharedGrid, int minI) {
        super(street, avenue, direction, beepers, color);
        this.sharedGrid = sharedGrid;
        this.robotId = robotId;
        this.currentAvenue = avenue;
        this.currentStreet = street;
        this.minI=minI;
        this.minJ=4;
        this.maxJ=15;
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
    public void turnLeft() {
        super.turnLeft();
        int[][] directions = {
            { 1, 0 }, // north
            { -1, 0 }, // south
            { 0, -1 }, // west
            { 0, 1 } // east
    };
    int direction = facingNorth() ? 0 : facingSouth() ? 1 : facingWest() ? 2 : 3;
    int nx, ny;
    
    nx = (this.getStreet() -1)* 2 + directions[direction][0];
    ny = (this.getAvenue()-1) * 2 + directions[direction][1];
    if (nx >= 0 && nx < this.sharedGrid.getGrid().length && ny >= 0 && ny < this.sharedGrid.getGrid()[0].length && this.sharedGrid.getValue(nx,ny) == 5) {
        if (frontIsClear()){
            this.sharedGrid.setValue(nx, ny, 1);
        }else{
            this.sharedGrid.setValue(nx, ny, 0);
        }
    }
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

    public int moveToPosition(int targetStreet, int targetAvenue) {
        if (getStreet() != targetStreet) {
            if (getStreet() < targetStreet)
                alignNorth();
            else
                alignSouth();
            while (getStreet() != targetStreet) {
                if (frontIsClear())
                    move();
                else
                    return -1;
            }
        }
        if (getAvenue() != targetAvenue) {
            if (getAvenue() < targetAvenue)
                alignEast();
            else
                alignWest();
            while (getAvenue() != targetAvenue) {
                if (frontIsClear())
                    move();
                else
                    return -1;
            }
        }
        return 1;
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
    public void setDestiny(boolean destiny){
        this.destiny=destiny;
    }

    public void rescuePeople() {
        synchronized (manager) {
            int temp=this.sharedGrid.getValue((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2);
            while (nextToABeeper() && rescuedPeople < MAX_PEOPLE && this.sharedGrid.getValue((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2) != -1) {
                this.sharedGrid.setValue((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2,-1);
                pickBeeper();
                rescuedPeople++;
                log("Rescued a person. Total rescued: " + rescuedPeople);
            }
            this.sharedGrid.setValue((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2,temp);

            if (nextToABeeper()){
                this.sharedGrid.addBeeper(this.getStreet(), this.getAvenue());
            }
        }

    }

    public ArrayList<Point> findPath(Point current, Point destination, boolean seek) {
        
        Queue<Register> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        visited.add(current);
        ArrayList<Point> path = new ArrayList<>();
        path.add(current);
        Register register = new Register(current, path);
        queue.add(register);
        int[][] directions = {
                { 0, 1 }, // right
                { 1, 0 }, // down
                { 0, -1 }, // left
                { -1, 0 } // up
        };
        while (!queue.isEmpty()) {
            Register currentRegister = queue.poll();
            ArrayList<Point> currentPath = currentRegister.getPath();
            Point currentPoint = currentRegister.getPoint();

            if (currentPoint.equals(destination)) {
                // for (Point point : currentPath) {
                //     System.out.println(point);
                // }
                ArrayList<Point> finalPath = new ArrayList<>();
                for (Point point : currentPath) {
                    if (point.getX() % 2 == 0 && point.getY() % 2 == 0) {
                        finalPath.add(new Point(point.getX() / 2 + 1, point.getY() / 2 + 1));
                    }
                }
                return finalPath;
            }
            int x = currentPoint.getX();
            int y = currentPoint.getY();

            for (int[] direction : directions) {
                int nx = x + direction[0];
                int ny = y + direction[1];
                int nx2 = x + 2 * direction[0];
                int ny2 = y + 2 * direction[1];
                if (nx >= 0 && nx < this.sharedGrid.getLength() && ny >= 0 && ny < this.sharedGrid.getWidth() && this.sharedGrid.getValue(nx, ny) != 0
                        && !visited.contains(new Point(nx, ny)) && nx2 >= 0 && nx2 < this.sharedGrid.getLength() && ny2 >= 0
                        && ny2 < this.sharedGrid.getWidth() && !visited.contains(new Point(nx2, ny2)) && (!seek || (seek && this.sharedGrid.getValue(nx2,ny2) == 1))) {
                            
                    // System.out.println("I printed something");
                    visited.add(new Point(nx2,ny2));
                    visited.add(new Point(nx, ny));
                    ArrayList<Point> newPath = new ArrayList<>(currentPath);
                    newPath.add(new Point(nx2, ny2));
                    Register newRegister = new Register(new Point(nx2, ny2), newPath);
                    queue.add(newRegister);
                }
            }
        }
        return null;
    }

    public Point moving(ArrayList<Point> path) {
        int i=0;
        for (Point point : path) {
            // System.out.println("Calling moveToPosition: "+point.getX()+",
            // "+point.getY());

            this.sharedGrid.setValue((this.getStreet()-1) * 2,(this.getAvenue()-1) * 2,1);
            int answer = moveToPosition(point.getX(), point.getY());

            if (answer != 1) {
                int[][] directions = {
                        { 1, 0 }, // north
                        { -1, 0 }, // south
                        { 0, -1 }, // west
                        { 0, 1 } // east
                };
                int direction = facingNorth() ? 0 : facingSouth() ? 1 : facingWest() ? 2 : 3;
                int nx, ny;
                nx = (this.getStreet() -1)* 2 + directions[direction][0];
                ny = (this.getAvenue()-1) * 2 + directions[direction][1];
                
                try {
                    this.sharedGrid.setValue(nx,ny,0);
                } catch (Exception e) {
                    
                }
                return point;
            } else {
                i++;
                if (nextToABeeper() && (this.getAvenue()-1) * 2 >=8 && (this.getAvenue()-1) * 2 <= 28) {
                    this.rescuingPeople();
                }
            }
        }
        return path.get(path.size() - 1);
    }
    
    
    
    public void rescuingPeople(){
    this.rescuePeople();
    if (this.rescuedPeople>=4 || this.destiny==true){
        Point currentPoint= new Point((this.getStreet()-1)*2, (this.getAvenue()-1)*2);
        Point destinationPoint = new Point(0, 2);
        do {
            ArrayList<Point> newPath = this.findPath(currentPoint, destinationPoint, true);
            Point result=this.moving(newPath);
            currentPoint = new Point((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2);
        } while (!currentPoint.equals(destinationPoint));


        while(anyBeepersInBeeperBag()){
            putBeeper();
            this.rescuedPeople--;
        }   
    }
}

    // public void printMatrix() { 
    //     for (int i = 0; i < this.sharedGrid.getLength(); i++) {
    //         for (int j = 0; j < this.sharedGrid.getWidth(); j++) {
    //             System.out.print(this.sharedGrid.getValue(i,j) + " ");
    //         }
    //         System.out.println();
    //     }
    // }
    public void goTo(int x, int y){
        
     Point current = new Point(2*(this.getStreet()-1), 2*(this.getAvenue()-1));
        Point destination = new Point(2*(x-1),2*(y-1) );
        Point result;
        do {
            ArrayList<Point> path = this.findPath(current, destination, false);
            result = this.moving(path);
            current = new Point((this.getStreet()-1) * 2, (this.getAvenue()-1) * 2);
        } while (!current.equals(destination));
    
    }
    public void explorer(){
        for (int i=this.minI; i<=this.maxI; i++){
            for (int j=this.minJ; j<=this.maxJ; j++){
                if (this.sharedGrid.isBeeper()){
                    Point beeper=this.sharedGrid.takeBeeper();
                    if (beeper != null){
                    this.sharedGrid.addCruce(i, j);
                    this.goTo(beeper.getX(), beeper.getY());
                    this.waiting();
                    return;
                    }
                }
                if (this.sharedGrid.getValue(2*(i-1),2*(j-1))==5){
                    this.goTo(i,j);
                }
            }
        }
        this.sharedGrid.addCompleted();
        this.waiting();
    }
    public int getMaxI(){
        return this.maxI;
    }
    public int getMinI(){
        return this.minI;
    }
    public void setMaxI(int minI) {
        int robots = this.sharedGrid.getRobots();
        int length = this.sharedGrid.getLength() / 2;
        int div = length / robots;
        int remainder = length % robots;  // Handle any remainder in division
    
        int tempMax = 0;
    
        for (int i = 0; i < robots; i++) {
            // Calculate the range for each robot based on div and remainder
            int currentMax = tempMax + div + (i < remainder ? 1 : 0); // Distribute remainder across initial robots
    
            if (minI < currentMax) {
                this.maxI = currentMax;
                return; // Exit early after setting maxI
            }
    
            tempMax = currentMax; // Move to next range for the next robot
        }
    
        // If minI is outside calculated ranges, set maxI to the full length (edge case)
        this.maxI = length;
    }
    public void setMinI(int minI){
        this.minI=minI;
    }
    public void setMinJ(int minJ){
        this.minJ=minJ;
    }
    public void waiting(){
        if (anyBeepersInBeeperBag()){
            this.goTo(1,2);
            this.rescuingPeople();
        }
        while (this.sharedGrid.getCompleted()<this.sharedGrid.getRobots() || (this.sharedGrid.isBeeper() || this.sharedGrid.isCruce())){
            if (this.sharedGrid.isBeeper()){
                Point beeper=this.sharedGrid.takeBeeper();
                if (beeper != null){
                    this.goTo(beeper.getX(), beeper.getY());
                }
            }
            if (this.sharedGrid.isCruce()){
                Point cruce=this.sharedGrid.takeCruce();
                if (cruce != null){
                    this.goTo(cruce.getX(), cruce.getY());
                    this.setMinI(cruce.getX());
                    this.setMinJ(cruce.getY());
                    this.setMaxI(cruce.getX());
                    log("Le hice un cruce");
                    this.explorer();
                }
            }
            
        }
        this.goTo(this.robotId,1);
        turnOff();
    }
    public void turnOff(){
        alignNorth();
        super.turnOff();
    }
    
    public static void main(String[] args) {
        if (args.length < 1 || !args[0].matches("\\d+")) {
            throw new IllegalArgumentException("Debe proporcionar un número válido de robots como argumento.");
        }

        int robotCount = Integer.parseInt(args[0]);
        World.setDelay(Config.WORLD_DELAY);
        World.readWorld(Config.WORLD_FILE);
        World.setVisible(true);
        SharedGrid sharedGrid = new SharedGrid(robotCount);
        int length=(int)sharedGrid.getLength()/2;
        int div=(int)length/robotCount;
        int min=1;
        for (int i = 0; i < robotCount; i++) {
            // RescueRobot robot = new RescueRobot(i + 1, i + 1, 1, East, 0,
            // Config.getColor());
            
            RescueRobot robot = new RescueRobot(i + 1, 1, 1, East, 0, Config.getColor(),sharedGrid, min);
            robot.setMaxI(min);
            min+=div;

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
        // robot.log("Initializing position...");
        // robot.moveToPosition(1, 5);
        robot.explorer();
        robot.setState(new SearchingState());
    }
}

class ApproachingState implements RobotState {
    public void handle(RescueRobot robot) {
        robot.log("Approaching to entry...");

        // Point current = new Point((robot.getStreet()-1)*2, (robot.getAvenue()-1)*2);
        // Point destination = new Point(14, 26);
        // Point result;
        // do {
        //     ArrayList<Point> path = robot.findPath(current, destination, false);
        //     for (Point point : path) {
        //         System.out.println(point + " ");
        //     }
        //     System.out.println();
        //     System.out.println();
        //     result = robot.moving(path);
        //     System.out.println("Result that arrived: " + result);
        //     current = new Point((robot.getStreet()-1) * 2, (robot.getAvenue()-1) * 2);
        //     System.out.println();
        //     System.out.println();
        //     System.out.println();
        //     System.out.println(current);
        //     robot.printMatrix();
        //     System.out.println();
        //     System.out.println();
        // } while (!current.equals(destination));
    }
}

class SearchingState implements RobotState {
    public void handle(RescueRobot robot) {
        // robot.log("Searching for people...");
       
        // robot.log("Finished");
        // robot.setDestiny(true);
        // robot.rescuingPeople();
        // robot.log("Finished RescuingPeople");
        // current.setX((robot.getStreet()-1)*2);
        // current.setY((robot.getAvenue()-1)*2);
        // destination=new Point(0,0);
        // do {
        //     ArrayList<Point> path = robot.findPath(current, destination, false);
        //     result = robot.moving(path);
        //     current = new Point((robot.getStreet()-1) * 2, (robot.getAvenue()-1) * 2);
        // } while (!current.equals(destination));
        // robot.log("Ready to turn off");
        // robot.turnOff();
        // while (true){

        // }
        // robot.rescuePeople();
        // robot.setState(new ReturningState());
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
