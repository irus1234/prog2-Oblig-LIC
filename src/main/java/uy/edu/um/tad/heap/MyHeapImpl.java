package uy.edu.um.tad.heap;

public class MyHeapImpl<T extends Comparable<T>> implements MyHeap<T> {

    private static final int DEFAULT_CAPACITY = 255;

    private int size;
    private T[] heap;
    private boolean isHeapMin;

    public MyHeapImpl(boolean isHeapMin, T[] values) {
        this.size = 0;
        this.isHeapMin = isHeapMin;
        this.heap = values;
    }

    public MyHeapImpl(int initialCapacity, boolean isHeapMin) {
        this(isHeapMin, (T[]) new Comparable[initialCapacity]);
    }

    public MyHeapImpl(boolean isHeapMin) {
        this(DEFAULT_CAPACITY, isHeapMin);
    }

    public MyHeapImpl(int initialCapacity) {
        this(initialCapacity, true);
    }

    public MyHeapImpl() {
        this(DEFAULT_CAPACITY);
    }

    public MyHeapImpl(T[] values) {
        this(values, true);
        this.size = values.length;
        buildHeap();
    }

    public MyHeapImpl(T[] values, boolean isHeapMin) {
        this(isHeapMin, values);
        this.size = values.length;
        buildHeap();
    }

    @Override
    public void insert(T element) {
        if (element == null)
            return;
        if (size == heap.length)
            resize();
        heap[size] = element;
        percolateUp(size);
        size++;
    }

    @Override
    public T remove() throws EmptyHeapException {
        if (size == 0)
            throw new EmptyHeapException();
        T root = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0)
            percolateDown(0);
        return root;
    }

    @Override
    public T get() throws EmptyHeapException {
        if (size == 0)
            throw new EmptyHeapException();
        return heap[0];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void buildHeap() {
        for (int i = parent(size - 1); i >= 0; i--) {
            percolateDown(i);
        }
    }

    private void percolateUp(int index) {
        int current = index;
        int parent = parent(current);
        while (current > 0 && (hasHigherPriority(heap[current], heap[parent]))) {
            swap(current, parent);
            current = parent;
            parent = parent(current);
        }
    }

    private void percolateDown(int index) {
        int current = index;
        while (leftChild(current) < size) {
            int left = leftChild(current);
            int right = rightChild(current);
            int candidate = left;
            if (right < size && hasHigherPriority(heap[right], heap[left])) {
                candidate = right;
            }
            if (hasHigherPriority(heap[candidate], heap[current])) {
                swap(current, candidate);
                current = candidate;
            } else {
                break;
            }
        }
    }

    private boolean hasHigherPriority(T a, T b) {
        return isHeapMin ? a.compareTo(b) < 0 : a.compareTo(b) > 0;
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return (2 * index) + 1;
    }

    private int rightChild(int index) {
        return (2 * index) + 2;
    }

    private void swap(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        T[] newHeap = (T[]) new Comparable[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}