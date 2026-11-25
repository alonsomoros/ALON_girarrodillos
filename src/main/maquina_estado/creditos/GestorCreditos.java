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

        if (tiempoTranscurrido <= duracion / 6) {
            index = 0;
        } else if (tiempoTranscurrido > duracion / 6 && tiempoTranscurrido <= duracion / 6 * 2) {
            index = 1;
        } else if (tiempoTranscurrido > duracion / 6 * 2 && tiempoTranscurrido <= duracion / 6 * 3) {
            index = 2;
        } else if (tiempoTranscurrido > duracion / 6 * 3 && tiempoTranscurrido <= duracion / 6 * 4) {
            index = 3;
        } else if (tiempoTranscurrido > duracion / 6 * 4 && tiempoTranscurrido <= duracion / 6 * 5) {
            index = 4;
        } else if (tiempoTranscurrido > duracion / 6 * 5 && tiempoTranscurrido <= duracion) {
            index = 5;
        } else {
            AudioPlayer.getInstance().stopSound();
            GestorEstados.getInstance().cambiarEstadoActual(Estado.MENU);
        }
    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        switch (index) {
            case 0:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.CASCADA), 0,0);
                break;
            case 1:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.ACANTILADO), 0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);
                break;
            case 2:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.PANTANO), 0, 0);
                break;
            case 3:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.RAMA), 0, 0);
                break;
            case 4:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.RUINAS), 0, 0);
                break;
            case 5:
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.CREDITOS.ARBOL), 0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);
                break;
        }
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