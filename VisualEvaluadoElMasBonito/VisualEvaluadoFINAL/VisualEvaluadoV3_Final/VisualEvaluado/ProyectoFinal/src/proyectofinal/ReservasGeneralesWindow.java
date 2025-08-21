/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
import javax.swing.*;
import java.awt.*;

public class ReservasGeneralesWindow extends JFrame {

    public ReservasGeneralesWindow(SistemaGimnasio sistema) {
        setTitle("Reservas Generales");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        StringBuilder sb = new StringBuilder();

        // 1. Parqueo
        sb.append("=== PARQUEO ===\n");
        mostrarParqueo(sistema.g1, sistema.g1IDs, sb, "G1");
        mostrarParqueo(sistema.g2, sistema.g2IDs, sb, "G2");
        mostrarParqueo(sistema.g3, sistema.g3IDs, sb, "G3");

        // 2. Clases
        sb.append("\n=== CLASES GRUPALES ===\n");
        for (ClaseGrupal c : sistema.clases) {
            sb.append(c.nombre).append(" | ").append(c.horario)
                    .append(" | Inscritos: ").append(c.reservados).append("\n");
        }

        // 3. Cabinas
        sb.append("\n=== CABINAS ===\n");
        for (int i = 0; i < sistema.cabinas.getCantidadCabinas(); i++) {
            for (int h = 0; h < sistema.cabinas.getCantidadHoras(); h++) {
                String id = sistema.cabinas.getReserva(i, h);
                if (id != null) {
                    sb.append("Cabina ").append(i + 1)
                            .append(" - ").append(sistema.cabinas.horas[h])
                            .append(": ").append(id).append("\n");
                }
            }
        }

        // 4. Auditorio
        sb.append("\n=== AUDITORIO ===\n");
        for (int s = 0; s < sistema.auditorio.sesiones.length; s++) {
            String[] inscritos = sistema.auditorio.getReservas(s);
            sb.append("Sesión ").append(sistema.auditorio.sesiones[s])
                    .append(": ").append(inscritos.length).append(" inscritos\n");
        }

        // 5. Sala de pesas
        sb.append("\n=== SALA DE PESAS ===\n");
        sb.append("Personas dentro: ").append(sistema.salaPesas.getCantidad()).append("\n");

        // 6. Recreación - mesas
        sb.append("\n=== PING-PONG ===\n");
        mostrarMesas(sistema.pingPong, sb, "Ping Pong");

        sb.append("\n=== BILLAR ===\n");
        mostrarMesas(sistema.billar, sb, "Billar");

        // 6. Recreación - canchas
        sb.append("\n=== FÚTBOL ===\n");
        mostrarCanchas(sistema.futbol, sb);
        sb.append("\n=== BALONCESTO ===\n");
        mostrarCanchas(sistema.baloncesto, sb);
        sb.append("\n=== TENIS ===\n");
        mostrarCanchas(sistema.tenis, sb);

        textArea.setText(sb.toString());
        add(new JScrollPane(textArea));
        setVisible(true);
    }

    private void mostrarParqueo(char[][] matriz, String[][] ids, StringBuilder sb, String nombre) {
        sb.append("Nivel ").append(nombre).append(":\n");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                if (matriz[i][j] == 'O') {
                    sb.append("[").append((char) ('A' + i)).append(j + 1).append("] ID: ").append(ids[i][j]).append("\n");
                }
            }
        }
    }

    private void mostrarMesas(MesaRecreativa mesa, StringBuilder sb, String tipo) {
        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            for (int h = 0; h < mesa.getCantidadHoras(); h++) {
                String id = mesa.getReserva(i, h);
                if (id != null) {
                    sb.append(tipo).append(" ").append(i + 1)
                            .append(" - ").append(mesa.horarios[h])
                            .append(": ").append(id).append("\n");
                }
            }
        }
    }

    private void mostrarCanchas(CanchaRecreativa[] grupo, StringBuilder sb) {
        for (CanchaRecreativa cancha : grupo) {
            sb.append(cancha.tipo).append(" (")
                    .append(cancha.getJugadores().length).append(" jugadores)\n");
        }
    }
}
