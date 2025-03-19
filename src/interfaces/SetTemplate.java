package interfaces;

import NodeForSet.Node;

import java.util.Collection;

public interface SetTemplate<T> {

    public void add(T o);

    public Node<T> remove(T o) ;

    public boolean contains(T o);

    public int size();

    public boolean isEmpty();

    public void clear();

    public boolean addAll(Collection<? extends T> c) ;

    public boolean removeAll(Collection<? extends T> c);

    public boolean retainAll(Collection<? extends T> c);

    public boolean containsAll(Collection<? extends T> c);

}
