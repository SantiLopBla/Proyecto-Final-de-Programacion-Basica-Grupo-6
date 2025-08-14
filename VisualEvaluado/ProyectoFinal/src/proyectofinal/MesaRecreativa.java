/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class MesaRecreativa {
    public String[] horarios = {
        "9:00", "9:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    };

    public String[][] reservas; // [mesa][hora]

    public MesaRecreativa(int cantidadMesas) {
        reservas = new String[cantidadMesas][horarios.length];
    }

    public boolean reservar(int mesa, int hora, String id) {
        if (reservas[mesa][hora] == null) {
            reservas[mesa][hora] = id;
            return true;
        }
        return false;
    }

    public void liberar(int mesa, int hora) {
        reservas[mesa][hora] = null;
    }

    public String getReserva(int mesa, int hora) {
        return reservas[mesa][hora];
    }

    public int getCantidadMesas() {
        return reservas.length;
    }

    public int getCantidadHoras() {
        return horarios.length;
    }
}

