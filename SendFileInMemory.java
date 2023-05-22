import com.jcraft.jsch.*;

public class SftpExample {

    public static void main(String[] args) {
        String hostname = "your_hostname";
        int port = 22;
        String username = "your_username";
        String privateKeyPath = "/path/to/your/private_key.pem";
        String remoteFilePath = "/path/on/remote/server/destination.txt";
        byte[] fileData = "Content of the file in memory".getBytes();

        JSch jsch = new JSch();
        try {
            // Load the private key
            jsch.addIdentity(privateKeyPath);

            // Create a session
            Session session = jsch.getSession(username, hostname, port);

            // Disable strict host key checking
            session.setConfig("StrictHostKeyChecking", "no");

            // Connect to the SSH server
            session.connect();

            // Create an SFTP channel
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // Create a file in memory
            String tempLocalFilePath = "/path/to/your/temp/file.txt";
            Files.write(Paths.get(tempLocalFilePath), fileData);

            // Upload the file to the remote server
            sftpChannel.put(tempLocalFilePath, remoteFilePath);

            // Close the SFTP channel and session
            sftpChannel.disconnect();
            session.disconnect();

            System.out.println("File uploaded successfully.");

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
    }
}



import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;

public class SftpExample {

    public static void main(String[] args) {
        String hostname = "your_hostname";
        int port = 22;
        String username = "your_username";
        String privateKeyPath = "/path/to/your/private_key.pem";
        String remoteFilePath = "/path/on/remote/server/destination.txt";
        byte[] fileData = "Content of the file in memory".getBytes();

        JSch jsch = new JSch();
        try {
            // Load the private key
            jsch.addIdentity(privateKeyPath);

            // Create a session
            Session session = jsch.getSession(username, hostname, port);

            // Disable strict host key checking
            session.setConfig("StrictHostKeyChecking", "no");

            // Connect to the SSH server
            session.connect();

            // Create an SFTP channel
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // Create an input stream from the file data
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

            // Upload the file to the remote server
            sftpChannel.put(inputStream, remoteFilePath);

            // Close the input stream, SFTP channel, and session
            inputStream.close();
            sftpChannel.disconnect();
            session.disconnect();

            System.out.println("File uploaded successfully.");

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
    }
}
