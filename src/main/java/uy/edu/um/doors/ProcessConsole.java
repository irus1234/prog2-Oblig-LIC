package uy.edu.um.doors;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public class ProcessConsole {
    private final ProcessManager processManager;

    public ProcessConsole(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public void init() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Doors OS - Process Manager");
        System.out.println("Escriba 'help' para ver los comandos disponibles.");
        while (true) {
            System.out.print("doors> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            try {
                switch (command) {
                    case "pload":
                        handleLoadCommand(parts);
                        break;
                    case "pprepare":
                        processManager.prepareProcesses();
                        break;
                    case "pexecute":
                        processManager.executeNextProcess();
                        break;
                    case "pfinish":
                        handleFinishCommand(parts);
                        break;
                    case "pstatus":
                        handleStatusCommand(parts);
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "exit":
                    case "quit":
                        System.out.println("Finalizando Doors...");
                        return;
                    default:
                        System.out.println("Comando no reconocido: " + command);
                        System.out.println("Escriba 'help' para ver los comandos disponibles.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void handleLoadCommand(String[] parts) {
        if (parts.length != 5) {
            System.out.println("Uso: pload -p [path_csv_procesos] -u [path_csv_usuarios]");
            return;
        }
        String processCsvPath = null;
        String usersCsvPath = null;
        for (int i = 1; i < parts.length - 1; i += 2) {
            String option = parts[i];
            String value = parts[i + 1];
            switch (option) {
                case "-p":
                    processCsvPath = value;
                    break;
                case "-u":
                    usersCsvPath = value;
                    break;
                default:
                    System.out.println("Opción inválida para pload: " + option);
                    System.out.println("Uso: pload -p [path_csv_procesos] -u [path_csv_usuarios]");
                    return;
            }
        }
        if (processCsvPath == null || usersCsvPath == null) {
            System.out.println("Uso: pload -p [path_csv_procesos] -u [path_csv_usuarios]");
            return;
        }
        try {
            Path processPath = Path.of(processCsvPath);
            Path usersPath = Path.of(usersCsvPath);
            if (!Files.exists(processPath)) {
                System.out.println("No existe el archivo de procesos: " + processCsvPath);
                return;
            }
            if (!Files.isRegularFile(processPath)) {
                System.out.println("La ruta de procesos no corresponde a un archivo: " + processCsvPath);
                return;
            }
            if (!Files.isReadable(processPath)) {
                System.out.println("No se puede leer el archivo de procesos: " + processCsvPath);
                return;
            }
            if (!Files.exists(usersPath)) {
                System.out.println("No existe el archivo de usuarios: " + usersCsvPath);
                return;
            }
            if (!Files.isRegularFile(usersPath)) {
                System.out.println("La ruta de usuarios no corresponde a un archivo: " + usersCsvPath);
                return;
            }
            if (!Files.isReadable(usersPath)) {
                System.out.println("No se puede leer el archivo de usuarios: " + usersCsvPath);
                return;
            }
            processManager.loadProcessAndUserData(processCsvPath, usersCsvPath);
        } catch (InvalidPathException e) {
            System.out.println("Ruta inválida: " + e.getInput());
        }
    }

    private void handleFinishCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Uso: pfinish OK | ERROR | TERM [UID]");
            return;
        }
        String finishType = parts[1].toUpperCase();
        switch (finishType) {
            case "OK":
                processManager.finishProcessOk();
                break;
            case "ERROR":
                processManager.finishProcessError();
                break;
            case "TERM":
                if (parts.length < 3) {
                    System.out.println("Uso: pfinish TERM [UID]");
                    return;
                }
                try {
                    int uid = Integer.parseInt(parts[2]);
                    processManager.terminateProcess(uid);
                } catch (NumberFormatException e) {
                    System.out.println("UID no es un número de ID válido");
                }
                break;
            default:
                System.out.println("Tipo de finalización inválido: " + finishType);
                System.out.println("Uso: pfinish OK | ERROR | TERM [UID]");
                break;
        }
    }

    private void handleStatusCommand(String[] parts) {
        if (parts.length == 1) {
            processManager.printStatus();
            return;
        }
        String option = parts[1].toLowerCase();
        switch (option) {
            case "-verbose":
                processManager.printStatusVerbose();
                break;
            case "-u":
                if (parts.length < 3) {
                    System.out.println("Uso: pstatus -u [UID]");
                    return;
                }
                try {
                    int uid = Integer.parseInt(parts[2]);
                    processManager.printStatusByUser(uid);
                } catch (NumberFormatException e) {
                    System.out.println("UID no es un número de ID válido");
                }
                break;
            case "-p":
                if (parts.length < 3) {
                    System.out.println("Uso: pstatus -p [PID]");
                    return;
                }
                try {
                    int pid = Integer.parseInt(parts[2]);
                    processManager.printStatusByProcess(pid);
                } catch (NumberFormatException e) {
                    System.out.println("PID no es un número de ID válido");
                }
                break;
            default:
                System.out.println("Opción inválida para pstatus: " + option);
                System.out.println("Uso: pstatus | pstatus -verbose | pstatus -u [UID] | pstatus -p [PID]");
                break;
        }
    }

    private void printHelp() {
        System.out.println("Comandos disponibles:");
        System.out.println("  pprepare              Prepara procesos nuevos y calcula prioridad");
        System.out.println("  pexecute              Ejecuta el próximo proceso pendiente");
        System.out.println("  pfinish OK            Finaliza el proceso actual correctamente");
        System.out.println("  pfinish ERROR         Finaliza el proceso actual con error");
        System.out.println("  pfinish TERM [UID]    Finaliza forzadamente el proceso actual");
        System.out.println("  pstatus               Muestra estado general de procesos");
        System.out.println("  pstatus -verbose      Muestra estado con detalle de eventos");
        System.out.println("  pstatus -u [UID]      Muestra procesos de un usuario");
        System.out.println("  pstatus -p [PID]      Muestra detalle de un proceso");
        System.out.println("  help                  Muestra esta ayuda");
        System.out.println("  exit                  Sale del sistema");
    }
}
