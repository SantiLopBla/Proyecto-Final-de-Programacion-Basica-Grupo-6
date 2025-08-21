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
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecreacionWindow extends JFrame {

    private SistemaGimnasio sistema;

    private MesaRecreativa pingPong;
    private MesaRecreativa billar;

    // botones visibles + mapa a índices reales de MesaRecreativa
    private JButton[][] botonesPing;
    private JButton[][] botonesBillar;
    private int[] mapPing;   // columnaVisible -> indiceHoraReal
    private int[] mapBill;

    private CanchaRecreativa[] futbol;
    private CanchaRecreativa[] tenis;
    private CanchaRecreativa[] baloncesto;

    // Panel de resumen
    private JPanel resumenContent;

    // Paleta
    private static final Color FONDO_NEGRO = new Color(12, 12, 12);
    private static final Color PANEL_DARK = new Color(20, 20, 20);
    private static final Color PANEL_CARD = new Color(28, 28, 28);
    private static final Color CHIP_BG = new Color(45, 45, 45);
    private static final Color TEXTO_BLANCO = new Color(230, 230, 230);
    private static final Color TEXTO_MUTED = new Color(200, 200, 200);

    // Tabs
    private static final Color TAB_BG = new Color(48, 48, 48);
    private static final Color TAB_BG_ACTIVE = new Color(70, 70, 70);
    private static final Color TAB_TXT = Color.WHITE;

    // Botones secundarios
    private static final Color BTN_COLOR = new Color(68, 68, 68);
    private static final Color BTN_FG = Color.WHITE;

    // Colores de estado para mesas
    private static final Color VERDE_LIBRE = new Color(46, 125, 50);
    private static final Color ROJO_OCUPADO = new Color(198, 40, 40);

    // Estética
    private static final int GRID_GAP = 8;
    private static final int SLOT_RADIUS = 12;
    private static final int LABEL_W = 230; // ancho fijo para que no se corte el texto

    // Horario visible (minutos desde medianoche) — 9:00 AM a 9:00 PM (INCLUSIVO)
    private static final int START_MIN = 9 * 60;  // 9:00 AM
    private static final int END_MIN = 21 * 60;  // 9:00 PM

    // ======= PERSISTENCIA =======
    private static final String ARCHIVO_ESTADO
            = System.getProperty("user.home") + File.separator + ".zonaelite_recreacion.dat";
    private static volatile boolean SHUTDOWN_HOOK_INSTALADO = false;

    private static class Estado implements Serializable {

        String[][] pingPong;
        String[][] billar;
        List<List<String>> futbol;
        List<List<String>> baloncesto;
        List<List<String>> tenis;
    }

    private JTabbedPane tabs;

    public RecreacionWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;

        // Borrar estado al salir COMPLETAMENTE de la app
        instalarShutdownHookBorrado();

        setTitle("Espacios de Recreación");
        setSize(1180, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(FONDO_NEGRO);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // ======= HEADER =======
        root.add(crearHeader(), BorderLayout.NORTH);

        // ======= MODELOS =======
        pingPong = new MesaRecreativa(2);
        billar = new MesaRecreativa(2);

        futbol = new CanchaRecreativa[]{
            new CanchaRecreativa("Fútbol 1", 12),
            new CanchaRecreativa("Fútbol 2", 12)
        };
        baloncesto = new CanchaRecreativa[]{
            new CanchaRecreativa("Baloncesto", 10)
        };
        tenis = new CanchaRecreativa[]{
            new CanchaRecreativa("Tenis 1", 2),
            new CanchaRecreativa("Tenis 2", 2)
        };

        // ======= TABS =======
        tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setBackground(FONDO_NEGRO);
        tabs.setForeground(TEXTO_BLANCO);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void installDefaults() {
                super.installDefaults();
                contentBorderInsets = new Insets(0, 0, 0, 0);
                tabAreaInsets = new Insets(0, 0, 0, 0);
                tabInsets = new Insets(0, 0, 0, 0);
                selectedTabPadInsets = new Insets(0, 0, 0, 0);
                lightHighlight = highlight = shadow = darkShadow = focus = FONDO_NEGRO;
            }
        });

        // Mesas con mapeo 9:00 AM -> 9:00 PM (incluye 9:00 PM)
        JPanel panelPing = crearPanelMesasUniforme(pingPong, "Ping Pong", true);
        JPanel panelBill = crearPanelMesasUniforme(billar, "Billar", false);
        JScrollPane panelCanchas = crearPanelCanchas();
        JScrollPane panelResumen = crearPanelResumen();

        tabs.addTab("", panelPing);
        tabs.addTab("", panelBill);
        tabs.addTab("", panelCanchas);
        tabs.addTab("", panelResumen);

        tabs.setTabComponentAt(0, crearTabHeader("Ping Pong"));
        tabs.setTabComponentAt(1, crearTabHeader("Billar"));
        tabs.setTabComponentAt(2, crearTabHeader("Canchas Deportivas"));
        tabs.setTabComponentAt(3, crearTabHeader("Resumen de reservas"));
        actualizarTabHeaders();
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actualizarTabHeaders();
            }
        });

        root.add(tabs, BorderLayout.CENTER);

        // ======= FOOTER =======
        JButton btnSalir = crearBotonRojoPuro("Salir", e -> {
            guardarEstado();
            dispose();
        });
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFooter.setOpaque(false);
        panelFooter.add(btnSalir);
        root.add(panelFooter, BorderLayout.SOUTH);

        // ======= ESTADO =======
        cargarEstado();
        if (botonesPing != null) {
            actualizarBotones(botonesPing, pingPong, mapPing);
        }
        if (botonesBillar != null) {
            actualizarBotones(botonesBillar, billar, mapBill);
        }
        actualizarResumen(); // inicial

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                guardarEstado();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                guardarEstado();
            }
        });

        setVisible(true);
    }

    // --- borra el archivo SOLO cuando la JVM termina (salida total del programa) ---
    private void instalarShutdownHookBorrado() {
        if (SHUTDOWN_HOOK_INSTALADO) {
            return;
        }
        SHUTDOWN_HOOK_INSTALADO = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Path p = Path.of(ARCHIVO_ESTADO);
                if (Files.exists(p)) {
                    Files.delete(p);
                }
            } catch (Exception ignored) {
            }
        }, "Recreacion-ResetOnExit"));
    }

    // ---------- Tabs ----------
    private JComponent crearTabHeader(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TAB_TXT);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(true);
        p.setBorder(new EmptyBorder(8, 16, 8, 16));
        p.add(lbl);
        p.putClientProperty("tabLabel", lbl);
        return p;
    }

    private void actualizarTabHeaders() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component c = tabs.getTabComponentAt(i);
            if (c instanceof JPanel p) {
                boolean selected = i == tabs.getSelectedIndex();
                p.setBackground(selected ? TAB_BG_ACTIVE : TAB_BG);
                JLabel lbl = (JLabel) p.getClientProperty("tabLabel");
                if (lbl != null) {
                    lbl.setForeground(TAB_TXT);
                }
            }
        }
    }

    // ---------- HEADER ----------
    private JComponent crearHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(true);
        header.setBackground(FONDO_NEGRO);
        header.setBorder(new EmptyBorder(6, 8, 6, 8));

        JLabel titulo = new JLabel("Espacios de Recreación", SwingConstants.LEFT);
        titulo.setForeground(TEXTO_BLANCO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel slogan = new JLabel("Tu recreo, tu privilegio", SwingConstants.LEFT);
        slogan.setForeground(TEXTO_MUTED);
        // ¡Aquí lo inclinamos en cursiva!
        slogan.setFont(new Font("SansSerif", Font.ITALIC, 15));

        header.add(titulo);
        header.add(slogan);
        return header;
    }

    // ---------- CANCHAS ----------
    private JScrollPane crearPanelCanchas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FONDO_NEGRO);
        panel.setBorder(new EmptyBorder(10, 14, 10, 14));

        panel.add(seccionTitulo("Fútbol"));
        for (CanchaRecreativa c : futbol) {
            panel.add(panelCancha(c));
        }

        panel.add(Box.createVerticalStrut(10));
        panel.add(seccionTitulo("Baloncesto"));
        for (CanchaRecreativa c : baloncesto) {
            panel.add(panelCancha(c));
        }

        panel.add(Box.createVerticalStrut(10));
        panel.add(seccionTitulo("Tenis"));
        for (CanchaRecreativa c : tenis) {
            panel.add(panelCancha(c));
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getViewport().setBackground(FONDO_NEGRO);
        return sp;
    }

    private JLabel seccionTitulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(TEXTO_BLANCO);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setBorder(new EmptyBorder(0, 4, 6, 0));
        return l;
    }

    private JPanel panelCancha(CanchaRecreativa cancha) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_DARK);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel label = new JLabel(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
        label.setForeground(TEXTO_BLANCO);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setBorder(new EmptyBorder(2, 2, 6, 2));

        JButton btnAgregar = crearBtnSecundario("Agregar socio", () -> {
            if (cancha.getDisponibles() == 0) {
                JOptionPane.showMessageDialog(this, darkHtml("Cancha llena."), "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String id = inputParqueo("Agregar", "Ingrese ID del socio:");
            if (id != null) {
                String idNorm = id.trim();
                // === Validación: no permitir usuarios repetidos en la misma cancha ===
                if (yaExisteEnCancha(cancha, idNorm)) {
                    JOptionPane.showMessageDialog(
                            this,
                            darkHtml("Este usuario ya ha sido registrado."),
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                // Si pasa la validación, procedemos con el registro normal
                if (cancha.registrar(idNorm)) {
                    JOptionPane.showMessageDialog(this, darkHtml("Socio registrado."), "OK", JOptionPane.INFORMATION_MESSAGE);
                    label.setText(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
                    guardarEstado();
                    actualizarResumen();
                } else {
                    // Fallback defensivo (por si la clase interna también valida duplicados)
                    JOptionPane.showMessageDialog(
                            this,
                            darkHtml("Este usuario ya ha sido registrado."),
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });

        JButton btnEliminar = crearBtnSecundario("Eliminar socio", () -> {
            String id = inputParqueo("Eliminar", "Ingrese ID del socio:");
            if (id != null) {
                if (cancha.cancelar(id)) {
                    JOptionPane.showMessageDialog(this, darkHtml("Socio eliminado."), "OK", JOptionPane.INFORMATION_MESSAGE);
                    label.setText(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
                    guardarEstado();
                    actualizarResumen();
                } else {
                    JOptionPane.showMessageDialog(this, darkHtml("No está registrado."), "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton btnVer = crearBtnSecundario("Ver jugadores", () -> {
            String[] jugadores = cancha.getJugadores();
            if (jugadores.length == 0) {
                JOptionPane.showMessageDialog(this, darkHtml("No hay jugadores registrados."), "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder("<html><div style='color:#e6e6e6;'>Jugadores en ")
                    .append(cancha.tipo).append(":<br>");
            for (String s : jugadores) {
                sb.append("&bull; ").append(s).append("<br>");
            }
            sb.append("</div></html>");
            JOptionPane.showMessageDialog(this, sb.toString(), "Jugadores", JOptionPane.PLAIN_MESSAGE);
        });

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(btnAgregar);
        botones.add(btnEliminar);
        botones.add(btnVer);

        p.add(label, BorderLayout.NORTH);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    // ---------- MESAS (columna fija ancha + grid de horarios) ----------
    private JPanel crearPanelMesasUniforme(MesaRecreativa mesa, String tipo, boolean esPingPong) {
        // mapear columnas visibles (9:00 AM -> 9:00 PM, INCLUSIVO)
        int[] map = construirMapaHoras(mesa.horarios);
        String[] horas12 = formatearHoras12(mesa.horarios, map);

        // botones visibles (filas: #mesas, columnas: horas visibles)
        JButton[][] botones = new JButton[mesa.getCantidadMesas()][map.length];

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(FONDO_NEGRO);

        // Header de horas
        JPanel headerRow = new JPanel(new BorderLayout(GRID_GAP, GRID_GAP));
        headerRow.setOpaque(false);
        JLabel esquina = etiqueta("", 14, TEXTO_BLANCO, SwingConstants.CENTER);
        esquina.setPreferredSize(new Dimension(LABEL_W, 24));
        headerRow.add(esquina, BorderLayout.WEST);

        JPanel horasGrid = new JPanel(new GridLayout(1, horas12.length, GRID_GAP, GRID_GAP));
        horasGrid.setOpaque(false);
        for (String h : horas12) {
            JLabel lbl = etiqueta(h, 13, TEXTO_BLANCO, SwingConstants.CENTER);
            lbl.setToolTipText("Turno de 30 minutos");
            horasGrid.add(lbl);
        }
        headerRow.add(horasGrid, BorderLayout.CENTER);

        // Cuerpo: filas por mesa
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(4, 0, 0, 0));

        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            JPanel row = new JPanel(new BorderLayout(GRID_GAP, GRID_GAP));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(0, 0, GRID_GAP, 0));

            JLabel lblMesa = etiqueta(nombreMesa(tipo, i), 14, TEXTO_BLANCO, SwingConstants.LEFT);
            lblMesa.setPreferredSize(new Dimension(LABEL_W, 28));
            row.add(lblMesa, BorderLayout.WEST);

            JPanel slots = new JPanel(new GridLayout(1, horas12.length, GRID_GAP, GRID_GAP));
            slots.setOpaque(false);

            for (int j = 0; j < map.length; j++) {
                int horaReal = map[j];

                SlotButton btn = new SlotButton("-");
                btn.setBackground(VERDE_LIBRE);
                btn.setForeground(Color.WHITE);
                btn.setToolTipText("Turno de 30 minutos");
                botones[i][j] = btn;

                final int mesaIndex = i, horaIndexReal = horaReal;
                btn.addActionListener(e -> {
                    String actual = mesa.getReserva(mesaIndex, horaIndexReal);
                    if (actual == null) {
                        String id = inputParqueo("Reservar", "Ingrese ID del socio:");
                        if (id != null) {
                            mesa.reservar(mesaIndex, horaIndexReal, id);
                            if (esPingPong) {
                                actualizarBotones(botonesPing, pingPong, mapPing);
                            } else {
                                actualizarBotones(botonesBillar, billar, mapBill);
                            }
                            guardarEstado();
                            actualizarResumen();
                        }
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                darkHtml("¿Liberar esta reserva?"), "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            mesa.liberar(mesaIndex, horaIndexReal);
                            if (esPingPong) {
                                actualizarBotones(botonesPing, pingPong, mapPing);
                            } else {
                                actualizarBotones(botonesBillar, billar, mapBill);
                            }
                            guardarEstado();
                            actualizarResumen();
                        }
                    }
                });

                slots.add(btn);
            }

            row.add(slots, BorderLayout.CENTER);
            body.add(row);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(headerRow, BorderLayout.NORTH);
        wrapper.add(body, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane sp = new JScrollPane(wrapper);
        sp.setBorder(null);
        sp.getViewport().setBackground(FONDO_NEGRO);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        cont.add(sp, BorderLayout.CENTER);

        // Guardar referencias según tablero
        if (esPingPong) {
            this.botonesPing = botones;
            this.mapPing = map;
        } else {
            this.botonesBillar = botones;
            this.mapBill = map;
        }

        return cont;
    }

    // ---------- RESUMEN ----------
    private JScrollPane crearPanelResumen() {
        resumenContent = new JPanel();
        resumenContent.setLayout(new BoxLayout(resumenContent, BoxLayout.Y_AXIS));
        resumenContent.setOpaque(false);
        resumenContent.setBorder(new EmptyBorder(12, 14, 12, 14));

        JScrollPane sp = new JScrollPane(resumenContent);
        sp.setBorder(null);
        sp.getViewport().setBackground(FONDO_NEGRO);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void actualizarResumen() {
        if (resumenContent == null) {
            return;
        }
        resumenContent.removeAll();

        // Card: Mesas Ping-Pong
        List<String> itemsPP = listarReservasMesa(pingPong, "Mesa de ping-pong", mapPing);
        resumenContent.add(card("Mesas de Ping-pong", itemsPP));

        // Card: Mesas Billar
        List<String> itemsBill = listarReservasMesa(billar, "Mesa de Billar", mapBill);
        resumenContent.add(Box.createVerticalStrut(10));
        resumenContent.add(card("Mesas de Billar", itemsBill));

        // Card: Canchas (todas)
        List<String> itemsCanchas = new ArrayList<>();
        listarCanchaResumen(itemsCanchas, futbol);
        listarCanchaResumen(itemsCanchas, baloncesto);
        listarCanchaResumen(itemsCanchas, tenis);

        resumenContent.add(Box.createVerticalStrut(10));
        resumenContent.add(card("Canchas", itemsCanchas));

        if (itemsPP.isEmpty() && itemsBill.isEmpty() && itemsCanchas.isEmpty()) {
            JLabel vacio = new JLabel("No hay reservas activas.", SwingConstants.LEFT);
            vacio.setForeground(TEXTO_MUTED);
            vacio.setFont(new Font("SansSerif", Font.ITALIC, 14));
            vacio.setBorder(new EmptyBorder(8, 2, 0, 2));
            resumenContent.add(Box.createVerticalStrut(8));
            resumenContent.add(vacio);
        }

        resumenContent.revalidate();
        resumenContent.repaint();
    }

    private List<String> listarReservasMesa(MesaRecreativa mesa, String nombreBase, int[] map) {
        List<String> items = new ArrayList<>();
        if (mesa == null || map == null) {
            return items;
        }
        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            for (int j = 0; j < map.length; j++) {
                int idxReal = map[j];
                String socio = mesa.getReserva(i, idxReal);
                if (socio != null) {
                    String hora = to12h(mesa.horarios[idxReal]);
                    items.add(nombreBase + " #" + (i + 1) + "  —  " + hora + "  —  Socio: " + socio);
                }
            }
        }
        return items;
    }

    private void listarCanchaResumen(List<String> out, CanchaRecreativa[] grupo) {
        if (grupo == null) {
            return;
        }
        for (CanchaRecreativa c : grupo) {
            String[] jugadores = c.getJugadores();
            int reservados = jugadores.length;
            int disponibles = c.getDisponibles();
            int total = reservados + disponibles;
            String cupos = "Cupos disponibles: " + disponibles + "/" + total;
            if (reservados == 0) {
                out.add(c.tipo + " — " + cupos + " — Socios: (ninguno)");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(c.tipo).append(" — ").append(cupos).append(" — Socios: ");
                for (int i = 0; i < jugadores.length; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(jugadores[i]);
                }
                out.add(sb.toString());
            }
        }
    }

    private JPanel card(String titulo, List<String> lineas) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_CARD);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel t = new JLabel(titulo);
        t.setForeground(TEXTO_BLANCO);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setOpaque(false);

        if (lineas.isEmpty()) {
            JLabel l = new JLabel("Sin registros.", SwingConstants.LEFT);
            l.setForeground(TEXTO_MUTED);
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            l.setBorder(new EmptyBorder(4, 2, 2, 2));
            lista.add(l);
        } else {
            for (String s : lineas) {
                lista.add(chip(s));
                lista.add(Box.createVerticalStrut(6));
            }
        }

        card.add(t, BorderLayout.NORTH);
        card.add(lista, BorderLayout.CENTER);
        return wrapRounded(card, 16);
    }

    private JPanel chip(String texto) {
        JPanel chip = new JPanel(new BorderLayout());
        chip.setOpaque(true);
        chip.setBackground(CHIP_BG);
        chip.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lbl = new JLabel("• " + texto);
        lbl.setForeground(new Color(235, 235, 235));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        chip.add(lbl, BorderLayout.CENTER);
        return wrapRounded(chip, 12);
    }

    private JPanel wrapRounded(JComponent inner, int radius) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBackground(inner.getBackground());
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    // ---------- Helpers Mesas ----------
    private int[] construirMapaHoras(String[] horarios) {
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < horarios.length; i++) {
            int m = parseMinutes(horarios[i]);
            if (m >= 0 && m >= START_MIN && m <= END_MIN) { // INCLUSIVO hasta las 9:00 PM
                idx.add(i);
            }
        }
        if (idx.isEmpty()) {
            for (int i = 0; i < horarios.length; i++) {
                idx.add(i);
            }
        }
        int[] map = new int[idx.size()];
        for (int i = 0; i < map.length; i++) {
            map[i] = idx.get(i);
        }
        return map;
    }

    private String[] formatearHoras12(String[] horarios, int[] map) {
        String[] out = new String[map.length];
        for (int i = 0; i < map.length; i++) {
            out[i] = to12h(horarios[map[i]]);
        }
        return out;
    }

    // Convierte "HH:mm"/"H:mm"/"h:mm a" -> "h:mm AM/PM"
    private String to12h(String s) {
        int m = parseMinutes(s);
        if (m < 0) {
            return s;
        }
        int h24 = m / 60;
        int mm = m % 60;
        boolean pm = h24 >= 12;
        int h12 = h24 % 12;
        if (h12 == 0) {
            h12 = 12;
        }
        return String.format("%d:%02d %s", h12, mm, pm ? "PM" : "AM");
    }

    // Parsea "HH:mm", "H:mm", "hh:mm a"/"h:mm a"
    private int parseMinutes(String t) {
        try {
            String s = t.trim().replaceAll("\\s+", " ");
            boolean hasAmPm = s.toLowerCase().endsWith("am") || s.toLowerCase().endsWith("pm");
            String timePart = s;
            boolean pm = false;
            if (hasAmPm) {
                pm = s.toLowerCase().endsWith("pm");
                timePart = s.substring(0, s.length() - 2).trim();
            }
            String[] parts = timePart.split(":");
            if (parts.length != 2) {
                return -1;
            }
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].trim());
            if (hasAmPm) {
                h = (h % 12) + (pm ? 12 : 0);
            }
            if (h < 0 || h > 23 || m < 0 || m > 59) {
                return -1;
            }
            return h * 60 + m;
        } catch (Exception e) {
            return -1;
        }
    }

    private String nombreMesa(String tipo, int indexCero) {
        int n = indexCero + 1;
        if ("Ping Pong".equalsIgnoreCase(tipo)) {
            return "Mesa de ping-pong #" + n;
        }
        if ("Billar".equalsIgnoreCase(tipo)) {
            return "Mesa de Billar #" + n;
        }
        return tipo + " " + n;
    }

    private void actualizarBotones(JButton[][] botones, MesaRecreativa mesa, int[] map) {
        if (botones == null || map == null) {
            return;
        }
        for (int i = 0; i < botones.length; i++) {
            for (int j = 0; j < botones[i].length; j++) {
                int horaReal = map[j];
                String reserva = mesa.getReserva(i, horaReal);
                JButton btn = botones[i][j];
                if (reserva == null) {
                    btn.setText("-");
                    btn.setBackground(VERDE_LIBRE);
                    btn.setForeground(Color.WHITE);
                    btn.setToolTipText("Disponible (30 min)");
                } else {
                    btn.setText("Ocupado");
                    btn.setBackground(ROJO_OCUPADO);
                    btn.setForeground(Color.WHITE);
                    btn.setToolTipText("ID: " + reserva);
                }
            }
        }
    }

    // ---------- UTIL/UI ----------
    private JLabel etiqueta(String txt, int size, Color color, int align) {
        JLabel l = new JLabel(txt, align);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }

    private JButton crearBtnSecundario(String texto, Runnable onClick) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(BTN_COLOR);
        b.setForeground(BTN_FG);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> onClick.run());
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

    private JButton crearBotonRojoPuro(String texto, java.awt.event.ActionListener al) {
        JButton b = crearBtnSecundario(texto, () -> {
        });
        for (ActionListener l : b.getActionListeners()) {
            b.removeActionListener(l);
        }
        b.addActionListener(al);
        b.setBackground(Color.RED);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(255, 77, 77));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(Color.RED);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(new Color(200, 0, 0));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(new Color(255, 77, 77));
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

    private String darkHtml(String s) {
        return "<html><div style='color:#e6e6e6; font-family:Sans-Serif; font-size:12px; width:360px;'>" + s + "</div></html>";
    }

    // Input estilo "parqueo": cuadro BLANCO, texto NEGRO, borde y padding
    private String inputParqueo(String titulo, String mensaje) {
        JTextField field = new JTextField(20);
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        field.setSelectionColor(new Color(210, 224, 255));
        field.setSelectedTextColor(Color.BLACK);
        field.setMargin(new Insets(6, 8, 6, 8));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));

        JLabel lbl = new JLabel(mensaje);
        lbl.setForeground(TEXTO_BLANCO);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(FONDO_NEGRO);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(this, p, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            String txt = field.getText();
            if (txt != null) {
                txt = txt.trim();
                return txt.isEmpty() ? null : txt;
            }
        }
        return null;
    }

    // ======= GUARDAR / CARGAR =======
    private void guardarEstado() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ESTADO))) {
            Estado st = new Estado();
            st.pingPong = snapshotMesa(pingPong);
            st.billar = snapshotMesa(billar);
            st.futbol = snapshotCanchas(futbol);
            st.baloncesto = snapshotCanchas(baloncesto);
            st.tenis = snapshotCanchas(tenis);
            out.writeObject(st);
        } catch (Exception ignored) {
        }
    }

    private void cargarEstado() {
        File f = new File(ARCHIVO_ESTADO);
        if (!f.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = in.readObject();
            if (!(obj instanceof Estado st)) {
                return;
            }

            aplicarMesa(pingPong, st.pingPong);
            aplicarMesa(billar, st.billar);
            aplicarCanchas(futbol, st.futbol);
            aplicarCanchas(baloncesto, st.baloncesto);
            aplicarCanchas(tenis, st.tenis);
        } catch (Exception ignored) {
        }
    }

    private String[][] snapshotMesa(MesaRecreativa mesa) {
        String[][] m = new String[mesa.getCantidadMesas()][mesa.getCantidadHoras()];
        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            for (int j = 0; j < mesa.getCantidadHoras(); j++) {
                m[i][j] = mesa.getReserva(i, j);
            }
        }
        return m;
    }

    private void aplicarMesa(MesaRecreativa mesa, String[][] m) {
        if (m == null) {
            return;
        }
        for (int i = 0; i < Math.min(mesa.getCantidadMesas(), m.length); i++) {
            for (int j = 0; j < Math.min(mesa.getCantidadHoras(), m[i].length); j++) {
                String id = m[i][j];
                if (id != null && !id.isEmpty() && mesa.getReserva(i, j) == null) {
                    mesa.reservar(i, j, id);
                }
            }
        }
    }

    private List<List<String>> snapshotCanchas(CanchaRecreativa[] grupo) {
        List<List<String>> data = new ArrayList<>();
        for (CanchaRecreativa c : grupo) {
            List<String> jugadores = new ArrayList<>();
            for (String s : c.getJugadores()) {
                jugadores.add(s);
            }
            data.add(jugadores);
        }
        return data;
    }

    private void aplicarCanchas(CanchaRecreativa[] grupo, List<List<String>> data) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < Math.min(grupo.length, data.size()); i++) {
            for (String id : data.get(i)) {
                if (id != null && !id.isEmpty()) {
                    grupo[i].registrar(id);
                }
            }
        }
    }

    // ---- Botón con esquinas redondeadas y relleno completo ----
    private static class SlotButton extends JButton {

        SlotButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);                // lo pinto manualmente
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(12, 14, 12, 14));
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), SLOT_RADIUS, SLOT_RADIUS);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // ====== Helper de validación para canchas (IDs únicos) ======
    private boolean yaExisteEnCancha(CanchaRecreativa cancha, String id) {
        if (id == null) {
            return false;
        }
        String idNorm = id.trim();
        for (String s : cancha.getJugadores()) {
            if (s != null && s.trim().equalsIgnoreCase(idNorm)) {
                return true;
            }
        }
        return false;
    }
}
