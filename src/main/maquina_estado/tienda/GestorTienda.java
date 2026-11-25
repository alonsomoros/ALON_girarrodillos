package main.maquina_estado.tienda;

import main.AudioPlayer;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;

import java.awt.*;

public class GestorTienda implements IEstadoJuego, IAudio {
    public GestorTienda() {
    }

    @Override
    public void actualizar() {

    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleOpaca(Constantes.RUTAS.FONDOS.TIENDA),0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);

    }

    public void activarAudio() {
        AudioPlayer.getInstance().stopSound();
        AudioPlayer.getInstance().playSound(Constantes.RUTAS.MUSICA.TIENDA);
    }
}