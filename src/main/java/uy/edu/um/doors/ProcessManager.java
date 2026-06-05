package uy.edu.um.doors;

public interface ProcessManager {
    public static final int MAX_FINISHED_PROCESS_ON_RAM = 3;
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath);
    public void prepareProcesses();
    public void executeNextProcess();
    public void finishProcessOk();
    public void finishProcessError();
    public void terminateProcess(int uid);
    public void printStatus();
    public void printStatusVerbose();
    public void printStatusByUser(int uid);
    public void printStatusByProcess(int pid);
}
