package ar.edu.unlu.compilador;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.StringReader;
import java_cup.runtime.Symbol;

import static org.junit.jupiter.api.Assertions.*;

public class LexicoTest {

    // ===== Identificadores =====
    @Test
    public void testIdentificadorSimple() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
        assertEquals("a", token.value);
    }

    @Test
    public void testIdentificadorConLetras() throws Exception {
        Lexico lexer = new Lexico(new StringReader("hola"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
        assertEquals("hola", token.value);
    }

    @Test
    public void testIdentificadorConGuion() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a-b"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
        assertEquals("a-b", token.value);
    }

    @Test
    public void testIdentificadorConGuionMultiples() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a-b-c"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
        assertEquals("a-b-c", token.value);
    }

    @Test
    public void testIdentificadorRestaConEspacios() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a - b"));
        Symbol token1 = lexer.next_token();
        assertEquals(sym.ID, token1.sym);
        Symbol token2 = lexer.next_token();
        assertEquals(sym.OP_RESTA, token2.sym);
        Symbol token3 = lexer.next_token();
        assertEquals(sym.ID, token3.sym);
    }

    @Test
    public void testIdentificadorSeguidoDeResta() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a-"));
        Symbol token1 = lexer.next_token();
        assertEquals(sym.ID, token1.sym);
        Symbol token2 = lexer.next_token();
        assertEquals(sym.OP_RESTA, token2.sym);
    }

    // ===== Palabras reservadas =====
    @Test
    public void testDeclareSection() throws Exception {
        Lexico lexer = new Lexico(new StringReader("DECLARE.SECTION"));
        Symbol token = lexer.next_token();
        assertEquals(sym.DECLARE, token.sym);
    }

    @Test
    public void testDeclareSoloEsID() throws Exception {
        Lexico lexer = new Lexico(new StringReader("DECLARE"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
    }

    @Test
    public void testWriteMayuscula() throws Exception {
        Lexico lexer = new Lexico(new StringReader("WRITE"));
        Symbol token = lexer.next_token();
        assertEquals(sym.WRITE, token.sym);
    }

    @Test
    public void testWriteMinusculaEsID() throws Exception {
        Lexico lexer = new Lexico(new StringReader("write"));
        Symbol token = lexer.next_token();
        assertEquals(sym.ID, token.sym);
    }

    @Test
    public void testIfThenElseEndif() throws Exception {
        Lexico lexer = new Lexico(new StringReader("IF THEN ELSE ENDIF"));
        assertEquals(sym.IF, lexer.next_token().sym);
        assertEquals(sym.THEN, lexer.next_token().sym);
        assertEquals(sym.ELSE, lexer.next_token().sym);
        assertEquals(sym.ENDIF, lexer.next_token().sym);
    }

    @Test
    public void testWhileEndwhile() throws Exception {
        Lexico lexer = new Lexico(new StringReader("WHILE ENDWHILE"));
        assertEquals(sym.WHILE, lexer.next_token().sym);
        assertEquals(sym.ENDWHILE, lexer.next_token().sym);
    }

    @Test
    public void testFloatIntegerString() throws Exception {
        Lexico lexer = new Lexico(new StringReader("FLOAT INTEGER STRING"));
        assertEquals(sym.FLOAT_T, lexer.next_token().sym);
        assertEquals(sym.INTEGER_T, lexer.next_token().sym);
        assertEquals(sym.STRING_T, lexer.next_token().sym);
    }

    @Test
    public void testAndOrPosition() throws Exception {
        Lexico lexer = new Lexico(new StringReader("AND OR POSITION"));
        assertEquals(sym.AND, lexer.next_token().sym);
        assertEquals(sym.OR, lexer.next_token().sym);
        assertEquals(sym.POSITION, lexer.next_token().sym);
    }

    // ===== Operadores =====
    @Test
    public void testAsignacionVsAsigDecl() throws Exception {
        Lexico lexer = new Lexico(new StringReader("::="));
        assertEquals(sym.ASIGNACION, lexer.next_token().sym);
        
        lexer = new Lexico(new StringReader(":="));
        assertEquals(sym.ASIG_DECL, lexer.next_token().sym);
    }

    @Test
    public void testOperadoresComparacion() throws Exception {
        Lexico lexer = new Lexico(new StringReader("<= < >= > == !="));
        assertEquals(sym.OP_MENOR_IGUAL, lexer.next_token().sym);
        assertEquals(sym.OP_MENOR, lexer.next_token().sym);
        assertEquals(sym.OP_MAYOR_IGUAL, lexer.next_token().sym);
        assertEquals(sym.OP_MAYOR, lexer.next_token().sym);
        assertEquals(sym.OP_IGUAL, lexer.next_token().sym);
        assertEquals(sym.OP_DISTINTO, lexer.next_token().sym);
    }

    @Test
    public void testIgualSoloEsError() throws Exception {
        Lexico lexer = new Lexico(new StringReader("="));
        Symbol token = lexer.next_token();
        assertEquals(sym.error, token.sym);
    }

    @Test
    public void testOperadoresAritmeticos() throws Exception {
        Lexico lexer = new Lexico(new StringReader("+ - * /"));
        assertEquals(sym.OP_SUMA, lexer.next_token().sym);
        assertEquals(sym.OP_RESTA, lexer.next_token().sym);
        assertEquals(sym.OP_MULT, lexer.next_token().sym);
        assertEquals(sym.OP_DIV, lexer.next_token().sym);
    }

    // ===== Delimitadores =====
    @Test
    public void testDelimitadores() throws Exception {
        Lexico lexer = new Lexico(new StringReader(", . ; ( ) [ ] { }"));
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.PUNTO, lexer.next_token().sym);
        assertEquals(sym.PUNTO_Y_COMA, lexer.next_token().sym);
        assertEquals(sym.PAR_A, lexer.next_token().sym);
        assertEquals(sym.PAR_C, lexer.next_token().sym);
        assertEquals(sym.COR_A, lexer.next_token().sym);
        assertEquals(sym.COR_C, lexer.next_token().sym);
        assertEquals(sym.LLAV_A, lexer.next_token().sym);
        assertEquals(sym.LLAV_C, lexer.next_token().sym);
    }

    // ===== Constantes enteras =====
    @Test
    public void testCteIntValido32767() throws Exception {
        Lexico lexer = new Lexico(new StringReader("32767"));
        Symbol token = lexer.next_token();
        assertEquals(sym.CTE_INT, token.sym);
        assertEquals("32767", token.value);
    }

    @Test
    public void testCteIntError32768() throws Exception {
        Lexico lexer = new Lexico(new StringReader("32768"));
        Symbol token = lexer.next_token();
        assertEquals(sym.error, token.sym);
    }

    @Test
    public void testCteIntCero() throws Exception {
        Lexico lexer = new Lexico(new StringReader("0"));
        Symbol token = lexer.next_token();
        assertEquals(sym.CTE_INT, token.sym);
    }

    // ===== Constantes float =====
    @Test
    public void testCteFloatConPartes() throws Exception {
        Lexico lexer = new Lexico(new StringReader("99.9999"));
        Symbol token = lexer.next_token();
        assertEquals(sym.CTE_FLOAT, token.sym);
        assertEquals("99.9999", token.value);
    }

    @Test
    public void testCteFloatSoloPuntoDecimal() throws Exception {
        Lexico lexer = new Lexico(new StringReader("99."));
        Symbol token = lexer.next_token();
        assertEquals(sym.CTE_FLOAT, token.sym);
        assertEquals("99.", token.value);
    }

    @Test
    public void testCteFloatSoloParteDecimal() throws Exception {
        Lexico lexer = new Lexico(new StringReader(".9999"));
        Symbol token = lexer.next_token();
        assertEquals(sym.CTE_FLOAT, token.sym);
        assertEquals(".9999", token.value);
    }

    @Test
    public void testPuntoSoloEsPunto() throws Exception {
        Lexico lexer = new Lexico(new StringReader("."));
        Symbol token = lexer.next_token();
        assertEquals(sym.PUNTO, token.sym);
    }

    // ===== Strings =====
    @Test
    public void testString30CaracteresValido() throws Exception {
        String str30 = "123456789012345678901234567890";
        Lexico lexer = new Lexico(new StringReader("\"" + str30 + "\""));
        Symbol token = lexer.next_token();
        assertEquals(sym.STRING, token.sym);
    }

    @Test
    public void testString31CaracteresError() throws Exception {
        String str31 = "1234567890123456789012345678901";
        Lexico lexer = new Lexico(new StringReader("\"" + str31 + "\""));
        Symbol token = lexer.next_token();
        assertEquals(sym.error, token.sym);
    }

    @Test
    public void testStringConEspacios() throws Exception {
        Lexico lexer = new Lexico(new StringReader("\"hola mundo\""));
        Symbol token = lexer.next_token();
        assertEquals(sym.STRING, token.sym);
    }

    @Test
    public void testStringSinCerrar() throws Exception {
        Lexico lexer = new Lexico(new StringReader("\"hola"));
        Symbol token = lexer.next_token();
        assertEquals(sym.error, token.sym);
    }

    // ===== POSITION =====
    @Test
    public void testPositionTokens() throws Exception {
        Lexico lexer = new Lexico(new StringReader("resul ::= POSITION(4;[10,20,30,40,5,4])"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.ASIGNACION, lexer.next_token().sym);
        assertEquals(sym.POSITION, lexer.next_token().sym);
        assertEquals(sym.PAR_A, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.PUNTO_Y_COMA, lexer.next_token().sym);
        assertEquals(sym.COR_A, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COMA, lexer.next_token().sym);
        assertEquals(sym.CTE_INT, lexer.next_token().sym);
        assertEquals(sym.COR_C, lexer.next_token().sym);
        assertEquals(sym.PAR_C, lexer.next_token().sym);
    }

    // ===== Comentarios =====
    @Test
    public void testComentarioSimple() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a //* comentario *// b"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.EOF, lexer.next_token().sym);
    }

    @Test
    public void testComentarioAnidadoUnNivel() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a //* outer //* inner *// outer *// b"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.EOF, lexer.next_token().sym);
    }

    @Test
    public void testComentarioAnidadoDosNivelesError() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a //* outer //* inner //* deep *// inner *// outer *// b"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.EOF, lexer.next_token().sym);
        assertTrue(lexer.errores.size() > 0);
        assertTrue(lexer.errores.get(0).contains("Anidamiento de comentarios mayor a un nivel"));
    }

    @Test
    public void testComentarioSinCerrar() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a //* comentario sin cerrar"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.error, lexer.next_token().sym);
        assertTrue(lexer.errores.size() > 0);
        assertTrue(lexer.errores.get(0).contains("Comentario no cerrado"));
    }

    @Test
    public void testComentarioConCodigoAdentro() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a //* IF THEN ENDIF *// b"));
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.ID, lexer.next_token().sym);
        assertEquals(sym.EOF, lexer.next_token().sym);
    }

    // ===== Tabla de símbolos =====
    @Test
    public void testTablaSimbolosSinDuplicados() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a a b"));
        lexer.next_token();
        lexer.next_token();
        lexer.next_token();
        assertEquals(2, lexer.tablaSimbolos.size());
    }

    @Test
    public void testTablaSimbolosPrefijoConstantes() throws Exception {
        Lexico lexer = new Lexico(new StringReader("123 45.5"));
        lexer.next_token();
        lexer.next_token();
        assertTrue(lexer.tablaSimbolos.containsKey("_123"));
        assertTrue(lexer.tablaSimbolos.containsKey("_45.5"));
    }

    @Test
    public void testTablaSimbolosLongitudString() throws Exception {
        Lexico lexer = new Lexico(new StringReader("\"hola\""));
        lexer.next_token();
        Simbolo simbolo = lexer.tablaSimbolos.get("_hola");
        assertEquals("4", simbolo.getLongitud());
    }

    @Test
    public void testTablaSimbolosStringConEspacios() throws Exception {
        Lexico lexer = new Lexico(new StringReader("\"hola mundo\""));
        lexer.next_token();
        assertTrue(lexer.tablaSimbolos.containsKey("_hola_mundo"));
        Simbolo simbolo = lexer.tablaSimbolos.get("_hola_mundo");
        assertEquals("hola mundo", simbolo.getValor());
    }

    @Test
    public void testTablaSimbolosEscribirArchivo() throws Exception {
        Lexico lexer = new Lexico(new StringReader("a1 30.5 55 \"hola\""));
        lexer.next_token();
        lexer.next_token();
        lexer.next_token();
        lexer.next_token();
        
        String testFile = "test_ts.txt";
        lexer.tablaSimbolos.escribirArchivo(testFile);
        
        File file = new File(testFile);
        assertTrue(file.exists());
        file.delete();
    }
}
