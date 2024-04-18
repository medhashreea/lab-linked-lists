import java.util.Iterator;
import java.util.ListIterator;

/**
 * Very simple lists.
 * 
 * @author Samuel A. Rebelsky
 */
public interface SimpleList<T> extends Iterable<T> {
  public Iterator<T> iterator();

  public ListIterator<T> listIterator();
} // interface SimpleList<T>
