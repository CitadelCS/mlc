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
	    //Sort the edges initialized along with compose a list of minimum inner-outer edges. The scanner is also closed.
	    g.sortEdges();
	    g.procedure1();
		
	}

}
