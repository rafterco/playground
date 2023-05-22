import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SftpExample {

    public static void main(String[] args) {
        String privateKeyPath = "/path/to/your/private_key.pem";
        String remoteFilePath = "/path/on/remote/server/destination.txt";
        byte[] fileData = "Content of the file in memory".getBytes();

        try {
            // Create an input stream from the file data
            InputStream inputStream = new ByteArrayInputStream(fileData);

            // Create a ProcessBuilder with the SFTP command
            ProcessBuilder processBuilder = new ProcessBuilder("sftp", "-i", privateKeyPath, "user@hostname");

            // Start the process
            Process process = processBuilder.start();

            // Get the input stream of the process
            InputStream processInputStream = process.getInputStream();

            // Create a thread to consume the input stream of the process
            Thread inputStreamThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = processInputStream.read(buffer)) != -1) {
                        // Consume the input stream
                        String output = new String(buffer, 0, bytesRead);
                        System.out.print(output);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputStreamThread.start();

            // Write the SFTP commands to the process
            process.getOutputStream().write(("put " + remoteFilePath + "\n").getBytes());
            process.getOutputStream().flush();

            // Write the file data to the process input stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                process.getOutputStream().write(buffer, 0, bytesRead);
            }
            process.getOutputStream().flush();

            // Write the remaining commands and close the process input stream
            process.getOutputStream().write(("\nquit\n").getBytes());
            process.getOutputStream().close();

            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("Failed to upload file.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
