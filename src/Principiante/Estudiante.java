package Principiante;

import java.io.*;

public class Estudiante {
    private int Id;
    private String Nombre;
    private String Apellidos;
    private int edad;
    private double nota;

    public Estudiante(int Id, String Nombre, String Apellidos, int edad, double nota) throws IOException {
        this.Id = Id;
        this.Nombre = Nombre;
        this.Apellidos = Apellidos;
        this.edad = edad;
        this.nota = nota;
    }

    public int getId() {

        return Id;
    }

    public void setId(int id) {

        this.Id = id;
    }

    public String getNombre() {

        return Nombre;
    }

    public void setNombre(String nombre) {

        this.Nombre = nombre;
    }

    public String getApellidos() {

        return Apellidos;
    }

    public void setApellidos(String apellidos) {

        this.Apellidos = apellidos;
    }

    public int getEdad() {

        return edad;
    }

    public void setEdad(int edad) {

        this.edad = edad;
    }

    public double getNota() {

        return nota;
    }

    public void setNota(double nota) {

        this.nota = nota;
    }

}
