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
public class ControlSocios {

    private Socio[] socios; // Arreglo de socios

    public ControlSocios() {
        socios = new Socio[50]; // espacio para 50 socios
        precargarSocios();
    }

    private void precargarSocios() {   // Método para precargar automáticamente IDs y nombres
        for (int i = 0; i < socios.length; i++) {
            String id = generarId(i + 1);
            socios[i] = new Socio(id, "Socio " + (i + 1));
        }
    }

    private String generarId(int numero) { // Formato socio1, socio2, socio3...
        return "socio" + numero;
    }

    public boolean existeId(String id) {// Verifica si existe un ID
        if (id == null) {
            return false;//se sale si no ingreso nada
        }
        for (int i = 0; i < socios.length; i++) {
            if (socios[i] != null && id.equalsIgnoreCase(socios[i].getId())) { //si encuentra el mismo ID, existe
                return true;
            }
        }
        return false;
    }

    public String getNombrePorId(String id) { //obtener nombre por ID, por si quieres mostrarlo
        for (int i = 0; i < socios.length; i++) {
            if (socios[i] != null && id.equalsIgnoreCase(socios[i].getId())) {
                return socios[i].getNombre(); //devuelve el nombre
            }
        }
        return null;
    }

    public void mostrarSocios() {   // Mostrar todos los socios 
        StringBuilder lista = new StringBuilder("Lista de IDs de socios:\n");
        for (int i = 0; i < socios.length; i++) {
            lista.append(socios[i].getId()).append("\n");
        }
        JOptionPane.showMessageDialog(null, lista.toString());
    }

    public Socio[] getSocios() {
        return socios;
    }

    public void setSocios(Socio[] socios) {
        this.socios = socios;
    }
}
