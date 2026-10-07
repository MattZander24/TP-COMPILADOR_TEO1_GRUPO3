package ar.edu.unlu.compilador;

import java.io.FileReader;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Compilador - Teoría de la Computación I - Grupo 3");
            System.out.println("Uso: java -jar compilador.jar <archivo>");
            return;
        }

        try {
            Lexico lexer = new Lexico(new FileReader(args[0]));
            Symbol token;
            int tokenCount = 0;
            int errorCount = 0;

            System.out.println("=== TOKENS ===");
            while ((token = lexer.next_token()).sym != sym.EOF) {
                if (token.sym == sym.error) {
                    errorCount++;
                    for (String error : lexer.errores) {
                        System.out.println(error);
                    }
                    lexer.errores.clear();
                } else {
                    tokenCount++;
                    System.out.println("[" + token.left + ":" + token.right + "] " + symToString(token.sym) + " -> " + token.value);
                }
            }

            System.out.println("\n=== TABLA DE SÍMBOLOS ===");
            lexer.tablaSimbolos.escribirArchivo("ts.txt");
            System.out.println("ts.txt generado");

            System.out.println("\n=== RESUMEN ===");
            System.out.println("Tokens reconocidos: " + tokenCount);
            System.out.println("Errores léxicos: " + errorCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String symToString(int tokenSym) {
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
}
