package Avanzado;

import java.io.*;
import java.text.DecimalFormat;   // Para formatear precios
import java.time.LocalDate;       // Para fechas
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;               // Para listas y mapas

public class HotelExportarAvanzado {

    public static void main(String[] args) throws IOException {

        Cliente c1 = new Cliente(1, "Juan García", "juan@email.com", "666111222");
        Cliente c2 = new Cliente(2, "María López", "maria@email.com", "666222333");
        Cliente c3 = new Cliente(3, "Pedro Ruiz", "pedro@email.com", "666333444");

        Habitacion h101 = new Habitacion(101, "Doble", 90.00, false);
        Habitacion h205 = new Habitacion(205, "Suite", 200.00, false);
        Habitacion h301 = new Habitacion(301, "Individual", 50.00, true);
        Habitacion h201 = new Habitacion(201, "Doble", 90.00, true);

        // Usamos LocalDate.parse ISO. Formatearemos al exportar.
        LocalDate f1e = LocalDate.parse("2025-10-20");
        LocalDate f1s = LocalDate.parse("2025-10-23");
        LocalDate f2e = LocalDate.parse("2025-10-21");
        LocalDate f2s = LocalDate.parse("2025-10-25");
        LocalDate f3e = LocalDate.parse("2025-09-10");
        LocalDate f3s = LocalDate.parse("2025-09-12");
        LocalDate f4e = LocalDate.parse("2025-10-22");
        LocalDate f4s = LocalDate.parse("2025-10-24");
        LocalDate f5e = LocalDate.parse("2025-10-28");
        LocalDate f5s = LocalDate.parse("2025-10-30");

        List<Reserva> reservas = Arrays.asList(
                crearReserva(1, c1, h101, f1e, f1s, "Confirmada"),
                crearReserva(2, c2, h205, f2e, f2s, "Confirmada"),
                crearReserva(3, c1, h101, f3e, f3s, "Completada"),
                crearReserva(4, c3, h301, f4e, f4s, "Cancelada"),
                crearReserva(5, c3, h201, f5e, f5s, "Confirmada")
        );

        exportarCSV(reservas, "Hotel Paradise");
        exportarXML(reservas, "Hotel Paradise");
        exportarJSON(reservas, "Hotel Paradise");
    }

    // Utilidad para crear reserva calculando noches y precioTotal
    private static Reserva crearReserva(int id, Cliente cliente, Habitacion habitacion, LocalDate entrada, LocalDate salida, String estado) throws IOException {
        int noches = (int) ChronoUnit.DAYS.between(entrada, salida);
        double precioTotal = noches * habitacion.getPrecioPorNoche();
        return new Reserva(id, cliente, habitacion, entrada, salida, noches, precioTotal, estado);
    }

    // Exportamos CSV aplanando relaciones
    public static void exportarCSV(List<Reserva> reservas, String nombreHotel) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("0.00");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("hotel_reservas.csv"))) {
            bw.write("# " + nombreHotel.toUpperCase() + " - RESERVAS\n");
            bw.write("ID;ClienteNombre;ClienteEmail;HabitacionNum;TipoHabitacion;FechaEntrada;FechaSalida;Noches;PrecioTotal;Estado\n");

            for (Reserva r : reservas) {
                bw.write(
                        r.getId() + ";" +
                                csv(r.getCliente().getNombre()) + ";" +
                                csv(r.getCliente().getEmail()) + ";" +
                                r.getHabitacion().getNumero() + ";" +
                                csv(r.getHabitacion().getTipo()) + ";" +
                                dtf.format(r.getFechaEntrada()) + ";" +
                                dtf.format(r.getFechaSalida()) + ";" +
                                r.getNoches() + ";" +
                                df.format(r.getPrecioTotal()) + ";" +
                                csv(r.getEstado()) + "\n"
                );
            }

            System.out.println("Archivo CSV creado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo CSV: " + e.getMessage());
        }
    }

    // Exportamos XML con relaciones complejas y estadísticas
    public static void exportarXML(List<Reserva> reservas, String nombreHotel) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("0.00");

        int totalReservas = reservas.size();
        Map<String, Integer> porEstado = contarPorEstado(reservas);
        Map<String, TipoStats> porTipo = estadisticasPorTipo(reservas);
        int nochesReservadas = reservas.stream().mapToInt(Reserva::getNoches).sum();
        double ingresosTotal = reservas.stream().mapToDouble(Reserva::getPrecioTotal).sum();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("hotel_reservas.xml"))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<hotel>\n");

            bw.write("  <informacion>\n");
            bw.write("    <nombre>" + escapeXml(nombreHotel) + "</nombre>\n");
            bw.write("    <fecha>" + LocalDate.now().format(dtf) + "</fecha>\n");
            bw.write("  </informacion>\n");

            bw.write("  <reservas totalReservas=\"" + totalReservas + "\">\n");
            for (Reserva r : reservas) {
                bw.write("    <reserva id=\"" + r.getId() + "\" estado=\"" + escapeXmlAttr(r.getEstado()) + "\">\n");


                bw.write("      <cliente>\n");
                bw.write("        <id>" + r.getCliente().getId() + "</id>\n");
                bw.write("        <nombre>" + escapeXml(r.getCliente().getNombre()) + "</nombre>\n");
                bw.write("        <email>" + escapeXml(r.getCliente().getEmail()) + "</email>\n");
                bw.write("        <telefono>" + escapeXml(r.getCliente().getTelefono()) + "</telefono>\n");
                bw.write("      </cliente>\n");


                bw.write("      <habitacion numero=\"" + r.getHabitacion().getNumero() + "\" tipo=\"" + escapeXmlAttr(r.getHabitacion().getTipo()) + "\">\n");
                bw.write("        <precioPorNoche>" + df.format(r.getHabitacion().getPrecioPorNoche()) + "</precioPorNoche>\n");
                bw.write("        <disponible>" + r.getHabitacion().isDisponible() + "</disponible>\n");
                bw.write("      </habitacion>\n");


                bw.write("      <fechas>\n");
                bw.write("        <entrada>" + dtf.format(r.getFechaEntrada()) + "</entrada>\n");
                bw.write("        <salida>" + dtf.format(r.getFechaSalida()) + "</salida>\n");
                bw.write("        <noches>" + r.getNoches() + "</noches>\n");
                bw.write("      </fechas>\n");


                bw.write("      <precio>\n");
                bw.write("        <total>" + df.format(r.getPrecioTotal()) + "</total>\n");
                bw.write("        <porNoche>" + df.format(r.getHabitacion().getPrecioPorNoche()) + "</porNoche>\n");
                bw.write("      </precio>\n");

                bw.write("    </reserva>\n");
            }
            bw.write("  </reservas>\n");


            bw.write("  <estadisticas>\n");

            bw.write("    <porTipoHabitacion>\n");
            for (Map.Entry<String, TipoStats> e : porTipo.entrySet()) {
                String tipo = e.getKey();
                TipoStats st = e.getValue();
                bw.write("      <" + escapeXmlTag(tipo) + ">\n");
                bw.write("        <totalReservas>" + st.totalReservas + "</totalReservas>\n");
                bw.write("        <ingresos>" + df.format(st.ingresos) + "</ingresos>\n");
                bw.write("      </" + escapeXmlTag(tipo) + ">\n");
            }
            bw.write("    </porTipoHabitacion>\n");

            bw.write("    <porEstado>\n");
            for (Map.Entry<String, Integer> e : porEstado.entrySet()) {
                String est = e.getKey();
                Integer cnt = e.getValue();
                bw.write("      <" + escapeXmlTag(est) + ">" + cnt + "</" + escapeXmlTag(est) + ">\n");
            }
            bw.write("    </porEstado>\n");

            bw.write("    <resumen>\n");
            bw.write("      <totalReservas>" + totalReservas + "</totalReservas>\n");
            bw.write("      <ingresosTotal>" + df.format(ingresosTotal) + "</ingresosTotal>\n");
            bw.write("      <nochesReservadas>" + nochesReservadas + "</nochesReservadas>\n");
            bw.write("    </resumen>\n");

            bw.write("  </estadisticas>\n");

            bw.write("</hotel>\n");

            System.out.println("Archivo XML creado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo XML: " + e.getMessage());
        }
    }

    // Exportamos JSON eficiente
    public static void exportarJSON(List<Reserva> reservas, String nombreHotel) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("0.00");

        // Construimos diccionarios únicos de clientes y habitaciones
        LinkedHashMap<Integer, Cliente> clientes = new LinkedHashMap<>();
        LinkedHashMap<Integer, Habitacion> habitaciones = new LinkedHashMap<>();
        for (Reserva r : reservas) {
            clientes.putIfAbsent(r.getCliente().getId(), r.getCliente());
            habitaciones.putIfAbsent(r.getHabitacion().getNumero(), r.getHabitacion());
        }

        // Estadísticas
        Map<String, TipoStats> porTipo = estadisticasPorTipo(reservas);
        Map<String, Integer> porEstado = contarPorEstado(reservas);
        int totalReservas = reservas.size();
        int nochesReservadas = reservas.stream().mapToInt(Reserva::getNoches).sum();
        double ingresosTotal = reservas.stream().mapToDouble(Reserva::getPrecioTotal).sum();

        // Porcentaje por tipo basado en número de reservas del tipo
        Map<String, Double> porcentajeTipo = new LinkedHashMap<>();
        for (Map.Entry<String, TipoStats> e : porTipo.entrySet()) {
            double pct = totalReservas > 0 ? (e.getValue().totalReservas * 100.0) / totalReservas : 0.0;
            porcentajeTipo.put(e.getKey(), pct);
        }
        double ocupacionMedia = 0.0;
        if (!reservas.isEmpty()) {
            LocalDate minEntrada = reservas.stream().map(Reserva::getFechaEntrada).min(LocalDate::compareTo).orElse(LocalDate.now());
            LocalDate maxSalida = reservas.stream().map(Reserva::getFechaSalida).max(LocalDate::compareTo).orElse(LocalDate.now());
            long diasRango = Math.max(1, ChronoUnit.DAYS.between(minEntrada, maxSalida));
            ocupacionMedia = (habitaciones.size() > 0) ? (nochesReservadas * 100.0) / (habitaciones.size() * diasRango) : 0.0;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("hotel_reservas.json"))) {
            bw.write("{\n");
            bw.write("  \"hotel\": {\n");

            bw.write("    \"informacion\": {\n");
            bw.write("      \"nombre\": " + quote(nombreHotel) + ",\n");
            bw.write("      \"fecha\": " + quote(LocalDate.now().format(dtf)) + "\n");
            bw.write("    },\n");

            bw.write("    \"clientes\": {\n");
            int idx = 0;
            int size = clientes.size();
            for (Map.Entry<Integer, Cliente> e : clientes.entrySet()) {
                Cliente c = e.getValue();
                bw.write("      " + quote(String.valueOf(e.getKey())) + ": {\n");
                bw.write("        \"nombre\": " + quote(c.getNombre()) + ",\n");
                bw.write("        \"email\": " + quote(c.getEmail()) + ",\n");
                bw.write("        \"telefono\": " + quote(c.getTelefono()) + "\n");
                bw.write("      }" + (++idx < size ? "," : "") + "\n");
            }
            bw.write("    },\n");

            bw.write("    \"habitaciones\": {\n");
            idx = 0;
            size = habitaciones.size();
            for (Map.Entry<Integer, Habitacion> e : habitaciones.entrySet()) {
                Habitacion h = e.getValue();
                bw.write("      " + quote(String.valueOf(e.getKey())) + ": {\n");
                bw.write("        \"tipo\": " + quote(h.getTipo()) + ",\n");
                bw.write("        \"precioPorNoche\": " + df.format(h.getPrecioPorNoche()) + ",\n");
                bw.write("        \"disponible\": " + h.isDisponible() + "\n");
                bw.write("      }" + (++idx < size ? "," : "") + "\n");
            }
            bw.write("    },\n");

            bw.write("    \"reservas\": [\n");
            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                bw.write("      {\n");
                bw.write("        \"id\": " + r.getId() + ",\n");
                bw.write("        \"clienteId\": " + r.getCliente().getId() + ",\n");
                bw.write("        \"habitacionNumero\": " + r.getHabitacion().getNumero() + ",\n");
                bw.write("        \"fechaEntrada\": " + quote(r.getFechaEntrada().format(dtf)) + ",\n");
                bw.write("        \"fechaSalida\": " + quote(r.getFechaSalida().format(dtf)) + ",\n");
                bw.write("        \"noches\": " + r.getNoches() + ",\n");
                bw.write("        \"precioTotal\": " + df.format(r.getPrecioTotal()) + ",\n");
                bw.write("        \"estado\": " + quote(r.getEstado()) + "\n");
                bw.write("      }" + (i < reservas.size() - 1 ? "," : "") + "\n");
            }
            bw.write("    ],\n");

            bw.write("    \"estadisticas\": {\n");

            bw.write("      \"porTipoHabitacion\": {\n");
            int t = 0, tSize = porTipo.size();
            for (Map.Entry<String, TipoStats> e : porTipo.entrySet()) {
                String tipo = e.getKey();
                TipoStats st = e.getValue();
                bw.write("        " + quote(tipo) + ": {\n");
                bw.write("          \"totalReservas\": " + st.totalReservas + ",\n");
                bw.write("          \"ingresos\": " + df.format(st.ingresos) + ",\n");
                bw.write("          \"porcentaje\": " + new DecimalFormat("0.0").format(porcentajeTipo.get(tipo)) + "\n");
                bw.write("        }" + (++t < tSize ? "," : "") + "\n");
            }
            bw.write("      },\n");

            bw.write("      \"porEstado\": {\n");
            int eIdx = 0, eSize = porEstado.size();
            for (Map.Entry<String, Integer> e : porEstado.entrySet()) {
                bw.write("        " + quote(e.getKey()) + ": " + e.getValue() + ( (++eIdx < eSize) ? "," : "") + "\n");
            }
            bw.write("      },\n");

            bw.write("      \"resumen\": {\n");
            bw.write("        \"totalReservas\": " + totalReservas + ",\n");
            bw.write("        \"ingresosTotal\": " + df.format(ingresosTotal) + ",\n");
            bw.write("        \"nochesReservadas\": " + nochesReservadas + ",\n");
            bw.write("        \"ocupacionMedia\": " + new DecimalFormat("0.0").format(ocupacionMedia) + "\n");
            bw.write("      }\n");

            bw.write("    }\n");

            bw.write("  }\n");
            bw.write("}\n");

            System.out.println("Archivo JSON generado correctamente (sin Gson).");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo JSON: " + e.getMessage());
        }
    }


    private static Map<String, Integer> contarPorEstado(List<Reserva> reservas) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (Reserva r : reservas) {
            mapa.put(r.getEstado(), mapa.getOrDefault(r.getEstado(), 0) + 1);
        }
        return mapa;
    }

    private static Map<String, TipoStats> estadisticasPorTipo(List<Reserva> reservas) {
        Map<String, TipoStats> mapa = new LinkedHashMap<>();
        for (Reserva r : reservas) {
            String tipo = r.getHabitacion().getTipo();
            TipoStats st = mapa.getOrDefault(tipo, new TipoStats());
            st.totalReservas += 1;
            st.ingresos += r.getPrecioTotal();
            mapa.put(tipo, st);
        }
        return mapa;
    }

    private static class TipoStats {
        int totalReservas = 0;
        double ingresos = 0.0;
    }

    // CSV: escapado básico
    private static String csv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(";") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String val = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + val + "\"" : val;
    }

    // XML: escapar contenido
    private static String escapeXml(String s) {
        if (s == null) return "";
        String out = s;
        out = out.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        return out;
    }

    // XML: escapar atributos
    private static String escapeXmlAttr(String s) {
        if (s == null) return "";
        String out = escapeXml(s);
        out = out.replace("\"", "&quot;").replace("'", "&apos;");
        return out;
    }

    // XML: nombres de etiqueta seguros (reemplaza espacios y acentos simples)
    private static String escapeXmlTag(String s) {
        if (s == null || s.isEmpty()) return "Item";
        // Simplificación: reemplazar espacios por guiones y quitar caracteres no alfanuméricos básicos
        return s.replace(' ', '_')
                .replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u")
                .replace("Á","A").replace("É","E").replace("Í","I").replace("Ó","O").replace("Ú","U")
                .replace("ñ","n").replace("Ñ","N")
                .replaceAll("[^A-Za-z0-9_]", "");
    }

    // JSON: comillas + escapado
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
