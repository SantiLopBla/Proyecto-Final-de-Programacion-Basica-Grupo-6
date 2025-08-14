/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prof;

/**
 *
 * @author sanlo
 */
public class Socio {

    private String id; //para crear el socio
    private String nombre;

    public Socio(String id, String nombre) { //constructor
        this.id = id; //asigna el id
        this.nombre = nombre; //asigna el nombre
    }

    public String getNombre() { //getters
        return nombre;
    }

    public String getId() {
        return id;
    }
}
