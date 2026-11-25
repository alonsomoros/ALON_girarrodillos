package main.herramientas;

import main.control.Etiqueta;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.Map;


public class Dibujador {

    public static int numeroObjetosDibujados = 0;

    public static class IMAGEN {

        public static void dibujar(final Graphics g, final Image imagen, final int x, final int y) {
            g.drawImage(imagen, x, y, null);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Image imagen, final Point punto) {
            g.drawImage(imagen, punto.x, punto.y, null);
            numeroObjetosDibujados++;
        }
        public static void dibujar(final Graphics g, final Image imagen, final int x, final int y, final int ancho, final int alto) {
            g.drawImage(imagen, x, y, ancho, alto,null);
            numeroObjetosDibujados++;
        }

        // Imágenes ya volteadas (se calculan una vez, no en cada frame)
        private static final Map<Image, BufferedImage> volteadas = new IdentityHashMap<>();

        private static BufferedImage voltear(final BufferedImage imagenBuff) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-imagenBuff.getWidth(), 0);

            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            return op.filter(imagenBuff, null);
        }

        // Igual que las volteadas en horizontal, pero boca abajo (espejo vertical)
        private static final Map<Image, BufferedImage> volteadasVertical = new IdentityHashMap<>();

        public static void dibujarVolteadoVertical(final Graphics g, final Image imagen, final Point punto) {
            BufferedImage reflejada = volteadasVertical.get(imagen);
            if (reflejada == null) {
                BufferedImage original = (BufferedImage) imagen;
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx.translate(0, -original.getHeight());
                reflejada = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(original, null);
                volteadasVertical.put(imagen, reflejada);
            }
            g.drawImage(reflejada, punto.x, punto.y, null);
            numeroObjetosDibujados++;
        }

        public static void dibujarRotado(final Graphics g, final Image imagen, final Point punto) {

            BufferedImage reflectedImage = volteadas.get(imagen);
            if (reflectedImage == null) {
                reflectedImage = voltear((BufferedImage) imagen);
                volteadas.put(imagen, reflectedImage);
            }

            g.drawImage(reflectedImage, punto.x, punto.y, null);
            numeroObjetosDibujados++;
        }


    }

    public static class TEXTO {
        public static void dibujar(final Graphics g, final String texto, final int x, final int y) {
            g.drawString(texto, x, y);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final String texto, final Point punto) {
            g.drawString(texto, punto.x, punto.y);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final String texto, final int x, final int y, final Color color) {
            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int altoTexto = (int) g.getFontMetrics().getLineMetrics(texto, g).getHeight();

            g.setColor(color);
            g.drawString(texto, x - anchoTexto / 2, y - altoTexto / 2);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final String texto, final int x, final int y, final Font fuente, final Color color) {

            g.setFont(fuente);

            g.setColor(color);
            g.drawString(texto, x, y);
            numeroObjetosDibujados++;

        }

        public static void dibujar(final Graphics g, final String texto, final int x, final int y, final int tamanioString, final Color color) {

            Font fontOriginal = g.getFont();

            Font fontGrande = fontOriginal.deriveFont(Font.PLAIN, tamanioString);
            g.setFont(fontGrande);

            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int altoTexto = (int) g.getFontMetrics().getLineMetrics(texto, g).getHeight();

            g.setColor(color);
            g.drawString(texto, x - anchoTexto / 2, y - altoTexto / 2);
            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }

        public static void dibujarConSombra(final Graphics g, final String texto, final int x, final int y, final int tamanioString, final Color color) {

            Font fontOriginal = g.getFont();

            Font fontGrande = fontOriginal.deriveFont((float) tamanioString);
            g.setFont(fontGrande);


            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int altoTexto = (int) g.getFontMetrics().getLineMetrics(texto, g).getHeight();

            // Hace la sombra y lo centra a la pantalla
            g.setColor(Color.BLACK);
            g.drawString(texto, x - anchoTexto / 2 + 3, y - altoTexto / 2 + 3);
            g.setColor(color);
            g.drawString(texto, x - anchoTexto / 2, y - altoTexto / 2);

            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }


        public static void dibujar(final Graphics g, final String texto, final int x, final int y, final int tamanioString, final Font fuente, final Color color) {
            Font fontOriginal = g.getFont();

            g.setFont(fuente);

            Font fontGrande = fuente.deriveFont(Font.PLAIN, tamanioString);
            g.setFont(fontGrande);

            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int altoTexto = (int) g.getFontMetrics().getLineMetrics(texto, g).getHeight();

            g.setColor(color);
            g.drawString(texto, x - anchoTexto / 2, y - altoTexto / 2);
            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }

        public static void dibujarConSombra(final Graphics g, final String texto, final int x, final int y, final int tamanioString, final Font fuente, final Color color) {
            Font fontOriginal = g.getFont();

            g.setFont(fuente);

            Font fontGrande = fuente.deriveFont(Font.PLAIN, tamanioString);
            g.setFont(fontGrande);

            int anchoTexto = g.getFontMetrics().stringWidth(texto);
            int altoTexto = (int) g.getFontMetrics().getLineMetrics(texto, g).getHeight();

            g.setColor(Color.black);
            g.drawString(texto, x - anchoTexto / 2 + 3, y - altoTexto / 2 + 3);
            g.setColor(color);
            g.drawString(texto, x - anchoTexto / 2, y - altoTexto / 2);
            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }


        public static void dibujar(final Graphics g, final String texto, final Point punto, final Color color) {
            g.setColor(color);
            g.drawString(texto, punto.x, punto.y);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final String texto, final Point punto, final int tamanioString, final Color color) {
            Font fontOriginal = g.getFont();
            Font fontGrande = fontOriginal.deriveFont(Font.PLAIN, tamanioString);
            g.setFont(fontGrande);

            g.setColor(color);
            g.drawString(texto, punto.x, punto.y);
            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }
    }


    public static class RECTANGULO_RELLENO {

        public static void dibujar(final Graphics g, final int x, final int y, final int ancho, final int alto, final Color color) {
            Color original = g.getColor();
            g.setColor(color);
            g.fillRect(x, y, ancho, alto);
            g.setColor(original);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Rectangle area, final Color color) {
            Color original = g.getColor();
            g.setColor(color);
            g.fillRect(area.x, area.y, area.width, area.height);
            g.setColor(original);
            numeroObjetosDibujados++;
        }
    }

    public static class RECTANGULO_CONTORNO {

        public static void dibujar(final Graphics g, final Point coordenadas, final Point tamanio) {
            g.drawRect(coordenadas.x, coordenadas.y, tamanio.x, tamanio.y);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int x, final int y, final int ancho, final int alto, final Color color) {
            g.setColor(color);
            g.drawRect(x, y, ancho, alto);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int x, final int y, final int ancho, final int alto, final int grosor, final Color color) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(grosor));
            g.setColor(color);
            g.drawRect(x, y, ancho, alto);
            g2.setStroke(new BasicStroke(1));
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int x, final int y, final Point tamanio, final Color color) {
            g.setColor(color);
            g.drawRect(x, y, tamanio.x, tamanio.y);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Rectangle rectangle, final Color color) {
            g.setColor(color);
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Rectangle rectangle, final int grosor, final Color color) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(grosor));
            g.setColor(color);
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            g2.setStroke(new BasicStroke(1));
            numeroObjetosDibujados++;
        }
    }


    public static class LINEA2D {
        public static void dibujar(final Graphics graficos, Point coordenadasI, Point coordenadasF, final Color color) {
            graficos.setColor(color);
            graficos.drawLine(coordenadasI.x, coordenadasI.y, coordenadasF.x, coordenadasF.y);
            numeroObjetosDibujados++;
        }
    }

    public static class CIRCULO {
        public static void dibujar(final Graphics g, final int x, final int y, final int radio, final Color color) {
            g.setColor(color);
            g.fillOval(x - radio, y - radio, radio * 2, radio * 2);
            numeroObjetosDibujados++;
        }
    }

    public static class SEMICIRCULO {

        public static void dibujar(final Graphics g, final int x, final int y, final int radio, final Color color) {
            g.setColor(color);
            g.fillArc(x - radio, y - radio, radio * 2, radio * 2, 0, 180);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int x, final int y, final int radio, final int anguloInicio, final int anguloArc, final Color color) {
            g.setColor(color);
            g.fillArc(x - radio, y - radio, radio * 2, radio * 2, anguloInicio, anguloArc);
            numeroObjetosDibujados++;
        }
    }

    public static class TRIANGULO {
        public static void dibujar(final Graphics g, final Point p1, final Point p2, final Point p3, final Color color) {
            g.setColor(color);
            int[] x = {p1.x, p2.x, p3.x};
            int[] y = {p1.y, p2.y, p3.y};
            g.fillPolygon(x, y, 3);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int x1, final int x2, final int x3, final int y1_3, final int y2, final Color color) {
            g.setColor(color);
            int[] x = {x1, x2, x3};
            int[] y = {y1_3, y2, y1_3};
            g.fillPolygon(x, y, 3);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Point[] triangulo, final Color color) {
            g.setColor(color);
            int[] x = {triangulo[0].x, triangulo[1].x, triangulo[2].x};
            int[] y = {triangulo[0].y, triangulo[1].y, triangulo[2].y};
            g.fillPolygon(x, y, 3);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final int posicion, final Point[] triangulo, final Color color) {
            g.setColor(color);
            int[] x = {triangulo[0].x + posicion * 69, triangulo[1].x+ posicion * 69, triangulo[2].x+ posicion * 69};
            int[] y = {triangulo[0].y, triangulo[1].y, triangulo[2].y};
            g.fillPolygon(x, y, 3);
            numeroObjetosDibujados++;
        }

        public static void dibujar(final Graphics g, final Triangulo triangulo, final Color color) {
            g.setColor(color);
            int[] x = {triangulo.getPuntos()[0].x, triangulo.getPuntos()[1].x, triangulo.getPuntos()[2].x};
            int[] y = {triangulo.getPuntos()[0].y, triangulo.getPuntos()[1].y, triangulo.getPuntos()[2].y};
            g.fillPolygon(x, y, 3);
            numeroObjetosDibujados++;
        }
    }

    public static class ETIQUETA {
        public static void dibujarEtiquetas(Graphics graficos, Etiqueta etiqueta, Color color) {
            Font fontOriginal = graficos.getFont();
            Font fontGrande = fontOriginal.deriveFont(Font.PLAIN, 30);
            graficos.setFont(fontGrande);

            graficos.setColor(Color.black);
            graficos.drawString(etiqueta.getNombre(), etiqueta.getPosicion().x + 3, etiqueta.getPosicion().y + 3);
            graficos.setColor(color);
            graficos.drawString(etiqueta.getNombre(), etiqueta.getPosicion().x, etiqueta.getPosicion().y);
            graficos.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }

        public static void dibujarEtiquetas(Graphics g, Etiqueta etiqueta, final int tamanio, Color color) {
            Font fontOriginal = g.getFont();
            Font fontGrande = fontOriginal.deriveFont(Font.PLAIN, tamanio);
            g.setFont(fontGrande);

            g.setColor(Color.black);
            g.drawString(etiqueta.getNombre(), etiqueta.getPosicion().x + 2, etiqueta.getPosicion().y + 2);
            g.setColor(color);
            g.drawString(etiqueta.getNombre(), etiqueta.getPosicion().x, etiqueta.getPosicion().y);
            g.setFont(fontOriginal);
            numeroObjetosDibujados++;
        }
    }
}
