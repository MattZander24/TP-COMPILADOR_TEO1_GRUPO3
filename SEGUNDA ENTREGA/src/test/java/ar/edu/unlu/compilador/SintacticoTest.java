package ar.edu.unlu.compilador;

import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class SintacticoTest {

    private static final Set<Integer> SILENCIOSAS = new HashSet<>(java.util.Arrays.asList(
        3, 4, 12, 13, 14, 15, 16, 17, 27, 31, 32, 33, 34, 35, 36, 43, 46, 50
    ));

    private Sintactico parsear(String codigo) throws Exception {
        Lexico lexer = new Lexico(new StringReader(codigo));
        Sintactico parser = new Sintactico(lexer);
        parser.parse();
        return parser;
    }

    private static String programa(String declaraciones, String sentencias) {
        return "DECLARE.SECTION\n" + declaraciones + "\nENDDECLARE.SECTION\n"
                + "PROGRAM.SECTION\n" + sentencias + "\nENDPROGRAM.SECTION\n";
    }

    private static String decl(String varsYTipos) {
        return "[" + varsYTipos + "]";
    }

    private List<String> reglasDe(Sintactico parser) {
        return parser.getReglas();
    }

    private Set<Integer> numerosDeReglas(Sintactico parser) {
        Set<Integer> numeros = new HashSet<>();
        Pattern p = Pattern.compile("Regla (\\d+):");
        for (String linea : parser.getReglas()) {
            Matcher m = p.matcher(linea);
            if (m.find()) {
                numeros.add(Integer.parseInt(m.group(1)));
            }
        }
        return numeros;
    }

    private int totalErrores(Sintactico parser) {
        return parser.lexer.errores.size()
                + parser.getErroresSintacticos().size()
                + parser.getErroresSemanticos().size();
    }

    // ==================== VÁLIDOS ====================

    @Test
    public void testProgramaMinimo() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "a ::= 1");
        Sintactico parser = parsear(codigo);
        assertEquals(0, totalErrores(parser));
        assertFalse(parser.getReglas().isEmpty());
    }

    @Test
    public void testPruebaTxtCompletoSinErrores() throws Exception {
        Lexico lexer = new Lexico(new FileReader("prueba.txt"));
        Sintactico parser = new Sintactico(lexer);
        parser.parse();
        assertEquals(0, totalErrores(parser),
                "Errores: " + lexer.errores + parser.getErroresSintacticos() + parser.getErroresSemanticos());
    }

    @Test
    public void testLas51ReglasAparecenYLasSilenciosasNo() throws Exception {
        Lexico lexer = new Lexico(new FileReader("prueba.txt"));
        Sintactico parser = new Sintactico(lexer);
        parser.parse();
        Set<Integer> numeros = numerosDeReglas(parser);
        for (int n = 1; n <= 51; n++) {
            if (SILENCIOSAS.contains(n)) {
                assertFalse(numeros.contains(n), "La regla silenciosa " + n + " no debe aparecer");
            } else {
                assertTrue(numeros.contains(n), "Falta la regla " + n + " en la salida");
            }
        }
    }

    @Test
    public void testPositionEncontradoPosicion6() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(4;[10,20,30,40,5,4])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getReglas().contains("[POSITION] Elemento encontrado en posición 6"),
                parser.getReglas().toString());
        assertEquals(0, totalErrores(parser));
    }

    @Test
    public void testPositionNoEncontrado() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(5;[2,2,2,4])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getReglas().contains("[POSITION] Elemento no encontrado"),
                parser.getReglas().toString());
    }

    @Test
    public void testPositionEncontradoPosicion2() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(1;[2,1,1,4])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getReglas().contains("[POSITION] Elemento encontrado en posición 2"),
                parser.getReglas().toString());
    }

    @Test
    public void testPositionListaVacia() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(1;[])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getReglas().contains("[POSITION] La lista está vacía"),
                parser.getReglas().toString());
    }

    @Test
    public void testPositionDentroDeExpresion() throws Exception {
        String codigo = programa("[x] := [INTEGER]",
                "x ::= POSITION(1;[3,1]) + 1");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getReglas().contains("[POSITION] Elemento encontrado en posición 2"),
                parser.getReglas().toString());
        assertEquals(0, totalErrores(parser));
    }

    @Test
    public void testDeclaracionTresVariablesTipos() throws Exception {
        String codigo = programa("[p1, p2, p3] := [FLOAT, FLOAT, INTEGER]", "p1 ::= 1.5");
        Sintactico parser = parsear(codigo);
        TablaSimbolos tabla = parser.lexer.tablaSimbolos;
        assertEquals(Tipo.FLOAT, tabla.getTipo("p1"));
        assertEquals(Tipo.FLOAT, tabla.getTipo("p2"));
        assertEquals(Tipo.INTEGER, tabla.getTipo("p3"));
        assertEquals(0, totalErrores(parser));
    }

    // ==================== ERRORES SINTÁCTICOS ====================

    @Test
    public void testErrorMasVariablesQueTipos() {
        String codigo = programa("[a, b] := [INTEGER]", "a ::= 1");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorMasTiposQueVariables() {
        String codigo = programa("[a] := [INTEGER, FLOAT]", "a ::= 1");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorFaltaEndif() {
        String codigo = programa("[a] := [INTEGER]",
                "IF a > 0 THEN\n    WRITE \"x\"\n");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorFaltaEndwhile() {
        String codigo = programa("[a] := [INTEGER]",
                "WHILE (a < 10)\n    a ::= a + 1\n");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorFaltaEndprogram() {
        String codigo = "DECLARE.SECTION\n[a] := [INTEGER]\nENDDECLARE.SECTION\n"
                + "PROGRAM.SECTION\na ::= 1\n";
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorWhileSinParentesis() {
        String codigo = programa("[a] := [INTEGER]",
                "WHILE a < 10\n    a ::= a + 1\nENDWHILE\n");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorComparacionConParentesis() {
        String codigo = programa("[a, b] := [INTEGER, INTEGER]",
                "IF (a < b) THEN\n    WRITE \"x\"\nENDIF\n");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorDosConectoresLogicos() {
        String codigo = programa("[a, b, c] := [INTEGER, INTEGER, INTEGER]",
                "IF a < b AND b < c OR a == c THEN\n    WRITE \"x\"\nENDIF\n");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorProgramaAntesDeDeclaracion() {
        String codigo = "PROGRAM.SECTION\na ::= 1\nENDPROGRAM.SECTION\n"
                + "DECLARE.SECTION\n[a] := [INTEGER]\nENDDECLARE.SECTION\n";
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testErrorPositionListaMalFormada() {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(1;[1,])");
        try {
            Sintactico parser = parsear(codigo);
            assertFalse(parser.getErroresSintacticos().isEmpty());
        } catch (Exception e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    // ==================== ERRORES SEMÁNTICOS ====================

    @Test
    public void testErrorVariableNoDeclaradaAsignacion() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "x ::= 1");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("variable no declarada")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorVariableNoDeclaradaExpresion() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "a ::= b + 1");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("variable no declarada")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorVariableNoDeclaradaWrite() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "WRITE x");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("variable no declarada")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorVariableDeclaradaDosVeces() throws Exception {
        String codigo = programa("[a] := [INTEGER]\n[a] := [FLOAT]", "a ::= 1");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("ya declarada")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorIntegerRecibeFloat() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "a ::= 1.5");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("FLOAT a INTEGER")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorStringRecibeExpresionNumerica() throws Exception {
        String codigo = programa("[s] := [STRING]", "s ::= 5");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("asignación incompatible")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorFloatRecibeString() throws Exception {
        String codigo = programa("[f] := [FLOAT]", "f ::= \"hola\"");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("asignación incompatible")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorIntegerRecibeString() throws Exception {
        String codigo = programa("[i] := [INTEGER]", "i ::= \"hola\"");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("asignación incompatible")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorWriteVariableString() throws Exception {
        String codigo = programa("[s] := [STRING]", "WRITE s");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("STRING")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorComparacionConString() throws Exception {
        String codigo = programa("[s] := [STRING]", "IF s < 5 THEN\n    WRITE \"x\"\nENDIF\n");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("no numérico")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorPositionPivotCero() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(0;[1,2])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("pivot")),
                parser.getErroresSemanticos().toString());
    }

    @Test
    public void testErrorPositionListaConCero() throws Exception {
        String codigo = programa("[resul] := [INTEGER]",
                "resul ::= POSITION(1;[0,2])");
        Sintactico parser = parsear(codigo);
        assertTrue(parser.getErroresSemanticos().stream().anyMatch(e -> e.contains("enteros positivos")),
                parser.getErroresSemanticos().toString());
    }

    // ==================== ERRORES LÉXICOS PROPAGADOS ====================

    @Test
    public void testErrorLexicoComentarioSinCerrar() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "a ::= 1 //* comentario sin cerrar\n");
        Sintactico parser = parsear(codigo);
        assertTrue(totalErrores(parser) > 0);
        String todo = String.join("\n", parser.getErroresSintacticos()) + parser.lexer.errores;
        assertTrue(todo.contains("Comentario no cerrado"), todo);
    }

    @Test
    public void testErrorLexicoEnteroFueraDeRango() throws Exception {
        String codigo = programa("[a] := [INTEGER]", "a ::= 32768");
        Sintactico parser = parsear(codigo);
        assertTrue(totalErrores(parser) > 0);
    }

    @Test
    public void testErrorLexicoStringLargo() throws Exception {
        String codigo = programa("[a] := [INTEGER]",
                "a ::= 1\nWRITE \"" + "x".repeat(31) + "\"");
        Sintactico parser = parsear(codigo);
        assertTrue(totalErrores(parser) > 0);
    }
}
