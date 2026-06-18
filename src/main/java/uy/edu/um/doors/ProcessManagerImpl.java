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

    // -------------------------------------------------------------------------
    // PREPARE
    // -------------------------------------------------------------------------

    @Override
    public void prepareProcesses() {
        if (nuevosProcesos.isEmpty()) {
            System.out.println("No hay procesos nuevos para preparar.");
            return;
        }
        while (!nuevosProcesos.isEmpty()) {
            Process p;
            try {
                p = nuevosProcesos.dequeue();
            } catch (EmptyQueueException e) {
                break;
            }
            p.calculatePriority();
            p.setState(ProcessState.PENDING);
            procesosPendientes.insert(p);
            log("NEW PENDING PROCESS: PID=" + p.getPid() + " | " + p.getName()
                    + " | USER:" + p.getUser().getAlias() + " UID:" + p.getUser().getUid()
                    + " | P=" + p.getPriority());
        }
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
