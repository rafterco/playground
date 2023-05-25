ProcessBuilder processBuilder = new ProcessBuilder("sftp", "-i", "privateKeyFile", "user@hostname");
processBuilder.redirectErrorStream(true);
Process process = processBuilder.start();

// Read the output and error streams of the process
BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
String line;
while ((line = reader.readLine()) != null) {
    System.out.println(line);
}

// Wait for the process to complete
int exitCode = process.waitFor();
System.out.println("Process exited with code: " + exitCode);

/*

If you are getting a return value of 255 when executing the sftp command using ProcessBuilder, it typically indicates an error or failure in the command execution. Here are a few things to check and troubleshoot:

Verify the correctness of the privateKeyFile path: Ensure that the privateKeyFile variable contains the correct file path to your private key file. Double-check the path to make sure it is accurate.

Check the file permissions of the private key: Ensure that the private key file has the appropriate permissions set. It should be readable by the user running the Java program.

Verify the username and hostname: Make sure the user@hostname argument is correctly formatted. Replace user with the actual username and hostname with the SFTP server's hostname or IP address.

Check if the sftp command is available: Ensure that the sftp command is installed and accessible from the command line. Try running the sftp command manually in your terminal to verify its availability.

Capture the error output: To obtain more information about the error, you can redirect the error stream of the process and read the error messages. Here's an example of how you can do it:

*/
