package uy.edu.um.doors;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

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
                tipo = TipoUsuario.valueOf(tipoTexto);
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
        
    }

    // -------------------------------------------------------------------------
    // EXECUTE
    // -------------------------------------------------------------------------

    @Override
    public void executeNextProcess() {
        if (procesoEjecutando != null) {
            System.out.println("ERROR: Ya hay un proceso en ejecución (PID=" + procesoEjecutando.getPid() + "). Finalícelo antes de ejecutar otro.");
            return;
        }
        if (procesosPendientes.isEmpty()) {
            System.out.println("ERROR: No hay procesos pendientes para ejecutar.");
            return;
        }
        Process p;
        try {
            p = procesosPendientes.remove();
        } catch (EmptyHeapException e) {
            System.out.println("ERROR: No se pudo obtener el siguiente proceso.");
            return;
        }
        p.setState(ProcessState.RUNNING);
        procesoEjecutando = p;

        // Loguear EXECUTING PROCESS
        StringBuilder sb = new StringBuilder();
        sb.append("EXECUTING PROCESS: PID=").append(p.getPid())
                .append(" | USER:").append(p.getUser().getAlias())
                .append(" UID:").append(p.getUser().getUid());
        log(sb.toString());

        // Loguear cada evento
        Node<Event> nodo = p.getEvents().getFirst();
        while (nodo != null) {
            Event ev = nodo.getValue();
            logRaw(" EVENT: " + ev.getType() + " | Instructions " + ev.getInstructionsString());
            nodo = nodo.getNext();
        }
    }

    
    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
