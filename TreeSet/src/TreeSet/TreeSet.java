
package TreeSet;

import NodeForSet.Node;
import interfaces.AbtractTreeSet;
import interfaces.SetTemplate;

import java.util.*;



public class TreeSet<T> implements SetTemplate<T>, AbtractTreeSet<T>,Cloneable,Iterable<T> {

    Node<T> root;
    private final Comparator<T> COMPARATOR;
    int size;
    private int modCount=0;

    public TreeSet() {
        COMPARATOR = null;
        root = null;
    }

    public TreeSet(Comparator<T> comparator) {
        root = null;
        this.COMPARATOR = comparator;
    }

    private int compare(T val1, T val2) {
        if (COMPARATOR != null)
            return COMPARATOR.compare(val1, val2);
        else if (val1 instanceof Comparable) {
            return ((Comparable<T>) val1).compareTo(val2);
        } else {
            throw new ClassCastException();
        }
    }

    // balancing tree methods
    private int getNodeHeight(Node<T> node) {
        return node == null ? -1 : node.height;
    }

    private int getBalanceFactor(Node<T> node) {
        return node == null ? 0 : getNodeHeight(node.getLeft()) - getNodeHeight(node.getRight());
    }

    private Node<T> balanceNode(Node<T> node) {
        node.height = 1 + Math.max(getNodeHeight(node.getLeft()), getNodeHeight(node.getRight()));

        int balanceFactor = getBalanceFactor(node);
        // LL Case
        if (balanceFactor > 1 && getBalanceFactor(node.getLeft()) >= 0) {
            return rightRotate(node);
        }

        // RR Case
        if (balanceFactor < -1 && getBalanceFactor(node.getRight()) <= 0) {
            return leftRotate(node);

        }

        // LR Case
        if (balanceFactor > 1 && getBalanceFactor(node.getLeft()) < 0) {
             node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        // RL Case
        if (balanceFactor < -1 && getBalanceFactor(node.getRight()) > 0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        return node;
    }



    // right rotation (fix right heaviness)

//    private Node<T> rightRotate(Node<T> node) {
//        Node<T> newRoot= node.getLeft();
//        Node<T> childOfNewRoot=newRoot.getRight();
//
//        // Rotation take place
//        node.setLeft(childOfNewRoot);
//        newRoot.setRight(node);
//
//        // Update heights
//        node.height = Math.max(getNodeHeight(node.getLeft()), getNodeHeight(node.getRight())) + 1;
//        newRoot.height = Math.max(getNodeHeight(newRoot.getLeft()), getNodeHeight(newRoot.getRight())) + 1;
//
//        return newRoot;
//
//    }
//
//    public Node<T> leftRotate(Node<T> node) {
//
//        Node<T> newRoot= node.getRight();
//        Node<T> childOfNewRoot=newRoot.getLeft();
//
//        // rotation take place
//        node.setRight(childOfNewRoot);
//        newRoot.setLeft(node);
//
//        // update heights
//        node.height = Math.max(getNodeHeight(node.getLeft()), getNodeHeight(node.getRight())) + 1;
//        newRoot.height = Math.max(getNodeHeight(newRoot.getLeft()), getNodeHeight(newRoot.getRight())) + 1;
//
//        return newRoot;
//
//    }

    private Node<T> rightRotate(Node<T> node){
        Node<T> newRoot=node.getLeft();
        Node<T> child=newRoot.getRight();

        newRoot.setRight(node);
        node.setLeft(child);

        node.height=findHeight(node);
        newRoot.height=findHeight(newRoot);

        return newRoot;
    }

    private Node<T> leftRotate(Node<T> node){
        Node<T> newRoot = node.getRight();
        Node<T> child = newRoot.getLeft();

        newRoot.setLeft(node);
        node.setRight(child);

        node.height=findHeight(node);
        newRoot.height=findHeight(newRoot);

        return newRoot;
    }


    public void add(T value) {
        if(value==null)
            throw new NullPointerException("Null value cannot be accept");
        if (root == null) {
                root = new Node<>(value);
                size++;modCount++;
        }
            root= checkAdd(root, value);
    }
    private Node<T> checkAdd(Node<T> node, T value) {
        if (node==null){
            return new Node<>(value);
        }

        int compare = compare(node.getData(), value);

        if (compare == 0) {
            return node;
        } else if (compare < 0) {
                node.setRight(checkAdd(node.getRight(), value));
                size++;
                modCount++;
        } else {
                node.setLeft(checkAdd(node.getLeft(), value));
                size++;
                modCount++;
        }

        return balanceNode(node);
    }

    private int findHeight(Node<T> node) {
        return 1 + Math.max(getNodeHeight(node.getLeft()), getNodeHeight(node.getRight()));
    }


    public void print() {
        print(root);
    }

    private void print(Node<T> node) {
        if (node != null) {
            print(node.getLeft());
            System.out.println(node.getData() + "-->My height is"+node.height);
            print(node.getRight());
        }
    }

    public Node<T> remove(T value) {
        if(value==null)
            throw new NullPointerException("Null value cannot be accept");
        root = remove(root, value);
        return root;
    }

    private Node<T> remove(Node<T> root, T value) {
        Node<T> parent = null;
        Node<T> current = root;

        // find the node for removal
        while (current != null && compare(current.getData(), value) != 0) {
            parent = current;
            if (compare(value, current.getData()) < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        if (current == null) {
            return root;
        }

        //  this for no child nodes
        if (current.getLeft() == null && current.getRight() == null) {
            if (current == root) {
                size--;modCount++;
                return null;
            };

            if (parent.getLeft() == current) parent.setLeft(null);
            else parent.setRight(null);
            size--;modCount++;
        }

        //this is for one child
        else if (current.getLeft() == null || current.getRight() == null) {
            Node<T> child = (current.getLeft() != null) ? current.getLeft() : current.getRight();
            if (current == root) {
                size--;modCount++;
                return balanceNode(child);
            }

            if (parent.getLeft() == current) parent.setLeft(child);
            else parent.setRight(child);
            size--;modCount++;
        }

        // this for two children
        else {
            Node<T> successorParent = current;
            Node<T> successor = current.getRight();
            while (successor.getLeft() != null) {
                successorParent = successor;
                successor = successor.getLeft();
            }
            current.setData(successor.getData());
            if (successorParent.getLeft() == successor) {
                successorParent.setLeft(successor.getRight());
            } else {
                successorParent.setRight(successor.getRight());
            }
            size--;modCount++;
        }

        return balanceNode(root);
    }



    public T get(T value){
        try {
            return getNode(root, value).getData();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Node<T> getNode(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        int compare = compare(node.getData(), value);
        if (compare == 0) {
            return node;
        } else if (compare < 0) {
            return getNode(node.getRight(), value);
        } else {
            return getNode(node.getLeft(), value);
        }
    }

    public boolean contains(T value)  {
        try {
            return contains(root,value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean contains(Node<T> node, T value){
        int compare=compare(node.getData(),value );
        if (compare==0) {
            return true;
        } else if (compare<0) {
            if (node.getRight()==null) {
                return false;
            }
            return contains(node.getRight(),value);
        }
        else {
            if (node.getLeft()==null) {
                return false;
            }
            return contains(node.getLeft(),value);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    private List<T> toArrayHelper() {
        List<T> list = new ArrayList<>();
        Stack<Node<T>> stack = new Stack<>();
        Node<T> current = root;
        while (current != null||!stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.getLeft();
            }

            current=stack.pop();
            list.add(current.getData());
            current = current.getRight();
        }
        return list;
    }

    public T[] toArray(T[] a) {
        return toArrayHelper().toArray(a);
    }


    public Object[] toArray() {
        return toArrayHelper().toArray();
    }


    public int size() {
        return size;
    }


    public TreeSet<T> clone() {
        TreeSet<T> clone=new TreeSet<T>();
        clone(root,clone);
        return clone;
    }

    private void clone(Node<T> node, TreeSet<T> clones) {
        if (node != null) {
            clone(node.getLeft(),clones);
            clones.add(node.getData());
            clone(node.getRight(),clones);
        }
    }

    public void clear() {
        root = null;
    }


    public boolean addAll(Collection<? extends T> c) {
        if (c == null) {
            return false;
        }
        for (T t : c) {
            add(t);
        }
        return true;
    }

    public boolean removeAll(Collection<? extends T> c) {
        if (c == null) {
            return false;
        }
        for (T t : c) {
            remove(t);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<? extends T> c) {
        TreeSet<T> temp=new TreeSet<T>();
        boolean changed=false;
        if (c == null) {
            return changed;
        }
        for (T t : c) {
            if(contains(t)){
                temp.add(t);
                changed=true;
            }
        }
        if (changed) {
            size=temp.size();
            setRoot(temp.getRoot());
        }
        return changed;
    }

    @Override
    public boolean containsAll(Collection<? extends T> c) {
        boolean changed=false;
        if (c == null) {
            return false;
        }
        for (T t : c) {
            if(contains(t)){
                changed=true;
            }
            else {
                return false;
            }
        }
        return changed;
    }

    private Node<T> getRoot() {
        return root;
    }

    private void setRoot(Node<T> root) {
        this.root = root;
    }

    @Override
    public T first() {
        Node<T> current = root;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current.getData();
    }

    public T last() {
        Node<T> current = root;
        while (current.getRight() != null) {
            current = current.getRight();
        }
        return current.getData();
    }

    public T pollFirst() {
        T first=first();
        remove(first()).getData();
        return first;
    }

    public T pollLast() {
        T last=last();
        remove(last()).getData();
        return last;
    }

    public TreeSet<T> subSet(T from, T to) {
        TreeSet<T> subSet=new TreeSet<T>();
        subSet(root,from,to,subSet);
        return subSet;
    }
    private void subSet(Node<T> node, T from, T to, TreeSet<T> subSet) {

        if (node == null) {
            return;
        }

        if (compare(node.getData(), from) >= 0) {
            subSet(node.getLeft(), from, to, subSet);
        }

        if (compare(node.getData(), from) >= 0 && compare(node.getData(), to) < 0) {
            subSet.add(node.getData());
        }

        if (compare(node.getData(), to) < 0) {
            subSet(node.getRight(), from, to, subSet);
        }
    }



    public TreeSet<T> headSet(T val) {
        TreeSet<T> head=new TreeSet<T>();
        headSet(root,val,head);
        return head;
    }
    private void headSet(Node<T> node,T val,TreeSet<T> head) {
        if (node == null) {
            return;
        }
        headSet(node.getLeft(),val,head);

        if(compare(node.getData(),val) < 0){
            head.add(node.getData());
        }else
            return;
        headSet(node.getRight(),val,head);
    }

    public TreeSet<T> tailSet(T val){
        TreeSet<T> tail=new TreeSet<T>();
        tailSet(root,val,tail);
        return tail;
    }
    private void tailSet(Node<T> node,T val,TreeSet<T> tail) {
        if (node == null) {
            return;
        }
        tailSet(node.getLeft(),val,tail);

        if(compare(node.getData(),val)>=0){
            tail.add(node.getData());
        }
        tailSet(node.getRight(),val,tail);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        toString(root, result);
        result.append("}");
        return result.toString();
    }

    private void toString(Node<T> node, StringBuilder result) {
        if (node == null) {
            return;
        }

        toString(node.getLeft(), result);
        if (result.length() > 1) {
            result.append(", ");
        }
        result.append(node.getData());
        toString(node.getRight(), result);
    }


    public List<T> descendingSet(){
        ArrayList<T> descendingSet=new ArrayList<>();
        descendingSet(root,descendingSet);
        return descendingSet;
    }

    private void descendingSet(Node<T> node, ArrayList<T> descendingSet) {
        if (node != null) {
            descendingSet(node.getRight(),descendingSet);
            descendingSet.add(node.getData());
            descendingSet(node.getLeft(),descendingSet);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeSetIterator<T>(root);
    }

    // iterator class

    class TreeSetIterator<T> implements Iterator<T> {

        private Stack<Node<T>> stack;
        private final int expectedModCount=modCount;
        int size;

        public TreeSetIterator(Node<T> root) {
            stack = new Stack<>();
            addLeftNodes(root);
        }

        @Override
        public boolean hasNext() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return !stack.isEmpty();
        }

        private void addLeftNodes(Node<T> node) {
            while (node != null) {
                stack.add(node);
                node = node.getLeft();
            }
        }

        @Override
        public T next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (stack.isEmpty()) {
                throw new NoSuchElementException("There is no more next elements");
            }

            Node<T> node = stack.pop();

            if (node.getRight() != null) {
                addLeftNodes(node.getRight());
            }
            return node.getData();
        }
    }
    public void printTree() {
        printTree(root, "", true);
    }

    private void printTree(Node<T> node, String prefix, boolean isTail) {
        if (node == null) {
            return;
        }

        printTree(node.getRight(), prefix + (isTail ? "│   " : "    "), false);

        System.out.println(prefix + (isTail ? "└── " : "┌── ") + node.getData());

        printTree(node.getLeft(), prefix + (isTail ? "    " : "│   "), true);
    }


    public static void main(String[] args) {
        // Create an instance of TreeSet
        TreeSet<Integer> treeSet = new TreeSet<Integer>();

        // Test add and print
        System.out.println("Testing add and print:");
        treeSet.add(1);
        treeSet.add(2);
        treeSet.add(3);
        treeSet.add(4);
        treeSet.add(4);
        treeSet.add(7);
        treeSet.add(10);
        System.out.println( "height:"+treeSet.root.getRight().height);
        treeSet.print(); // Expected order: 1, 3, 4, 5, 7, 8, 10

        treeSet.printTree();

        treeSet.remove(7);
        treeSet.remove(2);
        treeSet.remove(1);
//        treeSet.remove(null);

        treeSet.printTree();

//
//        // Test size
//        System.out.println("\nTesting size:");
//        System.out.println("Size: " + treeSet.size()); // Expected: 7
//
//        // Test contains
//        System.out.println("\nTesting contains:");
//        System.out.println("Contains 4: " + treeSet.contains(4)); // Expected: true
//        System.out.println("Contains 9: " + treeSet.contains(9)); // Expected: false
//
//        // Test first and last
//        System.out.println("\nTesting first and last:");
//        System.out.println("First: " + treeSet.first()); // Expected: 1
//        System.out.println("Last: " + treeSet.last());   // Expected: 10
//
//        // Test pollFirst and pollLast
//        System.out.println("\nTesting pollFirst and pollLast:");
//        System.out.println("Poll First: " + treeSet.pollFirst()); // Expected: 1
//        System.out.println("Poll Last: " + treeSet.pollLast());   // Expected: 10
//        treeSet.print(); // Remaining: 3, 4, 5, 7, 8
//
//        // Test subSet
//        System.out.println("\nTesting subSet:");
//        TreeSet<Integer> subset = treeSet.subSet(4, 8);
//        subset.print(); // Expected: 4, 5, 7
//
//        // Test headSet
//        System.out.println("\nTesting headSet:");
//        TreeSet<Integer> headSet = treeSet.headSet(5);
//        headSet.print(); // Expected: 3, 4
//
//        // Test tailSet
//        System.out.println("\nTesting tailSet:");
//        TreeSet<Integer> tailSet = treeSet.tailSet(5);
//        tailSet.print(); // Expected: 5, 7, 8
//
//        // Test toArray
//        System.out.println("\nTesting toArray:");
//        Integer[] array = treeSet.toArray(new Integer[0]);
//        System.out.println("Array: " + Arrays.toString(array)); // Expected: [3, 4, 5, 7, 8]
//
//        // Test descendingSet
//        System.out.println("\nTesting descendingSet:");
//        List<Integer> descendingList = treeSet.descendingSet();
//        System.out.println("Descending Order: " + descendingList); // Expected: [8, 7, 5, 4, 3]
//
//        // Test remove
//        System.out.println("\nTesting remove:");
//        treeSet.remove(5);
//        treeSet.print(); // Remaining: 3, 4, 7, 8
//
//        // Test addAll
//        System.out.println("\nTesting addAll:");
//        treeSet.addAll(Arrays.asList(6, 2, 9));
//        treeSet.print(); // Expected: 2, 3, 4, 6, 7, 8, 9
//
//        // Test removeAll
//        System.out.println("\nTesting removeAll:");
//        treeSet.removeAll(Arrays.asList(3, 7));
//        treeSet.print(); // Expected: 2, 4, 6, 8, 9
//
//        // Test retainAll
//        System.out.println("\nTesting retainAll:");
//        treeSet.retainAll(Arrays.asList(4, 8));
//        treeSet.print(); // Expected: 4, 8
//
//        // Test isEmpty
//        System.out.println("\nTesting isEmpty:");
//        System.out.println("Is empty: " + treeSet.isEmpty()); // Expected: false
//        treeSet.clear();
//        System.out.println("Is empty after clear: " + treeSet.isEmpty()); // Expected: true
//
//        // Test clone
//        System.out.println("\nTesting clone:");
//        treeSet.addAll(Arrays.asList(5, 10, 15));
//        TreeSet<Integer> clonedSet = treeSet.clone();
//        clonedSet.print(); // Expected: 5, 10, 15
//
//        // Test iterator
//        System.out.println("\nTesting iterator:");
//        Iterator<Integer> iterator = treeSet.iterator();
//        while (iterator.hasNext()) {
//            System.out.print(iterator.next() + " ");
//        }

        // Expected: 5, 10, 15
    }
}

//          -------------  *  --------------  *   ----------
