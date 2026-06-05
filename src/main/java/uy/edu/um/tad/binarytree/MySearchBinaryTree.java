/**
 * 
 */
package uy.edu.um.tad.binarytree;

import uy.edu.um.tad.list.MyList;

public interface MySearchBinaryTree<K extends Comparable<K>, V> {

	void add(K key, V value);
	void remove(K key);
	boolean contains(K key);
	V find(K key);
	MyList<K> inOrder();
	MyList<V> inOrderValues();
	TreeNode<K, V> getRoot();
	boolean isEmpty();
}
