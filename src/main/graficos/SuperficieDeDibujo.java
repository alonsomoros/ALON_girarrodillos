package main.graficos;

import main.Constantes;
import main.control.GestorControles;
import main.control.Raton;
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

    public Graphics2D graficos2D;

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

            GestorEstados.getInstance().dibujar(graficos); // Dibujar estado
            Raton.getInstance().dibujar(graficos); // Dibujar raton

            Toolkit.getDefaultToolkit().sync(); // Sincronizar graficos

            graficos.dispose(); // Liberar graficos
        }

        if (bufferStrategy != null) {
            bufferStrategy.show(); // Mostrar graficos
        }
    }

    public void inicializarGraficos() {
        BufferStrategy buffer = getBufferStrategy(); // Buffer

        if (buffer == null) {
            createBufferStrategy(4); // Crear buffer
            buffer = getBufferStrategy(); // Intentar obtener la estrategia de buffer nuevamente
        }

        this.graficos2D = (Graphics2D) buffer.getDrawGraphics(); // Obtener graficos
    }
}
