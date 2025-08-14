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

public class ParkingWindow extends JFrame {
    private SistemaGimnasio sistema;
    private final String[][] g1IDs = new String[4][5];
    private final String[][] g2IDs = new String[5][5];
    private final String[][] g3IDs = new String[6][5];
    
    private final char[][] g1 = {
            {'E', 'O', 'L', 'L', 'O'},
            {'L', 'L', 'L', 'L', 'L'},
            {'L', 'O', 'L', 'L', 'L'},
            {'D', 'D', 'D', 'L', 'E'}
    };

    private final char[][] g2 = {
            {'O', 'O', 'L', 'L', 'O'},
            {'L', 'E', 'L', 'L', 'L'},
            {'L', 'O', 'L', 'L', 'L'},
            {'L', 'L', 'L', 'L', 'O'},
            {'D', 'D', 'D', 'O', 'O'}
    };

    private final char[][] g3 = {
            {'O', 'O', 'L', 'L', 'O'},
            {'L', 'L', 'L', 'L', 'L'},
            {'L', 'O', 'L', 'L', 'L'},
            {'L', 'L', 'L', 'L', 'O'},
            {'O', 'O', 'E', 'O', 'O'},
            {'D', 'D', 'D', 'L', 'E'}
    };

    public ParkingWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Gestión de Parqueo");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createParkingPanel(g1, "Nivel G1 (4x5)"));
        mainPanel.add(createParkingPanel(g2, "Nivel G2 (5x5)"));
        mainPanel.add(createParkingPanel(g3, "Nivel G3 (6x5)"));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        setContentPane(scrollPane);
        setVisible(true);
    }

    private JPanel createParkingPanel(char[][] matriz, String titulo) {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel(titulo, JLabel.CENTER);
    label.setFont(new Font("Arial", Font.BOLD, 16));
    panel.add(label, BorderLayout.NORTH);

    // ✅ Declarar la variable grid ANTES de usarla
    JPanel grid = new JPanel(new GridLayout(matriz.length + 1, matriz[0].length + 1));

    // Encabezado de columnas
    grid.add(new JLabel("")); // espacio vacío esquina superior izquierda
    for (int col = 1; col <= matriz[0].length; col++) {
        JLabel colLabel = new JLabel(String.valueOf(col));
        colLabel.setHorizontalAlignment(JLabel.CENTER);
        grid.add(colLabel);
    }

    // Filas de parqueo
    for (int i = 0; i < matriz.length; i++) {
        char rowLabel = (char) ('A' + i);

        JLabel rowLabelLabel = new JLabel(String.valueOf(rowLabel));
        rowLabelLabel.setHorizontalAlignment(JLabel.CENTER);
        grid.add(rowLabelLabel);

        for (int j = 0; j < matriz[0].length; j++) {
            JButton spot = new JButton(String.valueOf(matriz[i][j]));
            setButtonColor(spot, matriz[i][j]);

            final int row = i;
            final int col = j;

            spot.addActionListener(e -> {
                char estadoActual = matriz[row][col];

                switch (estadoActual) {
                    case 'L':
                        String id = JOptionPane.showInputDialog(this,
                            "Ingrese ID del socio para asignar espacio:",
                            "Reserva de parqueo",
                            JOptionPane.QUESTION_MESSAGE);
                        if (id != null && !id.trim().isEmpty()) {
                            matriz[row][col] = 'O';
                            getIDArray(matriz)[row][col] = id.trim();
                            spot.setText("O");
                            setButtonColor(spot, 'O');
                        }
                        break;

                    case 'O':
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "¿Desea liberar este espacio?",
                            "Liberar espacio",
                            JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            matriz[row][col] = 'L';
                            getIDArray(matriz)[row][col] = null;
                            spot.setText("L");
                            setButtonColor(spot, 'L');
                        }
                        break;

                    default:
                        JOptionPane.showMessageDialog(this,
                            "Este espacio está reservado o no es modificable.",
                            "Acción no permitida",
                            JOptionPane.WARNING_MESSAGE);
                        break;
                }
            });

            grid.add(spot);
        }
    }

    panel.add(grid, BorderLayout.CENTER);
    return panel;
}

    private void setButtonColor(JButton button, char estado) {
        switch (estado) {
            case 'L':
                button.setBackground(Color.GREEN);
                break;
            case 'O':
                button.setBackground(Color.RED);
                break;
            case 'D':
                button.setBackground(Color.BLUE);
                button.setForeground(Color.WHITE);
                break;
            case 'E':
                button.setBackground(Color.ORANGE);
                break;
            default:
                button.setBackground(Color.GRAY);
                break;
        }
    }
    private String[][] getIDArray(char[][] matriz) {
    if (matriz == g1) return g1IDs;
    if (matriz == g2) return g2IDs;
    if (matriz == g3) return g3IDs;
    return null;
}
}

