package org.eclipse.recommenders.jayes.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * binary heap supporting the decreaseKey operation
 */
public class BinaryHeap<T> {

    private T[] heap;
    private int size;
    private Comparator<T> comparator;

    Map<T, Integer> positions = new HashMap<T, Integer>();

    @SuppressWarnings("unchecked")
    public BinaryHeap(Collection<T> items, Comparator<T> comparator) {
        this.comparator = comparator;
        heap = (T[]) items.toArray();
        size = heap.length;
        initPositions();
        buildHeap();
    }

    private void initPositions() {
        for (int i = 0; i < heap.length; i++) {
            positions.put(heap[i], i);
        }
    }

    private void buildHeap() {
        if (heap.length == 0)
            return;
        for (int i = heap.length / 2 - 1; i >= 0; i--) {
            heapify(i);
        }
    }

    private void heapify(int i) {
        int x = i;
        if (left(i) < size && lessThan(left(i), x))
            x = left(i);
        if (right(i) < size && lessThan(right(i), x))
            x = right(i);
        if (x != i) {
            swap(i, x);
            heapify(x);
        }

    }

    private boolean lessThan(int i, int x) {
        return comparator.compare(heap[i], heap[x]) < 0;
    }

    private void swap(int i, int x) {
        T temp = heap[i];
        heap[i] = heap[x];
        heap[x] = temp;
        positions.put(heap[i], i);
        positions.put(heap[x], x);

    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    public T extractMin() {
        return remove(0);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void decreaseKey(T item) {
        decreaseKey(positions.get(item));
    }

    private void decreaseKey(int i) {
        while (i > 0 && lessThan(i, parent(i))) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public T remove(T item) {
        return remove(positions.get(item));
    }

    public T remove(int i) {
        T temp = heap[i];
        int l = size - 1;
        swap(l, i);
        size = l;
        heapify(i);
        positions.remove(temp);
        return temp;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }
}