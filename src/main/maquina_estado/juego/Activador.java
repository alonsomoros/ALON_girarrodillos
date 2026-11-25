package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;

public class Activador {
    private int altura;
    private final int alturaDefault; // Fuente única de la altura inicial de la figura

    public Activador(final int alturaDefault) {
        this.alturaDefault = alturaDefault;
        this.altura = alturaDefault;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        if (altura > 5) {
            altura = 5;
        } else if (altura < 0) {
            altura = 0;
        }
        this.altura = altura;
    }

    public void disminuirAltura(int disminuionAltura) {
        altura -= disminuionAltura;
        if (altura < 0) {
            altura = 0;
        }
    }

    public void aumentarAltura(int aumentoAltura) {
        altura += aumentoAltura;
        if (altura > alturaDefault) {
            altura = alturaDefault;
        }
    }

    public int getAlturaDefault() {
        return alturaDefault;
    }

    public void resetear() { // Vuelve a la altura default de la figura
        this.altura = alturaDefault;
    }


    public void dibujarActivador(Graphics graficos, Point coordenadas, boolean esIzquierda) {
        String[] rutas = esIzquierda ? Constantes.RUTAS.ACTIVADORES.CUADRADO : Constantes.RUTAS.ACTIVADORES.ROMBO;
        if (altura >= 0 && altura < rutas.length) {
            Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[altura]), coordenadas);
        }
    }
}
