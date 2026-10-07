package ar.edu.unlu.compilador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class TablaSimbolos {
    private LinkedHashMap<String, Simbolo> simbolos;

    public TablaSimbolos() {
        this.simbolos = new LinkedHashMap<>();
    }

    public void agregar(String nombre, String token, String tipo, String valor, String longitud) {
        if (!simbolos.containsKey(nombre)) {
            simbolos.put(nombre, new Simbolo(nombre, token, tipo, valor, longitud));
        }
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
