import kareltherobot.*;
import java.awt.Color;

public class MiPrimerRobot implements Directions
{
    public static void main(String [] args)
    {
        // Usamos el archivo que creamos del mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);

        // Coloca el primer robot en la posición inicial del mundo (1,1),
        // mirando al Este, sin ninguna sirena.
        Robot Karel = new Robot(1, 1, East, 0);

        // Coloca el segundo robot en la misma posición que el primer robot
        // y le asigna el color azul.
        Robot Azul = new Robot(1, 1, East, 0, Color.blue);

        // Mover los robots alternadamente 4 pasos
        Karel.move();
        Azul.move();
        Karel.move();
        Azul.move();
        Karel.move();
        Azul.move();
        Karel.move();
        Azul.move();

        // Solo el primer robot recoge los 5 beepers
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();
        Karel.pickBeeper();

        // Girar a la izquierda y salir de los muros alternadamente
        Karel.turnLeft();
        Azul.turnLeft();
        Karel.move();
        Azul.move();
        Karel.move();
        Azul.move();

        // Solo el primer robot coloca los beepers fuera de los muros
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();
        Karel.putBeeper();

        // Moverse a otra posición y apagar ambos robots
        Karel.move();
        Azul.move();
        Karel.turnOff();
        Azul.turnOff();
    }
}
