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
            Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[altura]), coordenadas);
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
