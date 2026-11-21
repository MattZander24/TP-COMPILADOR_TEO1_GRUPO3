package ar.edu.unlu.compilador;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Map;

public class IDE extends JFrame {
    private JTextArea textAreaCodigo;
    private JTextArea textAreaSalida;
    private JTextArea textAreaTS;
    private File archivoActual;

    public IDE() {
        setTitle("Compilador - Teoría de la Computación I - Grupo 3 - Entrega 2 (Analizador Sintáctico)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel de código y salida
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAbrir = new JButton("Abrir");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCompilar = new JButton("Compilar");

        btnAbrir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirArchivo();
            }
        });

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarArchivo();
            }
        });

        btnCompilar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilar();
            }
        });

        panelBotones.add(btnAbrir);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCompilar);

        // Área de código
        textAreaCodigo = new JTextArea();
        textAreaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollCodigo = new JScrollPane(textAreaCodigo);
        scrollCodigo.setPreferredSize(new Dimension(600, 400));

        // Panel dividido horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollCodigo, null);
        splitPane.setResizeWeight(0.6);

        // Área de salida
        textAreaSalida = new JTextArea();
        textAreaSalida.setEditable(false);
        textAreaSalida.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollSalida = new JScrollPane(textAreaSalida);
        scrollSalida.setPreferredSize(new Dimension(600, 200));

        splitPane.setBottomComponent(scrollSalida);

        panelPrincipal.add(panelBotones, BorderLayout.NORTH);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        tabbedPane.addTab("Código", panelPrincipal);

        // Panel de tabla de símbolos
        textAreaTS = new JTextArea();
        textAreaTS.setEditable(false);
        textAreaTS.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollTS = new JScrollPane(textAreaTS);
        tabbedPane.addTab("Tabla de Símbolos", scrollTS);

        add(tabbedPane);
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
        
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoActual))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                textAreaCodigo.setText(contenido.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        if (archivoActual == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
            
            int resultado = fileChooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile();
                if (!archivoActual.getName().endsWith(".txt")) {
                    archivoActual = new File(archivoActual.getPath() + ".txt");
                }
            } else {
                return;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoActual))) {
            writer.write(textAreaCodigo.getText());
            JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compilar() {
        String codigo = textAreaCodigo.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay código para compilar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        textAreaSalida.setText("");
        textAreaTS.setText("");

        Lexico lexer = null;
        Sintactico parser = null;

        try {
            lexer = new Lexico(new StringReader(codigo));
            parser = new Sintactico(lexer);
            parser.parse();
        } catch (Exception ex) {
            // El error se informa en el panel de salida; la IDE no se cuelga.
            if (parser == null) {
                textAreaSalida.setText("Error durante la compilación: " + ex.getMessage());
                return;
            }
        }

        StringBuilder salida = new StringBuilder();

        // 1. Reglas y mensajes [POSITION] en orden de reducción
        for (String linea : parser.getReglas()) {
            salida.append(linea).append("\n");
        }

        // 2. Errores (léxicos + sintácticos/léxicos del parser + semánticos)
        for (String error : lexer.errores) {
            salida.append(error).append("\n");
        }
        for (String error : parser.getErroresSintacticos()) {
            salida.append(error).append("\n");
        }
        java.util.List<String> semanticos = parser.getErroresSemanticos();
        for (String error : semanticos) {
            salida.append(error).append("\n");
        }

        // 3. Resumen
        int totalErrores = lexer.errores.size()
                + parser.getErroresSintacticos().size()
                + semanticos.size();
        if (totalErrores == 0) {
            salida.append("Compilación exitosa\n");
        } else {
            salida.append("Compilación con ").append(totalErrores).append(" error(es)\n");
        }

        textAreaSalida.setText(salida.toString());

        // Tabla de símbolos (con tipos en los IDs)
        StringBuilder tsSalida = new StringBuilder();
        tsSalida.append("NOMBRE      TOKEN       TIPO  VALOR   LONG\n");
        for (Map.Entry<String, Simbolo> entry : lexer.tablaSimbolos.getEntries()) {
            tsSalida.append(entry.getValue().toString()).append("\n");
        }
        textAreaTS.setText(tsSalida.toString());

        // Generar ts.txt
        try {
            lexer.tablaSimbolos.escribirArchivo("ts.txt");
        } catch (IOException ex) {
            // No es crítico para la IDE
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IDE ide = new IDE();
                ide.setVisible(true);
            }
        });
    }
}
