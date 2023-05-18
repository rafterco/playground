import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class UnixScriptExecutorTest {

    @Test
    public void testScriptExecutionSuccess() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Mock the script execution with a successful exit code
        executor.MAX_RETRIES = 0;
        executor.RETRY_DELAY_MS = 0;

        // Execute the script
        executor.executeScript("/path/to/your/script.sh", "/path/to/your/file.txt");

        // Restore System.out
        System.setOut(System.out);

        // Verify the output
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("Script execution completed with exit code: 0"));
        Assertions.assertTrue(output.contains("Script executed successfully."));
        Assertions.assertTrue(output.contains("Additional SFTP successful."));
    }

    @Test
    public void testScriptExecutionFailure() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Mock the script execution with a failure exit code
        executor.MAX_RETRIES = 0;
        executor.RETRY_DELAY_MS = 0;
        executor.executeSftp = (filePath) -> false;

        // Execute the script
        executor.executeScript("/path/to/your/script.sh", "/path/to/your/file.txt");

        // Restore System.out
        System.setOut(System.out);

        // Verify the output
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("Script execution completed with exit code: 1"));
        Assertions.assertTrue(output.contains("Script execution failed."));
        Assertions.assertTrue(output.contains("Additional SFTP failed."));
    }

    @Test
    public void testScriptExecutionRetry() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Mock the script execution with failure on the first attempt and success on the second attempt
        executor.MAX_RETRIES = 1;
        executor.RETRY_DELAY_MS = 0;
        executor.executeSftp = (filePath) -> filePath.equals("/path/to/your/file.txt");

        // Execute the script
        executor.executeScript("/path/to/your/script.sh", "/path/to/your/file.txt");

        // Restore System.out
        System.setOut(System.out);

        // Verify the output
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("Script execution completed with exit code: 1"));
        Assertions.assertTrue(output.contains("Script execution failed. Retrying..."));
        Assertions.assertTrue(output.contains("Script executed successfully."));
        Assertions.assertTrue(output.contains("Additional SFTP successful."));
    }

    @Test
    public void testScriptExecutionMaxRetriesExceeded() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Mock the script execution with failure on all attempts
        executor.MAX_RETRIES = 2;
        executor.RETRY_DELAY_MS = 0;
        executor.executeSftp = (filePath) -> false;

        // Execute the script
        executor.executeScript("/path/to/your/script.sh", "/path/to/your/file.txt");

        // Restore System.out
        System.setOut(System.out);

        // Verify the output
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("Script execution failed after maximum retries."));
    }

    @Test
    public void testExecuteSftpSuccess() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Redirect System.in to provide input to the SFTP command
        ByteArrayInputStream inputStream = new ByteArrayInputStream("put /path/to/your/data.start\nquit\n".getBytes());
        System.setIn(inputStream);

        // Mock the SFTP command with a successful exit code
        executor.executeSftp = (filePath) -> true;

        // Execute the SFTP operation
        boolean success = executor.executeSftp("/path/to/your/data.start");

        // Restore System.out and System.in
        System.setOut(System.out);
        System.setIn(System.in);

        // Verify the output and success result
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("sftp user@hostname"));
        Assertions.assertTrue(output.contains("put /path/to/your/data.start"));
        Assertions.assertTrue(output.contains("quit"));
        Assertions.assertTrue(success);
    }

    @Test
    public void testExecuteSftpFailure() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create an instance of UnixScriptExecutor
        UnixScriptExecutor executor = new UnixScriptExecutor();

        // Mock the SFTP command with a failure exit code
        executor.executeSftp = (filePath) -> false;

        // Execute the SFTP operation
        boolean success = executor.executeSftp("/path/to/your/data.start");

        // Restore System.out
        System.setOut(System.out);

        // Verify the output and success result
        String output = outputStream.toString();
        Assertions.assertTrue(output.contains("sftp user@hostname"));
        Assertions.assertTrue(output.contains("put /path/to/your/data.start"));
        Assertions.assertTrue(output.contains("quit"));
        Assertions.assertFalse(success);
    }
}
