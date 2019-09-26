package mlc.components;

import java.util.ArrayList;

public class Vertex {
	
	private ArrayList<Edge> edges =  new ArrayList<>();
	private ArrayList<Region> regions = new ArrayList<>();
	private boolean selected, root, leaf, finalized;
	private final boolean outer;
	
	public Vertex(boolean o) {
		
		outer = o;
		root = false;
		leaf = false;
		finalized = false;
		
	}
	
	public void setSelected() {
		selected = !selected;
	}
	
	public boolean outer() {
		return outer;
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
	
}
