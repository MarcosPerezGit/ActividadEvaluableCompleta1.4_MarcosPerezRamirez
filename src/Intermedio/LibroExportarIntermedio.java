package Intermedio;

import java.io.*;
import java.text.DecimalFormat;   // Para redondear números decimales
import java.time.LocalDate;       // Para obtener la fecha actual
import java.util.*;               // Para usar listas y mapas

public class LibroExportarIntermedio {

    public static void main(String[] args) throws IOException {
        List<Libro> libros = Arrays.asList(
                new Libro("978-84-123", "El Quijote", "Miguel de Cervantes", "Ficción", 1605, 863, true, 150),
                new Libro("978-84-456", "Cien años de soledad", "Gabriel García Márquez", "Ficción", 1967, 471, false, 98),
                new Libro("978-84-789", "Breves respuestas a las grandes preguntas", "Stephen Hawking", "Ciencia", 2018, 256, true, 65),
                new Libro("978-84-321", "El gen egoísta", "Richard Dawkins", "Ciencia", 1976, 480, true, 72),
                new Libro("978-84-654", "La sombra del viento", "Carlos Ruiz Zafón", "Ficción", 2001, 576, true, 120)
        );

        exportarCSV(libros);
        exportarXML(libros);
        exportarJSON(libros);
    }

    // Exportamos a CSV con categorías
    public static void exportarCSV(List<Libro> libros) throws IOException {
        Map<String, List<Libro>> porCategoria = agruparPorCategoria(libros);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("biblioteca.csv"))) {
            bw.write("# BIBLIOTECA MUNICIPAL - CATÁLOGO DE LIBROS\n");

            for (Map.Entry<String, List<Libro>> entry : porCategoria.entrySet()) {
                String categoria = entry.getKey();
                List<Libro> lista = entry.getValue();

                bw.write("\n# CATEGORÍA: " + categoria + "\n\n");
                bw.write("ISBN;Título;Autor;Año;Páginas;Disponible;Préstamos\n");

                int subtotalLibros = 0;
                int subtotalPrestamos = 0;

                for (Libro l : lista) {
                    bw.write(
                            csv(l.getIsbn()) + ";" +
                                    csv(l.getTitulo()) + ";" +
                                    csv(l.getAutor()) + ";" +
                                    l.getAnioPublicacion() + ";" +
                                    l.getNumPaginas() + ";" +
                                    l.isDisponible() + ";" +
                                    l.getPrestamos() + "\n"
                    );
                    subtotalLibros++;
                    subtotalPrestamos += l.getPrestamos();
                }

                bw.write("# Subtotal " + categoria + ": " + subtotalLibros + " libros, " + subtotalPrestamos + " préstamos\n");
            }

            System.out.println("Archivo CSV creado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo CSV: " + e.getMessage());
        }
    }

    // Exportamos a XML con estructura compleja
    public static void exportarXML(List<Libro> libros) throws IOException {
        Map<String, List<Libro>> porCategoria = agruparPorCategoria(libros);

        int totalLibros = libros.size();
        int librosDisponibles = (int) libros.stream().filter(Libro::isDisponible).count();
        int librosPrestados = totalLibros - librosDisponibles;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("biblioteca.xml"))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<biblioteca>\n");
            bw.write("  <informacion>\n");
            bw.write("    <nombre>Biblioteca Municipal</nombre>\n");
            bw.write("    <fecha>" + LocalDate.now() + "</fecha>\n");
            bw.write("    <totalLibros>" + totalLibros + "</totalLibros>\n");
            bw.write("  </informacion>\n");

            bw.write("  <categorias>\n");

            for (Map.Entry<String, List<Libro>> entry : porCategoria.entrySet()) {
                String categoria = entry.getKey();
                List<Libro> lista = entry.getValue();

                int totalLibrosCat = lista.size();
                int totalPrestCat = lista.stream().mapToInt(Libro::getPrestamos).sum();
                double mediaPrestCat = totalLibrosCat > 0 ? (double) totalPrestCat / totalLibrosCat : 0.0;

                bw.write("    <categoria nombre=\"" + escapeXmlAttr(categoria) + "\" totalLibros=\"" + totalLibrosCat + "\">\n");

                for (Libro l : lista) {
                    bw.write("      <libro isbn=\"" + escapeXmlAttr(l.getIsbn()) + "\" disponible=\"" + l.isDisponible() + "\">\n");
                    bw.write("        <titulo>" + escapeXml(l.getTitulo()) + "</titulo>\n");
                    bw.write("        <autor>" + escapeXml(l.getAutor()) + "</autor>\n");
                    bw.write("        <año>" + l.getAnioPublicacion() + "</año>\n");
                    bw.write("        <paginas>" + l.getNumPaginas() + "</paginas>\n");
                    bw.write("        <prestamos>" + l.getPrestamos() + "</prestamos>\n");
                    bw.write("      </libro>\n");
                }

                bw.write("      <estadisticas>\n");
                bw.write("        <totalPrestamos>" + totalPrestCat + "</totalPrestamos>\n");
                bw.write("        <prestamosMedio>" + new DecimalFormat("0.00").format(mediaPrestCat) + "</prestamosMedio>\n");
                bw.write("      </estadisticas>\n");

                bw.write("    </categoria>\n");
            }

            bw.write("  </categorias>\n");

            bw.write("  <resumenGlobal>\n");
            bw.write("    <totalCategorias>" + porCategoria.size() + "</totalCategorias>\n");
            bw.write("    <totalLibros>" + totalLibros + "</totalLibros>\n");
            bw.write("    <librosDisponibles>" + librosDisponibles + "</librosDisponibles>\n");
            bw.write("    <librosPrestados>" + librosPrestados + "</librosPrestados>\n");
            bw.write("  </resumenGlobal>\n");

            bw.write("</biblioteca>\n");

            System.out.println("Archivo XML creado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo XML: " + e.getMessage());
        }
    }

    // Exportamos a JSON con datos anidados
    public static void exportarJSON(List<Libro> libros) throws IOException {
        Map<String, List<Libro>> porCategoria = agruparPorCategoria(libros);

        int totalLibros = libros.size();
        int librosDisponibles = (int) libros.stream().filter(Libro::isDisponible).count();
        int librosPrestados = totalLibros - librosDisponibles;
        int totalPrestamosHistorico = libros.stream().mapToInt(Libro::getPrestamos).sum();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("biblioteca.json"))) {
            bw.write("{\n");
            bw.write("  \"biblioteca\": {\n");

            bw.write("    \"informacion\": {\n");
            bw.write("      \"nombre\": \"Biblioteca Municipal\",\n");
            bw.write("      \"fecha\": \"" + LocalDate.now() + "\",\n");
            bw.write("      \"totalLibros\": " + totalLibros + "\n");
            bw.write("    },\n");

            bw.write("    \"categorias\": {\n");
            int cIndex = 0;
            int cSize = porCategoria.size();
            for (Map.Entry<String, List<Libro>> entry : porCategoria.entrySet()) {
                String categoria = entry.getKey();
                List<Libro> lista = entry.getValue();

                int totalLibrosCat = lista.size();
                int totalPrestCat = lista.stream().mapToInt(Libro::getPrestamos).sum();
                double mediaPrestCat = totalLibrosCat > 0 ? (double) totalPrestCat / totalLibrosCat : 0.0;
                String libroMasPrestado = lista.stream().max(Comparator.comparingInt(Libro::getPrestamos)).map(Libro::getTitulo).orElse("");

                bw.write("      " + quote(categoria) + ": {\n");
                bw.write("        \"totalLibros\": " + totalLibrosCat + ",\n");

                bw.write("        \"libros\": [\n");
                for (int i = 0; i < lista.size(); i++) {
                    Libro l = lista.get(i);
                    bw.write("          {\n");
                    bw.write("            \"isbn\": " + quote(l.getIsbn()) + ",\n");
                    bw.write("            \"titulo\": " + quote(l.getTitulo()) + ",\n");
                    bw.write("            \"autor\": " + quote(l.getAutor()) + ",\n");
                    bw.write("            \"año\": " + l.getAnioPublicacion() + ",\n");
                    bw.write("            \"paginas\": " + l.getNumPaginas() + ",\n");
                    bw.write("            \"disponible\": " + l.isDisponible() + ",\n");
                    bw.write("            \"prestamos\": " + l.getPrestamos() + "\n");
                    bw.write("          }" + (i < lista.size() - 1 ? "," : "") + "\n");
                }
                bw.write("        ],\n");

                bw.write("        \"estadisticas\": {\n");
                bw.write("          \"totalPrestamos\": " + totalPrestCat + ",\n");
                bw.write("          \"prestamosMedio\": " + new DecimalFormat("0.00").format(mediaPrestCat) + ",\n");
                bw.write("          \"libroMasPrestado\": " + quote(libroMasPrestado) + "\n");
                bw.write("        }\n");

                bw.write("      }" + (++cIndex < cSize ? "," : "") + "\n");
            }
            bw.write("    },\n");

            bw.write("    \"resumenGlobal\": {\n");
            bw.write("      \"totalCategorias\": " + porCategoria.size() + ",\n");
            bw.write("      \"totalLibros\": " + totalLibros + ",\n");
            bw.write("      \"librosDisponibles\": " + librosDisponibles + ",\n");
            bw.write("      \"librosPrestados\": " + librosPrestados + ",\n");
            bw.write("      \"totalPrestamosHistorico\": " + totalPrestamosHistorico + "\n");
            bw.write("    }\n");

            bw.write("  }\n");
            bw.write("}\n");

            System.out.println("Archivo JSON generado correctamente (sin Gson).");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo JSON: " + e.getMessage());
        }
    }

    private static Map<String, List<Libro>> agruparPorCategoria(List<Libro> libros) {
        // Mantenemos el orden de primera aparición de categorías
        Map<String, List<Libro>> mapa = new LinkedHashMap<>();
        for (Libro l : libros) {
            mapa.computeIfAbsent(l.getCategoria(), k -> new ArrayList<>()).add(l);
        }
        return mapa;
    }

    // Escapado para CSV (separador ';' y comillas)
    private static String csv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(";") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String val = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + val + "\"" : val;
    }

    // Escapar contenido XML
    private static String escapeXml(String s) {
        if (s == null) return "";
        String out = s;
        out = out.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        return out;
    }

    // Escapar atributos XML
    private static String escapeXmlAttr(String s) {
        if (s == null) return "";
        String out = escapeXml(s);
        out = out.replace("\"", "&quot;").replace("'", "&apos;");
        return out;
    }

    // Escapar/entrecomillar JSON
    private static String quote(String s) {
        return "\"" + escapeJson(s) + "\"";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
}


