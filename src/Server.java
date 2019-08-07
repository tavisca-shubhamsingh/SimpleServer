import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(80);

        while (true)
        {
            Socket socket=serverSocket.accept();
            ClientHandler clientHandler=new ClientHandler(socket);
            clientHandler.start();
        }
    }
}

