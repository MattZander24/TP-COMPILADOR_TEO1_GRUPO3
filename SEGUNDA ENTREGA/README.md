# Compilador - Teoría de la Computación I - Grupo 3

## Descripción

Compilador con analizador léxico (JFlex) y analizador sintáctico (JAVA CUP) para el lenguaje especificado en el TP Integrador de Teoría de la Computación I (UNLu), 2026. **Entrega 2: Analizador Sintáctico.**

## Tecnologías

- **Lenguaje**: Java 17
- **Build**: Maven
- **Generador de lexer**: JFlex 1.9.1
- **Generador de parser**: JAVA CUP 11b-20160615 (`com.github.vbmacher:java-cup`)
- **Runtime**: `java-cup-runtime` 11b-20160615
- **Tests**: JUnit 5
- **IDE**: Swing

## Estructura del Proyecto

```
/Lexico.flex                 # Especificación léxica JFlex (raíz, también entrada del build)
/Sintactico.cup              # Especificación sintáctica CUP (raíz, también entrada del build)
/prueba.txt                  # Programa de prueba válido (cubre las 51 reglas)
/ts.txt                      # Tabla de símbolos generada al compilar prueba.txt
/pom.xml                     # Configuración Maven
/src/main/java/...           # IDE, Main, Simbolo, TablaSimbolos, Tipo, DeclInfo
/src/test/java/...           # LexicoTest (42) y SintacticoTest (34)
/README.md                   # Este archivo
/dist/compilador.jar         # JAR ejecutable con dependencias
```

`Lexico.flex` y `Sintactico.cup` viven en la raíz porque el `pom.xml` los toma
desde ahí; los fuentes generados (`Lexico.java`, `Sintactico.java`, `sym.java`)
caen en `target/generated-sources/` y **no** se versionan.

## Cómo Compilar

```bash
mvn clean package
```

Genera, en orden: `Lexico.java` (JFlex) → `Sintactico.java` + `sym.java` (CUP,
clase `Sintactico`, símbolos `sym`) → clases → tests → `dist/compilador.jar`.

Resultado esperado de CUP: **0 conflictos** shift/reduce y reduce/reduce.

## Cómo Ejecutar

### Opción 1: IDE Gráfica (recomendado)

```bash
java -jar dist/compilador.jar
```

Abre la interfaz Swing "Entrega 2 - Analizador Sintáctico" con área de código,
Abrir / Guardar / Compilar, panel de salida (reglas reducidas, mensajes
`[POSITION]`, errores) y pestaña de tabla de símbolos con la columna TIPO.

### Opción 2: Línea de comandos

```bash
java -cp dist/compilador.jar ar.edu.unlu.compilador.Main prueba.txt
```

Produce la misma salida que la IDE y regenera `ts.txt` en el directorio actual.

## Salida del análisis

1. **Reglas**: una línea `Regla N: <producción>` por cada reducción, en orden
   bottom-up. Las reglas silenciosas no generan salida.
2. **Mensajes `[POSITION]`**: resultado de evaluar `POSITION(...)` en tiempo de
   compilación (todos sus datos son constantes).
3. **Errores**: léxicos (`ERROR LÉXICO`), sintácticos
   (`[línea:col] Error sintáctico: token inesperado '...' Se esperaba: ...`)
   y semánticos (`[línea:col] Error semántico: ...`). El primer error
   sintáctico o léxico es fatal; los semánticos se acumulan.
4. **Resumen**: `Compilación exitosa` o `Compilación con N error(es)`.

## Reglas silenciosas

No imprimen salida (solo pasan valores o encadenan listas). Definidas en
`SILENCIOSAS` dentro de `Sintactico.cup`:

```
3, 4, 12, 13, 14, 15, 16, 17, 27, 31, 32, 33, 34, 35, 36, 43, 46, 50
```

## Decisiones semánticas

- **Tipos** (`enum Tipo`): `INTEGER`, `FLOAT`, `STRING`, `ERROR`.
  `expresion`/`termino` con operador: `FLOAT` si algún operando es `FLOAT`;
  `INTEGER` si ambos son `INTEGER` (incluido `/`). `position` → `INTEGER`.
  `ERROR` se propaga sin generar errores en cascada.
- **Tabla de símbolos**: la columna TIPO de los IDs se completa al reducir la
  regla de `<declaracion>` con `Integer` / `Float` / `String`. Las constantes
  mantienen TIPO `—` y su VALOR/LONG de la Entrega 1. `TablaSimbolos` expone
  `setTipo`, `getTipo` y `estaDeclarado`.
- **Declaraciones**: la correspondencia variable↔tipo es por posición. La
  recursión de `<lista_var_tipo>` acumula variables con `addFirst` y tipos con
  `addLast` (`DeclInfo`), por lo que `[p1,p2,p3] := [FLOAT,FLOAT,INTEGER]`
  produce p1=Float, p2=Float, p3=Integer.
- **Asignaciones**: `ID ::= CTE_STRING` exige variable `STRING`; `ID ::=
  <expresion>` exige variable numérica. `INTEGER ← FLOAT` es error
  ("asignación incompatible (FLOAT a INTEGER)"). `STRING ←` cualquier
  expresión numérica es error.
- **WRITE**: `WRITE ID` exige variable declarada y numérica (para imprimir
  strings se usa `WRITE CTE_STRING`).
- **Condiciones**: un único conector lógico (`AND` u `OR`) por condición; los
  operandos de cada comparación deben ser numéricos.
- **POSITION**: pivot y elementos de la lista deben ser `>= 1`. Lista vacía →
  `La lista está vacía` (resultado 0); encontrado → `Elemento encontrado en
  posición N` (1-based, primera aparición); si no → `Elemento no encontrado`
  (resultado 0).

## Decisiones de la Entrega 1 (sin cambios)

- **ID**: sufijo opcional para permitir IDs de una sola letra; `a-b` sin
  espacios es un ID, `a - b` es `ID OP_RESTA ID`.
- **Comentarios** `//* ... *//`: un nivel de anidamiento; comentario sin cerrar
  o anidamiento >1 → error léxico.
- **CTE_INT**: 0..32767. **CTE_FLOAT**: `99999.99`, `99.`, `.9999`.
  **CTE_STRING**: máx. 30 caracteres sin comillas.
- Palabras reservadas sensibles a mayúsculas (`WRITE` ≠ `write`).

## Tests

```bash
mvn test
```

- `LexicoTest`: 42 tests del lexer.
- `SintacticoTest`: 34 tests del parser (programas válidos, errores
  sintácticos, semánticos y léxicos propagados).
