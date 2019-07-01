import java.io.IOException;

public class RunServer {
    public static void main(String[] args) throws IOException {
        new ChatServer(args[0]).run();
    }
}
