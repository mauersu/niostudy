package daily.y2016.m4.d06.a1;

import java.util.NoSuchElementException;

public class AvlBinarySearchTree<T extends Comparable<? super T>> {
	
	public static class AvlNode<T> {
		T element ;
		AvlNode<T> left;
		AvlNode<T> right;
		
		int height;
		
		AvlNode(T theElement) {
			this(theElement, null, null);
		}
		AvlNode(T theElement, AvlNode<T> lt, AvlNode<T> rt) {
			element = theElement ;
			left = lt;
			right = rt;
			height = 0;
		}
	}
	
	private int height(AvlNode<T> t) {
		return t ==null?-1:t.height;
	}
	
	public AvlNode<T> root;
	
	public AvlBinarySearchTree() {
		root = null;
	}
	
	public void insert(T x) {
		root = insert(x, root);
	}
	
	private AvlNode<T> insert(T x, AvlNode<T> t) {
		if(t==null) 
			return new AvlNode<T>(x, null, null);
		
		int compareResult = x.compareTo(t.element);
		
		if(compareResult<0) {
			t.left = insert(x, t.left);
			if(height(t.left) - height(t.right) == 2) 
				if(x.compareTo(t.left.element) < 0 ) 
					t = rotateWithLeftChild(t);
				else 
					t = doubleWithLeftChild(t);
		} else if(compareResult>0) {
			t.right = insert(x, t.right);
			if(height(t.right) - height(t.left) ==2) 
				if(x.compareTo(t.right.element) >0) 
					t = rotateWithRightChild(t);
				else 
					t = doubleWithRightChild(t);
		} else 
			;
		t.height = Math.max(height(t.left), height(t.right)) + 1;
		return t;
	}
	
	private AvlNode<T> rotateWithLeftChild(AvlNode<T> k2) {
		AvlNode<T> k1 = k2.left;
		k2.left = k1.right;
		k1.right = k2;
		k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
		k1.height = Math.max(height(k1.left), k2.height) + 1;
		return k1;
	}
	
	private AvlNode<T> doubleWithLeftChild(AvlNode<T> k3) {
		k3.left = rotateWithRightChild(k3.left);
		return rotateWithLeftChild(k3);
	}
	
	private AvlNode<T> rotateWithRightChild(AvlNode<T> k2) {
		AvlNode<T> k1 = k2.right;
		k2.right = k1.left;
		k1.left = k2;
		k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
		k1.height = Math.max(k2.height, height(k1.right)) + 1;
		return k1;
	}
	
	private AvlNode<T> doubleWithRightChild(AvlNode<T> k3) {
		k3.right = rotateWithLeftChild(k3.right);
		return rotateWithRightChild(k3);
	}
	
	public void remove(T x) {
		root = remove(root, x);
	}
	
	private AvlNode<T> remove(AvlNode<T> node, T x) {
		if(node ==null) 
			throw new NoSuchElementException();
		
		int compareResult = x.compareTo(node.element);
		
		if(compareResult>0) {
			node.right = remove(node.right, x);
			node.height = Math.max(height(node.left), height(node.right)) + 1;
			if(height(node.left) - height(node.right) ==2) {
				if(height(node.left.left) > height(node.left.right)) {
					node = rotateWithLeftChild(node);
				} else {
					node = doubleWithLeftChild(node);
				}
			}
		} else if(compareResult<0) {
			node.left = remove(node.left, x) ;
			node.height = Math.max(height(node.left), height(node.right)) + 1;
			if(height(node.right) - height(node.left) ==2) { 
				if(height(node.right.left)>height(node.right.right)) {
					node = doubleWithRightChild(node);
				} else {
					node = rotateWithRightChild(node);
				}
			}
		} else if(node.left!=null&& node.right!=null) {
			node.element = findMin(node.right);
			node.right = remove(node.right, node.element);
			node.height = Math.max(height(node.left), height(node.right)) + 1;
			if(height(node.left) - height(node.right) ==2) {
				if(height(node.left.right) > height(node.left.left)) {
					node = doubleWithLeftChild(node);
				} else {
					node = rotateWithLeftChild(node);
				}
			}
		} else {
			node = node.left!=null? node.left: node.right;
		}
		
		return node;
	}
	
	private T findMin(AvlNode<T> node) {
		if(node==null) {
			throw new NullPointerException();
		}
		while(node.left!=null) {
			node = node.left;
		}
		return node.element;
	}
}
