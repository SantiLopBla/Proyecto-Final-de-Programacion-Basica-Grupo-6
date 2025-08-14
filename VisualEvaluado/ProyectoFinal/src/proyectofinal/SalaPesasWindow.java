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

public class SalaPesasWindow extends JFrame {
    private SistemaGimnasio sistema;
    private SalaPesas salaPesas;
    private JLabel lblEstado;

    public SalaPesasWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Sala de Pesas");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        salaPesas = new SalaPesas();

        lblEstado = new JLabel("Personas actualmente dentro: 0", JLabel.CENTER);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblEstado, BorderLayout.NORTH);

        JButton btnIngresar = new JButton("Ingresar socio");
        JButton btnSalir = new JButton("Salir socio");
        JButton btnVerLista = new JButton("Ver lista de socios");

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);
        panelBotones.add(btnVerLista);

        add(panelBotones, BorderLayout.CENTER);

        btnIngresar.addActionListener(e -> {
            if (salaPesas.getCantidad() >= 50) {
                JOptionPane.showMessageDialog(this, "Sala llena. No se puede ingresar más.");
                return;
            }
            String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
            if (id != null && !id.trim().isEmpty()) {
                if (salaPesas.ingresar(id.trim())) {
                    JOptionPane.showMessageDialog(this, "Ingreso registrado.");
                } else {
                    JOptionPane.showMessageDialog(this, "Ese socio ya está dentro.");
                }
                actualizarEstado();
            }
        });

        btnSalir.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
            if (id != null && !id.trim().isEmpty()) {
                if (salaPesas.salir(id.trim())) {
                    JOptionPane.showMessageDialog(this, "Salida registrada.");
                } else {
                    JOptionPane.showMessageDialog(this, "Socio no encontrado.");
                }
                actualizarEstado();
            }
        });

        btnVerLista.addActionListener(e -> {
            String[] socios = salaPesas.getSociosActuales();
            if (socios.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay nadie en la sala.");
                return;
            }
            StringBuilder sb = new StringBuilder("Socios en la sala:\n");
            for (String s : socios) {
                sb.append("- ").append(s).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        });

        setVisible(true);
    }

    private void actualizarEstado() {
        lblEstado.setText("Personas actualmente dentro: " + salaPesas.getCantidad());
    }
}

