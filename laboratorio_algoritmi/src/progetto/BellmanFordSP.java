package progetto;

public class BellmanFordSP {
	private double[] distTo; // distTo[v] = distance of shortest s->v path
	private DirectedEdge[] edgeTo; // edgeTo[v] = last edge on shortest s->v
									// path
	private boolean[] onQueue; // onQueue[v] = is v currently on the queue?
	private Queue<Integer> queue; // queue of vertices to relax
	private int cost; // number of calls to relax()
	private Iterable<DirectedEdge> cycle; // negative cycle (or null if no such
											// cycle)

	public BellmanFordSP(EdgeWeightedDigraph G, int s) {
		distTo = new double[G.V()];
		edgeTo = new DirectedEdge[G.V()];
		onQueue = new boolean[G.V()];
		for (int v = 0; v < G.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;

		// Bellman-Ford algorithm
		queue = new Queue<Integer>();
		queue.enqueue(s);
		onQueue[s] = true;
		while (!queue.isEmpty() && !hasNegativeCycle()) {
			int v = queue.dequeue();
			onQueue[v] = false;
			relax(G, v);
		}

		assert check(G, s);
	}

	// relax vertex v and put other endpoints on queue if changed
	private void relax(EdgeWeightedDigraph G, int v) {
		for (DirectedEdge e : G.adj(v)) {
			int w = e.to();
			if (distTo[w] > distTo[v] + e.weight()) {
				distTo[w] = distTo[v] + e.weight();
				edgeTo[w] = e;
				if (!onQueue[w]) {
					queue.enqueue(w);
					onQueue[w] = true;
				}
			}
			if (cost++ % G.V() == 0) {
				findNegativeCycle();
				if (hasNegativeCycle())
					return; // found a negative cycle
			}
		}
	}

	public boolean hasNegativeCycle() {
		return cycle != null;
	}

	public Iterable<DirectedEdge> negativeCycle() {
		return cycle;
	}

	// by finding a cycle in predecessor graph
	private void findNegativeCycle() {
		int V = edgeTo.length;
		EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
		for (int v = 0; v < V; v++)
			if (edgeTo[v] != null)
				spt.addEdge(edgeTo[v]);

		EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
		cycle = finder.cycle();
	}

	public double distTo(int v) {
		validateVertex(v);
		if (hasNegativeCycle())
			throw new UnsupportedOperationException("Negative cost cycle exists");
		return distTo[v];
	}

	public boolean hasPathTo(int v) {
		validateVertex(v);
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	public Iterable<DirectedEdge> pathTo(int v) {
		validateVertex(v);
		if (hasNegativeCycle())
			throw new UnsupportedOperationException("Negative cost cycle exists");
		if (!hasPathTo(v))
			return null;
		Stack<DirectedEdge> path = new Stack<DirectedEdge>();
		for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
			path.push(e);
		}
		return path;
	}

	// check optimality conditions: either
	// (i) there exists a negative cycle reacheable from s
	// or
	// (ii) for all edges e = v->w: distTo[w] <= distTo[v] + e.weight()
	// (ii') for all edges e = v->w on the SPT: distTo[w] == distTo[v] +
	// e.weight()
	private boolean check(EdgeWeightedDigraph G, int s) {

		// has a negative cycle
		if (hasNegativeCycle()) {
			double weight = 0.0;
			for (DirectedEdge e : negativeCycle()) {
				weight += e.weight();
			}
			if (weight >= 0.0) {
				System.err.println("error: weight of negative cycle = " + weight);
				return false;
			}
		}

		// no negative cycle reachable from source
		else {

			// check that distTo[v] and edgeTo[v] are consistent
			if (distTo[s] != 0.0 || edgeTo[s] != null) {
				System.err.println("distanceTo[s] and edgeTo[s] inconsistent");
				return false;
			}
			for (int v = 0; v < G.V(); v++) {
				if (v == s)
					continue;
				if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
					System.err.println("distTo[] and edgeTo[] inconsistent");
					return false;
				}
			}

			// check that all edges e = v->w satisfy distTo[w] <= distTo[v] +
			// e.weight()
			for (int v = 0; v < G.V(); v++) {
				for (DirectedEdge e : G.adj(v)) {
					int w = e.to();
					if (distTo[v] + e.weight() < distTo[w]) {
						System.err.println("edge " + e + " not relaxed");
						return false;
					}
				}
			}

			// check that all edges e = v->w on SPT satisfy distTo[w] ==
			// distTo[v] + e.weight()
			for (int w = 0; w < G.V(); w++) {
				if (edgeTo[w] == null)
					continue;
				DirectedEdge e = edgeTo[w];
				int v = e.from();
				if (w != e.to())
					return false;
				if (distTo[v] + e.weight() != distTo[w]) {
					System.err.println("edge " + e + " on shortest path not tight");
					return false;
				}
			}
		}

		StdOut.println("Satisfies optimality conditions");
		StdOut.println();
		return true;
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v) {
		int V = distTo.length;
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
	}

}
