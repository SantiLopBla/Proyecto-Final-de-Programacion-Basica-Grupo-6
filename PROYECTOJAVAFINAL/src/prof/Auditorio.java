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
public class Auditorio {

    private static Sesion[] sesiones = new Sesion[2]; //arreglo de dos sesiones
    private static boolean precargado = false; //para cargar una sola vez los datos

    static class Sesion { //modelo simple de una sesion

        String nombre; //hora identificadora de la sesion
        int capacidadMaxima; //cupo total permitido
        int cuposDisponibles; //cupo restante
        String[] inscritos; //ids inscritos en esta sesion
        int cantidadInscritos; //contador de inscritos actuales

        public Sesion(String nombre, int capacidadMaxima) {
            this.nombre = nombre; //guarda el nombre de la sesion
            this.capacidadMaxima = capacidadMaxima; //define la capacidad
            this.cuposDisponibles = capacidadMaxima; //inicia con cupo lleno
            this.inscritos = new String[capacidadMaxima]; //reserva espacio para ids
            this.cantidadInscritos = 0;
        }

        public boolean reservar(String idSocio) {
            if (cuposDisponibles <= 0) {
                return false; //sin espacio no se reserva
            }
            for (int i = 0; i < cantidadInscritos; i++) {
                if (inscritos[i] != null && inscritos[i].equalsIgnoreCase(idSocio)) {
                    return false; //evita inscribir duplicado
                }
            }
            inscritos[cantidadInscritos] = idSocio; //agrega el id al final
            cantidadInscritos = cantidadInscritos + 1; //incrementa inscritos
            cuposDisponibles = cuposDisponibles - 1; //reduce cupo libre
            return true; //reserva confirmada
        }

        public boolean liberar(String idSocio) {
            int ind = -1; //posicion del id a eliminar
            for (int i = 0; i < cantidadInscritos; i++) {
                if (inscritos[i] != null && inscritos[i].equalsIgnoreCase(idSocio)) {
                    ind = i; //encontro la posicion
                    break; //sale de la busqueda
                }
            }
            if (ind == -1) {
                return false; //no estaba inscrito
            }
            inscritos[ind] = inscritos[cantidadInscritos - 1]; //mueve el que selecciono para eliminarlo
            inscritos[cantidadInscritos - 1] = null; //limpia la ultima celda
            cantidadInscritos = cantidadInscritos - 1; //baja el contador
            cuposDisponibles = cuposDisponibles + 1; //recupera un cupo
            return true; //liberacion exitosa
        }

        public String resumen() {
            return nombre + " - Cupos disponibles: " + cuposDisponibles; //mensaje corto de estado
        }
    }

    public void menuAuditorio(ControlSocios controlSocios) {
        if (!precargado) {
            precargarSesiones(); //crea 10am y 3pm
            precargado = true; //marca como cargado
        }
        while (true) {
            String opcion = JOptionPane.showInputDialog(
                    "Auditorio - Menú\n"
                    + "1) Ver sesiones\n"
                    + "2) Inscribir participante\n"
                    + "3) Mostrar lista de inscritos\n"
                    + "4) Liberar participante\n"
                    + "5) Volver al menú principal"
            );
            if (opcion == null) {
                break; //salida por cancelacion
            }

            switch (opcion) {
                case "1":
                    mostrarSesiones(); //muestra cupos de cada sesion
                    break;
                case "2":
                    inscribir(controlSocios); //para inscribir
                    break;
                case "3":
                    mostrarListaParticipantes(); //lista ids 
                    break;
                case "4":
                    liberarParticipante(); //elimina un id 
                    break;
                case "5":
                    return; //volver al menu general
                default:
                    JOptionPane.showMessageDialog(null, "Opción invalida"); //entrada no reconocida
            }
        }
    }

    private void precargarSesiones() {
        sesiones[0] = new Sesion("10am", 30); //sesion de la mañanita
        sesiones[1] = new Sesion("3pm", 30); //sesion tarde
    }

    public void mostrarSesiones() {
        StringBuilder mensaje = new StringBuilder("Sesiones disponibles:\n");
        for (int i = 0; i < sesiones.length; i++) {
            mensaje.append(i).append(". ").append(sesiones[i].resumen()).append("\n"); //para mostrar las sesiones
        }
        JOptionPane.showMessageDialog(null, mensaje.toString()); //muestra el mensaje
    }

    private String pedirIdValido(ControlSocios controlSocios) {
        while (true) {
            String idIngresado = JOptionPane.showInputDialog("Validación de socio\n\nIngrese su ID (ej: socio1):");
            if (idIngresado == null) {
                return null; //cancelado por el usuario
            }
            idIngresado = idIngresado.trim(); //limpia espacios
            if (idIngresado.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El ID no puede estar vacío"); //valida vacio
            } else {
                boolean existe = controlSocios.existeId(idIngresado); //consulta en control de socios
                if (existe) {
                    return idIngresado; //id aceptado
                } else {
                    JOptionPane.showMessageDialog(null, "ID no encontrado. Intente de nuevo"); //id desconocido
                }
            }
        }
    }

    private void inscribir(ControlSocios controlSocios) {
        String idValido = pedirIdValido(controlSocios); //obtiene un id valido
        if (idValido == null) {
            return; //salida si cancelaron
        }

        mostrarSesiones(); //muestra opciones antes de elegir
        String input = JOptionPane.showInputDialog("Ingrese el número de la sesión a reservar:"); //solicita indice
        if (input == null) {
            return; //salida si cancelaron
        }

        int opcion; //indice numerico elegido
        try {
            opcion = Integer.parseInt(input); //parseo porque ingresan String
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida"); //no ingreso un numero
            return; //corta 
        }

        if (opcion >= 0 && opcion < sesiones.length) { //valida rango
            boolean exito = sesiones[opcion].reservar(idValido); //intenta reservar
            if (exito) {
                String nombre = controlSocios.getNombrePorId(idValido); //busca el id valido y lo pone
                if (nombre == null || nombre.isEmpty()) {
                    nombre = "Desconocido"; //se pone esto para que el programa muestre algo aunque este vacio
                }
                JOptionPane.showMessageDialog(null, "Reserva exitosa\nID: " + idValido); //confirmacion al usuario
            } else {
                JOptionPane.showMessageDialog(null, "No hay cupos disponibles o el ID ya está inscrito en esa sesion");//Si algo falla
            }
        } else {
            JOptionPane.showMessageDialog(null, "Número de sesión invalido"); //fuera de rango
        }
    }

    public void mostrarListaParticipantes() {
        StringBuilder sb = new StringBuilder("Lista de inscritos por sesión (IDs):\n\n");

        sb.append("Sesión 10am:\n");// Validar Sesión 10am
        if (sesiones[0] == null || sesiones[0].cantidadInscritos == 0) { //valida que haya clases y inscritos
            sb.append("No hay inscritos\n");//muestra eso si no hay inscritos
        } else {
            for (int i = 0; i < sesiones[0].cantidadInscritos; i++) {
                if (sesiones[0].inscritos[i] != null) { //si el arreglo tiene algo dentro lo ejecuta
                    sb.append("- ").append(sesiones[0].inscritos[i]).append("\n");
                }
            }
        }

        // Validar Sesión 3pm
        sb.append("\nSesión 3pm:\n");
        if (sesiones[1] == null || sesiones[1].cantidadInscritos == 0) {
            sb.append("No hay inscritoss\n");//muestra eso si no hay inscritos
        } else {
            for (int i = 0; i < sesiones[1].cantidadInscritos; i++) {
                if (sesiones[1].inscritos[i] != null) {//Evita null y muestra la lista
                    sb.append("- ").append(sesiones[1].inscritos[i]).append("\n");
                }
            }
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public void liberarParticipante() {
        mostrarSesiones(); //contexto de indices
        String input = JOptionPane.showInputDialog("Ingrese el número de la sesión para liberar:"); //pide sesion
        if (input == null) {
            return; //salida si cancelan
        }

        int opcion;
        try {
            opcion = Integer.parseInt(input); //parsea la entrada
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada invalida"); //no es numero
            return; //corta 
        }

        if (opcion < 0 || opcion >= sesiones.length) {
            JOptionPane.showMessageDialog(null, "Número de sesión invalido"); //rango incorrecto
            return; //sale
        }

        String id = JOptionPane.showInputDialog("Ingrese el ID a liberar:"); //pide id a quitar
        if (id == null) {
            return; //cancelado
        }
        id = id.trim(); //limpia texto
        if (id.isEmpty()) {//si esta vacio
            JOptionPane.showMessageDialog(null, "El ID no puede estar vacío"); //valida vacio
            return; //sale
        }

        boolean liberado = sesiones[opcion].liberar(id); //intenta liberar el id
        if (liberado) {
            JOptionPane.showMessageDialog(null, "Reserva liberada exitosamente"); //confirmacion
        } else {
            JOptionPane.showMessageDialog(null, "ID no encontrado en esa sesión"); //no estaba en la lista
        }
    }
}
