package main.maquina_estado;

import main.herramientas.Depurar;
import main.AudioPlayer;
import main.control.GestorControles;
import main.motor.GestorEfectos;
import main.motor.Sacudida;


import main.maquina_estado.creditos.GestorCreditos;
import main.maquina_estado.juego.GestorGirarrodillos;
import main.maquina_estado.musica.GestorMenuMusica;
import main.maquina_estado.menu_inicial.GestorMenuPrincipal;
import main.maquina_estado.tienda.GestorTienda;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class GestorEstados {

    private final Map<Estado, IEstadoJuego> estadosDelJuego = new EnumMap<>(Estado.class);
    private Estado estadoActualId;
    private IEstadoJuego estadoActual;
    private static GestorEstados instance;
    private boolean iniciado;

    // Efectos de pantalla que no pertenecen a un estado concreto (fundidos, destellos, sacudidas...)
    private final GestorEfectos efectos = new GestorEfectos();

    // Constructor
    public GestorEstados() {
        AudioPlayer.getInstance().stopSound();
        estadosDelJuego.put(Estado.JUEGO, new GestorGirarrodillos());
        estadosDelJuego.put(Estado.TIENDA, new GestorTienda());
        estadosDelJuego.put(Estado.MUSICA, new GestorMenuMusica());
        estadosDelJuego.put(Estado.CREDITOS, new GestorCreditos());
        estadosDelJuego.put(Estado.MENU, new GestorMenuPrincipal());

        iniciado = false;
        cambiarEstadoActual(Estado.MENU);
    }

    public static synchronized GestorEstados getInstance() {
        if (instance == null) {
            instance = new GestorEstados();
        }
        return instance;
    }

    public void actualizar() {
        long tiempoActual = System.nanoTime();

        if (estadoActualId != Estado.MENU) {
            GestorControles.teclado.procesarTecla(GestorControles.teclado.X, () -> {
                cambiarEstadoActual(Estado.MENU);
            }, tiempoActual);
        }
        estadoActual.actualizar();
        efectos.actualizar();
        Sacudida.actualizar();
    }

    public GestorEfectos getEfectos() {
        return efectos;
    }

    public void dibujar(final Graphics graficos) {
        estadoActual.dibujar(graficos);
        efectos.dibujar(graficos);
    }

    public void cambiarEstadoActual(final Estado nuevoEstado) {
        Depurar.log("Cambiando estado a: " + nuevoEstado);

        estadoActualId = nuevoEstado;
        estadoActual = estadosDelJuego.get(nuevoEstado);

        if (nuevoEstado == Estado.JUEGO) { // Cada vez que se pulsa Jugar empieza una partida nueva
            ((GestorGirarrodillos) estadoActual).nuevaPartida();
        }


        // Al volver al menú no se reinicia la música (salvo en el arranque)
        if (nuevoEstado != Estado.MENU) {
            iniciado = true;
            ((IAudio) estadoActual).activarAudio();
        } else if (!iniciado) {
            ((IAudio) estadoActual).activarAudio();
        }
    }
}
