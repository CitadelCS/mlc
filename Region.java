package mlc.components;

import java.util.ArrayList;
import java.util.HashSet;

public class Region {
	
	private HashSet<Edge> edges = new HashSet<>();
	private HashSet<Vertex> vertices = new HashSet<Vertex>();
	private boolean covered;
	private final boolean outer;
	
	public Region(ArrayList<Edge> e) {
		edges.addAll(e);
		boolean t = false;
		for(Edge temp: edges) {
			vertices.add(temp.getVertices()[0]);
			vertices.add(temp.getVertices()[1]);
			if(temp.getOuter() == 1 || temp.getOuter() == 2) {
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
	
	public HashSet<Edge> getEdges(){
		return edges;
	}
	
	public HashSet<Vertex> getVertices(){
		return vertices;
	}
	
	public boolean covered() {
		return covered;
	}
	
}
