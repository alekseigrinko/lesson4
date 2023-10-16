package ru.geekbrains.lesson4;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;

/**
 * Хэш-таблица
 * @param <K>
 * @param <V>
 */
public class HashMap<K, V> implements Iterable<HashMap.Entity> {


    @Override
    public Iterator<HashMap.Entity> iterator() {
        return new HashMapIterator();
    }

    class HashMapIterator implements Iterator<HashMap.Entity>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Entity next() {
            return null;
        }
    }

    /**
     * TODO: В минимальном варианте, распечатать все элементы хэш-таблицы
     * @return
     */
    @Override
    public String toString() {
        return super.toString();
    }

    //region Публичные методы

    /**
     * Добавление нового элемента в хэш-таблицу
     * @param key ключ
     * @param value значение
     * @return
     */
    public V put(K key, V value){
        if (buckets.length * LOAD_FACTOR <= size){
            recalculate();
        }
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null){
            bucket = new Bucket();
            buckets[index] = bucket;
        }

        Entity entity = new Entity();
        entity.key = key;
        entity.value = value;

        V buf = (V)bucket.add(entity);
        if (buf == null){
            size++;
        }
        return buf;
    }

    /**
     * Поиск значения в хэш-таблице по ключу
     * @param key ключ
     * @return значение
     */
    public V get(K key){
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null)
            return null;
        return (V)bucket.get(key);
    }

    /**
     * Удаление элемента из хэш-таблицы по ключу
     * @param key ключ
     * @return значение
     */
    public V remove(K key){
        int index = calculateBucketIndex(key);
        Bucket bucket = buckets[index];
        if (bucket == null)
            return null;
        V buf = (V)bucket.remove(key);
        if (buf != null){
            size--;
        }
        return buf;
    }

    //endregion

    //region Методы

    private void recalculate(){
        size = 0;
        Bucket<K, V>[] old = buckets;
        buckets = new Bucket[old.length * 2];
        for (int i = 0; i < old.length; i++){
            Bucket<K, V> bucket = old[i];
            if (bucket == null)
                continue;
            Bucket.Node node = bucket.head;
            while (node != null){
                put((K)node.value.key, (V)node.value.value);
                node = node.next;
            }
        }
    }

    private int calculateBucketIndex(K key){
        return Math.abs(key.hashCode()) % buckets.length;
    }

    //endregion

    //region Конструкторы

    public HashMap(){
        buckets = new Bucket[INIT_BUCKET_COUNT];
    }

    public HashMap(int capacity){
        buckets = new Bucket[capacity];
    }

    //endregion

    //region Поля

    private Bucket[] buckets;
    private int size; // Кол-во элементов

    //endregion

    //region Константы

    private static final int INIT_BUCKET_COUNT = 16;
    private static final double LOAD_FACTOR = 0.5;


    //endregion

    //region Вспомогательные структуры

    /**
     * Элемент хэш-таблицы
     */
    class Entity{

        /**
         * Ключ
         */
        K key;

        /**
         * Значение
         */
        V value;

    }

    /**
     * Связный список
     * @param <K>
     * @param <V>
     */
    class Bucket<K, V>{

        /**
         * Указатель на первый элемент связного списка
         */
        private Node head;

        /**
         * Узел связного списка
         */
        class Node{

            /**
             * Ссылка на следующий узел
             */
            Node next;

            /**
             * Значение узла
             */
            Entity value;

        }

        /**
         * Добавление нового элемента хэш-таблицы (на уровне связного списка)
         * @param entity элемент хэш-таблицы
         * @return значение старого элемента (если ключи совпадают)
         */
        public V add(Entity entity){
            Node node = new Node();
            node.value = entity;

            if (head == null){
                head = node;
                return null;
            }

            Node currentNode = head;
            while (true){
                if (currentNode.value.key.equals(entity.key)){
                    V buf = (V)currentNode.value.value;
                    currentNode.value.value = entity.value;
                    return buf;
                }
                if (currentNode.next != null){
                    currentNode = currentNode.next;
                }
                else{
                    currentNode.next = node;
                    return null;
                }
            }

        }

        public V get(K key){
            Node node = head;
            while (node != null){
                if (node.value.key.equals(key))
                    return (V)node.value.value;
                node = node.next;
            }
            return null;
        }

        public V remove(K key){
            if (head == null)
                return null;
            if (head.value.key.equals(key)){
                V buf = (V)head.value.value;
                head = head.next;
                return buf;
            }
            else {
                Node node = head;
                while (node.next != null){
                    if (node.next.value.key.equals(key)){
                        V buf = (V)node.next.value.value;
                        node.next = node.next.next;
                        return buf;
                    }
                    node = node.next;
                }
                return null;
            }
        }

    }

    //endregion

}
