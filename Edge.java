package mlc.components;

import java.util.ArrayList;

public class Edge {

	private ArrayList<Vertex> vertices = new ArrayList<>();
	private int weight;
	private boolean selected, finalized;
	private final int outer;
	
	public Edge(int w, Vertex u, Vertex v) {
		
		vertices.add(u);
		vertices.add(v);
		weight = w;
		if(u.outer() && v.outer()) {
			outer = 1;
			//Both vertices are outer making e an outer edge.
		}
		else if(u.outer() || v.outer()) {
			outer = 2;
			//Vertices are both inner and outer making e an inner-outer edge.
		}
		else {
			outer = 3;
			//Both vertices are inner making e an inner edge.
		}
		
	}
	
	public void setSelected() {
		selected = !(selected);
	}
	
	public boolean selected() {
		return selected;
	}
	
	public boolean finalized() {
		return finalized;
	}
	
	public int outer() {
		return outer;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public ArrayList<Vertex> getVertices(){
		return vertices;
	}
	
	
}
