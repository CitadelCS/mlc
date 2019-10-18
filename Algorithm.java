package mlc.procedures;

import java.io.File;
import java.util.ArrayList;

import mlc.components.*;

public class Algorithm {
	// Variables used for the class.
	private Graph g;
	private ArrayList<Edge> tree;

	public Algorithm(File source) throws Exception {
		g = new Graph(source);
	}

	public Graph getGraph() {
		return g;
	}

	public void run() {
		g.sortEdges();
		procedure1();
		printVertices();
		printEdges();
	}
	
	public void procedure2() {
		
	}

	public void procedure1() {
		Edge[] edges = g.getEdges();
		Vertex[] e1 = new Vertex[2];
		int i = 0;
			for (i = 0; edges[i].getDepth() >= 3; i++) {
				e1 = edges[i].getVertices();
				e1[0].incDegree();
				e1[1].incDegree();
			}
			for(int j = 0; j < i; j++) {
				if(edges[j].getVertices()[0].getDegree() == 1 || edges[j].getVertices()[1].getDegree() == 1) {
					edges[j].setFinalized();
				}
			}
	}
	
	public void printEdges() {
		for(Edge e: g.getEdges()) { 
			System.out.println(e.toString()); 
		}
	}
	
	public void printVertices() {
		for (Vertex v : g.getVertices()) { 
			System.out.println(v.toString()); 
		}
	}
	
	public void printAdjacencyList() {
		ArrayList<Edge>[] temp = g.getAdjacencyList();
		for (ArrayList<Edge> e : temp) {
			for (Edge t : e) {
				System.out.print(t.getID() + ",");
			}
			System.out.println(" ");
		}
	}
}
