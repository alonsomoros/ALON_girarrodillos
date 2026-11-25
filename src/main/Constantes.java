package main;

import java.awt.*;
import java.util.HashMap;

public class Constantes {
    //    public static final int ANCHO_PANTALLA_COMPLETA = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width; //2560
//    public static final int ALTO_PANTALLA_COMPLETA = Toolkit.getDefaultToolkit().getScreenSize().height; //1440
    public static final int ANCHO_PANTALLA_COMPLETA = 1280; //1280
    public static final int ALTO_PANTALLA_COMPLETA = 640; //640
    public static final int ANCHO_JUEGO = ANCHO_PANTALLA_COMPLETA;
    public static final int ALTO_JUEGO = ALTO_PANTALLA_COMPLETA;
//

    public static final double FACTOR_ESCALA_X = (double) ANCHO_PANTALLA_COMPLETA / (double) ANCHO_JUEGO;
    public static final double FACTOR_ESCALA_Y = (double) ALTO_PANTALLA_COMPLETA / (double) ALTO_JUEGO;

    public static final int MITAD_VENTANA_ALTO = ALTO_PANTALLA_COMPLETA / 2;
    public static final int MITAD_VENTANA_ANCHO = ANCHO_PANTALLA_COMPLETA / 2;
    public static final String TITULO = "_.GIRARRODILLOS._";

    // Paleta

    public static final Color principal = new Color(0x94ADFBEC, true);
    public static final Color azul_1 = new Color(0xFF91F3DB, true);
    public static final Color azul_2 = new Color(0xFF44917F, true);

    // LOOP

    public static int FPS = 60;
    public static int APS = 200;

    // Rutas

    public static class RUTAS {

        public static final String CREDITOS = "rec/texto/creditos.txt";

        public static class ICONO {
            public static final String LOGO = "rec/iconos/icono.jpg";
            public static final String RATON = "rec/iconos/flecha.png";
            public static final String BOTON_START = "rec/iconos/botonStart.png";
            public static final String FLECHA_ETIQUETA = "rec/iconos/flechaEtiqueta.png";

        }

        public static class MUSICA {
            public static final String GIRARRODILLOS = "rec/musica/TheMountainTrail.wav";
            public static final String BATALLA_EPICA = "rec/musica/EncounterElite.wav";
            public static final String MENU_PRINCIPAL = "rec/musica/TitleScreen.wav";
            public static final String TIENDA = "rec/musica/TiendaTM.wav";
            public static final String CREDITOS = "rec/musica/PathOfAscension.wav";
            public static final String MENU_MUSICA = "rec/musica/TheFrozenPeak.wav";

        }


        public static class FONDOS {
            public static final String PRINCIPAL = "rec/wallpapers/";
            public static final String TABLERO = "rec/wallpapers/Tablero.png";
            public static final String TIENDA = "rec/wallpapers/TiendaTM.png";
            public static final String MUSICA = "rec/wallpapers/Musica.png";

            public static class CREDITOS {
                public static final String CASCADA = "rec/wallpapers/creditos/cascada.png";
                public static final String ACANTILADO = "rec/wallpapers/creditos/acantilado.png";
                public static final String PANTANO = "rec/wallpapers/creditos/pantano.png";
                public static final String RAMA = "rec/wallpapers/creditos/rama.png";
                public static final String RUINAS = "rec/wallpapers/creditos/ruinas.png";
                public static final String ARBOL = "rec/wallpapers/0.png";
            }
        }

        public static class ATAQUES {
            public static final String RUTA_ATAQUES = "rec/sprites/iconos/Ataque";
        }

        public static class FIGURAS {
            public static final String RUTA_FIGURAS = "rec/sprites/figuras/";

            public static final String GUERREROS_STATS = "rec/sprites/iconos/Iconos_Guerreros.png";
            public static final String SACERDOTE_STATS = "rec/sprites/iconos/Iconos_Sacerdote.png";
            public static final String ASESINO_STATS = "rec/sprites/iconos/Iconos_Asesino.png";
        }

        public static class ACTIVADORES {
            public static final String ACTIVADOR_CUADRADO_0 = "rec/sprites/activadores/ActivadorCuadrado_0.png";
            public static final String ACTIVADOR_CUADRADO_1 = "rec/sprites/activadores/ActivadorCuadrado_1.png";
            public static final String ACTIVADOR_CUADRADO_2 = "rec/sprites/activadores/ActivadorCuadrado_2.png";
            public static final String ACTIVADOR_CUADRADO_3 = "rec/sprites/activadores/ActivadorCuadrado_3.png";
            public static final String ACTIVADOR_CUADRADO_4 = "rec/sprites/activadores/ActivadorCuadrado_4.png";
            public static final String ACTIVADOR_CUADRADO_5 = "rec/sprites/activadores/ActivadorCuadrado_5.png";

            public static final String ACTIVADOR_ROMBO_0 = "rec/sprites/activadores/ActivadorRombo_0.png";
            public static final String ACTIVADOR_ROMBO_1 = "rec/sprites/activadores/ActivadorRombo_1.png";
            public static final String ACTIVADOR_ROMBO_2 = "rec/sprites/activadores/ActivadorRombo_2.png";
            public static final String ACTIVADOR_ROMBO_3 = "rec/sprites/activadores/ActivadorRombo_3.png";
            public static final String ACTIVADOR_ROMBO_4 = "rec/sprites/activadores/ActivadorRombo_4.png";
            public static final String ACTIVADOR_ROMBO_5 = "rec/sprites/activadores/ActivadorRombo_5.png";

            // Indexados por la altura del activador (0 a 5)
            public static final String[] CUADRADO = {ACTIVADOR_CUADRADO_0, ACTIVADOR_CUADRADO_1, ACTIVADOR_CUADRADO_2,
                    ACTIVADOR_CUADRADO_3, ACTIVADOR_CUADRADO_4, ACTIVADOR_CUADRADO_5};
            public static final String[] ROMBO = {ACTIVADOR_ROMBO_0, ACTIVADOR_ROMBO_1, ACTIVADOR_ROMBO_2,
                    ACTIVADOR_ROMBO_3, ACTIVADOR_ROMBO_4, ACTIVADOR_ROMBO_5};
        }

        public static class BASES {
            public static final String BASE_ROMBO_0 = "rec/sprites/baseFigura/base0.png";
            public static final String BASE_CUADRADO_0 = "rec/sprites/baseFigura/base_0.png";
            public static final String BASE_CUADRADO_1 = "rec/sprites/baseFigura/BaseFigura_1.png";
            public static final String BASE_CUADRADO_2 = "rec/sprites/baseFigura/BaseFigura_2.png";
            public static final String BASE_CUADRADO_3 = "rec/sprites/baseFigura/BaseFigura_3.png";
            public static final String BASE_CUADRADO_4 = "rec/sprites/baseFigura/BaseFigura_4.png";
            public static final String BASE_CUADRADO_5 = "rec/sprites/baseFigura/BaseFigura_5.png";
            public static final String BASE_CUADRADO_6 = "rec/sprites/baseFigura/BaseFigura_6.png";

            public static final String BASE_ROMBO_1 = "rec/sprites/baseFigura/BaseFigura1.png";
            public static final String BASE_ROMBO_2 = "rec/sprites/baseFigura/BaseFigura2.png";
            public static final String BASE_ROMBO_3 = "rec/sprites/baseFigura/BaseFigura3.png";
            public static final String BASE_ROMBO_4 = "rec/sprites/baseFigura/BaseFigura4.png";
            public static final String BASE_ROMBO_5 = "rec/sprites/baseFigura/BaseFigura5.png";
            public static final String BASE_ROMBO_6 = "rec/sprites/baseFigura/BaseFigura6.png";

            // Indexados por la experiencia de la figura (0 a 6)
            public static final String[] CUADRADO = {BASE_CUADRADO_0, BASE_CUADRADO_1, BASE_CUADRADO_2, BASE_CUADRADO_3,
                    BASE_CUADRADO_4, BASE_CUADRADO_5, BASE_CUADRADO_6};
            public static final String[] ROMBO = {BASE_ROMBO_0, BASE_ROMBO_1, BASE_ROMBO_2, BASE_ROMBO_3,
                    BASE_ROMBO_4, BASE_ROMBO_5, BASE_ROMBO_6};
        }

        public static class BASTION {
            public static final String BASTION_PLAYER_1 = "rec/sprites/bastion/bastion_1.png";
            public static final String BASTION_PLAYER_2 = "rec/sprites/bastion/bastion_2.png";
            public static final String BASTION_PLAYER_3 = "rec/sprites/bastion/bastion_3.png";
            public static final String BASTION_PLAYER_4 = "rec/sprites/bastion/bastion_4.png";
            public static final String BASTION_PLAYER_5 = "rec/sprites/bastion/bastion_5.png";


            // Indexados por la altura del bastión (0 = sin bastión, 1 a 5)
            public static final String[] PLAYER = {null, BASTION_PLAYER_1, BASTION_PLAYER_2, BASTION_PLAYER_3,
                    BASTION_PLAYER_4, BASTION_PLAYER_5};
            // Los PNG propios de la IA (bastion1..5.png) nunca existieron: de momento reutiliza los del jugador.
            // La IA no dibuja su bastión todavía (tarea 4.7 decide posición y volteo).
            public static final String[] IA = PLAYER;


        }

        public static class ELEMENTOS_RODILLO {
            public static final String CUADRADO_1 = "rec/sprites/rodillo/cuadrado1.png";
            public static final String FIJADOR = "rec/sprites/rodillo/Fijador.png";
        }
    }
    // Fuentes

    public static class FUENTES {
        //    public static final Font FUENTE_PIXEL = CargadorRecursos.cargarFuente("rec/fuentes/PixelifySans-Regular.ttf");
        public static final Font FUENTE_TITULO = new Font("Algerian", Font.PLAIN, 15);
        public static final Font FUENTE_TEXTO = new Font("AniMe Matrix - MB_EN", Font.PLAIN, 15);
//    public static final Font FUENTE_TEXTO = new Font("Tw Cen MT Condensed", Font.PLAIN, 15);
    }

    public static class COORDENADAS {
        public static final Point FIGURA_1 = new Point(248, 26); // 242, 30
        public static final Point FIGURA_2 = new Point(881, 25); // 895, 33
        public static final Point FIGURA_3 = new Point(250, 350); // 250, 350
        public static final Point FIGURA_4 = new Point(879, 351); // 895, 355

        public static final Point ACTIVADOR_1 = new Point(150, 113); // 150, 113
        public static final Point ACTIVADOR_2 = new Point(1023, 118); // 1023, 118
        public static final Point ACTIVADOR_3 = new Point(151, 288); // 151, 288
        public static final Point ACTIVADOR_4 = new Point(1022, 293); // 1022, 293

        public static final Point[] ATAQUE_1 = new Point[]{new Point(385,170), new Point(442,268), new Point(528,395)};
        public static final Point[] ATAQUE_2 = new Point[]{new Point(798,170), new Point(738,260), new Point(653,385)};
        public static final Point[] ATAQUE_3 = new Point[]{new Point(385,350), new Point(445,262), new Point(527,148)};
        public static final Point[] ATAQUE_4 = new Point[]{new Point(802,345), new Point(740,255), new Point(655,140)};


        public static final Point STATS_1 = new Point(216, 218); // 216, 218
        public static final Point STATS_2 = new Point(865, 222); // 865, 222
        public static final Point STATS_3 = new Point(219, 535); // 219, 535
        public static final Point STATS_4 = new Point(860, 531); // 860, 531

        public static final Point STATS_1_TEXTO = new Point(268, 253); // +52, +35
        public static final Point STATS_2_TEXTO = new Point(915, 257); // +50, +35
        public static final Point STATS_3_TEXTO = new Point(269, 570); // +50, +35
        public static final Point STATS_4_TEXTO = new Point(916, 566); // +56, +35

        public static final Point BASE_1 = new Point(202, 75); // 150, 110
        public static final Point BASE_2 = new Point(853, 78); // 1020, 115
        public static final Point BASE_3 = new Point(206, 399); // 200, 350
        public static final Point BASE_4 = new Point(851, 404); // 850, 355

        public static final Point BASTION_PLAYER = new Point(440, 305);
        public static final Point BASTION_IA = new Point(440, 175); // Espejo vertical del del jugador (440, 305; alto 160)

        //        public static final Point RODILLO_PLAYER = new Point(448, 525);
        public static final Point RODILLO_PLAYER = new Point(448, 490);
        //        public static final Point RODILLO_IA = new Point(448, 60);
        public static final Point RODILLO_IA = new Point(448, 25);

        // Las mismas coordenadas por posición de figura (índice = posición - 1)
        public static final Point[] FIGURA = {FIGURA_1, FIGURA_2, FIGURA_3, FIGURA_4};
        public static final Point[] ACTIVADOR = {ACTIVADOR_1, ACTIVADOR_2, ACTIVADOR_3, ACTIVADOR_4};
        public static final Point[] BASE = {BASE_1, BASE_2, BASE_3, BASE_4};
        public static final Point[] STATS = {STATS_1, STATS_2, STATS_3, STATS_4};
        public static final Point[][] ATAQUE = {ATAQUE_1, ATAQUE_2, ATAQUE_3, ATAQUE_4};

    }
    // Jugador

    public static final int VIDA_MAXIMA = 20;
    public static final int VIDA = 10;


    public static class RODILLOS {

        public enum Tipo { // Qué energía aporta un símbolo
            CUADRADO, ROMBO, MARTILLO, ROTO
        }

        // Elementos de un rodillo. El nombre es también el del sprite (rec/sprites/rodillo/<nombre>.png).
        // valor = cuántos "del mismo tipo" cuenta; daExperiencia = fondo azul (además da EXP igual a su valor).
        public enum Elementos {
            cuadrado1(Tipo.CUADRADO, 1, false), cuadrado2(Tipo.CUADRADO, 2, false), cuadrado3(Tipo.CUADRADO, 3, false),
            cuadradoNivel1(Tipo.CUADRADO, 1, true), cuadradoNivel2(Tipo.CUADRADO, 2, true), cuadradoNivel3(Tipo.CUADRADO, 3, true),
            rombo1(Tipo.ROMBO, 1, false), rombo2(Tipo.ROMBO, 2, false), rombo3(Tipo.ROMBO, 3, false),
            romboNivel1(Tipo.ROMBO, 1, true), romboNivel2(Tipo.ROMBO, 2, true), romboNivel3(Tipo.ROMBO, 3, true),
            martillo1(Tipo.MARTILLO, 1, false), martillo2(Tipo.MARTILLO, 2, false), martillo3(Tipo.MARTILLO, 3, false),
            roto(Tipo.ROTO, 0, false);

            public final Tipo tipo;
            public final int valor;
            public final boolean daExperiencia;

            Elementos(final Tipo tipo, final int valor, final boolean daExperiencia) {
                this.tipo = tipo;
                this.valor = valor;
                this.daExperiencia = daExperiencia;
            }
        }



        public static final HashMap<Elementos, Double> R1_Prob = new HashMap<>(); // Rodillo simple con roto, y solo 2 niveles, muy raro nivel
        public static final HashMap<Elementos, Double> R2_Prob = new HashMap<>(); // Rodillo simple sin roto
        public static final HashMap<Elementos, Double> R3_Prob = new HashMap<>(); // Rodillo simple
        public static final HashMap<Elementos, Double> R4_Prob = new HashMap<>(); // Rodillo simple
        public static final HashMap<Elementos, Double> R5_Prob = new HashMap<>(); // Rodillo simple

        static {
            // Aquí puedes ajustar las probabilidades a tu gusto

            // Rodillo 1
            R1_Prob.put(Elementos.romboNivel2,      0.035   );
            R1_Prob.put(Elementos.cuadradoNivel2,   0.035   );
            R1_Prob.put(Elementos.rombo2,           0.07    );
            R1_Prob.put(Elementos.cuadrado2,        0.07    );
            R1_Prob.put(Elementos.martillo2,        0.07   );
            R1_Prob.put(Elementos.cuadradoNivel1,   0.08    );
            R1_Prob.put(Elementos.romboNivel1,      0.08   );
            R1_Prob.put(Elementos.cuadrado1,        0.12    );
            R1_Prob.put(Elementos.rombo1,           0.12    );
            R1_Prob.put(Elementos.martillo1,        0.15    );
            R1_Prob.put(Elementos.roto,             0.17    );

            // Rodillo 2
            R2_Prob.put(Elementos.cuadrado1,        0.15);
            R2_Prob.put(Elementos.rombo1,           0.15);
            R2_Prob.put(Elementos.martillo1,        0.15);
            R2_Prob.put(Elementos.cuadrado2,        0.1);
            R2_Prob.put(Elementos.rombo2,           0.1);
            R2_Prob.put(Elementos.cuadradoNivel1,   0.1);
            R2_Prob.put(Elementos.romboNivel1,      0.1);
            R2_Prob.put(Elementos.martillo2,        0.1);
            R2_Prob.put(Elementos.cuadradoNivel2,   0.025);
            R2_Prob.put(Elementos.romboNivel2,      0.025);
        }
    }
}
