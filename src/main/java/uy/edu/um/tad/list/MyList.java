package uy.edu.um.tad.list;

public interface MyList<T> {

    void add(T value);
    T get(int position) throws IndexOutOfBoundsException;
    boolean contains(T value);
    void remove(T value);
    T remove (int index) throws IndexOutOfBoundsException;
    int size();
    Node<T> getFirst();
    boolean isEmpty();
    int indexOf(T value);
}
