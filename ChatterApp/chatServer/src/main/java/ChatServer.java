import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {

    private int BIND_PORT = -1;

    private Selector selector = null;

    private ServerSocketChannel serverSocket = null;

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
                    handleIncomingMessage(key);
                }
                iterator.remove();
            }
        }
    }

    private void registerNewUser() {
        //Register
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
