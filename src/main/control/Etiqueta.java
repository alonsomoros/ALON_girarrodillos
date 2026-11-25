package main.control;

import main.Constantes;
import main.herramientas.MedidorString;

import java.awt.*;

public class Etiqueta {

    private static int siguienteId = 0;
    private final int id;
    private String nombre;
    private final int anchoEtiqueta;
    private final int altoEtiqueta;
    private final Font fuente;

    private final Point posicion;

    private final Rectangle hitbox;

    public Etiqueta(final String nombreEtiqueta, int x, int y, final Font fuente, final int tamanioFuente) {
        this.id = siguienteId++; // WTF

        this.nombre = nombreEtiqueta;
        this.fuente = fuente;

        this.anchoEtiqueta = MedidorString.medirAnchoPixeles(nombreEtiqueta, fuente, tamanioFuente); // Esta devolviendo el String org
        this.altoEtiqueta = MedidorString.medirAltoPixeles(nombreEtiqueta, fuente, tamanioFuente);

        this.posicion = new Point(x - anchoEtiqueta / 2, y);

        hitbox = new Rectangle(x - anchoEtiqueta / 2, (int) (y - altoEtiqueta / 1.3), anchoEtiqueta, altoEtiqueta);
    }

    public int getAnchoEtiqueta() {
        return anchoEtiqueta;
    }

    public int getAltoEtiqueta() {
        return altoEtiqueta;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Point getPosicion() {
        return posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public Font getFuente() {
        return fuente;
    }

    public static void resetId() {
        siguienteId = 0;
    }
}
