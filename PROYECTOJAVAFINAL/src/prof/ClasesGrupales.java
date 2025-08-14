/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prof;

import javax.swing.JOptionPane;

/**
 *
 * @author sanlo
 */
public class ClasesGrupales {

    private static ClaseGrupal[] clases = new ClaseGrupal[6]; //arreglo que guarda las clases
    private static boolean precargado = false; //para no cargar varias veces

    static class ClaseGrupal {

        String nombre;
        String horario;
        int capacidadMaxima;
        int cuposDisponibles;

        public ClaseGrupal(String nombre, String horario, int capacidadMaxima) {
            this.nombre = nombre;
            this.horario = horario;
            this.capacidadMaxima = capacidadMaxima; //capacidad total
            this.cuposDisponibles = capacidadMaxima; //cupos que quedan
        }

        public boolean reservar() { //quita un cupo si hay espacio
            if (cuposDisponibles > 0) {
                cuposDisponibles--;
                return true;
            }
            return false;
        }

        public void modificar(String nuevoNombre, String nuevoHorario, int nuevaCapacidad) {
            int diferencia = nuevaCapacidad - this.capacidadMaxima; //calcula cambio de cupos
            this.nombre = nuevoNombre;
            this.horario = nuevoHorario;
            this.capacidadMaxima = nuevaCapacidad;
            this.cuposDisponibles += diferencia; //ajusta cupos
        }

        public String resumen() { //muestra resumen de la clase
            return nombre + " (" + horario + ") - Cupos disponibles: " + cuposDisponibles;
        }
    }

    public void menuClases(ControlSocios controlSocios) { //menu principal de clases
        if (!precargado) {
            precargarClases(); //carga las clases iniciales
            precargado = true;
        }
        while (true) {
            String opcion = JOptionPane.showInputDialog(
                    "Clases Grupales - Menú\n"
                    + "1) Ver clases disponibles\n"
                    + "2) Reservar clase\n"
                    + "3) Modificar clase\n"
                    + "4) Volver al menú principal"
            );
            if (opcion == null || opcion.equals("4")) {
                break;
            }

            switch (opcion) {
                case "1":
                    mostrarClases(); //muestra lista
                    break;
                case "2":
                    reservarClase(controlSocios); //reserva una clase
                    break;
                case "3":
                    modificarClase(); //modifica datos de una clase
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción invalida");
            }
        }
    }

    private void precargarClases() { //carga clases fijas
        clases[0] = new ClaseGrupal("Yoga", "mañana", 10);
        clases[1] = new ClaseGrupal("Crossfit", "mañana", 10);
        clases[2] = new ClaseGrupal("Funcional", "mañana", 10);
        clases[3] = new ClaseGrupal("Pilates", "noche", 10);
        clases[4] = new ClaseGrupal("Zumba", "noche", 10);
        clases[5] = new ClaseGrupal("Cardio Dance", "noche", 10);
    }

    public void mostrarClases() { // muestra todas las clases con sus datos
        StringBuilder mensaje = new StringBuilder("Clases Disponibles:\n");

        if (clases == null || clases.length == 0) { // valida que haya arreglo
            mensaje.append("No hay clases registradas\n");
        } else {
            for (int i = 0; i < clases.length; i++) {
                if (clases[i] != null) { //solo ejecuta si no esta vacia
                    mensaje.append(i).append(". ").append(clases[i].resumen()).append("\n");
                } else {
                    mensaje.append(i).append(". No hay datos\n"); //Esto lo muestra si se entra antes de haber precargado las clases
                }
            }
        }

        JOptionPane.showMessageDialog(null, mensaje.toString());
    }

    private String pedirIdValido(ControlSocios controlSocios) { //pide id y valida que exista
        while (true) {
            String idIngresado = JOptionPane.showInputDialog("Validación de socio\n\nIngrese su ID (ej: Socio#):"
            );
            if (idIngresado == null) {
                return null;
            }
            idIngresado = idIngresado.trim();
            if (idIngresado.isEmpty()) { //si esta vacio
                JOptionPane.showMessageDialog(null, "El ID no puede estar vacío");
                continue;
            }
            if (controlSocios.existeId(idIngresado)) {
                return idIngresado; //id valido
            } else {
                JOptionPane.showMessageDialog(null, "ID no encontrado, intente de nuevo");
            }
        }
    }

    private void reservarClase(ControlSocios controlSocios) { //reserva un espacio en la clase
        String idValido = pedirIdValido(controlSocios);//pide el id
        if (idValido == null) {//si cancela
            return;
        }

        mostrarClases();//muestra la lista de clases
        String input = JOptionPane.showInputDialog("Ingrese el número de la clase a reservar:");
        if (input == null) {//si cancela
            return;
        }

        int opcion;
        try {
            opcion = Integer.parseInt(input);//convierte a numero
        } catch (NumberFormatException e) {//si no es numero se sale
            JOptionPane.showMessageDialog(null, "Entrada invalida");
            return;
        }

        if (opcion >= 0 && opcion < clases.length) {//intenta reservar si esta en rando
            if (clases[opcion].reservar()) {
                JOptionPane.showMessageDialog(null, "Reserva exitosa\nID Socio: " + idValido + "\nClase: " + clases[opcion].nombre + " (" + clases[opcion].horario + ")");
            } else {
                JOptionPane.showMessageDialog(null, "No hay cupos disponibles");
            }
        } else {//fuera de rango
            JOptionPane.showMessageDialog(null, "Número de clase invalido");
        }
    }

    private void modificarClase() { //permite cambiar datos de la clase
        mostrarClases();
        String input = JOptionPane.showInputDialog("Ingrese el número de la clase a modificar:");
        if (input == null) {//si cancela
            return;
        }

        int opcion;
        try {
            opcion = Integer.parseInt(input); //parsea el input
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida");
            return;
        }

        if (opcion >= 0 && opcion < clases.length) { //si la clase se puede editar
            String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
            String nuevoHorario = JOptionPane.showInputDialog("Nuevo horario (mañana/noche):");
            String capacidadStr = JOptionPane.showInputDialog("Nueva capacidad máxima:");
            if (nuevoNombre == null || nuevoHorario == null || capacidadStr == null) {
                return;
            }

            int nuevaCapacidad;
            try {
                nuevaCapacidad = Integer.parseInt(capacidadStr); //pone la capacidad en int
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Capacidad invalida");
                return;
            }

            clases[opcion].modificar(nuevoNombre, nuevoHorario, nuevaCapacidad);
            JOptionPane.showMessageDialog(null, "Clase modificada exitosamente");
        } else {
            JOptionPane.showMessageDialog(null, "Número de clase invalido");
        }
    }

    public void liberarReserva() { //libera un espacio reservado
        mostrarClases();
        String input = JOptionPane.showInputDialog("Ingrese el número de la clase para liberar reserva:");
        if (input == null) {
            return;//si se sale
        }

        int opcion;
        try {
            opcion = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida");
            return;
        }

        if (opcion >= 0 && opcion < clases.length) {
            if (clases[opcion].cuposDisponibles < clases[opcion].capacidadMaxima) { //revisa si hay reservas
                clases[opcion].cuposDisponibles++; //libera un espacio sumando un cupo
                JOptionPane.showMessageDialog(null, "Reserva liberada");
            } else {
                JOptionPane.showMessageDialog(null, "No hay reservas para liberar en esa clase");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Número de clase invalido");
        }
    }
}
