package mlc.components;

import java.util.ArrayList;

public class Region {
	
	private ArrayList<Edge> edges = new ArrayList<>();
	private ArrayList<Vertex> vertices = new ArrayList<>();
	private boolean covered;
	private final boolean outer;
	
	public Region(ArrayList<Edge> e) {
		
		edges = e;
		vertices = v;
		outer = o;
		
	}
	
	public void setCovered() {
		covered = !(covered);
	}
	
	public boolean outer() {
		return outer;
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
	
	public ArrayList<Vertex> getVertices(){
		return vertices;
	}
	
	public boolean covered() {
		return covered;
	}
	
}
