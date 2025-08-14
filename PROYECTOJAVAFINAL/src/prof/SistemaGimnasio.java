/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prof;

import javax.swing.JOptionPane;

/**
 *
 * @author sanlo
 */
public class SistemaGimnasio {

    static Recepcionista recepcionActual = null; // guarda la recepcionista que usa el sistema
    static Parqueo parqueo = new Parqueo(); // todas afuera para poder usarlas más de una vez
    static Auditorio auditorio = new Auditorio();
    static Cabina cabina = new Cabina();
    static SalaDePesas salaDePesas = new SalaDePesas();
    static ClasesGrupales clasesGrupales = new ClasesGrupales();
    static EspacioRecreativo espacioRecreativo = new EspacioRecreativo();
    static ControlSocios controlSocios = new ControlSocios();

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Bienvenido al Sistema del Gimnasio");

        recepcionActual = Recepcionista.registrarSocio();
        JOptionPane.showMessageDialog(null, "Nombre del/la recepcionista: " + recepcionActual.getId());

        boolean salir = false;

        while (!salir) { // menú principal
            String opcion = JOptionPane.showInputDialog(
                    "Menú principal:\n"
                    + "1. Control de Socios\n"
                    + "2. Parqueo\n"
                    + "3. Reservar Auditorio\n"
                    + "4. Cabinas\n"
                    + "5. Sala de Pesas\n"
                    + "6. Clases Grupales\n"
                    + "7. Espacios Recreativos\n"
                    + "8. Listado de Reservas\n"
                    + "9. Salir"
            );

            if (opcion == null) {
                break;
            }

            switch (opcion) {
                case "1":
                    menuControlSocios();
                    break;
                case "2":
                    parqueo.menuParqueo(recepcionActual);
                    break;
                case "3":
                    auditorio.menuAuditorio(controlSocios);
                    break;
                case "4":
                    cabina.menuCabina(controlSocios);
                    break;
                case "5":
                    salaDePesas.menuSala(controlSocios);
                    break;
                case "6":
                    clasesGrupales.menuClases(controlSocios);
                    break;
                case "7":
                    espacioRecreativo.menuEspacioRecreativo();
                    break;
                case "8":
                    mostrarListadoReservas();
                    break;
                case "9":
                    salir = true;
                    JOptionPane.showMessageDialog(null, "Gracias por usar el sistema, vuelva pronto");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opcion invalida, intente de nuevo");

            }
        }
    }

    private static void menuControlSocios() { //Para mostrar los socios
        String opcion = JOptionPane.showInputDialog(
                "Control de Socios:\n"
                + "1. Mostrar todos los socios\n"
                + "2. Volver al menú principal"
        );
        if (opcion == null) {
            return;
        }

        switch (opcion) {
            case "1":
                controlSocios.mostrarSocios();
                break;
            case "2": //Para salir
                JOptionPane.showMessageDialog(null, "Volviendo al menu principal");
                return;
            default:
                JOptionPane.showMessageDialog(null, "Opción invalida");
        }
    }

    private static void mostrarListadoReservas() { // para mostrar los listados de todo
        String opcion;

        do {
            opcion = JOptionPane.showInputDialog(
                    "Seleccione el área que desea ver:\n"
                    + "1. Ver parqueo\n"
                    + "2. Ver sala de pesas\n"
                    + "3. Ver auditorio\n"
                    + "4. Ver cabina\n"
                    + "5. Ver clases grupales\n"
                    + "6. Ver espacios recreativos\n"
                    + "7. Salir"
            );

            if (opcion == null || opcion.equals("7")) { //salir si presiona cancelar o 7
                break;
            }

            switch (opcion) {
                case "1":
                    parqueo.mostrarParqueo();
                    break;
                case "2":
                    salaDePesas.mostrarReservasSala();
                    break;
                case "3":
                    auditorio.mostrarListaParticipantes();
                    break;
                case "4":
                    cabina.mostrarResumenReservas();
                    break;
                case "5":
                    clasesGrupales.mostrarClases();
                    break;
                case "6":
                    espacioRecreativo.mostrarReservas();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción invalida");
            }

        } while (true);
    }

}
