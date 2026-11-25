package main.maquina_estado.juego;

import main.Constantes;


public class GestorAtaques {

    public static final int DANIO_BOMBA = 2;

    public static void procesarFinalAtaque(Figura figuraAtacante, Ataque ataque, EsqueletoPlayer enemigo) { // Cuando actuan suben 2 de exp

        if (ataque.esEspecial()) { // Bomba: daño directo, ignora el bastión
            enemigo.recibirAtaque(DANIO_BOMBA);
            return;
        }


        switch (figuraAtacante.getClass().getSimpleName()) {

            case "Arquero": // Ataca a 3 de altura
                if (enemigo.tieneBation() && enemigo.getBastion().getAltura() < 3) { // Altura insuficiente
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                } else if (enemigo.tieneBation() && enemigo.getBastion().getAltura() >= 3) { // Altura suficiente
                    enemigo.getBastion().disminuirAltura(3);
                } else if (!enemigo.tieneBation()) { // No tiene bastion
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                }
                break;
            case "Asesino": // Ignora bastion
                enemigo.recibirAtaque(figuraAtacante.getStat2());
                enemigo.getFiguraEnfrente(figuraAtacante).getActivador().aumentarAltura(figuraAtacante.getStat1());
                break;
            case "Caballero": // Normal
                if (enemigo.tieneBation()) { // Si el enemigo tiene bastion y es mayor que el ataque
                    enemigo.getBastion().disminuirAltura(enemigo.getBastion().getAltura() - ataque.figuraAtacante.getStat2());
                } else if (!enemigo.tieneBation()) {
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                }
                break;
            case "Ingeniero":
                if (enemigo.tieneBation()) { // Si el enemigo tiene bastion y es mayor que el ataque
                    enemigo.getBastion().disminuirAltura(enemigo.getBastion().getAltura() - ataque.figuraAtacante.getStat2());
                } else if (!enemigo.tieneBation()) {
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                }
                figuraAtacante.getDuenio().getBastion().aumentarAltura(2);
                break;
            case "Mago": // Ataca x2
                if (ataque.ignoraBastion()) { // 2.º ataque: vuela a altura máxima
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                } else if (enemigo.tieneBation()) { // 1.º ataque: a nivel de suelo, lo frena el bastión
                    enemigo.getBastion().disminuirAltura(enemigo.getBastion().getAltura() - ataque.figuraAtacante.getStat2());
                } else {
                    enemigo.recibirAtaque(figuraAtacante.getStat1());
                }

                break;
            case "Sacerdote": // Cura
                if (!(figuraAtacante.getDuenio().getVida() == Constantes.VIDA_MAXIMA)) {
                    figuraAtacante.getDuenio().recibirCuracion(figuraAtacante.getStat1());
                }

                if (figuraAtacante.getCompaniero().getActivador().getAltura() > 0) {
                    figuraAtacante.getCompaniero().getActivador().disminuirAltura(figuraAtacante.getStat2());
                }
                break;
        }

    }
}
