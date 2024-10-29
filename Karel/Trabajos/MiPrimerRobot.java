import kareltherobot.*;
import java.awt.Color;

// Definición de la clase Racer que extiende Robot y permite ejecutar en un hilo
class Racer extends Robot implements Runnable {
    public Racer(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
        World.setupThread(this); // Configuración del hilo para el robot
    }

    // Método que define el recorrido del robot
    public void race() {
        // Mover el robot 4 pasos
        for (int i = 0; i < 4; i++) {
            if (frontIsClear()) {
                move();
            }
        }

        // Recoger los 5 beepers si están presentes
        for (int i = 0; i < 5; i++) {
            if (nextToABeeper()) {
                pickBeeper();
            }
        }

        // Girar a la izquierda y salir de los muros
        turnLeft();
        if (frontIsClear()) {
            move();
        }
        if (frontIsClear()) {
            move();
        }

        // Poner los beepers fuera de los muros si tiene en la bolsa
        for (int i = 0; i < 5; i++) {
            if (anyBeepersInBeeperBag()) {
                putBeeper();
            }
        }

        // Moverse a otra posición y apagar el robot
        if (frontIsClear()) {
            move();
        }
        turnOff();
    }

    // Método que activa el hilo y ejecuta el recorrido
    public void run() {
        race();
    }
}

public class MiPrimerRobot implements Directions {
    public static void main(String[] args) {
        // Configuramos el mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);

        // Crear el primer robot en la posición inicial
        Racer first = new Racer(1, 1, East, 0, Color.GREEN);

        // Crear el segundo robot en una posición diferente con color azul
        Racer second = new Racer(1, 1, East, 0, Color.BLUE);

        // Crear hilos para los robots
        Thread thread1 = new Thread(first);
        Thread thread2 = new Thread(second);

        // Iniciar los hilos para que ambos robots comiencen en paralelo
        thread1.start();
        thread2.start();
    }
}
