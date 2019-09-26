package mlc.procedures;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import mlc.components.*;

public class Algorithm {
	
	public static void main(String[] args) throws Exception {
	
		Vertex[] vertices;
		Edge[] edges;
		Region[] regions;
		String temp;
		String[] var;
		int num;
		
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
	    	vertices[i] = new Vertex(Boolean.parseBoolean(temp));
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
	    	var = temp.split(",", 0);
	    	edges[i] = new Edge(Integer.parseInt(var[0]), vertices[Integer.parseInt(var[1])], vertices[Integer.parseInt(var[2])]);
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
	    	var = temp.split(",");
	    	
	    	//Throws an exception if anything except numbers or commas are used.
	    	if(temp.matches("[^0-9,]")) {
	    		throw new Exception("There must only be numbers separated by commas for arguments after the first.");
	    	}
	    	
	    	//Creates an ArrayList of edges that is passed as a parameter for a new Region.
	    	ArrayList<Edge> e = new ArrayList<>();
	    	for(int j = 0; j < var.length; j++) {
	    		e.add(edges[Integer.parseInt(var[j])]);
	    	}
	    	regions[i] = new Region(e);
	    }
	    
	}
}