package mlc.components;

import java.util.ArrayList;
import java.util.HashSet;

public class Region {

	private ArrayList<Edge> edges = new ArrayList<>();
	private HashSet<Vertex> vertices = new HashSet<Vertex>();
	private boolean covered;
	private final boolean outer;

	public Region(ArrayList<Edge> e) {
		edges.addAll(e);
		boolean t = false;
		for (Edge temp : edges) {
			vertices.add(temp.getVertices()[0]);
			vertices.add(temp.getVertices()[1]);
			if (temp.getDepth() == 3 || temp.getDepth() == 2) {
				t = true;
			}
		}
		outer = t;
	}

	public void setCovered() {
		covered = !(covered);
	}

	public boolean getOuter() {
		return outer;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public HashSet<Vertex> getVertices() {
		return vertices;
	}

	public boolean covered() {
		return covered;
	}

}
