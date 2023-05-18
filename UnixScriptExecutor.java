Unix Script Executor

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnixScriptExecutor {

    public static void main(String[] args) {
        String scriptPath = "/path/to/your/script.sh";

        try {
            // Create a ProcessBuilder with the command to execute the script
            ProcessBuilder processBuilder = new ProcessBuilder("sh", scriptPath);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Script execution completed with exit code: " + exitCode);

            // Handle the exit code
            if (exitCode == 0) {
                System.out.println("Script executed successfully.");
            } else {
                System.out.println("Script execution failed.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
