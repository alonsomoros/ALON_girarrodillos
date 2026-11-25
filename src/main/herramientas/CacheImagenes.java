package main.herramientas;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Caché de imágenes ya cargadas y convertidas a un formato compatible con la gráfica.
 * Evita releer el PNG del disco y reconvertirlo en cada frame.
 * La clave combina la ruta y el tipo de transparencia ({@link java.awt.Transparency}),
 * porque la misma ruta puede cargarse como opaca o como translúcida.
 */
public class CacheImagenes {

    /** Si es false, la caché no guarda nada (útil para depurar). */
    public static boolean activa = true;

    private static final Map<String, BufferedImage> imagenes = new HashMap<>();

    private CacheImagenes() {
    }

    private static String clave(final String ruta, final int transparencia) {
        return transparencia + "|" + ruta;
    }

    /** Devuelve la imagen cacheada o null si no está (o la caché está desactivada). */
    public static synchronized BufferedImage obtener(final String ruta, final int transparencia) {
        if (!activa) {
            return null;
        }
        return imagenes.get(clave(ruta, transparencia));
    }

    public static synchronized void guardar(final String ruta, final int transparencia, final BufferedImage imagen) {
        if (activa && imagen != null) {
            imagenes.put(clave(ruta, transparencia), imagen);
        }
    }

    /** Carga por adelantado las rutas indicadas con la transparencia dada (java.awt.Transparency.*). */
    public static void precargar(final int transparencia, final String... rutas) {
        for (String ruta : rutas) {
            if (transparencia == java.awt.Transparency.OPAQUE) {
                CargadorRecursos.cargarImagenCompatibleOpaca(ruta);
            } else {
                CargadorRecursos.cargarImagenCompatibleTranslucida(ruta);
            }
        }
    }

    /** Carga por adelantado todos los .png de una carpeta (sin entrar en subcarpetas). */
    public static void precargarCarpeta(final int transparencia, final String carpeta) {
        java.io.File[] archivos = new java.io.File(carpeta).listFiles();
        if (archivos == null) {
            return;
        }
        for (java.io.File archivo : archivos) {
            if (archivo.isFile() && archivo.getName().toLowerCase().endsWith(".png")) {
                precargar(transparencia, carpeta + "/" + archivo.getName());
            }
        }
    }

    public static synchronized void limpiar() {

        imagenes.clear();
    }

    public static synchronized int tamanio() {
        return imagenes.size();
    }
}
