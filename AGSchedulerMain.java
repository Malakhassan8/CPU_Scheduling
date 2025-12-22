package os.schedulers;

import java.util.*;

public class AGSchedulerMain {

    public static class AGProcess {
        public String name;
        public int arrivalTime;
        public int burstTime;
        public int remainingTime;
        public int priority;
        public int quantum;
        public int completionTime = -1;
        public List<Integer> quantumHistory = new ArrayList<>();

        public AGProcess(String name, int arrival, int burst, int priority, int quantum) {
            this.name = name;
            this.arrivalTime = arrival;
            this.burstTime = burst;
            this.remainingTime = burst;
            this.priority = priority;
            this.quantum = quantum;
        }
    }


    public static class AGScheduler {

        public List<AGProcess> processes;
        public Queue<AGProcess> readyQueue = new LinkedList<>();
        public int time = 0;
        public List<String> executionOrder = new ArrayList<>();

        public AGScheduler(List<AGProcess> processes) {
            this.processes = processes;
        }

        public void schedule() {
            int completed = 0;
            AGProcess running = null;

            while (completed < processes.size()) {

                for (AGProcess p : processes) {
                    if (p.remainingTime > 0 && p != running && !readyQueue.contains(p) && p.arrivalTime <= time) {
                        readyQueue.add(p);
                    }
                }


                if (running == null && !readyQueue.isEmpty()) {
                    running = readyQueue.poll();
                    executionOrder.add(running.name);
                    running.quantumHistory.add(running.quantum);
                }

                if (running == null) {
                    time++;
                    continue;
                }

                int usedQuantum = 0;
                int startQuantum = running.quantum;

                int q25 = (int) Math.ceil(startQuantum * 0.25);
                int q50 = (int) Math.ceil(startQuantum * 0.50);

                while (usedQuantum < startQuantum && running.remainingTime > 0) {
                    time++;
                    usedQuantum++;
                    running.remainingTime--;

                    // Add newly arrived processes
                    for (AGProcess p : processes) {
                        if (p.remainingTime > 0 && p != running && !readyQueue.contains(p) && p.arrivalTime <= time) {
                            readyQueue.add(p);
                        }
                    }

                    // Phase 2: Priority preemption
                    if (usedQuantum >= q25 && usedQuantum < q50) {
                        AGProcess higher = getHigherPriority(running);
                        if (higher != null) {
                            int rem = startQuantum - usedQuantum;
                            running.quantum += (int) Math.ceil(rem / 2.0);
                            running.quantumHistory.add(running.quantum);
                            readyQueue.add(running);
                            running = null;
                            break;
                        }
                    }

                    // Phase 3: SJF preemption
                    if (usedQuantum >= q50) {
                        AGProcess shorter = getShorterJob(running);
                        if (shorter != null) {
                            int rem = startQuantum - usedQuantum;
                            running.quantum += rem;
                            running.quantumHistory.add(running.quantum);
                            readyQueue.add(running);
                            running = null;
                            break;
                        }
                    }
                }

                // Process completed
                if (running != null && running.remainingTime == 0) {
                    running.completionTime = time;
                    running.quantum = 0;
                    running.quantumHistory.add(0);
                    completed++;
                    running = null;
                }
                // Quantum fully used
                else if (running != null && usedQuantum == startQuantum) {
                    running.quantum += 2;
                    running.quantumHistory.add(running.quantum);
                    readyQueue.add(running);
                    running = null;
                }
            }

            printResults();
        }

        private AGProcess getHigherPriority(AGProcess current) {
            return readyQueue.stream()
                    .filter(p -> p.priority < current.priority)
                    .min(Comparator.comparingInt(p -> p.priority))
                    .orElse(null);
        }

        private AGProcess getShorterJob(AGProcess current) {
            return readyQueue.stream()
                    .filter(p -> p.remainingTime < current.remainingTime)
                    .min(Comparator.comparingInt(p -> p.remainingTime))
                    .orElse(null);
        }

        private void printResults() {
            double totalWT = 0, totalTAT = 0;

            System.out.println("Execution Order: " + executionOrder);
            System.out.println("\nProcess | Waiting Time | Turnaround Time | Quantum History");

            for (AGProcess p : processes) {
                int tat = p.completionTime - p.arrivalTime;
                int wt = tat - p.burstTime;
                totalWT += wt;
                totalTAT += tat;

                System.out.printf("%-7s | %-12d | %-15d | %s%n",
                        p.name, wt, tat, p.quantumHistory);
            }

            System.out.printf("\nAverage Waiting Time = %.2f%n", totalWT / processes.size());
            System.out.printf("Average Turnaround Time = %.2f%n", totalTAT / processes.size());
        }
    }


    public static void main(String[] args) {
        List<AGProcess> processes = List.of(
                new AGProcess("P1", 0, 17, 4, 7),
                new AGProcess("P2", 2, 6, 7, 9),
                new AGProcess("P3", 5, 11, 3, 4),
                new AGProcess("P4", 15, 4, 6, 6)
        );

        AGScheduler scheduler = new AGScheduler(new ArrayList<>(processes));
        scheduler.schedule();
    }
}
