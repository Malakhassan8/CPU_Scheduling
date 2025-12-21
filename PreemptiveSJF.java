        package os.schedulers;

        import java.util.ArrayList;
        import java.util.List;

        public class PreemptiveSJF {

            private final ArrayList<Process> processes;
            private final int contextSwitchTime;
            private final List<String> executionOrder;

            public PreemptiveSJF(int contextSwitchTime) {
                this.processes = new ArrayList<>();
                this.contextSwitchTime = contextSwitchTime;
                this.executionOrder = new ArrayList<>();
            }

            public void addProcess(Process p) {
                processes.add(p);
            }

            private Process findShortest(int time) {
                Process shortest = null;
                for (Process p : processes) {
                    if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                        if (shortest == null || p.getRemainingTime() < shortest.getRemainingTime())
                            shortest = p;
                        else if (p.getRemainingTime() == shortest.getRemainingTime()
                                && p.getArrivalTime() < shortest.getArrivalTime())
                            shortest = p;
                    }
                }
                return shortest;
            }

            public void schedule() {
                int time = 0;
                int completedCount = 0;
                int total = processes.size();
                Process current = null;
                boolean firstEver = true;

                while (completedCount < total) {
                    Process shortest = findShortest(time);
                    if (shortest == null) {
                        time++;
                        continue;
                    }

                    if (current != shortest) {
                        if (!firstEver) {
                            time += contextSwitchTime;
                        }
                        firstEver = false;
                        current = shortest;
                        if (executionOrder.isEmpty() || !executionOrder.get(executionOrder.size() - 1).equals(current.getName())) {
                            executionOrder.add(current.getName());
                        }
                    }

                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    if (current.getRemainingTime() == 0) {
                        current.setCompletionTime(time);
                        completedCount++;
                    }
                }
                calculateTimes();
            }

            private void calculateTimes() {
                for (Process p : processes) {
                    int tat = p.getCompletionTime() - p.getArrivalTime();
                    p.setTurnaroundTime(tat);
                    p.setWaitingTime(tat - p.getBurstTime());
                }
            }

            public List<Process> getProcesses() {
                return processes;
            }

            public List<String> getExecutionOrder() {
                return executionOrder;
            }
        }
