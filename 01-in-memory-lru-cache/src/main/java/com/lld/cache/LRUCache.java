package com.lld.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final ReentrantReadWriteLock rwLock;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be strictly positive");
        }
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
        this.rwLock = new ReentrantReadWriteLock();

        // Dummy head and tail nodes to eliminate null boundary checks
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public Optional<V> get(K key) {
        rwLock.writeLock().lock(); // Promotes node to MRU, requires write lock
        try {
            Node<K, V> node = map.get(key);
            if (node == null) {
                return Optional.empty();
            }
            moveToHead(node);
            return Optional.of(node.value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            Node<K, V> existing = map.get(key);
            if (existing != null) {
                existing.value = value;
                moveToHead(existing);
                return;
            }

            if (map.size() >= capacity) {
                evictLeastRecentlyUsed();
            }

            Node<K, V> newNode = new Node<>(key, value);
            map.put(key, newNode);
            addNodeToHead(newNode);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(K key) {
        rwLock.writeLock().lock();
        try {
            Node<K, V> node = map.remove(key);
            if (node != null) {
                removeNode(node);
                return true;
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        rwLock.readLock().lock();
        try {
            return map.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        rwLock.writeLock().lock();
        try {
            map.clear();
            head.next = tail;
            tail.prev = head;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void addNodeToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addNodeToHead(node);
    }

    private void evictLeastRecentlyUsed() {
        Node<K, V> lru = tail.prev;
        if (lru != head) {
            removeNode(lru);
            map.remove(lru.key);
        }
    }

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
