package org.ditaa.web;

import java.io.*;

/** Contains two objects, <code>this.first</code> and <code>this.second</code>. */
public class Pair<TA,TB> implements Comparable, Externalizable {
	private TA first = null;
	private TB second = null;

	public Pair() {}

	public Pair(TA first, TB second) {
		this.first = first;
		this.second = second;
	}

	public TA getFirst() { return first; }
	public void setFirst(TA first) { this.first = first; }
	public TB getSecond() { return second; }
	public void setSecond(TB second) { this.second = second; }

    public void set(TA first, TB second) {
        setFirst(first);
        setSecond(second);
    }

	/** If o is a <code>Pair</code>, compare <code>first</code>s and, if they are equal,
	 *  compare <code>second</code>s. If, on the other hand, o is not a Pair, then just
	 *  compare it to <code>first</code>. */
	@SuppressWarnings({"unchecked"})
	public int compareTo(Object o) {
		// first, try to compare o as a Pair
		Pair p;
		try {
			p = (Pair) o;
		}
		// if o is not a pair, compare first to o
		catch (ClassCastException e) {
			return ((Comparable) first).compareTo(o);
		}
		// so, if o is a Pair ...
		int f = ((Comparable) first).compareTo(p.first); // if firsts are equal, ...
		if (f == 0 && second != null && p.second != null)
			return ((Comparable) second).compareTo(p.second); // ... compare seconds
		else
			return f;
	} // otherwise, compare firsts

	/** Chain o to this, linked-list style -- you can think of o as a third element of
	 *  this, for sorting purposes. Specifically, replace second with a new Pair whose
	 *  first is the old second and whose second is o. That way, sorting will still work
	 *  in a "natural" way, where first is compared first, the old second is compared
	 *  second, and o is compared third. */
	@SuppressWarnings({"unchecked"})
	public void chain(TB o) {
		second = (TB) new Pair<TB, TB>(second, o);
	}

	public String toString() {
		return new StringBuffer("(").append(first).append(", ")
		        .append(second).append(")").toString();
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		else if (o == null || !o.getClass().equals(Pair.class)) return false;
		else {
			final Pair<Object,Object> that = (Pair<Object,Object>) o;
			if (first != null ? !first.equals(that.first) : that.first != null) return false;
			else return !(second != null ? !second.equals(that.second) : that.second != null);
		}
	}

	public int hashCode() {
		int result;
		result = (first != null ? first.hashCode() : -1);
		result = 73 * result + (second != null ? second.hashCode() : -1);
		return result;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(1); // version
		if (first instanceof Serializable) out.writeObject(first);
		else out.writeObject(null);
		if (second instanceof Serializable) out.writeObject(second);
		else out.writeObject(null);
	}

	@SuppressWarnings({"unchecked"})
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		in.readInt();
		first = (TA) in.readObject();
		second = (TB) in.readObject();
	}
}
