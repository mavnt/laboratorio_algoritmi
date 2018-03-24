package progetto;

import java.util.ArrayList;
import java.util.Iterator;

public class EdgeWeightedDigraph {
	private static final String NEWLINE = System.getProperty("line.separator");
	private final int V; // number of vertices in this digraph
	private int E; // number of edges in this digraph
	private Bag<DirectedEdge>[] adj; // adj[v] = adjacency list for vertex v
	private int[] indegree; // indegree[v] = indegree of vertex v

	@SuppressWarnings("unchecked")
	public EdgeWeightedDigraph(int V) {
		if (V < 0)
			throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
		this.V = V;
		this.E = 0;
		this.indegree = new int[V];
		adj = (Bag<DirectedEdge>[]) new Bag[V];
		for (int v = 0; v < V; v++)
			adj[v] = new Bag<DirectedEdge>();
	}

	public Bag<DirectedEdge>[] getAdg() {
		return this.adj;
	}

	public EdgeWeightedDigraph(int V, int E) {
		this(V);
		if (E < 0)
			throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = StdRandom.uniform(V);
			int w = StdRandom.uniform(V);
			double weight = 0.01 * StdRandom.uniform(100);
			DirectedEdge e = new DirectedEdge(v, w, weight);
			addEdge(e);
		}
	}

	public EdgeWeightedDigraph(In in) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException("Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			validateVertex(v);
			validateVertex(w);
			double weight = in.readDouble();
			addEdge(new DirectedEdge(v, w, weight));
		}
	}

	public EdgeWeightedDigraph(EdgeWeightedDigraph G) {
		this(G.V());
		this.E = G.E();
		for (int v = 0; v < G.V(); v++)
			this.indegree[v] = G.indegree(v);
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<DirectedEdge> reverse = new Stack<DirectedEdge>();
			for (DirectedEdge e : G.adj[v]) {
				reverse.push(e);
			}
			for (DirectedEdge e : reverse) {
				adj[v].add(e);
			}
		}
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
	}

	public void addEdge(DirectedEdge e) {
		int v = e.from();
		int w = e.to();
		validateVertex(v);
		validateVertex(w);
		adj[v].add(e);
		indegree[w]++;
		E++;
	}

	public void deleteEdge(DirectedEdge e) {
		Iterator<DirectedEdge> i = this.edges().iterator();
		while (i.hasNext()) {
			DirectedEdge de = (DirectedEdge) i.next();
			if (de.equals(e)) {
				this.adj[e.from()].remove(e);
				i.remove();
			}
		}
	}

	public Iterable<DirectedEdge> adj(int v) {
		validateVertex(v);
		return adj[v];
	}

	public int outdegree(int v) {
		validateVertex(v);
		return adj[v].size();
	}

	public int indegree(int v) {
		validateVertex(v);
		return indegree[v];
	}

	public Iterable<DirectedEdge> edges() {
		Bag<DirectedEdge> list = new Bag<DirectedEdge>();
		for (int v = 0; v < V; v++) {
			for (DirectedEdge e : adj(v)) {
				list.add(e);
			}
		}
		return list;
	}

	public ArrayList<DirectedEdge> edgesAsArray() {
		ArrayList<DirectedEdge> tmp = new ArrayList<>();
		for (DirectedEdge de : this.edges())
			tmp.add(de);
		return tmp;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " " + E + NEWLINE);
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (DirectedEdge e : adj[v]) {
				s.append(e + "  ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	public String toStringGraphviz() {
		StringBuilder sb = new StringBuilder("digraph{");
		for (int i = 0; i < this.V; i++) {
			// sb.append("\"" + i + "\"");
			for (DirectedEdge de : this.adj[i]) {
				sb.append(de.from() + "->" + de.to() + "[label=\"" + de.weight() + "\"]\n");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public String toFile() {
		StringBuilder sb = new StringBuilder("");
		sb.append(this.V() + "\n");
		sb.append(this.E() + "\n");
		for (int i = 0; i < this.V; i++) {
			for (DirectedEdge de : this.adj[i]) {
				sb.append(de.from() + " " + de.to() + " " + de.weight() + "\n");
			}
		}
		return sb.toString();
	}
}
