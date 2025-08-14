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

public class CabinasWindow extends JFrame {
    private SistemaGimnasio sistema;
    private Cabina cabinas;
    private JButton[][] botones;

    public CabinasWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Cabinas Insonorizadas");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cabinas = new Cabina(4); // 4 cabinas
        botones = new JButton[cabinas.getCantidadCabinas()][cabinas.getCantidadHoras()];

        JPanel gridPanel = new JPanel(new GridLayout(cabinas.getCantidadCabinas() + 1, cabinas.getCantidadHoras() + 1));
        gridPanel.add(new JLabel("")); // Esquina superior izquierda vacía

        // Encabezado de horas
        for (String hora : cabinas.horas) {
            gridPanel.add(new JLabel(hora, JLabel.CENTER));
        }

        // Cabinas y botones
        for (int i = 0; i < cabinas.getCantidadCabinas(); i++) {
            gridPanel.add(new JLabel("Cabina " + (i + 1), JLabel.CENTER));

            for (int j = 0; j < cabinas.getCantidadHoras(); j++) {
                JButton btn = new JButton("-");
                btn.setBackground(Color.GREEN);
                btn.setToolTipText("Disponible");
                botones[i][j] = btn;

                final int cabina = i;
                final int hora = j;

                btn.addActionListener(e -> {
                    String actual = cabinas.getReserva(cabina, hora);
                    if (actual == null) {
                        String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
                        if (id != null && !id.trim().isEmpty()) {
                            cabinas.reservar(cabina, hora, id.trim());
                            actualizarBotones();
                        }
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(this, "¿Liberar esta cabina?", "Liberar", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            cabinas.liberar(cabina, hora);
                            actualizarBotones();
                        }
                    }
                });

                gridPanel.add(btn);
            }
        }

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        actualizarBotones();
        setVisible(true);
    }

    private void actualizarBotones() {
        for (int i = 0; i < cabinas.getCantidadCabinas(); i++) {
            for (int j = 0; j < cabinas.getCantidadHoras(); j++) {
                String reserva = cabinas.getReserva(i, j);
                JButton btn = botones[i][j];
                if (reserva == null) {
                    btn.setText("-");
                    btn.setBackground(Color.GREEN);
                    btn.setToolTipText("Disponible");
                } else {
                    btn.setText("Ocupado");
                    btn.setBackground(Color.RED);
                    btn.setToolTipText("Reservado por ID: " + reserva);
                }
            }
        }
    }
}

