import java.io.Serializable;

public class Response implements Serializable{
	private static final long serialVersionUID = -6607717688045923503L;
	public final static int MAX_LENGTH = 4000;
	
	public String nameFile = "";
	public boolean lastMessage = true;
	public int validBytes = 0;
	public byte[] fileContent = new byte[MAX_LENGTH];
}
