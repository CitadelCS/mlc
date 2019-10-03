package mlc.components;

import java.util.ArrayList;
import java.util.HashSet;

public class Region {
	
	private ArrayList<Edge> edges = new ArrayList<>();
	private HashSet<Vertex> vertices = new HashSet<Vertex>();
	private boolean covered;
	private final boolean outer;
	
	public Region(ArrayList<Edge> e) {
		edges = e;
		boolean t = false;
		for(Edge temp: edges) {
			vertices.addAll(temp.getVertices());
			if(temp.outer() == 1 || temp.outer() == 2) {
				t = true;
			}
		}
		outer = t;
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
	
	public HashSet<Object> getVertices(){
		return vertices;
	}
	
	public boolean covered() {
		return covered;
	}
	
}
