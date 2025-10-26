package Avanzado;

import java.io.*;

public class Cliente {
    private int Id;
    private String Nombre;
    private String Email;
    private String Telefono;

    public Cliente(int Id, String Nombre, String Email, String Telefono) throws IOException {
        this.Id = Id;
        this.Nombre = Nombre;
        this.Email = Email;
        this.Telefono = Telefono;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }
}
