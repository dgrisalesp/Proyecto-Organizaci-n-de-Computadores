import kareltherobot.*;
import java.awt.Color;

public class MiPrimerRobot implements Directions {

    public static void main(String[] args) {
        // Configuración del mundo
        World.readWorld("EvacuationZone.kwld"); // Archivo del mundo generado o provisto
        World.setVisible(true);
        World.setDelay(15); // Configura la velocidad de ejecución

        // Obtener el número de robots de los argumentos
        int robotCount = getRobotCountFromArgs(args);

        // Crear e iniciar los robots de rescate
        for (int i = 0; i < robotCount; i++) {
            // Cada robot comienza en una posición diferente en la avenida 1
            RescueRobot robot = new Rescate(1 + i, 1, East, 0, Color.BLUE);
            new Thread(robot).start(); // Iniciar el robot en un hilo separado
        }
    }

    public static int getRobotCountFromArgs(String[] args) {
        // Leer el argumento -r seguido de la cantidad de robots
        int count = 1; // Valor predeterminado
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-r") && i + 1 < args.length) {
                try {
                    count = Integer.parseInt(args[i + 1]);
                    if (count < 1 || count > 15) {
                        System.out.println("Número de robots fuera de rango (1-15). Usando valor predeterminado (1).");
                        count = 1; // Asigna el valor predeterminado si está fuera del rango
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Formato incorrecto para la cantidad de robots. Usando valor predeterminado (1).");
                    count = 1;
                }
            }
        }
        return count;
    }
}
