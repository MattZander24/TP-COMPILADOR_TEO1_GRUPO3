package ar.edu.unlu.compilador;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java_cup.runtime.Symbol;
import java.util.Map;

public class IDE extends JFrame {
    private JTextArea textAreaCodigo;
    private JTextArea textAreaSalida;
    private JTextArea textAreaTS;
    private File archivoActual;

    public IDE() {
        setTitle("Compilador - Teoría de la Computación I - Grupo 3");
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

        try {
            Lexico lexer = new Lexico(new StringReader(codigo));
            Symbol token;
            int tokenCount = 0;
            int errorCount = 0;

            StringBuilder salida = new StringBuilder();
            salida.append("=== TOKENS ===\n");

            while ((token = lexer.next_token()).sym != sym.EOF) {
                if (token.sym == sym.error) {
                    errorCount++;
                    for (String error : lexer.errores) {
                        salida.append(error).append("\n");
                    }
                    lexer.errores.clear();
                } else {
                    tokenCount++;
                    salida.append("[").append(token.left).append(":").append(token.right).append("] ")
                           .append(symToString(token.sym)).append(" -> ").append(token.value).append("\n");
                }
            }

            salida.append("\n=== RESUMEN ===\n");
            salida.append("Tokens reconocidos: ").append(tokenCount).append("\n");
            salida.append("Errores léxicos: ").append(errorCount).append("\n");

            textAreaSalida.setText(salida.toString());

            // Mostrar tabla de símbolos
            StringBuilder tsSalida = new StringBuilder();
            tsSalida.append("NOMBRE      TOKEN       TIPO  VALOR   LONG\n");
            for (Map.Entry<String, Simbolo> entry : lexer.tablaSimbolos.getEntries()) {
                tsSalida.append(entry.getValue().toString()).append("\n");
            }
            textAreaTS.setText(tsSalida.toString());

            // Generar ts.txt
            lexer.tablaSimbolos.escribirArchivo("ts.txt");

        } catch (Exception ex) {
            textAreaSalida.setText("Error durante la compilación: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String symToString(int tokenSym) {
        if (tokenSym == sym.DECLARE) return "DECLARE";
        if (tokenSym == sym.ENDDECLARE) return "ENDDECLARE";
        if (tokenSym == sym.PROGRAM) return "PROGRAM";
        if (tokenSym == sym.ENDPROGRAM) return "ENDPROGRAM";
        if (tokenSym == sym.IF) return "IF";
        if (tokenSym == sym.THEN) return "THEN";
        if (tokenSym == sym.ELSE) return "ELSE";
        if (tokenSym == sym.ENDIF) return "ENDIF";
        if (tokenSym == sym.WHILE) return "WHILE";
        if (tokenSym == sym.ENDWHILE) return "ENDWHILE";
        if (tokenSym == sym.WRITE) return "WRITE";
        if (tokenSym == sym.FLOAT_T) return "FLOAT";
        if (tokenSym == sym.INTEGER_T) return "INTEGER";
        if (tokenSym == sym.STRING_T) return "STRING";
        if (tokenSym == sym.AND) return "AND";
        if (tokenSym == sym.OR) return "OR";
        if (tokenSym == sym.POSITION) return "POSITION";
        if (tokenSym == sym.ASIGNACION) return "ASIGNACION";
        if (tokenSym == sym.ASIG_DECL) return "ASIG_DECL";
        if (tokenSym == sym.OP_IGUAL) return "OP_IGUAL";
        if (tokenSym == sym.OP_DISTINTO) return "OP_DISTINTO";
        if (tokenSym == sym.OP_MENOR_IGUAL) return "OP_MENOR_IGUAL";
        if (tokenSym == sym.OP_MAYOR_IGUAL) return "OP_MAYOR_IGUAL";
        if (tokenSym == sym.OP_MENOR) return "OP_MENOR";
        if (tokenSym == sym.OP_MAYOR) return "OP_MAYOR";
        if (tokenSym == sym.OP_SUMA) return "OP_SUMA";
        if (tokenSym == sym.OP_RESTA) return "OP_RESTA";
        if (tokenSym == sym.OP_MULT) return "OP_MULT";
        if (tokenSym == sym.OP_DIV) return "OP_DIV";
        if (tokenSym == sym.COMA) return "COMA";
        if (tokenSym == sym.PUNTO) return "PUNTO";
        if (tokenSym == sym.PUNTO_Y_COMA) return "PUNTO_Y_COMA";
        if (tokenSym == sym.PAR_A) return "PAR_A";
        if (tokenSym == sym.PAR_C) return "PAR_C";
        if (tokenSym == sym.COR_A) return "COR_A";
        if (tokenSym == sym.COR_C) return "COR_C";
        if (tokenSym == sym.LLAV_A) return "LLAV_A";
        if (tokenSym == sym.LLAV_C) return "LLAV_C";
        if (tokenSym == sym.ID) return "ID";
        if (tokenSym == sym.CTE_INT) return "CTE_INT";
        if (tokenSym == sym.CTE_FLOAT) return "CTE_FLOAT";
        if (tokenSym == sym.STRING) return "STRING";
        return "UNKNOWN";
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
