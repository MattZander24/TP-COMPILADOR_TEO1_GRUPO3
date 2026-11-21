package ar.edu.unlu.compilador;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Compilador - Teoría de la Computación I - Grupo 3 - Entrega 2 (Analizador Sintáctico)");
            System.out.println("Uso: java -cp dist/compilador.jar ar.edu.unlu.compilador.Main <archivo>");
            return;
        }

        try {
            Lexico lexer = new Lexico(new FileReader(args[0]));
            Sintactico parser = new Sintactico(lexer);

            try {
                parser.parse();
            } catch (Exception e) {
                System.out.println("Error durante el análisis: " + e.getMessage());
            }

            // 1. Reglas y mensajes [POSITION] en orden de reducción
            for (String linea : parser.getReglas()) {
                System.out.println(linea);
            }

            // 2. Errores (léxicos del lexer + sintácticos/léxicos del parser + semánticos)
            for (String error : lexer.errores) {
                System.out.println(error);
            }
            for (String error : parser.getErroresSintacticos()) {
                System.out.println(error);
            }
            List<String> semanticos = parser.getErroresSemanticos();
            for (String error : semanticos) {
                System.out.println(error);
            }

            // Tabla de símbolos (con tipos en los IDs)
            lexer.tablaSimbolos.escribirArchivo("ts.txt");

            // 3. Resumen
            int totalErrores = lexer.errores.size()
                    + parser.getErroresSintacticos().size()
                    + semanticos.size();
            if (totalErrores == 0) {
                System.out.println("Compilación exitosa");
            } else {
                System.out.println("Compilación con " + totalErrores + " error(es)");
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
