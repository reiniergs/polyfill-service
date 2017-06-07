package org.polyfill.api.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Graph object to do topological sort.
 */
public class TSort {

    private List<Relation> T;

    public TSort() {
        T = new ArrayList<>();
    }

    public void addRelation(String from, String to) {
        T.add(new Relation(from, to));
    }

    public List<String> sort() {
        List<String> sortedList = new ArrayList<>();

        // find nodes with no incoming edges
        List<String> S = new ArrayList<>();
        for (Relation rel: T) {
            if (!S.contains(rel.from)) S.add(rel.from);
        }

        for (Relation rel: T) {
            int index = S.indexOf(rel.to);
            if (index != -1) S.remove(index);
        }

        // while S is non-empty
        while (S.size() > 0) {
            String n = S.get(0);
            S.remove(0);
            sortedList.add(n);

            Iterator<Relation> iter = T.iterator();
            while (iter.hasNext()) {
                Relation rel = iter.next();

                if (rel.to == null) {
                    iter.remove();
                    continue;
                }

                // if node m with an edge e from n to m
                if (n.equals(rel.from)) {
                    String m = rel.to;
                    // remove edge e from the graph
                    iter.remove();

                    // search nodes has no other incoming edges
                    ArrayList<Relation> outputNodes = new ArrayList<Relation>();
                    for (Relation relB: T) {
                        if (m.equals(relB.to)) outputNodes.add(relB);
                    }

                    // if m has no other incoming edges
                    // insert m into S
                    if (outputNodes.size() == 0) S.add(m);
                }
            }
        }

        // if graph has edges
        if (T.size() > 0) {
            System.err.println("This graph has cycle. Aborted.");
        }

        return sortedList;
    }

    public void clear() {
        T = new ArrayList<>();
    }

    private class Relation {
        final String from;
        final String to;

        Relation(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
