package ar.edu.unlu.compilador;

import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.List;

%%

%class Lexico
%public
%unicode
%cup
%line
%column
%throws Exception
%state COMENTARIO

%{
    public List<String> errores = new ArrayList<>();
    int profundidadComentario = 0;
    public TablaSimbolos tablaSimbolos = new TablaSimbolos();
%}

DIGITO              = [0-9]
LETRA               = [a-zA-Z]
TERMINADOR_LINEA    = \r|\n|\r\n
ESPACIO_BLANCO       = {TERMINADOR_LINEA} | [ \t]
OPERADORES           = (\+|-|\/|\*|>|<|\!=|<=|>=|=)
SIGNOS               = ,|:|;

%%

<YYINITIAL> {
    // Apertura de comentario
    "//*" {
        profundidadComentario = 1;
        yybegin(COMENTARIO);
    }
}

<COMENTARIO> {
    // Apertura anidada
    "//*" {
        profundidadComentario++;
        if (profundidadComentario > 2) {
            errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: Anidamiento de comentarios mayor a un nivel");
            // Se devuelve error para que se muestre; el lexer sigue dentro del comentario
            return new Symbol(sym.error, yyline+1, yycolumn+1, yytext());
        }
    }

    // Cierre de comentario
    "*//" {
        profundidadComentario--;
        if (profundidadComentario == 0) {
            yybegin(YYINITIAL);
        }
    }

    // Cualquier otro carácter (incluido salto de línea) se ignora
    [^] { /* ignorar */ }

    // EOF dentro de comentario
    <<EOF>> {
        errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: Comentario no cerrado");
        // Volver a YYINITIAL para que la próxima llamada devuelva EOF (si no, se repite este error para siempre)
        yybegin(YYINITIAL);
        return new Symbol(sym.error, yyline+1, yycolumn+1, "");
    }
}

<YYINITIAL> {
    // Palabras reservadas (DEBEN ir ANTES que ID)
    "DECLARE.SECTION"        { return new Symbol(sym.DECLARE, yyline+1, yycolumn+1, yytext()); }
    "ENDDECLARE.SECTION"     { return new Symbol(sym.ENDDECLARE, yyline+1, yycolumn+1, yytext()); }
    "PROGRAM.SECTION"        { return new Symbol(sym.PROGRAM, yyline+1, yycolumn+1, yytext()); }
    "ENDPROGRAM.SECTION"     { return new Symbol(sym.ENDPROGRAM, yyline+1, yycolumn+1, yytext()); }
    "IF"                     { return new Symbol(sym.IF, yyline+1, yycolumn+1, yytext()); }
    "THEN"                   { return new Symbol(sym.THEN, yyline+1, yycolumn+1, yytext()); }
    "ELSE"                   { return new Symbol(sym.ELSE, yyline+1, yycolumn+1, yytext()); }
    "ENDIF"                  { return new Symbol(sym.ENDIF, yyline+1, yycolumn+1, yytext()); }
    "WHILE"                  { return new Symbol(sym.WHILE, yyline+1, yycolumn+1, yytext()); }
    "ENDWHILE"               { return new Symbol(sym.ENDWHILE, yyline+1, yycolumn+1, yytext()); }
    "WRITE"                  { return new Symbol(sym.WRITE, yyline+1, yycolumn+1, yytext()); }
    "FLOAT"                  { return new Symbol(sym.FLOAT_T, yyline+1, yycolumn+1, yytext()); }
    "INTEGER"                { return new Symbol(sym.INTEGER_T, yyline+1, yycolumn+1, yytext()); }
    "STRING"                 { return new Symbol(sym.STRING_T, yyline+1, yycolumn+1, yytext()); }
    "AND"                    { return new Symbol(sym.AND, yyline+1, yycolumn+1, yytext()); }
    "OR"                     { return new Symbol(sym.OR, yyline+1, yycolumn+1, yytext()); }
    "POSITION"               { return new Symbol(sym.POSITION, yyline+1, yycolumn+1, yytext()); }

    // Operadores
    "::="                    { return new Symbol(sym.ASIGNACION, yyline+1, yycolumn+1, yytext()); }
    ":="                     { return new Symbol(sym.ASIG_DECL, yyline+1, yycolumn+1, yytext()); }
    "=="                     { return new Symbol(sym.OP_IGUAL, yyline+1, yycolumn+1, yytext()); }
    "!="                     { return new Symbol(sym.OP_DISTINTO, yyline+1, yycolumn+1, yytext()); }
    "<="                     { return new Symbol(sym.OP_MENOR_IGUAL, yyline+1, yycolumn+1, yytext()); }
    ">="                     { return new Symbol(sym.OP_MAYOR_IGUAL, yyline+1, yycolumn+1, yytext()); }
    "<"                      { return new Symbol(sym.OP_MENOR, yyline+1, yycolumn+1, yytext()); }
    ">"                      { return new Symbol(sym.OP_MAYOR, yyline+1, yycolumn+1, yytext()); }
    "+"                      { return new Symbol(sym.OP_SUMA, yyline+1, yycolumn+1, yytext()); }
    "-"                      { return new Symbol(sym.OP_RESTA, yyline+1, yycolumn+1, yytext()); }
    "*"                      { return new Symbol(sym.OP_MULT, yyline+1, yycolumn+1, yytext()); }
    "/"                      { return new Symbol(sym.OP_DIV, yyline+1, yycolumn+1, yytext()); }

    // Delimitadores
    ","                      { return new Symbol(sym.COMA, yyline+1, yycolumn+1, yytext()); }
    "."                      { return new Symbol(sym.PUNTO, yyline+1, yycolumn+1, yytext()); }
    ";"                      { return new Symbol(sym.PUNTO_Y_COMA, yyline+1, yycolumn+1, yytext()); }
    "("                      { return new Symbol(sym.PAR_A, yyline+1, yycolumn+1, yytext()); }
    ")"                      { return new Symbol(sym.PAR_C, yyline+1, yycolumn+1, yytext()); }
    "["                      { return new Symbol(sym.COR_A, yyline+1, yycolumn+1, yytext()); }
    "]"                      { return new Symbol(sym.COR_C, yyline+1, yycolumn+1, yytext()); }
    "{"                      { return new Symbol(sym.LLAV_A, yyline+1, yycolumn+1, yytext()); }
    "}"                      { return new Symbol(sym.LLAV_C, yyline+1, yycolumn+1, yytext()); }

    // Identificador (modificado: sufijo opcional para permitir IDs de una sola letra)
    {LETRA}(({LETRA}|{DIGITO}|\-)*({LETRA}|{DIGITO}))? {
        String lexema = yytext();
        tablaSimbolos.agregar(lexema, "ID", "", "", "");
        return new Symbol(sym.ID, yyline+1, yycolumn+1, lexema);
    }

    // Constante entera con validación de rango 0..32767
    {DIGITO}{DIGITO}* {
        String lexema = yytext();
        // BigInteger: Integer.parseInt lanza excepción con números de más de 10 dígitos
        if (new java.math.BigInteger(lexema).compareTo(java.math.BigInteger.valueOf(32767)) > 0) {
            errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: CTE_INT fuera de rango (0..32767) -> " + lexema);
            return new Symbol(sym.error, yyline+1, yycolumn+1, lexema);
        }
        tablaSimbolos.agregar("_" + lexema, "CTE_INT", "", lexema, "");
        return new Symbol(sym.CTE_INT, yyline+1, yycolumn+1, lexema);
    }

    // Constante float con validación de rango (float 32 bits)
    {DIGITO}+ "." {DIGITO}+ | {DIGITO}+ "." | "." {DIGITO}+ {
        String lexema = yytext();
        float valor = Float.parseFloat(lexema);
        if (Float.isInfinite(valor)) {
            errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: CTE_FLOAT fuera de rango -> " + lexema);
            return new Symbol(sym.error, yyline+1, yycolumn+1, lexema);
        }
        tablaSimbolos.agregar("_" + lexema, "CTE_FLOAT", "", lexema, "");
        return new Symbol(sym.CTE_FLOAT, yyline+1, yycolumn+1, lexema);
    }

    // String con validación de máximo 30 caracteres (sin contar comillas)
    \"({ESPACIO_BLANCO}|{SIGNOS}|{OPERADORES}|{LETRA}|{DIGITO}|\.|\!|\¡)*\" {
        String contenido = yytext().substring(1, yytext().length() - 1);
        if (contenido.length() > 30) {
            errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: STRING supera 30 caracteres -> " + yytext());
            return new Symbol(sym.error, yyline+1, yycolumn+1, yytext());
        }
        String nombreTs = "_" + contenido.replace(" ", "_");
        tablaSimbolos.agregar(nombreTs, "STRING", "", contenido, String.valueOf(contenido.length()));
        return new Symbol(sym.STRING, yyline+1, yycolumn+1, yytext());
    }

    // String sin cerrar (comilla sola)
    \" {
        errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: STRING sin cerrar -> " + yytext());
        return new Symbol(sym.error, yyline+1, yycolumn+1, yytext());
    }

    // Espacios en blanco
    {ESPACIO_BLANCO}+ { /* ignorar */ }

    // Carácter no reconocido
    . {
        errores.add("ERROR LÉXICO [" + (yyline+1) + ":" + (yycolumn+1) + "]: Carácter no reconocido -> " + yytext());
        return new Symbol(sym.error, yyline+1, yycolumn+1, yytext());
    }
}
