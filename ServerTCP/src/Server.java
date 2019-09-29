import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public final static int PORT = 35000;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.escuchar(PORT);
	}
	
	public void escuchar(int port)
	{
		try {
			// Socket del servidor
			ServerSocket serverSocket = new ServerSocket(port);
			// Nuevo cliente
			while(true)
			{
				Socket client = serverSocket.accept();
				System.out.println("Client accepted!");
				ProtocoloThread newThread = new ProtocoloThread(client);
				newThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
