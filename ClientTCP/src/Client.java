import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	public final static int PORT = 35000;
	
	public final static String NAME_FILE = "recetas.txt";

	public static void main(String[] args) {
		Client client = new Client();
		// client.pedir("../data/" + NAME_FILE, "52.37.186.233", PORT);		// Produccion
		client.pedir("./data/" + NAME_FILE, "localhost", PORT);				// Desarrollo
	}
	
	public void pedir(String name, String server, int port) {
		try
		{
			Socket socket = new Socket(server, port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pwt = new PrintWriter(socket.getOutputStream(), true);
			
			System.out.println("Enviando PETICION");
			pwt.println("PETICION"); // Se solicita la conexion
			String peticionResponse = bfr.readLine();
			System.out.println(peticionResponse); // CONEXION ESTABLECIDA
			
			System.out.println("Notificando PREPARADO");
			pwt.println("PREPARADO"); // Se notifica que esta preparado para recibir archivos
			peticionResponse = bfr.readLine();
			System.out.println(peticionResponse); // SERVER_PREPARADO
			
			Request request = new Request();
			request.nameFile = name;
			oos.writeObject(request);
			
			// FileOutputStream fos = new FileOutputStream(request.nameFile);
			FileOutputStream fos = new FileOutputStream("./data/" + NAME_FILE);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			Response response;
			Object aux;
			do
			{
				aux = ois.readObject();
				if(aux instanceof Response)
				{
					response = (Response) aux;
					System.out.println();
					System.out.println("Status de la conexión: ACTIVA");
					// System.out.println(new String(response.fileContent, 0, response.validBytes));
					System.out.println("Nombre del archivo: " + response.nameFile);
					System.out.println("¿Último mensaje?: " + response.lastMessage);
					fos.write(response.fileContent, 0, response.validBytes);
				}
				else
				{
					System.err.println("Mensaje inesperado: " + aux.getClass().getName());
					break;
				}
			}
			while(!response.lastMessage);

			System.out.println("\nTransferencia terminada");
			System.out.println("Ruta del archivo: ./data/" + NAME_FILE);
			
			bfr.close();
			pwt.close();
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
