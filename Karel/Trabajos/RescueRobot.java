import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.locks.ReentrantLock;

public class RescueRobot extends Robot implements Runnable {
    private static final int MAX_PERSONS = 4;
    private int personsCarried = 0;
    private static ReentrantLock[][] gridLocks = new ReentrantLock[16][16]; // Para evitar colisiones

    private int currentStreet;
    private int currentAvenue;

    public RescueRobot(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
        World.setupThread(this);

        // Inicializar la posición inicial del robot
        this.currentStreet = street;
        this.currentAvenue = avenue;

        // Inicializar gridLocks para evitar que los robots colisionen en la misma posición
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (gridLocks[i][j] == null) {
                    gridLocks[i][j] = new ReentrantLock();
                }
            }
        }
    }

    @Override
    public void run() {
        // moveToEntrance();
        // evacuateZone();
        goToStart();
    }

    public void goToStart(){
        for(int street=1; street <= 1; street++){
            for (int avenue = 1; avenue <= 3; avenue++){
                moveTo(street, avenue);
            }
        }
    }

    // Método para recorrer la zona en riesgo y buscar personas
    public void evacuateZone() {
        for (int street = 1; street <= 2; street++) {
            for (int avenue = 1; avenue <= 2; avenue++) {
                moveTo(street, avenue);
                // // Si el robot detecta una persona (sirena), intenta recogerla
                // if (nextToABeeper()) {
                //     pickPerson();
                // }

                // // Si está transportando 4 personas, vuelve al cuartel general para dejarlas
                // if (personsCarried == MAX_PERSONS) {
                //     moveToBase();
                //     depositPersons();
                // }
            }

        }
        turnOff();
    }

    // Método para mover el robot a una posición específica
    // Método para mover el robot a una posición específica
private void moveTo(int targetStreet, int targetAvenue) {
    // Mover verticalmente hasta alcanzar la calle objetivo
    while (currentStreet != targetStreet) {
        if (currentStreet < targetStreet) {
            faceSouth();
        } else {
            faceNorth();
        }
        if (frontIsClear()) {
            move();
            currentStreet += (facingSouth() ? 1 : -1); // Actualiza la posición manualmente
        }
    }

    // Mover horizontalmente hasta alcanzar la avenida objetivo
    while (currentAvenue != targetAvenue) {
        if (currentAvenue < targetAvenue) {
            faceEast();
        } else {
            faceWest();
        }
        if (frontIsClear()) {
            move();
            currentAvenue += (facingEast() ? 1 : -1); // Actualiza la posición manualmente
        }
    }
}

    // Métodos auxiliares para verificar la posición actual
    private boolean isAtStreet(int street) {
        return currentStreet == street;
    }

    private boolean isAtAvenue(int avenue) {
        return currentAvenue == avenue;
    }

    // Métodos auxiliares para orientar el robot
    private void faceNorth() {
        while (!facingNorth()) {
            turnLeft();
        }
    }

    private void faceSouth() {
        while (!facingSouth()) {
            turnLeft();
        }
    }

    private void faceEast() {
        while (!facingEast()) {
            turnLeft();
        }
    }

    private void faceWest() {
        while (!facingWest()) {
            turnLeft();
        }
    }
}
