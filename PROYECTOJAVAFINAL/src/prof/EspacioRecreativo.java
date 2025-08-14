/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prof;

import javax.swing.JOptionPane;

/**
 *
 * @author emmac
 */
public class EspacioRecreativo {

    static Recreativo[] espacios = new Recreativo[7]; //arreglo de 7 espacios recreativos
    static boolean precargado = false; //para saber si ya se precargo

    public EspacioRecreativo() { //constructor vacio
    }

    static void precargar() { //carga los espacios recreativos por defecto
        espacios[0] = new Recreativo("Ping-pong", "Mesa 1", 2); //mesa de ping pong, 2 cupos
        espacios[1] = new Recreativo("Billar", "Mesa 2", 2); //mesa de billar, 2 cupos
        espacios[2] = new Recreativo("Futbol", "Cancha 1", 12); //cancha de futbol, 12 cupos
        espacios[3] = new Recreativo("Futbol", "Cancha 2", 12); //otra cancha de futbol, 12 cupos
        espacios[4] = new Recreativo("Baloncesto", "Cancha Principal", 10); //cancha de baloncesto, 10 cupos
        espacios[5] = new Recreativo("Tenis", "Cancha 1", 2); //cancha de tenis, 2 cupos
        espacios[6] = new Recreativo("Tenis", "Cancha 2", 2); //segunda cancha de tenis, 2 cupos
    }

    static class Recreativo { //clase para representar cada espacio

        String tipo; //tipo de actividad
        String nombre; //nombre del espacio
        int capacidad; //capacidad total del espacio
        int disponibles; //cupo disponible

        public Recreativo(String tipo, String nombre, int capacidad) {
            this.tipo = tipo;
            this.nombre = nombre;
            this.capacidad = capacidad;
            this.disponibles = capacidad;
        }

        public boolean reservar(int cantidad) { //reserva x cantidad de cupos
            if (disponibles >= cantidad) { //si hay espacio suficiente
                disponibles -= cantidad; //resta los cupos
                return true;
            }
            return false; //no se puede reservar
        }

        public void liberar(int cantidad) { //libera cierta cantidad de cupos
            disponibles = Math.min(disponibles + cantidad, capacidad); //no se pasa de la capacidad total
        }

        public String resumen() { //devuelve un resumen en texto
            return tipo + " - " + nombre + " | Cupos: " + disponibles;
        }

        public int reservados() { //retorna cuantos cupos ya se usaron
            return capacidad - disponibles;
        }
    }

    public void menuEspacioRecreativo() { //menu principal para espacios recreativos
        if (!precargado) { //si no se ha precargado
            precargar(); //carga los espacios
            precargado = true; //marca como ya precargado
        }

        String op;
        do {
            op = JOptionPane.showInputDialog("Espacios Recreativos\n1) Ver\n2) Reservar\n3) Volver"); //menu
            if (op == null || op.equals("3")) { //salir
                break;
            }

            if (op.equals("1")) { //ver espacios
                mostrarEspacios();
            } else if (op.equals("2")) { //reservar espacio
                reservar();
            } else {
                JOptionPane.showMessageDialog(null, "Opcion invalida"); //mensaje si digita algo incorrecto
            }
        } while (true); //bucle del menu
    }

    public void mostrarEspacios() { //muestra todos los espacios y sus cupos
        String msg = "Espacios disponibles:\n";
        for (int i = 0; i < espacios.length; i++) {
            msg += i + ". " + espacios[i].resumen() + "\n"; //agrega cada espacio con su numero
        }
        JOptionPane.showMessageDialog(null, msg); //muestra el mensaje
    }

    public void mostrarReservas() { //muestra las reservas
        String mensaje = "Espacios con reservas\n\n";
        boolean hay = false; //para saber si hay alguno reservado

        if (espacios == null || espacios.length == 0) { //si no hay arreglo
            JOptionPane.showMessageDialog(null, "No hay espacios registrados");
            return; // salir del metodo
        }

        for (int i = 0; i < espacios.length; i++) {
            if (espacios[i] != null) { //comprueba que el objeto existe
                int res = espacios[i].reservados(); //cantidad reservada
                if (res > 0) { // si hay reservas
                    mensaje += espacios[i].tipo + " " + espacios[i].nombre + " reservados " + res + "\n";
                    hay = true;
                }
            }
        }

        if (hay) {
            JOptionPane.showMessageDialog(null, mensaje); //si hay reservas muestra este mensaje
        } else {
            JOptionPane.showMessageDialog(null, "No hay reservas realizadas"); //si no no
        }
    }

    public void reservar() { //permite reservar un espacio
        mostrarEspacios();
        try {
            String id = JOptionPane.showInputDialog("ID de socio:");
            int esp = Integer.parseInt(JOptionPane.showInputDialog("Espacio a reservar (numero):"));
            int cant = Integer.parseInt(JOptionPane.showInputDialog("¿Cuantas personas usaran el espacio?"));

            if (esp >= 0 && esp < espacios.length) { //valida el numero del espacio
                if (espacios[esp].reservar(cant)) { //intenta reservar
                    JOptionPane.showMessageDialog(null, "Reserva hecha para socio " + id);
                } else {
                    JOptionPane.showMessageDialog(null, "No hay cupos suficientes");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Numero de espacio invalido");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida"); //si pone algo que no es numero
        }
    }

    public void liberarReserva() { //libera cupos en un espacio
        mostrarEspacios();
        try {
            int esp = Integer.parseInt(JOptionPane.showInputDialog("Espacio a liberar (numero):"));
            int cant = Integer.parseInt(JOptionPane.showInputDialog("¿Cuantos cupos desea liberar?"));

            if (esp >= 0 && esp < espacios.length) { //valida el numero
                espacios[esp].liberar(cant); //libera los cupos
                JOptionPane.showMessageDialog(null, "Reserva liberada");
            } else {
                JOptionPane.showMessageDialog(null, "Numero invalido");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida"); //si no digita numeros validos
        }
    }
}
