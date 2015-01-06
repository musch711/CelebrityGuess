import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.regex.*;
import java.util.concurrent.*;

public class CelebrityGuess
{
	private static HashMap<String,Connection> whoGuess = new HashMap<String,Connection>();

    public static void main(String[] args) 
	{
		System.setProperty("line.separator", "\r\n");
        {
            ServerSocket serverSock = null;
            try 
			{
                serverSock = new ServerSocket(6001);
            }
            catch(IOException e) 
			{
                System.err.println("Error trying to open server socket!");
                System.err.println(e);
                System.exit(1);
            }
        
            ExecutorService exe = Executors.newCachedThreadPool();
			
			BinaryTree t = BinaryTree.constructTree();

            while(true) 
			{
                Socket sock = null;
                try 
				{
                    sock = serverSock.accept();
                    Connection connection = new Connection(sock,t);
                    exe.execute(connection);
                }
                catch(IOException e) 
				{
                    System.err.println("IOException occured while setting up connection.");
                    System.err.println(e);
                    if (sock != null) 
					{
                        try 
						{
                            sock.close();
                        }
                        catch(IOException e2) 
						{
                            System.err.println("IOException occured while closing connection socket.");
                            System.err.println(e2);
                        }
                    }
                }
            }
        }
    }
	
	public static void addGuess(String name, Connection con)
	{
		whoGuess.put(name, con);
	}
	
	public static boolean contains(String name)
	{
		if (whoGuess.containsKey(name) == true)
			return true;
		else
			return false;
	}
	
	public static Connection getCon(String name)
	{
		return whoGuess.get(name);
	}
}
