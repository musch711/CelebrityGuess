import java.io.*;

public class BinaryTree 
{
  private BinaryNode root;
  private static int numNodes;

  public BinaryTree() 
  { 
    root = null;
	numNodes = 0;
  }
  
  public BinaryTree(BinaryNode n) 
  { 
    root = n; 
	numNodes = 0;
  }

  public BinaryNode getRoot()
  {
	return root;
  }
  
  public static int getNumNodes()
  {
	return numNodes;
  }
  
  public static void incNumNodes()
  {
	numNodes = numNodes + 1;
  }

  public static BinaryTree constructTree()
  {
	RandomAccessFile raf = null;
	BinaryTree t = null;
	try
	{
		raf = new RandomAccessFile("tree.txt", "rw");
		if (raf.length() <= 0)
		{
			t = new BinaryTree(new BinaryNode(0, -1, -1, null, "Barack Obama"));
			t.getRoot().writeNode();
		}
		else
		{
			t.numNodes = raf.readInt();
			t = new BinaryTree(BinaryNode.constructNode(raf, 0));
			if (t.getRoot() == null)
				return t;
			t.build(raf, t.getRoot());
		}
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
		return t;
	}
  }
  
  public void build(RandomAccessFile raf, BinaryNode n)
  {
	if (n.getLeftChild() == -1 || n.getRightChild() == -1)
		return;
	BinaryNode left = BinaryNode.constructNode(raf, n.getLeftChild());
	n.setLeft(left);
	incNumNodes();
	build(raf, n.getLeft());
	BinaryNode right = BinaryNode.constructNode(raf, n.getRightChild());
	n.setRight(right);
	incNumNodes();
	build(raf, n.getRight());
  }
}
