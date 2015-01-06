import java.io.*;
import java.util.regex.*;
import java.net.*;
import java.lang.StringBuffer;

public class Connection implements Runnable
{
	public static final int yes = 1;
	public static final int no = 0;
	public static final int err = -1;
	
	public static final String guessCeleb = "Is the celebrity you are thinking of ";
	public static final String questionMark = "?";
	public static final String correctGuess = "I'm so smart!";
	public static final String wrongGuess = "Who are you thinking of?";
	public static final String addQuestion = "Ask a yes/no question that would distinguish between ";
	public static final String answerQuestion = "Would an answer of yes indicate ";
	public static final String thankYou1 = "Thank you for adding "; 
	public static final String thankYou2 = " to the database.";
	public static final String invalidInput = "Invalid input!  I expect one of the following: yes, Yes, y, Y, no, No, n, N.";
	
	public static final Pattern y = Pattern.compile("yes|Yes|y|Y");
	public static final Pattern n = Pattern.compile("no|No|n|N");
	
	private BinaryTree tree;
	private BinaryNode curNode;
	
	private String userName;
	private String userIn;
	private String newPerson;
	private String newQuestion;
	private String writeOut;
	
	private Socket socket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;

    public Connection(Socket s, BinaryTree t) throws IOException 
	{
        socket = s;
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		tree = t;
    }

    public void run() 
    {
		boolean on = true;
		
		try
		{
			socketOut.println("What is your name?");
			socketOut.flush();
			userName = removeBack(socketIn.readLine());
			while(on)
			{
				curNode = tree.getRoot();
				socketOut.println(userName + ", would you like to play a celebrity guessing game?");
				socketOut.flush();
				userIn = removeBack(socketIn.readLine());
				if (yOrN(userIn) == yes)
					play();
				else if (yOrN(userIn) == no)
					on = false;
				else
				{
					socketOut.println(invalidInput);
					socketOut.flush();
				}	
			}
		}
		catch (IOException e) 
		{
			System.err.println("IOException occured!!!");
			System.err.println(e);
		}
		finally 
		{
			try 
			{
				socket.close();
				System.out.println("connection closed");
			}
			catch(IOException e) 
			{
				System.err.println("IOException while closing socket!!!");
				System.err.println(e);
			}
		}
    }
	
	public static int yOrN(String input)
	{
		Matcher m = y.matcher(input);
		boolean b = m.matches();
		if (b == true)
			return yes;
		
		m = n.matcher(input);
		b = m.matches();
		if (b == true)
			return no;
		else
			return err;
	}
	
	private void play()
	{
		try 
		{
			while(true)
			{
				if(curNode.isLeaf() == true)
				{
					synchronized(curNode)
					{
						if (curNode.isLeaf() == true)
						{
							writeOut = guessCeleb + curNode.getPerson() + questionMark;
							socketOut.println(writeOut);
							socketOut.flush();
							userIn = removeBack(socketIn.readLine());
							if (yOrN(userIn) == yes)
							{
								writeOut = correctGuess;
								socketOut.println(writeOut);
								socketOut.flush();
								notifyOfGuess(curNode.getPerson());
								break;
							}
							else if (yOrN(userIn) == no)
							{
								writeOut = wrongGuess;
								socketOut.println(writeOut);
								socketOut.flush();
								newPerson = removeBack(socketIn.readLine());
								writeOut = addQuestion + newPerson + " and " + curNode.getPerson();
								socketOut.println(writeOut);
								socketOut.flush();
								newQuestion = removeBack(socketIn.readLine());
								writeOut = answerQuestion + newPerson + questionMark;
								socketOut.println(writeOut);
								socketOut.flush();
								userIn = removeBack(socketIn.readLine());
								curNode.setQuestion(newQuestion);
								curNode.setQuestion();
								if (yOrN(userIn) == yes)
								{
									BinaryTree.incNumNodes();
									curNode.setLeftChild(BinaryTree.getNumNodes());
									BinaryNode left = new BinaryNode(BinaryTree.getNumNodes(), -1, -1, null, newPerson);
									left.writeNode();
									curNode.setLeft(left);
									curNode.setLeftChild();
									
									BinaryTree.incNumNodes();
									curNode.setRightChild(BinaryTree.getNumNodes());
									BinaryNode right = new BinaryNode(BinaryTree.getNumNodes(), -1, -1, null, curNode.getPerson());
									right.writeNode();
									curNode.setRight(right);
									curNode.setRightChild();
								}
								else
								{
									BinaryTree.incNumNodes();
									curNode.setLeftChild(BinaryTree.getNumNodes());
									BinaryNode left = new BinaryNode(BinaryTree.getNumNodes(), -1, -1, null, curNode.getPerson());
									left.writeNode();
									curNode.setLeft(left);
									curNode.setLeftChild();
									
									BinaryTree.incNumNodes();
									curNode.setRightChild(BinaryTree.getNumNodes());
									BinaryNode right = new BinaryNode(BinaryTree.getNumNodes(), -1, -1, null, newPerson);
									right.writeNode();
									curNode.setRight(right);
									curNode.setRightChild();
								}
								writeOut = thankYou1 + newPerson + thankYou2;
								socketOut.println(writeOut);
								socketOut.flush();
								CelebrityGuess.addGuess(newPerson, this);
								break;
							}
							else
							{
								writeOut = invalidInput;
								socketOut.println(writeOut);
								socketOut.flush();
							}
						}
					}
				}
				else
				{
					writeOut = curNode.getQuestion();
					socketOut.println(writeOut);
					socketOut.flush();
					userIn = removeBack(socketIn.readLine());
					if (yOrN(userIn) == yes)
					{
						curNode = curNode.getLeft();
					}
					else if (yOrN(userIn) == no)
					{
						curNode = curNode.getRight();
					}
					else
					{
						writeOut = invalidInput;
						socketOut.println(writeOut);
						socketOut.flush();
					}
				}
			}
        }
        catch (IOException e) 
		{
            System.err.println("IOException occured!!!");
            System.err.println(e);
        }
	}
	
	private void notifyOfGuess(String name)
	{
		if (CelebrityGuess.contains(name) == true)
		{
			Connection n = CelebrityGuess.getCon(name);
			if (n.socket.isClosed() == false && n.socket.getPort() != this.socket.getPort())
			{
				n.socketOut.println(this.userName + " thought of " + name);
			}
		}
	}
	
	private static String removeBack(String s)
	{
		StringBuffer st = new StringBuffer(s);
		int i = 0;
		int ln = st.length();
		while (i < ln)
		{
			if(st.charAt(i) == '\u0008')
			{
				if (i != 0)
				{
					st.deleteCharAt(i);
					st.deleteCharAt(i - 1);
					i = i - 1;
				}
				else
				{
					st.deleteCharAt(i);
					i = 0;
				}
			}
			else
			{
				i = i+1;
			}
			ln = st.length();
		}
		return st.toString();
	}
}