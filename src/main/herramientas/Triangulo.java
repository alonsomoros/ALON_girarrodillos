package main.herramientas;

import java.awt.Point;

public enum Triangulo {
    TRIANGULO1(new Point[]{new Point(210, 445), new Point(195, 455), new Point(210, 465)}),
    TRIANGULO2(new Point[]{new Point(390, 445), new Point(405, 455), new Point(390, 465)}),
    TRIANGULO3(new Point[]{new Point(855, 445), new Point(840, 455), new Point(855, 465)}),
    TRIANGULO4(new Point[]{new Point(1035, 445), new Point(1050, 455), new Point(1035, 465)});

    private final Point[] puntos;

    Triangulo(Point[] puntos) {
        this.puntos = puntos;
    }

    public Point[] getPuntos() {
        return puntos;
    }
}

