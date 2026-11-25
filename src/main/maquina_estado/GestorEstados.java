package main.maquina_estado;

import main.AudioPlayer;
import main.GestorPrincipal;
import main.control.GestorControles;
import main.maquina_estado.creditos.GestorCreditos;
import main.maquina_estado.juego.GestorGirarrodillos;
import main.maquina_estado.musica.GestorMenuMusica;
import main.maquina_estado.menu_inicial.GestorMenuPrincipal;
import main.maquina_estado.tienda.GestorTienda;

import java.awt.*;
import java.util.ArrayList;

public class GestorEstados {

    private final ArrayList<IEstadoJuego> estadosDelJuego;
    private IEstadoJuego estadoActual;
    private static GestorEstados instance;
    private boolean iniciado;

    // Constructor
    public GestorEstados() {
        AudioPlayer.getInstance().stopSound();
        estadosDelJuego = new ArrayList<>();
        estadosDelJuego.add(new GestorGirarrodillos());
        estadosDelJuego.add(new GestorTienda());
        estadosDelJuego.add(new GestorMenuMusica());
        estadosDelJuego.add(new GestorCreditos());
        estadosDelJuego.add(new GestorMenuPrincipal());

        iniciado = false;
        cambiarEstadoActual(4);
    }

    public static synchronized GestorEstados getInstance() {
        if (instance == null) {
            instance = new GestorEstados();
        }
        return instance;
    }

    public void actualizar() {
        long tiempoActual = System.nanoTime();

        if (!estadoActual.getClass().equals(GestorMenuPrincipal.class)) {
            GestorControles.teclado.procesarTecla(GestorControles.teclado.X, () -> {
                cambiarEstadoActual(4);
            }, tiempoActual);
        }
        estadoActual.actualizar();
    }

    public void dibujar(final Graphics graficos) {
        estadoActual.dibujar(graficos);
    }

    public void cambiarEstadoActual(final int nuevoEstado) {
        System.out.println("Cambiando estado a: " + estadosDelJuego.get(nuevoEstado).getClass().getSimpleName());

        if (nuevoEstado == 4) {
            if (iniciado) {
                estadoActual = estadosDelJuego.get(nuevoEstado);
            } else {
                estadoActual = estadosDelJuego.get(nuevoEstado);
                IAudio audio = (IAudio) estadoActual;
                audio.activarAudio();
            }
        } else {
            iniciado = true;
            estadoActual = estadosDelJuego.get(nuevoEstado);
            IAudio audio = (IAudio) estadoActual;
            audio.activarAudio();
        }
//        AudioPlayer.getInstance().stopSound();

    }
}
