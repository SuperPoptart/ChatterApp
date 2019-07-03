import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {

    private int BIND_PORT = -1;
    private String hostName = "";

    public ChatClient(String arg0, String arg1) {
        BIND_PORT = Integer.parseInt(arg0);
        hostName = arg1;
    }

    public void run() throws IOException {
        Socket socket = new Socket(hostName, BIND_PORT);
        System.out.println("What is your name?");


        while (true) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String clientInput = reader.readLine();
            //print sent message
            socket.getOutputStream().write(clientInput.getBytes());
            byte[] b = new byte[200];
            socket.getInputStream().read(b);
            String text = new String(b).trim();
            System.out.println(text);
            // print received
        }
    }

    public void voidReceiveMessage(String msg, String from) {
        //print msg from server
        System.out.println(from + msg);
    }

    public void sendMessage(String msg, String to) {
        //send to server
    }
}
