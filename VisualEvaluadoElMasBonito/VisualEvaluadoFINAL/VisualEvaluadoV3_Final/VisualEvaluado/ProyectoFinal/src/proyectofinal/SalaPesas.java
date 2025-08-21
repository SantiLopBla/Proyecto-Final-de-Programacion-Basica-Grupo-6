/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class SalaPesas {

    private final String[] sociosDentro;
    private int cantidad;

    public SalaPesas() {
        sociosDentro = new String[50];
        cantidad = 0;
    }

    public boolean ingresar(String idSocio) {
        if (cantidad < sociosDentro.length && !estaDentro(idSocio)) {
            sociosDentro[cantidad++] = idSocio;
            return true;
        }
        return false;
    }

    public boolean salir(String idSocio) {
        for (int i = 0; i < cantidad; i++) {
            if (sociosDentro[i].equals(idSocio)) {
                for (int j = i; j < cantidad - 1; j++) {
                    sociosDentro[j] = sociosDentro[j + 1];
                }
                sociosDentro[--cantidad] = null;
                return true;
            }
        }
        return false;
    }

    public boolean estaDentro(String idSocio) {
        for (int i = 0; i < cantidad; i++) {
            if (sociosDentro[i].equals(idSocio)) {
                return true;
            }
        }
        return false;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String[] getSociosActuales() {
        String[] actuales = new String[cantidad];
        System.arraycopy(sociosDentro, 0, actuales, 0, cantidad);
        return actuales;
    }

}
