package main.maquina_estado.juego;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

/**
 * Pruebas de las clases del juego. Está en el mismo paquete que ellas para poder
 * usar los miembros protegidos/de paquete (actualizarEstados, estaAtacando, ...).
 */
public class PruebasJuego {

    // Pone las dos figuras con el activador a 0 y deja que el jugador lance sus ataques
    private static Player jugadorConFiguras(final Figura izquierda, final Figura derecha) {
        Player p = new Player();
        izquierda.setPosicion(3);
        derecha.setPosicion(4);
        p.setFiguraIzquierda(izquierda);

        p.setFiguraDerecha(derecha);
        izquierda.getActivador().setAltura(0);
        derecha.getActivador().setAltura(0);
        return p;
    }

    // Actualiza la figura (como hace el bucle del juego) hasta que termine de atacar
    private static void esperarFinDeAtaques(final Figura f) throws Exception {
        long limite = System.currentTimeMillis() + 3000;
        while (f.estaAtacando && System.currentTimeMillis() < limite) {
            f.actualizar();
            Thread.sleep(2);
        }
        verificar(!f.estaAtacando, "la figura no termina de atacar");
    }

    public static void ejecutar() {
        Ataque.duracionPorDefecto = 30; // animaciones cortas para las pruebas

        prueba("Mago: sus 2 ataques se animan en secuencia y dan la XP una sola vez", () -> {
            Player p = new Player();
            IA ia = new IA();
            p.setEnemigo(ia);
            ia.setEnemigo(p);
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p);
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p);
            mago.setPosicion(3);
            arquero.setPosicion(4);
            p.setFiguraIzquierda(mago);
            p.setFiguraDerecha(arquero);
            mago.getActivador().setAltura(0);
            arquero.getActivador().setAltura(3); // no se activa
            p.actualizarEstados();
            verificar(mago.estaAtacando, "el Mago debería estar atacando");
            verificar(mago.getAtaquesLanzados() == 2, "ataques " + mago.getAtaquesLanzados());
            esperarFinDeAtaques(mago);
            verificar(mago.getExperiencia() == 2, "XP " + mago.getExperiencia() + " (esperada 2: una activación)");
            verificar(mago.getActivador().getAltura() == mago.getAlturaDefault(), "el activador no se reseteó");
        });


        prueba("Mago a la derecha ataca 2 veces y la izquierda (Arquero) 1", () -> {
            Player p0 = new Player();
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p0);
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p0);
            Player p = jugadorConFiguras(arquero, mago);
            p.actualizarEstados();
            verificar(arquero.getAtaquesLanzados() == 1, "Arquero: " + arquero.getAtaquesLanzados());
            verificar(mago.getAtaquesLanzados() == 2, "Mago: " + mago.getAtaquesLanzados());
        });

        prueba("Arquero a la derecha con Mago a la izquierda ataca 1 sola vez", () -> {
            Player p0 = new Player();
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p0);
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p0);
            Player p = jugadorConFiguras(mago, arquero);
            p.actualizarEstados();
            verificar(mago.getAtaquesLanzados() == 2, "Mago: " + mago.getAtaquesLanzados());
            verificar(arquero.getAtaquesLanzados() == 1, "Arquero: " + arquero.getAtaquesLanzados());
        });


        prueba("un ataque del jugador daña a la IA, no al propio jugador", () -> {
            Player p = new Player();
            IA ia = new IA();
            p.setEnemigo(ia);
            ia.setEnemigo(p);
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p);
            Figura sacerdote = new main.maquina_estado.juego.figuras.Sacerdote(p);
            arquero.setPosicion(3);
            sacerdote.setPosicion(4);
            p.setFiguraIzquierda(arquero);
            p.setFiguraDerecha(sacerdote);
            arquero.getActivador().setAltura(0);
            sacerdote.getActivador().setAltura(3);
            int vidaPlayer = p.getVida(), vidaIA = ia.getVida();
            p.actualizarEstados();
            esperarFinDeAtaques(arquero);
            verificar(p.getVida() == vidaPlayer, "el jugador perdió vida: " + vidaPlayer + " -> " + p.getVida());
            verificar(ia.getVida() < vidaIA, "la IA no perdió vida: " + vidaIA + " -> " + ia.getVida());
        });

        prueba("héroe de oro con la XP llena lanza la bomba: 2 de daño directo y termina limpio", () -> {
            Player p = new Player();
            IA ia = new IA();
            p.setEnemigo(ia);
            ia.setEnemigo(p);
            Figura sacerdote = new main.maquina_estado.juego.figuras.Sacerdote(p);
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p);
            sacerdote.setPosicion(3);
            mago.setPosicion(4);
            p.setFiguraIzquierda(sacerdote);
            p.setFiguraDerecha(mago);
            sacerdote.nivel = 3;
            sacerdote.setExperiencia(4); // +2 al actuar = 6
            sacerdote.getActivador().setAltura(0);
            mago.getActivador().setAltura(3);
            ia.getBastion().aumentarAltura(5); // el bastión no debe frenar la bomba
            int vidaIA = ia.getVida();
            p.actualizarEstados();
            esperarFinDeAtaques(sacerdote);
            verificar(ia.getVida() == vidaIA - 2, "vida IA " + vidaIA + " -> " + ia.getVida());
            verificar(sacerdote.getExperiencia() == 0, "XP " + sacerdote.getExperiencia());
            verificar(sacerdote.getNivel() == 3, "nivel " + sacerdote.getNivel());
            verificar(!sacerdote.estaAtacando, "sigue atacando");
        });

        prueba("el jugador y la IA tienen rodillos distintos", () -> {


            Player player = new Player();
            IA ia = new IA();
            verificar(player.getRodillo() != null && ia.getRodillo() != null, "rodillo null");
            verificar(player.getRodillo() != ia.getRodillo(), "comparten el mismo rodillo");
        });
    }
}
