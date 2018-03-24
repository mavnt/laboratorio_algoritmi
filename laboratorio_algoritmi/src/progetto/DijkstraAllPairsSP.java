package progetto;

public class DijkstraAllPairsSP {
	private DijkstraSP[] all;

	public DijkstraAllPairsSP(EdgeWeightedDigraph G) {
		all = new DijkstraSP[G.V()];
		for (int v = 0; v < G.V(); v++)
			all[v] = new DijkstraSP(G, v);
	}

	public DijkstraSP[] getAll() {
		return all;
	}
}
