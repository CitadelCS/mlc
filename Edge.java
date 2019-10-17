package mlc.components;

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
		if(u.getDepth() == 1 && v.getDepth() == 1) {
			depth = 1;
			//Both vertices are depth making e an depth edge.
		}
		else if(u.getDepth() == 2 || v.getDepth() == 2) {
			depth = 2;
			//Vertices are both inner and depth making e an inner-depth edge.
		}
		else {
			depth = 3;
			//Both vertices are inner making e an inner edge.
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
	
	public Vertex[] getVertices(){
		return vertices;
	}
	
	public void setSelected() {
		if(selected == 0) {
			selected = 1;
			if(vertices[0].getSelected() == false) {
				vertices[0].setSelected();
			}
			if(vertices[1].getSelected() == false) {
				vertices[1].getSelected();
			}
		}
		else {
			selected = 0;
			if(vertices[0].getSelected() == true) {
				vertices[0].setSelected();
			}
			if(vertices[1].getSelected() == true) {
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
		return("ID:" + id + ", Weight: " + weight + ", Depth: " + depth + ", Selected: " + selected + ", Finalized: " + finalized);
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
