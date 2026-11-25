package main.graficos;

import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationPanelSprites extends JPanel {
    private boolean animando = false;
    private long tiempoInicial;
    private final int DURACION_ANIMACION;
    private final Image[] sprites;
    private int indexActualSprite = 0;

    public AnimationPanelSprites(final int duracion, final int cantidadSprites, final String ruta) {
        DURACION_ANIMACION = duracion;
        // Cargar los sprites
        sprites = new Image[cantidadSprites];
        for (int i = 1; i <= sprites.length; i++) {
            sprites[i - 1] = CargadorRecursos.cargarImagenCompatibleTranslucida(ruta + i + ".png");
        }

        // Timer para actualizar la animación
        new Timer(100, new ActionListener() { // Cambiar sprite cada 100ms
            public void actionPerformed(ActionEvent e) {
                if (animando) {
                    long transcurrido = System.currentTimeMillis() - tiempoInicial;
                    if (transcurrido >= DURACION_ANIMACION) {
                        animando = false;
                        ((Timer) e.getSource()).stop();
                    } else {
                        indexActualSprite = (indexActualSprite + 1) % sprites.length;
                        repaint();
                    }
                }
            }
        }).start();
    }

    public void iniciarAtaque() {
        animando = true;
        tiempoInicial = System.currentTimeMillis();
        indexActualSprite = 0; // Reiniciar el índice del sprite
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (animando && sprites[indexActualSprite] != null) {
            Dibujador.IMAGEN.dibujar(g, sprites[indexActualSprite], 0, 0);
        }
    }
}
