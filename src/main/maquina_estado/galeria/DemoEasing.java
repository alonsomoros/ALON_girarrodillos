package main.maquina_estado.galeria;

import main.herramientas.Dibujador;
import main.motor.EfectoTemporal;
import main.motor.Interpolador;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/** Demostración: una "carrera" de cuadrados, cada uno con una curva de easing distinta, para ver cómo se mueve cada una. */
class DemoEasing extends EfectoTemporal {

    private static final String[] NOMBRES = {"LINEAL", "ENTRADA", "SALIDA", "ENTRADA_SALIDA", "RETROCESO", "REBOTE", "ELASTICO"};
    private static final Interpolador.Easing[] CURVAS = {Interpolador.LINEAL, Interpolador.ENTRADA, Interpolador.SALIDA,
            Interpolador.ENTRADA_SALIDA, Interpolador.RETROCESO, Interpolador.REBOTE, Interpolador.ELASTICO};

    private final Rectangle area;

    DemoEasing(final Rectangle area) {
        super(2200);
        this.area = area;
    }

    @Override
    public void dibujar(final Graphics graficos) {
        // Los primeros 300 ms quieto, luego recorre la pista en 1,5 s
        double t = Interpolador.limitar((progreso() * 2200 - 300) / 1500.0);
        int pista = area.width - 220;
        for (int i = 0; i < CURVAS.length; i++) {
            int y = area.y + 40 + i * 62;
            Dibujador.TEXTO.dibujar(graficos, NOMBRES[i], new Point(area.x + 16, y + 28), 14, Color.WHITE);
            Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, new Rectangle(area.x + 170, y + 14, pista, 2), new Color(0x3A4056));
            int x = area.x + 170 + Interpolador.interpolar(0, pista - 24, t, CURVAS[i]);
            Dibujador.RECTANGULO_RELLENO.dibujar(graficos, new Rectangle(x, y, 24, 28), new Color(0x89D4D9));
        }
    }
}
