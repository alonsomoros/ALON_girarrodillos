import main.AudioPlayer;
import main.Constantes;
import main.herramientas.Depurar;
import main.maquina_estado.Estado;
import main.maquina_estado.GestorEstados;
import main.maquina_estado.galeria.GestorGaleria;
import main.maquina_estado.tienda.Progreso;
import main.motor.Tiempo;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Saca una captura PNG de una pantalla sin abrir ventana. Se usa con scripts\capturar.bat:
 * <pre>Capturador ESTADO salida.png [ticks] [entrada-galeria]</pre>
 */
public class Capturador {

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: Capturador <MENU|JUEGO|TIENDA|MUSICA|CREDITOS|GALERIA> <salida.png> [ticks] [entrada de la galeria]");
            System.exit(1);
        }
        Estado estado = Estado.valueOf(args[0].toUpperCase());
        File salida = new File(args[1]);
        int ticks = args.length > 2 ? Integer.parseInt(args[2]) : 0;

        AudioPlayer.efectosActivos = false;      // Sin sonido
        Progreso.usarSoloEnMemoria();            // Sin tocar tu guardado
        Depurar.ACTIVO = true;                   // La galería necesita el modo depuración

        GestorEstados gestor = GestorEstados.getInstance();
        gestor.cambiarEstadoActual(estado);
        if (estado == Estado.GALERIA && args.length > 3) {
            ((GestorGaleria) gestor.getEstado(Estado.GALERIA)).lanzarEntrada(Integer.parseInt(args[3]));
        }

        for (int i = 0; i < ticks; i++) {
            Tiempo.avanzar();
            gestor.actualizar();
        }

        BufferedImage imagen = new BufferedImage(Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.setFont(Constantes.FUENTES.FUENTE_TEXTO);
        gestor.dibujar(g);
        g.dispose();

        File carpeta = salida.getAbsoluteFile().getParentFile();
        if (carpeta != null) {
            carpeta.mkdirs();
        }
        ImageIO.write(imagen, "png", salida);
        System.out.println("Captura guardada en " + salida.getAbsolutePath());
        System.exit(0);
    }
}
