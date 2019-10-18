package mlc.components;

import java.util.Arrays;

public class Edge {

	private Vertex[] vertices = new Vertex[2];
	private int weight, id, selected;
	private boolean finalized;
	private final int depth;

	public Edge(int w, int i, Vertex u, Vertex v) {

		vertices[0] = u;
		vertices[1] = v;
		weight = w;
		id = i;
		if (u.getDepth() == 1 && v.getDepth() == 1) {
			depth = 1;
			// Both vertices have a depth of 1, so the edge has a depth of 1.
		} else if ((u.getDepth() == 2 && v.getDepth() == 2) || (u.getDepth() == 3 || v.getDepth() == 3)) {
			depth = 3;
			// Both vertices have a depth of 2 or at least one has a depth of 3, then the
			// edge has a depth of 3.
		} else {
			depth = 2;
			// All other cases can only be an edge with a depth of 2.
		}

	}

	public int getID() {
		return id;
	}

	public int getSelected() {
		return selected;
	}

	public boolean getFinalized() {
		return finalized;
	}

	public int getDepth() {
		return depth;
	}

	public int getWeight() {
		return weight;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setSelected() {
		if (selected == 0) {
			selected = 1;
			if (vertices[0].getSelected() == false) {
				vertices[0].setSelected();
			}
			if (vertices[1].getSelected() == false) {
				vertices[1].getSelected();
			}
		} else {
			selected = 0;
			if (vertices[0].getSelected() == true) {
				vertices[0].setSelected();
			}
			if (vertices[1].getSelected() == true) {
				vertices[1].setSelected();
			}
		}
	}

	public void setFinalized() {
		finalized = !finalized;
	}

	public Edge copy() {
		return new Edge(weight, id, vertices[0], vertices[1]);
	}

	public String toString() {
		return ("ID:" + id + ", Weight: " + weight + ", Depth: " + depth + ", Selected: " + selected + ", Finalized: "
				+ finalized);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + (finalized ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + selected;
		result = prime * result + Arrays.hashCode(vertices);
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (depth != other.depth)
			return false;
		if (finalized != other.finalized)
			return false;
		if (id != other.id)
			return false;
		if (selected != other.selected)
			return false;
		if (!Arrays.equals(vertices, other.vertices))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}


}
