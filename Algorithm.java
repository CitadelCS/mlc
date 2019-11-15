package mlc.procedures;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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
		System.out.println("Tree length: " + tree.length + "\n");
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
		for(ArrayList<Edge> t : tree)
		{
			for(Edge e : t)
			{
				System.out.println(e.toString());
			}
			System.out.println("Next tree \n \n \n");
		}
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
		for(int i = numOfDepth3Edges + 1; i < edges.length; i++)
		{
			e1 = edges[i].getVertices();
			e1[0].incDegree();
			e1[1].incDegree();
		}
		for(int i = numOfDepth3Edges + 1; i < edges.length; i++)
		{
			if(edges[i].getVertices()[0].getDegree() >= 4)
			{
				int depth2Edge = 0;
				for(Edge e : edges[i].getVertices()[0].getEdges())
				{
					if(e.getDepth() == 2)
					{
						depth2Edge++;
					}
				}
				if(depth2Edge > 1)
				{
					edges[i].getVertices()[0].setFinalized(true);
				}
			}
			else if(edges[i].getVertices()[1].getDegree() >= 4)
			{
				int depth2Edge = 0;
				for(Edge e : edges[i].getVertices()[1].getEdges())
				{
					if(e.getDepth() == 2)
					{
						depth2Edge++;
					}
				}
				if(depth2Edge > 1)
				{
					edges[i].getVertices()[1].setFinalized(true);
				}
			}
		}

		/*
		 * Section to connect critical points via shortest path to form a minimum
		 * spanning tree of the critical points.
		 */
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
		for(int treeIndex = 0; treeIndex < pMinIOEdges.size(); treeIndex++)
		{

			System.out.println("This is the " + treeIndex + " iteration. \n");

			// Creates copies of the arrays of vertices, edges, regions, and adjacency list
			// for mutual exclusion.
			Vertex[] vertices = g.getVertices().clone();
			Edge[] edges = g.getEdges().clone();
			Region[] regions = g.getRegions().clone();
			ArrayList<Edge>[] adjacency = g.getAdjacencyList().clone();

			// Finalizing the minimum IO edge used and adding it to the tree.
			edges[pMinIOEdges.get(treeIndex).getID()].setFinalized();
			tree[treeIndex].add(pMinIOEdges.get(treeIndex));

			/*
			 * Selecting the vertices, covering the regions, and assigning weights to
			 * applicable vertex.
			 */
			for(Vertex v : tree[treeIndex].get(0).getVertices())
			{
				vertices[v.getID()].setSelected(true);
				for(Region r : v.getRegions())
				{
					regions[r.getID()].setCovered(true);
				}

				/*
				 * The first vertex with a weight will be of depth 2 from the minimum depth 2
				 * edge. Any regions of depth 3 adjacent to the vertex will be resolved.
				 */
				if(v.getDepth() == 2)
				{
					v.setWeight(pMinIOEdges.get(treeIndex).getWeight());
					for(Region temp : v.getRegions())
					{
						if(temp.getDepth() == 3)
						{
							cycleResolver(temp, vertices, edges, regions, adjacency, treeIndex);
						}
					}
				}
			}

			/*
			 * An array of minimum adjacent edges to the edges in the tree, with each edge
			 * getting a "representative" in the array.
			 */
			Edge[] minWeight = new Edge[edges.length];
			updateAdjacency(pMinIOEdges.get(treeIndex).getID(), treeIndex, minWeight, adjacency);

			/*
			 * The following will loop through until all the regions have been covered.
			 */

			boolean regionsNotCovered = false;
			for(Region r : regions)
			{
				if(r.isCovered() == false && r.getDepth() == 2)
				{
					System.out.println("Region checked for coverage and depth: ");
					System.out.println(r.toString() + "\n");
					regionsNotCovered = true;
				}
			}

			while(regionsNotCovered == true)
			{
				System.out.println("Regions update with while loop for while(!regionsCovered).");
				for(Region r : regions)
				{
					System.out.println(r.toString());
				}
				tree[0].add(minWeight[0]);
				Vertex u = vertices[minWeight[0].getVertices()[0].getID()];
				Vertex v = vertices[minWeight[0].getVertices()[1].getID()];

				/*
				 * Updates the vertex if it has not already been added to tree along with new
				 * regions covered. Then the regions covered are resolved as applicable.
				 */
				if(u.getWeight() == 0)
				{
					u.setWeight(minWeight[0].getWeight() + v.getWeight());
					u.setSelected(true);
					for(Region r : u.getRegions())
					{
						regions[r.getID()].setCovered(true);
						if(r.getDepth() == 3 && r.isResolved() == false)
						{
							cycleResolver(r, vertices, edges, regions, adjacency, treeIndex);
						}
					}

					// Sets v to -1. Denotes it has been resolved at this point.
					v.setWeight(-1);
				}

				/*
				 * Updates the vertex if it has not already been added to tree along with new
				 * regions covered. Then the regions covered are resolved as applicable.
				 */
				if(v.getWeight() == 0)
				{
					v.setWeight(minWeight[0].getWeight() + u.getWeight());
					v.setSelected(true);
					for(Region r : v.getRegions())
					{
						regions[r.getID()].setCovered(true);
						if(r.getDepth() == 3 && r.isResolved() == false)
						{
							cycleResolver(r, vertices, edges, regions, adjacency, treeIndex);
						}
					}

					// Sets u to -1. Denotes it has been resolved at this point.
					u.setWeight(-1);
				}

				minWeight[0] = null;

				System.out.println("Regions not covered: " + regionsNotCovered);
			}

			for(Region r : regions)
			{
				if(r.isHold() == true)
				{
					resolveHold(r, vertices, edges, regions, adjacency, treeIndex);
				}
			}
			System.out.println("This is the end of the " + treeIndex + " iteration. \n \n"
						+ "************************************" + "\n \n");
		}
	}

	/*
	 * Checks for an adjacent edge to the given edge of depth 3 and not null. If the
	 * first condition is satisfied, the edge is added to the minWeight array. Then
	 * the same edge is removed from the copy of the adjacency list. minWeight then
	 * sorts by weight. If the first condition is not satisfied, a null value is put
	 * as a filler.
	 */
	private void updateAdjacency(int edgeID, int treeIndex, Edge[] minWeight, ArrayList<Edge>[] adjacency)
	{
		if(adjacency[edgeID].get(0) != null && adjacency[edgeID].get(0).getDepth() == 3)
		{
			minWeight[tree[treeIndex].size()] = adjacency[edgeID].get(0);
			adjacency[edgeID].remove(0);
			Arrays.sort(minWeight, (e1, e2) -> e1.getWeight() - e2.getWeight());
		}
		else
		{
			minWeight[tree[treeIndex].size()] = null;
		}
	}

	/*
	 * This method takes a region then resolves if applicable. If additional edges
	 * are added to the tree, then the method will recursively call itself until all
	 * regions covered are either resolved or on hold.
	 */
	private void cycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions, ArrayList<Edge>[] adjacency,
				int treeIndex)
	{
		Edge[] path1 = new Edge[r.getEdges().size() / 2];
		Edge[] path2 = new Edge[r.getEdges().size() - path1.length];
		System.out.println("Rectangle being resolved in cycleResolver(): " + r.toString());

		// The indices represent the information as follows: vertex id, vertex weight
		int[] rootInfo = { -1, -1 };
		for(Vertex v : r.getVertices())
		{
			if(rootInfo[0] == -1 || vertices[v.getID()].getWeight() < rootInfo[1])
			{
				rootInfo[0] = v.getID();
				rootInfo[1] = v.getWeight();
			}
		}

		// Initializes the two separate paths first edge.
		for(int i = 0; i < vertices[rootInfo[0]].getEdges().size(); i++)
		{
			if(r.getEdges().contains(vertices[rootInfo[0]].getEdges().get(i)))
			{
				if(path1[0] == null)
				{
					path1[0] = vertices[rootInfo[0]].getEdges().get(i);
				}
				else
				{
					path2[0] = vertices[rootInfo[0]].getEdges().get(i);
				}
			}
		}

		// Adds edges to path1 until half of the edges in region are contained in path1
		for(int i = 1; i < path1.length; i++)
		{
			for(int j = 0; j < adjacency[path1[i - 1].getID()].size(); j++)
			{
				if(r.getEdges().contains(adjacency[path1[i - 1].getID()].get(j))
							&& !Arrays.asList(path2).contains(adjacency[path1[i - 1].getID()].get(j)))
				{
					// Need to check this for reference passing rather than copy and all other
					// similar things above.
					path1[i] = adjacency[path1[i - 1].getID()].get(j);
				}
			}
		}

		/*
		 * Adds edges to path2 until there are no remaining edges not contained in path1
		 * and path2.
		 */
		int i = 1;
		for(Edge e : r.getEdges())
		{
			if(!Arrays.asList(path1).contains(e) && !e.equals(path2[0]))
			{
				path2[i] = e;
				i++;
			}
		}

		System.out.println("Path 1 length: " + path1.length);
		for(Edge e : path1)
		{
			System.out.println(e.toString());
		}
		System.out.println();
		System.out.println("Path 2 length: " + path2.length);
		for(Edge e : path2)
		{
			System.out.println(e.toString());
		}
		System.out.println("\n");

		if(path1.length == path2.length)
		{

			/*
			 * Creating hashsets to easily compare the regions covered by each path.
			 */
			HashSet<Region> set1 = new HashSet<Region>();
			HashSet<Region> set2 = new HashSet<Region>();
			for(Edge e : path1)
			{
				for(Vertex v : e.getVertices())
				{
					set1.addAll(v.getRegions());
				}
			}
			for(Edge e : path2)
			{
				for(Vertex v : e.getVertices())
				{
					set2.addAll(v.getRegions());
				}
			}

			if(set1.containsAll(set2) && set2.containsAll(set1))
			{
				boolean path1Selected = true;
				boolean path2Selected = true;
				for(Edge e : path1)
				{
					if(e.getSelected() == 0)
					{
						path1Selected = false;
					}
				}
				for(Edge e : path2)
				{
					if(e.getSelected() == 0)
					{
						path2Selected = false;
					}
				}
				if(path1Selected == true)
				{
					regions[r.getID()].setResolved(true);
					for(Edge e : path1)
					{
						tree[treeIndex].add(edges[e.getID()]);
						Vertex u = vertices[e.getVertices()[0].getID()];
						Vertex v = vertices[e.getVertices()[1].getID()];
						if(u.getWeight() == 0)
						{
							u.setWeight(v.getWeight() + e.getWeight());
							u.setSelected(true);
							for(Region r1 : v.getRegions())
							{
								regions[r1.getID()].setCovered(true);
								if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
								{
									cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
								}
							}
						}
						else if(v.getWeight() == 0)
						{
							v.setWeight(u.getWeight() + e.getWeight());
							v.setSelected(true);
							for(Region r1 : u.getRegions())
							{
								regions[r1.getID()].setCovered(true);
								if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
								{
									cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
								}
							}
						}
					}
				}
				else if(path2Selected == true)
				{
					regions[r.getID()].setResolved(true);
					for(Edge e : path2)
					{
						tree[treeIndex].add(edges[e.getID()]);
						Vertex u = vertices[e.getVertices()[0].getID()];
						Vertex v = vertices[e.getVertices()[1].getID()];
						if(u.getWeight() == 0)
						{
							u.setWeight(v.getWeight() + e.getWeight());
							u.setSelected(true);
							for(Region r1 : v.getRegions())
							{
								r1.setCovered(true);
								if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
								{
									cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
								}
							}
						}
						else if(v.getWeight() == 0)
						{
							v.setWeight(u.getWeight() + e.getWeight());
							v.setSelected(true);
							for(Region r1 : u.getRegions())
							{
								regions[r1.getID()].setCovered(true);
								if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
								{
									cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
								}
							}
						}
					}
				}
				else
				{
					regions[r.getID()].setHold(true);
				}
			}

			/*
			 * The following else if statements add the edges of the path to the tree while
			 * making sure the weights are updated accordingly. This will resolve all
			 * regions recursively until the region is either resolved or on hold.
			 */
			else if(set1.containsAll(set2))
			{
				regions[r.getID()].setResolved(true);
				for(Edge e : path1)
				{
					tree[treeIndex].add(edges[e.getID()]);
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);
						for(Region r1 : v.getRegions())
						{
							regions[r1.getID()].setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);
						for(Region r1 : u.getRegions())
						{
							regions[r1.getID()].setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
				}
				for(Edge e : path1)
				{
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					for(Region r1 : v.getRegions())
					{
						regions[r1.getID()].setCovered(true);
						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
						}
					}
					for(Region r1 : u.getRegions())
					{
						regions[r1.getID()].setCovered(true);
						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
						}
					}
				}
			}
			else if(set2.containsAll(set1))
			{
				regions[r.getID()].setResolved(true);
				for(Edge e : path2)
				{
					tree[treeIndex].add(edges[e.getID()]);
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);
						for(Region r1 : v.getRegions())
						{
							regions[r1.getID()].setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);
						for(Region r1 : u.getRegions())
						{
							regions[r1.getID()].setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
				}
			}
			else
			{
				regions[r.getID()].setCovered(true);
				altCycleResolver(r, vertices, edges, regions, adjacency, treeIndex, path1, path2);
			}
		}
		else
		{
			regions[r.getID()].setCovered(true);
			altCycleResolver(r, vertices, edges, regions, adjacency, treeIndex, path1, path2);
		}
	}

	private void altCycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions,
				ArrayList<Edge>[] adjacency, int treeIndex, Edge[] path1, Edge[] path2)
	{

		/*
		 * Create a path where adjacent edges are adjacent in the array. Index 0 and
		 * Index path.length - 1 are adjacent.
		 */
		Edge[] path = new Edge[path1.length + path2.length];
		for(int i = 0; i < path1.length; i++)
		{
			path[i] = path1[i];
		}
		for(int i = 0; i < path2.length; i++)
		{
			path[path1.length + i] = path2[path2.length - 1 - i];
		}
		int weight2Edges, weight1Edge, indexPair1, indexPair2, indexMax;
		weight2Edges = 0;
		weight1Edge = 0;
		indexPair1 = 0;
		indexPair2 = 0;
		indexMax = 0;

		/*
		 * The values set to 0 above are updated to represent the indices of the
		 * greatest weighted pair of edges and weight of the greatest weighted pair of
		 * edges plus the index and weight of the greatest weighted edge. Checks to make
		 * sure the union of two edges for the shared vertex does not have a degree of
		 * 4+ or the vertex connecting the region to the tree is selected for the pair
		 * of edges.
		 */
		for(int i = 0; i < path.length; i++)
		{
			if(path[i].getWeight() > weight1Edge)
			{
				weight1Edge = path[i].getWeight();
				indexMax = i;
			}
			if(path[i].getWeight() + path[(i + 1) % path.length].getWeight() > weight2Edges
						&& (i != (path.length - 1) && (i + 1) % path.length != 0))
			{
				if(path[i].getVertices()[0].equals(path[(i + 1) % path.length].getVertices()[0])
							&& path[i].getVertices()[0].getDegree() < 4)
				{
					if(path[i].getSelected() != 1 && path[i].getVertices()[0].isSelected() == true)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[0].equals(path[(i + 1) % path.length].getVertices()[1])
							&& path[i].getVertices()[0].getDegree() < 4)
				{
					if(path[i].getSelected() != 1 && path[i].getVertices()[0].isSelected() == true)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[1].equals(path[(i + 1) % path.length].getVertices()[0])
							&& path[i].getVertices()[1].getDegree() < 4)
				{
					if(path[i].getSelected() != 1 && path[i].getVertices()[0].isSelected() == true)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[1].equals(path[(i + 1) % path.length].getVertices()[1])
							&& path[i].getVertices()[1].getDegree() < 4)
				{
					if(path[i].getSelected() != 1 && path[i].getVertices()[0].isSelected() == true)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
			}
		}

		/*
		 * If the two edge indices were never given a new index and the weight of the
		 * pair of edges exceeds the weight of the greatest weighted edge for the
		 * region, then the pair of edges will not be added to the graph. Otherwise, the
		 * greatest weighted edge will be removed from the region. This will resolve
		 * cycles much like cycleResolver unless the region has been resolved or is on
		 * hold.
		 */
		if(indexPair1 != 0 && indexPair2 != 0 && weight2Edges >= weight1Edge)
		{
			regions[r.getID()].setResolved(true);
			for(Edge e : path)
			{
				if(!e.equals(path[indexPair1]) && !e.equals(path[indexPair2]))
				{
					tree[treeIndex].add(edges[e.getID()]);
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);
						for(Region r1 : v.getRegions())
						{
							regions[r1.getID()].setCovered(true);
						}
					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);
						for(Region r1 : u.getRegions())
						{
							regions[r1.getID()].setCovered(true);
						}
					}
				}
			}
			for(Edge e : path)
			{
				Vertex u = vertices[e.getVertices()[0].getID()];
				Vertex v = vertices[e.getVertices()[1].getID()];
				for(Region r1 : v.getRegions())
				{
					regions[r1.getID()].setCovered(true);
					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
					}
				}
				for(Region r1 : u.getRegions())
				{
					regions[r1.getID()].setCovered(true);
					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
					}
				}
			}
		}
		else
		{
			regions[r.getID()].setResolved(true);
			for(Edge e : path)
			{
				if(!e.equals(path[indexMax]))
				{
					tree[treeIndex].add(edges[e.getID()]);
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);
						for(Region r1 : v.getRegions())
						{
							r1.setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);
						for(Region r1 : u.getRegions())
						{
							r1.setCovered(true);
							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
							}
						}
					}
				}
			}
		}

	}

	private void alt2CycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions,
				ArrayList<Edge>[] adjacency, int treeIndex)
	{
		Edge[] path1 = new Edge[r.getEdges().size() / 2];
		Edge[] path2 = new Edge[(r.getEdges().size() / 2) - path1.length];

		// The indices represent the information as follows: vertex id, vertex weight
		int[] rootInfo = { -1, -1 };
		for(Vertex v : r.getVertices())
		{
			if(rootInfo[0] == -1 || vertices[v.getID()].getWeight() < rootInfo[1])
			{
				rootInfo[0] = v.getID();
				rootInfo[1] = v.getWeight();
			}
		}

		// Initializes the two separate paths first edge.
		for(int i = 0; i < vertices[rootInfo[0]].getEdges().size(); i++)
		{
			if(r.getEdges().contains(vertices[rootInfo[0]].getEdges().get(i)))
			{
				if(path1[0] == null)
				{
					path1[0] = vertices[rootInfo[0]].getEdges().get(i);
				}
				else
				{
					path2[0] = vertices[rootInfo[0]].getEdges().get(i);
				}
			}
		}

		// Adds edges to path1 until half of the edges in region are contained in path1
		for(int i = 1; i < path1.length; i++)
		{
			for(int j = 0; j < adjacency[path1[i - 1].getID()].size(); j++)
			{
				if(r.getEdges().contains(adjacency[path1[i - 1].getID()].get(j))
							&& !Arrays.asList(path2).contains(adjacency[path1[i - 1].getID()].get(j)))
				{
					// Need to check this for reference passing rather than copy and all other
					// similar things above.
					path1[i] = adjacency[path1[i - 1].getID()].get(j);
				}
			}
		}

		/*
		 * The choice between the paths is arbitrary at this point, so either one can be
		 * picked. Thus, path1 will be added to the tree.
		 */

		for(Edge e : path1)
		{
			Vertex u = vertices[e.getVertices()[0].getID()];
			Vertex v = vertices[e.getVertices()[1].getID()];
			for(Region r1 : v.getRegions())
			{
				if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
				{
					cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
				}
			}
			for(Region r1 : u.getRegions())
			{
				if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
				{
					cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex);
				}
			}
		}

	}

	/*
	 * private void alt3CycleResolver(Region r, Vertex[] vertices, Edge[] edges,
	 * Region[] regions, ArrayList<Edge>[] adjacency, int treeIndex) {
	 * 
	 * }
	 */

	/*
	 * The method first determines if all the regions have been covered that are
	 * adjacent to the given region. If they are not, then the next step moves onto
	 * sending the region to a different cycle resolver meant for regions on hold at
	 * the end.
	 */
	private void resolveHold(Region r, Vertex[] vertices, Edge[] edges, Region[] regions, ArrayList<Edge>[] adjacency,
				int treeIndex)
	{
		boolean allRegionsCovered = true;
		for(Vertex v : r.getVertices())
		{
			for(Region r1 : v.getRegions())
			{
				if(r1.isCovered() == false)
				{
					allRegionsCovered = false;
				}
			}
		}
		if(allRegionsCovered == false)
		{
			r.setResolved(true);
			r.setHold(false);
			alt2CycleResolver(r, vertices, edges, regions, adjacency, treeIndex);

			/*
			 * int edgesNotSelected = 0; for(Edge e : r.getEdges()) { if(e.getSelected() ==
			 * 0) { edgesNotSelected++; } } if(edgesNotSelected == 2) { r.setResolved();
			 * r.setHold(); alt2CycleResolver(r, vertices, edges, regions, adjacency,
			 * treeIndex); } else if(edgesNotSelected != r.getEdges().size()) {
			 * r.setResolved(); r.setHold(); // alt2CycleResolver(r, vertices, edges,
			 * regions, adjacency, treeIndex); } else { r.setResolved(); r.setHold();
			 * alt3CycleResolver(r, vertices, edges, regions, adjacency, treeIndex); }
			 */
		}
	}

	public void printEdges()
	{
		System.out.println("List of edges: ");
		for(Edge e : g.getEdges())
		{
			System.out.println(e.toString());
		}
		System.out.println();
	}

	public void printVertices()
	{
		System.out.println("List of vertices: ");
		for(Vertex v : g.getVertices())
		{
			System.out.println(v.toString());
		}
		System.out.println();
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
