import main.Constantes;
import main.maquina_estado.juego.PruebasJuego;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

import java.util.HashMap;
import java.util.Map;

/**
 * Pruebas mínimas de lógica, sin librerías: cada prueba es un método registrado en main().
 * Se ejecutan con scripts\probar.bat. Una prueba falla si lanza cualquier excepción o AssertionError.
 */
public class PruebasLogica {

    public static void main(final String[] args) {
        System.out.println("Pruebas de logica");
        main.AudioPlayer.efectosActivos = false; // sin sonidos durante las pruebas
        main.maquina_estado.tienda.Progreso.usarSoloEnMemoria(); // sin tocar tu guardado real


        prueba("las probabilidades de los rodillos de juego suman 1", () -> {
            Map<String, HashMap<Constantes.RODILLOS.Elementos, Double>> rodillos = new HashMap<>();
            rodillos.put("R1", Constantes.RODILLOS.R1_Prob);
            rodillos.put("R2", Constantes.RODILLOS.R2_Prob);
            rodillos.put("R3", Constantes.RODILLOS.R3_Prob);
            rodillos.put("R4", Constantes.RODILLOS.R4_Prob);

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
                for (Constantes.RODILLOS.Elementos s : rodillo.getSalidaRodillo()) {
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

        prueba("cargarEscalada devuelve el tamaño pedido, lo cachea y no rompe con rutas inexistentes", () -> {
            java.awt.image.BufferedImage a = main.herramientas.CargadorRecursos.cargarEscalada("rec/wallpapers/Musica.png", 640, 320);
            verificar(a.getWidth() == 640 && a.getHeight() == 320, "tamaño " + a.getWidth() + "x" + a.getHeight());
            verificar(a == main.herramientas.CargadorRecursos.cargarEscalada("rec/wallpapers/Musica.png", 640, 320), "no se cachea");
            java.awt.image.BufferedImage grande = main.herramientas.CargadorRecursos.cargarEscalada("rec/wallpapers/1.png", 1280, 640);
            verificar(grande.getWidth() == 1280 && grande.getHeight() == 640, "fondo grande " + grande.getWidth());
            java.awt.image.BufferedImage falta = main.herramientas.CargadorRecursos.cargarEscalada("rec/no_existe.png", 100, 50);
            verificar(falta.getWidth() == 100 && falta.getHeight() == 50, "placeholder escalado");
        });

        PruebasJuego.ejecutar();

        main.maquina_estado.tienda.PruebasTienda.ejecutar();
        main.motor.PruebasMotor.ejecutar();



        pruebas.Ejecutor.resumenYSalir();
    }
}
