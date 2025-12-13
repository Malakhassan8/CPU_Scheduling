import java.util.*;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int priority;

    int startTime = -1;
    int finishTime;

    public Process(String name, int arrival, int burst, int priority) {
        this.name = name;
        this.arrivalTime = arrival;
        this.burstTime = burst;
        this.remainingTime = burst;
        this.priority = priority;
    }
}

class PreemptivePriorityScheduler {

    static void schedule(List<Process> processes) {
        int time = 0;
        int completed = 0;
        int n = processes.size();
        int AGING_INTERVAL = 5;

        Process current = null;
        List<Process> readyQueue = new ArrayList<>();
        List<String> executionOrder = new ArrayList<>();

        while (completed < n) {

            // Add newly arrived processes to ready queue
            for (Process p : processes) {
                if (p.arrivalTime == time) {
                    readyQueue.add(p);
                }
            }

            // Aging every AGING_INTERVAL time units
            if (time > 0 && time % AGING_INTERVAL == 0) {
                for (Process p : readyQueue) {
                    if (p != current && p.priority > 0) p.priority--;
                }
            }

            // Sort ready queue by priority (lower number = higher priority), then arrival time
            readyQueue.sort((a, b) -> {
                if (a.priority != b.priority) return a.priority - b.priority;
                return a.arrivalTime - b.arrivalTime;
            });

            // Pick the next process for execution
            if (!readyQueue.isEmpty()) {
                Process next = readyQueue.get(0);

                // Preempt if next process has higher priority
                if (current == null || next.priority < current.priority) {
                    current = next;
                    if (current.startTime == -1) current.startTime = time;
                }
            }

            // Execute 1 time unit
            if (current != null) {
                executionOrder.add(current.name);
                current.remainingTime--;

                if (current.remainingTime == 0) {
                    current.finishTime = time + 1;
                    readyQueue.remove(current);
                    completed++;
                    current = null;
                }
            }

            time++;
        }

        // Print results
        printResults(processes, executionOrder);
    }

    static void printResults(List<Process> processes, List<String> order) {
        double totalWT = 0;
        double totalTAT = 0;

        System.out.println("Execution Order:");
        System.out.println(order + "\n");

        for (Process p : processes) {
            int turnaround = p.finishTime - p.arrivalTime;
            int waiting = turnaround - p.burstTime;
            totalWT += waiting;
            totalTAT += turnaround;

            System.out.println(p.name +
                    " | Waiting Time = " + waiting +
                    " | Turnaround Time = " + turnaround);
        }

        System.out.println("\nAverage Waiting Time = " + (totalWT / processes.size()));
        System.out.println("Average Turnaround Time = " + (totalTAT / processes.size()));
    }
}

public class Main {

    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 5, 2));
        processes.add(new Process("P2", 2, 3, 1));
        processes.add(new Process("P3", 4, 4, 3));
        processes.add(new Process("P4", 5, 2, 2));
        //processes.add(new Process("P5", 0, 2, 5));

        System.out.println("===== Preemptive Priority Scheduling Test =====\n");

        PreemptivePriorityScheduler.schedule(processes);
    }
}




