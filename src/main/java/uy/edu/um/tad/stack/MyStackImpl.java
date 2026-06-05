package uy.edu.um.tad.stack;

import uy.edu.um.tad.list.MyLinkedListImpl;

public class MyStackImpl<T> extends MyLinkedListImpl<T> implements MyStack<T> {
    @Override
    public void push(T value) {
        addToTheEnd(value);
    }

    @Override
    public T pop() throws EmptyStackException {
        if (isEmpty()) { // si la pila esta vacia
            throw new EmptyStackException();
        }
        return removeLast();
    }

    @Override
    public T peek() {
        return isEmpty() ? null : this.getLast().getValue();
    }
}

