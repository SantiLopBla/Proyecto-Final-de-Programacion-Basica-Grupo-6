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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class MenuPrincipal extends JFrame {

    private final SistemaGimnasio sistema;

    // = Paleta =
    private static final Color BG_DARK = new Color(12, 12, 12);
    private static final Color TXT_LIGHT = new Color(230, 230, 230);
    private static final Color TXT_MUTED = new Color(205, 205, 205);
    private static final Color CARD_1 = new Color(40, 92, 170);
    private static final Color CARD_2 = new Color(8, 115, 102);
    private static final Color CARD_3 = new Color(62, 66, 73);
    private static final Color CARD_4 = new Color(160, 39, 54);
    private static final Color CARD_5 = new Color(96, 56, 145);
    private static final Color CARD_6 = new Color(177, 116, 16);
    private static final Color BTN_WARN = new Color(198, 40, 40);

    // Imagen de fondo
    private static final String IMG_URL
            = "https://img.freepik.com/fotos-premium/pesas-gimnasio-espacio-libre-culturista-borrosa-fondo_144962-14530.jpg";

    // Emojis
    private static final String E_PARQUEO = "🅿️";
    private static final String E_CLASES_P = "🕺";
    private static final String E_CLASES_F = "💃";
    private static final String E_CABINAS_P = "🧘🏻‍♀";
    private static final String E_CABINAS_F = "🧘";
    private static final String E_AUDITORIO = "🗣";
    private static final String E_PESAS = "🏋️";
    private static final String E_RECRE = "🏓";

    public MenuPrincipal(SistemaGimnasio sistema) {
        this.sistema = sistema;

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        setTitle("Zona Elite - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel(IMG_URL);
        bg.setLayout(new BorderLayout(18, 18));
        bg.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(bg);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("ZONA ELITE", SwingConstants.LEFT);
        title.setForeground(TXT_LIGHT);
        title.setFont(vipFont(true, 38f));
        title.setBorder(new EmptyBorder(2, 10, 0, 10));
        header.add(title, BorderLayout.NORTH);

        JLabel slogan = new JLabel("Fuerza, lujo y resultados", SwingConstants.LEFT);
        slogan.setForeground(TXT_MUTED);
        slogan.setFont(vipFont(false, 20f));
        slogan.setBorder(new EmptyBorder(2, 12, 12, 10));
        header.add(slogan, BorderLayout.CENTER);

        bg.add(header, BorderLayout.NORTH);

        // GRID DE TARJETAS
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        String clasesEmoji = emojiSafe(E_CLASES_P, E_CLASES_F, 28f);
        String cabinasEmoji = emojiSafe(E_CABINAS_P, E_CABINAS_F, 28f);

        grid.add(card(E_PARQUEO + "  Parqueo", CARD_1, e -> abrirParqueo()));
        grid.add(card(clasesEmoji + "  Clases Grupales", CARD_2, e -> abrirClases()));
        grid.add(card(cabinasEmoji + "  Cabinas Insonorizadas", CARD_3, e -> abrirCabinas()));
        grid.add(card(E_AUDITORIO + "  Auditorio", CARD_4, e -> abrirAuditorio()));
        grid.add(card(E_PESAS + "  Sala de Pesas", CARD_5, e -> abrirSalaPesas()));

        // Espacios Recreativos -> ahora abre la ventana real
        grid.add(card(E_RECRE + "  Espacios Recreativos", CARD_6, e -> abrirRecreacion()));

        bg.add(grid, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setOpaque(false);
        JButton btnSalir = smallButton("⏏️  Salir", BTN_WARN);
        btnSalir.addActionListener(ev -> System.exit(0));
        footer.add(btnSalir);
        bg.add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    // === ACCIONES ===
    private void abrirParqueo() {
        new ParkingWindow(sistema);
    }

    private void abrirClases() {
        new ClasesWindow(sistema);
    }

    private void abrirCabinas() {
        new CabinasWindow(sistema);
    }

    private void abrirAuditorio() {
        new AuditorioWindow(sistema);
    }

    private void abrirSalaPesas() {
        new SalaPesasWindow(sistema);
    }

    private void abrirRecreacion() {
        new RecreacionWindow(sistema);
    }

    // === BOTONES / UTILIDADES ===
    private JButton card(String text, Color base, ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setFont(emojiFont(28f));
        b.setBorder(new EmptyBorder(24, 26, 24, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(onClick);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(base, 1.10f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(base, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(base, 1.06f));
            }
        });
        return b;
    }

    private JButton smallButton(String text, Color base) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(base);
        b.setForeground(Color.WHITE);
        b.setFont(emojiFont(16f));
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(base, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(base, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(base, 1.06f));
            }
        });
        return b;
    }

    private static Font vipFont(boolean bold, float size) {
        String[] prefs = {
            "Futura", "Futura PT", "FuturaStd", "Montserrat",
            "Bebas Neue", "Oswald", "Anton", "Impact",
            "Arial Black", "Segoe UI Semibold", "SansSerif"
        };
        String fam = findInstalledFont(prefs);
        int style = bold ? Font.BOLD : Font.PLAIN;
        return new Font(fam, style, Math.round(size));
    }

    private static Font emojiFont(float size) {
        String os = System.getProperty("os.name", "").toLowerCase();
        String[] prefs;
        if (os.contains("mac")) {
            prefs = new String[]{"Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", "Segoe UI", "SansSerif"};
        } else if (os.contains("win")) {
            prefs = new String[]{"Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji", "Segoe UI", "SansSerif"};
        } else {
            prefs = new String[]{"Noto Color Emoji", "Segoe UI Emoji", "EmojiOne Color", "DejaVu Sans", "SansSerif"};
        }
        String fam = findInstalledFont(prefs);
        return new Font(fam, Font.PLAIN, Math.round(size));
    }

    private static String findInstalledFont(String[] families) {
        String[] installed = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        for (String candidate : families) {
            if (Arrays.stream(installed).anyMatch(f -> f.equalsIgnoreCase(candidate))) {
                return candidate;
            }
        }
        return "SansSerif";
    }

    private static String emojiSafe(String preferred, String fallback, float size) {
        Font f = emojiFont(size);
        return (f.canDisplayUpTo(preferred) == -1) ? preferred : fallback;
    }

    private Color adjust(Color c, float factor) {
        int r = Math.min(255, Math.max(0, Math.round(c.getRed() * factor)));
        int g = Math.min(255, Math.max(0, Math.round(c.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, Math.round(c.getBlue() * factor)));
        return new Color(r, g, b);
    }

    static class BackgroundPanel extends JPanel {

        private BufferedImage img;

        public BackgroundPanel(String pathOrUrl) {
            setBackground(BG_DARK);
            loadImage(pathOrUrl);
        }

        private void loadImage(String pathOrUrl) {
            img = null;
            if (pathOrUrl == null || pathOrUrl.isBlank()) {
                return;
            }
            try {
                if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
                    img = javax.imageio.ImageIO.read(new URL(pathOrUrl));
                    return;
                }
                try (InputStream in = getClass().getResourceAsStream(pathOrUrl)) {
                    if (in != null) {
                        img = javax.imageio.ImageIO.read(in);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, new Color(18, 18, 18),
                    0, getHeight(), new Color(8, 8, 8));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (img != null) {
                double panelRatio = getWidth() / (double) getHeight();
                double imgRatio = img.getWidth() / (double) img.getHeight();
                int drawW, drawH;
                if (panelRatio > imgRatio) {
                    drawW = getWidth();
                    drawH = (int) (getWidth() / imgRatio);
                } else {
                    drawH = getHeight();
                    drawW = (int) (getHeight() * imgRatio);
                }
                int x = (getWidth() - drawW) / 2, y = (getHeight() - drawH) / 2;
                g2.drawImage(img, x, y, drawW, drawH, null);
                g2.setColor(new Color(0, 0, 0, 140)); // capa oscura
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }
    }

    // MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MenuPrincipal(new SistemaGimnasio());
        });
    }

    // ====== Clase interna: SalaPesasWindow (con el FIX del LineBorder) ======
    public static class SalaPesasWindow extends JFrame {

        private final SistemaGimnasio sistema;
        private final SalaPesas salaPesas;
        private JLabel lblEstado;
        private JLabel lblCupo;
        private final DefaultListModel<String> sociosModel = new DefaultListModel<>();
        private final JList<String> sociosList = new JList<>(sociosModel);

        private static final int MAX_CUPOS = 50;
        private static final Color BTN_COLOR = new Color(68, 68, 68);
        private static final Color BTN_FG = Color.WHITE;
        private static final Color BTN_ROJO = new Color(198, 40, 40);
        private static final Color FONDO_NEGRO = new Color(12, 12, 12);
        private static final Color TEXTO_BLANCO = Color.WHITE;
        private static final Color FONDO_LISTA = new Color(30, 30, 30);
        private static final Color BORDE_LISTA = new Color(180, 180, 180);

        public SalaPesasWindow(SistemaGimnasio sistema) {
            this.sistema = sistema;
            this.salaPesas = sistema.getSalaPesas();
            aplicarTemaOscuroJOptionPane();

            setTitle("Sala de Pesas");
            setSize(800, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            getContentPane().setBackground(FONDO_NEGRO);
            setLayout(new BorderLayout(10, 10));

            // Header
            JPanel headerPanel = new JPanel(new GridLayout(2, 1));
            headerPanel.setBackground(FONDO_NEGRO);
            JLabel lblTitulo = new JLabel("Sala de Pesas", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblTitulo.setForeground(TEXTO_BLANCO);
            JLabel lblEslogan = new JLabel("Movimiento, fuerza y bienestar", SwingConstants.LEFT);
            lblEslogan.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblEslogan.setForeground(Color.LIGHT_GRAY);
            headerPanel.add(lblTitulo);
            headerPanel.add(lblEslogan);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            add(headerPanel, BorderLayout.NORTH);

            lblEstado = new JLabel("Personas actualmente dentro: " + salaPesas.getCantidad(), JLabel.CENTER);
            lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
            lblEstado.setForeground(TEXTO_BLANCO);
            lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            JButton btnIngresar = crearBotonUnificado("Ingresar socio", e -> {
                if (salaPesas.getCantidad() >= MAX_CUPOS) {
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

            JButton btnSalirSocio = crearBotonUnificado("Salir socio", e -> {
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

            JPanel panelBotones = new JPanel(new BorderLayout());
            panelBotones.setBackground(FONDO_NEGRO);
            panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            JPanel buttonsInner = new JPanel(new GridLayout(2, 1, 10, 10));
            buttonsInner.setOpaque(false);
            buttonsInner.add(btnIngresar);
            buttonsInner.add(btnSalirSocio);
            panelBotones.add(lblEstado, BorderLayout.NORTH);
            panelBotones.add(buttonsInner, BorderLayout.CENTER);

            // Panel derecho — Lista + contador
            JPanel panelSocios = new JPanel(new BorderLayout(10, 10));
            panelSocios.setBackground(FONDO_LISTA);

            // 🔧 FIX: quitamos new LineBorder(...) y usamos BorderFactory.createLineBorder(...)
            TitledBorder titled = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(BORDE_LISTA, 3, true),
                    "Socios ingresados",
                    TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION,
                    new Font("Segoe UI", Font.BOLD, 16),
                    TEXTO_BLANCO
            );
            panelSocios.setBorder(titled);

            sociosList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            sociosList.setBackground(FONDO_LISTA);
            sociosList.setForeground(TEXTO_BLANCO);
            sociosList.setSelectionBackground(new Color(212, 172, 13, 150));
            sociosList.setSelectionForeground(Color.BLACK);

            JScrollPane scroll = new JScrollPane(sociosList);
            scroll.setBorder(null);
            scroll.getViewport().setBackground(FONDO_LISTA);

            lblCupo = new JLabel();
            lblCupo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblCupo.setForeground(TEXTO_BLANCO);
            lblCupo.setHorizontalAlignment(SwingConstants.CENTER);
            lblCupo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

            panelSocios.add(scroll, BorderLayout.CENTER);
            panelSocios.add(lblCupo, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelBotones, panelSocios);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerLocation(400);
            splitPane.setBorder(null);
            add(splitPane, BorderLayout.CENTER);

            JButton btnSalir = crearBotonRojo("Salir", e -> dispose());
            JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelFooter.setBackground(FONDO_NEGRO);
            panelFooter.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            panelFooter.add(btnSalir);
            add(panelFooter, BorderLayout.SOUTH);

            actualizarEstado();
            setVisible(true);
        }

        private void actualizarEstado() {
            int cantidadActual = salaPesas.getCantidad();
            lblEstado.setText("Personas actualmente dentro: " + cantidadActual);
            sociosModel.clear();
            for (String socio : salaPesas.getSociosActuales()) {
                sociosModel.addElement(socio);
            }
            int cuposRestantes = MAX_CUPOS - cantidadActual;
            lblCupo.setText("Cupos disponibles: " + cuposRestantes);
        }

        private JButton crearBotonUnificado(String texto, ActionListener al) {
            JButton b = new JButton(texto);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(true);
            b.setBackground(BTN_COLOR);
            b.setForeground(BTN_FG);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addActionListener(al);
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_COLOR, 1.08f));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    b.setBackground(BTN_COLOR);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_COLOR, 0.92f));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_COLOR, 1.06f));
                }
            });
            return b;
        }

        private JButton crearBotonRojo(String texto, ActionListener al) {
            JButton b = crearBotonUnificado(texto, al);
            b.setBackground(BTN_ROJO);
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_ROJO, 1.08f));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    b.setBackground(BTN_ROJO);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_ROJO, 0.92f));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    b.setBackground(adjustColor(BTN_ROJO, 1.06f));
                }
            });
            return b;
        }

        private Color adjustColor(Color color, float factor) {
            int r = Math.min(255, Math.max(0, Math.round(color.getRed() * factor)));
            int g = Math.min(255, Math.max(0, Math.round(color.getGreen() * factor)));
            int b = Math.min(255, Math.max(0, Math.round(color.getBlue() * factor)));
            return new Color(r, g, b);
        }

        private void aplicarTemaOscuroJOptionPane() {
            UIManager.put("OptionPane.background", FONDO_NEGRO);
            UIManager.put("Panel.background", FONDO_NEGRO);
            UIManager.put("OptionPane.messageForeground", TEXTO_BLANCO);
            UIManager.put("Label.foreground", TEXTO_BLANCO);
            UIManager.put("TextField.background", new Color(32, 32, 32));
            UIManager.put("TextField.foreground", TEXTO_BLANCO);
            UIManager.put("TextField.caretForeground", TEXTO_BLANCO);
            UIManager.put("Spinner.background", new Color(32, 32, 32));
            UIManager.put("Spinner.foreground", TEXTO_BLANCO);
            UIManager.put("ComboBox.background", new Color(32, 32, 32));
            UIManager.put("ComboBox.foreground", TEXTO_BLANCO);
        }
    }
}
