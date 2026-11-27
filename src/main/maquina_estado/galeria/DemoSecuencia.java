package main.maquina_estado.galeria;

import main.herramientas.Dibujador;
import main.motor.Efecto;
import main.motor.GestorEfectos;
import main.motor.Interpolador;
import main.motor.Secuencia;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/** Demostración de una Secuencia: espera, mueve una bola con easing, y al llegar muestra un aviso y la hace parpadear. */
class DemoSecuencia implements Efecto {

    private final Rectangle area;
    private double x = 0;       // 0 a 1 a lo largo de la pista
    private String aviso = "esperando...";
    private boolean parpadeo = false;
    private boolean terminado = false;

    DemoSecuencia(final Rectangle area, final GestorEfectos gestor) {
        this.area = area;
        gestor.agregar(Secuencia.nueva()
                .esperar(600)
                .hacer(() -> aviso = "moviendo (durante 900 ms)")
                .durante(900, t -> x = Interpolador.RETROCESO.aplicar(t))
                .hacer(() -> aviso = "¡llegó! parpadeo x3")
                .durante(600, t -> parpadeo = ((int) (t * 6)) % 2 == 0)
                .hacer(() -> {
                    parpadeo = false;
                    aviso = "fin de la secuencia";
                })
                .esperar(900)
                .hacer(() -> terminado = true));
    }

    @Override
    public void actualizar() {
    }

    @Override
    public void dibujar(final Graphics graficos) {
        int pista = area.width - 120;
        int cx = area.x + 40 + (int) (pista * x);
        int cy = area.y + area.height / 2;
        Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, new Rectangle(area.x + 40, cy + 30, pista, 2), new Color(0x3A4056));
        Dibujador.CIRCULO.dibujar(graficos, cx + 20, cy, 20, parpadeo ? Color.WHITE : new Color(0xE4DEB0));
        Dibujador.TEXTO.dibujar(graficos, aviso, new Point(area.x + 40, area.y + 40), 20, Color.WHITE);
    }

    @Override
    public boolean terminado() {
        return terminado;
    }
}
