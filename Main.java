public class Main {
    public static void main(String[] args) {
        UnixScriptExecutor executor = new UnixScriptExecutor();
        executor.executeScript("/path/to/your/script.sh", "/path/to/your/file.txt");
    }
}
