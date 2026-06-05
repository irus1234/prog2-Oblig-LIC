package uy.edu.um.tad.heap;

public interface MyHeap<T extends Comparable<T>> {

	/**
	 * Elimina el minimo o máximo dependiendo si es un Heap minimo o maximo (Ver constructor parametro isHeapMin)
	 * @return
	 */
	T remove() throws EmptyHeapException;

	/**
	 * Obtiene el minimo o maximo dependiendo si es un heap minimo o maximo
	 * @return
	 */
	T get() throws EmptyHeapException;
	void insert(T element);
	int size();
	boolean isEmpty();
}
