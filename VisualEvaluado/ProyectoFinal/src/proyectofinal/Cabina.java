/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class Cabina {
    public String[] horas = {
        "9:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
        "17:00", "18:00"
    };

    public String[][] reservas; // [cabina][hora] = ID del socio

    public Cabina(int cantidadCabinas) {
        reservas = new String[cantidadCabinas][horas.length];
    }

    public boolean reservar(int cabinaIndex, int horaIndex, String idSocio) {
        if (reservas[cabinaIndex][horaIndex] == null) {
            reservas[cabinaIndex][horaIndex] = idSocio;
            return true;
        }
        return false;
    }

    public void liberar(int cabinaIndex, int horaIndex) {
        reservas[cabinaIndex][horaIndex] = null;
    }

    public String getReserva(int cabinaIndex, int horaIndex) {
        return reservas[cabinaIndex][horaIndex];
    }

    public int getCantidadCabinas() {
        return reservas.length;
    }

    public int getCantidadHoras() {
        return horas.length;
    }
}

