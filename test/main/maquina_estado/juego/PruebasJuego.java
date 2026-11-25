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

        prueba("XP llena: sube de nivel y la XP vuelve a 0; en oro encola la bomba", () -> {
            Player p = new Player();
            Figura f = new main.maquina_estado.juego.figuras.Arquero(p);
            f.setPosicion(3);
            f.aumentarExperiencia(5);
            verificar(f.getNivel() == 1 && f.getExperiencia() == 5, "5 XP: nivel " + f.getNivel() + " XP " + f.getExperiencia());
            f.aumentarExperiencia(1);
            verificar(f.getNivel() == 2 && f.getExperiencia() == 0, "6 XP: nivel " + f.getNivel() + " XP " + f.getExperiencia());
            f.aumentarExperiencia(6); // de golpe
            verificar(f.getNivel() == 3 && f.getExperiencia() == 0, "nivel " + f.getNivel() + " XP " + f.getExperiencia());
            verificar(!f.estaAtacando, "no debería haber bomba todavía");
            f.aumentarExperiencia(6); // oro con la barra llena
            verificar(f.getNivel() == 3 && f.getExperiencia() == 0, "oro: nivel " + f.getNivel() + " XP " + f.getExperiencia());
            verificar(f.estaAtacando, "debería haber encolado la bomba");
        });

        prueba("existe el sprite de cada héroe en sus 3 niveles", () -> {
            for (String h : new String[]{"arquero", "mago", "caballero", "asesino", "ingeniero", "sacerdote"}) {
                for (int n = 1; n <= 3; n++) {
                    java.io.File f = new java.io.File("rec/sprites/figuras/" + h + "/" + h + "_lvl" + n + ".png");
                    verificar(f.exists(), "falta " + f);
                }
            }
        });

        for (String heroe : new String[]{"Caballero", "Ingeniero", "Mago"}) {
            prueba(heroe + " contra un bastión de 2: el bastión baja, nunca sube", () -> {
                Player p = new Player();
                IA ia = new IA();
                p.setEnemigo(ia);
                ia.setEnemigo(p);
                Figura f = heroe.equals("Caballero") ? new main.maquina_estado.juego.figuras.Caballero(p)
                        : heroe.equals("Ingeniero") ? new main.maquina_estado.juego.figuras.Ingeniero(p)
                        : new main.maquina_estado.juego.figuras.Mago(p);
                Figura otro = new main.maquina_estado.juego.figuras.Sacerdote(p);
                f.setPosicion(3);
                otro.setPosicion(4);
                p.setFiguraIzquierda(f);
                p.setFiguraDerecha(otro);
                f.getActivador().setAltura(0);
                otro.getActivador().setAltura(3);
                ia.getBastion().aumentarAltura(2);
                p.actualizarEstados();
                esperarFinDeAtaques(f);
                verificar(ia.getBastion().getAltura() == 0, "bastión de la IA: " + ia.getBastion().getAltura() + " (esperado 0, stat2=" + f.getStat2() + ")");
            });
        }

        prueba("Sacerdote de la IA ataca sin romper y da energía a su compañero", () -> {
            Player p = new Player();
            IA ia = new IA();
            p.setEnemigo(ia);
            ia.setEnemigo(p);
            Figura sacerdote = new main.maquina_estado.juego.figuras.Sacerdote(ia);
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(ia);
            sacerdote.setPosicion(1);
            arquero.setPosicion(2);
            ia.setFiguraIzquierda(sacerdote);
            ia.setFiguraDerecha(arquero);
            sacerdote.getActivador().setAltura(0);
            arquero.getActivador().setAltura(5);
            ia.actualizarEstados();
            esperarFinDeAtaques(sacerdote);
            verificar(arquero.getActivador().getAltura() == 5 - sacerdote.getStat2(),
                    "activador del compañero: " + arquero.getActivador().getAltura());
        });

        prueba("Asesino: retrasa al héroe rival al que le queda menos energía y daña a la corona sin pasar por el bastión", () -> {
            Player p = new Player();
            IA ia = new IA();
            p.setEnemigo(ia);
            ia.setEnemigo(p);
            Figura asesino = new main.maquina_estado.juego.figuras.Asesino(p);
            Figura otro = new main.maquina_estado.juego.figuras.Sacerdote(p);
            asesino.setPosicion(3);
            otro.setPosicion(4);
            p.setFiguraIzquierda(asesino);
            p.setFiguraDerecha(otro);
            asesino.getActivador().setAltura(0);
            otro.getActivador().setAltura(3);
            Figura rivalIzq = new main.maquina_estado.juego.figuras.Mago(ia);
            Figura rivalDer = new main.maquina_estado.juego.figuras.Mago(ia);
            ia.setFiguraIzquierda(rivalIzq);
            ia.setFiguraDerecha(rivalDer);
            rivalIzq.getActivador().setAltura(4);
            rivalDer.getActivador().setAltura(1); // le queda menos: es el que se retrasa
            ia.getBastion().aumentarAltura(5);
            int vidaIA = ia.getVida();
            p.actualizarEstados();
            esperarFinDeAtaques(asesino);
            verificar(rivalDer.getActivador().getAltura() == 1 + asesino.getStat1(), "derecho: " + rivalDer.getActivador().getAltura());
            verificar(rivalIzq.getActivador().getAltura() == 4, "izquierdo: " + rivalIzq.getActivador().getAltura());
            verificar(ia.getVida() == vidaIA - asesino.getStat2(), "vida IA " + vidaIA + " -> " + ia.getVida());
        });

        prueba("terminadoDeProcesarAtaques se recalcula en cada ronda", () -> {
            Player p = new Player();
            Figura a = new main.maquina_estado.juego.figuras.Arquero(p);
            Figura b = new main.maquina_estado.juego.figuras.Sacerdote(p);
            a.setPosicion(3);
            b.setPosicion(4);
            p.setFiguraIzquierda(a);
            p.setFiguraDerecha(b);
            p.actualizarEstados(); // nadie se activa
            verificar(p.terminadoDeProcesarAtaques, "ronda sin ataques: debería quedar true");
            a.getActivador().setAltura(0);
            p.actualizarEstados(); // la figura ataca
            verificar(!p.terminadoDeProcesarAtaques, "ronda con ataques: debería quedar false");
        });

        prueba("regla de energía: los símbolos suman su valor y a partir de 3 la barra baja suma-2", () -> {
            Player p = new Player();
            Figura izq = new main.maquina_estado.juego.figuras.Mago(p);   // altura 5
            Figura der = new main.maquina_estado.juego.figuras.Mago(p);
            p.setFiguraIzquierda(izq);
            p.setFiguraDerecha(der);
            // cuadrado1 + cuadrado2 + cuadrado1 = 4 -> baja 2; rombo1 solo = 1 -> no baja; martillos 1+2 = 3 -> bastión +1
            p.calcularSalidaRodillo(new String[]{"cuadrado1", "cuadrado2", "cuadrado1", "rombo1", "martillo1"});
            verificar(izq.getActivador().getAltura() == 3, "izquierdo: " + izq.getActivador().getAltura());
            verificar(der.getActivador().getAltura() == 5, "derecho: " + der.getActivador().getAltura());
            verificar(p.getBastion().getAltura() == 0, "bastión con 1 martillo: " + p.getBastion().getAltura());
            p.calcularSalidaRodillo(new String[]{"martillo2", "martillo1", "roto", "roto", "roto"});
            verificar(p.getBastion().getAltura() == 1, "bastión con martillos 2+1=3: " + p.getBastion().getAltura());
        });

        prueba("el jugador y la IA tienen rodillos distintos", () -> {








            Player player = new Player();
            IA ia = new IA();
            verificar(player.getRodillo() != null && ia.getRodillo() != null, "rodillo null");
            verificar(player.getRodillo() != ia.getRodillo(), "comparten el mismo rodillo");
        });
    }
}
