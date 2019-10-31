package mlc.procedures;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import mlc.components.*;

public class Algorithm
{
	// Variables used for the class.
	private Graph g;
	private ArrayList<Edge>[] tree;
	private int numOfDepth3Edges;

	@SuppressWarnings("unchecked")
	public Algorithm(File source) throws Exception
	{
		g = new Graph(source);
		tree = new ArrayList[g.getMinIOEdges().size()];
		for(int i = 0; i < g.getMinIOEdges().size(); i++)
		{
			tree[i] = new ArrayList<>();
		}

	}

	public Graph getGraph()
	{
		return g;
	}

	public void run()
	{
		g.sortEdges();
		procedure1();
		printVertices();
		printEdges();
		procedure2();
	}

	/*
	 * The first procedure takes the graph and "pseudo" partitions it. A limiter is
	 * set by establishing the degree of the vertices. Then a second loop goes
	 * through to add only edges with vertices of degree one from the subset of
	 * edges, where the degree count only reflects the subset.
	 */
	private void procedure1()
	{
		Edge[] edges = g.getEdges();
		Vertex[] e1 = new Vertex[2];
		numOfDepth3Edges = 0;
		for(numOfDepth3Edges = 0; edges[numOfDepth3Edges].getDepth() >= 3; numOfDepth3Edges++)
		{
			e1 = edges[numOfDepth3Edges].getVertices();
			e1[0].incDegree();
			e1[1].incDegree();
		}
		for(int j = 0; j < numOfDepth3Edges; j++)
		{
			if(edges[j].getVertices()[0].getDegree() == 1 || edges[j].getVertices()[1].getDegree() == 1)
			{
				edges[j].setFinalized();
			}
		}
	}

	/*
	 * The second procedure keeps track of the minimum adjacent edge to the tree.
	 * These edges are limited by their depth. It starts by adding a smallest
	 * minimum edge of depth 2 to the tree. When an edge to be added already has
	 * both vertices selected, the first sub-routine is triggered.
	 * 
	 * Copies of the various lists are kept to allow for parallel processes to run
	 * and keep the different objects and variables mutually exclusive.
	 */
	private void procedure2()
	{
		ArrayList<Edge> pMinIOEdges = g.getMinIOEdges();

		// Creates a loop to start the main algorithm with the minimum IO edge.
		for(int i = 0; i < pMinIOEdges.size(); i++)
		{

			// Creates copies of the arrays of vertices, edges, and regions for
			// mutual exclusion.
			Vertex[] vertices = g.getVertices().clone();
			Edge[] edges = g.getEdges().clone();
			Region[] regions = g.getRegions().clone();

			// Finalizing the minimum IO edge used and adding it to the tree.
			edges[pMinIOEdges.get(i).getID()].setFinalized();
			tree[0].add(pMinIOEdges.get(i));

			/*
			 * Selecting the vertices, covering the regions, and assigning weights to
			 * applicable vertex.
			 */
			for(Vertex v : tree[0].get(i).getVertices())
			{
				vertices[v.getID()].setSelected();
				for(Region r : v.getRegions())
				{
					regions[r.getID()].setCovered();
				}

				/*
				 * The first vertex with a weight will be of depth 2 from the minimum depth 2
				 * edge. Any regions of depth 3 adjacent to the vertex will be resolved.
				 */
				if(v.getDepth() == 2)
				{
					v.setWeight(pMinIOEdges.get(i).getWeight());
					for(Region temp : v.getRegions())
					{
						if(temp.getDepth() == 3)
						{
							cycleResolver(temp);
						}
					}
				}
			}

			// Obtains a copy of adjacency list to allow removal of edges during procedure.
			ArrayList<Edge>[] copyAdjacency = g.getAdjacencyList().clone();

			/*
			 * An array of minimum adjacent edges to the edges in the tree, with each edge
			 * getting a "representative" in the array.
			 */
			Edge[] minWeight = new Edge[edges.length];
			updateAdjacency(pMinIOEdges.get(i).getID(), tree[0].size(), minWeight, copyAdjacency);

			/*
			 * The following will loop through until all the regions have been covered.
			 */

			boolean regionsCovered = false;
			while(!regionsCovered)
			{
				tree[0].add(minWeight[0]);
				Vertex u = vertices[minWeight[0].getVertices()[0].getID()];
				Vertex v = vertices[minWeight[0].getVertices()[1].getID()];

				/*
				 * Updates the vertex if it has not already been added to tree along with new
				 * regions covered. Then the regions covered are resolved as applicable.
				 */
				if(u.getWeight() != 0)
				{
					u.setWeight(minWeight[0].getWeight());
					u.setSelected();
					for(Region r : u.getRegions())
					{
						r.setCovered();
						if(r.getDepth() == 3 && r.getResolved() == false)
						{
							cycleResolver(r);
						}
					}

					// Sets v to -1. Denotes it has been resolved at this point.
					v.setWeight(u.getWeight() + 1);
				}

				/*
				 * Updates the vertex if it has not already been added to tree along with new
				 * regions covered. Then the regions covered are resolved as applicable.
				 */
				if(v.getWeight() != 0)
				{
					v.setWeight(minWeight[0].getWeight());
					v.setSelected();
					for(Region r : v.getRegions())
					{
						r.setCovered();
						if(r.getDepth() == 3 && r.getResolved() == false)
						{
							cycleResolver(r);
						}
					}

					// Sets u to -1. Denotes it has been resolved at this point.
					u.setWeight(v.getWeight() + 1);
				}
				minWeight[0] = null;
			}
		}
	}

	/*
	 * Checks for an adjacent edge to the given edge of depth 3 and not null. If the
	 * first condition is satisfied, the edge is added to the minWeight array. Then
	 * the same edge is removed from the copy of the adjacency list. minWeight then
	 * sorts by weight. If the first condition is not satisfied, a null value is put
	 * as a filler.
	 */
	private void updateAdjacency(int edgeID, int numOfEdges, Edge[] minWeight, ArrayList<Edge>[] copyAdjacency)
	{
		if(copyAdjacency[edgeID].get(0) != null && copyAdjacency[edgeID].get(0).getDepth() == 3)
		{
			minWeight[tree[numOfEdges].size()] = copyAdjacency[edgeID].get(0);
			copyAdjacency[edgeID].remove(0);
			Arrays.sort(minWeight, (e1, e2) -> e1.getWeight() - e2.getWeight());
		}
		else
		{
			minWeight[tree[numOfEdges].size()] = null;
		}
	}

	/*
	 * This method takes a region then resolves if applicable. If additional edges
	 * are added to the tree, then the method will recursively call itself until all
	 * regions covered are either resolved or on hold.
	 */
	private void cycleResolver(Region r)
	{
		Edge[] temp = new Edge[r.getEdges().size()];

	}

	public void printEdges()
	{
		for(Edge e : g.getEdges())
		{
			System.out.println(e.toString());
		}
	}

	public void printVertices()
	{
		for(Vertex v : g.getVertices())
		{
			System.out.println(v.toString());
		}
	}

	public void printAdjacencyList()
	{
		ArrayList<Edge>[] temp = g.getAdjacencyList();
		for(ArrayList<Edge> e : temp)
		{
			for(Edge t : e)
			{
				System.out.print(t.getID() + ",");
			}
			System.out.println(" ");
		}
	}
}
