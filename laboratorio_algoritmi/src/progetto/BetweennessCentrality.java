package progetto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class MyInteger {
	int value;

	MyInteger() {
		this.value = 1;
	}

	public void inc() {
		this.value++;
	}
}

public class BetweennessCentrality {
	HashMap<Integer, HashMap<Integer, HashMap<Integer, MyInteger>>> sigmaSTV;
	/**
	 * Grafo su cui calcolare la misura.
	 */
	private EdgeWeightedDigraph G;
	/**
	 * Punteggi. scores[i] = punteggio vertice i-esimo.
	 */
	private double[] scores;
	/**
	 * I percorsi minimi calcolati. key = vertice di partenza. value = percorsi
	 * minimi dal vertice di partenza verso ogni altro vertice.
	 */
	private HashMap<Integer, ArrayList<ArrayList<Integer>>> shortestPaths;
	/**
	 * Di supporto per sigmaST(s, t)
	 */
	private HashSet<Integer> containedVertices;

	/**
	 * Classe per la misura di betweeness centrality.
	 * 
	 * @param InputGraph
	 *            grafo di input.
	 */
	public BetweennessCentrality(EdgeWeightedDigraph InputGraph) {
		this.G = new EdgeWeightedDigraph(InputGraph.V() + 1);
		for (DirectedEdge e : InputGraph.edges())
			this.G.addEdge(e);
		this.scores = new double[G.V()];
		this.shortestPaths = new HashMap<>(32);
		this.containedVertices = new HashSet<>(32);
		int V = this.G.V();
		this.sigmaSTV = new HashMap<Integer, HashMap<Integer, HashMap<Integer, MyInteger>>>(V);
		for (int i = 0; i < V; i++)
			this.sigmaSTV.put(i, new HashMap<>(V));
		Stopwatch BellmanFordSW = new Stopwatch();
		int q = V - 1;
		/*
		 * Aggiungo archi q --> v, di peso 0 per ogni v != q in G.
		 */
		for (int i = 0; i < q; i++)
			G.addEdge(new DirectedEdge(q, i, 0));
		BellmanFordSP bfsp = new BellmanFordSP(this.G, q);
		System.out.printf("BellmanFordSP completato in %f secondi.\n", BellmanFordSW.elapsedTime());
		if (bfsp.hasNegativeCycle()) {
			System.out.println("Rilevato ciclo negativo!");
			for (DirectedEdge e : bfsp.negativeCycle())
				StdOut.println(e);
		} else {
			/*
			 * Elimino gli archi che partono da q.
			 */
			G.getAdg()[q].clear();
			/*
			 * Ripeso gli archi nel grafo.
			 */
			for (DirectedEdge de : this.G.edges())
				de.setWeight(de.weight() + bfsp.distTo(de.from()) - bfsp.distTo(de.to()));
			/*
			 * Applico Dijkstra per ogni coppia di vertici in G.
			 */
			Stopwatch DijkstraAllPairsSW = new Stopwatch();
			DijkstraAllPairsSP dsp = new DijkstraAllPairsSP(this.G);
			/*
			 * Recupero i percorsi minimi da ogni nodo source. duplicatePaths.get(i) ritorna
			 * un ArrayList<ArrayList<Integer>> che rappresenta tutti gli shortest part che
			 * partono dal vertice i.
			 */
			for (DijkstraSP singleSourceDij : dsp.getAll())
				this.shortestPaths.put(singleSourceDij.sourceNode, singleSourceDij.getAllShortestPaths());
			System.out.printf("DijkstraAllPairs completato in %f secondi.\n", DijkstraAllPairsSW.elapsedTime());
			Stopwatch JohnsonSW = new Stopwatch();
			for (int s = 0; s < V; s++) {
				for (int t = 0; t < V; t++) {
					if (s != t) {
						/*
						 * Calcolo il numero di shortest paths tra s e t, e segno in containedVertices
						 * quali vertici sono contenuti da questi percorsi.
						 */
						int _s = sigmaST(s, t);
						if (_s != 0)
							for (int v : this.containedVertices)
								/*
								 * Se ce ne sono, per ogni v nel path da s a t, calcola il relativo punteggio.
								 */
								this.scores[v] += (float) sigmaSTV(s, t, v) / _s;
					}
				}
				this.sigmaSTV.get(s).clear();
			}
			System.out.printf("Johnson completato in %f secondi.\n", JohnsonSW.elapsedTime());
		}
	}

	/**
	 * Questo metodo conta il numero di shortest paths tra s e t. Inoltre, memorizza
	 * i vertici incontrati (diversi da s e t) in tempV
	 * 
	 * @param s
	 *            vertice di partenza
	 * @param t
	 *            vertice di arrivo
	 * @return il numero di shortest paths tra s e t.
	 */
	public int sigmaST(int s, int t) {
		this.containedVertices.clear();
		int counter = 0;
		/*
		 * Per ogni path che parte da s: se t è l'ultimo nodo, aumenta il contatore e
		 * segnati i vertici coinvolti (quelli diversi da s e t).
		 */
		for (ArrayList<Integer> path : this.shortestPaths.get(s))
			if (path.get(path.size() - 1) == t) {
				counter++;
				for (int i = 1; i < path.size() - 1; i++) {
					int v = path.get(i);
					this.containedVertices.add(v);
					HashMap<Integer, MyInteger> st = this.sigmaSTV.get(s).get(t);
					if (st == null) {
						this.sigmaSTV.get(s).put(t, new HashMap<Integer, MyInteger>());
						this.sigmaSTV.get(s).get(t).put(v, new MyInteger());
					} else {
						MyInteger stv = st.get(v);
						if (stv == null)
							st.put(v, new MyInteger());
						else
							stv.inc();
					}
				}
			}
		return counter;
	}

	/**
	 * Questo metodo ritorna il numero di shortest paths tra s e t, passanti per v.
	 * 
	 * @param s
	 *            vertice di partenza
	 * @param t
	 *            vertice di arrivo
	 * @param v
	 * 
	 * @return il numero di shortest paths tra s e t, passanti per v.
	 */
	public int sigmaSTV(int s, int t, int v) {
		return this.sigmaSTV.get(s).get(t).get(v).value;
	}

	public double[] getScores() {
		return scores;
	}

	public static void main(String[] args) {
		/*
		 * Leggo grafo da "graph.txt" e lo passo al costruttore della classe.
		 */
		In file = new In("graph.txt");
		EdgeWeightedDigraph G = new EdgeWeightedDigraph(file);
		Stopwatch sw = new Stopwatch();
		BetweennessCentrality bc = new BetweennessCentrality(G);
		double time = sw.elapsedTime();
		/*
		 * Ottengo i punteggi, li normalizzo e li stampo.
		 */
		double[] scores = bc.getScores();
		double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
		for (double d : scores) {
			if (d > max)
				max = d;
			if (d < min)
				min = d;
		}
		System.out.printf("Elapsed time: %f\n", time);
		for (int i = 0; i < scores.length; i++)
			System.out.printf("V: %05d\tScore: %15.5f\t(%.5f)\n", i, scores[i], (scores[i] - min) / (max - min));
	}
}
