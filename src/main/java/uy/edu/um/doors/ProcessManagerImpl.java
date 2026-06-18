package uy.edu.um.doors;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        // --- Cargar usuarios ---
        // Formato: uid;alias;type  (primera línea es header)
        try {
            List<String> lineasUsuarios = Files.readAllLines(Paths.get(usersCsvPath));
            boolean primeraLinea = true;
            for (String linea : lineasUsuarios) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (primeraLinea) { primeraLinea = false; continue; } // saltar header
                String[] partes = linea.split(";");
                if (partes.length < 3) continue;
                try {
                    int uid = Integer.parseInt(partes[0].trim());
                    String alias = partes[1].trim();
                    UserType tipo = UserType.valueOf(partes[2].trim().toUpperCase());
                    usuariosPorUid.put(uid, new User(uid, alias, tipo));
                } catch (Exception e) {
                    System.out.println("Línea de usuario inválida, se omite: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR leyendo usuarios: " + e.getMessage());
            return;
        }

        // --- Cargar procesos ---
        // Formato: pid;uid;name;events  (primera línea es header)
        // events tiene la forma: {TIPO:[inst, inst, ...]# TIPO:[inst, ...]# ...}
        try {
            List<String> lineasProcesos = Files.readAllLines(Paths.get(processCsvPath));
            boolean primeraLinea = true;
            for (String linea : lineasProcesos) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (primeraLinea) { primeraLinea = false; continue; } // saltar header

                // Separar por ";" pero solo las primeras 3 (el campo events puede tener contenido libre)
                int idx1 = linea.indexOf(';');
                int idx2 = linea.indexOf(';', idx1 + 1);
                int idx3 = linea.indexOf(';', idx2 + 1);
                if (idx1 < 0 || idx2 < 0 || idx3 < 0) continue;

                try {
                    int pid = Integer.parseInt(linea.substring(0, idx1).trim());
                    int uid = Integer.parseInt(linea.substring(idx1 + 1, idx2).trim());
                    String nombre = linea.substring(idx2 + 1, idx3).trim();
                    String eventsStr = linea.substring(idx3 + 1).trim();

                    User user = usuariosPorUid.get(uid);
                    if (user == null) {
                        System.out.println("Usuario UID=" + uid + " no encontrado, proceso PID=" + pid + " omitido.");
                        continue;
                    }

                    Process proceso = new Process(pid, nombre, user);

                    // Parsear eventos: {TIPO:[inst, inst]# TIPO:[inst]}
                    // Quitar llaves externas
                    if (eventsStr.startsWith("{")) eventsStr = eventsStr.substring(1);
                    if (eventsStr.endsWith("}")) eventsStr = eventsStr.substring(0, eventsStr.length() - 1);

                    // Separar eventos por "#"
                    String[] eventosArr = eventsStr.split("#");
                    for (String eventoStr : eventosArr) {
                        eventoStr = eventoStr.trim();
                        if (eventoStr.isEmpty()) continue;
                        // Formato: TIPO:[inst1, inst2, ...]
                        int bracketOpen = eventoStr.indexOf('[');
                        int bracketClose = eventoStr.indexOf(']');
                        if (bracketOpen < 0 || bracketClose < 0) continue;

                        String tipoStr = eventoStr.substring(0, bracketOpen).replace(":", "").trim();
                        String instStr = eventoStr.substring(bracketOpen + 1, bracketClose).trim();

                        try {
                            EventType tipoEvento = EventType.valueOf(tipoStr.toUpperCase());
                            Event evento = new Event(tipoEvento);
                            // Instrucciones separadas por ", "
                            String[] instrucciones = instStr.split(",");
                            for (String inst : instrucciones) {
                                String i = inst.trim();
                                if (!i.isEmpty()) evento.addInstruction(i);
                            }
                            proceso.addEvent(evento);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Tipo de evento inválido, se omite: " + tipoStr);
                        }
                    }

                    nuevosProcesos.enqueue(proceso);
                    procesosPorPid.put(pid, proceso);
                    registrarProcesoPorUsuario(uid, pid);

                } catch (NumberFormatException e) {
                    System.out.println("Línea de proceso inválida, se omite: " + linea);
                }
            }
            System.out.println("Datos cargados correctamente.");
        } catch (IOException e) {
            System.out.println("ERROR leyendo procesos: " + e.getMessage());
        }
    }

    private void registrarProcesoPorUsuario(int uid, int pid) {
        MyStack<Integer> pids = procesosEnMemoriaPorUsuario.get(uid);
        if (pids == null) {
            pids = new MyStackImpl<>();
            procesosEnMemoriaPorUsuario.put(uid, pids);
        }
        pids.push(pid);
    }

    // -------------------------------------------------------------------------
    // PREPARE
    // -------------------------------------------------------------------------

    
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
