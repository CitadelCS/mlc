package mlc.components;

public class Vertex {
	private boolean selected, root, leaf, finalized;
	private int degree = 0;
	private final int id, depth;
	
	public Vertex(int d, int id) {
		depth = d;
		root = false;
		leaf = false;
		finalized = false;
		this.id = id;
	}
	
	public boolean getFinalized() {
		return finalized;
	}
	
	public boolean getLeaf() {
		return leaf;
	}
	
	public boolean getRoot() {
		return root;
	}
	
	public int getDegree() {
		return degree;
	}
	
	public int getID() {
		return id;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void incDegree() {
		degree++;
	}
	
	public void setSelected() {
		selected = !selected;
	}
	
	public void setLeaf() {
		leaf = !leaf;
	}
	
	public void setRoot() {
		root = !root;
	}
	
	public void setFinalized() {
		finalized = !finalized;
	}
	
	public String toString() {
		return("ID: " + id + ", Depth: " + depth + ", Degree:" + degree);
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
