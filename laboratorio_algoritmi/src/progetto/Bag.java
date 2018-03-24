package progetto;

import java.util.ArrayList;
import java.util.Iterator;

public class Bag<Item> implements Iterable<Item> {
	ArrayList<Item> b;

	public Bag() {
		this.b = new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	public void remove(Item item) {
		Iterator i = this.iterator();
		while (i.hasNext()) {
			@SuppressWarnings("unchecked")
			Item tmp = (Item) i.next();
			if (tmp.equals(item))
				i.remove();
		}
	}

	public void clear() {
		this.b.clear();
	}

	public boolean isEmpty() {
		return this.b.isEmpty();
	}

	public int size() {
		return this.b.size();
	}

	public void add(Item item) {
		this.b.add(item);
	}

	public Iterator<Item> iterator() {
		return this.b.iterator();
	}

	public boolean contains(Item i) {
		return this.b.contains(i);
	}

	public String toString() {
		return this.b.toString();
	}
}
