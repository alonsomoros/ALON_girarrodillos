import main.Constantes;

import java.util.HashMap;
import java.util.Map;

/**
 * Pruebas mínimas de lógica, sin librerías: cada prueba es un método registrado en main().
 * Se ejecutan con scripts\probar.bat. Una prueba falla si lanza cualquier excepción o AssertionError.
 */
public class PruebasLogica {

    private static int ejecutadas = 0;
    private static int fallidas = 0;

    interface Prueba {
        void ejecutar() throws Exception;
    }

    static void prueba(final String nombre, final Prueba prueba) {
        ejecutadas++;
        try {
            prueba.ejecutar();
            System.out.println("  OK     " + nombre);
        } catch (Throwable t) {
            fallidas++;
            System.out.println("  FALLA  " + nombre + " -> " + t);
        }
    }

    static void verificar(final boolean condicion, final String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void main(final String[] args) {
        System.out.println("Pruebas de logica");

        prueba("las probabilidades de los rodillos de juego suman 1", () -> {
            Map<String, HashMap<Constantes.RODILLOS.Elementos, Double>> rodillos = new HashMap<>();
            rodillos.put("R1", Constantes.RODILLOS.R1_Prob);
            rodillos.put("R2", Constantes.RODILLOS.R2_Prob);
            for (Map.Entry<String, HashMap<Constantes.RODILLOS.Elementos, Double>> e : rodillos.entrySet()) {
                double suma = 0;
                for (double p : e.getValue().values()) {
                    suma += p;
                }
                verificar(Math.abs(suma - 1.0) < 1e-9, e.getKey() + " suma " + suma);
            }
        });

        System.out.println(ejecutadas + " pruebas, " + fallidas + " fallidas");
        System.exit(fallidas == 0 ? 0 : 1);
    }
}
