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

        prueba("Rodillo: 100000 giros sin null, tambien con probabilidades que no suman 1", () -> {
            HashMap<Constantes.RODILLOS.Elementos, Double> incompleto = new HashMap<>();
            incompleto.put(Constantes.RODILLOS.Elementos.cuadrado1, 0.3);
            incompleto.put(Constantes.RODILLOS.Elementos.rombo1, 0.3); // suma 0.6
            @SuppressWarnings("unchecked")
            HashMap<Constantes.RODILLOS.Elementos, Double>[] canales = new HashMap[5];
            canales[0] = Constantes.RODILLOS.R1_Prob;
            canales[1] = Constantes.RODILLOS.R2_Prob;
            canales[2] = incompleto;
            canales[3] = new HashMap<>(); // vacío
            canales[4] = Constantes.RODILLOS.R1_Prob;
            main.maquina_estado.juego.Rodillo rodillo = new main.maquina_estado.juego.Rodillo(canales);
            for (int i = 0; i < 100000; i++) {
                rodillo.girarRodillo();
                for (String s : rodillo.getSalidaRodillo()) {
                    verificar(s != null, "símbolo null en el giro " + i);
                }
            }
        });

        prueba("cada símbolo del rodillo tiene sprite normal y lateral", () -> {
            for (Constantes.RODILLOS.Elementos e : Constantes.RODILLOS.Elementos.values()) {
                for (String sufijo : new String[]{".png", "Lateral.png"}) {
                    java.io.File f = new java.io.File("rec/sprites/rodillo/" + e + sufijo);
                    verificar(f.exists(), "falta " + f);
                }
            }
        });

        System.out.println(ejecutadas + " pruebas, "

 + fallidas + " fallidas");
        System.exit(fallidas == 0 ? 0 : 1);
    }
}
