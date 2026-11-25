package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;

public class Bastion {
    private int altura;

    public Bastion() {
        altura = 0;
    }

    public void dibujarBastion(Graphics graficos, EsqueletoPlayer duenio, Point coordenadas) {
        String[] rutas = (duenio instanceof Player) ? Constantes.RUTAS.BASTION.PLAYER : Constantes.RUTAS.BASTION.IA;
        if (altura >= 1 && altura < rutas.length) {
            Image imagen = CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[altura]);
            if (duenio instanceof Player) {
                Dibujador.IMAGEN.dibujar(graficos, imagen, coordenadas);
            } else { // El bastión de la IA es el del jugador boca abajo
                Dibujador.IMAGEN.dibujarVolteadoVertical(graficos, imagen, coordenadas);
            }
        }
    }

    public void aumentarAltura(int altura) {
        this.altura += altura;

        if (this.altura > 5) {
            this.altura = 5;
        }
    }

    public int getAltura() {
        return altura;
    }

    public void disminuirAltura(int altura) {
        this.altura -= altura;

        if (this.altura < 0) {
            this.altura = 0;
        }

    }
}
