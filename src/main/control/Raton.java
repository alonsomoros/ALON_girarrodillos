package main.control;

import main.Constantes;
import main.graficos.SuperficieDeDibujo;
import main.herramientas.CargadorRecursos;
import main.herramientas.Depurar;

import main.herramientas.Dibujador;
import main.maquina_estado.GestorEstados;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Raton extends MouseAdapter {
    private final Cursor cursor;
    private final Point posicion;
    private boolean clickIzquierdo;
    private static Raton instance;

    public Raton(final SuperficieDeDibujo superficieDeDibujo) {
        Toolkit configuracion = Toolkit.getDefaultToolkit();

        BufferedImage imagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ICONO.RATON);

        Point punta = new Point(0, 0);

        this.cursor = configuracion.createCustomCursor(imagen, punta, "Cursor por defecto");

        posicion = new Point();
        superficieDeDibujo.addMouseListener(this);
        superficieDeDibujo.addMouseMotionListener(this);
    }

    public static synchronized Raton getInstance() {
        if (instance == null) {
            instance = new Raton(SuperficieDeDibujo.getInstance());
        }
        return instance;
    }

    //Si quiero dibujar mi propio ratón
    public void actualizar(SuperficieDeDibujo superficieDeDibujo) {
        actualizarPosicion(superficieDeDibujo);
    }

    public void dibujar(Graphics graficos) {
        if (!Depurar.ACTIVO) {
            return;
        }
        Rectangle rec = obtenerRectanguloPosicion();
        Dibujador.TEXTO.dibujar(graficos, "Raton x: " + rec.x + " y: " + rec.y, 110, 30, Constantes.azul_2);
        if (clickIzquierdo) {
            Dibujador.TEXTO.dibujar(graficos, "Click Izquierdo", 100, 100, Color.RED);
        }
    }

    public Cursor obtenerCursor() {
        return this.cursor;
    }

    private void actualizarPosicion(final SuperficieDeDibujo superficieDeDibujo) {
        final Point posicionInicial = MouseInfo.getPointerInfo().getLocation();

        SwingUtilities.convertPointFromScreen(posicionInicial, superficieDeDibujo);

        posicion.setLocation(posicionInicial.getX(), posicionInicial.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            clickIzquierdo = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            clickIzquierdo = false;
        }
    }

    public boolean esInterseccionConEtiqueta(Etiqueta etiqueta) {
        return this.obtenerRectanguloPosicion().intersects(etiqueta.getHitbox());
    }


    public Rectangle obtenerRectanguloPosicion() {
        return new Rectangle(posicion.x, posicion.y, 1, 1);
    }

    public boolean obtenerClickIzquierdo() {
        return clickIzquierdo;
    }

    public void reiniciarClickIzquierdo() {
        clickIzquierdo = false;
    }
}
