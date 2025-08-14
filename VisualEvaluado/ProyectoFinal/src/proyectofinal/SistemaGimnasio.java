/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
public class SistemaGimnasio {
    // Parqueo
    public char[][] g1, g2, g3;
    public String[][] g1IDs, g2IDs, g3IDs;

    // Clases grupales
    public ClaseGrupal[] clases;

    // Cabinas
    public Cabina cabinas;

    // Auditorio
    public Auditorio auditorio;

    // Sala de pesas
    public SalaPesas salaPesas;

    // Recreación - mesas
    public MesaRecreativa pingPong;
    public MesaRecreativa billar;

    // Recreación - canchas
    public CanchaRecreativa[] futbol;
    public CanchaRecreativa[] baloncesto;
    public CanchaRecreativa[] tenis;

    public SistemaGimnasio() {
        // --- Parqueo ---
        g1 = new char[][] {
            {'L','L','L','L','L'},
            {'L','E','L','L','L'},
            {'L','L','L','L','L'},
            {'D','D','D','L','E'}
        };
        g2 = new char[][] {
            {'L','L','L','L','L'},
            {'L','E','L','L','L'},
            {'L','L','L','L','L'},
            {'L','L','L','L','L'},
            {'D','D','D','L','E'}
        };
        g3 = new char[][] {
            {'L','L','L','L','L'},
            {'L','L','L','L','L'},
            {'L','L','L','L','L'},
            {'L','L','L','L','L'},
            {'L','L','E','L','L'},
            {'D','D','D','L','E'}
        };
        g1IDs = new String[4][5];
        g2IDs = new String[5][5];
        g3IDs = new String[6][5];

        // --- Clases ---
        clases = new ClaseGrupal[] {
            new ClaseGrupal("Yoga", "8:00 a.m.", 10),
            new ClaseGrupal("Crossfit", "10:00 a.m.", 10),
            new ClaseGrupal("Pilates", "6:00 p.m.", 10),
            new ClaseGrupal("Zumba", "7:00 p.m.", 10),
        };

        // --- Cabinas ---
        cabinas = new Cabina(4);

        // --- Auditorio ---
        auditorio = new Auditorio();

        // --- Sala de pesas ---
        salaPesas = new SalaPesas();

        // --- Recreación (mesas) ---
        pingPong = new MesaRecreativa(2);
        billar = new MesaRecreativa(2);

        // --- Recreación (canchas) ---
        futbol = new CanchaRecreativa[] {
            new CanchaRecreativa("Fútbol 1", 12),
            new CanchaRecreativa("Fútbol 2", 12)
        };
        baloncesto = new CanchaRecreativa[] {
            new CanchaRecreativa("Baloncesto", 10)
        };
        tenis = new CanchaRecreativa[] {
            new CanchaRecreativa("Tenis 1", 2),
            new CanchaRecreativa("Tenis 2", 2)
        };
    }
}

