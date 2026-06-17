package uy.edu.um.doors;

import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.list.Node;

public class Proceso implements Comparable<Proceso> {

    private final int pid;
    private final String nombre;
    private final Usuario usuario;
    private final MyList<Evento> eventos;

    private int prioridad;
    private EstadoProceso estado;
    private TipoFinalizacion tipoFinalizacion;
    private Usuario usuarioTerminador;

    private int cantCPU;
    private int cantRAM;
    private int cantDISK;
    private int cantEventos;

    public Proceso(int pid, String nombre, Usuario usuario, MyList<Evento> eventos) {
        if (pid <= 0) {
            throw new IllegalArgumentException("PID inválido");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de proceso inválido");
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        if (eventos == null || eventos.isEmpty()) {
            throw new IllegalArgumentException("El proceso debe tener al menos un evento");
        }

        this.pid = pid;
        this.nombre = nombre.trim();
        this.usuario = usuario;
        this.eventos = eventos;

        this.prioridad = 0;
        this.estado = EstadoProceso.NEW;
        this.tipoFinalizacion = null;
        this.usuarioTerminador = null;

        this.cantCPU = 0;
        this.cantRAM = 0;
        this.cantDISK = 0;
        this.cantEventos = 0;

        calcularContadores();
    }

    private void calcularContadores() {
        Node<Evento> nodo = eventos.getFirst();

        while (nodo != null) {
            Evento evento = nodo.getValue();

            cantEventos++;

            if (evento.getTipo() == TipoEvento.CPU) {
                cantCPU++;
            } else if (evento.getTipo() == TipoEvento.RAM) {
                cantRAM++;
            } else if (evento.getTipo() == TipoEvento.DISK) {
                cantDISK++;
            }

            nodo = nodo.getNext();
        }
    }

    public void calcularPrioridad() {
        int pesoUsuario = usuario.getPeso();

        this.prioridad =
                ((8 * cantCPU + 2 * cantRAM + 2 * cantDISK) / cantEventos)
                        + pesoUsuario * cantEventos;
    }

    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(this.prioridad, otro.prioridad);
    }

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public MyList<Evento> getEventos() {
        return eventos;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado de proceso inválido");
        }

        this.estado = estado;
    }

    public TipoFinalizacion getTipoFinalizacion() {
        return tipoFinalizacion;
    }

    public void setTipoFinalizacion(TipoFinalizacion tipoFinalizacion) {
        if (tipoFinalizacion == null) {
            throw new IllegalArgumentException("Tipo de finalización inválido");
        }

        this.tipoFinalizacion = tipoFinalizacion;
    }

    public Usuario getUsuarioTerminador() {
        return usuarioTerminador;
    }

    public void setUsuarioTerminador(Usuario usuarioTerminador) {
        this.usuarioTerminador = usuarioTerminador;
    }

    public int getCantCPU() {
        return cantCPU;
    }

    public int getCantRAM() {
        return cantRAM;
    }

    public int getCantDISK() {
        return cantDISK;
    }

    public int getCantEventos() {
        return cantEventos;
    }

    @Override
    public String toString() {
        return "PID=" + pid
                + " | " + nombre
                + " | USER:" + usuario.getAlias()
                + " UID:" + usuario.getUid()
                + " | STATE:" + estado
                + " | P=" + prioridad;
    }
}