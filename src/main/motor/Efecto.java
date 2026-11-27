package main.motor;

import java.awt.Graphics;

/** Algo que se actualiza y se dibuja cada frame durante un tiempo (un impacto, un número flotante, una chispa...). */
public interface Efecto {

    void actualizar();

    void dibujar(Graphics graficos);

    /** true cuando ya no hay que actualizarlo ni dibujarlo: el gestor lo retira. */
    boolean terminado();
}
