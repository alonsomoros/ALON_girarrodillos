package pruebas;

/** Mini ejecutor de pruebas (sin librerías). Una prueba falla si lanza cualquier excepción o AssertionError. */
public class Ejecutor {

    private static int ejecutadas = 0;
    private static int fallidas = 0;

    public interface Prueba {
        void ejecutar() throws Exception;
    }

    public static void prueba(final String nombre, final Prueba prueba) {
        ejecutadas++;
        try {
            prueba.ejecutar();
            System.out.println("  OK     " + nombre);
        } catch (Throwable t) {
            fallidas++;
            System.out.println("  FALLA  " + nombre + " -> " + t);
        }
    }

    public static void verificar(final boolean condicion, final String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void resumenYSalir() {
        System.out.println(ejecutadas + " pruebas, " + fallidas + " fallidas");
        System.exit(fallidas == 0 ? 0 : 1);
    }
}
