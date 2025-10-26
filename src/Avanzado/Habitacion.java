package Avanzado;

import java.io.*;

public class Habitacion {
    private int Numero;
    private String Tipo; // Individual, Doble, Suite
    private double PrecioPorNoche;
    private boolean Disponible;

    public Habitacion(int Numero, String Tipo, double PrecioPorNoche, boolean Disponible) throws IOException {
        this.Numero = Numero;
        this.Tipo = Tipo;
        this.PrecioPorNoche = PrecioPorNoche;
        this.Disponible = Disponible;
    }

    public int getNumero() {
        return Numero;
    }

    public void setNumero(int numero) {
        this.Numero = numero;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        this.Tipo = tipo;
    }

    public double getPrecioPorNoche() {
        return PrecioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.PrecioPorNoche = precioPorNoche;
    }

    public boolean isDisponible() {
        return Disponible;
    }

    public void setDisponible(boolean disponible) {
        this.Disponible = disponible;
    }
}