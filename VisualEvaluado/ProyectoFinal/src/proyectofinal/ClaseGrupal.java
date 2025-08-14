/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class ClaseGrupal {
    public String nombre;
    public String horario; 
    public int capacidad;
    public int reservados;
    public String[] idsSocios;

    public ClaseGrupal(String nombre, String horario, int capacidad) {
        this.nombre = nombre;
        this.horario = horario;
        this.capacidad = capacidad;
        this.reservados = 0;
        this.idsSocios = new String[capacidad];
    }

    public boolean reservar(String idSocio) {
        if (reservados < capacidad) {
            idsSocios[reservados] = idSocio;
            reservados++;
            return true;
        }
        return false;
    }

    public void modificar(String nuevoNombre, String nuevoHorario, int nuevaCapacidad) {
        this.nombre = nuevoNombre;
        this.horario = nuevoHorario;

        // Si se cambia capacidad, redimensionar el array
        if (nuevaCapacidad != this.capacidad) {
            String[] nuevaLista = new String[nuevaCapacidad];
            for (int i = 0; i < Math.min(reservados, nuevaCapacidad); i++) {
                nuevaLista[i] = idsSocios[i];
            }
            this.idsSocios = nuevaLista;
            this.capacidad = nuevaCapacidad;
            if (reservados > nuevaCapacidad) {
                reservados = nuevaCapacidad;
            }
        }
    }
}

