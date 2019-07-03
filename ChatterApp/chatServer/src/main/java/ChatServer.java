import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChatServer {

    private int BIND_PORT = -1;

    private Selector selector = null;

    private ServerSocketChannel serverSocket = null;

    private List<User> currentUsers = new ArrayList<>();

    public ChatServer(String arg) {
        try {
            BIND_PORT = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.out.println("The port number is not valid.");
        }
    }

    public void run() throws IOException {
        selector = Selector.open();
        bindAndListenToPort();


        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            System.out.println("Started loop");
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    System.out.println("Made to register");
                    registerNewUser();
                }
                if (key.isReadable()) {
                    handleIncomingMessage(key);
                }
                iterator.remove();
            }
        }
    }

    private void registerNewUser() throws IOException {
        //Register
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        byte[] b = new byte[200];
        while (client.socket().getInputStream().available() != 0) {
            client.socket().getInputStream().read(b);
            System.out.println("Read from client");
        }
        String text = new String(b).trim();

        currentUsers.add(new User(text, client));
        SocketChannel hold = null;
        for (User u : currentUsers) {
            if (u.Name.equals(text)) {
                System.out.println("In the users");
                hold = u.Channel;
            }
        }
        client.write(ByteBuffer.wrap(String.format("%" + Configuration.BUFFER_CAPACITY + "s", "Hi " + text).getBytes()));
    }

    private void handleIncomingMessage(SelectionKey key) throws IOException {
        try (SocketChannel client = (SocketChannel) key.channel()) {
            ByteBuffer requestBuffer = ByteBuffer.allocate(Configuration.BUFFER_CAPACITY);
            client.read(requestBuffer);
            String request = new String(requestBuffer.array()).trim();
            String response = null;
            //Check if the message contains a username currently on the server
            client.write(ByteBuffer.wrap(String.format("%" + Configuration.BUFFER_CAPACITY + "s", response).getBytes()));
        }
    }

    private void bindAndListenToPort() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", BIND_PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }
}
