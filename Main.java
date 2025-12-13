import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ===== Program Input =====
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        System.out.print("Enter Context Switching Time: ");
        int contextSwitch = sc.nextInt();

        PreemptiveSJF sjf = new PreemptiveSJF(contextSwitch);

        // ===== Read Processes =====
        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1));

            System.out.print("Process Name: ");
            String name = sc.next().trim();   // safe read

            System.out.print("Arrival Time: ");
            int arrival = sc.nextInt();

            System.out.print("Burst Time: ");
            int burst = sc.nextInt();

            // Required by assignment input format but not used in SJF
            System.out.print("Priority: ");
            sc.nextInt(); // intentionally ignored

            sjf.addProcess(new Process(name, arrival, burst));
        }

        // ===== Output =====
        System.out.println("\n======================================");
        System.out.println("Preemptive Shortest Job First Scheduling");
        System.out.println("======================================\n");

        sjf.schedule();

        sc.close();
    }
}
