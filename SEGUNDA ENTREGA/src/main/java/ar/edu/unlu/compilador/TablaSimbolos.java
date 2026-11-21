package ar.edu.unlu.compilador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class TablaSimbolos {
    private LinkedHashMap<String, Simbolo> simbolos;
    private LinkedHashMap<String, Tipo> tiposDeclarados;

    public TablaSimbolos() {
        this.simbolos = new LinkedHashMap<>();
        this.tiposDeclarados = new LinkedHashMap<>();
    }

    public void agregar(String nombre, String token, String tipo, String valor, String longitud) {
        if (!simbolos.containsKey(nombre)) {
            simbolos.put(nombre, new Simbolo(nombre, token, tipo, valor, longitud));
        }
    }

    public void setTipo(String nombre, Tipo tipo) {
        tiposDeclarados.put(nombre, tipo);
        Simbolo simbolo = simbolos.get(nombre);
        if (simbolo != null) {
            simbolo.setTipo(tipo.etiqueta());
        }
    }

    public Tipo getTipo(String nombre) {
        return tiposDeclarados.get(nombre);
    }

    public boolean estaDeclarado(String nombre) {
        return tiposDeclarados.containsKey(nombre);
    }

    public void escribirArchivo(String ruta) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.println("NOMBRE      TOKEN       TIPO  VALOR   LONG");
            for (Simbolo simbolo : simbolos.values()) {
                pw.println(simbolo.toString());
            }
        }
    }

    public void limpiar() {
        simbolos.clear();
        tiposDeclarados.clear();
    }

    public int size() {
        return simbolos.size();
    }

    public boolean containsKey(String nombre) {
        return simbolos.containsKey(nombre);
    }

    public Simbolo get(String nombre) {
        return simbolos.get(nombre);
    }

    public Iterable<Map.Entry<String, Simbolo>> getEntries() {
        return simbolos.entrySet();
    }
}
