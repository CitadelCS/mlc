package mlc.procedures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import mlc.components.*;

public class Algorithm
{
	// Variables used for the class.
	private Graph g;
	private ArrayList<Edge>[] tree;
	private int numOfDepth3Edges;
	private final static boolean DEBUG = true;

	@SuppressWarnings("unchecked")
	public Algorithm(File source) throws Exception
	{
		g = new Graph(source);
		tree = new ArrayList[g.getMinIOEdges().size()];
		for(int i = 0; i < g.getMinIOEdges().size(); i++)
		{
			tree[i] = new ArrayList<>();
		}

		if(DEBUG)
		{
			System.out.println("Tree length: " + tree.length + "\n");
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
				edges[j].setFinalized(true);

				if(DEBUG)
				{
					System.out.println("Depth 3 edge check for being finalized:\n" + edges[j].toString() + "\n");
				}
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

					if(DEBUG)
					{
						System.out.println("Depth 2 edge and degree >= 4 check for being finalized:\n"
									+ edges[i].toString() + "\n");
					}
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

					if(DEBUG)
					{
						System.out.println("Depth 2 edge and degree >= 4 check for being finalized:\n"
									+ edges[i].toString() + "\n");
					}
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
	@SuppressWarnings("unlikely-arg-type")
	private void procedure2()
	{
		ArrayList<Edge> pMinIOEdges = g.getMinIOEdges();

		// Creates a loop to start the main algorithm with the minimum IO edge.
		for(int treeIndex = 0; treeIndex < pMinIOEdges.size(); treeIndex++)
		{
			if(DEBUG)
			{
				System.out.println("This is iteration " + treeIndex + "\n");
			}

			// Creates copies of the arrays of vertices, edges, regions, and adjacency list
			// for mutual exclusion.
			Vertex[] vertices = g.getVertices().clone();

			if(DEBUG)
			{
				System.out.println("Check to make sure regions were correctly initialized from Algorithm class.\n");
				for(Vertex v : vertices)
				{
					System.out.println("The following are regions for Vertex: " + v.getID());
					for(Region r : v.getRegions())
					{
						System.out.println(r.toString());
					}
					System.out.println();
				}
				System.out.println();
			}

			Edge[] edges = g.getEdges().clone();
			Region[] regions = g.getRegions().clone();
			ArrayList<Edge>[] adjacency = g.getAdjacencyList().clone();

			Arrays.sort(edges, (e1, e2) -> e1.getID() - e2.getID());
			/*
			 * An array of minimum adjacent edges to the edges in the tree, with each edge
			 * getting a "representative" in the array.
			 */
			Edge[] minWeight = new Edge[edges.length];

			if(DEBUG)
			{
				System.out.println("pMinIOEdges are as follows:\n");
				for(Edge e : pMinIOEdges)
				{
					System.out.println(e.toString());
				}
				System.out.println();
			}

			// Finalizing the minimum IO edge used and adding it to the tree.
			edges[pMinIOEdges.get(treeIndex).getID()].setFinalized(true);

			if(DEBUG)
			{
				System.out.println("Minimum inner-outer edge check for being finalized:\n"
							+ edges[pMinIOEdges.get(treeIndex).getID()].toString() + "\n");
			}

			if(DEBUG)
			{
				System.out.println("Edge to be added to tree:\n" + pMinIOEdges.get(treeIndex).toString() + "\n");
			}

			tree[treeIndex].add(pMinIOEdges.get(treeIndex));

			/*
			 * Selecting the vertices, covering the regions, and assigning weights to
			 * applicable vertex for minimum inner outer edge.
			 */
			for(Vertex v : tree[treeIndex].get(0).getVertices())
			{
				vertices[v.getID()].setSelected(true);

				if(DEBUG)
				{
					System.out.println("Minimum inner-outer edge's vertex check for being selected:\n"
								+ vertices[v.getID()].toString() + "\n");
				}

				for(Region r : v.getRegions())
				{
					regions[r.getID()].setCovered(true);

					if(DEBUG)
					{
						System.out.println("Minimum inner-outer edge's vertex's region for being covered:\n"
									+ regions[r.getID()].toString() + "\n");
					}
				}

				/*
				 * The first vertex with a weight will be of depth 2 from the minimum depth 2
				 * edge. Any regions of depth 3 adjacent to the vertex will be resolved.
				 */
				if(v.getDepth() == 2)
				{
					vertices[v.getID()].setWeight(pMinIOEdges.get(treeIndex).getWeight());

					if(DEBUG)
					{
						System.out.println("Minimum inner-outer edge's depth 2 vertex check for weight assignment:\n"
									+ vertices[v.getID()].toString() + "\n\n********************************\n");
					}

					for(Region temp : v.getRegions())
					{
						if(temp.getDepth() == 3)
						{
							if(DEBUG)
							{
								System.out.println(
											"Minimum inner-outer edge's depth 2 vertex check for cycle resolver call on region:\n"
														+ temp.toString() + "\n");
							}

							cycleResolver(temp, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}
				}
			}

			if(DEBUG)
			{
				System.out.println("Minimum inner-outer edge completion call to updateAdjacency().\n");
			}

			updateAdjacency(pMinIOEdges.get(treeIndex).getID(), treeIndex, minWeight, adjacency);

			/*
			 * The following will loop through until all the regions have been covered.
			 */

			boolean regionsNotCovered = false;
			for(Region r : regions)
			{
				if(r.isCovered() == false && r.getDepth() == 2)
				{
					if(DEBUG)
					{
						System.out.println("Region checked for coverage and depth:" + r.toString() + "\n");
					}

					regionsNotCovered = true;
				}
			}

			while(regionsNotCovered == true)
			{
				if(DEBUG)
				{
					System.out.println("Regions update with while loop for while(!regionsCovered).\n");
				}

				for(Region r : regions)
				{
					System.out.println(r.toString());
				}

				if(!tree[treeIndex].contains(minWeight[0]))
				{
					tree[treeIndex].add(minWeight[0]);
				}

				if(DEBUG)
				{
					System.out.println("Edge of minimum weight added to tree:\n" + minWeight[0] + "\n");
					System.out.println("Tree after addition of edge:");
					for(Edge e : tree[treeIndex])
					{
						System.out.println(e.toString());
					}
					System.out.println("\n");
				}

				// Direct pointers to the vertices in vertices[]
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

					if(DEBUG)
					{
						System.out.println("Vertex with weight of 0 checking for updated weight and being selected:\n"
									+ u.toString() + "\n");
					}

					for(Region r : u.getRegions())
					{
						regions[r.getID()].setCovered(true);

						if(DEBUG)
						{
							System.out.println(
										"Region of " + u.getID() + " check for being covered:\n" + r.toString() + "\n");
						}

						if(r.getDepth() == 3 && r.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println("Region:" + r.getID()
											+ " has depth of 3 and not resolved call to cycleResolver().\n");
							}

							cycleResolver(r, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}

					// Sets v to -1. Denotes it has been resolved at this point.
					v.setWeight(-1);

					if(DEBUG)
					{
						System.out.println("Vertex:" + v.getID()
									+ " weight has been set to -1 in indication of being resolved.");
					}
				}

				/*
				 * Updates the vertex if it has not already been added to tree along with new
				 * regions covered. Then the regions covered are resolved as applicable.
				 */
				if(v.getWeight() == 0)
				{
					v.setWeight(minWeight[0].getWeight() + u.getWeight());
					v.setSelected(true);

					if(DEBUG)
					{
						System.out.println("Vertex with weight of 0 checking for updated weight and being selected:\n"
									+ v.toString() + "\n");
					}

					for(Region r : v.getRegions())
					{
						regions[r.getID()].setCovered(true);

						if(DEBUG)
						{
							System.out.println(("Region of " + v.getID() + " check for beingcovered:\n" + r.toString()
										+ "\n"));
						}

						if(r.getDepth() == 3 && r.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println("Region:" + r.getID()
											+ " has depth of 3 and not resolved call to cycleResolver().\n");
							}

							cycleResolver(r, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}

					// Sets u to -1. Denotes it has been resolved at this point.
					u.setWeight(-1);

					if(DEBUG)
					{
						System.out.println("Vertex:" + u.getID()
									+ " weight has been set to -1 in indication of being resolved.");
					}
				}

				minWeight[0] = null;

				if(DEBUG)
				{
					System.out.println("Regions not covered: " + regionsNotCovered);
				}
			}

			for(Region r : regions)
			{
				if(r.isHold() == true)
				{
					if(DEBUG)
					{
						System.out.println("Region:" + r.getID() + " is on hold and resolveHold() called.\n");
					}

					resolveHold(r, vertices, edges, regions, adjacency, treeIndex, minWeight);
				}
			}

			if(DEBUG)
			{
				System.out.println("This is the end of the " + treeIndex + " iteration. \n \n"
							+ "************************************" + "\n \n");
			}
			for(Vertex v : vertices)
			{
				v.setSelected(false);
				v.setWeight(0);
				v.setFinalized(false);
			}
			for(Edge e : edges)
			{
				e.setFinalized(false);
				e.setSelected(false);
			}
			for(Region r : regions)
			{
				r.setCovered(false);
				r.setResolved(false);
				r.setHold(false);
			}
			for(int j = 0; j < edges.length; j++)
			{
				Collections.sort(adjacency[j], (temp1, temp2) -> temp1.getWeight() - temp2.getWeight());
				Collections.sort(adjacency[j], (temp1, temp2) -> temp2.getDepth() - temp1.getDepth());
			}
		}

		if(DEBUG)
		{
			System.out.println("The output:\n");
			int i = 0;
			for(ArrayList<Edge> e : tree)
			{
				System.out.println("Tree " + i);
				i++;
				for(Edge temp : e)
				{
					System.out.println(temp.toString());
				}
				System.out.println();
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
	private void updateAdjacency(int edgeID, int treeIndex, Edge[] minWeight, ArrayList<Edge>[] adjacency)
	{
		while(adjacency[edgeID].get(0).getSelected() == 1)
		{
			if(DEBUG)
			{
				System.out.println("Edge: " + edgeID + " minimum adjacent edge selected is removed.");
			}

			adjacency[edgeID].add(adjacency[edgeID].remove(0));
		}

		if(adjacency[edgeID].get(0).getSelected() != 1 && adjacency[edgeID].get(0) != null
					&& adjacency[edgeID].get(0).getDepth() == 3)
		{
			// Adds to the last place so that it does not overwrite minimum edges in the
			// array.
			minWeight[tree[treeIndex].size()] = adjacency[edgeID].get(0);

			if(DEBUG)
			{
				System.out.println("The minWeight tree after update:");
				for(Edge e : minWeight)
				{
					if(e != null)
					{
						System.out.println(e.toString());
					}
					else
					{
						System.out.println("Null");
					}
				}
				System.out.println();
			}

			if(DEBUG)
			{
				System.out.println("The adjacency list for edge: " + edgeID + " before removal:");
				for(Edge e : adjacency[edgeID])
				{
					System.out.println(e.toString());
				}
				System.out.println("\n");
			}

			adjacency[edgeID].add(adjacency[edgeID].remove(0));

			if(DEBUG)
			{
				System.out.println("The adjacency list for edge: " + edgeID + " after removal:");
				for(Edge e : adjacency[edgeID])
				{
					System.out.println(e.toString());
				}
				System.out.println("\n");
			}

			Arrays.sort(minWeight, new Comparator<Edge>()
			{
				@Override
				public int compare(Edge e1, Edge e2)
				{
					if(e1 == null && e2 == null)
					{
						return 0;
					}
					else if(e1 == null)
					{
						return 1;
					}
					else if(e2 == null)
					{
						return -1;
					}
					else
					{
						return e1.compareTo(e2);
					}
				}
			});

			if(DEBUG)
			{
				System.out.println("The minWeight tree after sorting.");
				for(Edge e : minWeight)
				{
					if(e != null)
					{
						System.out.println(e.toString());
					}
					else
					{
						System.out.println("Null");
					}
				}
				System.out.println();
			}
		}

		else
		{
			if(DEBUG)
			{
				System.out.println("Edge to be used from adjacency had degree < 3 and setting last index to null.\n");
				System.out.println("The adjacency list for edge: " + edgeID + ":");
				for(Edge e : adjacency[edgeID])
				{
					System.out.println(e.toString());
				}
				System.out.println("\n");
			}

			minWeight[tree[treeIndex].size()] = null;
		}
	}

	/*
	 * This method takes a region then resolves if applicable. If additional edges
	 * are added to the tree, then the method will recursively call itself until all
	 * regions covered are either resolved or on hold.
	 */
	private void cycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions, ArrayList<Edge>[] adjacency,
				int treeIndex, Edge[] minWeight)
	{
		Edge[] path1 = new Edge[r.getEdges().size() / 2];
		Edge[] path2 = new Edge[r.getEdges().size() - path1.length];

		if(DEBUG)
		{
			System.out.println("Rectangle being resolved in cycleResolver(): " + r.getID());

		}

		// The indices represent the information as follows: vertex id, vertex weight
		int[] rootInfo = { -1, -1 };
		for(Vertex v : r.getVertices())
		{
			if(vertices[v.getID()].getWeight() < rootInfo[1] || rootInfo[0] == -1)
			{
				if(v.getWeight() != 0)
				{
					if(DEBUG)
					{
						System.out.println("New root Vertex: " + v.getID());
					}

					rootInfo[0] = v.getID();
					rootInfo[1] = v.getWeight();
				}
			}
		}

		if(DEBUG)
		{
			System.out.println("Root of the rectangle being resolved is vertex: " + rootInfo[0] + " with a weight of: "
						+ rootInfo[1]);
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
					path1[i] = adjacency[path1[i - 1].getID()].get(j);
				}
			}
		}

		/*
		 * Adds edges to path2 until there are no remaining edges not contained in path1
		 * and path2.
		 */
		for(int i = 1; i < path2.length; i++)
		{
			for(int j = 0; j < adjacency[path2[i - 1].getID()].size(); j++)
			{
				if(r.getEdges().contains(adjacency[path2[i - 1].getID()].get(j))
							&& !Arrays.asList(path1).contains(adjacency[path2[i - 1].getID()].get(j)))
				{
					path2[i] = adjacency[path2[i - 1].getID()].get(j);
				}
			}
		}

		if(DEBUG)
		{
			System.out.println("Path 1 length: " + path1.length);
			for(Edge e : path1)
			{
				if(e != null)
				{
					System.out.println(e.toString());
				}
				else
				{
					System.out.println("Null");
				}

			}
			System.out.println();
			System.out.println("Path 2 length: " + path2.length);
			for(Edge e : path2)
			{
				System.out.println(e.toString());
			}
			System.out.println();
		}

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
					if(DEBUG)
					{
						System.out.println("Path 1 has all edges selected.\n");
					}

					regions[r.getID()].setResolved(true);

					for(Edge e : path1)
					{
						if(DEBUG)
						{
							System.out.println("Edge:" + e.getID() + " is being added to tree:" + treeIndex
										+ " and updateAdjacency is being called.\n");
						}
						edges[e.getID()].setSelected(true);
						if(!Arrays.asList(tree[treeIndex]).contains(edges[e.getID()]))
						{
							tree[treeIndex].add(edges[e.getID()]);
						}
						
						updateAdjacency(e.getID(), treeIndex, minWeight, adjacency);
						Vertex u = vertices[e.getVertices()[0].getID()];
						Vertex v = vertices[e.getVertices()[1].getID()];

						if(u.getWeight() == 0)
						{
							u.setWeight(v.getWeight() + e.getWeight());
							u.setSelected(true);

							if(DEBUG)
							{
								System.out.println("The following vertex should be selected and have a weight:\n"
											+ u.toString() + "\n");
							}
						}

						else if(v.getWeight() == 0)
						{
							v.setWeight(u.getWeight() + e.getWeight());
							v.setSelected(true);

							if(DEBUG)
							{
								System.out.println("The following vertex should be selected and have a weight:\n"
											+ v.toString() + "\n");
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

							if(DEBUG)
							{
								System.out.println("The following region should be covered by Vertex " + v.getID()
											+ ":\n" + r1.getID() + "\n");
							}

							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								if(DEBUG)
								{
									System.out.println("Region: " + r1.getID()
												+ " has met the conditions for cycleResolver().\n");
								}

								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
							}
						}
						for(Region r1 : u.getRegions())
						{
							regions[r1.getID()].setCovered(true);

							if(DEBUG)
							{
								System.out.println("The following region should be covered by Vertex " + u.getID()
											+ ":\n" + r1.getID() + "\n");
							}

							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								if(DEBUG)
								{
									System.out.println("Region: " + r1.getID()
												+ " has met the conditions for cycleResolver().\n");
								}

								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
							}
						}
					}
				}

				else if(path2Selected == true)
				{
					if(DEBUG)
					{
						System.out.println("Path 2 has all edges selected.\n");
					}

					regions[r.getID()].setResolved(true);
					for(Edge e : path2)
					{
						if(DEBUG)
						{
							System.out.println("Edge:" + e.getID() + " is being added to tree:" + treeIndex
										+ " and updateAdjacency is being called.\n");
						}

						edges[e.getID()].setSelected(true);
						if(!Arrays.asList(tree[treeIndex]).contains(edges[e.getID()]))
						{
							tree[treeIndex].add(edges[e.getID()]);
						}
						Vertex u = vertices[e.getVertices()[0].getID()];
						Vertex v = vertices[e.getVertices()[1].getID()];
						if(u.getWeight() == 0)
						{
							u.setWeight(v.getWeight() + e.getWeight());
							u.setSelected(true);

							if(DEBUG)
							{
								System.out.println("The following vertex should be selected and have a weight:\n"
											+ u.toString() + "\n");
							}
						}

						else if(v.getWeight() == 0)
						{
							v.setWeight(u.getWeight() + e.getWeight());
							v.setSelected(true);

							if(DEBUG)
							{
								System.out.println("The following vertex should be selected and have a weight:\n"
											+ v.toString() + "\n");
							}
						}
					}

					for(Edge e : path2)
					{
						Vertex u = vertices[e.getVertices()[0].getID()];
						Vertex v = vertices[e.getVertices()[1].getID()];
						for(Region r1 : v.getRegions())
						{
							regions[r1.getID()].setCovered(true);

							if(DEBUG)
							{
								System.out.println("The following region should be covered by Vertex " + v.getID()
											+ ":\n" + r1.getID() + "\n");
							}

							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								if(DEBUG)
								{
									System.out.println("Region: " + r1.getID()
												+ " has met the conditions for cycleResolver().\n");
								}

								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
							}
						}
						for(Region r1 : u.getRegions())
						{
							regions[r1.getID()].setCovered(true);

							if(DEBUG)
							{
								System.out.println("The following region should be covered by Vertex " + u.getID()
											+ ":\n" + r1.getID() + "\n");
							}

							if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
							{
								if(DEBUG)
								{
									System.out.println("Region: " + r1.getID()
												+ " has met the conditions for cycleResolver().\n");
								}

								cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
							}
						}
					}

				}
				else
				{
					regions[r.getID()].setHold(true);

					if(DEBUG)
					{
						System.out.println("Region: " + r.getID() + " should be on hold:\n" + r.toString() + "\n");
					}
				}
			}

			/*
			 * The following else if statements add the edges of the path to the tree while
			 * making sure the weights are updated accordingly. This will resolve all
			 * regions recursively until the region is either resolved or on hold.
			 */
			else if(set1.containsAll(set2))
			{
				if(DEBUG)
				{
					System.out.println("The two paths have equal length, but path2 is a proper subset of path1.");
				}

				regions[r.getID()].setResolved(true);
				for(Edge e : path1)
				{
					edges[e.getID()].setSelected(true);
					if(!Arrays.asList(tree[treeIndex]).contains(edges[e.getID()]))
					{
						tree[treeIndex].add(edges[e.getID()]);
					}
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should be selected and have a weight:\n"
										+ u.toString() + "\n");
						}
					}
					else if(v.getWeight() == 0)
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should be selected and have a weight:\n"
										+ v.toString() + "\n");
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

						if(DEBUG)
						{
							System.out.println("The following region should be covered by Vertex " + v.getID() + ":\n"
										+ r1.getID() + "\n");
						}

						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println(
											"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
							}

							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}
					for(Region r1 : u.getRegions())
					{
						regions[r1.getID()].setCovered(true);

						if(DEBUG)
						{
							System.out.println("The following region should be covered by Vertex " + u.getID() + ":\n"
										+ r1.getID() + "\n");
						}

						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println(
											"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
							}

							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}
				}
			}

			else if(set2.containsAll(set1))
			{
				if(DEBUG)
				{
					System.out.println("The two paths have equal length, but path1 is a proper subset of path2.\n");
				}

				regions[r.getID()].setResolved(true);
				for(Edge e : path2)
				{
					edges[e.getID()].setSelected(true);
					if(!Arrays.asList(tree[treeIndex]).contains(e))
					{
						tree[treeIndex].add(edges[e.getID()]);
					}
					updateAdjacency(e.getID(), treeIndex, minWeight, adjacency);
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];

					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should be selected and have a weight:\n"
										+ u.toString() + "\n");
						}
					}
					else if(v.getWeight() == 0)
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should be selected and have a weight:\n"
										+ v.toString() + "\n");
						}
					}
				}
				for(Edge e : path2)
				{
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					for(Region r1 : v.getRegions())
					{
						regions[r1.getID()].setCovered(true);

						if(DEBUG)
						{
							System.out.println("The following region should be covered for Vertex: " + v.getID() + ":\n"
										+ r1.getID() + "\n");
						}

						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println(
											"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
							}

							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}
					for(Region r1 : u.getRegions())
					{
						regions[r1.getID()].setCovered(true);

						if(DEBUG)
						{
							System.out.println("The following region should be covered by Vertex " + u.getID() + ":\n"
										+ r1.getID() + "\n");
						}

						if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
						{
							if(DEBUG)
							{
								System.out.println(
											"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
							}

							cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
						}
					}
				}
			}
			else
			{
				regions[r.getID()].setCovered(true);

				if(DEBUG)
				{
					System.out.println("The following region should be covered and call made to altCycleResolver():\n"
								+ r.toString() + "\n");
				}

				altCycleResolver(r, vertices, edges, regions, adjacency, treeIndex, path1, path2, minWeight);
			}
		}

		else
		{
			regions[r.getID()].setCovered(true);

			if(DEBUG)
			{
				System.out.println("The two path lengths were not the same.\n");
				System.out.println("The following region should be covered and call made to altCycleResolver():\n"
							+ r.toString() + "\n");
			}

			altCycleResolver(r, vertices, edges, regions, adjacency, treeIndex, path1, path2, minWeight);
		}
	}

	private void altCycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions,
				ArrayList<Edge>[] adjacency, int treeIndex, Edge[] path1, Edge[] path2, Edge[] minWeight)
	{
		if(DEBUG)
		{
			System.out.println("altCycleResolver has been called.\n");
		}

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

		if(DEBUG)
		{
			System.out.println("Path1's edges should be as follows:\n");
			for(Edge e : path1)
			{
				if(e != null)
				{
					System.out.println(e.toString());
				}
				else
				{
					System.out.println("Null");
				}
			}
			System.out.println("\nPath2's edges should be as follows:\n");
			for(Edge e : path2)
			{
				System.out.println(e.toString());
			}
			System.out.println("\nPath's edges should be as follows:\n");
			for(Edge e : path)
			{
				if(e != null)
				{
					System.out.println(e.toString());
				}
				else
				{
					System.out.println("Null");
				}
			}
			System.out.println("\n");
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
				if(DEBUG)
				{
					System.out.println(
								"Combined edges greater than previous weight and indices edges do not share root vertex.\n");
				}

				if(path[i].getVertices()[0].equals(path[(i + 1) % path.length].getVertices()[0])
							&& path[i].getVertices()[0].getDegree() < 4)
				{
					if(path[i].getSelected() != 1)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[0].equals(path[(i + 1) % path.length].getVertices()[1])
							&& path[i].getVertices()[0].getDegree() < 4)
				{
					if(path[i].getSelected() != 1)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[1].equals(path[(i + 1) % path.length].getVertices()[0])
							&& path[i].getVertices()[1].getDegree() < 4)
				{
					if(path[i].getSelected() != 1)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
				else if(path[i].getVertices()[1].equals(path[(i + 1) % path.length].getVertices()[1])
							&& path[i].getVertices()[1].getDegree() < 4)
				{
					if(path[i].getSelected() != 1)
					{
						weight2Edges = path[i].getWeight() + path[(i + 1) % path.length].getWeight();
						indexPair1 = i;
						indexPair2 = (i + 1) % path.length;
					}
				}
			}
		}

		if(DEBUG)
		{
			System.out.println(
						"The following are the results for the calculation of two weighted edges, single weighted edge, and indices:");
			System.out.println("The weight of the greatest combined weight edges: " + weight2Edges);
			System.out.println("The indices for the pair are: " + indexPair1 + " and " + indexPair2);
			System.out.println("Corresponding edges:\n" + path[indexPair1].toString() + "\n"
						+ path[indexPair2].toString() + "\n");
			System.out.println("The weight of the greatest edge: " + weight1Edge);
			System.out.println("The index for the edge is: " + indexMax);
			System.out.println("Corresponding edge:\n" + path[indexMax].toString() + "\n");
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
			if(DEBUG)
			{
				System.out.println("The weight of the two adjacent edges is greater than max weighted edge.\n");
			}

			regions[r.getID()].setResolved(true);

			if(DEBUG)
			{
				System.out.println("The following region should be resolved:\n" + regions[r.getID()].toString() + "\n");
			}

			for(Edge e : path)
			{
				if(!e.equals(path[indexPair1]) && !e.equals(path[indexPair2]))
				{
					if(DEBUG)
					{
						System.out.println("Tree updated and updateAdjacency called for Edge: " + e.getID() + "\n");
					}

					edges[e.getID()].setSelected(true);
					if(!tree[treeIndex].contains(e))
					{
						tree[treeIndex].add(edges[e.getID()]);
					}
					updateAdjacency(e.getID(), treeIndex, minWeight, adjacency);

					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should have a weight and be selected:\n"
										+ u.toString() + "\n");
						}

					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);

						if(DEBUG)
						{
							System.out.println("The following vertex should have a weight and be selected:\n"
										+ v.toString() + "\n");
						}
					}
				}
			}

			/*
			 * Ensures the addition of all edges to the tree and selection before calling
			 * cycleResolver() on new edges.
			 */
			for(Edge e : path)
			{
				Vertex u = vertices[e.getVertices()[0].getID()];
				Vertex v = vertices[e.getVertices()[1].getID()];
				for(Region r1 : v.getRegions())
				{
					regions[r1.getID()].setCovered(true);

					if(DEBUG)
					{
						System.out.println("The following region should be covered by Vertex " + v.getID() + ":\n"
									+ r1.getID() + "\n");
					}

					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						if(DEBUG)
						{
							System.out.println(
										"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
						}

						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
					}
				}
				for(Region r1 : u.getRegions())
				{
					regions[r1.getID()].setCovered(true);

					if(DEBUG)
					{
						System.out.println("The following region should be covered by Vertex " + u.getID() + ":\n"
									+ r1.getID() + "\n");
					}

					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						if(DEBUG)
						{
							System.out.println(
										"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
						}

						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
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
					edges[e.getID()].setSelected(true);
					if(!tree[treeIndex].contains(edges[e.getID()]))
					{
						tree[treeIndex].add(edges[e.getID()]);
					}
					Vertex u = vertices[e.getVertices()[0].getID()];
					Vertex v = vertices[e.getVertices()[1].getID()];
					if(u.getWeight() == 0)
					{
						u.setWeight(v.getWeight() + e.getWeight());
						u.setSelected(true);
					}
					else
					{
						v.setWeight(u.getWeight() + e.getWeight());
						v.setSelected(true);
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

					if(DEBUG)
					{
						System.out.println("The following region should be covered by Vertex " + v.getID() + ":\n"
									+ r1.getID() + "\n");
					}

					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						if(DEBUG)
						{
							System.out.println(
										"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
						}

						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
					}
				}
				for(Region r1 : u.getRegions())
				{
					regions[r1.getID()].setCovered(true);

					if(DEBUG)
					{
						System.out.println("The following region should be covered by Vertex " + u.getID() + ":\n"
									+ r1.getID() + "\n");
					}

					if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
					{
						if(DEBUG)
						{
							System.out.println(
										"Region: " + r1.getID() + " has met the conditions for cycleResolver().\n");
						}

						cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
					}
				}
			}
		}

	}

	private void alt2CycleResolver(Region r, Vertex[] vertices, Edge[] edges, Region[] regions,
				ArrayList<Edge>[] adjacency, int treeIndex, Edge[] minWeight)
	{
		Edge[] path1 = new Edge[r.getEdges().size() / 2];
		Edge[] path2 = new Edge[r.getEdges().size() - path1.length];

		if(DEBUG)
		{
			System.out.println("Rectangle:" + r.getID() + " being resolved in alt2CycleResolver.");
		}

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

		if(DEBUG)
		{
			System.out.println("Root of the rectangle being resolved is vertex:" + rootInfo[0] + " with a weight of: "
						+ rootInfo[1]);
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
					path1[i] = adjacency[path1[i - 1].getID()].get(j);
				}
			}
		}

		if(DEBUG)
		{
			System.out.println("Path 1 length: " + path1.length);
			for(Edge e : path1)
			{
				System.out.println(e.toString());
			}
			System.out.println();
		}

		/*
		 * The choice between the paths is arbitrary at this point, so either one can be
		 * picked. Thus, path1 will be added to the tree.
		 */

		for(Edge e : path1)
		{
			Vertex u = vertices[e.getVertices()[0].getID()];
			Vertex v = vertices[e.getVertices()[1].getID()];

			edges[e.getID()].setSelected(true);
			if(!tree[treeIndex].contains(edges[e.getID()]))
			{
				tree[treeIndex].add(edges[e.getID()]);
			}
			updateAdjacency(e.getID(), treeIndex, minWeight, adjacency);
			for(Region r1 : v.getRegions())
			{
				if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
				{
					cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
				}
			}
			for(Region r1 : u.getRegions())
			{
				if(r1.getDepth() == 3 && r1.isHold() == false && r1.isResolved() == false)
				{
					cycleResolver(r1, vertices, edges, regions, adjacency, treeIndex, minWeight);
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
				int treeIndex, Edge[] minWeight)
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
			if(DEBUG)
			{
				System.out.println("Region:" + r.getID()
							+ " on hold did not have all adjacent regions covered and calls alt2CycleResolver.\n");
			}

			r.setResolved(true);
			r.setHold(false);
			alt2CycleResolver(r, vertices, edges, regions, adjacency, treeIndex, minWeight);

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