import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProtocoloThread extends Thread{
	
	private Socket client;
	
	public ProtocoloThread(Socket client) {
		this.client = client;
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
			
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
