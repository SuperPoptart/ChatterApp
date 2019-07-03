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
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    registerNewUser();
                }
                if (key.isReadable()) {
                    System.out.println("Handle");
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

        currentUsers.add(new User("default", client));
    }

    private void handleIncomingMessage(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        User holder = new User();
        for (User u : currentUsers) {
            if (u.Channel == client) {
                holder = u;
            }
        }
        if (holder.Name.equals("default")) {
            ByteBuffer nameBuffer = ByteBuffer.allocate(Configuration.BUFFER_CAPACITY);
            client.read(nameBuffer);
            String request = new String(nameBuffer.array()).trim();
            SocketChannel hold = null;
            for (User u : currentUsers) {
                if (u.Channel == client) {
                    u.Name = request;
                    hold = u.Channel;
                }
            }
            hold.write(ByteBuffer.wrap(String.format("%" + Configuration.BUFFER_CAPACITY + "s", "Hello " + request).getBytes()));
        } else {
            ByteBuffer nameBuffer = ByteBuffer.allocate(Configuration.BUFFER_CAPACITY);
            client.read(nameBuffer);
            String request = new String(nameBuffer.array()).trim();
            System.out.println("Sending: " + request);
            String receiver = request.split(":")[0];
            String message = request.split(":")[1];
            SocketChannel hold = null;
            for (User u : currentUsers) {
                if (u.Name.equals(receiver)) {
                    hold = u.Channel;
                }
            }
            hold.write(ByteBuffer.wrap(String.format("%" + Configuration.BUFFER_CAPACITY + "s", holder.Name + ": " + message).getBytes()));
        }
        //Check if the message contains a username currently on the server
    }

    private void bindAndListenToPort() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", BIND_PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }
}
