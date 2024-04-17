import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
       * inserts a new value in the linked/circular structure
       */
      public void add(T val) {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

        this.prev = this.prev.insertAfter(val);
        this.update = null;

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position
        ++this.pos;

        this.version++;
        SimpleCDLL.this.count++;
      } // add(T)

      /**
       * returns bool based on if there is a next
       */
      public boolean hasNext() {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }
        // if the next thing is not the dummy, true
        return (this.next != dummy);
      } // hasNext()

      /**
       * returns bool based on if there is a previous
       */
      public boolean hasPrevious() {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }
        // if the previous thing is not the dummy, true
        return (this.prev != dummy);
      } // hasPrevious()

      /**
       * returns the next 'thing'
       */
      public T next() {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

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
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

        return this.pos;
      } // nextIndex()

      /**
       * returns the previous index
       */
      public int previousIndex() {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

        return this.pos - 1;
      } // prevIndex

      /**
       * returns the previous 'thing'
       */
      public T previous() throws NoSuchElementException {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

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

        return null;
      } // previous()

      /**
       * removes the element after the iterator
       */
      public void remove() {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.next == this.update) {
          this.next = this.update.next;
        } // if
        if (this.prev == this.update) {
          this.prev = this.update.prev;
          --this.pos;
        } // if

        // Update the front
        if (dummy.next == this.update) {
          dummy.next = this.update.next;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleCDLL.this.size;

        // Note that no more updates are possible
        this.update = null;
      } // remove()

      /**
       * sets the item after the iterator to val
       */
      public void set(T val) {
        if(this.version != SimpleCDLL.this.count) {
          throw new ConcurrentModificationException();
        }

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