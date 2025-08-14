/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class Auditorio {
    public final String[] sesiones = { "10:00 a.m.", "3:00 p.m." };
    private final String[][] reservas; // reservas[sesión][índice participante]
    private final int capacidad = 30;
    private final int[] inscritos; // cuántos hay inscritos por sesión

    public Auditorio() {
        reservas = new String[2][capacidad];
        inscritos = new int[2];
    }

    public boolean registrar(int sesionIndex, String idSocio) {
        if (inscritos[sesionIndex] < capacidad) {
            reservas[sesionIndex][inscritos[sesionIndex]] = idSocio;
            inscritos[sesionIndex]++;
            return true;
        }
        return false;
    }

    public void cancelar(int sesionIndex, String idSocio) {
        for (int i = 0; i < inscritos[sesionIndex]; i++) {
            if (reservas[sesionIndex][i].equals(idSocio)) {
                // Desplazar los siguientes hacia atrás
                for (int j = i; j < inscritos[sesionIndex] - 1; j++) {
                    reservas[sesionIndex][j] = reservas[sesionIndex][j + 1];
                }
                reservas[sesionIndex][inscritos[sesionIndex] - 1] = null;
                inscritos[sesionIndex]--;
                break;
            }
        }
    }

    public int cuposDisponibles(int sesionIndex) {
        return capacidad - inscritos[sesionIndex];
    }

    public String[] getReservas(int sesionIndex) {
        String[] actual = new String[inscritos[sesionIndex]];
        System.arraycopy(reservas[sesionIndex], 0, actual, 0, inscritos[sesionIndex]);
        return actual;
    }
}

