package os.schedulers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AGSchedulerTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("loadTestCases")
    void testAGScheduler(String testName, JsonNode input, JsonNode expected) {
        System.out.println("=== Running: " + testName + " ===");

        List<AGSchedulerMain.AGProcess> processes = new ArrayList<>();
        for (JsonNode p : input.get("processes")) {
            processes.add(new AGSchedulerMain.AGProcess(
                    p.get("name").asText(),
                    p.get("arrival").asInt(),
                    p.get("burst").asInt(),
                    p.get("priority").asInt(),
                    p.get("quantum").asInt()
            ));
        }

        AGSchedulerMain.AGScheduler scheduler = new AGSchedulerMain.AGScheduler(processes);
        scheduler.schedule();

        // Check execution order if provided
        if (expected != null && expected.has("executionOrder")) {
            List<String> actualOrder = scheduler.executionOrder;
            JsonNode expectedOrder = expected.get("executionOrder");
            System.out.println("Expected execution order: " + expectedOrder);
            System.out.println("Actual execution order:   " + actualOrder);
            for (int i = 0; i < expectedOrder.size(); i++) {
                assertEquals(expectedOrder.get(i).asText(), actualOrder.get(i));
            }
        } else {
            System.out.println("Skipping execution order check (no expected output)");
        }

        // Check process waiting time and turnaround time
        if (expected != null && expected.has("processResults")) {
            JsonNode expectedProcesses = expected.get("processResults");
            for (int i = 0; i < expectedProcesses.size(); i++) {
                JsonNode e = expectedProcesses.get(i);
                AGSchedulerMain.AGProcess a = processes.get(i);
                System.out.println("Process " + a.name +
                        " | Expected Waiting: " + e.get("waitingTime").asInt() +
                        ", Actual Waiting: " + (a.completionTime - a.arrivalTime - a.burstTime) +
                        " | Expected Turnaround: " + e.get("turnaroundTime").asInt() +
                        ", Actual Turnaround: " + (a.completionTime - a.arrivalTime));
                assertEquals(e.get("waitingTime").asInt(), a.completionTime - a.arrivalTime - a.burstTime);
                assertEquals(e.get("turnaroundTime").asInt(), a.completionTime - a.arrivalTime);
            }
        } else {
            System.out.println("Skipping waiting/turnaround check (no expected output)");
        }

        System.out.println("=== Finished: " + testName + " ===\n");
    }

    static Stream<Arguments> loadTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = AGSchedulerTest.class.getClassLoader().getResourceAsStream("test_cases.json");
        if (is == null) throw new RuntimeException("test_cases.json not found in resources");

        JsonNode root = mapper.readTree(is);

        return StreamSupport.stream(root.spliterator(), false)
                .map(tc -> Arguments.of(
                        tc.has("name") ? tc.get("name").asText() : "Unnamed",
                        tc.get("input"),
                        tc.has("expectedOutput") ? tc.get("expectedOutput").get("AG") : null
                ));
    }
}
