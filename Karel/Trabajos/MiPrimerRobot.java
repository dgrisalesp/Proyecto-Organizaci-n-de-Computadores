import kareltherobot.*;
import java.awt.Color;

// Definición de la clase Racer que extiende Robot y permite ejecutar en un hilo
class Racer extends Robot implements Runnable {
    public Racer(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
        World.setupThread(this); // Configuración del hilo para el robot
    }

    // Método que define el recorrido del robot
    public void race()
        { while(! nextToABeeper())
        move();
        pickBeeper();
        turnOff();
        }

    // Método que activa el hilo y ejecuta el recorrido
    public void run() {
        race();
    }
}

public class MiPrimerRobot implements Directions { // Implementa Directions para acceder a las constantes de dirección
    public static void main(String[] args) {
        // Configuramos el mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);

        // Crear el primer robot en la posición inicial
        Racer first = new Racer(1, 1, East, 0, Color.GREEN);

        // Crear el segundo robot en la misma posición con color azul
        Racer second = new Racer(2, 1, East, 0, Color.BLUE);
        // Crear hilos para los robots
        Thread thread1 = new Thread(first);
        Thread thread2 = new Thread(second);

        // Iniciar los hilos para que ambos robots comiencen en paralelo
        thread1.start();
        thread2.start();
    }
}
