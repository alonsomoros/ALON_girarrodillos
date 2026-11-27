package main.motor;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasSonidos {

    public static void ejecutar() {
        prueba("Sonidos: volumen lineal a decibelios (1 = 0 dB, 0,5 = -6 dB, 0 = casi mudo)", () -> {
            verificar(Sonidos.volumenADecibelios(1.0) == 0f, "1.0");
            verificar(Math.abs(Sonidos.volumenADecibelios(0.5) + 6.02f) < 0.05f, "0.5 = " + Sonidos.volumenADecibelios(0.5));
            verificar(Sonidos.volumenADecibelios(0) == -80f && Sonidos.volumenADecibelios(-1) == -80f, "0 y negativos");
            verificar(Sonidos.volumenADecibelios(5.0) == 0f, "más de 1 se limita a 0 dB");
            verificar(Sonidos.volumenADecibelios(0.1) < Sonidos.volumenADecibelios(0.9), "creciente");
        });

        prueba("Sonidos: volúmenes limitados a [0, 1], silencio y precargar un sonido inexistente no rompe", () -> {
            Sonidos s = Sonidos.getInstance();
            s.setVolumenEfectos(3);
            verificar(s.getVolumenEfectos() == 1.0, "tope superior");
            s.setVolumenEfectos(-2);
            verificar(s.getVolumenEfectos() == 0.0, "tope inferior");
            s.setVolumenEfectos(1);
            s.setVolumenMusica(0.4);
            verificar(Math.abs(s.volumenMusicaEfectivo() - 0.4) < 1e-9, "música efectiva");
            s.setSilencio(true);
            verificar(s.estaSilenciado() && s.volumenMusicaEfectivo() == 0, "silencio");
            s.setSilencio(false);
            s.setVolumenMusica(1);
            verificar(!s.precargar("rec/sonidos/no_existe.wav"), "un archivo inexistente devuelve false");
            s.reproducir("rec/sonidos/no_existe.wav"); // no lanza excepción
        });
    }
}
