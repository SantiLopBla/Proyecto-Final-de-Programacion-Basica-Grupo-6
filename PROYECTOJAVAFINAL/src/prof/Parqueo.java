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
public class Parqueo {

    private char[][] planta1 = new char[4][5]; //matriz 4x5 planta 1
    private char[][] planta2 = new char[5][5]; //matriz 5x5 planta 2
    private char[][] planta3 = new char[6][5]; //matriz 6x5 planta 3

    public Parqueo() {
        inicializarPlantas(); //inicializa todas las plantas con valores iniciales
    }

    private void inicializarPlantas() {
        inicializarPlanta(planta1, 3, 2); //pone discapacitados y entrenadores en planta1
        inicializarPlanta(planta2, 3, 2); //hace los mismo para planta 2
        inicializarPlanta(planta3, 3, 2); //igual para planta 3
    }

    private void inicializarPlanta(char[][] planta, int discapacitados, int entrenadores) {
        for (int i = 0; i < planta.length; i++) {
            for (int j = 0; j < planta[0].length; j++) {
                planta[i][j] = 'L'; //marca todos los parqueos como libres
            }
        }
        for (int i = 0; i < discapacitados; i++) {
            planta[0][i] = 'D'; //reserva las primeras columnas de la fila 0 para discapacitados
        }
        for (int i = 0; i < entrenadores; i++) {
            planta[0][planta[0].length - 1 - i] = 'E'; //reserva las ultimas columnas de la fila 0 para entrenadores
        }
    }

    public void menuParqueo(Recepcionista socio) {
        String opcion; //opcion del menu
        do {
            opcion = JOptionPane.showInputDialog( //menu principal del parqueo
                    "Parqueo - Menu\n"
                    + "1. Ver estado del parqueo\n"
                    + "2. Reservar espacio\n"
                    + "3. Liberar espacio\n"
                    + "4. Volver al menu principal"
            );
            if (opcion == null || opcion.equals("4")) { //si cancela o elige salir
                break; //sale del menu
            }

            switch (opcion) {
                case "1":
                    mostrarParqueo(); //muestra el estado completo del parqueo
                    break;
                case "2":
                    reservarEspacio(socio); //inicia reserva para socio activo
                    break;
                case "3":
                    liberarEspacio(); //libera un espacio ocupado
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opcion invalida"); //entrada no valida
            }
        } while (true); //repite hasta salir
    }

    public void mostrarParqueo() {
        String mensaje = "Estado del parqueo:\n\n";
        mensaje += "Planta 1:\n" + mostrarPlanta(planta1); //muestra planta 1
        mensaje += "Planta 2:\n" + mostrarPlanta(planta2); //planta 2
        mensaje += "Planta 3:\n" + mostrarPlanta(planta3); //planta 3
        JOptionPane.showMessageDialog(null, mensaje); //muestra todo junto
    }

    private String mostrarPlanta(char[][] planta) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < planta.length; i++) {
            for (int j = 0; j < planta[0].length; j++) {
                sb.append("[").append(planta[i][j]).append("]"); //hace que cada parqueo este entre corchetes
            }
            sb.append("\n"); //salto de linea para cada fila
        }
        return sb.toString(); //retorna string
    }

    public void reservarEspacio(Recepcionista socio) {
        if (!socio.isActivo()) {
            JOptionPane.showMessageDialog(null, "Su membresia no esta activa"); //verifica que socio este activo
            return;
        }

        int plantaSeleccionada = -1; //para empezar con logica humana
        while (true) {
            String input = JOptionPane.showInputDialog("Ingrese la planta para reservar espacio (1, 2 o 3):");
            if (input == null) {
                return; //si cancela vuelve
            }
            try {
                plantaSeleccionada = Integer.parseInt(input);
                if (plantaSeleccionada < 1 || plantaSeleccionada > 3) {
                    JOptionPane.showMessageDialog(null, "Planta invalida, intente de nuevo"); //validacion planta correcta
                    continue;
                }
                break; //sale si planta valida
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada invalida, ingrese un número"); //error si no es numero
            }
        }

        char[][] planta;
        if (plantaSeleccionada == 1) {
            planta = planta1; //elige el parqueo que la persona diga (1,2,3)
        } else if (plantaSeleccionada == 2) {
            planta = planta2;
        } else {
            planta = planta3;
        }

        mostrarParqueoDePlanta(plantaSeleccionada, planta); //muestra estado de la planta elegida

        int filaSeleccionada = -1; //Resta 1 porque los arreglos empiezan en 0
        int columnaSeleccionada = -1;//Lo mismo

        while (true) {
            String filaInput = JOptionPane.showInputDialog("Ingrese la fila que desea reservar (1 a " + planta.length + "):"); //pide fila
            if (filaInput == null) {
                return; //cancela
            }
            String columnaInput = JOptionPane.showInputDialog("Ingrese la columna que desea reservar (1 a " + planta[0].length + "):"); //pide columna
            if (columnaInput == null) {
                return;
            }

            try { //validacion para que no se caiga si el usuario pone algo malo
                filaSeleccionada = Integer.parseInt(filaInput) - 1;
                columnaSeleccionada = Integer.parseInt(columnaInput) - 1;

                if (filaSeleccionada < 0 || filaSeleccionada >= planta.length //no deja si es menor a 0 o mayor al tamaño de la planta
                        || columnaSeleccionada < 0 || columnaSeleccionada >= planta[0].length) {
                    JOptionPane.showMessageDialog(null, "Fila o columna invalida, intente de nuevo"); //validacion indices validos
                    continue;
                }

                if (planta[filaSeleccionada][columnaSeleccionada] == 'L') {
                    planta[filaSeleccionada][columnaSeleccionada] = 'O'; //marca espacio como ocupado
                    JOptionPane.showMessageDialog(null, "Espacio reservado en planta " + plantaSeleccionada + " posición [Fila: " + (filaSeleccionada + 1) + ", Columna: " + (columnaSeleccionada + 1) + "]");
                    return; //fin si reserva con exito
                } else {
                    JOptionPane.showMessageDialog(null, "El espacio no está libre, elija otro"); //si no esta libre no puede reservar
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada invalida, ingrese números validos"); //error si el numero no es valido
            }
        }
    }

    private void mostrarParqueoDePlanta(int plantaNumero, char[][] planta) {
        String mensaje = "Estado planta " + plantaNumero + ":\n";
        mensaje += mostrarPlanta(planta); //muestra el estado de la planta indicada
        JOptionPane.showMessageDialog(null, mensaje); //cuadro de texto para print del estado de la planta
    }

    public void liberarEspacio() {
        int plantaSeleccionada = -1; //porque los arreglos empiezan en 0 entonces para que el usuario pueda ponerlo normal
        while (true) {
            String input = JOptionPane.showInputDialog("Ingrese la planta (1, 2 o 3) para liberar espacio:");
            if (input == null) {//si el usuario cancela se sale 
                return; //cancela
            }
            try {
                plantaSeleccionada = Integer.parseInt(input);
                if (plantaSeleccionada < 1 || plantaSeleccionada > 3) { //sale si pone un numero menor a 1 o mayor a 3
                    JOptionPane.showMessageDialog(null, "Planta invalida, intente de nuevo"); //validacion planta
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada invalida, ingrese un número"); //error formato
            }
        }

        char[][] planta;
        if (plantaSeleccionada == 1) { //logica de seleccion de planta
            planta = planta1;
        } else if (plantaSeleccionada == 2) {
            planta = planta2;
        } else {
            planta = planta3;
        }

        JOptionPane.showMessageDialog(null, mostrarPlanta(planta)); //muestra la planta para ver posiciones
        String posicionInput = JOptionPane.showInputDialog("Ingrese la posición a liberar (fila , columna) ej: 1,3");
        if (posicionInput == null) {
            return; //cancela
        }

        try {
            String[] parts = posicionInput.split(",");  //divide el texto que ingreso el usuario usando la coma como separador y lo guarda en un arreglo
            int fila = Integer.parseInt(parts[0].trim()) - 1; //convierte indices a base 0
            int columna = Integer.parseInt(parts[1].trim()) - 1;

            if (fila < 0 || fila >= planta.length || columna < 0 || columna >= planta[0].length) {
                JOptionPane.showMessageDialog(null, "Posición inválida."); //da posicion invalida si la fila es menor a 0 o mayor al tamaño de esta
                return;
            }

            if (planta[fila][columna] == 'O') {
                planta[fila][columna] = 'L'; //libera espacio marcado como ocupado
                JOptionPane.showMessageDialog(null, "Espacio liberado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "El espacio no está ocupado o no puede ser liberado."); //si no esta ocupado no puede liberar
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Formato inválido."); //error si el formato no es correcto
        }
    }
}
