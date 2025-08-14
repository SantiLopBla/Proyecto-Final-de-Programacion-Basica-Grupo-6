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
public class SalaDePesas {

    private static final int capMax = 50; //capacidad maxima permitida en la sala

    private String[] presentes = new String[capMax]; //guarda los ids de las personas que estan dentro
    private int cantidad = 0; //lleva la cuenta de cuantas personas hay

    private ControlSocios controlSocios; //sirve para validar ids y mostrar nombres

    public void menuSala(ControlSocios controlSocios) { //menu para usar la sala
        this.controlSocios = controlSocios;

        while (true) {
            String opcion = JOptionPane.showInputDialog(
                    "Sala de Pesas - Menú\n"
                    + "1) Registrar ingreso\n"
                    + "2) Registrar salida\n"
                    + "3) Ver estado actual\n"
                    + "4) Volver al menú principal"
            );
            if (opcion == null) {
                break; //sale del menu si se cancela
            }

            switch (opcion) {
                case "1":
                    registrarIngreso(); //agrega una persona a la sala
                    break;
                case "2":
                    registrarSalida(); //saca una persona de la sala
                    break;
                case "3":
                    mostrarReservasSala(); //muestra quienes estan adentro
                    break;
                case "4":
                    return; //regresa al menu anterior
                default:
                    JOptionPane.showMessageDialog(null, "Opción invalida");
            }
        }
    }

    private void registrarIngreso() { //registrar entrada
        if (cantidad >= capMax) {
            JOptionPane.showMessageDialog(null, "La sala esta llena");
            return; //no deja entrar si esta llena
        }

        String id = pedirIdValido(); //pide un id correcto
        if (id == null) {
            return; //si se cancela, sale
        }

        if (estaDentro(id)) { //evita que entre dos veces
            JOptionPane.showMessageDialog(null, "Ese ID ya está dentro de la sala");
            return;
        }
        presentes[cantidad] = id; //guarda el id en el arreglo
        cantidad++;

        String nombre = null;
        if (controlSocios != null) {
            nombre = controlSocios.getNombrePorId(id); //busca el nombre del socio
        }
        if (nombre == null || nombre.isEmpty()) {
            nombre = "Desconocido"; //si no encuentra el nombre
        }

        JOptionPane.showMessageDialog(null, "Ingreso registrado\nUsuario: " + nombre + "\nDentro ahora: " + cantidad + "/" + capMax
        );
    }

    private void registrarSalida() { //registrar salida
        if (cantidad == 0) {
            JOptionPane.showMessageDialog(null, "No hay personas dentro de la sala");
            return; //si esta vacia, no hace nada
        }

        String id = JOptionPane.showInputDialog("Ingrese el ID que va a salir:");
        if (id == null) {
            return; //si cancela, sale
        }
        id = id.trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El ID no puede estar vacio");
            return;
        }

        int idBuscado = buscarIndice(id); //busca donde esta el id
        if (idBuscado == -1) {
            JOptionPane.showMessageDialog(null, "Ese ID no está registrado dentro de la sala");
            return;
        }

        presentes[idBuscado] = presentes[cantidad - 1]; //mueve el ultimo al espacio que queda libre
        presentes[cantidad - 1] = null; //borra el ultimo
        cantidad--;

        JOptionPane.showMessageDialog(null, "Salida registrada. Ahorita hay dentro: " + cantidad + "/" + capMax);
    }

    public void mostrarReservasSala() { //muestra lista de personas dentro
        StringBuilder sb = new StringBuilder("Estado actual de la Sala de Pesas:\n");
        sb.append("Personas dentro: ").append(cantidad).append("/").append(capMax).append("\n\n");

        if (cantidad == 0) {
            sb.append("Sala vacía\n");
        } else {
            for (int i = 0; i < cantidad; i++) {
                String id = presentes[i];
                String nombre = null;
                if (controlSocios != null) {
                    nombre = controlSocios.getNombrePorId(id);
                }
                if (nombre == null || nombre.isEmpty()) {
                    nombre = "(Nombre no disponible)";
                }
                sb.append("- ").append(nombre).append(" (").append(id).append(")\n");
            }
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public void liberarReserva() { //libera espacio usando el mismo metodo de salida
        registrarSalida();
    }

    private String pedirIdValido() { //pide un id que exista
        while (true) {
            String id = JOptionPane.showInputDialog("Validación de socio\n\nIngrese su ID (ej: socio1):");
            if (id == null) {
                return null;//si cancela, se sale
            }
            id = id.trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El ID no puede estar vacío");
            } else {
                boolean existe = false;
                if (controlSocios != null) {//revisa que el id este en la lista de socios
                    existe = controlSocios.existeId(id);
                }
                if (existe) {
                    return id;
                } else {
                    JOptionPane.showMessageDialog(null, "ID no encontrado en socios precargados, intente de nuevo");
                }
            }
        }
    }

    private boolean estaDentro(String id) { //verifica si ya esta dentro de la sala
        for (int i = 0; i < cantidad; i++) {
            if (presentes[i].equalsIgnoreCase(id)) { //si el id esta, tira verdadero
                return true;
            }
        }
        return false;
    }

    private int buscarIndice(String id) { //dice la posicion del id en arreglo
        for (int i = 0; i < cantidad; i++) {
            if (presentes[i].equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1; //-1 para indicar que no esta
    }
}
