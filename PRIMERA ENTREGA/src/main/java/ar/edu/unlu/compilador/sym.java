package ar.edu.unlu.compilador;

public class sym {
    public static final int EOF = 0;
    public static final int error = 1;

    // Palabras reservadas
    public static final int DECLARE = 2;
    public static final int ENDDECLARE = 3;
    public static final int PROGRAM = 4;
    public static final int ENDPROGRAM = 5;
    public static final int IF = 6;
    public static final int THEN = 7;
    public static final int ELSE = 8;
    public static final int ENDIF = 9;
    public static final int WHILE = 10;
    public static final int ENDWHILE = 11;
    public static final int WRITE = 12;
    public static final int FLOAT_T = 13;
    public static final int INTEGER_T = 14;
    public static final int STRING_T = 15;
    public static final int AND = 16;
    public static final int OR = 17;
    public static final int POSITION = 18;

    // Operadores
    public static final int ASIGNACION = 19;
    public static final int ASIG_DECL = 20;
    public static final int OP_IGUAL = 21;
    public static final int OP_DISTINTO = 22;
    public static final int OP_MENOR_IGUAL = 23;
    public static final int OP_MAYOR_IGUAL = 24;
    public static final int OP_MENOR = 25;
    public static final int OP_MAYOR = 26;
    public static final int OP_SUMA = 27;
    public static final int OP_RESTA = 28;
    public static final int OP_MULT = 29;
    public static final int OP_DIV = 30;

    // Delimitadores
    public static final int COMA = 31;
    public static final int PUNTO = 32;
    public static final int PUNTO_Y_COMA = 33;
    public static final int PAR_A = 34;
    public static final int PAR_C = 35;
    public static final int COR_A = 36;
    public static final int COR_C = 37;
    public static final int LLAV_A = 38;
    public static final int LLAV_C = 39;

    // Identificador y constantes
    public static final int ID = 40;
    public static final int CTE_INT = 41;
    public static final int CTE_FLOAT = 42;
    public static final int STRING = 43;
}
