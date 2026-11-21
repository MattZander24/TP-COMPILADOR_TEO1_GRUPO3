package ar.edu.unlu.compilador;

public enum Tipo {
    INTEGER("Integer"),
    FLOAT("Float"),
    STRING("String"),
    ERROR("—");

    private final String etiqueta;

    Tipo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}
