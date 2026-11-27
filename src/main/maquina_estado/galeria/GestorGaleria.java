package main.maquina_estado.galeria;

import main.Constantes;
import main.control.GestorControles;
import main.herramientas.Dibujador;
import main.maquina_estado.GestorEstados;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;
import main.motor.Fundido;
import main.motor.GestorEfectos;
import main.motor.Sacudida;
import main.motor.Secuencia;
import main.motor.Sonidos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Galería de efectos: solo existe con <code>-Dgirar.debug=true</code> (tecla F2). Lista cada efecto, animación y sonido del
 * motor para lanzarlo aislado (W/S elegir, Espacio lanzar, Enter volver) y ver cómo queda sin jugar una partida.
 * Cada efecto nuevo que se cree se añade aquí con {@link #registrar(String, Runnable)}.
 */
public class GestorGaleria implements IEstadoJuego, IAudio {

    private static class Entrada {
        final String nombre;
        final Runnable lanzar;

        Entrada(final String nombre, final Runnable lanzar) {
            this.nombre = nombre;
            this.lanzar = lanzar;
        }
    }

    private static final Rectangle AREA_DEMO = new Rectangle(420, 60, 820, 520);

    private final List<Entrada> entradas = new ArrayList<>();
    private final GestorEfectos efectosLocales = new GestorEfectos();
    private int seleccion = 0;
    private String ultimo = "";

    public GestorGaleria() {
        registrar("Sacudida suave", () -> Sacudida.iniciar(4, 300));
        registrar("Sacudida fuerte", () -> Sacudida.iniciar(14, 600));
        registrar("Fundido a negro y vuelta", () -> {
            GestorEfectos global = GestorEstados.getInstance().getEfectos();
            global.agregar(GestorEfectos.Capa.FRENTE, new Fundido(400, true));
            global.agregar(Secuencia.nueva().esperar(400).hacer(() ->
                    global.agregar(GestorEfectos.Capa.FRENTE, new Fundido(400, false))));
        });
        registrar("Curvas de easing (carrera)", () -> efectosLocales.agregar(new DemoEasing(AREA_DEMO)));
        registrar("Secuencia: esperar, mover, avisar", () -> efectosLocales.agregar(new DemoSecuencia(AREA_DEMO, efectosLocales)));
        registrar("Sonido: clic de botón", () -> Sonidos.getInstance().reproducir(Constantes.RUTAS.EFECTOS.CLICK_BOTON));
        registrar("Sonido: ataque", () -> Sonidos.getInstance().reproducir(Constantes.RUTAS.EFECTOS.ATAQUE));
        registrar("Sonido: victoria", () -> Sonidos.getInstance().reproducir(Constantes.RUTAS.EFECTOS.VICTORIA));
        registrar("Sonido: derrota", () -> Sonidos.getInstance().reproducir(Constantes.RUTAS.EFECTOS.DERROTA));
    }

    /** Lanza la entrada número i (la usa el Capturador para sacar fotos de un efecto concreto). */
    public void lanzarEntrada(final int i) {
        if (i >= 0 && i < entradas.size()) {
            seleccion = i;
            ultimo = entradas.get(i).nombre;
            entradas.get(i).lanzar.run();
        }
    }

    public int numeroEntradas() {
        return entradas.size();
    }

    /** Añade una entrada a la lista (para efectos que se creen en otras fases). */

    public final void registrar(final String nombre, final Runnable lanzar) {
        entradas.add(new Entrada(nombre, lanzar));
    }

    @Override
    public void actualizar() {
        long ahora = System.nanoTime();
        GestorControles.teclado.procesarTecla(GestorControles.teclado.abajo, () -> {
            seleccion = (seleccion + 1) % entradas.size();
        }, ahora);
        GestorControles.teclado.procesarTecla(GestorControles.teclado.arriba, () -> {
            seleccion = (seleccion + entradas.size() - 1) % entradas.size();
        }, ahora);
        GestorControles.teclado.procesarTecla(GestorControles.teclado.espacio, () -> {
            Entrada e = entradas.get(seleccion);
            ultimo = e.nombre;
            e.lanzar.run();
        }, ahora);

        efectosLocales.actualizar();
    }

    @Override
    public void dibujar(Graphics graficos) {
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, new Rectangle(0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO), new Color(0x14161F));

        // Lista de efectos
        Dibujador.TEXTO.dibujar(graficos, "GALERIA DE EFECTOS", new Point(24, 40), 22, new Color(0xE4DEB0));
        for (int i = 0; i < entradas.size(); i++) {
            boolean elegida = i == seleccion;
            Dibujador.TEXTO.dibujar(graficos, (elegida ? "> " : "  ") + entradas.get(i).nombre,
                    new Point(24, 90 + i * 30), 18, elegida ? new Color(0x89D4D9) : new Color(0xAAAAAA));
        }

        // Zona de demostración
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, AREA_DEMO, new Color(0x1E2230));
        Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, AREA_DEMO, 2, new Color(0x3A4056));
        efectosLocales.dibujar(graficos);

        Dibujador.TEXTO.dibujar(graficos, "W/S elegir   Espacio lanzar   Enter volver", new Point(24, Constantes.ALTO_JUEGO - 40), 14, Color.WHITE);
        if (!ultimo.isEmpty()) {
            Dibujador.TEXTO.dibujar(graficos, "Ultimo: " + ultimo, new Point(24, Constantes.ALTO_JUEGO - 18), 14, new Color(0xE4DEB0));
        }
    }

    @Override
    public void activarAudio() {
        // Mantiene la música que estuviera sonando
    }
}
