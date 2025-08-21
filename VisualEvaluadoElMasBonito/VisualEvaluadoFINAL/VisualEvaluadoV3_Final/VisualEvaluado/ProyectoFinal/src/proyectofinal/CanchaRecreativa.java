/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class CanchaRecreativa {

    public final String tipo;
    public final int capacidad;
    public String[] ids;
    public int registrados;

    public CanchaRecreativa(String tipo, int capacidad) {
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.ids = new String[capacidad];
        this.registrados = 0;
    }

    public boolean registrar(String id) {
        if (registrados < capacidad) {
            ids[registrados++] = id;
            return true;
        }
        return false;
    }

    public boolean cancelar(String id) {
        for (int i = 0; i < registrados; i++) {
            if (ids[i].equals(id)) {
                for (int j = i; j < registrados - 1; j++) {
                    ids[j] = ids[j + 1];
                }
                ids[--registrados] = null;
                return true;
            }
        }
        return false;
    }

    public String[] getJugadores() {
        String[] actual = new String[registrados];
        System.arraycopy(ids, 0, actual, 0, registrados);
        return actual;
    }

    public int getDisponibles() {
        return capacidad - registrados;
    }
}
