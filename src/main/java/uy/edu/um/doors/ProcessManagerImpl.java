package uy.edu.um.doors;
//Imports

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.list.Node;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessManagerImpl implements ProcessManager{

   //EEstructuras de datos
    private MyHash<Integer, Usuario> usuariosPorUid;
    private MyHash<Integer, Proceso> procesosPorPid;
    private MyQueue<Proceso> procesosNew;
    private MyHeap<Proceso> procesosPending;
    private MyStack<Proceso> procesosFinished;
    private MyList<Proceso> procesosEnMemoria;
    private Proceso procesoEjecutando;
    private LogManager logManager;

    //Constructor
    public ProcessManagerImpl() {
        this.usuariosPorUid = new MyHashImpl<>();
        this.procesosPorPid = new MyHashImpl<>();
        this.procesosNew = new MyQueueImpl<>();
        // false = heap máximo, para ejecutar primero el proceso de mayor prioridad
        this.procesosPending = new MyHeapImpl<>(false);
        this.procesosFinished = new MyStackImpl<>();
        this.procesosEnMemoria = new MyLinkedListImpl<>();
        this.procesoEjecutando = null;
        this.logManager = new LogManager();
    }
    
//ACA EMPIEZA EL LOAD Y LAS FUNCIONES:
    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        try {
            cargarUsuarios(usersCsvPath);
            cargarProcesos(processCsvPath);
            System.out.println("Carga finalizada correctamente.");
        } catch (IOException e) {
            System.out.println("Error leyendo archivos CSV: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en datos de entrada: " + e.getMessage());
        }
    }

    private void cargarUsuarios(String usersCsvPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(usersCsvPath));

        String linea;
        int cantidadUsuarios = 0;
        int numeroLinea = 0;

        while ((linea = br.readLine()) != null) {
            numeroLinea++;
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            // Saltea encabezado del CSV: uid;alias;type
            if (numeroLinea == 1 && linea.toLowerCase().startsWith("uid;")) continue;

            // Por si el archivo viene con BOM invisible al inicio
            if (linea.startsWith("\uFEFF")) linea = linea.substring(1);

            String[] partes = linea.split(";");

            if (partes.length != 3) {
                br.close();
                throw new IllegalArgumentException("Formato inválido en users.csv línea " + numeroLinea);
            }

            int uid;
            try {
                uid = Integer.parseInt(partes[0].trim());
            } catch (NumberFormatException e) {
                br.close();
                throw new IllegalArgumentException("UID inválido en users.csv línea " + numeroLinea);
            }

            String alias = partes[1].trim();
            String tipoTexto = partes[2].trim();

            TipoUsuario tipo;
            try {

                tipo = TipoUsuario.valueOf(tipoTexto.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                br.close();
                throw new IllegalArgumentException("Tipo de usuario inválido en users.csv línea " + numeroLinea);
            }

            if (usuariosPorUid.contains(uid)) {
                br.close();
                throw new IllegalArgumentException("UID duplicado en users.csv línea " + numeroLinea + ": " + uid);
            }

            usuariosPorUid.put(uid, new Usuario(uid, alias, tipo));
            cantidadUsuarios++;
        }

        br.close();
        System.out.println("Usuarios cargados: " + cantidadUsuarios);
    }

    private void cargarProcesos(String processCsvPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(processCsvPath));

        String linea;
        int cantidadProcesos = 0;
        int numeroLinea = 0;

        while ((linea = br.readLine()) != null) {
            numeroLinea++;
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            // Por si el archivo viene con BOM invisible al inicio
            if (linea.startsWith("\uFEFF")) linea = linea.substring(1);

            // Saltea encabezado del CSV: pid;uid;name;events
            if (numeroLinea == 1 && linea.toLowerCase().startsWith("pid;")) continue;

            // split(";", 4) para no partir el campo events si tuviera ';'
            String[] partes = linea.split(";", 4);

            if (partes.length != 4) {
                br.close();
                throw new IllegalArgumentException("Formato inválido en process.csv línea " + numeroLinea);
            }

            int pid;
            int uid;

            try {
                pid = Integer.parseInt(partes[0].trim());
            } catch (NumberFormatException e) {
                br.close();
                throw new IllegalArgumentException("PID inválido en process.csv línea " + numeroLinea);
            }

            try {
                uid = Integer.parseInt(partes[1].trim());
            } catch (NumberFormatException e) {
                br.close();
                throw new IllegalArgumentException("UID inválido en process.csv línea " + numeroLinea);
            }

            String nombre = partes[2].trim();
            String eventosRaw = partes[3].trim();

            if (procesosPorPid.contains(pid)) {
                br.close();
                throw new IllegalArgumentException("PID duplicado en process.csv línea " + numeroLinea + ": " + pid);
            }

            Usuario usuario = usuariosPorUid.get(uid);
            if (usuario == null) {
                br.close();
                throw new IllegalArgumentException(
                        "Usuario inexistente para proceso en process.csv línea " + numeroLinea + ". UID: " + uid);
            }

            MyList<Evento> eventos = parsearEventos(eventosRaw, numeroLinea);
            Proceso proceso = new Proceso(pid, nombre, usuario, eventos);

            procesosNew.enqueue(proceso);
            procesosPorPid.put(pid, proceso);
            procesosEnMemoria.add(proceso);

            cantidadProcesos++;
        }

        br.close();
        System.out.println("Procesos cargados en NEW: " + cantidadProcesos);
    }

    private MyList<Evento> parsearEventos(String eventosRaw, int numeroLinea) {
        if (eventosRaw == null || eventosRaw.trim().isEmpty()) {
            throw new IllegalArgumentException("Eventos vacíos en process.csv línea " + numeroLinea);
        }

        eventosRaw = eventosRaw.trim();

        if (!eventosRaw.startsWith("{") || !eventosRaw.endsWith("}")) {
            throw new IllegalArgumentException("Formato de eventos inválido en process.csv línea " + numeroLinea);
        }

        String contenido = eventosRaw.substring(1, eventosRaw.length() - 1).trim();

        if (contenido.isEmpty()) {
            throw new IllegalArgumentException("Proceso sin eventos en process.csv línea " + numeroLinea);
        }

        MyList<Evento> eventos = new MyLinkedListImpl<>();
        String[] eventosSeparados = contenido.split("#");

        for (String eventoRaw : eventosSeparados) {
            eventoRaw = eventoRaw.trim();
            if (eventoRaw.isEmpty()) continue;

            int posicionDosPuntos = eventoRaw.indexOf(":");
            int posicionCorcheteInicio = eventoRaw.indexOf("[");
            int posicionCorcheteFin = eventoRaw.lastIndexOf("]");

            if (posicionDosPuntos == -1 || posicionCorcheteInicio == -1 || posicionCorcheteFin == -1
                    || posicionCorcheteInicio < posicionDosPuntos
                    || posicionCorcheteFin < posicionCorcheteInicio) {
                throw new IllegalArgumentException("Evento mal formado en process.csv línea " + numeroLinea);
            }

            String tipoTexto = eventoRaw.substring(0, posicionDosPuntos).trim();
            TipoEvento tipoEvento;
            try {
                tipoEvento = TipoEvento.valueOf(tipoTexto.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Tipo de evento inválido en process.csv línea " + numeroLinea + ": " + tipoTexto);
            }

            String instruccionesRaw = eventoRaw
                    .substring(posicionCorcheteInicio + 1, posicionCorcheteFin).trim();

            if (instruccionesRaw.isEmpty()) {
                throw new IllegalArgumentException("Evento sin instrucciones en process.csv línea " + numeroLinea);
            }

            MyList<String> instrucciones = new MyLinkedListImpl<>();
            for (String instruccion : instruccionesRaw.split(",")) {
                instruccion = instruccion.trim();
                if (!instruccion.isEmpty()) instrucciones.add(instruccion);
            }

            if (instrucciones.isEmpty()) {
                throw new IllegalArgumentException(
                        "Evento sin instrucciones válidas en process.csv línea " + numeroLinea);
            }

            eventos.add(new Evento(tipoEvento, instrucciones));
        }

        if (eventos.isEmpty()) {
            throw new IllegalArgumentException("Proceso sin eventos válidos en process.csv línea " + numeroLinea);
        }

        return eventos;
    }

    // -------------------------------------------------------------------------
    // PREPARE
    // -------------------------------------------------------------------------

 @Override
    public void prepareProcesses() {
        if (procesosNew.isEmpty()) {
            System.out.println("No hay procesos NEW para preparar.");
            return;
        }

        int cantidadPreparados = 0;

        while (!procesosNew.isEmpty()) {
            try {
                Proceso proceso = procesosNew.dequeue();
                proceso.calcularPrioridad();
                proceso.setEstado(EstadoProceso.PENDING);
                procesosPending.insert(proceso);
                logManager.logNewPending(proceso);
                cantidadPreparados++;
            } catch (EmptyQueueException e) {
                System.out.println("Error: la cola de procesos NEW está vacía.");
                return;
            }
        }

        System.out.println("Procesos preparados: " + cantidadPreparados);
    }

    // -------------------------------------------------------------------------
    // EXECUTE
    // -------------------------------------------------------------------------

   @Override
    public void executeNextProcess() {
        if (procesoEjecutando != null) {
            System.out.println("ERROR: Ya hay un proceso en ejecución (PID="
                    + procesoEjecutando.getPid() + "). Finalícelo antes de ejecutar otro.");
            return;
        }

        if (procesosPending.isEmpty()) {
            System.out.println("ERROR: No hay procesos pendientes para ejecutar.");
            return;
        }

        try {
            Proceso proceso = procesosPending.remove();
            proceso.setEstado(EstadoProceso.RUNNING);
            procesoEjecutando = proceso;
            logManager.logExecutingProcess(proceso);
        } catch (EmptyHeapException e) {
            System.out.println("ERROR: No se pudo obtener el siguiente proceso.");
        }
    }

//PARTE FINISH
   
    @Override
    public void finishProcessOk() {
        finalizarProceso(TipoFinalizacion.OK, null);
    }

    @Override
    public void finishProcessError() {
        finalizarProceso(TipoFinalizacion.ERROR, null);
    }

    @Override
    public void terminateProcess(int uid) {
        if (procesoEjecutando == null) {
            System.out.println("ERROR: No hay proceso en ejecución.");
            return;
        }

        Usuario responsable = usuariosPorUid.get(uid);
        if (responsable == null) {
            System.out.println("ERROR: No existe usuario con UID=" + uid);
            return;
        }

        finalizarProceso(TipoFinalizacion.TERMINATED, responsable);
    }

    private void finalizarProceso(TipoFinalizacion tipo, Usuario responsable) {
        if (procesoEjecutando == null) {
            System.out.println("ERROR: No hay proceso en ejecución.");
            return;
        }

        Proceso proceso = procesoEjecutando;
        proceso.setEstado(EstadoProceso.FINISHED);
        proceso.setTipoFinalizacion(tipo);

        if (tipo == TipoFinalizacion.TERMINATED) {
            proceso.setUsuarioTerminador(responsable);
        }

        logManager.logEndingProcess(proceso);

        procesoEjecutando = null;

        agregarAFinalizados(proceso);
    }

    private void agregarAFinalizados(Proceso proceso) {
        if (procesosFinished.size() >= MAX_FINISHED_PROCESS_ON_RAM) {
            logManager.logFinishedStackOverflow();

            while (!procesosFinished.isEmpty()) {
                try {
                    Proceso descartado = procesosFinished.pop();

                    logManager.logProcesoFinalizadoEnOverflow(descartado);

                    procesosPorPid.remove(descartado.getPid());
                    procesosEnMemoria.remove(descartado);

                } catch (EmptyStackException e) {
                    break;
                }
            }
        }

        procesosFinished.push(proceso);
    }

    // =========================================================
    // STATUS
    // =========================================================

    @Override
    public void printStatus() {
        System.out.println("PROCESS STATUS");
        imprimirEjecutando(false);
        imprimirPendientes(false);
        imprimirFinalizados(false);
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("PROCESS STATUS");
        imprimirEjecutando(true);
        imprimirPendientes(true);
        imprimirFinalizados(true);
    }

    @Override
    public void printStatusByUser(int uid) {
        Usuario user = usuariosPorUid.get(uid);

        if (user == null) {
            System.out.println("No existe usuario con UID=" + uid);
            return;
        }

        System.out.println("PROCESS STATUS - USER:" + user.getAlias() + " UID:" + uid);

        // Nuevos
        System.out.println("NEW:");
        Node<Proceso> nodo = procesosEnMemoria.getFirst();
        while (nodo != null) {
            Proceso p = nodo.getValue();

            if (p.getUsuario().getUid() == uid && p.getEstado() == EstadoProceso.NEW) {
                System.out.println("        " + lineaResumenProcesoConEstado(p));
            }

            nodo = nodo.getNext();
        }

        // Ejecutando
        System.out.println("EXECUTING:");
        if (procesoEjecutando != null && procesoEjecutando.getUsuario().getUid() == uid) {
            System.out.println("        " + lineaResumenProceso(procesoEjecutando));
        }

        // Pendientes
        System.out.println("PENDING:");
        nodo = procesosEnMemoria.getFirst();
        while (nodo != null) {
            Proceso p = nodo.getValue();

            if (p.getUsuario().getUid() == uid && p.getEstado() == EstadoProceso.PENDING) {
                System.out.println("        " + lineaResumenProceso(p));
            }

            nodo = nodo.getNext();
        }

        // Finalizados
        System.out.println("FINISHED:");
        MyStackImpl<Proceso> pilaFinalizados = (MyStackImpl<Proceso>) procesosFinished;
        imprimirFinalizadosDesdeNodoPorUsuario(pilaFinalizados.getFirst(), false, uid);
    }

    @Override
    public void printStatusByProcess(int pid) {
        Proceso p = procesosPorPid.get(pid);
        if (p == null) {
            System.out.println("No existe proceso con PID=" + pid + " en memoria.");
            return;
        }

        System.out.println("PROCESS DETAIL");
        System.out.println("PID=" + p.getPid()
                + " | " + p.getNombre()
                + " | USER:" + p.getUsuario().getAlias()
                + " UID:" + p.getUsuario().getUid()
                + " | P=" + p.getPrioridad()
                + " | STATE:" + p.getEstado());
        imprimirEventos(p);
    }

    // =========================================================
    // Helpers privados de impresión
    // =========================================================

    private void imprimirEjecutando(boolean verbose) {
        System.out.println("EXECUTING:");
        if (procesoEjecutando != null) {
            System.out.println("        " + lineaResumenProceso(procesoEjecutando));
            if (verbose) imprimirEventos(procesoEjecutando);
        }
    }

    private void imprimirPendientes(boolean verbose) {
        System.out.println("PENDING:");
        // Recorre procesosEnMemoria filtrando PENDING — O(n), sin estructuras intermedias
        Node<Proceso> nodo = procesosEnMemoria.getFirst();
        while (nodo != null) {
            Proceso p = nodo.getValue();
            if (p.getEstado() == EstadoProceso.PENDING) {
                System.out.println("        " + lineaResumenProceso(p));
                if (verbose) imprimirEventos(p);
            }
            nodo = nodo.getNext();
        }
    }

    private void imprimirFinalizados(boolean verbose) {
        System.out.println("FINISHED:");

        MyStackImpl<Proceso> pilaFinalizados = (MyStackImpl<Proceso>) procesosFinished;
        imprimirFinalizadosDesdeNodo(pilaFinalizados.getFirst(), verbose);
    }

    private void imprimirFinalizadosDesdeNodo(Node<Proceso> nodo, boolean verbose) {
        if (nodo == null) {
            return;
        }

        imprimirFinalizadosDesdeNodo(nodo.getNext(), verbose);

        Proceso p = nodo.getValue();
        System.out.println("        " + lineaResumenFinalizados(p));

        if (verbose) {
            imprimirEventos(p);
        }
    }

    private void imprimirEventos(Proceso p) {
        Node<Evento> nodoEvento = p.getEventos().getFirst();
        while (nodoEvento != null) {
            Evento ev = nodoEvento.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append("        EVENT: ").append(ev.getTipo()).append(" | Instructions [");
            Node<String> nodoInst = ev.getInstrucciones().getFirst();
            while (nodoInst != null) {
                sb.append(nodoInst.getValue());
                if (nodoInst.getNext() != null) sb.append(", ");
                nodoInst = nodoInst.getNext();
            }
            sb.append("]");
            System.out.println(sb.toString());
            nodoEvento = nodoEvento.getNext();
        }
    }

    private String lineaResumenProceso(Proceso p) {
        return "PID=" + p.getPid()
                + " | " + p.getNombre()
                + " | USER:" + p.getUsuario().getAlias()
                + " UID:" + p.getUsuario().getUid()
                + " | P=" + p.getPrioridad();
    }

    private String lineaResumenFinalizados(Proceso p) {
        String base = "PID=" + p.getPid()
                + " " + p.getNombre()
                + " | STATE: " + p.getTipoFinalizacion()
                + " | USER:" + p.getUsuario().getAlias()
                + " UID:" + p.getUsuario().getUid();
        if (p.getTipoFinalizacion() == TipoFinalizacion.TERMINATED
                && p.getUsuarioTerminador() != null) {
            base += " by USER:" + p.getUsuarioTerminador().getAlias()
                    + " UID:" + p.getUsuarioTerminador().getUid();
        }
        return base;
    }

    private String lineaResumenProcesoConEstado(Proceso p) {
        String resumen = "PID=" + p.getPid()
                + " | " + p.getNombre()
                + " | USER:" + p.getUsuario().getAlias()
                + " UID:" + p.getUsuario().getUid()
                + " | STATE:" + p.getEstado();

        if (p.getEstado() != EstadoProceso.NEW) {
            resumen += " | P=" + p.getPrioridad();
        }

        return resumen;
    }

    private void imprimirFinalizadosDesdeNodoPorUsuario(Node<Proceso> nodo, boolean verbose, int uid) {
        if (nodo == null) {
            return;
        }

        imprimirFinalizadosDesdeNodoPorUsuario(nodo.getNext(), verbose, uid);

        Proceso p = nodo.getValue();

        if (p.getUsuario().getUid() == uid) {
            System.out.println("        " + lineaResumenFinalizados(p));

            if (verbose) {
                imprimirEventos(p);
            }
        }
    }
}
