package mlc.components;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
 * The Graph class keeps track of the vertices, edges, regions, and minimum inner-outer edges.
 * Within the graph, there are methods to  sort the list of edges along with other basic
 * methods to interact with the object.
 */

public class Graph
{

	/*
	 * Variables of the Graph class are listed below.
	 */
	private Vertex[] vertices;
	private Edge[] edges;
	private Region[] regions;
	private ArrayList<Edge> minimumIOEdge;

	/*
	 * The length matches the number of edges. Then a reference to the Edge object
	 * is kept in the 2-D adjustable array.
	 */
	private ArrayList<Edge>[] adjacency;

	// Calls initialize to construct the graph.
	public Graph(File source) throws Exception
	{
		initialize(source);
	}

	/*
	 * The following method requests a file path from the user to initialize the
	 * graph class. Then the file is read according to the following format:
	 * Vertices # of Vertices Depth of Vertices (Vertex per line) Edges # of Edges
	 * Weight,Vertex u, Vertex v (Vertices are the indexes, Edge per line) Regions #
	 * of Regions Edge 1,Edge 2,...,Edge 3 (Edges are the indexes, Region per line)
	 */
	private void initialize(File source) throws Exception
	{
		String temp;
		String[] input;
		int num;
		Scanner sc = new Scanner(source);
		temp = sc.nextLine();
		if(!temp.equals("Vertices"))
		{
			throw new Exception(
						"The word \"Vertices\" must be denoted followed by the number of vertices on a new line.");
		}

		// Reads the number of vertices to follow. Initializes the array,
		// vertices, to
		// the number of vertices.
		num = sc.nextInt();
		vertices = new Vertex[num];

		/*
		 * Adds the vertices to the Vertex[] from the file. Reads for a 1, 2, or 3 to
		 * determine the depth of the vertex, where 3 represents a depth >= 3.
		 */
		sc.nextLine();
		for(int i = 0; i < num; i++)
		{
			temp = sc.nextLine();

			// Throws an exception if either true or false are not used.
			if(temp.matches("[^0-3]"))
			{
				throw new Exception("The depth of the vertex can only be 1, 2, or 3.");
			}
			vertices[i] = new Vertex(Integer.parseInt(temp), i);
		}
		System.out.println("Vertices have been successfully intialized.\n");

		// Checks to make sure the format has been followed correctly.
		temp = sc.nextLine();
		if(!temp.equals("Edges"))
		{
			throw new Exception("The word \"Edges\" must be denoted followed by the number of edges on a new line.");
		}

		// Reads the number of edges to follow. Initializes the array, edges, to
		// the
		// number of edges.
		num = sc.nextInt();
		edges = new Edge[num];
		sc.nextLine();

		/*
		 * Adds the edges to the Edge[] from the file. Reads numbers separated by a
		 * comma with the first being the weight. These are the vertices, by zero-index,
		 * in correspondence to the Vertex[] initialization.
		 */
		for(int i = 0; i < num; i++)
		{
			temp = sc.nextLine();

			// Throws an exception if anything except numbers or commas are
			// used.
			if(temp.matches("[^0-9,]"))
			{
				throw new Exception("There must only be numbers separated by a comma for an edge input.");
			}
			input = temp.split(",", 0);
			edges[i] = new Edge(Integer.parseInt(input[0]), i, vertices[Integer.parseInt(input[1])],
						vertices[Integer.parseInt(input[2])]);
		}
		System.out.println("Edges have been successfully initialized.\n");

		// Checks to make sure the format has been followed correctly.
		temp = sc.nextLine();
		if(!temp.equals("Regions"))
		{
			throw new Exception(
						"The word \"Regions\" must be denoted followed by the number of regions on a new line.");
		}

		// Reads the number of regions to follow. Initializes the array,
		// regions, to the
		// number of regions.
		num = sc.nextInt();
		regions = new Region[num];
		sc.nextLine();

		/*
		 * Adds the regions to the Region[] from the file. Reads the boolean for the
		 * conditional outer followed by numbers separated by a comma. These are the
		 * edges, by zero-index, in correspondence to the Edge[] initialization.
		 */
		for(int i = 0; i < num; i++)
		{
			temp = sc.nextLine();
			input = temp.split(",");

			// Throws an exception if anything except numbers or commas are
			// used.
			if(temp.matches("[^0-9,]"))
			{
				throw new Exception("There must only be numbers separated by commas for arguments after the first.");
			}

			// Creates an ArrayList of edges that is passed as a parameter for a
			// new Region.
			ArrayList<Edge> e = new ArrayList<>();
			for(int j = 0; j < input.length; j++)
			{
				e.add(edges[Integer.parseInt(input[j])]);
			}
			regions[i] = new Region(e, i);
		}
		System.out.println("Regions have been successfully initialized.\n");
		adjacencyList();
		System.out.println("Adjacency list has been successfully initialized.\n");
		regionsToVertices();
		System.out.println("Regions have been successfully added to vertices.\n");
		regionsToEdges();
		System.out.println("Regions have been successfully added to edges.\n");
		edgesToVertices();
		System.out.println("Edges have been successfully added to vertices.\n");
		minimumIOEdge = minimumIOEdges();
		sc.close();
	}

	/*
	 * Sorts edges by selected status, then weight, then outer status. Since merge
	 * sort is used for objects, the sort will be stable.
	 */
	public void sortEdges()
	{
		Arrays.sort(edges, (e1, e2) -> (int) e2.getSelected() - (int) e1.getSelected());
		Arrays.sort(edges, (e1, e2) -> e1.getWeight() - e2.getWeight());
		Arrays.sort(edges, (e1, e2) -> e2.getDepth() - e1.getDepth());
	}

	/*
	 * Initializes the Region[] of each edge.
	 */
	private void regionsToEdges()
	{
		for(Region r : regions)
		{
			for(Edge e : r.getEdges())
			{
				e.setRegion(r);
			}
		}
	}

	/*
	 * Initializes the Region[] of each vertex.
	 */
	private void regionsToVertices()
	{
		for(Region r : regions)
		{
			for(Vertex v : r.getVertices())
			{
				v.setRegion(r);
			}
		}
		for(Vertex v : vertices)
		{
			Arrays.sort(v.getRegions().toArray(new Region[v.getRegions().size()]), (r1, r2) -> r1.getID() - r2.getID());
		}
	}

	private void edgesToVertices()
	{
		for(Edge e : edges)
		{
			for(Vertex v : e.getVertices())
			{
				v.setEdge(e);
			}
		}
	}

	public void sortEdges(Edge[] e)
	{
		Arrays.sort(e, (e1, e2) -> (int) e2.getSelected() - (int) e1.getSelected());
		Arrays.sort(e, (e1, e2) -> e1.getWeight() - e2.getWeight());
		Arrays.sort(e, (e1, e2) -> e2.getDepth() - e1.getDepth());
	}

	/*
	 * Initializes the edge adjacency list.
	 */
	@SuppressWarnings("unchecked")
	private void adjacencyList()
	{
		adjacency = new ArrayList[edges.length];
		Vertex[] e1 = new Vertex[2];
		Vertex[] e2 = new Vertex[2];
		for(int j = 0; j < edges.length; j++)
		{
			adjacency[j] = new ArrayList<>();
		}
		for(int k = 0; k < edges.length; k++)
		{
			e1 = edges[k].getVertices();
			for(int j = k + 1; j < edges.length; j++)
			{
				e2 = edges[j].getVertices();
				if(e1[0].equals(e2[0]))
				{
					// System.out.println("Edge 1 - " + edges[k].toString() +
					// "\nEdge 2 - " + edges[j].toString() + "\n");
					adjacency[edges[k].getID()].add(edges[j]);
					adjacency[edges[j].getID()].add(edges[k]);
				}
				else if(e1[0].equals(e2[1]))
				{
					// System.out.println("Edge 1 - " + edges[k].toString() +
					// "\nEdge 2 - " + edges[j].toString() + "\n");
					adjacency[edges[k].getID()].add(edges[j]);
					adjacency[edges[j].getID()].add(edges[k]);
				}
				else if(e1[1].equals(e2[0]))
				{
					// System.out.println("Edge 1 - " + edges[k].toString() +
					// "\nEdge 2 - " + edges[j].toString() + "\n");
					adjacency[edges[k].getID()].add(edges[j]);
					adjacency[edges[j].getID()].add(edges[k]);
				}
				else if(e1[1].equals(e2[1]))
				{
					// System.out.println("Edge 1 - " + edges[k].toString() +
					// "\nEdge 2 - " + edges[j].toString() + "\n");
					adjacency[edges[k].getID()].add(edges[j]);
					adjacency[edges[j].getID()].add(edges[k]);
				}
			}
		}
	}

	private ArrayList<Edge> minimumIOEdges()
	{
		ArrayList<Edge> min = new ArrayList<>();
		for(Edge e : edges)
		{
			if(e.getDepth() == 2)
			{
				min.add(e);
			}
		}
		int minWeight = min.get(0).getWeight();
		for(Edge e : edges)
		{
			if(e.getWeight() < minWeight)
			{
				minWeight = e.getWeight();
			}
		}
		for(Edge e : edges)
		{
			if(e.getWeight() > minWeight)
			{
				min.remove(e);
			}
		}

		/*
		 * System.out.println("Printing edges of minimum inner outer edge list: ");
		 * for(Edge e : min) { System.out.println(e.toString()); } System.out.println();
		 */

		return min;
	}

	public Vertex[] getVertices()
	{
		return vertices;
	}

	public Region[] getRegions()
	{
		return regions;
	}

	public Edge[] getEdges()
	{
		return edges;
	}

	public ArrayList<Edge> getMinIOEdges()
	{
		return minimumIOEdge;
	}

	public ArrayList<Edge>[] getAdjacencyList()
	{
		return adjacency;
	}

	public ArrayList<Edge> getEdgeAdjacency(Edge e)
	{
		return adjacency[e.getID()];
	}

	// @Override
	// public void run() {
	// try {
	// initialize();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}