package progetto;

public class EdgeWeightedDirectedCycle {
	private boolean[] marked; // marked[v] = has vertex v been marked?
	private DirectedEdge[] edgeTo; // edgeTo[v] = previous edge on path to v
	private boolean[] onStack; // onStack[v] = is vertex on the stack?
	private Stack<DirectedEdge> cycle; // directed cycle (or null if no such
										// cycle)

	public EdgeWeightedDirectedCycle(EdgeWeightedDigraph G) {
		marked = new boolean[G.V()];
		onStack = new boolean[G.V()];
		edgeTo = new DirectedEdge[G.V()];
		for (int v = 0; v < G.V(); v++)
			if (!marked[v])
				dfs(G, v);
		// check that digraph has a cycle
		assert check();
	}

	// check that algorithm computes either the topological order or finds a
	// directed cycle
	private void dfs(EdgeWeightedDigraph G, int v) {
		onStack[v] = true;
		marked[v] = true;
		for (DirectedEdge e : G.adj(v)) {
			int w = e.to();
			// short circuit if directed cycle found
			if (cycle != null)
				return;
			// found new vertex, so recur
			else if (!marked[w]) {
				edgeTo[w] = e;
				dfs(G, w);
			}
			// trace back directed cycle
			else if (onStack[w]) {
				cycle = new Stack<DirectedEdge>();
				DirectedEdge f = e;
				while (f.from() != w) {
					cycle.push(f);
					f = edgeTo[f.from()];
				}
				cycle.push(f);
				return;
			}
		}
		onStack[v] = false;
	}

	public boolean hasCycle() {
		return cycle != null;
	}

	public Iterable<DirectedEdge> cycle() {
		return cycle;
	}

	// certify that digraph is either acyclic or has a directed cycle
	private boolean check() {
		// edge-weighted digraph is cyclic
		if (hasCycle()) {
			// verify cycle
			DirectedEdge first = null, last = null;
			for (DirectedEdge e : cycle()) {
				if (first == null)
					first = e;
				if (last != null) {
					if (last.to() != e.from()) {
						System.err.printf("cycle edges %s and %s not incident\n", last, e);
						return false;
					}
				}
				last = e;
			}
			if (last.to() != first.from()) {
				System.err.printf("cycle edges %s and %s not incident\n", last, first);
				return false;
			}
		}
		return true;
	}
}
