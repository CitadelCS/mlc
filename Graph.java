package mlc.procedures;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import mlc.components.*;

public class Graph {
	
	private Vertex[] vertices;
	private Edge[] edges;
	private Region[] regions;
	private ArrayList<Edge> minimumIOEdge;
	
	/*
	 * The length matches the number of edge. Then a reference to the Edge object is kept
	 * in the 2-D adjustable array.
	 */
	private ArrayList<Edge>[] adjacency = new ArrayList[edges.length];
	
	public void initialize(Graph g) throws Exception {
		String temp;
		String[] input;
		int num;
		
		//Scanner sc closed at the end of initialize(), so there is not a resource leak.
		Scanner sc = new Scanner(System.in);
		System.out.println("Input the path name for the file");
		File file = new File(sc.nextLine());
		sc = new Scanner(file);
		
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
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	
	    	//Throws an exception if either true or false are not used.
	    	if(temp.matches("[^tT][^rR][^uU][^Ee]|[^fF][^Aa][^lL][^Ss][^eE]")) {
	    		throw new Exception("There must be either true or false");
	    	}
	    	vertices[i] = new Vertex(Boolean.parseBoolean(temp), i);
	    }
	    
	    //Checks to make sure the format has been followed correctly.
	    temp = sc.nextLine();
	    if(!temp.equals("Edges")) {
	    	throw new Exception("The word \"Edges\" must be denoted followed by the number of edges on a new line.");
	    }
		
	    //Reads the number of edges to follow. Initializes the array, edges, to the number of edges.
	    num = sc.nextInt();
	    edges = new Edge[num];
	    
	    /*
	     * Adds the edges to the Edge[] from the file. Reads numbers separated by a comma with the 
	     * first being the weight. These are the vertices by zero-index in correspondence to the 
	     * Vertex[] initialization.
	     */
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	
	    	//Throws an exception if anything except numbers or commas are used.
	    	if(temp.matches("[^0-9,]")) {
	    		throw new Exception("There must only be numbers separated by a comma for an edge input.");
	    	}
	    	input = temp.split(",", 0);
	    	edges[i] = new Edge(Integer.parseInt(input[0]), i, vertices[Integer.parseInt(input[1])], vertices[Integer.parseInt(input[2])]);
	    }
	    
	    //Checks to make sure the format has been followed correctly.
	    temp = sc.nextLine();
	    if(!temp.equals("Regions")) {
	    	throw new Exception("The word \"Regions\" must be denoted followed by the number of regions on a new line.");
	    }
	    
	    //Reads the number of regions to follow. Initializes the array, regions, to the number of regions.
	    num = sc.nextInt();
	    regions = new Region[num];
	    
	    
	    /*
	     * Adds the regions to the Region[] from the file. Reads the boolean for the conditional outer
	     * followed by numbers separated by a comma. These are the edges by zero-index in
	     * correspondence to the Edge[] initialization.
	    */
	    for(int i = 0; i < num; i++) {
	    	temp = sc.nextLine();
	    	input = temp.split(",");
	    	
	    	//Throws an exception if anything except numbers or commas are used.
	    	if(temp.matches("[^0-9,]")) {
	    		throw new Exception("There must only be numbers separated by commas for arguments after the first.");
	    	}
	    	
	    	//Creates an ArrayList of edges that is passed as a parameter for a new Region.
	    	ArrayList<Edge> e = new ArrayList<>();
	    	for(int j = 0; j < input.length; j++) {
	    		e.add(edges[Integer.parseInt(input[j])]);
	    	}
	    	regions[i] = new Region(e);
	    }
    	//Initializes the edge adjacency list.
	    for(int j = 0; j < edges.length; j++) {
	    	for(int i = 1; i < edges.length; i++) {
	    		if(edges[i].getVertices().equals(edges[j].getVertices())) {
	    			adjacency[j].add(edges[j]);
	    		}
	    	}
	    }
	    sc.close();
	    g.sortEdges(edges);
	    minimumIOEdge = g.minimumInnerOuterEdge();
	}
	
	//Sorts edges by weight then by outer status. Since merge sort is used for objects, the sort will be stable.
	public void sortEdges(Edge[] e ) {
		Arrays.sort(e, (e1, e2) -> e1.getWeight()-e2.getWeight());
		Arrays.sort(e, (e1, e2) -> e1.outer()-e2.outer());
	}
	
	public ArrayList<Edge> minimumInnerOuterEdge() {
		ArrayList<Edge> min = new ArrayList<>();
		for(Edge e: edges) {
			if(e.outer() == 2) {
				min.add(e);
			}
		}
		return min;
	}
	
}