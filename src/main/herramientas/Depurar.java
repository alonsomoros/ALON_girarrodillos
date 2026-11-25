package main.herramientas;

/**
 * Interruptor único para la información de depuración (consola y textos en pantalla).
 * Se activa con la propiedad de JVM -Dgirar.debug=true o poniendo ACTIVO a true.
 */
public class Depurar {

    public static boolean ACTIVO = Boolean.getBoolean("girar.debug");

    private Depurar() {
    }

    public static void log(final String mensaje) {
        if (ACTIVO) {
            System.out.println(mensaje);
        }
    }
}
