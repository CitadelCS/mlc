package mlc.components;

import java.util.ArrayList;

public class Vertex {
	
	private ArrayList<Edge> edges =  new ArrayList<>();
	private ArrayList<Region> regions = new ArrayList<>();
	private boolean selected, root, leaf, finalized;
	private final int id, depth;
	
	public Vertex(int d, int id) {
		
		depth = d;
		root = false;
		leaf = false;
		finalized = false;
		this.id = id;
		
	}
	
	public int getID() {
		return id;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected() {
		selected = !selected;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
	
	public ArrayList<Region> getRegion(){
		return regions;
	}
	
	public boolean finalized() {
		return finalized;
	}
	
	public boolean leaf() {
		return leaf;
	}
	
	public boolean root() {
		return root;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
    		return false;
    	}
    	else if(!(obj instanceof Vertex)) {
    		return false;
    	}
    	else if(id == ((Vertex) obj).getID()) {
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
