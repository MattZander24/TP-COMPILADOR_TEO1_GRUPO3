package ar.edu.unlu.compilador;

import java.util.LinkedList;

public class DeclInfo {
    public LinkedList<String> vars = new LinkedList<>();
    public LinkedList<Tipo> tipos = new LinkedList<>();
    // posición [línea, columna] de cada variable, paralela a vars
    public LinkedList<int[]> posiciones = new LinkedList<>();
}
