package uy.edu.um.doors;

import uy.edu.um.tad.list.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter LOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String fileName;

    public LogManager() {
        this.fileName = "DOORS_PROCESS_LOG_" +
                LocalDate.now().format(FILE_DATE_FORMAT) +
                ".txt";
    }


    public void logProcesoFinalizadoEnOverflow(Proceso proceso) {
        writeRaw("PID=" + proceso.getPid()
                + " " + proceso.getNombre()
                + " | STATE: " + proceso.getTipoFinalizacion()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid());
    }

    public void logNewPending(Proceso proceso) {
        write("NEW PENDING PROCESS: PID=" + proceso.getPid()
                + " | " + proceso.getNombre()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid()
                + " | P=" + proceso.getPrioridad());
    }

    public void logExecutingProcess(Proceso proceso) {
        write("EXECUTING PROCESS: PID=" + proceso.getPid()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid());

        Node<Evento> nodoEvento = proceso.getEventos().getFirst();

        while (nodoEvento != null) {
            Evento evento = nodoEvento.getValue();

            StringBuilder instrucciones = new StringBuilder();
            instrucciones.append("EVENT: ")
                    .append(evento.getTipo())
                    .append(" | Instructions [");

            Node<String> nodoInstruccion = evento.getInstrucciones().getFirst();

            while (nodoInstruccion != null) {
                instrucciones.append(nodoInstruccion.getValue());

                if (nodoInstruccion.getNext() != null) {
                    instrucciones.append(", ");
                }

                nodoInstruccion = nodoInstruccion.getNext();
            }

            instrucciones.append("]");

            write(instrucciones.toString());

            nodoEvento = nodoEvento.getNext();
        }
    }

    public void logEndingProcess(Proceso proceso) {
        String mensaje = "ENDING PROCESS: PID=" + proceso.getPid()
                + " | STATE: " + proceso.getTipoFinalizacion();

        if (proceso.getTipoFinalizacion() == TipoFinalizacion.TERMINATED
                && proceso.getUsuarioTerminador() != null) {

            mensaje += " by USER:" + proceso.getUsuarioTerminador().getAlias()
                    + " UID:" + proceso.getUsuarioTerminador().getUid();
        }

        write(mensaje);
    }

    public void logFinishedStackOverflow() {
        write("Finished process stack overflow");
    }

    public void logProcesoDescartadoPorOverflow(Proceso proceso) {
        write("DISCARDED FINISHED PROCESS: PID=" + proceso.getPid()
                + " | " + proceso.getNombre()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid()
                + " | STATE:" + proceso.getTipoFinalizacion());
    }

    private void writeRaw(String message) {
        System.out.println(message);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.out.println("ERROR escribiendo log: " + e.getMessage());
        }
    }

    private void write(String message) {
        String line = "[" + LocalDateTime.now().format(LOG_DATE_FORMAT) + "]: " + message;

        System.out.println(line);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(line);
        } catch (IOException e) {
            System.out.println("ERROR escribiendo log: " + e.getMessage());
        }
    }
}