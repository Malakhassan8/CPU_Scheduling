import java.util.*;

class Process {
    String name;
    int arrival;
    int burst;
    int remaining;
    int priority;

    int start = -1;
    int finish = 0;

    public Process(String n, int a, int b, int p) {
        name = n;
        arrival = a;
        burst = b;
        remaining = b;
        priority = p;
    }
}

class PreemptivePriorityScheduler {

    static final int AGING_INTERVAL = 5;

    static void schedule(List<Process> processes) {
        int time = 0;
        int completed = 0;
        List<String> execOrder = new ArrayList<>();
        Process current = null;
        List<Process> ready = new ArrayList<>();

        while (completed < processes.size()) {

            // Add newly arrived processes
            for (Process p : processes) {
                if (p.arrival == time && !ready.contains(p) && p.remaining > 0) {
                    ready.add(p);
                }
            }

            // Aging for waiting processes (not currently running)
            if (time > 0 && time % AGING_INTERVAL == 0) {
                for (Process p : ready) {
                    if (p != current && p.remaining > 0 && p.priority > 1) {
                        p.priority--; // increase priority
                    }
                }
            }

            // Choose process to run
            if (!ready.isEmpty()) {
                // Sort by priority first, then by arrival time
                ready.sort(Comparator.comparingInt((Process p) -> p.priority)
                        .thenComparingInt(p -> p.arrival));

                Process next = ready.get(0);

                // Preempt if new process has higher priority
                if (current == null || next.priority < current.priority) {
                    current = next;
                }
            }

            // Execute 1 time unit
            if (current != null) {
                if (current.start == -1) current.start = time;
                execOrder.add(current.name);
                current.remaining--;

                if (current.remaining == 0) {
                    current.finish = time + 1;
                    ready.remove(current);
                    completed++;
                    current = null;
                }
            }

            time++;
        }

        print(processes, execOrder);
    }

    static void print(List<Process> processes, List<String> order) {
        System.out.println("Execution Order (Preemptive Priority with Aging, FIFO on Tie):");
        System.out.println(order);

        double totalWT = 0, totalTAT = 0;
        for (Process p : processes) {
            int tat = p.finish - p.arrival;
            int wt = tat - p.burst;
            totalWT += wt;
            totalTAT += tat;
            System.out.println(p.name + ": Waiting=" + wt + " Turnaround=" + tat);
        }

        System.out.println("\nAverage Waiting Time = " + (totalWT / processes.size()));
        System.out.println("Average Turnaround Time = " + (totalTAT / processes.size()));
    }
}

public class Main {



    }



