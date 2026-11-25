package main;

import main.herramientas.Depurar;
import main.control.Raton;
import main.graficos.SuperficieDeDibujo;
import main.graficos.Ventana;
import main.herramientas.CacheImagenes;
import main.herramientas.Dibujador;
import main.maquina_estado.GestorEstados;

import java.awt.Transparency;
import java.io.IOException;

public class GestorPrincipal implements Runnable {

    // Atributos
    //
    private Thread gameThread;

    private final Ventana ventana;

    private GestorEstados gestorEstados;
    private AudioPlayer audioPlayer;

    private volatile boolean enFuncionamiento = false;

    // Constructor
    // Main
    public static void main(String[] args) throws IOException {
        GestorPrincipal gestorPrincipal = new GestorPrincipal();
//        CargadorRecursos.unzip("rec/musica/OST.zip", "rec/musica");
        gestorPrincipal.iniciarBuclePrincipal();
    }

    // Carga por adelantado las imágenes que se usan en partida para que no haya tirones al jugar
    private static void precargarRecursos() {
        final String carpetaRodillo = "rec/sprites/rodillo/";
        for (Constantes.RODILLOS.Elementos elemento : Constantes.RODILLOS.Elementos.values()) {
            CacheImagenes.precargar(Transparency.OPAQUE,
                    carpetaRodillo + elemento + ".png",
                    carpetaRodillo + elemento + "Lateral.png");
        }
        CacheImagenes.precargar(Transparency.TRANSLUCENT,
                Constantes.RUTAS.ELEMENTOS_RODILLO.FIJADOR,
                Constantes.RUTAS.FONDOS.TABLERO);
        CacheImagenes.precargarCarpeta(Transparency.TRANSLUCENT, "rec/sprites/activadores");
        CacheImagenes.precargarCarpeta(Transparency.TRANSLUCENT, "rec/sprites/baseFigura");
        CacheImagenes.precargarCarpeta(Transparency.TRANSLUCENT, "rec/sprites/bastion");
    }

    private GestorPrincipal() {

        precargarRecursos();
        ventana = new Ventana();
//        ventana.requestFocus();

        inicializarClases();
    }

    private void inicializarClases() {
        audioPlayer = AudioPlayer.getInstance();
        gestorEstados = GestorEstados.getInstance();
    }

    public void iniciarBuclePrincipal() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        int actualizacionesAcumuladas = 0;                                          // Actualizaciones por segundo
        int framesAcumulados = 0;                                                   // Fotogramas por segundo

        final int NS_POR_SEGUNDO = 1000000000                                       // Nanosegundos en un segundo
                , APS_OBJETIVO = 60;                                                // Actualizaciones por segundo objetivo
        final double NS_POR_ACTUALIZACION = (double) NS_POR_SEGUNDO / APS_OBJETIVO; // Cada cuanto se actualiza

        long referenciaActualizacion = System.nanoTime()                            // Tiempo actual
                , referenciaContador = System.nanoTime();                           // Tiempo actual

        double tiempoTranscurrido, delta = 0;

        enFuncionamiento = true;

        while (enFuncionamiento) {
            final long inicioBucle = System.nanoTime();                             // Tiempo actual del bucle

            tiempoTranscurrido = inicioBucle - referenciaActualizacion; // Tiempo transcurrido desde referenciaActualizacion hasta inicioBucle
            referenciaActualizacion = inicioBucle; // Actualizar el tiempo

            delta += tiempoTranscurrido / NS_POR_ACTUALIZACION; // Sumar el tiempo transcurrido al delta

            while (delta >= 1) { // Actualizar 60 veces por segundo
                actualizar();
                actualizacionesAcumuladas++;

                delta--;
            }

            dibujar();
            framesAcumulados++;

            // Dormir lo que sobre del frame para no ocupar un núcleo entero dibujando sin parar
            // (en Windows parkNanos es muy impreciso, por eso se duerme de 1 ms en 1 ms)
            while (System.nanoTime() - inicioBucle < NS_POR_ACTUALIZACION) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (System.nanoTime() - referenciaContador > NS_POR_SEGUNDO) { // Mostrar los FPS y APS cada segundo
                Depurar.log("FPS: " + framesAcumulados + " APS: " + actualizacionesAcumuladas);

                Constantes.APS = actualizacionesAcumuladas;
                Constantes.FPS = framesAcumulados;

                framesAcumulados = 0;
                actualizacionesAcumuladas = 0;
                Dibujador.numeroObjetosDibujados = 0;

                referenciaContador = System.nanoTime();
            }
        }
    }

    private void actualizar() {
        Raton.getInstance().actualizar(SuperficieDeDibujo.getInstance());
        gestorEstados.actualizar();
    }

    private void dibujar() {
        SuperficieDeDibujo.getInstance().dibujar();
    }

}