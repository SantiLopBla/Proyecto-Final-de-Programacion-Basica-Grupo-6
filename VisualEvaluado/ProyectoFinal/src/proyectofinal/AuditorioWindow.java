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

public class AuditorioWindow extends JFrame {
    private SistemaGimnasio sistema;
    private Auditorio auditorio;

    public AuditorioWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Auditorio Fitness");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        auditorio = new Auditorio();

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(crearPanelSesion(0)); // 10:00 a. m.
        panel.add(crearPanelSesion(1)); // 3:00 p. m.

        add(panel);
        setVisible(true);
    }

    private JPanel crearPanelSesion(int sesionIndex) {
        JPanel sesionPanel = new JPanel(new BorderLayout());
        String hora = auditorio.sesiones[sesionIndex];

        JLabel label = new JLabel("Sesión " + hora + " | Cupos disponibles: " + auditorio.cuposDisponibles(sesionIndex));
        sesionPanel.add(label, BorderLayout.NORTH);

        JButton btnRegistrar = new JButton("Registrar socio");
        JButton btnVerLista = new JButton("Ver inscritos");
        JButton btnEliminar = new JButton("Eliminar inscripción");

        JPanel btns = new JPanel(new FlowLayout());
        btns.add(btnRegistrar);
        btns.add(btnVerLista);
        btns.add(btnEliminar);

        sesionPanel.add(btns, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> {
            if (auditorio.cuposDisponibles(sesionIndex) == 0) {
                JOptionPane.showMessageDialog(this, "Sesión llena.");
                return;
            }

            String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
            if (id != null && !id.trim().isEmpty()) {
                boolean ok = auditorio.registrar(sesionIndex, id.trim());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Registro exitoso.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo registrar.");
                }
                label.setText("Sesión " + hora + " | Cupos disponibles: " + auditorio.cuposDisponibles(sesionIndex));
            }
        });

        btnVerLista.addActionListener(e -> {
            String[] inscritos = auditorio.getReservas(sesionIndex);
            if (inscritos.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay inscritos aún.");
            } else {
                StringBuilder sb = new StringBuilder("Inscritos en sesión " + hora + ":\n");
                for (String id : inscritos) {
                    sb.append("- ").append(id).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        });
        btnEliminar.addActionListener(e -> {
    String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio a eliminar:");
    if (id != null && !id.trim().isEmpty()) {
        String[] actuales = auditorio.getReservas(sesionIndex);
        boolean encontrado = false;
        for (String s : actuales) {
            if (s.equals(id.trim())) {
                auditorio.cancelar(sesionIndex, id.trim());
                label.setText("Sesión " + hora + " | Cupos disponibles: " + auditorio.cuposDisponibles(sesionIndex));
                JOptionPane.showMessageDialog(this, "Socio eliminado de la sesión.");
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            JOptionPane.showMessageDialog(this, "Ese ID no está inscrito en esta sesión.");
        }
    }
});


        return sesionPanel;
    }
}

