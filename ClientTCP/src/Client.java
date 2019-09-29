import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
	
	public final static int PORT = 35000;

	public static void main(String[] args) {
		Client client = new Client();
		client.pedir("./data/recetas.txt", "localhost", PORT);
	}
	
	public void pedir(String name, String server, int port) {
		try
		{
			Socket socket = new Socket(server, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			Request request = new Request();
			request.nameFile = name;
			oos.writeObject(request);
			
			FileOutputStream fos = new FileOutputStream(request.nameFile);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			Response response;
			Object aux;
			do
			{
				aux = ois.readObject();
				if(aux instanceof Response)
				{
					response = (Response) aux;
					System.out.println("\n#Log#");
					System.out.println(new String(response.fileContent, 0, response.validBytes));
					fos.write(response.fileContent, 0, response.validBytes);
				}
				else
				{
					System.err.println("Mensaje inesperado: " + aux.getClass().getName());
					break;
				}
			}
			while(!response.lastMessage);
			
			fos.close();
			ois.close();
			socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
