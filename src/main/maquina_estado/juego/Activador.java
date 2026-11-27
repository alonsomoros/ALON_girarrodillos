package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.motor.Tiempo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * La barra de energía de un héroe (de 5 a 0: al llegar a 0 actúa). Cuando cambia, la barra pasa por cada altura intermedia
 * (en vez de saltar), y mientras está a 0 ("lista") brilla y parpadea hasta que el héroe actúa.
 */
public class Activador {
    private static final int TICKS_POR_PASO = 5;
    private static final int PERIODO_BRILLO = 36;

    private int altura;
    private final int alturaDefault; // Fuente única de la altura inicial de la figura

    private int alturaAnterior;      // Altura que se veía antes del último cambio
    private long tickCambio = -1000;

    public Activador(final int alturaDefault) {
        this.alturaDefault = alturaDefault;
        this.altura = alturaDefault;
        this.alturaAnterior = alturaDefault;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        int antes = this.altura;
        int visible = alturaMostrada();
        if (altura > 5) {
            altura = 5;
        } else if (altura < 0) {
            altura = 0;
        }
        this.altura = altura;
        registrarCambio(antes, visible);
    }

    public void disminuirAltura(int disminuionAltura) {
        int antes = altura;
        int visible = alturaMostrada();
        altura -= disminuionAltura;
        if (altura < 0) {
            altura = 0;
        }
        registrarCambio(antes, visible);
    }

    public void aumentarAltura(int aumentoAltura) {
        int antes = altura;
        int visible = alturaMostrada();
        altura += aumentoAltura;
        if (altura > alturaDefault) {
            altura = alturaDefault;
        }
        registrarCambio(antes, visible);
    }

    public int getAlturaDefault() {
        return alturaDefault;
    }

    public void resetear() { // Vuelve a la altura default de la figura
        int antes = altura;
        int visible = alturaMostrada();
        this.altura = alturaDefault;
        registrarCambio(antes, visible);
    }

    // La nueva animación parte de lo que se estaba viendo (por si el cambio anterior no había terminado)
    private void registrarCambio(final int antes, final int visible) {
        if (antes != altura) {
            alturaAnterior = visible;
            tickCambio = Tiempo.ahora();
        }
    }

    /** Altura que se está dibujando ahora: va de la anterior a la actual de una en una, cada 5 actualizaciones. */
    public int alturaMostrada() {
        int distancia = altura - alturaAnterior;
        if (distancia == 0) {
            return altura;
        }
        long pasos = Math.max(0, Tiempo.ahora() - tickCambio) / TICKS_POR_PASO;
        if (pasos >= Math.abs(distancia)) {
            return altura;
        }
        return alturaAnterior + (distancia > 0 ? 1 : -1) * (int) pasos;
    }

    public void dibujarActivador(Graphics graficos, Point coordenadas, boolean esIzquierda) {
        String[] rutas = esIzquierda ? Constantes.RUTAS.ACTIVADORES.CUADRADO : Constantes.RUTAS.ACTIVADORES.ROMBO;
        int mostrada = alturaMostrada();

        if (altura == 0 && mostrada == 0) { // Lista para actuar: brillo que late
            double onda = (Math.sin(2 * Math.PI * (Tiempo.ahora() % PERIODO_BRILLO) / PERIODO_BRILLO) + 1) / 2;
            Graphics2D g = (Graphics2D) graficos;
            java.awt.Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.15 + 0.35 * onda)));
            g.setColor(new Color(0xFFE9A0));
            int radio = (int) (34 + 8 * onda);
            g.fillOval(coordenadas.x + 37 - radio, coordenadas.y + 70 - radio, radio * 2, radio * 2);
            g.setComposite(original);
        }

        if (mostrada >= 0 && mostrada < rutas.length) {
            Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[mostrada]), coordenadas);
        }
    }
}
