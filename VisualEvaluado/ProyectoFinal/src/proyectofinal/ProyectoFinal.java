/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofinal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ProyectoFinal extends JFrame {
    private SistemaGimnasio sistema;
    public ProyectoFinal(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Sistema de Gestión - Gimnasio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Panel con imagen de fondo
        BackgroundPanel background = new BackgroundPanel("proyectofinal.img/imagengym.jpg");
        background.setLayout(new BorderLayout());

        JLabel title = new JLabel("Bienvenido al Sistema del Gimnasio", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE); // Contrasta con fondo
        background.add(title, BorderLayout.NORTH);

        // Menú de botones
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        menuPanel.setOpaque(false); // Dejar transparente para que se vea el fondo
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnParqueo = new JButton("Gestión de Parqueo");
        btnParqueo.addActionListener(e -> new ParkingWindow(sistema));
        JButton btnClases = new JButton("Clases Grupales");
        btnClases.addActionListener(e -> new ClasesWindow(sistema));
        JButton btnCabinas = new JButton("Cabinas Insonorizadas");
        btnCabinas.addActionListener(e -> new CabinasWindow(sistema));
        JButton btnAuditorio = new JButton("Auditorio");
        btnAuditorio.addActionListener(e -> new AuditorioWindow(sistema));
        JButton btnPesas = new JButton("Sala de Pesas");
        btnPesas.addActionListener(e -> new SalaPesasWindow(sistema));
        JButton btnRecreacion = new JButton("Espacios Recreativos");
        btnRecreacion.addActionListener(e -> new RecreacionWindow(sistema));
        JButton btnReservas = new JButton("Ver Reservas");
        btnReservas.addActionListener(e -> new ReservasGeneralesWindow(sistema));
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        menuPanel.add(btnParqueo);
        menuPanel.add(btnClases);
        menuPanel.add(btnCabinas);
        menuPanel.add(btnAuditorio);
        menuPanel.add(btnPesas);
        menuPanel.add(btnRecreacion);
        menuPanel.add(btnReservas);
        menuPanel.add(btnSalir);

        background.add(menuPanel, BorderLayout.CENTER);
        setContentPane(background);

        setVisible(true);
    }

    // Clase interna para fondo con imagen
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SistemaGimnasio sistema = new SistemaGimnasio();
        new ProyectoFinal(sistema);
    }
}

