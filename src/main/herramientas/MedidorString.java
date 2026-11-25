package main.herramientas;

import java.awt.*;

public class MedidorString {

    // Graphics propio (imagen 1x1) para medir texto sin depender de la superficie de dibujo
    private static final Graphics2D graficosMedida = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB).createGraphics();

    public static int medirAnchoPixeles(final String texto, final Font fuente, final int tamanioFuente) {
        return medirAnchoPixeles(graficosMedida, texto, fuente, tamanioFuente);
    }

    public static int medirAltoPixeles(final String texto, final Font fuente, final int tamanioFuente) {
        return medirAltoPixeles(graficosMedida, texto, fuente, tamanioFuente);
    }


    public static int medirAnchoPixeles(final Graphics graficos, final String texto, final Font fuente, final int tamanioFuente) {
        Font fuenteEscalada = fuente.deriveFont(Font.PLAIN, tamanioFuente);
        return graficos.getFontMetrics(fuenteEscalada).stringWidth(texto);
    }

    public static int medirAltoPixeles(final Graphics graficos, final String texto, final Font fuente, final int tamanioFuente) {
        Font fuenteEscalada = fuente.deriveFont(Font.PLAIN, tamanioFuente);
        return (int) graficos.getFontMetrics(fuenteEscalada).getLineMetrics(texto, graficos).getHeight();
    }

    public static String eliminarSubcadena(String texto, String subcadena) {
        // Verifica si la subcadena está presente en el texto
        if (texto.contains(subcadena)) {
            // Reemplaza la subcadena con una cadena vacía
            return texto.replace(subcadena, "");
        } else {
            // Si no se encuentra la subcadena, retorna el texto original
            return texto;
        }
    }

}
