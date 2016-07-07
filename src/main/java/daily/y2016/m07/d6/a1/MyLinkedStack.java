package daily.y2016.m07.d6.a1;

public class MyLinkedStack<T> {

	private static final int DEFAULT_CAPACITY = 10;
	
	private int theSize;
	private Node<T> top;
	
	private static class Node<T> {
		private Node<T> next;
		private T data;
		
		public Node(Node next, T x) {
			this.next = next;
			data = x;
		}
	}
	
	public MyLinkedStack() {
		clear();
	}
	public void clear() {
		top = null;
		theSize = 0;
	}
	
	public boolean isEmpty() {
		return top==null;
	}
	public T top() {
		if(isEmpty()) 
			throw new RuntimeException("stack is empty");
		return top.data;
	}
	public T pop() {
		if(isEmpty())
			throw new RuntimeException("stack is empty()");
		T t = top.data;
		top = top.next;
		theSize--;
		return t;
	}
	public boolean push(T x) {
		Node newTop = new Node(top, x);
		top = newTop;
		theSize++;
		return true;
	}
}
