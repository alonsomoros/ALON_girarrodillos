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

    public void actualizar() {

    }

    public void dibujarBastion(Graphics graficos, EsqueletoPlayer duenio, Point coordenadas) {

        if (duenio instanceof Player) {
            switch (this.altura) {
                case 1:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_PLAYER_1), coordenadas);
                    break;
                case 2:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_PLAYER_2), coordenadas);
                    break;
                case 3:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_PLAYER_3), coordenadas);
                    break;
                case 4:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_PLAYER_4), coordenadas);
                    break;
                case 5:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_PLAYER_5), coordenadas);
                    break;
            }
        } else
            switch (this.altura) {
                case 1:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_IA_1), coordenadas);
                    break;
                case 2:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_IA_2), coordenadas);
                    break;
                case 3:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_IA_3), coordenadas);
                    break;
                case 4:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_IA_4), coordenadas);
                    break;
                case 5:
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASTION.BASTION_IA_5), coordenadas);
                    break;
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
