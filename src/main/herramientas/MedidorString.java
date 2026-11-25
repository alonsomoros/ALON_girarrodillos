package main.herramientas;

import java.awt.*;

public class MedidorString {

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
