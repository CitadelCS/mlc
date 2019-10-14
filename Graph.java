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

public class Graph{
	/*
	 * Variables of the Graph class are listed below.
	 */
	private Vertex[] vertices;
	private Edge[] edges;
	private Region[] regions;
	private ArrayList<Edge> minimumIOEdge;
	private ArrayList<Edge>[] adjacency;
	
	public Graph(File source) throws Exception{
		initialize(source);
		
		//run();
	}
	
	@SuppressWarnings("unchecked")
	public void initialize(File source) throws Exception {
		String temp;
		String[] input;
		int num;
		//boolean b = true;
		
		//Scanner sc closed at the end of initialize(), so there is not a resource leak.
		//@SuppressWarnings("resource")
		//Scanner sc = new Scanner(System.in);
		Scanner sc = new Scanner(source);
		/*
		 * System.out.print("Input the path name for the file:");
		 try {
			file = new File(sc.nextLine());
			sc = new Scanner(file);
			b = false;
		}
		catch(FileNotFoundException e) {
			throw new FileNotFoundException("Incorrect file pathname.");
		} 
		 */
		
		
		//Checks to make sure the format has been followed correctly.
		temp = sc.nextLine();
		if(!temp.equals("Vertices")) {
			throw new Exception("The word \"Vertices\" must be denoted followed by the number of vertices on a new line.");
		}
		
		//Reads the number of vertices to follow. Initializes the array, vertices, to the number of vertices.
		num = sc.nextInt();
		vertices = new Vertex[num];
		
		/*
		 * Adds the vertices to the Vertex[] from the file. Reads for a 1 or 0 (true or false) to 
		 * determine the vertex's incidence to outside region.
		 */
		sc.nextLine();
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	System.out.println(temp);
	    	
	    	//Throws an exception if either true or false are not used.
	    	if(temp.matches("[^tT][^rR][^uU][^Ee]|[^fF][^Aa][^lL][^Ss][^eE]")) {
	    		throw new Exception("There must be either true or false");
	    	}
	    	vertices[i] = new Vertex(Boolean.parseBoolean(temp), i);
	    }
	    System.out.println("Vertices have been successfully intialized.");
	    //Checks to make sure the format has been followed correctly.
	    temp = sc.nextLine();
	    System.out.println(temp);
	    if(!temp.equals("Edges")) {
	    	throw new Exception("The word \"Edges\" must be denoted followed by the number of edges on a new line.");
	    }
		
	    //Reads the number of edges to follow. Initializes the array, edges, to the number of edges.
	    num = sc.nextInt();
	    edges = new Edge[num];
	    sc.nextLine();
	    
	    /*
	     * Adds the edges to the Edge[] from the file. Reads numbers separated by a comma with the 
	     * first being the weight. These are the vertices, not by zero-index, in correspondence to the 
	     * Vertex[] initialization.
	     */
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	System.out.println(temp);
	    	
	    	//Throws an exception if anything except numbers or commas are used.
	    	if(temp.matches("[^0-9,]")) {
	    		throw new Exception("There must only be numbers separated by a comma for an edge input.");
	    	}
	    	input = temp.split(",", 0);
	    	edges[i] = new Edge(Integer.parseInt(input[0]), i, vertices[Integer.parseInt(input[1])-1], vertices[Integer.parseInt(input[2])-1]);
	    }
	    System.out.println("Edges have been initialized successfully.");
		
	    /*
		 * The length matches the number of edge. Then a reference to the Edge object is kept
		 * in the 2-D adjustable array.
		 */
		adjacency = new ArrayList[edges.length];
		for(int i = 0; i < edges.length; i++) {
			adjacency[i] = new ArrayList<>();
		}
		//Initializes the edge adjacency list.
	    for(int i = 0; i < edges.length; i++) {
	    	//System.out.println("i: "+ i);
	    	for(int j = 0; j < edges.length; j++) {
	    		//System.out.println("j: "+ j);
	    		if(edges[i].getVertices()[0].equals(edges[j].getVertices()[0]) || (edges[i].getVertices()[0].equals(edges[j].getVertices()[1]))) {
	    			adjacency[i].add(edges[j]);
	    			System.out.println(i + ":" + j);
	    		}
	    		else if(edges[i].getVertices()[1].equals(edges[j].getVertices()[0]) || (edges[i].getVertices()[1].equals(edges[j].getVertices()[1]))) {
	    			adjacency[i].add(edges[j]);
	    			System.out.println(i + ":" + j);
	    		}
	    	}
	    }
	    System.out.println("Adjacency list has been initialized successfully.");
	    //Checks to make sure the format has been followed correctly.
	    temp = sc.nextLine();
	    System.out.println(temp);
	    if(!temp.equals("Regions")) {
	    	throw new Exception("The word \"Regions\" must be denoted followed by the number of regions on a new line.");
	    }
	    
	    //Reads the number of regions to follow. Initializes the array, regions, to the number of regions.
	    num = sc.nextInt();
	    regions = new Region[num];
	    sc.nextLine();
	    
	    
	    /*
	     * Adds the regions to the Region[] from the file. Reads the boolean for the conditional outer
	     * followed by numbers separated by a comma. These are the edges, not by zero-index, in
	     * correspondence to the Edge[] initialization.
	    */
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	System.out.println(temp);
	    	input = temp.split(",");
	    	
	    	//Throws an exception if anything except numbers or commas are used.
	    	if(temp.matches("[^0-9,]")) {
	    		throw new Exception("There must only be numbers separated by commas for arguments after the first.");
	    	}
	    	
	    	//Creates an ArrayList of edges that is passed as a parameter for a new Region.
	    	ArrayList<Edge> e = new ArrayList<>();
	    	for(int j = 0; j < input.length; j++) {
	    		e.add(edges[Integer.parseInt(input[j])-1]);
	    	}
	    	regions[i] = new Region(e);
	    }
	    //Sort the edges initialized along with compose a list of minimum inner-outer edges. The scanner is also closed.
	    sortEdges(edges);
	    minimumIOEdge = minimumIOEdges();
	    sc.close();
	    }
	
	//Sorts edges by selected status, then weight, then outer status. Since merge sort is used for objects, the sort will be stable.
	public void sortEdges(Edge[] e) {
		Arrays.sort(e, (e1, e2) -> (int) e2.getSelected() - (int)e1.getSelected());
		Arrays.sort(e, (e1, e2) -> e1.getOuter()-e2.getOuter());
		Arrays.sort(e, (e1, e2) -> e1.getWeight()-e2.getWeight());
	}
	
	public ArrayList<Edge> minimumIOEdges() {
		ArrayList<Edge> min = new ArrayList<>();
		for(Edge e: edges) {
			if(e.getOuter() == 2) {
				min.add(e);
			}
		}
		return min;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}
	public Region[] getRegions() {
		return regions;
	}
	public Edge[] getEdges() {
		return edges;
	}
	public ArrayList<Edge> getIOEdges(){
		return minimumIOEdge;
	}
	
	//@Override
	//public void run() {
	//	try {
	//		initialize();
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//	}
	//}
	
}