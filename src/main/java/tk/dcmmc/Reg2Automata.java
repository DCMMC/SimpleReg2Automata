package tk.dcmmc;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.lang.reflect.Array;
import java.util.stream.IntStream;
import com.sun.istack.internal.NotNull;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

/**
 * C style Pointer container
 * @param <T>
 */
class Pointer<T> {
    T item;

    Pointer(T t) {
        item = t;
    }
}

/**
 * Invalid Type Exception
 */
class InvalidTypeException extends Exception {
    InvalidTypeException(String message) {
    	super(message);
    }
}


/**
 * Pair
 */
class Pair<F, S> {
    F first;
    S second;

    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair with (" + first.toString() + ", " + second.toString() + ")";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && ((Pair<F,S>)obj).first.equals(first) &&
                ((Pair<F, S>)obj).second.equals(second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
}

/**
 * class comment : Generic Type Bag
 * 使用SLL(Single Linked List)实现的Bag.
 * Bag: 一种只能添加元素, 不能删除元素, 可以用foreach遍历的一种ADT.
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
class Bag<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前Bag中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node first;

    /*
     **************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    Bag() {

    }

    /**
     * constructor from items
     * @param items
     *      items
     */
    @SafeVarargs
    Bag(Item... items) {
        for (Item i : items) {
            add(i);
        }
    }

    /*
     **************************************
     * Inner Class                        *
     **************************************/
    /**
     * Linked List节点
     */
    private class Node {
        //节点中保存的元素, 初始化为null
        Item item;

        //下一个节点, 初始化为null
        Node next;

        //构造器
        Node(Item item) {
            this.item = item;
        }

        @Override
        public String toString() {
            return "Bag node with value: " + item.toString();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object obj) {
            return obj != null && getClass() == obj.getClass() && ((Node)obj).item.equals(item);
        }
    }

    /**
     * to string to represents all the elements
     * @return
     *      string info
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bag(" + this.hashCode() + ") with elements: (");
        for (Item i : this) {
            sb.append(i.toString()).append(", ");
        }

        return sb.append(")").toString();
    }

    /**
     * 成员内部类
     * 用于遍历这个Bag
     */
    private class BagIterator implements Iterator<Item> {
        private Bag<Item>.Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return Bag中上个被遍历的元素后面还有元素就返回true
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * 继续遍历Bag后面的所有元素
         * @return 下一个元素的值
         */
        @Override
        public Item next() {
            if (hasNext()) {
                Item item = current.item;
                current = current.next;
                return item;
            }
            else
                return null;
        }

    }

    /*
     **************************************
     * Methods     		                  *
     **************************************/
    /**
     * 向Bag中添加新的元素
     * @param item 新元素
     */
    public void add(Item item) {
        //如果LinkedList里面还没有任何元素
        if (first == null) {
            first = new Node(item);
        } else {
            Node tmpFirst = new Node(item);
            tmpFirst.next = first;
            first = tmpFirst;
        }
        size++;
    }

    /**
     * if the Bag contains item
     * @param item
     *      item to search
     * @return
     *      true if contain
     */
    boolean contain(Item item) {
        for (Item i : this) {
            if (i.equals(item))
                return true;
        }
        return false;
    }

    /**
     * 获得当前Bag存储了多少个元素
     * @return 当前Bag存储的多少个元素
     */
    public int getSize() {
        return size;
    }

    /**
     * 判断Bag是否是空的
     * @return 判断Bag是否是空的
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 判断Bag是否已满, SLL的实现方案直接都返回true
     * @return 判断Bag是否已满
     */
    public boolean isFull() {
        return false;
    }

    /**
     * check all the elements of this equals to Bag obj
     * @param obj
     *      another Bagobject
     * @return
     *      true if all the elements of this equals to the obj.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        ArrayList<Item> list = new ArrayList<>();
        for (Item i : this) {
            list.add(i);
        }
        list.sort(null);

        ArrayList<Item> list2 = new ArrayList<>();
        for (Item i : (Bag<Item>)obj) {
            list2.add(i);
        }
        list2.sort(null);

        return list.equals(list2);
    }

    /**
     * sufficient hashCode implementation
     *
     * @return
     *      the hash code value for this list
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Item i : this) {
            hashCode += (i == null ? 0 : i.hashCode());
        }

        return hashCode;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    public Iterator<Item> iterator() {
        return this.new BagIterator();
    }

    /**
     * Test Client.
     * @param args
     *			command-line arguments.
     */
    public static void main(String[] args) {
        //foreach遍历测试
        System.out.println("foreach遍历测试");

        Bag<Integer> bag = new Bag<>();
        bag.add(5);
        bag.add(2);
        bag.add(1);
        bag.add(7);

        //遍历
        for (int i : bag) {
            System.out.println(i);
        }

    }
}

/**
 * class comment : Generic Type Double Linked List(DLLists)
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
class DoubleLinkedList<Item> implements Iterable<Item> {
    /*
     **************************************
     * Fields                             *
     **************************************/
    //当前List中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node first;

    //尾节点也就是最晚加入的节点, 初始化为null
    private Node last;

    //为ReverseArrayIterator迭代器记录对List的操作次数, 防止在迭代的过程中List被更改
    private int opsCnt;



    /*
     **************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    DoubleLinkedList() {

    }

    /**
     * 从数组创建LinkedList
     * 基本类型数组并不能向上转型为Item[](Object[]), 不过(基本类型)数组都可以向上转型为Object, 然后再利用
     * java.lang.reflect.Array类获取index和get元素. 在强制向下转型为Item
     * @param array
     *           Item数组
     */
    @SuppressWarnings("unchecked")
    DoubleLinkedList(Object array) {
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++)
            addLast( (Item)Array.get(array, i));
    }

    /**
     * 从可变参数中创建LinkedList
     * @param arrayElements
     *           可变长参数
     */
    @SuppressWarnings("unchecked")
    DoubleLinkedList(Item... arrayElements) {
        for (Item i : arrayElements)
            addLast(i);
    }

    /*
     **************************************
     * Inner Class                        *
     **************************************/
    /**
     * Linked List节点
     */
    class Node {
        //节点中保存的元素, 初始化为null
        Item item;

        //下一个节点, 初始化为null
        Node next;

        //上一个节点
        Node previous;

        //构造器
        Node(Item item) {
            this.item = item;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && getClass() == obj.getClass() && item.equals(obj);
        }

        /**
         * 1) If two objects are equal, then they must have the same hash code.
         * 2) If two objects have the same hash code, they may or may not be equal.
         * @return
         *      if the length of item.toString() are equal.
         */
        @Override
        public int hashCode() {
            return item.hashCode();
        }

        @Override
        public String toString() {
            return "LinkedList Node with value " + item.toString();
        }
    }

    /**
     * 成员内部类
     * 用于遍历这个DoubleLinkedList
     */
    private class ReverseArrayIterator implements Iterator<Item> {
        //为当前opsCnt创建副本
        private final int opsCntCopy = opsCnt;

        private DoubleLinkedList<Item>.Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return List中上个被遍历的元素后面还有元素就返回true
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public boolean hasNext() throws ConcurrentModificationException {
            if (opsCntCopy != opsCnt)
                throw new ConcurrentModificationException();

            return current != null;
        }

        /**
         * 继续遍历List后面的所有元素
         * @return 下一个元素的值
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public Item next() throws ConcurrentModificationException {
            if (opsCntCopy != opsCnt)
                throw new ConcurrentModificationException();

            if (hasNext()) {
                Item item = current.item;
                current = current.next;
                return item;
            }
            else
                return null;
        }

    }

    /*
     **************************************
     * Methods                            *
     **************************************/

    /**
     * 返回由DoubleLinkedList表示的数组(FIFO)
     * @return 返回List中存储的所有引用类型(包装类型)的数组, 因为泛型擦除, 所以调用的时候需要强制把返回的Object[]转化为目标类型(i.e
     * Item[]), 而且通过构造器DoubleLinkedList(Object array)创建的基本类型List会被转化为引用类型.
     */
    @SuppressWarnings("unchecked")
    Item[] toArray() {
        if (first == null || first.item == null)
            return null;

        Item[] array = (Item[])java.lang.reflect.Array.newInstance(first.item.getClass(), getSize());
        //不能像下面这样做, 因为实质性的array还是Object[], 而上面的虽然转换成了Object(Item[]), 但是通过RTTI可以知道其是一个Item所属的
        //引用类型的数组
        //Item[] array = (Item[])new Object[getSize()];

        ReverseArrayIterator iter = new ReverseArrayIterator();

        int index = 0;

        while (iter.hasNext())
            array[index++] = iter.next();

        return array;
    }

    /**
     * 从DoubleLinkedList中的前端添加新的元素(模拟LIFO)
     * @param item 新元素
     */
    public DoubleLinkedList<Item> addFirst(Item item) {
        //如果LinkedList里面还没有任何元素
        if (first == null) {
            last = first = new Node(item);
            size++;
            opsCnt++;
        } else {
            Node tmpFirst = new Node(item);
            tmpFirst.next = this.first;
            this.first.previous = tmpFirst;
            first = tmpFirst;
            size++;
            opsCnt++;
        }

        return this;
    }

    /**
     * 从DoubleLinkedList中的后端添加新的元素(模拟FIFO)
     * @param item 新元素
     */
    public void addLast(Item item) {
        //如果LinkedList里面还没有任何元素
        if (last == null) {
            last = first = new Node(item);
            size++;
            opsCnt++;
        } else {
            Node tmpLast = new Node(item);
            last.next = tmpLast;
            tmpLast.previous = this.last;
            last = tmpLast;
            size++;
            opsCnt++;
        }
    }

    /**
     * 从给定的offset后面插入指定的值
     * @param offset
     *           在offset后插入新的节点
     * @param item
     *           新的这个节点中Item的值
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    void add(int offset, Item item) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                //找到该offset所在的Node
                Node newNode = new Node(item);
                newNode.previous = current;
                newNode.next = current.next;
                current.next = newNode;
                size++;
                opsCnt++;
            }

            //继续向后遍历
            current = current.next;
        }

        //那就可能是last那个Node
        if (index == offset) {
            //找到该offset所在的Node
            Node newNode = new Node(item);
            newNode.previous = current;
            newNode.next = current.next;
            current.next = newNode;
            last = newNode;
            size++;
            opsCnt++;
        }

    }

    /**
     * 从任意的offset中获取item, 并把这个item所在的Node从List中删除
     * @param offset
     *           要获取的元素的offset, 0 <= offset <= getSize() - 1
     * @return 要获取的元素
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     * @throws NoSuchElementException 如果List为空
     */
    Item pop(int offset) throws IndexOutOfBoundsException, NoSuchElementException {
        outOfBoundsCheck(offset);
        if (getSize() == 0)
            throw new NoSuchElementException("This DoubleLinkedList is empty!");

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                //如果获取到该offset所在的Node
                //如果是first
                if (current.previous == null) {
                    //如果List只有一个元素
                    if (first.next == null)
                        first = last = null;
                    else {
                        first = first.next;
                        first.previous = null;
                    }
                } else {
                    current.previous.next = current.next;
                }
                size--;
                opsCnt++;
                return current.item;
            }

            //继续向后遍历
            current = current.next;
        }

        //如果是last(first)
        //记得减回去
        if (index == offset) {
            //如果只有一个元素
            if (getSize() == 1) {
                first = last = null;
            } else {
                last = last.previous;
                last.next = null;
            }
            size--;
            opsCnt++;
            return current.item;
        }

        return null;
    }


    /**
     * 返回List前端的元素, 并把该元素从List中删除.(模拟LIFO)
     * @throws NoSuchElementException
     * if the client attempts to remove an item from an empty list
     * @return List前端第一个元素
     */
    Item popFirst() {
        if (getSize() == 0)
            throw new NoSuchElementException("This DoubleLinkedList is empty!");

        return pop(0);
    }

    /**
     * 返回List后端的元素, 并把该元素从List中删除.(模拟FIFO)
     * @throws NoSuchElementException
     * if the client attempts to remove an item from an empty list
     * @return List后端最后一个元素
     */
    @SuppressWarnings("unchecked")
    Item popLast() {
        if (getSize() == 0)
            throw new NoSuchElementException("This DoubleLinkedList is empty!");

        Item lastItem = last.item;

        //如果只有一个元素
        if (getSize() == 1) {
            first = last = null;
        } else {
            last = last.previous;
            last.next = null;
        }
        size--;
        opsCnt++;
        return lastItem;
    }

    /**
     * 用List中删除指定offset的元素
     * @param offset
     *          要删除的元素的序号
     * @throws IndexOutOfBoundsException
     *          如果offset不存在就抛出异常
     */
    public void remove(int offset) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        pop(offset);
    }


    /**
     * 返回List后端的元素, 并且不会删除这个元素
     * @return List前端第一个元素
     */
    public Item getFirst() {
        //如果List为空就返回null
        if (first == null)
            return null;

        return first.item;
    }

    /**
     * 返回List后端的元素, 并且不会删除这个元素
     * @return List后端最后一个元素
     */
    Item getLast() {
        //如果List为空就返回null
        if (last == null)
            return null;

        return last.item;
    }

    /**
     * 从任意的offset中获取item, 并且不会删除这个元素
     * @param offset
     *           要获取的元素的offset, 0 <= offset <= getSize() - 1
     * @return 要获取的元素
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public Item get(int offset) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset)
                return current.item;

            //继续向后遍历
            current = current.next;
        }

        //如果是last
        if (index == offset)
            return current.item;

        return null;
    }

    /**
     * get first node, **be careful to use it**!
     * @return
     *      first node
     */
    public DoubleLinkedList<Item>.Node getFirstNode() {
        return first;
    }

    /**
     * get the last node, **be careful to use it**!
     * @return
     *      last node
     */
    public DoubleLinkedList<Item>.Node getLastNode() {
        return last;
    }

    /**
     * Concatenate this list with another list, **be careful to use it**!
     * @param another
     *          another list
     * @return
     *          this list
     */
    public DoubleLinkedList<Item> concate(DoubleLinkedList<Item> another) {
        this.last.next = another.getFirstNode();
        another.getFirstNode().previous = this.last;
        this.last = another.getLastNode();
        this.size += another.getSize();

        return this;
    }


    /**
     * 将指定offset的元素中的内容更换
     * @param offset
     *           元素的序列
     * @param item
     *           该Node上的新item
     * @return 该Node上的旧值
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    Item modify(int offset, Item item) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                Item oldItem = current.item;
                current.item = item;
                return oldItem;
            }


            //继续向后遍历
            current = current.next;
        }

        //可能是last
        if (index == offset) {
            Item oldItem = current.item;
            current.item = item;
            return oldItem;
        }

        return null;
    }

    /**
     * Ex 2.2.17
     * mergesort the list
     */
    public void mergesort() {
        //check
        if ( first.item == null || !(first.item instanceof Comparable) )  {
            //err
            System.out.println("the items (" + (first.item != null ? first.item.getClass() : "") + ") stored in List are not intanceof Comparable,"
                    +  "you can use the Comparator version of mergesort.");
            return;
        }

        //创建辅助数组, 只额外分配一次
        Comparable[] auxLocal = new Comparable[getSize()];

        sort(auxLocal, 0, getSize() - 1);
    }

    /**
     * Ex 2.2.17
     * mergesort the list with Comparator
     * @param comp
     *           比较器
     */
    public void mergesort(Comparator comp) {
        //创建辅助数组, 只额外分配一次
        Object[] auxLocal = new Object[getSize()];

        sort(comp, auxLocal, 0, getSize() - 1);
    }

    /**
     * Ex 2.2.18
     * 有点难度
     * Shuffling linked list using divide-and-conquer design diagram
     */
    public void shuffle() {

    }


    /**
     * 获得当前Stack存储了多少个元素
     * @return 当前Stack存储的多少个元素
     */
    int getSize() {
        return size;
    }

    /**
     * 判断Stack是否是空的
     * @return 判断Stack是否是空的
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 判断Stack是否已满, resizing-capacity array的实现方案直接都返回true
     * @return 判断Stack是否已满
     */
    public boolean isFull() {
        //这是原来fixed-capacity array实现的stack的方案
        //return getSize() >= MAXSIZE;

        //新的resizing-capacity array实现的stack, 直接返回false.
        return false;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        return this.new ReverseArrayIterator();
    }

    /**
     * 更加易读的toString信息
     * @return 关于该List中所有元素的打印
     */
    @Override
    public String toString() {
        if (getSize() == 0)
            return "没有任何元素.";

        Iterator<Item> itr = iterator();

        String info = "[ ";

        while (itr.hasNext()) {
            info += (itr.next() + ", ");
        }

        info = info.substring(0, info.length() - 2);

        info += ("] (总共有" + getSize() + "个"
                + first.item.getClass().toString().replaceFirst("class ", "")
                + "型元素.)");

        return info;
    }

    /**
     * Ex 2.2.17
     * 采用自顶向下的方法(递归)排序List中的指定的部分
     * @param aux
     *       局部暂存数组
     * @param lo
     *       要排序的部分的第一个元素的下标
     * @param hi
     *       要排序的部分的最后一个元素的下标
     */
    private void sort(Comparable[] aux, int lo, int hi) {
        //当只有一个元素的时候, 这个子序列一定是排序好的了, 所以这就作为递归结束的条件
        if (lo >= hi)
            return;

        int mid = lo + (hi - lo) / 2;

        //下述代码形成一个二叉树形结构, 或者用trace表示为一个自顶向下的结构(top-down)
        //sort left half
        sort(aux, lo, mid);
        //sort right half
        sort(aux, mid + 1, hi);

        //merge才是真正的比较的地方, 上面的代码只是会形成二叉树, 真正的比较是在merge中
        merge(aux, lo , mid, hi);
    }

    /**
     * Ex 2.2.17
     * merge(optimized version)
     * @param aux
     *       暂存数组, 有方法参数传递, 使用方法局部变量
     * @param lo
     *       要归并的前半部分的起始下标
     * @param mid
     *       要归并的前半部分的最后一个元素的下标
     * @param hi
     *       要归并的后半部分的最后一个元素的下标
     */
    @SuppressWarnings("unchecked")
    private void merge(Comparable[] aux, int lo, int mid, int hi) {
        //check是否本身lo...hi就已经是排序好的了, 提高效率
        if ( ((Comparable)get(mid)).compareTo((Comparable)get(mid + 1)) <= 0)
            return;

        //先将数据暂存在辅助数组中
        for (int i = lo; i <= hi; i++)
            aux[i] = (Comparable)get(i);


        //i, j分别为两部分的第一个元素的下标
        int i = lo;
        int j = mid + 1;
        //归并
        //先找到lo所在的结点
        Node current = first;
        int index = 0;

        while (index++ != lo) {
            current = current.next;
        }

        //前面index++, 这里一定要记得减回去
        index--;

        while (index <= hi) {
            if (i > mid)
                current.item = (Item)aux[j++];
            else if (j > hi)
                current.item = (Item)aux[i++];
            else if (aux[j].compareTo(aux[i]) < 0)
                current.item = (Item)aux[j++];
            else
                current.item = (Item)aux[i++];

            current = current.next;
            index++;
        }
    }


    /**
     * get offset by value
     *
     * !!!all the values in list must be different!!!
     *
     * @param item
     *          item to be local
     * @return
     *          -1 if not found
     */
    int getOffsetByValue(Item item) {
        int index = 0;
        for (Item i : this) {
            if (item.equals(i))
                return index;
            index++;
        }

        return -1;
    }

    /**
     * Ex 2.2.17
     * 采用自顶向下的方法(递归)和给定的Comparator排序List中的指定的部分
     * @param comp
     *       比较器
     * @param aux
     *       局部暂存数组
     * @param lo
     *       要排序的部分的第一个元素的下标
     * @param hi
     *       要排序的部分的最后一个元素的下标
     */
    private void sort(Comparator comp, Object[] aux, int lo, int hi) {
        //当只有一个元素的时候, 这个子序列一定是排序好的了, 所以这就作为递归结束的条件
        if (lo >= hi)
            return;

        int mid = lo + (hi - lo) / 2;

        //下述代码形成一个二叉树形结构, 或者用trace表示为一个自顶向下的结构(top-down)
        //sort left half
        sort(comp, aux, lo, mid);
        //sort right half
        sort(comp, aux, mid + 1, hi);

        //merge才是真正的比较的地方, 上面的代码只是会形成二叉树, 真正的比较是在merge中
        merge(comp, aux, lo , mid, hi);
    }

    /**
     * Ex 2.2.17
     * merge(optimized version) with Comparator
     * @param comp
     *       比较器
     * @param aux
     *       暂存数组, 有方法参数传递, 使用方法局部变量
     * @param lo
     *       要归并的前半部分的起始下标
     * @param mid
     *       要归并的前半部分的最后一个元素的下标
     * @param hi
     *       要归并的后半部分的最后一个元素的下标
     */
    @SuppressWarnings("unchecked")
    private void merge(Comparator comp, Object[] aux, int lo, int mid, int hi) {
        //check是否本身lo...hi就已经是排序好的了, 提高效率
        if ( comp.compare(get(mid), get(mid + 1)) <= 0)
            return;

        //先将数据暂存在辅助数组中
        for (int i = lo; i <= hi; i++)
            aux[i] = get(i);

        //i, j分别为两部分的第一个元素的下标
        int i = lo;
        int j = mid + 1;
        //归并
        //先找到lo所在的结点
        Node current = first;
        int index = 0;

        while (index++ != lo) {
            current = current.next;
        }

        //前面index++, 这里一定要记得减回去
        index--;

        while (index <= hi) {
            if (i > mid)
                current.item = (Item)aux[j++];
            else if (j > hi)
                current.item = (Item)aux[i++];
            else if (comp.compare(aux[j], aux[i]) < 0)
                current.item = (Item)aux[j++];
            else
                current.item = (Item)aux[i++];

            current = current.next;
            index++;
        }
    }


    /**
     * 检查offset是否合法
     * @param offset
     *           要检查的offset
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    private void outOfBoundsCheck(int offset) throws IndexOutOfBoundsException {
        if ( offset < 0 || offset >= getSize() )
            throw new IndexOutOfBoundsException("序号" + offset
                    + "在本List中不存在, 请输入0 ~ " + (getSize() - 1) + "的数");
    }

    /*
     **************************************
     * 我的一些方法和client测试方法         *
     **************************************/

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * @param obj 要输出的String.
     * @throws IllegalArgumentException 参数不能为空
     */
    private static void o(Object obj) throws IllegalArgumentException {
        if (obj == null)
            throw new IllegalArgumentException("参数不能为空!");

        System.out.println(obj);
    }

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * 重载的一个版本, 不接受任何参数, 就是为了输出一个回车.
     */
    private static void o() {
        System.out.println();
    }



    /**
     * Test Client.
     * @param args
     *          command-line arguments.
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        DoubleLinkedList<Integer> dllist = new DoubleLinkedList<>();

        //一系列操作, 操作完的结果应该是:
        dllist.addFirst(5);
        dllist.addFirst(6);
        dllist.addLast(2);
        dllist.addLast(7);
        dllist.add(3, 6);
        dllist.add(0, 8);
        dllist.add(dllist.getSize() - 1, 9);


        o("getFirst(): " + dllist.getFirst() + ", getLast(): " + dllist.getLast()
                + ", get(2): " + dllist.get(2));

        //Pop Test
        dllist.popFirst();
        dllist.popLast();
        dllist.pop(2);

        //Test Modify
        dllist.modify(1, 7);

        //Test toString and Iterator, 结果应该是8, 7, 7, 6
        o(dllist);

        //array to Linked List
        //Primitive type array
        int[] array = {1, 3, 5, 7};
        o(new DoubleLinkedList<Integer>(array));

        //test toArray
        o("test toArray()");
        for (Integer i : new DoubleLinkedList<Integer>(array).toArray())
            System.out.print(" " + i);
        o();

        //Reference type array
        Integer[] integerArray = {1, 3, 5, 7};
        //为了抑制java对于数组造成的潜在的varargs参数混淆的警告, 先强制转化为Object而不是Object[]
        o(new DoubleLinkedList((Object)integerArray));

        //varargs test
        o(new DoubleLinkedList(1, 5, 9, 11));
    }
}

/**
 * (Weighted Directed) Graph/Network implemented by AdjList (LinkedList)
 *
 * @param <VerT>
 *              vertex element type
 * @param <EdgeT>
 *              edge/arc weight type, bool indicates DG/UDG
 */
class Graph<VerT, EdgeT> {
    /**
     * DG: Directed Graph
     * DN: Directed Network
     * UDG: Undirected Graph
     * UDN: Undirected Network
     */
    public enum GraphType {
        DG, DN, UDG, UDN
    }

    // the list of vertex nodes, Second element in inner Pair indicates the verNode index that the Edge point to.
    // if use HashTable to store the vertex nodes, the second element in inner Pair can use
    // the value type of vertex to local the tail node in the arc/edge.
    private DoubleLinkedList<Pair<Bag<Pair<EdgeT, Object>>, VerT>> adjLists;
    // HashMap to local vertex node by vertex value quick
    // **all the values of vertex must be different!!!**
    // the limits will lead to the Graph not in common use but can be efficiently used in NFA2DFA Dtrans
    private HashMap<VerT, DoubleLinkedList<Pair<Bag<Pair<EdgeT, Object>>, VerT>>.Node> vertexMap = new HashMap<>();
    // size of edges
    private int edgeNum;
    // size of vertex
    private int verNum;
    // type of graph
    private GraphType type;
    // visited map used by DFS and BFS
    // **all the values of vertex must be different!!!**
    private HashMap<VerT, Boolean> visited;

    /**
     * default constructor
     * @param type
     *          the type of graph
     */
    Graph(Graph.GraphType type) {
        this.type = type;
        // verNum = 0;
        // edgeNum = 0;
        this.adjLists = new DoubleLinkedList<>();
    }

    /**
     * A simple Graph implemented by LinkedList
     *
     * **Object of inner Pair is the reference of vertex node's object**
     *
     * @param type
     *              DG, UDG(EdgeT == bool), DN, UDN
     * @param vers
     *              array of vertex
     * @param edgeMatrix
     *              Matrix of edge, the subscript of Matrix is the index of vertex, if there is DG/UDG then EdgeT is
     *              bool
     * @param disconnectedNotation
     *              notation indicates no Edge in Matrix, if there is DG/UDG, then disconnectedNotation can be false or
     *              true, its value is useless.
     */
    @SuppressWarnings("unchecked")
    Graph(Graph.GraphType type, VerT[] vers, EdgeT[][] edgeMatrix, final EdgeT disconnectedNotation) {
        this.adjLists = new DoubleLinkedList<>();

        this.type = type;
        this.verNum = vers.length;
        this.edgeNum = edgeMatrix.length;

        for (int i = 0; i < verNum; i++) {
            Pair<Bag<Pair<EdgeT, Object>>, VerT> ver = new Pair<>(new Bag<>(), vers[i]);

            adjLists.addLast(ver);
            vertexMap.put(vers[i], adjLists.getLastNode());
        }

        int i = 0;
        for (Pair<Bag<Pair<EdgeT, Object>>, VerT> ver : adjLists) {
            for (int j = 0; j < verNum; j++) {
                if (edgeMatrix[i][j] != disconnectedNotation) {
                    ver.first.add(new Pair<>(edgeMatrix[i][j], adjLists.get(j)));
                }
            }
            i++;
        }
    }

    /**
     * check index of vertex
     * @param indexOfVer
     *          index of vertex
     * @return
     *          false if index of ver not beyond the valid range (i.e., [0, verNum))
     */
    private boolean isVerIndexInvalid(int indexOfVer) {
        return indexOfVer < 0 && indexOfVer >= verNum;
    }

    /**
     * add an edge/arc to the graph
     * if there is Undirected Graph or Undirected Network, the order of v and w is not affect.
     * @param v
     *          vertex index the edge point from
     * @param w
     *          vertex index the edge point to
     * @param weight
     *          the weight of edge, if there is Grapg, the weight is useless, so it can be neither true or false
     * @return
     *          this instance
     */
    public Graph<VerT, EdgeT> addEdge(int v, int w, EdgeT weight) throws InvalidParameterException {
        // check vertex index
        if (isVerIndexInvalid(v) || isVerIndexInvalid(w)) {
            throw new InvalidParameterException("parameter v or w is invalid!");
        }

        adjLists.get(v).first.add(new Pair<>(weight, adjLists.get(w)));
        edgeNum++;

        return this;
    }

    /**
     * the result set of vertex values that the vertex in position offset can reach using DFS(depth-first search)
     * @param offset
     *      the offset of the object vertex
     * @return
     *      the list of vertex's values that vertex offset can reach
     */
    List<VerT> dfsTraverse(int offset) {
        visited = new HashMap<>();
        for (Pair<Bag<Pair<EdgeT, Object>>, VerT> v : adjLists)
            visited.put(v.second, false);
        ArrayList<VerT> list = new ArrayList<>();

        for (int i = offset; i < offset + verNum; i++) {
            Pair<Bag<Pair<EdgeT, Object>>, VerT> node = adjLists.get(i % verNum);
            if (!visited.get(node.second)) {
                dfs(list, node);
            }
        }

        return list;
    }

    /**
     * dfs recursive method
     *
     * @param list
     *      result list
     * @param node
     *      vertex node
     */
    @SuppressWarnings("unchecked")
    void dfs(List<VerT> list, Pair<Bag<Pair<EdgeT, Object>>, VerT> node) {
        visited.put(node.second, true);
        // operation
        list.add(node.second);
        for (Pair<EdgeT, Object> e : node.first) {
            Pair<Bag<Pair<EdgeT, Object>>, VerT> vertex =
                (Pair<Bag<Pair<EdgeT, Object>>, VerT>)(e.second);
            if (!visited.get(vertex.second)) {
                dfs(list, vertex);
            }
        }
    }

    /**
     * add a vertex to the graph (add to the end of adjLists)
     * @param value
     *          the value of vertex
     * @return
     *          this instance
     */
    public Graph<VerT, EdgeT> addVer(VerT value) {
        adjLists.addLast(new Pair<>(new Bag<>(), value));
        vertexMap.put(value, adjLists.getLastNode());
        verNum++;

        return this;
    }

    /**
     * add a vertex to the graph(add to the first of adjLists)
     * @param value
     *          the value of vertex
     * @return
     *          this instance
     */
    public Graph<VerT, EdgeT> addVerFirst(VerT value) {
        adjLists.addFirst(new Pair<>(new Bag<>(), value));
        vertexMap.put(value, adjLists.getFirstNode());
        verNum++;

        return this;
    }

    /**
     * add edge to the vertex
     * @param v
     *      the index of vertex
     * @param edge
     *      edge, the Integer indicates the index of node the edge point to in the adjLists
     * @return
     *      this instance
     */
    public Graph<VerT, EdgeT> addEdge2Ver(int v, Pair<EdgeT, Integer> edge) {
        edgeNum++;
        getAdj(v).add(new Pair<>(edge.first, adjLists.get(edge.second)));

        return this;
    }

    /**
     * get the adjacency list of the edges point from vertex v
     * @param v
     *          the vertex index
     * @return
     *          the iterable list of the edges
     */
    Bag<Pair<EdgeT, Object>> getAdj(int v) {
        // check v
        if (isVerIndexInvalid(v)) {
            throw new InvalidParameterException("parameter v is invalid!");
        }

        return adjLists.get(v).first;
    }

    /**
     * concatenate this graph's adjLists with another's
     * @param another
     *          another Graph
     * @return
     *          this instance
     */
    public Graph<VerT, EdgeT> concate(Graph<VerT, EdgeT> another) throws InvalidTypeException {
        if (another.type != this.type) {
            throw new InvalidTypeException("another has different type with the Graph(" + getTypeString() + ")");
        }

        adjLists.concate(another.getAdjLists());
        for (Pair<Bag<Pair<EdgeT, Object>>, VerT> v : another.getAdjLists()) {
            vertexMap.put(v.second, another.getVertexNodeByValue(v.second));
        }

        verNum += another.verNum;
        edgeNum += another.edgeNum;

        return this;
    }

    /**
     * get the adjLists, **be careful to use it!**
     * @return
     *      the adjLists
     */
    DoubleLinkedList<Pair<Bag<Pair<EdgeT, Object>>, VerT>> getAdjLists() {
        return adjLists;
    }

    /**
     * get edges count
     * @return
     *          count of edges
     */
    public int getEdgeNum() {
        return edgeNum;
    }

    /**
     * get vertex count
     * @return
     *          count of vertex
     */
    public int getVerNum() {
        return verNum;
    }

    /**
     * get graph type
     * @return
     *          the type of graph, UDG/DG/UDN/DN
     */
    public GraphType getType() {
        return type;
    }

    /**
     * return the string indicates the type of graph
     * @return
     *      the type information string
     */
    String getTypeString() {
        switch(type) {
            case DG: return "DG";
            case DN: return "DN";
            case UDG: return "UDG";
            case UDN: return "UDN";
            default:
                return "Error!";
        }
    }

    /**
     * get the vertex node from HashMap by vertex value
     *
     * @param value
     *          vertex value, **all the values of vertex must be different!!!**
     * @return
     *          null if not found else return the LinkedList node of vertex.
     */
    DoubleLinkedList<Pair<Bag<Pair<EdgeT, Object>>, VerT>>.Node getVertexNodeByValue(VerT value) {
        return vertexMap.get(value);
    }
}

/**
 * class comment : Generic Type Stack(LIFO)
 * 使用(可变长度resizing-capacity)数组实现.
 * 但是更换数组长度的时候会产生时间消耗(不过至少比fixed-capacity array好一点)
 * 一个更好的实现就是LinkedList(SLL/DLL).
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
class Stack<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前Stack中的元素个数
    private int size = 0;
    //默认最大容量
    private int MAXSIZE = 1000;
    /* reseizing-capacity stack using array implement */
    private Item[] elements;

    /*
     **************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    @SuppressWarnings("unchecked")
    Stack() {
        elements = (Item[]) new Object[MAXSIZE];
    }

    /**
     * 重载的构造器, 指定Stack容量
     * @param capacity
     *			Stack的容量
     */
    @SuppressWarnings("unchecked")
    Stack(int capacity) {
        elements = (Item[]) new Object[capacity];
        this.MAXSIZE = capacity;
    }

    /*
     **************************************
     * Inner Class                        *
     **************************************/
    /**
     * 成员内部类
     * 用于遍历这个Stack
     */
    private class ReverseArrayIterator implements Iterator<Item> {
        //用于遍历的时候存储当前遍历的序列, 还没有遍历过的时候默认值为-1
        private int iterateoffset = -1;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return Stack中上个被遍历的元素下面还有元素就返回true
         */
        @Override
        public boolean hasNext() {
            //第一次调用, 把iterateOffset设为当前Stack中的元素数量
            if (iterateoffset == -1) {
                iterateoffset = size;
            }

            return iterateoffset > 0;
        }

        /**
         * 遍历iterateOffset下面的所有元素
         * @return 下一个元素的值
         */
        @Override
        public Item next() {
            if (hasNext())
                return elements[--iterateoffset];
            else
                return null;
        }

    }

    /*
     **************************************
     * Methods     		                  *
     **************************************/
    /**
     * 向Stack中添加新的元素
     * @param item 新元素
     */
    public void push(Item item) {
        //如果Stack小于MAXSIZE * 0.8, 就正常添加, 否则就resize到MAXSIZE * 2.
        //原书是到了MAXSIZE才加倍到MAXSIZE * 2
        if(getSize() >= MAXSIZE * 4 / 5)
            resize(MAXSIZE * 2);

        elements[size++] = item;
    }

    /**
     * 从Stack中取出最后一个添加到Stack的元素, 并把这个元素从Stack中删除, 这里会把元素强制向下转型
     * @return 最后一个添加到Stack的元素
     */
    @SuppressWarnings("unchecked")
    public Item pop() {
        //如果size等于Stack的1/4就resize到MAXSIZE / 2
        if (isEmpty())
            return null;
        else if (size > 0 && size == MAXSIZE / 4)
            resize(MAXSIZE / 2);

        Object tmp = elements[size - 1];
        elements[--size] = null;
        return (Item)tmp;
    }

    /**
     * 从Stack中取出最后一个添加到Stack的元素, 并且不会把这个元素从Stack中删除, 这里会把元素强制向下转型
     * @return 最后一个添加到Stack的元素
     */
    @SuppressWarnings("unchecked")
    public Item peek() {
        //如果size等于Stack的1/4就resize到MAXSIZE / 2
        if (isEmpty())
            return null;
        else if (size > 0 && size == MAXSIZE / 4)
            resize(MAXSIZE / 2);

        return (Item)elements[size - 1];
    }

    /**
     * 获得当前Stack存储了多少个元素
     * @return 当前Stack存储的多少个元素
     */
    public int getSize() {
        return size;
    }

    /**
     * 判断Stack是否是空的
     * @return 判断Stack是否是空的
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 判断Stack是否已满, resizing-capacity array的实现方案直接都返回true
     * @return 判断Stack是否已满
     */
    public boolean isFull() {
        //这是原来fixed-capacity array实现的stack的方案
        //return getSize() >= MAXSIZE;

        //新的resizing-capacity array实现的stack, 直接返回false.
        return false;
    }



    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        return this.new ReverseArrayIterator();
    }

    /**
     * 更新array的大小, 使用原子操作, 防止在更换的时候, stack出现异常
     * @param newCapacity 新的array的大小
     * @return 原来stack的MAXSIZE
     */
    @SuppressWarnings("unchecked")
    private int resize(int newCapacity) {
        //注意: 这里不会检查size是否大于newCapacity
        synchronized (elements) {
            Item[] newElements = (Item[]) new Object[newCapacity];
            int cnt = 0;
            for (Item i : elements)
                newElements[cnt++] = i;

            //把elements交接到新的array对象
            elements = newElements;

            //更新MAXSIZE大小
            int oldMAXSIZE = this.MAXSIZE;
            this.MAXSIZE = newCapacity;
            return oldMAXSIZE;
        }
    }

    /**
     * Test Client.
     * @param args
     *			command-line arguments.
     */
    public static void main(String[] args) {
        //foreach遍历测试
        System.out.println("foreach遍历测试");
        Stack<Integer> stack = new Stack<>(4);
        stack.push(5);
        stack.push(2);
        stack.push(1);
        stack.push(7);

        //遍历
        for (int i : stack) {
            System.out.println(i);
        }


        /* Dijkstra双栈算术表达式求值算法 */
        System.out.println("输入一行算术表达式, 空行回车或者Ctrl+Z结束本程序.");

        Stack<String> ops = new Stack<>();
        Stack<Double> vals = new Stack<>();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            //nextLine会读取回车, 但是回车的内容不会成为返回的字符串的一部分.
            String line = sc.nextLine();

            if (line.equals("")) {
                System.out.println("End of Reading.");
                return;
            } else {
                //从这一行中读取
                Scanner scInLine = new Scanner(line);

                /* 算术表达式示例: ( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) ) */
                while (scInLine.hasNext()) {
                    //读取字符串
                    String str = scInLine.next();

                    switch (str) {
                        //忽略左括号
                        case "(" 	:  break;
                        case "+" 	:
                        case "-" 	:
                        case "*" 	:
                        case "/" 	:
                        case "sqrt" : ops.push(str);
                            break;
                        //如果是右括号的话, 就执行操作
                        case ")" 	:
                            String operator = ops.pop();
                            Double val = vals.pop();

                            //计算并将结果压回Stack
                            switch (operator) {
                                case "+" : vals.push(vals.pop() + val);
                                    break;
                                case "-" : vals.push(vals.pop() - val);
                                    break;
                                case "*" : vals.push(vals.pop() * val);
                                    break;
                                case "/" : vals.push(vals.pop() / val);
                                    break;
                                case "sqrt" : vals.push(Math.sqrt(val));
                                    break;
                            }
                            break;
                        //如果既不是运算符也不是括号, 就是操作数了
                        default : vals.push(Double.parseDouble(str));
                    }
                }

                //输出结果
                System.out.println("上述算术表达式的值为" + vals.pop());
                System.out.println("输入一行算术表达式, 空行回车或者Ctrl+Z结束本程序.");
            }
        }

    }
}

/**
 * Dragon Book 2nd
 *
 * Regex expression to NFA, NFA to DFA and minimize DFA
 *
 * A Automata is a tuple with 5 elements: M = {S, \Sigma, f, s_0, Z} where S is set of all the states,
 * \Sigma is the vocabulary(a.k.a alphabet), f is the map functions in automata, s_0 is the start state,
 * Z is the set of finish states.
 *
 * TODO using HashMap to speed up the get by index in LinkedList from O(n) to O(1)
 * TODO , alternative using resized-array to replace LinkedList(although its time complexity
 * TODO large than the former
 *
 * @since 1.8
 * @author DCMMC
 * @code UTF-8
 */
public class Reg2Automata {
    /**
     * execute different operation for different operators
     * @param operator
     *          operator: &, *, |
     * @param operands
     *          operands Graph
     * @param ptStateCnt
     *          pointer to state count
     */
    private static void op(Character operator, Stack<Graph<Integer, String>> operands, Pointer<Integer> ptStateCnt) {
        try {
            switch (operator) {
                case '|':
                    operands.push(OpOr(operands.pop(), operands.pop(), ptStateCnt));
                    break;
                case '&':
                    operands.push(OpAnd(operands.pop(), operands.pop()));
                    break;
                case '*':
                    operands.push(OpClosure(operands.pop(), ptStateCnt));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * use epsilon expression to concatenate two NFAs
     *
     * @param NFA1
     *          left operand, @notEmpty
     * @param NFA2
     *          right operand, @notEmpty
     * @param ptStateCnt
     *          pointer of state count
     * @return
     *          NFA = NFA1 | NFA2, and NFA use the object of NFA1, NFA2 now is useless.
     */
    private static Graph<Integer, String> OpOr(Graph<Integer, String> NFA2,
                                                              Graph<Integer, String> NFA1,
                                               Pointer<Integer> ptStateCnt) throws Exception {
        // check
        if (NFA1.getVerNum() < 2 || NFA2.getVerNum() < 2) {
            throw new Exception("NFA1 and NFA2's vertex number must large than or equal to 2!");
        }

        NFA1.addVerFirst(ptStateCnt.item++);
        NFA2.addVer(ptStateCnt.item++);
        try {
            // null indicates epsilon expression
            int oldLastIndex = NFA1.getVerNum() - 1;
            NFA1.concate(NFA2)
                    .addEdge2Ver(0, new Pair<>(null, 1))
                    .addEdge2Ver(0, new Pair<>(null, oldLastIndex + 1))
                    .addEdge2Ver(oldLastIndex, new Pair<>(null, NFA1.getVerNum() - 1))
                    .addEdge2Ver(NFA1.getVerNum() - 2, new Pair<>(null, NFA1.getVerNum() - 1));
        } catch (InvalidTypeException ite) {
            throw new RuntimeException("incompatible NFA2 graph type!");
        }

        return NFA1;
    }

    /**
     * concatenate two NFAs
     * @param NFA1
     *      left operand, @notEmpty
     * @param NFA2
     *      right operand, @notEmpty
     * @return
     *      NFA = (NFA1)(NFA2), and NFA use the object of NFA1, NFA2 now is useless.
     */
    private static Graph<Integer, String> OpAnd(Graph<Integer, String> NFA2,
                                                Graph<Integer, String> NFA1) throws Exception {
        // check
        if (NFA1.getVerNum() < 2 || NFA2.getVerNum() < 2) {
            throw new Exception("NFA1 and NFA2's vertex number must large than or equal to 2!");
        }

        try {
            int oldLastIndex = NFA1.getVerNum() - 1;
            // null indicate epsilon expression
            // according to McNaughton-Yamada-Thompson Algorithms, there is should not use epsilon expression to
            // concatenate two NFAs, just let last vertex of NFA1 combine with the first vertex of NFA2
            // but remove operation will influence stateCount and I don't want to handle it.
            // so there is a bit inconsistent with McNaughton-Yamada-Thompson Algorithms
            NFA1.concate(NFA2)
                    .addEdge2Ver(oldLastIndex, new Pair<>(null, oldLastIndex + 1));
        } catch (InvalidTypeException ite) {
            throw new RuntimeException("incompatible NFA2 graph type!");
        }

        return NFA1;
    }

    /**
     * closure operation of a NFA
     * @param NFA
     *      NFA to be operated.
     * @return
     *      the new NFA
     * @param ptStateCnt
     *          pointer of state count
     * @throws Exception
     *      if NFA's vertex number less than 2
     */
    private static Graph<Integer, String> OpClosure(Graph<Integer, String> NFA,
                                                    Pointer<Integer> ptStateCnt) throws Exception {
        // check
        if (NFA.getVerNum() < 2) {
            throw new Exception("NFA's vertex number must large than or equal to 2!");
        }

        // null indicates epsilon expression
        NFA.addVerFirst(ptStateCnt.item++)
                .addVer(ptStateCnt.item++)
                .addEdge2Ver(0, new Pair<>(null, 1))
                .addEdge2Ver(0, new Pair<>(null, NFA.getVerNum() - 1))
                .addEdge2Ver(NFA.getVerNum() - 2, new Pair<>(null, 1))
                .addEdge2Ver(NFA.getVerNum() - 2, new Pair<>(null, NFA.getVerNum() - 1));

        return NFA;
    }


    /**
     * Basic Rule of McNaughton-Yamada-Thompson Algorithms
     *
     * @param ptStateCnt
     *          pointer of state count
     * @param operands
     *          Stack of operands
     * @param alphabet
     *          alphabet \Sigma
     */
    private static void basicRule(Pointer<Integer> ptStateCnt,
                                  Stack<Graph<Integer, String>> operands,
                                  String str, Set<String> alphabet) {
        // debug
        // System.out.println(str);

        Graph<Integer, String> NFA = new Graph<>(Graph.GraphType.DN);
        NFA.addVer(ptStateCnt.item++)
                .addVer(ptStateCnt.item++)
                .addEdge2Ver(0, new Pair<>(str, 1));
        operands.push(NFA);
        alphabet.add(str);
    }

    /**
     * P152 3.7.4 Algorithm 3.23
     *
     * Regex expression to NFA using McNaughton-Yamada-Thompson Algorithms (i.e. Thompson 法)
     *
     * Basic regex expression, only have concatenation, or, closure operations and parentheses.
     *
     * operation priorities: left parentheses > *(left-associative) > concatenation(left-associative)
     * > |(left-associative) > right parentheses
     *
     * @param regExp
     *          regex expression, all elements should in alphabet \Sigma
     * @return
     *          the e-NFA constructed by regExp using McNaughton-Yamada-Thompson Algorithms, the graph only has one
     *          start state and one final state.
     */
    static Pair<Graph<Integer, String>, Set<String>> reg2NFA(String regExp) {
        // *basic rule* for handling subexpressions with no operators: two situation, state i using epsilon-expression
        // to state f, and state i using subexpression a to state f.
        //
        // *induction rule* for constructing large NFAs from many small NFAs connected by operators.
        // to avoid conflicts between a NFA to another, we should use epsilon expression carefully in the junction(交界处)
        // of two sub NFAs using the rules McNaughton-Yamada-Thompson uses.

        // Dijkstra's two stack expression evaluation(or parse a syntax tree)
        Stack<Graph<Integer, String>> operands = new Stack<>();
        Stack<Character> ops = new Stack<>();

        final char or = '|';
        final char closure = '*';
        final char openingBracket = '(';
        final char closingBracket = ')';
        // in fact, and operator has no character representation
        final char and = '&';
        // alphabet or vocabulary
        final String alphabetReg = ".";

        // operator priorities
        final HashMap<Character, Integer> priorities = new HashMap<>();
        priorities.put('|', 1);
        // in fact, and operator has no character representation
        priorities.put('&', 2);
        priorities.put('*', 3);
        // priority that can ignore
        priorities.put('(', 0);

        // count the number of states in the NFA
        Integer stateCount = 0;
        Pointer<Integer> ptStateCnt = new Pointer<>(stateCount);

        // according to McNaughton-Yamada-Thompson Algorithms in Dragon Book,
        // element with no operators is single character.
        Set<String> alphabet = new HashSet<>();

        try {
            Character last = null;
            for (char c : regExp.toCharArray()) {
                switch (c) {
                    case openingBracket:
                        if (last != null && last != or) {
                            // and operator
                            ops.push(and);
                        }
                        last = c;
                        ops.push(c);
                        break;
                    case closure:
                        last = c;
                        ops.push(c);
                        op(closure, operands, ptStateCnt);
                        while (!ops.isEmpty() && priorities.get(ops.peek()) >= priorities.get(c)) {
                            // in the case, ops.pop() is only '*'
                            op(ops.pop(), operands, ptStateCnt);
                        }
                        break;
                    case or:
                        last = c;
                        while (!ops.isEmpty() && priorities.get(ops.peek()) >= priorities.get(c)) {
                            // &, *, |
                            op(ops.pop(), operands, ptStateCnt);
                        }
                        ops.push(c);
                        break;
                    case closingBracket:
                        last = c;
                        // execute operation until encounter '('
                        while (ops.peek() != '(') {
                            // &, *, |
                            op(ops.pop(), operands, ptStateCnt);
                        }
                        // '('
                        ops.pop();
                        break;
                    default:
                        basicRule(ptStateCnt, operands, Character.toString(c), alphabet);
                        if (last != null && last != openingBracket && last != or) {
                            // and operator
                            ops.push('&');
                        }
                        last = c;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 最后相当于还有一个 ')'
        while (!ops.isEmpty()) {
            op(ops.pop(), operands, ptStateCnt);
        }

        return new Pair<>(operands.pop(), alphabet);
    }

    /**
     * P154
     * ε-closure(T) using Stack(alternative using DFS)
     *
     * @param N
     *          NFA graph
     * @param T
     *          set of states in NFA
     * @return
     *          set of states in NFA after several ε-transitions
     */
    @SuppressWarnings("unchecked")
    private static Bag<Integer> eClosure(Graph<Integer, String> N,
                                 Bag<Integer> T) {
        Bag<Integer> eClosureBag = new Bag<>();
        if (T.isEmpty())
            return new Bag<>();

        Stack<Integer> stack = new Stack<>();
        // initialize e-closure(T) to T
        for (Integer i : T) {
            eClosureBag.add(i);
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            Integer i = stack.pop();
            for (Pair<String, Object> e : N.getVertexNodeByValue(i).item.first) {
                if (e.first == null && !eClosureBag.contain(
                        ((Pair<Bag<Pair<String, Object>>, Integer>)e.second).second)) {
                    Integer u = ((Pair<Bag<Pair<String, Object>>, Integer>)e.second).second;
                    eClosureBag.add(u);
                    stack.push(u);
                }
            }
        }

        return eClosureBag;
    }

    /**
     * P152 3.7.1, Algorithm 3.20
     *
     * convert e-NFA to DFA using subset construction
     *
     * Theorem: any NFA one alphabet \Sigma has at least one equality DFA on alphabet \Sigma
     *
     * @param NandAlphabet
     *          an NFA
     *          and
     *          alphabet \Sigma
     * @param newFinals
     *          set of final states in DFA
     * @return
     *          an DFA equal to the NFA(accept the same language as NFA N)
     */
    @SuppressWarnings("unchecked")
    static Pair<Graph<Integer, String>, Set<String>> NFA2DFA(
            Pair<Graph<Integer, String>, Set<String>> NandAlphabet,
            Bag<Integer> newFinals) {
        Graph<Integer, String> N = NandAlphabet.first;
        Set<String> alphabet = NandAlphabet.second;

        // Dtran is the DFA's transition table whose row is set of NFA states(NFA state) and col is
        // each element in alphabet
        // three operations:
        // 1. ε-closure(s) return the set of NFA states reachable from NFA state s on ε-transitions alone.
        // 2. ε-closure(T) return \cup_{s in T} ε-closure(s) where T is a set of NFA states.
        // 3. move(T, a) return the set of NFA states to which there is a transition on input symbol a from
        // some state s in T.
        //
        // The DFA's start state is ε-closure(s_0). suppose that N can be in set of states T after reading input
        // string x. if it next reads input a, then N could be in any state of ε-closure(move(T, a))
        // the Bag<Integer> is the set of state in NFA in order(e.g. {1 5 6 7})
        // **must use LinkedHashMap to store the order of D-states, because the first ket of Map is the start
        // state**, but it will use more space to keep a LinkedList.
        LinkedHashMap<Bag<Integer>, HashMap<String, Bag<Integer>>> Dtrans = new LinkedHashMap<>();

        // Dstates
        // initially, e-closure(s_0) is the only in Dstates(i.e. Dtrans' row element)
        HashSet<Bag<Integer>> Dstates = new HashSet<>();
        Dstates.add(eClosure(N, new Bag<>(N.getAdjLists().getFirst().second)));
        while (!Dstates.isEmpty()) {
            Bag<Integer> t =  Dstates.iterator().next();
            Dstates.remove(t);
            if (Dtrans.containsKey(t)) {
                continue;
            }

            Dtrans.put(t, new HashMap<>());
            for (String s : alphabet) {
                // move(T, s)
                Bag<Integer> moveTs = new Bag<>();
                for (Integer i : t) {
                    for (Pair<String, Object> e : N.getVertexNodeByValue(i).item.first) {
                        if (e.first != null && e.first.equals(s)) {
                            moveTs.add(((Pair<Bag<Pair<String, Object>>, Integer>)e.second).second);
                        }
                    }
                }

                moveTs = eClosure(N, moveTs);

                if (!moveTs.isEmpty() && !Dstates.contains(moveTs)) {
                    Dstates.add(moveTs);
                }
                // Dtrans[T, s] = moveTs
                Dtrans.get(t).put(s, moveTs);
            }
        }

        //construct DFA Graph from Dtrans
        Integer[] vers = Arrays.stream(IntStream.range(0, Dtrans.size()).toArray()).boxed()
                .toArray(Integer[]::new);
        int index = 0;
        // a map with set of NFA states to DFA state
        LinkedHashMap<Bag<Integer>, Integer> dfaStateMap = new LinkedHashMap<>();
        // final state in NFA
        Integer finalStateNFA = N.getAdjLists().getLastNode().item.second;

        for (Bag<Integer> i : Dtrans.keySet()) {
            dfaStateMap.put(i, index);
            if (i.contain(finalStateNFA))
                newFinals.add(index);
            index++;
        }

        String[][] edges = new String[vers.length][vers.length];
        for (Bag<Integer> i : Dtrans.keySet()) {
            HashMap<String, Bag<Integer>> toStates = Dtrans.get(i);
            for (String s : alphabet) {
                // if toStates.get(s) is not empty state
                if (toStates.get(s).getSize() > 0)
                    edges[dfaStateMap.get(i)][dfaStateMap.get(toStates.get(s))] = s;
            }
        }

        return new Pair<>(new Graph<>(Graph.GraphType.DN, vers, edges, null), alphabet);
    }

    /**
     * minimize the DFA
     *
     * in worst cases, the algorithm will cost many space and time.
     *
     * Ref: https://en.wikipedia.org/wiki/DFA_minimization
     *
     * TODO 把中文注释改成英文
     * TODO 改进这个 minimize DFA 算法, 根据 Ref, 我觉得现在这个实现的效率太低了
     *
     * @param DFAandAlphabet
     *          DFA and alphabet
     * @param finalsBag
     *          set of final states in DFA
     * @return
     *          the DFA minimized and alphabet
     */
    @SuppressWarnings("unchecked")
    static Pair<Graph<Integer, Set<String>>, Set<String>> minimizeDFA(
            Pair<Graph<Integer, String>, Set<String>> DFAandAlphabet, Pointer<Bag<Integer>> finalsBag) {
        Graph<Integer, String> DFA = DFAandAlphabet.first;
        Set<String> alphabet = DFAandAlphabet.second;
        // set of states exclude unreachable states
        List<Integer> states = DFA.dfsTraverse(0);

        // divide states into non-final states and final-states
        DoubleLinkedList<HashSet<Integer>> statesList = new DoubleLinkedList<>();
        HashSet<Integer> nonFinals = new HashSet<>(), finals = new HashSet<>();
        for (Integer i : states) {
            if (finalsBag.item.contain(i))
                finals.add(i);
            else
                nonFinals.add(i);
        }
        statesList.addFirst(nonFinals)
            .addFirst(finals);

        // supposed there are two states (a set of DFA states represents one minimized-DFA state)
        // and we iterate all the states in all the sets, if there are two states a, b in same states set T_i,
        // if input any symbol s from alphabet \Sigma, f(a, s) = T_j but f(b, s) = T_k, a and b go to different
        // states set, so a and b are not equality states, they should be in different sets.
        // loop the procedure until all elements in same set is equality.
        //
        // 总的来说就是把划分为非终结和终结状态这两个集合, 然后再根据状态是否等价继续划分, 直到没有可划分的了
        // FIXME !!!heavy time complexity!!!
        Outer:
        while (true) {
            // 标记是否所有集合, 集合里面的元素都是完全相等了
            // boolean end = true;

            for (HashSet<Integer> l : statesList) {
                // in the end of loop, all the values are one element
                // inner map 必须是 Linked 的, 因为要保存顺序
                HashMap<String, LinkedHashMap<Integer, HashSet<Integer>>> map = new HashMap<>();
                for (String s : alphabet) {
                    for (Integer v : l) {
                        // !!!注意: 空集也是一种状态!!!, 两个状态等价必须还满足经过同一 input symbol 如果有一个是空集那么另外一个也一定是空集!
                        boolean emptyState = true;

                        // 查找这一状态集合中所有状态通过 input symbol s 能够到达的状态集合
                        for (Pair<String, Object> edge :
                                (DFA.getVertexNodeByValue(v).item).first) {
                            // 状态 v 能够通过 s 到达新状态
                            if (edge.first.equals(s)) {
                                emptyState = false;
                                int index = 0;
                                // 遍历判断这一个状态在哪个状态集合里面
                                for (HashSet<Integer> ll : statesList) {
                                    Pair<Bag<Pair<String, Object>>, Integer> node =
                                            (Pair<Bag<Pair<String, Object>>, Integer>)
                                                    (edge.second);
                                    // 如果找到了所在的状态集合
                                    if (ll.contains(node.second)) {
                                        // 判断是否是第一次处理 s 这个 input symbol
                                        if (!map.containsKey(s))
                                            map.put(s, new LinkedHashMap<>());
                                        // 是否是状态第一次达到这个状态集合
                                        if (!map.get(s).containsKey(index))
                                            map.get(s).put(index, new HashSet<>());
                                        // 在 map 指定 s 中的指定状态集合索引中放入这一个状态号
                                        map.get(s).get(index).add(v);
                                        break;
                                    }
                                    index++;
                                }
                            }
                        }

                        // 如果是空集, 那就创建一个空的 HashSet
                        if (emptyState) {
                            // 判断是否是第一次处理 s 这个 input symbol
                            if (!map.containsKey(s))
                                map.put(s, new LinkedHashMap<>());
                            // 空集用序号 statesList.getSize() 表示
                            // 是否是状态第一次达到这个状态集合
                            if (!map.get(s).containsKey(statesList.getSize()))
                                map.get(s).put(statesList.getSize(), new HashSet<>());
                            // 在 map 指定 s 中的空状态集合索引中放入这一个状态号
                            map.get(s).get(statesList.getSize()).add(v);
                        }
                    }
                    // 如果这一状态集合中所有状态通过 input symbol s 能够到达的状态集合有多个, 就继续将这一状态集合 l 分区
                    if (map.containsKey(s) && map.get(s).size() > 1) {
                        // end = false;
                        // 先 remove 掉
                        statesList.remove(statesList.getOffsetByValue(l));
                        for (HashSet<Integer> set : map.get(s).values()) {
                            // 添加到后面去, 这样可以下次先遍历原来没遍历到的
                            statesList.addLast(set);
                        }

                        // 后面的 input symbol s 也没必要去做了
                        // 因为 statesList 更改了, 所以要 break 到外面去
                        continue Outer;
                    }
                }
            }

            break;
        }

        // 从 statesList 生成 minimized-DFA
        // (set of states in DFA) <==> (new state in minimized-DFA)
        LinkedHashMap<HashSet<Integer>, Integer> newStates = new LinkedHashMap<>();
        // edge matrix
        HashSet<String>[][] edges = new HashSet[statesList.getSize()][statesList.getSize()];
        // move states Set contains s_0 to the first of list
        Integer s0 = DFA.getAdjLists().getFirstNode().item.second;
        int index = 0;
        End:
        for (HashSet<Integer> statesSet : statesList) {
            for (Integer i : statesSet) {
                if (i.equals(s0)) {
                    statesList.remove(index);
                    statesList.addFirst(statesSet);
                    break End;
                }
            }
            index++;
        }

        // 终结状态集合
        HashSet<Integer> newFinals = new HashSet<>();
        index = 0;
        for (HashSet<Integer> statesSet : statesList) {
            newStates.put(statesSet, index);
            for (Integer i : finalsBag.item) {
                if (statesSet.contains(i)) {
                    newFinals.add(index);
                }
            }
            index++;
        }
        // 替换 finals 中的内容
        Bag<Integer> newFinalsBag = new Bag<>();
        for (Integer i : newFinals)
            newFinalsBag.add(i);
        finalsBag.item = newFinalsBag;

        for (HashSet<Integer> fromStates : statesList) {
            for (String symbol : alphabet) {
                // fromStates 能够通过 symbol 到达的所有状态的集合(肯定是在 statesList 里面)
                HashSet<Integer> toStates = new HashSet<>();
                for (Integer s : fromStates) {
                    // 遍历状态 s 所连接的所有边, 看是否有能够输入 symbol 后到达的状态
                    for (Pair<String, Object> to : DFA.getVertexNodeByValue(s).item.first) {
                        if (to.first.equals(symbol)) {
                            // 说明通过输入符号 symbol 后 s 状态能够到达 to 状态
                            toStates.add(((Pair<Bag<Pair<String, Object>>, Integer>)(to.second)).second);
                        }
                    }
                }
                if (toStates.isEmpty())
                    continue;

                // toStates 可能只是 statesList 中的某个集合的子集
                Outer2:
                for (HashSet<Integer> sSet : statesList) {
                    for (Integer state : sSet) {
                        if (toStates.contains(state)) {
                            toStates = sSet;
                            break Outer2;
                        }
                    }
                }

                if (edges[newStates.get(fromStates)][newStates.get(toStates)] == null) {
                    edges[newStates.get(fromStates)][newStates.get(toStates)] = new HashSet<>();
                }
                edges[newStates.get(fromStates)][newStates.get(toStates)].add(symbol);
            }
        }

        // new states
        Integer[] vers = Arrays.stream(IntStream.range(0, statesList.getSize()).toArray()).boxed()
                .toArray(Integer[]::new);

        return new Pair<>(new Graph<>(Graph.GraphType.DN, vers, edges, null), alphabet);
    }

    /**
     * simulate a DFA generated by NFA2DFA
     * @param s
     *      string
     * @param regExp
     *      regex expression
     * @return
     *      true if s match the L(NFA) (i.e. the language of NFA)
     */
    @SuppressWarnings("unchecked")
    static Boolean simulateDFA(String s, String regExp) {
        Bag<Integer> finals = new Bag<>();
        Graph<Integer, String> dfa = NFA2DFA(reg2NFA(regExp), finals).first;

        // 从状态 0 (i.e., s_0) 开始
        Integer state = 0;
        Outer:
        for (char c : s.toCharArray()) {
            for (Pair<String, Object> to : dfa.getVertexNodeByValue(state).item.first) {
                if (to.first.charAt(0) == c) {
                    state = ((Pair<Bag<Pair<String, Object>>, Integer>)
                                (to.second)).second;
                    continue Outer;
                }
            }

            // 如果所有输入符号都不通过, 那肯定是该字符串不匹配正则表达式
            return false;
        }

        // 如果遍历结束了, 检查下 state 是否在 finals 里面
        return finals.contain(state);
    }

    /**
     * draw the NFA using graphviz
     * @param FA
     *          NFA graph that need to draw
     * @param fileName
     *          file name in graph directory
     * @param finals
     *          set of final states, if e-NFA genrated by Thomposon's Algorithm then finals is null
     * @param format
     *          format of output file
     * @throws
     *          if the type of EdgeT is neither String nor HashSet
     */
    @SuppressWarnings("unchecked")
    static <EdgeT> void graphvizDraw(Graph<Integer, EdgeT> FA, String fileName, Bag<Integer> finals,
                                     Format format)
            throws Exception {
        // **Be careful: all elements(like Graph and Node) of graphviz is immutable!!**
        guru.nidi.graphviz.model.Graph g = graph("FA").directed();

        if (finals == null)
            finals = new Bag<>(FA.getAdjLists().getLastNode().item.second);

        // start state
        Node[] nodes = new Node[FA.getVerNum() + 1];

        nodes[0] = node("start").with(Shape.NONE);
        for (Pair<Bag<Pair<EdgeT, Object>>, Integer> nodeAdjList : FA.getAdjLists()) {
            // the index of node is its vertex node's value(i.e. state number)
            nodes[nodeAdjList.second + 1] = node(nodeAdjList.second.toString()).with(Shape.CIRCLE);
        }
        // start state
        nodes[0] = nodes[0].link(to(nodes[FA.getAdjLists().getFirst().second + 1]));
        // 因为 0 状态没有任何指向他的, 所以如果 0 就在 finals 里面, 需要手动处理
        if (finals.contain(0)) {
            nodes[1] = nodes[1].with(Shape.DOUBLE_CIRCLE,
                    Color.RED);
        }

        for (Pair<Bag<Pair<EdgeT, Object>>, Integer> nodeAdjList : FA.getAdjLists()) {
            ArrayList<Link> links = new ArrayList<>();
            int j = 0;
            for (Pair<EdgeT, Object> edge : nodeAdjList.first) {
                Pair<Bag<Pair<String, Object>>, Integer> vertexNode =
                        (Pair<Bag<Pair<String, Object>>, Integer>) (edge.second);
                // check if is finish state
                if (finals.contain(vertexNode.second)) {
                    nodes[vertexNode.second + 1] = nodes[vertexNode.second + 1].with(Shape.DOUBLE_CIRCLE,
                            Color.RED);
                }

                if (edge.first == null) {
                    // epsilon expression uses dashed line
                    links.add( to(nodes[vertexNode.second + 1])
                            .with(Label.of("ε"), Style.DASHED));
                } else {
                    if (String.class.isInstance(edge.first)) {
                        // EdgeT is String, DFA/NFA
                        links.add(to(nodes[vertexNode.second + 1])
                                .with(Label.of((String) (edge.first))));
                    } else if (HashSet.class.isInstance(edge.first)) {
                        // EdgeT is HashSet, **but the generic type of HashSet must be String!!!**
                        HashSet<String> set = (HashSet<String>) (edge.first);
                        for (String s : set) {
                            links.add(to(nodes[vertexNode.second + 1])
                                    .with(Label.of(s)));
                        }
                    } else {
                        // ERROR type of EdgeT
                        throw new Exception("ERROR type of EdgeT");
                    }

                }
            }
            nodes[nodeAdjList.second + 1] =  nodes[nodeAdjList.second + 1].link(links.toArray(new Link[0]));
        }
        g = g.with(nodes).graphAttr().with(RankDir.LEFT_TO_RIGHT);

        URL path = Reg2Automata.class.getClassLoader().getResource(".");
        try {
            if (path != null) {
                // if use Windows, path.getPath will start with '/' incorrectly.
                Files.createDirectories(Paths.get(path.getPath().charAt(0) == '/' ?
                        path.getPath().substring(1) : path.getPath(), "graphs"));
                System.out.println("Output file path: " +
                        new File("graphs" + File.separator
                        + fileName).getAbsolutePath());
                Graphviz.fromGraph(g).width(1000).render(format).toFile(new File("graphs"
                        + File.separator
                        + fileName));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // graphvizDraw(reg2NFA("(sub_exp)|(sub_exp)(sub_exp)|(sub_exp)((sub_exp)*)*"));
        // graphvizDraw(reg2NFA("(abc|bd)|e*|((a*)bb*|(kk|d))*").first);
        // graphvizDraw(reg2NFA("(a|b)*a(a|b)(aa|bb)"));

        // graphvizDraw(reg2NFA("a|b").first, "NFA.svg");
        // graphvizDraw(NFA2DFA(reg2NFA("a|b")), "DFA.svg");

//        Bag<Integer> newFinals = new Bag<>();
//        graphvizDraw(reg2NFA("((a)(b))*(a*|b*)((b)(a))*").first, "NFA.svg", null);
//        graphvizDraw(NFA2DFA(reg2NFA("((a)(b))*(a*|b*)((b)(a))*"), newFinals).first, "DFA.svg", newFinals);
//        newFinals = new Bag<>();
//        minimizeDFA(NFA2DFA(reg2NFA("((a)(b))*(a*|b*)((b)(a))*"), newFinals), newFinals);

//        try {
//            Bag<Integer> newFinals = new Bag<>();
//            graphvizDraw(reg2NFA("(a)|(a((b)|((c)(c)*)))").first, "NFA.svg",
//                    null);
//            graphvizDraw(NFA2DFA(reg2NFA("(a)|(a((b)|((c)(c)*)))"), newFinals).first,
//                    "DFA.svg", newFinals);
//            Pointer<Bag<Integer>> ptFinals = new Pointer<>(newFinals);
//            graphvizDraw(minimizeDFA(NFA2DFA(reg2NFA("(a)|(a((b)|((c)(c)*)))"),
//                    newFinals), ptFinals).first, "minimized-DFA.svg", ptFinals.item);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String testReg1 = "a|b";
        try {
            Bag<Integer> newFinals = new Bag<>();
            graphvizDraw(reg2NFA(testReg1).first, "NFA.png",
                    null, Format.SVG);
            graphvizDraw(NFA2DFA(reg2NFA(testReg1), newFinals).first,
                    "DFA.png", newFinals, Format.SVG);
            Pointer<Bag<Integer>> ptFinals = new Pointer<>(newFinals);
            graphvizDraw(minimizeDFA(NFA2DFA(reg2NFA(testReg1),
                    newFinals), ptFinals).first, "minimized-DFA.png", ptFinals.item, Format.SVG);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}///~
