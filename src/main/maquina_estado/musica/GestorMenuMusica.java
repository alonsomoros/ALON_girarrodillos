package main.maquina_estado.musica;

import main.AudioPlayer;
import main.Constantes;
import main.control.Etiqueta;
import main.control.GestorEtiquetas;
import main.control.Raton;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.herramientas.MedidorString;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;


import java.awt.*;
import java.io.File;
import java.util.ArrayList;


public class GestorMenuMusica implements IEstadoJuego, IAudio {
    Color colorPrimario = new Color(0x89d4d9);
    Color colorSecundario = new Color(0xbddfe0);
    Color colorTerciario = new Color(0xe1eae7);
    Color colorOscuro = new Color(0x0B8567);

    private static final ArrayList<Etiqueta> secciones;

    static {
        secciones = GestorEtiquetas.getInstance().getEtiquetasMenuMusica();
    }

    private Etiqueta etiquetaActual;

    public GestorMenuMusica() {


    }

    @Override
    public void actualizar() {
        Etiqueta seccionNueva = null;

        for (Etiqueta etiqueta : secciones) {
            if (etiqueta.getHitbox().intersects(Raton.getInstance().obtenerRectanguloPosicion())) {
                seccionNueva = etiqueta;
                if (Raton.getInstance().obtenerClickIzquierdo()) {
                    Raton.getInstance().reiniciarClickIzquierdo(); // Un clic = una sola acción
                    AudioPlayer.getInstance().playSound("rec/musica/" + etiqueta.getNombre() + ".wav");

                }
            }
        }

        etiquetaActual = seccionNueva;
    }

    @Override
    public void dibujar(Graphics graficos) {
        dibujarFondo(graficos);
        dibujarPanelPrincipal(graficos);
        dibujarTituloCanciones(graficos);
    }

    private void dibujarPanelPrincipal(Graphics graficos) {
        Color colorPanel = new Color(0x71E1EAE7, true);
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, 120, 55, (int) (Constantes.ANCHO_JUEGO / 1.2), (int) (Constantes.ALTO_JUEGO / 1.2), colorPanel);
        Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, 120, 55, (int) (Constantes.ANCHO_JUEGO / 1.2), (int) (Constantes.ALTO_JUEGO / 1.2), 2, colorPrimario);
        Dibujador.TEXTO.dibujar(graficos, "<< MUSICA >>", Constantes.ANCHO_JUEGO / 2, 200, 60, colorOscuro);
    }

    public void dibujarTituloCanciones(Graphics graficos) {
        Etiqueta seccionNueva = null;

        for (Etiqueta etiqueta : secciones) {
            if (etiqueta.getHitbox().intersects(Raton.getInstance().obtenerRectanguloPosicion())) {
                Dibujador.ETIQUETA.dibujarEtiquetas(graficos, etiqueta, 20, colorOscuro);
                seccionNueva = etiqueta;
            } else {
                Dibujador.ETIQUETA.dibujarEtiquetas(graficos, etiqueta, 20, colorTerciario);
            }
        }
        etiquetaActual = seccionNueva;
    }

    public void dibujarFondo(Graphics graficos) {
        Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.MUSICA), 0, 0);
        dibujarNubes(graficos);
    }

    private void dibujarNubes(Graphics graficos) {

    }

    public void activarAudio() {
//        AudioPlayer.getInstance().stopSound();
//        AudioPlayer.getInstance().playSound(Constantes.RUTAS.MUSICA.MENU_MUSICA);
    }
}