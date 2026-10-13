package com.lld.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    @DisplayName("Should put and get items accurately")
    void testBasicPutAndGet() {
        Cache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "Alpha");
        cache.put(2, "Beta");

        assertEquals(Optional.of("Alpha"), cache.get(1));
        assertEquals(Optional.of("Beta"), cache.get(2));
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("Should evict the least recently used item when capacity exceeded")
    void testLRUEviction() {
        Cache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "Alpha");
        cache.put(2, "Beta");

        // Access 1 to mark it as MRU
        cache.get(1);

        // Put 3 -> Capacity exceeded -> Key 2 must be evicted
        cache.put(3, "Gamma");

        assertEquals(Optional.of("Alpha"), cache.get(1));
        assertEquals(Optional.empty(), cache.get(2)); // Evicted!
        assertEquals(Optional.of("Gamma"), cache.get(3));
    }

    @Test
    @DisplayName("Should update existing key without altering capacity")
    void testUpdateExistingKey() {
        Cache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 100);
        cache.put("A", 200);

        assertEquals(1, cache.size());
        assertEquals(Optional.of(200), cache.get("A"));
    }

    @Test
    @DisplayName("Should remove elements and clear cache")
    void testRemoveAndClear() {
        Cache<String, String> cache = new LRUCache<>(3);
        cache.put("k1", "v1");
        cache.put("k2", "v2");

        assertTrue(cache.remove("k1"));
        assertFalse(cache.remove("k999"));
        assertEquals(1, cache.size());

        cache.clear();
        assertEquals(0, cache.size());
        assertEquals(Optional.empty(), cache.get("k2"));
    }
}
