package main.motor;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasMotor {

    private static java.awt.image.BufferedImage[] framesDePrueba(final int n) {
        java.awt.image.BufferedImage[] f = new java.awt.image.BufferedImage[n];
        for (int i = 0; i < n; i++) {
            f[i] = new java.awt.image.BufferedImage(4, 4, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }
        return f;
    }

    // Efecto de prueba: termina tras 'vidas' actualizaciones y anota su orden de dibujo
    private static class EfectoPrueba implements Efecto {
        final String nombre;
        final java.util.List<String> registro;
        int vidas;
        int actualizaciones = 0;
        Runnable alActualizar;

        EfectoPrueba(final String nombre, final int vidas, final java.util.List<String> registro) {
            this.nombre = nombre;
            this.vidas = vidas;
            this.registro = registro;
        }

        public void actualizar() {
            actualizaciones++;
            vidas--;
            if (alActualizar != null) {
                alActualizar.run();
            }
        }

        public void dibujar(final java.awt.Graphics g) {
            registro.add(nombre);
        }

        public boolean terminado() {
            return vidas <= 0;
        }
    }

    public static void ejecutar() {
        prueba("GestorEfectos: se actualizan, se retiran al terminar y se dibujan por capas (fondo, medio, frente)", () -> {
            java.util.List<String> reg = new java.util.ArrayList<>();
            GestorEfectos g = new GestorEfectos();
            g.agregar(GestorEfectos.Capa.FRENTE, new EfectoPrueba("frente", 5, reg));
            g.agregar(GestorEfectos.Capa.FONDO, new EfectoPrueba("fondo", 1, reg));
            g.agregar(new EfectoPrueba("medio", 3, reg)); // capa MEDIO por defecto
            verificar(g.cantidad() == 3, "cantidad inicial");
            g.dibujar(null);
            verificar(reg.equals(java.util.Arrays.asList("fondo", "medio", "frente")), "orden de dibujo " + reg);
            g.actualizar();
            verificar(g.cantidad() == 2 && g.cantidad(GestorEfectos.Capa.FONDO) == 0, "el de 1 vida se retira");
            g.actualizar();
            g.actualizar();
            verificar(g.cantidad() == 1, "queda solo el de frente");
            g.limpiar();
            verificar(g.cantidad() == 0, "limpiar");
        });

        prueba("GestorEfectos: un efecto puede añadir otros al actualizarse y el límite descarta el exceso", () -> {
            java.util.List<String> reg = new java.util.ArrayList<>();
            GestorEfectos g = new GestorEfectos(3);
            EfectoPrueba padre = new EfectoPrueba("padre", 1, reg);
            EfectoPrueba hijo = new EfectoPrueba("hijo", 2, reg);
            padre.alActualizar = () -> g.agregar(hijo);
            g.agregar(padre);
            g.actualizar();
            verificar(g.cantidad() == 1 && hijo.actualizaciones == 0, "el hijo empieza en la siguiente pasada");
            g.actualizar();
            verificar(hijo.actualizaciones == 1, "el hijo se actualiza");
            verificar(g.agregar(new EfectoPrueba("a", 9, reg)) && g.agregar(new EfectoPrueba("b", 9, reg)), "caben hasta el límite");
            verificar(!g.agregar(new EfectoPrueba("c", 9, reg)), "el cuarto se descarta");
            verificar(g.cantidad() == 3, "cantidad " + g.cantidad());
        });

        prueba("EfectoTemporal: progreso de 0 a 1 según los ticks y termina a su duración", () -> {
            EfectoTemporal e = new EfectoTemporal(500) { // 30 ticks
                public void dibujar(final java.awt.Graphics g) {
                }

                double progresoPublico() {
                    return progreso();
                }
            };
            verificar(!e.terminado(), "recién creado");
            for (int i = 0; i < 15; i++) {
                Tiempo.avanzar();
            }
            verificar(!e.terminado(), "mitad");
            for (int i = 0; i < 15; i++) {
                Tiempo.avanzar();
            }
            verificar(e.terminado(), "final");
        });

        prueba("Animacion: el número de frames sale del nombre del archivo", () -> {
            verificar(Animacion.framesEnNombre("rec/sprites/efectos/impacto_5f.png") == 5, "5f");
            verificar(Animacion.framesEnNombre("a_12f.png") == 12, "12f");
            verificar(Animacion.framesEnNombre("rec/sprites/efectos/estrella.png") == 1, "sin indicar");
            verificar(Animacion.framesEnNombre("rec/mi_0f.png") == 1, "0f cuenta como 1");
        });

        prueba("Animacion.cortarTira: n frames iguales y tira estrecha = 1 frame", () -> {
            java.awt.image.BufferedImage tira = new java.awt.image.BufferedImage(50, 10, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.image.BufferedImage[] f = Animacion.cortarTira(tira, 5);
            verificar(f.length == 5 && f[0].getWidth() == 10 && f[4].getHeight() == 10, "5 frames de 10x10");
            verificar(Animacion.cortarTira(tira, 1).length == 1, "n = 1");
            verificar(Animacion.cortarTira(new java.awt.image.BufferedImage(3, 3, java.awt.image.BufferedImage.TYPE_INT_ARGB), 8).length == 1, "demasiado estrecha");
        });

        prueba("Animacion: frame según los ticks en modo UNA_VEZ, BUCLE y PING_PONG", () -> {
            // 100 ms por frame = 6 ticks
            Animacion una = new Animacion(framesDePrueba(3), 100, Animacion.Modo.UNA_VEZ);
            verificar(una.indiceFrame(0) == 0 && una.indiceFrame(5) == 0 && una.indiceFrame(6) == 1 && una.indiceFrame(12) == 2, "una vez");
            verificar(una.indiceFrame(1000) == 2, "una vez se queda en el último");
            verificar(!una.terminada(17) && una.terminada(18), "terminada a los " + una.duracionTicks() + " ticks");

            Animacion bucle = new Animacion(framesDePrueba(3), 100, Animacion.Modo.BUCLE);
            verificar(bucle.indiceFrame(18) == 0 && bucle.indiceFrame(24) == 1, "bucle");
            verificar(!bucle.terminada(100000), "el bucle no termina");

            Animacion pp = new Animacion(framesDePrueba(3), 100, Animacion.Modo.PING_PONG);
            int[] esperado = {0, 1, 2, 1, 0, 1, 2};
            for (int i = 0; i < esperado.length; i++) {
                verificar(pp.indiceFrame(i * 6) == esperado[i], "ping-pong paso " + i + " = " + pp.indiceFrame(i * 6));
            }
            verificar(new Animacion(framesDePrueba(1), 100, Animacion.Modo.PING_PONG).indiceFrame(50) == 0, "ping-pong de 1 frame");
        });

        prueba("Animacion.cargar de un archivo inexistente no rompe (placeholder)", () -> {
            Animacion a = Animacion.cargar("rec/sprites/efectos/no_existe_4f.png", 100, Animacion.Modo.BUCLE);
            verificar(a.numeroFrames() >= 1 && a.frame(0) != null, "sin frame");
        });


        prueba("Interpolador: todas las curvas empiezan en 0 y acaban en 1", () -> {
            Interpolador.Easing[] curvas = {Interpolador.LINEAL, Interpolador.ENTRADA, Interpolador.SALIDA,
                    Interpolador.ENTRADA_SALIDA, Interpolador.RETROCESO, Interpolador.REBOTE, Interpolador.ELASTICO};
            for (int i = 0; i < curvas.length; i++) {
                verificar(Math.abs(curvas[i].aplicar(0)) < 1e-9, "curva " + i + " en 0 = " + curvas[i].aplicar(0));
                verificar(Math.abs(curvas[i].aplicar(1) - 1) < 1e-9, "curva " + i + " en 1 = " + curvas[i].aplicar(1));
            }
        });

        prueba("Interpolador: valores conocidos y monotonía de las curvas suaves", () -> {
            verificar(Math.abs(Interpolador.LINEAL.aplicar(0.5) - 0.5) < 1e-9, "lineal");
            verificar(Math.abs(Interpolador.ENTRADA_SALIDA.aplicar(0.5) - 0.5) < 1e-9, "entrada-salida en el centro");
            verificar(Interpolador.ENTRADA.aplicar(0.5) < 0.5, "entrada arranca lenta");
            verificar(Interpolador.SALIDA.aplicar(0.5) > 0.5, "salida frena");
            Interpolador.Easing[] suaves = {Interpolador.LINEAL, Interpolador.ENTRADA, Interpolador.SALIDA, Interpolador.ENTRADA_SALIDA};
            for (Interpolador.Easing e : suaves) {
                double anterior = -1;
                for (int i = 0; i <= 100; i++) {
                    double v = e.aplicar(i / 100.0);
                    verificar(v >= anterior - 1e-12, "no monótona");
                    anterior = v;
                }
            }
        });

        prueba("Interpolador: el retroceso y el elástico se pasan del destino; el rebote nunca baja de 0", () -> {
            boolean pasaRetroceso = false, pasaElastico = false;
            for (int i = 0; i <= 100; i++) {
                double t = i / 100.0;
                pasaRetroceso |= Interpolador.RETROCESO.aplicar(t) > 1.0001;
                pasaElastico |= Interpolador.ELASTICO.aplicar(t) > 1.0001;
                verificar(Interpolador.REBOTE.aplicar(t) >= 0 && Interpolador.REBOTE.aplicar(t) <= 1.0001, "rebote fuera de [0,1] en " + t);
            }
            verificar(pasaRetroceso && pasaElastico, "deberían pasarse del destino");
        });

        prueba("Interpolador.interpolar: mezcla entre dos valores y limita t fuera de [0,1]", () -> {
            verificar(Interpolador.interpolar(10.0, 20.0, 0.5, Interpolador.LINEAL) == 15.0, "mitad");
            verificar(Interpolador.interpolar(10, 20, -3, Interpolador.LINEAL) == 10, "t < 0");
            verificar(Interpolador.interpolar(10, 20, 9, Interpolador.LINEAL) == 20, "t > 1");
            verificar(Interpolador.interpolar(100, 0, 1, Interpolador.SALIDA) == 0, "hacia abajo");
        });


        prueba("Tiempo: conversión ms <-> ticks y avance del reloj", () -> {
            verificar(Tiempo.msATicks(1000) == 60, "1000 ms = " + Tiempo.msATicks(1000));
            verificar(Tiempo.msATicks(500) == 30, "500 ms");
            verificar(Tiempo.msATicks(0) == 0 && Tiempo.msATicks(-5) == 0, "cero o negativo");
            verificar(Tiempo.msATicks(1) == 1, "una duración positiva dura al menos 1 tick");
            verificar(Tiempo.ticksAMs(30) == 500 && Tiempo.ticksAMs(60) == 1000, "ticks a ms");
            long antes = Tiempo.ahora();
            Tiempo.avanzar();
            Tiempo.avanzar();
            verificar(Tiempo.ahora() == antes + 2, "avanzar");
        });
    }
}
