import java.io.IOException;

public class RunClient {
    public static void main(String[] args) throws IOException {
        new ChatClient(args[0], args[1]).run();
    }
}
