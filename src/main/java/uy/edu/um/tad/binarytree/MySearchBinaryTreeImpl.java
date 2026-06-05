/**
 *
 */
package uy.edu.um.tad.binarytree;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;

@NoArgsConstructor
public class MySearchBinaryTreeImpl<K extends Comparable<K>, V> implements MySearchBinaryTree<K, V> {
    @Getter
    private TreeNode<K, V> root;

    @Override
    public void add(K key, V value) {
        TreeNode<K, V> elementToAdd = new TreeNode<>(key, value);
        if (root == null) {
            root = elementToAdd;
        } else {
            root.add(key, value);
        }
    }

    public V find(K key) {
        return isEmpty() ? null : this.root.find(key);
    }

    public boolean contains(K key) {
        return isEmpty() ? false : this.root.contains(key);
    }

    @Override
    public void remove(K key) {
        if (root != null) {
            root = root.remove(key);
        }
    }

    @Override
    public MyList<K> inOrder() {
        MyList<K> inOrderTraverse = new MyLinkedListImpl<>();
        if (root != null) {
            root.inOrderTraverse(inOrderTraverse);
        }
        return inOrderTraverse;
    }

    @Override
    public MyList<V> inOrderValues() {
        MyList<V> inorderList = new MyLinkedListImpl<>();
        if (root != null) {
            root.inOrderValues(inorderList);
        }
        return inorderList;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }
}
