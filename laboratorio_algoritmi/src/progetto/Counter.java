package progetto;

public class Counter implements Comparable<Counter> {

	private final String name; // counter name
	private int count = 0; // current value

	public Counter(String id) {
		name = id;
	}

	public void increment() {
		count++;
	}

	public int tally() {
		return count;
	}

	public String toString() {
		return count + " " + name;
	}

	@Override
	public int compareTo(Counter that) {
		if (this.count < that.count)
			return -1;
		else if (this.count > that.count)
			return +1;
		else
			return 0;
	}

}
