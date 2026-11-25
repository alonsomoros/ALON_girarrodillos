package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;

public class Activador {
    public int altura;
    public int alturaDefault;

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

    public void resetear(int altura){ // La altura default de la figura
        this.altura = altura;
    }

    public void dibujarActivador(Graphics graficos, Point coordenadas, boolean esIzquierda) {
        switch (altura) {
            case 0:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_0), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_0), coordenadas);
                }
                break;
            case 1:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_1), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_1), coordenadas);
                }
                break;
            case 2:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_2), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_2), coordenadas);
                }
                break;
            case 3:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_3), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_3), coordenadas);
                }
                break;
            case 4:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_4), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_4), coordenadas);
                }
                break;
            case 5:
                if (esIzquierda) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_CUADRADO_5), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ACTIVADORES.ACTIVADOR_ROMBO_5), coordenadas);
                }
                break;
        }
    }
}
