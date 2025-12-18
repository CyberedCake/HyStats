package net.cybercake.hystats.utils;

import java.util.Map;

public class Pair<A, B> {

    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first() { return this.first; }
    public B second() { return this.second; }

    public boolean equal() {
        return this.first.equals(this.second);
    }

    @Override
    public String toString() {
        return "Pair[first=" + this.first + ", second=" + this.second + "]";
    }
}
