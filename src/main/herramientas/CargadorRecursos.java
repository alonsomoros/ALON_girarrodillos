package main.herramientas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class CargadorRecursos {


    // Se carga una imagen normal y se comprueba si se cumplen los requisitos de poder acelerarla por la grafica propia
    public static BufferedImage cargarImagenCompatibleOpaca(final String ruta) {
        BufferedImage enCache = CacheImagenes.obtener(ruta, Transparency.OPAQUE);
        if (enCache != null) {
            return enCache;
        }

        BufferedImage imagen = null;

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("File does not exist: " + ruta);
        } else if (!file.canRead()) {
            System.out.println("Cannot read file: " + ruta);
        } else {
            try {
                imagen = ImageIO.read(file);
            } catch (IOException e) {
                System.out.println("Error reading the image: " + ruta);
                throw new RuntimeException(e);
            }
        }

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

        BufferedImage imagen = null;

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("File does not exist: " + ruta);
        } else if (!file.canRead()) {
            System.out.println("Cannot read file: " + ruta);
        } else {
            try {
                imagen = ImageIO.read(file);
            } catch (IOException e) {
                System.out.println("Error reading the image: " + ruta);
                throw new RuntimeException(e);
            }
        }

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        BufferedImage imagenAcelerada = gc.createCompatibleImage(imagen.getWidth(null), imagen.getHeight(null), Transparency.TRANSLUCENT);
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                // Comprobar si el píxel actual es transparente
                if ((imagen.getRGB(x, y) >> 24) == 0x00) {
                    // Comprobar si algún píxel vecino es opaco
                    if (tieneVecinoOpaco(imagen, x, y)) {
                        // Si es así, pintar el píxel actual en la imagen de bordes
//                        imagen.setRGB(x, y, Color.WHITE.getRGB());
                    } else {
//                        imagen.setRGB(x, y, Color.BLACK.getRGB());
                    }
                } else {
//                    imagen.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        Graphics graficos = imagenAcelerada.getGraphics();
        graficos.drawImage(imagen, 0, 0, null);
        graficos.dispose();

        CacheImagenes.guardar(ruta, Transparency.TRANSLUCENT, imagenAcelerada);
        return imagenAcelerada;
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

    private static boolean tieneVecinoOpaco(BufferedImage image, int x, int y) {
        // Coordenadas de los vecinos
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        // Comprobar cada vecino
        for (int i = 0; i < dx.length; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            // Comprobar si está dentro de los límites de la imagen
            if (nx >= 0 && nx < image.getWidth() && ny >= 0 && ny < image.getHeight()) {
                // Comprobar si el vecino es opaco
                if ((image.getRGB(nx, ny) >> 24) != 0x00) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Font cargarFuente(final String ruta) {
        Font fuente = null;

//        InputStream entradaBytes = ClassLoader.class.getResourceAsStream(ruta);
//
//        try {
//            fuente = Font.createFont(Font.TRUETYPE_FONT, entradaBytes);
//        } catch (FontFormatException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
        return fuente;
    }

    public static boolean spriteVacio(BufferedImage imagen) {
        boolean estaVacio = true;
        for (int y1 = 0; y1 < imagen.getHeight(); y1++) {
            for (int x1 = 0; x1 < imagen.getWidth(); x1++) {
                int pixel = imagen.getRGB(x1, y1);
                if (pixel != 0 && pixel != Color.TRANSLUCENT) { //No es transparente
                    estaVacio = false;
                    break;
                }
            }
            if (!estaVacio) {
                break;
            }
        }
        return estaVacio;
    }

    public static void unzip(String rutaOrigen, String rutaDestino) throws IOException {
        File destDir = new File(rutaDestino);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(rutaOrigen));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = rutaDestino + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                new File(filePath).mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
