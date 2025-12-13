import java.util.ArrayList;

public class PreemptiveSJF {

    private ArrayList<Process> processes;
    private int contextSwitchTime;

    public PreemptiveSJF(int contextSwitchTime) {
        this.processes = new ArrayList<>();
        this.contextSwitchTime = contextSwitchTime;
    }

    public void addProcess(Process p) {
        processes.add(p);
    }

    public void schedule() {

        int time = 0;
        int completed = 0;
        Process currentProcess = null;

        System.out.println("Process execution order:");

        while (completed < processes.size()) {

            Process shortest = null;

            for (Process p : processes) {
                if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                    if (shortest == null ||
                            p.getRemainingTime() < shortest.getRemainingTime()) {
                        shortest = p;
                    }
                }
            }

            if (shortest == null) {
                time++;
                continue;
            }

            if (currentProcess != shortest) {
                if (currentProcess != null) {
                    time += contextSwitchTime;
                }
                currentProcess = shortest;
                System.out.print(currentProcess.getName() + " ");
            }

            currentProcess.setRemainingTime(
                    currentProcess.getRemainingTime() - 1);
            time++;

            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(time);
                completed++;
                currentProcess = null;
            }
        }

        System.out.println("\n");
        calculateTimes();
        printResults();
    }

    private void calculateTimes() {
        for (Process p : processes) {
            int turnaround = p.getCompletionTime() - p.getArrivalTime();
            int waiting = turnaround - p.getBurstTime();
            p.setTurnaroundTime(turnaround);
            p.setWaitingTime(waiting);
        }
    }

    private void printResults() {

        double totalWT = 0;
        double totalTAT = 0;

        System.out.println();
        System.out.printf("%-12s %-15s %-18s%n",
                "Process", "Waiting Time", "Turnaround Time");

        for (Process p : processes) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();

            System.out.printf("%-12s %-15d %-18d%n",
                    p.getName(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());
        }

        System.out.println();
        System.out.printf("Average Waiting Time = %.2f%n",
                totalWT / processes.size());
        System.out.printf("Average Turnaround Time = %.2f%n",
                totalTAT / processes.size());
    }
}
