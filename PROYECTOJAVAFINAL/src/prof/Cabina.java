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
public class Cabina {

    private String[] horas = {"9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm"}; //horas que se pueden reservar
    private String[] reservas = new String[horas.length]; //id de la persona que reservo en cada hora

    private ControlSocios controlSocios; //para verificar datos de socios

    public Cabina() {
        for (int i = 0; i < reservas.length; i++) {
            reservas[i] = ""; //deja todas las horas libres al inicio
        }
    }

    public void menuCabina(ControlSocios controlSocios) { //muestra las opciones para usar la cabina
        this.controlSocios = controlSocios;

        while (true) {
            String opcion = JOptionPane.showInputDialog(
                    "Cabina - Menú\n"
                    + "1) Ver horarios\n"
                    + "2) Reservar horario\n"
                    + "3) Liberar reserva\n"
                    + "4) Resumen de reservas\n"
                    + "5) Volver al menú principal"
            );
            if (opcion == null || opcion.equals("5")) {
                break; //sale del menu
            }

            switch (opcion) {
                case "1":
                    mostrarHorarios(); //enseña las horas y si estan libres o no
                    break;
                case "2":
                    reservarHorario(); //permite reservar
                    break;
                case "3":
                    liberarReserva(); //permite liberar una hora ocupada
                    break;
                case "4":
                    mostrarResumenReservas(); //muestra solo las reservas hechas
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida");
            }
        }
    }

    private void mostrarHorarios() { //revisa cada hora y dice si esta libre o reservada
        StringBuilder sb = new StringBuilder("Horarios de Cabina:\n");
        int libres = 0; //contador de libres

        for (int i = 0; i < horas.length; i++) {
            if (reservas[i].isEmpty()) { //si esta libre le pone al apar que esta libre
                sb.append(horas[i]).append(" - Libre\n");
                libres++;//suma contador
            } else {
                String nombre = null;
                if (controlSocios != null) {
                    nombre = controlSocios.getNombrePorId(reservas[i]); //busca el nombre con el id
                }
                if (nombre == null || nombre.isEmpty()) {
                    nombre = "(Nombre no disponible)";
                }
                sb.append(horas[i]).append(" - Reservado por ").append(nombre).append("\n");
            }
        }

        sb.append("\nTotal horarios libres: ").append(libres);
        JOptionPane.showMessageDialog(null, sb.toString()); //muestra el mensaje
    }

    private String pedirIdValido() { //pide el id y valida que sea de un socio
        while (true) {
            String id = JOptionPane.showInputDialog(
                    "Validación de socio\n\nIngrese su ID (ej: socio#): ");
            if (id == null) {
                return null; //si cancela
            }
            id = id.trim();//quita espacios
            if (id.isEmpty()) {//por si pone un id vacio
                JOptionPane.showMessageDialog(null, "El ID no puede estar vacio");
            } else {
                boolean existe = false;
                if (controlSocios != null) { //revisa si el ID esta en los socios
                    existe = controlSocios.existeId(id); //cambio a true
                }
                if (existe) {
                    return id; //si existe lo devuelve
                } else {
                    JOptionPane.showMessageDialog(null, "ID no encontrado, intente otra vez");
                }
            }
        }
    }

    private int buscarIndiceHora(String hora) { //busca en la posicion del arreglo la hora escrita
        int pos = -1;//no se encontro
        for (int i = 0; i < horas.length; i++) {
            if (horas[i].equalsIgnoreCase(hora)) {
                pos = i;//guarda la posicion si la encuentra
                break;
            }
        }
        return pos;//devuelve la posicion o -1 si no la encontro
    }

    private void reservarHorario() { //Para reservar una hora
        String hora = JOptionPane.showInputDialog("Ingrese hora a reservar (ej: 9am, 2pm):");
        if (hora == null) {
            return; //Si camcela
        }
        hora = hora.trim().toLowerCase();//quita espacios y lo pone en minuscula

        int pos = buscarIndiceHora(hora);
        if (pos == -1) {//como pos es -1, si no se encontro se queda asi
            JOptionPane.showMessageDialog(null, "Hora invalida");
            return;
        }

        if (!reservas[pos].isEmpty()) { //dice si ahi hay un id guardado
            JOptionPane.showMessageDialog(null, "Hora ya reservada");
            return;
        }

        String idValido = pedirIdValido();//pide un id y lo valida
        if (idValido == null) {
            return;//se sale si cancela o no es valido
        }

        reservas[pos] = idValido; //guarda el id en la hora seleccionada

        String nombre = null;//para poder guardar el id del socio
        if (controlSocios != null) {
            nombre = controlSocios.getNombrePorId(idValido);//busca el id
        }
        if (nombre == null || nombre.isEmpty()) {
            nombre = "Desconocido";//pone eso si no encontro nombre o esta vacio
        }

        JOptionPane.showMessageDialog(null, "Reserva exitosa para usuario " + nombre + " a las " + horas[pos]);
    }

    public void mostrarResumenReservas() { //muestra todas las horas ocupadas y por quien
        StringBuilder sb = new StringBuilder("Resumen de reservas cabina:\n");
        boolean alguna = false;//para validar si hay al menos una reserva

        for (int i = 0; i < horas.length; i++) {
            if (!reservas[i].isEmpty()) { //si en esa hora hay un id guardado
                String nombre = null; //para guardar el nombre del socio
                if (controlSocios != null) {
                    nombre = controlSocios.getNombrePorId(reservas[i]); //busca el nombre usando el id
                }
                if (nombre == null || nombre.isEmpty()) { //si no encontro nombre
                    nombre = "Nombre no disponible"; //pone texto por defecto
                }
                sb.append(horas[i]).append(" - ").append(nombre).append("\n"); //agrega la hora y el nombre al texto
                alguna = true; //marca que si hubo reservas
            }
        }

        if (!alguna) { //si no se encontro ninguna reserva
            sb.append("No hay reservas\n"); //mensaje cuando esta vacio
        }

        JOptionPane.showMessageDialog(null, sb.toString()); //muestra la lista de reservas en pantalla
    }

    public void liberarReserva() { //libera una hora que estaba ocupada
        String hora = JOptionPane.showInputDialog("Ingrese la hora para liberar (ej: 9am):");
        if (hora == null) {
            return;
        }
        hora = hora.trim().toLowerCase();//quitar espacios y poner minuscula

        int pos = buscarIndiceHora(hora);//busca la posicion de la hora
        if (pos == -1) {//si no existe
            JOptionPane.showMessageDialog(null, "Hora invalida");
            return;
        }

        if (!reservas[pos].isEmpty()) {//si hay un id guardado esta ocupada
            reservas[pos] = "";//la deja libre
            JOptionPane.showMessageDialog(null, "Reserva liberada correctamente");
        } else {
            JOptionPane.showMessageDialog(null, "No hay reserva en esa hora");
        }
    }
}
