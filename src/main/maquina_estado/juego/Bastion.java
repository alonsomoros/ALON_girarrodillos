package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;

public class Bastion {
    private int altura;

    private final Point centroTexto; // Dónde salen los números flotantes (null: no salen)

    public Bastion() {
        this(null);
    }

    public Bastion(final Point centroTexto) {
        altura = 0;
        this.centroTexto = centroTexto;
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
        int antes = this.altura;
        this.altura += altura;

        if (this.altura > 5) {
            this.altura = 5;
        }
        mostrarCambio(this.altura - antes);
    }

    public int getAltura() {
        return altura;
    }

    public void disminuirAltura(int altura) {
        int antes = this.altura;
        this.altura -= altura;

        if (this.altura < 0) {
            this.altura = 0;
        }
        mostrarCambio(this.altura - antes);
    }

    // Número flotante con lo que cambió de verdad (gris al bajar, claro al subir)
    private void mostrarCambio(final int cambio) {
        if (cambio != 0 && centroTexto != null) {
            main.motor.TextoFlotante.mostrar((cambio > 0 ? "+" : "") + cambio, centroTexto.x, centroTexto.y,
                    cambio > 0 ? new Color(0xE6E0D2) : new Color(0xB5ADA0));
        }
    }
}
