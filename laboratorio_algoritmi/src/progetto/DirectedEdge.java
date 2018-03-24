package progetto;

public class DirectedEdge {
	private final int v;
	private final int w;
	private double weight;

	public DirectedEdge(int v, int w, double weight) {
		if (v < 0)
			throw new IllegalArgumentException("Vertex names must be nonnegative integers");
		if (w < 0)
			throw new IllegalArgumentException("Vertex names must be nonnegative integers");
		if (Double.isNaN(weight))
			throw new IllegalArgumentException("Weight is NaN");
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	public int from() {
		return v;
	}

	public int to() {
		return w;
	}

	public double weight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String toString() {
		return this.from() + "->" + this.to() + "(" + this.weight + ")";
	}

	@Override
	public boolean equals(Object obj) {
		DirectedEdge de = (DirectedEdge) obj;
		if (de.from() == this.from() && de.to() == this.to() && de.weight == this.weight)
			return true;
		return false;
	}
}
