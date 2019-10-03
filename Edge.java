package mlc.components;

import java.util.ArrayList;

public class Edge {

	private ArrayList<Vertex> vertices = new ArrayList<>();
	private int weight, id;
	private boolean selected, finalized;
	private final int outer;
	
	public Edge(int w, int i, Vertex u, Vertex v) {
		
		vertices.add(u);
		vertices.add(v);
		weight = w;
		id = i;
		if(u.outer() && v.outer()) {
			outer = 3;
			//Both vertices are outer making e an outer edge.
		}
		else if(u.outer() || v.outer()) {
			outer = 2;
			//Vertices are both inner and outer making e an inner-outer edge.
		}
		else {
			outer = 1;
			//Both vertices are inner making e an inner edge.
		}
		
	}
	
	public int getID() {
		return id;
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
    		return false;
    	}
    	else if(!(obj instanceof Edge)) {
    		return false;
    	}
    	else if(id == ((Edge) obj).getID()) {
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	
	@Override
	public int hashCode() {
		int prime = 17;
		int result = 5;
		result = prime * result + id;
		return result;
	}
	
}
