package mlc.components;

import java.util.ArrayList;

public class Vertex
{

	private ArrayList<Region> regions = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	private boolean selected, root, leaf, finalized;
	private int degree = 0;
	private int weight = 0;
	private final int id, depth;

	public Vertex(int d, int id)
	{
		depth = d;
		root = false;
		leaf = false;
		finalized = false;
		this.id = id;
	}
	
	public Vertex(boolean selected, boolean root, boolean leaf, boolean finalized, int degree, int weight, int id, int depth, ArrayList<Region> regions, ArrayList<Edge> edges)
	{
		this.selected = selected;
		this.root = root;
		this.leaf = leaf;
		this.finalized = finalized;
		this.degree = degree;
		this.weight = weight;
		this.id = id;
		this.depth = depth;
		this.regions = regions;
		this.edges = edges;
	}
	
	public Vertex copy()
	{
		return new Vertex(selected, root, leaf, finalized, degree, weight, id, depth, regions, edges);
	}

	public ArrayList<Region> getRegions()
	{
		return regions;
	}

	public boolean isFinalized()
	{
		return finalized;
	}

	public boolean isLeaf()
	{
		return leaf;
	}

	public boolean isRoot()
	{
		return root;
	}

	public int getDegree()
	{
		return degree;
	}

	public int getID()
	{
		return id;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public int getDepth()
	{
		return depth;
	}

	public int getWeight()
	{
		return weight;
	}

	public ArrayList<Edge> getEdges()
	{
		return edges;
	}

	public void incDegree()
	{
		degree++;
	}

	public void setRegion(Region r)
	{
		regions.add(r);
	}

	public void setWeight(int w)
	{
		weight = w;
	}

	public void setSelected(boolean b)
	{
		selected = b;
	}

	public void setLeaf(boolean b)
	{
		leaf = b;
	}

	public void setRoot(boolean b)
	{
		root = b;
	}

	public void setFinalized(boolean b)
	{
		finalized = b;
	}

	public void setEdge(Edge e)
	{
		edges.add(e);
	}

	public String toString()
	{
		return("ID: " + id + ", Depth: " + depth +", Selected: " + selected + ", Degree: " + degree + " Weight: " + weight);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + degree;
		result = prime * result + depth;
		result = prime * result + (finalized ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + (leaf ? 1231 : 1237);
		result = prime * result + (root ? 1231 : 1237);
		result = prime * result + (selected ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if(id != other.id)
			return false;
		return true;
	}

}
