import java.util.*;

class Process {
    int pid;
    int burstTime;
    int remainingTime;
    int waitingTime = 0;
    int turnaroundTime = 0;

    Process(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

public class RoundRobinWithCS {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        System.out.print("Enter time quantum: ");
        int tq = sc.nextInt();

        System.out.print("Enter context switching time: ");
        int cs = sc.nextInt();

        Queue<Process> queue = new LinkedList<>();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter burst time for P" + (i + 1) + ": ");
            int bt = sc.nextInt();
            processes[i] = new Process(i + 1, bt);
            queue.add(processes[i]);
        }

        int time = 0;
        Process prev = null;

        while (!queue.isEmpty()) {
            Process current = queue.poll();

            // Add context switching time if switching processes
            if (prev != null && prev != current) {
                time += cs;
            }

            int execTime = Math.min(tq, current.remainingTime);
            time += execTime;
            current.remainingTime -= execTime;

            // Update waiting time for other processes
            for (Process p : queue) {
                p.waitingTime += execTime + cs;
            }

            if (current.remainingTime > 0) {
                queue.add(current);
            } else {
                current.turnaroundTime = time;
            }

            prev = current;
        }

        double totalWT = 0, totalTAT = 0;

        System.out.println("\nPID\tBT\tWT\tTAT");
        for (Process p : processes) {
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;

            System.out.println("P" + p.pid + "\t" +
                               p.burstTime + "\t" +
                               p.waitingTime + "\t" +
                               p.turnaroundTime);
        }

        System.out.printf("\nAverage Waiting Time: %.2f", totalWT / n);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / n);
    }
}
