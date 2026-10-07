# Compilador - Teoría de la Computación I - Grupo 3

## Descripción

Analizador léxico implementado con JFlex para el lenguaje especificado en el TP Integrador de Teoría de la Computación I (UNLu), 2026.

## Tecnologías

- **Lenguaje**: Java 17
- **Build**: Maven
- **Generador de lexer**: JFlex 1.9.1
- **Runtime**: Java CUP 11b-20160615
- **Tests**: JUnit 5
- **IDE**: Swing

## Estructura del Proyecto

```
/Lexico.flex                 # Especificación léxica JFlex
/prueba.txt                  # Archivo de prueba con todos los tokens
/ts.txt                      # Tabla de símbolos generada (al compilar)
/pom.xml                     # Configuración Maven
/src/main/java/...           # Código fuente (GUI, TablaSimbolos, Simbolo, sym, IDE)
/src/test/java/...           # Tests JUnit 5 del lexer
/README.md                   # Este archivo
/dist/compilador.jar         # JAR ejecutable con dependencias
```

## Cómo Compilar

```bash
mvn clean package
```

Esto genera:
- El JAR ejecutable en `dist/compilador.jar`
- El lexer generado en `target/generated-sources/jflex/`
- Los archivos compilados en `target/classes/`

## Cómo Ejecutar

### Opción 1: IDE Gráfica (recomendado)

```bash
java -jar dist/compilador.jar
```

Esto abre la interfaz gráfica Swing con:
- Área de código editable
- Botones: Abrir, Guardar, Compilar
- Panel de salida con tokens y errores
- Pestaña para ver la tabla de símbolos

### Opción 2: Línea de comandos

```bash
java -cp dist/compilador.jar ar.edu.unlu.compilador.Main prueba.txt
```

Esto genera:
- Salida en consola con tokens reconocidos
- Archivo `ts.txt` con la tabla de símbolos

## Cómo Ejecutar Tests

```bash
mvn test
```

## Decisiones de Diseño

### Modificación al Identificador (ID)

**Cambio**: La regex del ID fue modificada respecto al TP REGEX original.

**Regex original**: Exigía mínimo dos caracteres: `{LETRA}({LETRA}|{DIGITO}|\-)+({LETRA}|{DIGITO})`

**Regex actual**: Sufijo opcional para permitir IDs de una sola letra: `{LETRA}(({LETRA}|{DIGITO}|\-)*({LETRA}|{DIGITO}))?`

**Motivo**: Para que identificadores de una sola letra (a, b, x) sean válidos.

**Tratamiento del guion**: 
- `a-b` sin espacios es UN ID
- `a - b` con espacios es `ID OP_RESTA ID`
- `a-` es `ID` seguido de `OP_RESTA`

### Comentarios

- Apertura: `//*`
- Cierre: `*//`
- Anidamiento permitido de UN solo nivel
- Anidamiento de dos niveles genera error léxico
- Comentario sin cerrar genera error léxico
- Código dentro de comentarios no genera tokens

### Tabla de Símbolos

**Columnas**: NOMBRE | TOKEN | TIPO | VALOR | LONG

**Reglas**:
- ID: NOMBRE = lexema, VALOR = —, LONG = —
- CTE_INT: NOMBRE = "_" + lexema, VALOR = lexema, LONG = —
- CTE_FLOAT: NOMBRE = "_" + lexema, VALOR = lexema, LONG = —
- STRING: NOMBRE = "_" + contenido (espacios reemplazados por "_"), VALOR = contenido sin comillas, LONG = longitud del contenido

**Sin duplicados**: Usa LinkedHashMap para conservar orden de inserción y evitar duplicados.

### Validaciones

- **CTE_INT**: Rango 0..32767 (16 bits). Fuera de rango → error léxico.
- **CTE_FLOAT**: Rango float 32 bits (máx 3.4028235e38). Float.isInfinite → error léxico.
- **STRING**: Máximo 30 caracteres sin contar comillas. Más de 30 → error léxico.

### Palabras Reservadas

- **Sensibles a mayúsculas**: Solo válidas en MAYÚSCULAS
- `WRITE` es palabra reservada, `write` es ID
- `DECLARE.SECTION` es palabra reservada, `DECLARE` es ID
- Las reglas de palabras reservadas van ANTES que la regla de ID en el .flex

### Constantes Float

Acepta tres formatos:
- `99999.99` (parte entera y decimal)
- `99.` (solo parte entera con punto)
- `.9999` (solo parte decimal)

Por regla de lexema más largo, `.9` es CTE_FLOAT y `.` solo es PUNTO.

### Integración Futura con CUP

- El .flex usa `%cup`, `%class Lexico`, `%public`, `%unicode`, `%line`, `%column`
- Devuelve `java_cup.runtime.Symbol` con constantes de la clase `sym`
- `sym.java` fue escrito a mano con las mismas constantes que generará CUP
- En la segunda entrega se reemplazará por la generada por CUP

## Tokens Implementados

### Palabras Reservadas (17)
DECLARE, ENDDECLARE, PROGRAM, ENDPROGRAM, IF, THEN, ELSE, ENDIF, WHILE, ENDWHILE, WRITE, FLOAT, INTEGER, STRING, AND, OR, POSITION

### Operadores (13)
::= (asignación), := (asignación declaración), ==, !=, <=, >=, <, >, +, -, *, /

### Delimitadores (9)
, . ; ( ) [ ] { }

### Identificador y Constantes (4)
ID, CTE_INT, CTE_FLOAT, STRING

## TEMA ESPECIAL: POSITION

La sentencia permite encontrar en qué posición de una lista de constantes enteras positivas se encuentra un elemento pivot.

**Formato**: `id ::= POSITION(constante_entera; [lista de constantes])`

**Ejemplos**:
- `resul ::= POSITION(4;[10,20,30,40,5,4])` → posición 6
- `resul ::= POSITION(5;[2,2,2,4])` → no encontrado → 0
- `resul ::= POSITION(1;[2,1,1,4])` → posición 2
- `resul ::= POSITION(1;[])` → lista vacía → 0

En esta entrega solo se requiere reconocer los tokens. La semántica corresponde a entregas posteriores.

## Errores Léxicos

El lexer NO aborta en el primer error; reporta y continúa.

Tipos de errores:
- Carácter no reconocido
- CTE_INT fuera de rango (0..32767)
- CTE_FLOAT fuera de rango (float 32 bits)
- STRING de más de 30 caracteres
- STRING sin cerrar
- Comentario sin cerrar
- Anidamiento de comentarios mayor a un nivel

Todos los errores reportan línea y columna (base 1: yyline+1, yycolumn+1).
