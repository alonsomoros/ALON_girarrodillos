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

    // Sprites propios de la IA (bastion_ia_1.png a bastion_ia_5.png, mismo tamaño que los del jugador): si no existen,
    // usa los del jugador volteados en vertical. Se comprueba una sola vez por altura.
    private static final Boolean[] TIENE_PROPIO_IA = new Boolean[6];

    private static boolean existeSpriteIA(final int altura) {
        if (TIENE_PROPIO_IA[altura] == null) {
            TIENE_PROPIO_IA[altura] = new java.io.File(RUTA_IA + altura + ".png").exists();
        }
        return TIENE_PROPIO_IA[altura];
    }

    private static final String RUTA_IA = "rec/sprites/bastion/bastion_ia_";

    public void dibujarBastion(Graphics graficos, EsqueletoPlayer duenio, Point coordenadas) {
        String[] rutas = Constantes.RUTAS.BASTION.PLAYER;
        if (altura >= 1 && altura < rutas.length) {
            if (duenio instanceof Player) {
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[altura]), coordenadas);
            } else if (existeSpriteIA(altura)) {
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA_IA + altura + ".png"), coordenadas);
            } else { // Sin sprite propio: el del jugador boca abajo
                Dibujador.IMAGEN.dibujarVolteadoVertical(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[altura]), coordenadas);
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
