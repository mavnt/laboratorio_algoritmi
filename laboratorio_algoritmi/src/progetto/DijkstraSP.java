package progetto;

import java.util.ArrayList;

/**
 * Classe per l'algoritmo di Dijkstra.
 */
public class DijkstraSP {
	private IndexMinPQ<Double> pq;
	/**
	 * distTo[i] = distanza minima per arrivare al vertice i-esimo.
	 */
	private double[] distTo;
	/**
	 * prevEdges[i] è un ArrayList<Integer> che contiene tutti i possibili archi che
	 * portano al nodo i-esimo in uno shortest path.
	 */
	public ArrayList<Integer>[] prevEdges;
	/**
	 * Nodo di partenza
	 */
	public int sourceNode;
	/**
	 * Di supporto al metodo getShortestPaths.
	 */
	ArrayList<ArrayList<Integer>> tempPathList;
	/**
	 * Tutti i percorsi da source a ogni v in G, v != source.
	 */
	ArrayList<ArrayList<Integer>> allShortestPaths;

	@SuppressWarnings("unchecked")
	DijkstraSP(EdgeWeightedDigraph G, int s) {
		this.prevEdges = (ArrayList<Integer>[]) new ArrayList[G.V()];
		for (int i = 0; i < G.V(); i++)
			this.prevEdges[i] = new ArrayList<>();
		this.sourceNode = s;
		this.allShortestPaths = new ArrayList<>(32);
		this.tempPathList = new ArrayList<>(32);
		for (DirectedEdge e : G.edges())
			if (e.weight() < 0)
				throw new IllegalArgumentException("edge " + e + " has negative weight");
		this.distTo = new double[G.V()];
		for (int v = 0; v < G.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;
		this.prevEdges[s] = null;
		pq = new IndexMinPQ<Double>(G.V());
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (DirectedEdge e : G.adj(v))
				relax(e);
		}
		for (int v = 0; v < G.V(); v++)
			if (v != s) {
				getAllShortestPathsTo(v);
				if (this.distTo[v] < Double.POSITIVE_INFINITY)
					allShortestPaths.addAll(tempPathList);
			}
	}

	/**
	 * Metodo di supporto.
	 * 
	 * @param dest
	 */
	private void getAllShortestPathsTo(int dest) {
		this.tempPathList.clear();
		getShortestPaths(new ArrayList<Integer>(), dest);
	}

	private ArrayList<Integer> getShortestPaths(ArrayList<Integer> shortestPath, int dest) {
		ArrayList<Integer> prev = prevEdges[dest];
		/*
		 * Se prev == null, sono arrivato a source. Aggiungi il nodo corrente e salva il
		 * percorso.
		 */
		if (prev == null) {
			shortestPath.add(0, dest);
			tempPathList.add(shortestPath);
		} else {
			/*
			 * Altrimenti, aggiungi il nodo corrent al path e, per ogni arco in prev procedi
			 * ricorsivamente.
			 */
			ArrayList<Integer> updatedPath = new ArrayList<>(shortestPath);
			updatedPath.add(0, dest);
			for (int p : prev)
				getShortestPaths(new ArrayList<>(updatedPath), p);
		}
		return shortestPath;
	}

	/**
	 * Rilassa l'arco e. La distanza di arrivo a un vertice è minore di una già
	 * calcolata? Salvala e ricordati l'arco che usi.
	 * 
	 * La distanza è uguale? aggiungi l'arco alla lista.
	 * 
	 * @param e
	 *            l'arco da rilassare
	 */
	private void relax(DirectedEdge e) {
		int v = e.from(), w = e.to();
		if (distTo[w] > distTo[v] + e.weight()) {
			distTo[w] = distTo[v] + e.weight();
			this.prevEdges[w].clear();
			this.prevEdges[w].add(e.from());
			if (pq.contains(w))
				pq.decreaseKey(w, distTo[w]);
			else
				pq.insert(w, distTo[w]);
		} else if (distTo[w] == distTo[v] + e.weight())
			this.prevEdges[w].add(e.from());
	}

	/**
	 * Getter per allShortestPaths.
	 * 
	 * @return tutti gli shortest path che partono da source.
	 */
	public ArrayList<ArrayList<Integer>> getAllShortestPaths() {
		return this.allShortestPaths;
	}
}
