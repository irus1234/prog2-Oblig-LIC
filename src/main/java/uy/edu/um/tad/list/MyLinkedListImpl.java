package uy.edu.um.tad.list;

import lombok.Getter;

public class MyLinkedListImpl<T> implements MyList<T>{

    @Getter
    protected Node<T> first;
    @Getter
    protected Node<T> last;

    public MyLinkedListImpl() {
        this.first = null;
        this.last = null;
    }

    @Override
    public void add(T value) {
        addToTheEnd(value);
    }

    public T get(int position) throws IndexOutOfBoundsException {
        if (position < 0 || position >= size())
            throw new IndexOutOfBoundsException();

        int tempPosition = 0;
        Node<T> temp = this.first;
        // Se busca el nodo que corresponde con la posicion
        while (tempPosition != position) {
            temp = temp.getNext();
            tempPosition++;
        }
        return temp.getValue();
    }

    public boolean contains(T value) {
        Node<T> temp = this.first;
        while (temp != null) {
            if (temp.getValue().equals(value))
                return true; // si se enecuentra el valor del elemento, se devuelve true, cortando del bucle while
            temp = temp.getNext();
        }
        return false;// si se ejecuta todo el bucle while, quiere decir que el elemento no se ha encontrado
    }

    public void remove(T value) {
        if (isEmpty())
            return;

        Node<T> prevNode = this.first;
        Node<T> removedNode = this.first;
        while (removedNode != null && !removedNode.getValue().equals(value)) { // se recorre la lista hata encontrar el dato o hasta llegar al final de la misma
            prevNode = removedNode;
            removedNode = removedNode.getNext();
        }
        if (removedNode == null)// si se llega al final de la lista, entonces no se encontró el valor, por lo tanto se corta el algoritmo
            return;

        if (size() == 1) { // si se encontró el valor y es el único elemento en la lista
            this.first = null;
            this.last = null;
            return;
        }
        prevNode.setNext(removedNode.getNext());
        if (removedNode == this.first)
            this.first = removedNode.getNext();
        if (removedNode == this.last)
            this.last = prevNode;
    }

    @Override
    public T remove(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();
        int size = size();
        if (size == 1) { // se evalua si la lista contiene un solo nodo
            T dato = this.first.getValue();
            this.first = null;
            this.last = null;
            return dato;
        }
        Node<T> prevNode = this.first;
        Node<T> removedNode = this.first;
        if (index > 0) {
            int count = 0;
            while (++count < index) {
                prevNode = prevNode.getNext();
            }
            removedNode = prevNode.getNext();
        }
        prevNode.setNext(removedNode.getNext());
        if (removedNode == this.first)
            this.first = removedNode.getNext();
        if (removedNode == this.last)
            this.last = prevNode;
        return removedNode.getValue();
    }

    public int size() {
        int size = 0;
        Node<T> temp = this.first;
        while (temp != null) {
            temp = temp.getNext();
            size++;
        }
        return size;
    }

    public boolean isEmpty() {
        return (this.first == null && this.last == null);
    }

    @Override
    public int indexOf(T value) {
        Node<T> temp = this.first;
        int index = 0;                                      // arranca en 0
        while (temp != null) {
            if (temp.getValue().equals(value))
                return index;
            index++;
            temp = temp.getNext();
        }
        return -1;
    }

    protected void addToBeginning(T value) {
        if (value == null)//Si el valor es nulo, no se realiza ninguna acción
            return;
        Node<T> elementToAdd = new Node<>(value);
        if (this.first == null) { // si la lista es vacia
            this.first = elementToAdd;
            this.last = elementToAdd;
        } else { // en caso de no ser vacia se agrega al comienzo
            elementToAdd.setNext(this.first);
            this.first = elementToAdd;
        }
    }

    protected void addToTheEnd(T value) {
        if (value == null)
            return;
        Node<T> elementToAdd = new Node<>(value);
        if (this.first == null) { // si la lista es vacia
            this.first = elementToAdd;
            this.last = elementToAdd;
        } else { // en caso de no ser vacia se agrega al final
            this.last.setNext(elementToAdd);
            this.last = elementToAdd;
        }
    }

    protected T removeLast() { // esta operación remueve el último elemento y retorna el elemento eliminado
        T valueToRemove = null;
        if (this.last != null) {
            valueToRemove = this.last.getValue();
            remove(valueToRemove);
        }
        return valueToRemove;
    }
}
