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

	public void procedure1() {
		ArrayList<Edge> p1Edges = new ArrayList<>();
		for (int i = 0; i < g.getEdges().length; i++) {
			Edge e = g.getEdges()[i].copy();
			p1Edges.add(e);
			if (e.getDepth() == 3) {
				e.setSelected();
			} else {
				break;
			}
		}
		
		
		boolean b = true;
		for(Region r: g.getRegions()) {
			if(!r.getOuter()) {
				for(int k = 0; k < r.getEdges().size(); k++) {
					if(r.getEdges().get(k).getSelected() == 0) {
						b = false; 
					}
				}
			}
			if(!b) {
				//tree.add()
			}
			b = true;
		}
	}

}
