package org.eclipse.recommenders.jayes.inference.junctionTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.recommenders.jayes.util.BinaryHeap;
import org.eclipse.recommenders.jayes.util.Graph.Edge;
import org.eclipse.recommenders.jayes.util.Pair;

public class SetSpanningTreeComputer {

    public List<Pair<Edge, List<Integer>>> computeSpanningTree(List<List<Integer>> sets) {
        Map<Integer, List<List<Integer>>> invertedIndex = buildInvertedIndex(sets);

        List<Integer> first = sets.get(0);
        Set<Integer> allVars = new HashSet<Integer>(first);
        List<Pair<Edge, List<Integer>>> edges = new ArrayList<Pair<Edge, List<Integer>>>();

        final Map<List<Integer>, Integer> distances = initializeDistances(first, sets);
        Comparator<List<Integer>> comparator = new Comparator<List<Integer>>() {

            @Override
            public int compare(List<Integer> arg0, List<Integer> arg1) {
                return -(distances.get(arg0) - distances.get(arg1));
            }
        };
        BinaryHeap<List<Integer>> q = new BinaryHeap<List<Integer>>(sets, comparator);

        q.remove(first);
        Set<List<Integer>> processed = new HashSet<List<Integer>>();
        processed.add(first);

        while (!q.isEmpty()) {
            List<Integer> min = q.extractMin();
            processed.add(min);

            List<Integer> difference = new ArrayList<Integer>(min);
            difference.removeAll(allVars);

            updateDistances(q, difference, invertedIndex, distances, processed);

            List<Integer> intersection = new ArrayList<Integer>(min);
            intersection.retainAll(allVars);

            allVars.addAll(min);
            edges.add(Pair.newPair(makeEdge(findMatching(intersection, processed, min), min, sets), intersection));
        }

        return edges;
    }

    private <T> Edge makeEdge(T a, T b, List<T> list) {
        return new Edge(list.indexOf(a), list.indexOf(b));
    }

    private List<Integer> findMatching(List<Integer> intersection, Set<List<Integer>> processed, List<Integer> min) {
        for (List<Integer> set : processed) {
            if (set.containsAll(intersection) && set != min) {
                return set;
            }
        }
        throw new AssertionError();
    }

    private void updateDistances(BinaryHeap<List<Integer>> q, List<Integer> differences,
            Map<Integer, List<List<Integer>>> invertedIndex, Map<List<Integer>, Integer> distances,
            Set<List<Integer>> processed) {
        for (Integer concernedVar : differences) {
            for (List<Integer> set : invertedIndex.get(concernedVar)) {
                if (processed.contains(set))
                    continue; // TODO update II
                distances.put(set, distances.get(set) + 1);
                q.decreaseKey(set);
            }
        }

    }

    private Map<Integer, List<List<Integer>>> buildInvertedIndex(List<List<Integer>> sets) {
        Map<Integer, List<List<Integer>>> invertedIndex = new HashMap<Integer, List<List<Integer>>>();
        ListIterator<List<Integer>> it = sets.listIterator();
        while (it.hasNext()) {
            List<Integer> set = it.next();
            for (Integer i : set) {
                if (!invertedIndex.containsKey(i)) {
                    invertedIndex.put(i, new ArrayList<List<Integer>>());
                }
                invertedIndex.get(i).add(set);
            }
        }
        return invertedIndex;
    }

    private Map<List<Integer>, Integer> initializeDistances(List<Integer> first, List<List<Integer>> sets) {
        Map<List<Integer>, Integer> distances = new HashMap<List<Integer>, Integer>();

        for (List<Integer> set : sets) {
            List<Integer> setCopy = new ArrayList<Integer>(set);
            setCopy.retainAll(first);
            distances.put(set, setCopy.size());
        }

        return distances;
    }

}
