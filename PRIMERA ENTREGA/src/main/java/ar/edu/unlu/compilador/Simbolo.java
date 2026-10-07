package ar.edu.unlu.compilador;

public class Simbolo {
    private String nombre;
    private String token;
    private String tipo;
    private String valor;
    private String longitud;

    public Simbolo(String nombre, String token, String tipo, String valor, String longitud) {
        this.nombre = nombre;
        this.token = token;
        this.tipo = tipo;
        this.valor = valor;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getLongitud() {
        return longitud;
    }

    @Override
    public String toString() {
        return String.format("%-12s %-12s %-6s %-8s %-6s",
            nombre, token, tipo.isEmpty() ? "—" : tipo, valor.isEmpty() ? "—" : valor, longitud.isEmpty() ? "—" : longitud);
    }
}
