/**
 *
 */
package uy.edu.um.tad.binarytree;

import lombok.Getter;
import lombok.Setter;
import uy.edu.um.tad.list.MyList;

@Getter
@Setter
public class TreeNode<K extends Comparable<K>, V> {
    private K key;
    private V value;
    private TreeNode<K, V> left;
    private TreeNode<K, V> right;

    public TreeNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void add(K key, V value) {
        TreeNode<K, V> elementToAdd = new TreeNode<>(key, value);
        if (key.compareTo(this.key) > 0) {
            if (right == null) {
                right = elementToAdd;
            } else {
                right.add(key, value);
            }
        } else {
            if (left == null) {
                left = elementToAdd;
            } else {
                left.add(key, value);
            }
        }
    }

    public TreeNode<K, V> remove(K key) {
        TreeNode<K, V> elementToReturn = this;
        if (key.compareTo(this.key) > 0) {
            if (right != null) {
                right = right.remove(key);
            }
        } else if (key.compareTo(this.key) < 0) {
            if (left != null) {
                left = left.remove(key);
            }
        } else if (left != null && right != null) {
            // Encontre el elemento a eliminar
            TreeNode<K, V> min = right.findMin();
            this.key = min.getKey();
            this.value = min.getValue();
            right = right.remove(min.getKey());
        } else {
            if (left != null) {
                elementToReturn = left;
            } else {
                elementToReturn = right;
            }
        }
        return elementToReturn;
    }

    public void inOrderTraverse(MyList<K> traverse) {
        if (left != null) {
            left.inOrderTraverse(traverse);
        }
        traverse.add(this.getKey());
        if (right != null) {
            right.inOrderTraverse(traverse);
        }
    }

    public TreeNode<K, V> findMin() {
        TreeNode<K, V> oReturn = this;
        if (left != null) {
            oReturn = left.findMin();
        }
        return oReturn;
    }

    public void inOrderValues(MyList<V> inorderList) {
        if (left != null) {
            left.inOrderValues(inorderList);
        }
        inorderList.add(this.getValue());
        if (right != null) {
            right.inOrderValues(inorderList);
        }
    }

    public boolean contains(K key) {
        int nValue = this.getKey().compareTo(key);
        if (nValue == 0)
            return true;
        if (nValue > 0 && this.getRight() != null)
            return this.getRight().contains(key);
        if (nValue < 0 && this.getLeft() != null)
            return this.getLeft().contains(key);
        return false;
    }

    public V find(K key) {
        int nValue = this.getKey().compareTo(key);
        if (nValue == 0)
            return this.value;
        if (nValue > 0 && this.getRight() != null)
            return this.getRight().find(key);
        if (nValue < 0 && this.getLeft() != null)
            return this.getLeft().find(key);
        return null;
    }
}
