package uy.edu.um.tad.queue;

import uy.edu.um.tad.list.MyLinkedListImpl;

public class MyQueueImpl<T> extends MyLinkedListImpl<T> implements MyQueue<T> {
    @Override
    public void enqueue(T value) {
        addToBeginning(value);
    }

    @Override
    public T dequeue() throws EmptyQueueException {
        if (isEmpty()) { // si la queue esta vacia, se lanza la excepción
            throw new EmptyQueueException();
        }
        return removeLast();
    }
}
