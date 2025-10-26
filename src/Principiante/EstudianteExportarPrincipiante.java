package Principiante;

import java.io.*;
import java.text.DecimalFormat;   // Para redondear números decimales
import java.time.LocalDate;       // Para obtener la fecha actual
import java.util.*;

public class EstudianteExportarPrincipiante {

    public static void main(String[] args) throws IOException {
        List<Estudiante> estudiantes = Arrays.asList(
                new Estudiante(1, "Juan", "García López", 20, 8.5),
                new Estudiante(2, "María", "Rodríguez", 19, 9.2),
                new Estudiante(3, "Pedro", "Martínez", 21, 7.8),
                new Estudiante(4, "Ana", "López", 20, 8.9),
                new Estudiante(5, "Carlos", "Sánchez", 22, 6.5)
        );

        exportarCSV(estudiantes);
        exportarXML(estudiantes);
        exportarJSON(estudiantes);
    }

    public static void exportarCSV(List<Estudiante> estudiantes) throws IOException {
        // Implementación de exportación a CSV
        DecimalFormat df = new DecimalFormat("0.00");
        double notaMedia = estudiantes.stream().mapToDouble(Estudiante::getNota).average().orElse(0.0);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("estudiantes.csv"))) {
            bw.write("ID;Nombre;Apellidos;Edad;Nota\n");

            for(Estudiante estudiante : estudiantes) {
                bw.write(estudiante.getId() + ";" + estudiante.getNombre() + ";" + estudiante.getApellidos() + ";" + estudiante.getEdad() + ";" + df.format(estudiante.getNota()) + "\n");

            }
            bw.write(" # Nota media: " + df.format(notaMedia) + "\n");
            System.out.println("Archivo CSV creado correctamente.");

        }catch (IOException e) {
            System.out.println("Error al crear el archivo CSV: " + e.getMessage());
        }
    }

    public static void exportarXML(List<Estudiante> estudiantes) throws IOException {
        // Implementación de exportación a XML
        double notaMedia = estudiantes.stream().mapToDouble(Estudiante::getNota).average().orElse(0.0);
        double notaMaxima = estudiantes.stream().mapToDouble(Estudiante::getNota).max().orElse(0.0);
        double notaMinima = estudiantes.stream().mapToDouble(Estudiante::getNota).min().orElse(0.0);


        try(BufferedWriter bw = new BufferedWriter(new FileWriter("estudiantes.xml"))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<clase>\n");

            bw.write(" <metadata>\n");
            bw.write("   <fecha>" + LocalDate.now() + "</fecha>\n");
            bw.write("   <totalEstudiantes>" + estudiantes.size() + "</totalEstudiantes>\n");
            bw.write(" </metadata>\n");

            bw.write(" <estudiantes>\n");

            for (Estudiante estudiante : estudiantes) {
                bw.write("  <estudiante id=\"" + estudiante.getId() + "\">\n");
                bw.write("    <nombre>" + estudiante.getNombre() + "</nombre>\n");
                bw.write("    <apellidos>" + estudiante.getApellidos() + "</apellidos>\n");
                bw.write("    <edad>" + estudiante.getEdad() + "</edad>\n");
                bw.write("    <nota>" + estudiante.getNota() + "</nota>\n");
                bw.write("  </estudiante>\n");
            }
            bw.write(" </estudiantes>\n");

            bw.write("  <estadisticas>\n");
            bw.write("    <notaMedia>" + notaMedia + "</notaMedia>\n");
            bw.write("    <notaMaxima>" + notaMaxima + "</notaMaxima>\n");
            bw.write("    <notaMinima>" + notaMinima + "</notaMinima>\n");
            bw.write("  </estadisticas>\n");

            bw.write("</clase>\n");
            System.out.println("Archivo XML creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo XML: " + e.getMessage());
        }
    }
    public static void exportarJSON(List<Estudiante> estudiantes) throws IOException {
        // Implementación de exportación a JSON

        double notaMedia = estudiantes.stream().mapToDouble(Estudiante::getNota).average().orElse(0.0);
        double notaMaxima = estudiantes.stream().mapToDouble(Estudiante::getNota).max().orElse(0.0);
        double notaMinima = estudiantes.stream().mapToDouble(Estudiante::getNota).min().orElse(0.0);
        double aprobados = estudiantes.stream().filter(e -> e.getNota() >= 5).count();
        double suspensos = estudiantes.stream().filter(e -> e.getNota() < 5).count();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("estudiantes.json"))) {
            bw.write("{\n");
            bw.write("  \"clase\": {\n");
            bw.write("    \"metadata\": {\n");
            bw.write("      \"fecha\": \"" + LocalDate.now() + "\",\n");
            bw.write("      \"totalEstudiantes\": " + estudiantes.size() + "\n");
            bw.write("    },\n");

            bw.write("    \"estudiantes\": [\n");
            for (int i = 0; i < estudiantes.size(); i++) {
                Estudiante e = estudiantes.get(i);
                bw.write("      {\n");
                bw.write("        \"id\": " + e.getId() + ",\n");
                bw.write("        \"nombre\": \"" + e.getNombre() + "\",\n");
                bw.write("        \"apellidos\": \"" + e.getApellidos() + "\",\n");
                bw.write("        \"edad\": " + e.getEdad() + ",\n");
                bw.write("        \"nota\": " + e.getNota() + "\n");
                // Añadimos coma entre estudiantes, excepto en el último
                bw.write("      }" + (i < estudiantes.size() - 1 ? "," : "") + "\n");
            }
            bw.write("    ],\n");

            bw.write("    \"estadisticas\": {\n");
            bw.write("      \"notaMedia\": " + notaMedia + ",\n");
            bw.write("      \"notaMaxima\": " + notaMaxima + ",\n");
            bw.write("      \"notaMinima\": " + notaMinima + ",\n");
            bw.write("      \"aprobados\": " + aprobados + ",\n");
            bw.write("      \"suspensos\": " + suspensos + "\n");
            bw.write("    }\n");

            bw.write("  }\n");
            bw.write("}");
            System.out.println("Archivo JSON generado correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


