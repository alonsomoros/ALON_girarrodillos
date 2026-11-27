package main.maquina_estado.juego;

import main.Constantes;
import main.Constantes.RODILLOS.Elementos;

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

    // Coloca dos figuras en un equipo (posiciones 1-2 para la IA, 3-4 para el jugador) con la altura de activador dada
    private static void equipo(final EsqueletoPlayer e, final boolean arriba, final Figura izq, final int alturaIzq, final Figura der, final int alturaDer) {
        izq.setPosicion(arriba ? 1 : 3);
        der.setPosicion(arriba ? 2 : 4);
        izq.getActivador().setAltura(alturaIzq);
        der.getActivador().setAltura(alturaDer);
        e.setFiguraIzquierda(izq);
        e.setFiguraDerecha(der);
    }

    // Juega una ronda completa con ColaAcciones y devuelve el orden (nombres) en que actuaron los héroes
    private static java.util.List<String> ordenDeRonda(final Player p, final IA ia) throws Exception {
        ColaAcciones cola = new ColaAcciones(p, ia);
        cola.iniciarRonda();
        java.util.List<String> orden = new java.util.ArrayList<>();
        Figura[] todas = {p.getFiguraIzquierda(), p.getFiguraDerecha(), ia.getFiguraIzquierda(), ia.getFiguraDerecha()};
        java.util.Set<Figura> vistas = new java.util.HashSet<>();
        long limite = System.currentTimeMillis() + 5000;
        while ((cola.estaActiva() || p.figurasAtacando() || ia.figurasAtacando()) && System.currentTimeMillis() < limite) {
            cola.avanzar();
            for (Figura f : todas) {
                if (f.estaAtacando && vistas.add(f)) {
                    orden.add(f.getClass().getSimpleName());
                }
            }
            p.actualizar();
            ia.actualizar();
            for (Figura f : todas) { // Una figura puede actuar dos veces en la ronda (ayuda del Sacerdote)
                if (!f.estaAtacando) {
                    vistas.remove(f);
                }
            }
            Thread.sleep(2);
        }
        verificar(!cola.estaActiva(), "la ronda no termina");
        return orden;
    }

    private static Player jugadorContraIA() {
        Player p = new Player();
        IA ia = new IA();
        p.setEnemigo(ia);
        ia.setEnemigo(p);
        return p;
    }

    // Mapa de probabilidades con un solo símbolo (el canal siempre da ese)
    private static java.util.HashMap<Elementos, Double> un(final Elementos e) {
        java.util.HashMap<Elementos, Double> m = new java.util.HashMap<>();
        m.put(e, 1.0);
        return m;
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
            p.calcularSalidaRodillo(new Constantes.RODILLOS.Elementos[]{Elementos.cuadrado1, Elementos.cuadrado2, Elementos.cuadrado1, Elementos.rombo1, Elementos.martillo1});
            verificar(izq.getActivador().getAltura() == 3, "izquierdo: " + izq.getActivador().getAltura());
            verificar(der.getActivador().getAltura() == 5, "derecho: " + der.getActivador().getAltura());
            verificar(p.getBastion().getAltura() == 0, "bastión con 1 martillo: " + p.getBastion().getAltura());
            p.calcularSalidaRodillo(new Constantes.RODILLOS.Elementos[]{Elementos.martillo2, Elementos.martillo1, Elementos.roto, Elementos.roto, Elementos.roto});
            verificar(p.getBastion().getAltura() == 1, "bastión con martillos 2+1=3: " + p.getBastion().getAltura());
        });

        prueba("ColaAcciones: los héroes de ambos equipos actúan de uno en uno por prioridad (Ingeniero, Mago, Guerrero, Arquero)", () -> {
            Player p = jugadorContraIA();
            IA ia = (IA) p.getEnemigo();
            equipo(p, false, new main.maquina_estado.juego.figuras.Arquero(p), 0, new main.maquina_estado.juego.figuras.Caballero(p), 0);
            equipo(ia, true, new main.maquina_estado.juego.figuras.Mago(ia), 0, new main.maquina_estado.juego.figuras.Ingeniero(ia), 0);
            java.util.List<String> orden = ordenDeRonda(p, ia);
            verificar(orden.equals(java.util.List.of("Ingeniero", "Mago", "Caballero", "Arquero")), "orden: " + orden);
        });

        prueba("ColaAcciones: el Asesino retrasa antes de su turno al rival más cercano, que pierde la acción", () -> {
            Player p = jugadorContraIA();
            IA ia = (IA) p.getEnemigo();
            equipo(p, false, new main.maquina_estado.juego.figuras.Asesino(p), 0, new main.maquina_estado.juego.figuras.Arquero(p), 4);
            equipo(ia, true, new main.maquina_estado.juego.figuras.Caballero(ia), 0, new main.maquina_estado.juego.figuras.Mago(ia), 5);
            java.util.List<String> orden = ordenDeRonda(p, ia);
            verificar(orden.equals(java.util.List.of("Asesino")), "orden: " + orden);
            verificar(ia.getFiguraIzquierda().getActivador().getAltura() == 1, "el Caballero debía quedar retrasado a 1");
        });

        prueba("ColaAcciones: dos Asesinos activados a la vez no se atacan entre sí", () -> {
            Player p = jugadorContraIA();
            IA ia = (IA) p.getEnemigo();
            equipo(p, false, new main.maquina_estado.juego.figuras.Asesino(p), 0, new main.maquina_estado.juego.figuras.Arquero(p), 4);
            equipo(ia, true, new main.maquina_estado.juego.figuras.Asesino(ia), 0, new main.maquina_estado.juego.figuras.Mago(ia), 5);
            java.util.List<String> orden = ordenDeRonda(p, ia);
            verificar(orden.equals(java.util.List.of("Asesino", "Asesino")), "ambos asesinos debían actuar: " + orden);
        });

        prueba("Sacerdote: si el compañero también actúa esta ronda, la energía llega después de que actúe", () -> {
            Player p = jugadorContraIA();
            IA ia = (IA) p.getEnemigo();
            equipo(p, false, new main.maquina_estado.juego.figuras.Sacerdote(p), 0, new main.maquina_estado.juego.figuras.Caballero(p), 0);
            equipo(ia, true, new main.maquina_estado.juego.figuras.Arquero(ia), 3, new main.maquina_estado.juego.figuras.Mago(ia), 5);
            p.setVida(5);
            java.util.List<String> orden = ordenDeRonda(p, ia);
            verificar(orden.equals(java.util.List.of("Sacerdote", "Caballero")), "orden: " + orden);
            verificar(p.getFiguraDerecha().getActivador().getAltura() == 1, "tras actuar queda a 3 - 2 de energía: " + p.getFiguraDerecha().getActivador().getAltura());
            verificar(p.getVida() == 6, "el Sacerdote cura 1 en nivel 1: " + p.getVida());
        });

        prueba("Sacerdote: la energía que deja listo al compañero hace que actúe después de todos los demás", () -> {
            Player p = jugadorContraIA();
            IA ia = (IA) p.getEnemigo();
            equipo(p, false, new main.maquina_estado.juego.figuras.Sacerdote(p), 0, new main.maquina_estado.juego.figuras.Caballero(p), 2);
            equipo(ia, true, new main.maquina_estado.juego.figuras.Arquero(ia), 0, new main.maquina_estado.juego.figuras.Mago(ia), 5);
            java.util.List<String> orden = ordenDeRonda(p, ia);
            verificar(orden.equals(java.util.List.of("Sacerdote", "Arquero", "Caballero")), "orden: " + orden);
        });

        prueba("Vida máxima 12: el Sacerdote no cura por encima y la animación no cambia la regla", () -> {
            Player p = jugadorContraIA();
            p.setVida(11);
            p.recibirCuracion(5);
            verificar(p.getVida() == 12, "tope de vida: " + p.getVida());
        });

        prueba("Activador: la barra baja de uno en uno (sin saltar) y un cambio nuevo parte de lo que se veía", () -> {
            Activador a = new Activador(5);
            verificar(a.alturaMostrada() == 5, "inicio");
            a.disminuirAltura(3);
            verificar(a.getAltura() == 2, "la lógica cambia al instante: " + a.getAltura());
            verificar(a.alturaMostrada() == 5, "al principio se sigue viendo la anterior: " + a.alturaMostrada());
            for (int i = 0; i < 5; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(a.alturaMostrada() == 4, "tras 5 ticks un paso: " + a.alturaMostrada());
            a.disminuirAltura(1); // Cambio a mitad de animación: parte de lo visible (4)
            verificar(a.getAltura() == 1 && a.alturaMostrada() == 4, "nuevo cambio: " + a.alturaMostrada());
            for (int i = 0; i < 30; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(a.alturaMostrada() == 1, "al final se ve la real: " + a.alturaMostrada());
        });

        prueba("Barra de EXP: sube de una en una y, al llenarse, se queda llena un momento antes de subir de nivel", () -> {
            Player p = new Player();
            Figura f = new main.maquina_estado.juego.figuras.Arquero(p);
            f.aumentarExperiencia(2);
            verificar(f.getExperiencia() == 2 && f.expMostrada() == 0, "la lógica cambia al instante, el dibujo no: " + f.expMostrada());
            for (int i = 0; i < 9; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(f.expMostrada() == 2, "tras 8 ticks muestra 2: " + f.expMostrada());
            f.aumentarExperiencia(4); // llena: sube de nivel
            verificar(f.getNivel() == 2 && f.getExperiencia() == 0, "nivel " + f.getNivel() + " xp " + f.getExperiencia());
            verificar(f.expMostrada() == 2, "al empezar se sigue viendo 2: " + f.expMostrada());
            for (int i = 0; i < 17; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(f.expMostrada() == 6, "llena y brillando: " + f.expMostrada());
            for (int i = 0; i < 30; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(f.expMostrada() == 0, "después vuelve a la real: " + f.expMostrada());
        });

        prueba("Pantalla final: las monedas suben de una en una tras el título, hasta el total", () -> {
            verificar(PantallaFinal.monedasMostradas(10, 3) == 0, "antes de empezar");
            verificar(PantallaFinal.monedasMostradas(70, 3) == 1, "primera moneda");
            verificar(PantallaFinal.monedasMostradas(80, 3) == 2, "segunda moneda");
            verificar(PantallaFinal.monedasMostradas(500, 3) == 3, "tope en el total");
            verificar(PantallaFinal.monedasMostradas(500, 0) == 0, "sin monedas no sube nada");
        });

        prueba("Rodillo: el resultado se decide al girar, el giro dura ~1 s, los canales fijados no giran", () -> {
            Player p = new Player();
            Rodillo r = p.getRodillo();
            verificar(!r.estaGirando(), "recién creado no debe girar");
            r.setFijado(0, true);
            r.girarRodillo();
            verificar(r.estaGirando(), "debe estar girando tras girarRodillo()");
            Elementos[] salida = r.getSalidaRodillo().clone();
            for (int i = 0; i < 70; i++) {
                main.motor.Tiempo.avanzar();
            }
            verificar(!r.estaGirando(), "debe parar en 70 actualizaciones");
            verificar(java.util.Arrays.equals(salida, r.getSalidaRodillo()), "el resultado cambió durante la animación");
            Rodillo.animar = false;
            r.girarRodillo();
            Rodillo.animar = true;
            verificar(!r.estaGirando(), "con animar=false no debe girar");
        });

        prueba("nuevaPartida() crea jugador e IA nuevos (vida, figuras y rodillos reiniciados)", () -> {
            GestorGirarrodillos g = new GestorGirarrodillos();
            Player p1 = g.player;
            IA ia1 = g.ia;
            p1.setVida(1);
            ia1.setVida(0);
            g.nuevaPartida();
            verificar(g.player != p1 && g.ia != ia1, "no se recrearon");
            verificar(g.player.getVida() == main.Constantes.VIDA && g.ia.getVida() == main.Constantes.VIDA, "vida no reiniciada");
            verificar(g.player.getEnemigo() == g.ia && g.ia.getEnemigo() == g.player, "enemigos mal enlazados");
        });

        prueba("fin de partida: victoria, derrota, empate o sigue jugando según las coronas", () -> {
            GestorGirarrodillos g = new GestorGirarrodillos();
            verificar(g.evaluarResultado() == ResultadoPartida.JUGANDO, "inicio");
            g.ia.setVida(0);
            verificar(g.evaluarResultado() == ResultadoPartida.VICTORIA, "victoria");
            g.player.setVida(0);
            verificar(g.evaluarResultado() == ResultadoPartida.EMPATE, "empate");
            g.ia.setVida(5);
            verificar(g.evaluarResultado() == ResultadoPartida.DERROTA, "derrota");
        });

        prueba("la IA elige 2 héroes distintos al azar en las posiciones 1 y 2", () -> {
            java.util.Set<String> combinaciones = new java.util.HashSet<>();
            for (int i = 0; i < 100; i++) {
                IA ia = new IA();
                Figura izq = ia.getFiguraIzquierda(), der = ia.getFiguraDerecha();
                verificar(izq != null && der != null && izq != der, "héroes repetidos o nulos");
                verificar(izq.getClass() != der.getClass(), "mismo tipo de héroe");
                verificar(izq.getPosicion() == 1 && der.getPosicion() == 2, "posiciones " + izq.getPosicion() + "," + der.getPosicion());
                combinaciones.add(izq.getClass().getSimpleName() + "+" + der.getClass().getSimpleName());
            }
            verificar(combinaciones.size() >= 5, "poca variedad: " + combinaciones.size());
        });

        prueba("la IA fija solo los símbolos valiosos (valor 2+, o con EXP) y no los rotos ni los de valor 1", () -> {
            IA ia = new IA();
            ia.establecerRodillo(un(Elementos.cuadrado3), un(Elementos.cuadrado1), un(Elementos.romboNivel1),
                    un(Elementos.roto), un(Elementos.martillo2));
            ia.decidirFijados();
            boolean[] esperado = {true, false, true, false, true};
            for (int i = 0; i < 5; i++) {
                verificar(ia.getRodillo().estaFijado(i) == esperado[i], "canal " + i + " fijado=" + ia.getRodillo().estaFijado(i));
            }
        });

        prueba("al terminar la partida se dan las monedas (3 victoria, 1 empate, 1 derrota) una sola vez", () -> {
            main.maquina_estado.tienda.Progreso.usarSoloEnMemoria();
            GestorGirarrodillos g = new GestorGirarrodillos();
            g.terminarPartida(ResultadoPartida.VICTORIA);
            verificar(main.maquina_estado.tienda.Progreso.getInstance().getMonedas() == 3 && g.getMonedasGanadas() == 3, "victoria");
            g.nuevaPartida();
            verificar(g.getMonedasGanadas() == 0 && g.getResultado() == ResultadoPartida.JUGANDO, "nueva partida limpia");
            g.terminarPartida(ResultadoPartida.DERROTA);
            g.nuevaPartida();
            g.terminarPartida(ResultadoPartida.EMPATE);
            verificar(main.maquina_estado.tienda.Progreso.getInstance().getMonedas() == 3 + 1 + 1, "total " + main.maquina_estado.tienda.Progreso.getInstance().getMonedas());
            main.maquina_estado.tienda.Progreso.usarSoloEnMemoria();
        });

        prueba("el jugador y la IA tienen rodillos distintos", () -> {












            Player player = new Player();
            IA ia = new IA();
            verificar(player.getRodillo() != null && ia.getRodillo() != null, "rodillo null");
            verificar(player.getRodillo() != ia.getRodillo(), "comparten el mismo rodillo");
        });
    }
}
