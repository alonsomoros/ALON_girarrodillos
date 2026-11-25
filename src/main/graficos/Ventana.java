package main.graficos;

import main.Constantes;
import main.herramientas.CargadorRecursos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;

public class Ventana extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String titulo;
    private final BufferedImage icono;

    private final SuperficieDeDibujo superficieDeDibujo;

    public Ventana() {
        this.titulo = Constantes.TITULO;
        this.icono = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ICONO.LOGO);

        setTitle(titulo);
        setIconImage(icono);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout()); // Layout de la ventana

        this.superficieDeDibujo = new SuperficieDeDibujo();
        add(superficieDeDibujo, BorderLayout.CENTER); // La ventana rodeara el canvas cuadrándose

        setUndecorated(false);

        pack(); // Ajusta el tamaño de la ventana al tamaño del canvas
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setVisible(true); // Hace visible la ventana

        superficieDeDibujo.inicializarGraficos();
    }

    private void dibujar() {
        SuperficieDeDibujo.getInstance().dibujar();
    }
}
