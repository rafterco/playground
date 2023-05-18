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

        // Mock the script execution with a successful exit code
        UnixScriptExecutor.MAX_RETRIES = 0;
        UnixScriptExecutor.RETRY_DELAY_MS = 0;

        // Execute the script
        UnixScriptExecutor.main(new String[0]);

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

        // Mock the script execution with a failure exit code
        UnixScriptExecutor.MAX_RETRIES = 0;
        UnixScriptExecutor.RETRY_DELAY_MS = 0;
        UnixScriptExecutor.executeSftp = (filePath) -> false;

        // Execute the script
        UnixScriptExecutor.main(new String[0]);

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

        // Mock the script execution with failure on the first attempt and success on the second attempt
        UnixScriptExecutor.MAX_RETRIES = 1;
        UnixScriptExecutor.RETRY_DELAY_MS = 0;
        UnixScriptExecutor.executeSftp = (filePath) -> filePath.equals("/path/to/your/file.txt");

        // Execute the script
        UnixScriptExecutor.main(new String[0]);

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

        // Mock the script execution with failure on all attempts
        UnixScriptExecutor.MAX_RETRIES = 2;
        UnixScriptExecutor.RETRY_DELAY_MS = 0;
        UnixScriptExecutor.executeSftp = (filePath) -> false;

        // Execute the script
        UnixScriptExecutor.main(new String[0]);

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

        // Redirect System.in to provide input to the SFTP command
        ByteArrayInputStream inputStream = new ByteArrayInputStream("put /path/to/your/data.start\nquit\n".getBytes());
        System.setIn(inputStream);

        // Mock the SFTP command with a successful exit code
        UnixScriptExecutor.executeSftp = (filePath) -> true;

        // Execute the SFTP operation
        boolean success = UnixScriptExecutor.executeSftp("/path/to/your/data.start");

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

        // Mock the SFTP command with a failure exit code
        UnixScriptExecutor.executeSftp = (filePath) -> false;

        // Execute the SFTP operation
        boolean success = UnixScriptExecutor.executeSftp("/path/to/your/data.start");

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
