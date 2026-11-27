package main.herramientas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;


public class CargadorRecursos {

    private static final Set<String> rutasAvisadas = new HashSet<>();

    // Lee la imagen del disco. Si no existe o no se puede leer devuelve un cuadrado magenta
    // (y avisa una sola vez por ruta) en lugar de romper el juego con un NullPointerException.
    private static BufferedImage leerImagen(final String ruta) {
        File file = new File(ruta);
        String problema = null;

        if (!file.exists()) {
            problema = "No existe el archivo";
        } else if (!file.canRead()) {
            problema = "No se puede leer el archivo";
        } else {
            try {
                BufferedImage imagen = ImageIO.read(file);
                if (imagen != null) {
                    return imagen;
                }
                problema = "Formato de imagen no reconocido";
            } catch (IOException e) {
                problema = "Error leyendo la imagen (" + e.getMessage() + ")";
            }
        }

        if (rutasAvisadas.add(ruta)) {
            System.err.println(problema + ": " + ruta);
        }
        return crearPlaceholder();
    }

    private static BufferedImage crearPlaceholder() {
        BufferedImage placeholder = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 32, 32);
        g.dispose();
        return placeholder;
    }

    // Se carga una imagen normal y se comprueba si se cumplen los requisitos de poder acelerarla por la grafica propia
    public static BufferedImage cargarImagenCompatibleOpaca(final String ruta) {
        BufferedImage enCache = CacheImagenes.obtener(ruta, Transparency.OPAQUE);
        if (enCache != null) {
            return enCache;
        }

        BufferedImage imagen = leerImagen(ruta);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        BufferedImage imagenAcelerada = gc.createCompatibleImage(imagen.getWidth(null), imagen.getHeight(null), Transparency.OPAQUE);

        Graphics graficos = imagenAcelerada.getGraphics();
        graficos.drawImage(imagen, 0, 0, null);
        graficos.dispose();

        CacheImagenes.guardar(ruta, Transparency.OPAQUE, imagenAcelerada);
        return imagenAcelerada;
    }

    // Translucido
    public static BufferedImage cargarImagenCompatibleTranslucida(final String ruta) {
        BufferedImage enCache = CacheImagenes.obtener(ruta, Transparency.TRANSLUCENT);
        if (enCache != null) {
            return enCache;
        }

        BufferedImage imagen = leerImagen(ruta);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        BufferedImage imagenAcelerada = gc.createCompatibleImage(imagen.getWidth(null), imagen.getHeight(null), Transparency.TRANSLUCENT);

        Graphics graficos = imagenAcelerada.getGraphics();
        graficos.drawImage(imagen, 0, 0, null);
        graficos.dispose();

        CacheImagenes.guardar(ruta, Transparency.TRANSLUCENT, imagenAcelerada);
        return imagenAcelerada;
    }

    // Carga una imagen ya reducida (o ampliada) al tamaño pedido y la cachea con ese tamaño. Pensada para los
    // fondos grandes (3840x2400...): así no se guardan en memoria a tamaño completo ni se escalan en cada frame.
    public static BufferedImage cargarEscalada(final String ruta, final int ancho, final int alto) {
        return cargarEscalada(ruta, ancho, alto, Transparency.OPAQUE);
    }

    public static BufferedImage cargarEscalada(final String ruta, final int ancho, final int alto, final int transparencia) {
        final String clave = ruta + "@" + ancho + "x" + alto;
        BufferedImage enCache = CacheImagenes.obtener(clave, transparencia);
        if (enCache != null) {
            return enCache;
        }

        BufferedImage origen = leerImagen(ruta); // No se guarda en la caché: solo interesa la escalada
        BufferedImage escalada = escalar(origen, ancho, alto, transparencia);
        CacheImagenes.guardar(clave, transparencia, escalada);
        return escalada;
    }

    // Escala con buena calidad: si se reduce a menos de la mitad, lo hace por pasos de la mitad (evita dientes de sierra)
    private static BufferedImage escalar(final BufferedImage origen, final int ancho, final int alto, final int transparencia) {
        BufferedImage actual = origen;
        int w = origen.getWidth();
        int h = origen.getHeight();

        while (w / 2 >= ancho && h / 2 >= alto) {
            w /= 2;
            h /= 2;
            actual = dibujarEscalada(actual, w, h, transparencia);
        }
        if (w != ancho || h != alto) {
            actual = dibujarEscalada(actual, ancho, alto, transparencia);
        } else if (actual == origen) {
            actual = dibujarEscalada(actual, ancho, alto, transparencia); // Mismo tamaño: solo la convierte a formato compatible
        }
        return actual;
    }

    private static BufferedImage dibujarEscalada(final BufferedImage origen, final int ancho, final int alto, final int transparencia) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage destino = gc.createCompatibleImage(ancho, alto, transparencia);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(origen, 0, 0, ancho, alto, null);
        g.dispose();
        return destino;
    }

    public static String leerArchivoTexto

(final String ruta) {
        StringBuilder contenido = new StringBuilder();

        FileInputStream entradaBytes = null;
        BufferedReader lector = null;
        try {
            entradaBytes = new FileInputStream(ruta);
            lector = new BufferedReader(new InputStreamReader(entradaBytes));

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Archivo no encontrado " + ruta);
        }

        String linea;

        try {
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo " + ruta);
        } finally {
            try {
                if (entradaBytes != null) {
                    entradaBytes.close();
                }
                if (lector != null) {
                    lector.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error al cerrar el archivo " + ruta);
            }
        }

        return contenido.toString();

    }

    // Carga una fuente .ttf (tamaño 15). Si el archivo falta o es inválido, avisa una vez y usa SansSerif.
    public static Font cargarFuente(final String ruta) {
        File archivo = new File(ruta);
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, archivo);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
            return fuente.deriveFont(Font.PLAIN, 15f);
        } catch (FontFormatException | IOException e) {
            if (rutasAvisadas.add(ruta)) {
                System.err.println("No se pudo cargar la fuente " + ruta + " (" + e.getMessage() + "), se usa SansSerif");
            }
            return new Font(Font.SANS_SERIF, Font.PLAIN, 15);
        }
    }

}
