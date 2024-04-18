import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implements SimpleList<T> and uses iterator to make and adjust lists
 * 
 * @author Samuel A. Rebelsky
 * @author Medhashree Adhikari
 */

public class SimpleCDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  int count;
  /**
   * The dummy node
   */
  Node2<T> dummy;

  /**
   * The number of values in the list.
   */
  int size;

  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * builds a SimpleCDLL with a given value
   */
  public SimpleCDLL() {
    this.dummy = new Node2<T>(null);

    this.dummy.next = this.dummy;
    this.dummy.prev = this.dummy;

    this.size = 0;
    this.count = 0;
  } // SimpleCDLL()

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+
      int version = SimpleCDLL.this.count;
      /**
       * The position in the list of the next value to be returned.
       * Included because ListIterators must provide nextIndex and
       * prevIndex.
       */
      int pos = 0;

      /**
       * The dummy node is what comes before the iterator
       */
      Node2<T> prev = SimpleCDLL.this.dummy;

      /**
       * The front node is what comes after the iterator
       */
      Node2<T> next = SimpleCDLL.this.dummy.next;

      /**
       * The node to be updated by remove or set. Has a value of
       * null when there is no such value.
       */
      Node2<T> update = null;

      // +---------+-------------------------------------------------------
      // | Methods |
      // +---------+

      /**
       * throws an exception when iterator is invalid
       */
      public void failFast() {
        if (this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }
      } // failFast()

      /**
       * updates the list and iterator counts
       */
      public void updateItCount() {
        this.version++;
        SimpleCDLL.this.count++;
      } // updateItCount()

      /**
       * inserts a new value in the linked/circular structure
       */
      public void add(T val) {
        // invalid iterator check
        failFast();

        // add the value
        this.prev = this.prev.insertAfter(val);
        this.update = null;

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position
        ++this.pos;

        updateItCount();
      } // add(T)

      /**
       * returns bool based on if there is a next
       */
      public boolean hasNext() {
        // invalid iterator check
        failFast();

        // compares size and pos to check if there is next
        return (this.pos < SimpleCDLL.this.size);
      } // hasNext()

      /**
       * returns bool based on if there is a previous
       */
      public boolean hasPrevious() {
        // invalid iterator check
        failFast();

        // compares size and pos to check if there is prev
        return (this.pos > 0);
      } // hasPrevious()

      /**
       * returns the next 'thing'
       */
      public T next() {
        // invalid iterator check
        failFast();

        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if

        // Identify the node to update
        this.update = this.next;
        // Advance the cursor
        this.prev = this.next;
        this.next = this.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()

      /**
       * returns the next index
       */
      public int nextIndex() {
        // invalid iterator check
        failFast();

        return this.pos;
      } // nextIndex()

      /**
       * returns the previous index
       */
      public int previousIndex() {
        // invalid iterator check
        failFast();

        return this.pos - 1;
      } // prevIndex

      /**
       * returns the previous 'thing'
       */
      public T previous() throws NoSuchElementException {
        // invalid iterator check
        failFast();

        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        }

        // update this.update to previous
        this.update = this.prev;

        // update
        this.next = this.prev;
        this.prev = this.prev.prev;

        // update position
        --this.pos;

        return this.update.value;
      } // previous()

      /**
       * removes the element after the iterator
       */
      public void remove() {
        // invalid iterator check
        failFast();

        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        if (this.prev == this.update) {
          this.prev = this.update.prev;
          --this.pos;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleCDLL.this.size;

        // Note that no more updates are possible
        this.update = null;

        updateItCount();
      } // remove()

      /**
       * sets the item after the iterator to val
       */
      public void set(T val) {
        // invalid iterator check
        failFast();

        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if
        // Do the real work
        this.update.value = val;
        // Note that no more updates are possible
        this.update = null;
      } // set(T)
    };
  } // listIterator()
} // class SimpleCDLL