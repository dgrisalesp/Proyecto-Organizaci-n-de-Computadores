import kareltherobot.*;
import java.awt.Color;
public class MiPrimerRobot implements Directions
{
public static void main(String [] args)
{
Robot Karel = new Robot(1, 1, East, 0);
// Coloca el robot en la posición inicial del mundo (1,1),
// mirando al Este, sin ninguna sirena.
// Mover el robot 3 pasos, girar hacia el norte y apagar el robot.
Karel.move();
Karel.move();
Karel.move();
Karel.turnLeft();
Karel.turnOff();
}
}
