import java.io.*;
import java.lang.StringBuffer;

public class BinaryNode implements Serializable 
{ 
	private int inserted;
    private BinaryNode left; 
    private BinaryNode right;
	private int leftChild;
	private int rightChild;
    private String question;
    private String person;

    BinaryNode(int i, int lc, int rc, String q, String p) 
    { 
		inserted = i;
        left = null; 
        right = null;
		leftChild = lc;
		rightChild = rc;
		setQuestion(q);
		setPerson(p);
    }
	
	private static String padRight(String s, int n)
	{
		return String.format("%1$-" + n + "s", s);
	}

    public String getQuestion()
    {
		return question.trim();
    }
	
	public String getFullQuestion()
	{	
		return question;
	}
    
    public void setQuestion(String s)
    {
		if (s == null)
			s = "";
        question = padRight(s, 256);
    }
	
    public String getPerson()
    {
		return person.trim();
    }
	
	public String getFullPerson()
	{
		return person;
	}
    
    public void setPerson(String s)
    {
		if (s == null)
			s = "";
		person = padRight(s, 256);
    }
    
    public BinaryNode getLeft()
    {
        return left;
    }
    
    public void setLeft(BinaryNode n)
    {
        left = n;
    }
    
    public BinaryNode getRight()
    {
        return right;
    }
    
    public void setRight(BinaryNode n)
    {
        right = n;
    }
	
	public int getInserted()
	{
		return inserted;
	}
	
	public int getRightChild()
	{
		return rightChild;
	}
	
	public void setRightChild(int n)
	{
		rightChild = n;
	}
	
	public int getLeftChild()
	{
		return leftChild;
	}
	
	public void setLeftChild(int  n)
	{
		leftChild = n;
	}
  
    public boolean isLeaf()
    {
        if (left == null && right == null)
            return true;
        else
            return false;
    }
	
	public static BinaryNode constructNode(RandomAccessFile raf, int pos)
	{
		BinaryNode n = null;
		try
		{
			raf.seek((524*pos)+4);
			int ins = raf.readInt();
			int lc = raf.readInt();
			int rc = raf.readInt();
			byte[] b = new byte[256];
			raf.read(b, 0, 256);
			String q = new String(b);
			raf.read(b, 0, 256);
			String p = new String(b);
			n = new BinaryNode(ins,lc,rc,q,p);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			return n;
		}
	}
	
	public void writeNode()
	{
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile("tree.txt", "rw");
			raf.seek((this.inserted*524) + 4);
			raf.writeInt(this.getInserted());
			raf.writeInt(this.getLeftChild());
			raf.writeInt(this.getRightChild());
			raf.write(this.getFullQuestion().getBytes(), 0, 256);
			raf.write(this.getFullPerson().getBytes(), 0, 256);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				raf.close();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	public void setQuestion()
	{
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile("tree.txt", "rw");
			raf.seek((this.inserted*524) + 4 + 12);
			raf.write(this.getFullQuestion().getBytes(), 0, 256);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				raf.close();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	public void setLeftChild()
	{
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile("tree.txt", "rw");
			raf.seek((this.inserted*524) + 4 + 4);
			raf.writeInt(this.left.inserted);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				raf.close();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	public void setRightChild()
	{
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile("tree.txt", "rw");
			raf.seek((this.inserted*524) + 4 + 8);
			raf.writeInt(this.right.inserted);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				raf.close();
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
		}
	}
}