import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnixScriptExecutor {

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 5000;

    public static void main(String[] args) {
        String scriptPath = "/path/to/your/script.sh";
        String filePath = "/path/to/your/file.txt";
        String additionalFilePath = "/path/to/your/data.start";

        int retryCount = 0;
        boolean executionSuccess = false;

        while (retryCount < MAX_RETRIES && !executionSuccess) {
            try {
                // Create a ProcessBuilder with the command to execute the script and file path as parameter
                ProcessBuilder processBuilder = new ProcessBuilder("sh", scriptPath, filePath);

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
                    executionSuccess = true;

                    // Attempt the additional SFTP operation
                    boolean additionalSftpSuccess = executeSftp(additionalFilePath);
                    if (additionalSftpSuccess) {
                        System.out.println("Additional SFTP successful.");
                    } else {
                        System.out.println("Additional SFTP failed.");
                    }
                } else {
                    System.out.println("Script execution failed. Retrying...");
                    retryCount++;
                    Thread.sleep(RETRY_DELAY_MS);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                retryCount++;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (!executionSuccess) {
            System.out.println("Script execution failed after maximum retries.");
        }
    }

    private static boolean executeSftp(String filePath) {
        try {
            // Create a ProcessBuilder with the command to execute the SFTP operation
            ProcessBuilder processBuilder = new ProcessBuilder("sftp", "user@hostname");
            Process process = processBuilder.start();

            // Input the SFTP commands
            process.getOutputStream().write(("put " + filePath + "\nquit\n").getBytes());
            process.getOutputStream().flush();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
