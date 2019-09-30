import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ProtocoloThread extends Thread{

	private Socket client;

	private String ipSource;

	private BufferedReader bfr;

	private PrintWriter pwt;

	public ProtocoloThread(Socket client) {
		this.client = client;
		ipSource = client.getInetAddress().getHostAddress();
	}

	private void sendFile(String nameFile, ObjectOutputStream oos) {
		try
		{
			boolean lastMessage = false;

			FileInputStream fis = new FileInputStream(nameFile);

			Response response = new Response();
			response.nameFile = nameFile;

			int leidos = fis.read(response.fileContent);

			while(leidos > -1)
			{
				response.validBytes = leidos;

				if(leidos < Response.MAX_LENGTH)
				{
					response.lastMessage = true;
					lastMessage = true;
				}
				else
				{
					response.lastMessage = false;
				}
				oos.writeObject(response);
				if(response.lastMessage)
					break;
				response = new Response();
				response.nameFile = nameFile;

				leidos = fis.read(response.fileContent);
			}
			if (!lastMessage)
			{
				response.lastMessage = true;
				response.validBytes = 0;
				oos.writeObject(response);
			}
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			client.setSoLinger(true, 10);

			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			bfr = new BufferedReader(new InputStreamReader(client.getInputStream()));
			pwt = new PrintWriter(client.getOutputStream(), true);

			String peticion = bfr.readLine();
			if(peticion.equals("PETICION"))
				pwt.println("CONEXION ESTABLECIDA");
			
			peticion = bfr.readLine();
			if(peticion.equals("PREPARADO"))
				pwt.println("SERVER_PREPARADO");

			Object request = ois.readObject();

			if(request instanceof Request)
			{
				System.out.println("Me piden: " + ((Request)request).nameFile);
				sendFile(((Request)request).nameFile, new ObjectOutputStream(client.getOutputStream()));
			}
			else
			{
				System.err.println("Mensaje inesperado: " + request.getClass().getName());
			}
			bfr.close();
			pwt.close();
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
