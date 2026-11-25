package main.sprites;

import main.herramientas.CargadorRecursos;

import java.awt.image.BufferedImage;

public class HojaSprites {
    final private int anchoHojaEnPixeles;
    final private int altoHojaEnPixeles;

    final private int anchoHojaEnSprites;
    final private int altoHojaEnSprites;

    final private int anchoSprites;
    final private int altoSprites;

    final private Sprite[] sprites;

    public HojaSprites(final String ruta, final int tamagnoSprites, final boolean hojaOpaca) {
        final BufferedImage imagen;

        if (hojaOpaca) {
            imagen = CargadorRecursos.cargarImagenCompatibleOpaca(ruta);
        } else {
            imagen = CargadorRecursos.cargarImagenCompatibleTranslucida(ruta);
        }

        anchoHojaEnPixeles = imagen.getWidth();
        altoHojaEnPixeles = imagen.getHeight();

        anchoHojaEnSprites = anchoHojaEnPixeles / tamagnoSprites;
        altoHojaEnSprites = altoHojaEnPixeles / tamagnoSprites;

        anchoSprites = tamagnoSprites;
        altoSprites = tamagnoSprites;

        sprites = new Sprite[anchoHojaEnSprites * altoHojaEnSprites];

        cargarSpritesDesdeImagen(imagen);
    }

    public HojaSprites(final String ruta, final int anchoSprites, final int altoSprites, final boolean hojaOpaca) {
        final BufferedImage imagen;

        if (hojaOpaca) {
            imagen = CargadorRecursos.cargarImagenCompatibleOpaca(ruta);
        } else {
            imagen = CargadorRecursos.cargarImagenCompatibleTranslucida(ruta);
        }

        anchoHojaEnPixeles = imagen.getWidth();
        altoHojaEnPixeles = imagen.getHeight();

        anchoHojaEnSprites = anchoHojaEnPixeles / anchoSprites;
        altoHojaEnSprites = altoHojaEnPixeles / altoSprites;

        this.anchoSprites = anchoSprites;
        this.altoSprites = altoSprites;

        sprites = new Sprite[anchoHojaEnSprites * altoHojaEnSprites];

        cargarSpritesDesdeImagen(imagen);
    }

    private void cargarSpritesDesdeImagen(final BufferedImage imagen) {

        for (int y = 0; y < altoHojaEnSprites; y++) {
            for (int x = 0; x < anchoHojaEnSprites; x++) {
                final int posicionX = x * anchoSprites;
                final int posicionY = y * altoSprites;

                sprites[x + y * anchoHojaEnSprites] = new Sprite(imagen.getSubimage(posicionX, posicionY, anchoSprites, altoSprites));
            }
        }
    }

    public Sprite obtenerSprite(final int indice) {
        return sprites[indice];
    }

    public Sprite obtenerSprite(final int x, final int y) {
        return sprites[x + y * anchoHojaEnSprites];
    }

    public int obtenerNumeroSprites() {
        return sprites.length;
    }

}
