package main.graficos;

import main.Constantes;
import main.control.GestorControles;
import main.control.Raton;
import main.motor.Sacudida;

import main.maquina_estado.GestorEstados;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.Serial;

public class SuperficieDeDibujo extends Canvas {

    @Serial
    private static final long serialVersionUID = -2543002649071963867L;
    private static SuperficieDeDibujo instance;

    private final int ancho;
    private final int alto;
    private final Raton raton;

    public SuperficieDeDibujo() {
        this.alto = Constantes.ALTO_JUEGO;
        this.ancho = Constantes.ANCHO_JUEGO;

        this.raton = new Raton(this);

        setIgnoreRepaint(true); // Ignorar repintado
        setCursor(raton.obtenerCursor()); // Cursor
        setPreferredSize(new Dimension(ancho, alto)); // Dimensiones
        addKeyListener(GestorControles.teclado); // Añadir teclado
        setFocusable(true); // Foco
        requestFocus(); // Pedir foco
        instance = this;
    }

    public static synchronized SuperficieDeDibujo getInstance() {
        if (instance == null) {
            instance = new SuperficieDeDibujo();
        }
        return instance;
    }

    public void dibujar() {
        BufferStrategy bufferStrategy = getBufferStrategy(); // Buffer

        if (bufferStrategy == null) {
            createBufferStrategy(2); // Crear buffer
            bufferStrategy = getBufferStrategy();
        }

        Graphics2D graficos = null; // Declare graficos here

        if (bufferStrategy != null) {
            graficos = (Graphics2D) bufferStrategy.getDrawGraphics();
        }

        if (graficos != null) {
            graficos.setFont(Constantes.FUENTES.FUENTE_TEXTO); // Fuente

            if (Constantes.FACTOR_ESCALA_X != 1 || Constantes.FACTOR_ESCALA_Y != 1) {
                graficos.scale(Constantes.FACTOR_ESCALA_X, Constantes.FACTOR_ESCALA_Y); // Escalar
            }

            if (Sacudida.activa()) { // El borde que deja al descubierto el desplazamiento se pinta de negro
                graficos.setColor(Color.BLACK);
                graficos.fillRect(0, 0, ancho, alto);
                graficos.translate(Sacudida.desplazamientoX(), Sacudida.desplazamientoY());
            }

            GestorEstados.getInstance().dibujar(graficos); // Dibujar estado

            if (Sacudida.activa()) {
                graficos.translate(-Sacudida.desplazamientoX(), -Sacudida.desplazamientoY());
            }
            Raton.getInstance().dibujar(graficos); // Dibujar raton

            Toolkit.getDefaultToolkit().sync(); // Sincronizar graficos

            graficos.dispose(); // Liberar graficos
        }

        if (bufferStrategy != null) {
            bufferStrategy.show(); // Mostrar graficos
        }
    }

    // Crea la estrategia de buffer al abrir la ventana
    public void inicializarBuffer() {
        if (getBufferStrategy() == null) {
            createBufferStrategy(4);
        }
    }
}
