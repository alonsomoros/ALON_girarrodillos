package main.maquina_estado.creditos;

import main.AudioPlayer;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.herramientas.MedidorString;
import main.maquina_estado.Estado;
import main.maquina_estado.GestorEstados;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;


import java.awt.*;


public class GestorCreditos implements IEstadoJuego, IAudio {
    private long startTime;
    private long duracion;
    // Fondos en el orden en que aparecen; algunos se ajustan al tamaño del juego
    private static final String[] FONDOS = {
            Constantes.RUTAS.FONDOS.CREDITOS.CASCADA, Constantes.RUTAS.FONDOS.CREDITOS.ACANTILADO,
            Constantes.RUTAS.FONDOS.CREDITOS.PANTANO, Constantes.RUTAS.FONDOS.CREDITOS.RAMA,
            Constantes.RUTAS.FONDOS.CREDITOS.RUINAS, Constantes.RUTAS.FONDOS.CREDITOS.ARBOL};
    private static final boolean[] ESCALAR_FONDO = {false, true, false, false, false, true};

    private int index = 0;
    private final String creditos;
    private final String[] creditosLineas;
    private long tiempoTranscurrido;

    public GestorCreditos() {
        this.duracion = 80000;
        this.creditos = CargadorRecursos.leerArchivoTexto(Constantes.RUTAS.CREDITOS);
        this.creditosLineas = creditos.split("\n");
        this.startTime = 0;
        this.tiempoTranscurrido = 0;
    }

    @Override
    public void actualizar() {

        tiempoTranscurrido = System.currentTimeMillis() - startTime;

        if (tiempoTranscurrido <= duracion) {
            // El tiempo se reparte a partes iguales entre los fondos
            index = (int) Math.min(FONDOS.length - 1, tiempoTranscurrido / (duracion / FONDOS.length));
        } else {
            AudioPlayer.getInstance().stopSound();
            GestorEstados.getInstance().cambiarEstadoActual(Estado.MENU);
        }
    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        // Escalados al tamaño del juego, o recortados (se veía solo la esquina superior izquierda a tamaño real)
        Image fondo = ESCALAR_FONDO[index]
                ? CargadorRecursos.cargarEscalada(FONDOS[index], Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO)
                : CargadorRecursos.cargarRecortada(FONDOS[index], Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);
        Dibujador.IMAGEN.dibujar(graficos, fondo, 0, 0);

        dibujarCreditos(graficos);
    }

    private void dibujarCreditos(Graphics g) {
        for (int i = 0; i < creditosLineas.length; i++) {
            Dibujador.TEXTO.dibujar(g, creditosLineas[i], new Point(Constantes.ANCHO_JUEGO / 2 - MedidorString.medirAnchoPixeles(g, creditosLineas[i], Constantes.FUENTES.FUENTE_TEXTO, 15) / 2, (int) (Constantes.ALTO_JUEGO + i * 30 - tiempoTranscurrido / 33)), 15, Color.WHITE);
        }
    }

    public void activarAudio() {
        AudioPlayer.getInstance().playSound(Constantes.RUTAS.MUSICA.CREDITOS);
        this.startTime = System.currentTimeMillis();
    }
}