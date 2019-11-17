package mlc.components;

import java.util.Arrays;

public class Edge implements Comparable<Edge>
{

	private Region[] regions = new Region[2];
	private Vertex[] vertices = new Vertex[2];
	private int weight, id, selected;
	private boolean finalized;
	private final int depth;

	public Edge(int w, int i, Vertex u, Vertex v)
	{

		vertices[0] = u;
		vertices[1] = v;
		weight = w;
		id = i;

		// Both vertices have a depth of 1, so the edge has a depth of 1.
		if(u.getDepth() == 1 && v.getDepth() == 1)
		{
			depth = 1;
		}

		// Both vertices have a depth of 2 or at least one has a depth of 3,
		// then the
		// edge has a depth of 3.
		else if((u.getDepth() == 2 && v.getDepth() == 2) || (u.getDepth() == 3 || v.getDepth() == 3))
		{
			depth = 3;
		}

		// All other cases can only be an edge with a depth of 2.
		else
		{
			depth = 2;
		}

	}

	public int getID()
	{
		return id;
	}

	public int getSelected()
	{
		return selected;
	}

	public boolean getFinalized()
	{
		return finalized;
	}

	public int getDepth()
	{
		return depth;
	}

	public int getWeight()
	{
		return weight;
	}

	public Vertex[] getVertices()
	{
		return vertices;
	}

	public Region[] getRegions()
	{
		return regions;
	}

	public void setRegion(Region r)
	{
		if(regions[0] == null)
		{
			regions[1] = r;
		}
		else
		{
			regions[0] = r;
		}
	}

	public void setSelected(boolean b)
	{
		if(selected == 0)
		{
			selected = 1;
			if(vertices[0].isSelected() == false)
			{
				vertices[0].setSelected(b);
			}
			if(vertices[1].isSelected() == false)
			{
				vertices[1].setSelected(b);
			}
		}
		else
		{
			selected = 0;
			if(vertices[0].isSelected() == true)
			{
				vertices[0].setSelected(b);
			}
			if(vertices[1].isSelected() == true)
			{
				vertices[1].setSelected(b);
			}
		}
	}

	public void setFinalized(boolean b)
	{
		finalized = b;
		vertices[0].setFinalized(b);
		vertices[1].setFinalized(b);
	}

	public Edge copy()
	{
		return new Edge(weight, id, vertices[0], vertices[1]);
	}

	public String toString()
	{
		return("ID:" + id + ", Weight: " + weight + ", Depth: " + depth + ", Selected: " + selected + ", Finalized: "
					+ finalized + ", Vertex u: " + vertices[0].getID() +  ", Vertex v: " + vertices[1].getID());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + (finalized ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + selected;
		result = prime * result + Arrays.hashCode(vertices);
		result = prime * result + weight;
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
		Edge other = (Edge) obj;
		if(depth != other.depth)
			return false;
		if(finalized != other.finalized)
			return false;
		if(id != other.id)
			return false;
		if(selected != other.selected)
			return false;
		if(!Arrays.equals(vertices, other.vertices))
			return false;
		if(weight != other.weight)
			return false;
		return true;
	}

	public int compareTo(Edge e2)
	{
		if(weight == e2.getWeight())
		{
			return 0;
		}
		else if(weight < e2.getWeight())
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
