import java.nio.channels.SocketChannel;

public class User {
    public String Name;
    public SocketChannel Channel;

    public User(String name, SocketChannel add) {
        this.Name = name;
        this.Channel = add;
    }
}
