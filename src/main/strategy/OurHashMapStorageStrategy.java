package main.strategy;

import java.util.Objects;

public class OurHashMapStorageStrategy implements StorageStrategy{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    Entry[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    int size;
    int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    float loadFactor = DEFAULT_LOAD_FACTOR;

    public int hash(Long k) {
        return k.hashCode();
    }

    public int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public Entry getEntry(Long key){
        //No size = no entries
        if (size == 0) return null;

        //Generate hash for given key - to speed up search, null key = 0
        int hash = (key == null) ? 0 : hash(key);

        for (Entry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
            //We need to have identical hash and key objects
            if (e.hash == hash &&
                    Objects.equals(e.key, key)) return e;
        }
        //If not found = null
        return null;
    }

    public void resize(int newCapacity){
        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfers all entries to newTable from the old one
     * I copypasted this code and went line by line in order to understand what is going on here
     */
    public void transfer(Entry[] newTable){
        //Source is current table
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int i = 0; i < src.length; i++) {
            //Take entry of index i
            Entry e = src[i];
            if (e != null) {
                //Nullify our array entry
                src[i] = null;
                do {
                    //Take next entry from current one
                    Entry next = e.next;
                    //Calculate new index for given hash
                    int j = indexFor(e.hash, newCapacity);
                    //if element is present at j place in array, will be assigned to this element
                    e.next = newTable[j];
                    //we place our original element at j place in array
                    newTable[j] = e;
                    //Go deeper into entry chain
                    e = next;
                } while(e != null);
            }
        }
        //Replace current table with new one
        table = newTable;
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex){
        //Take entry with given index
        Entry e = table[bucketIndex];
        //Replace it with new Entry, with "next" being current one
        table[bucketIndex] = new Entry(hash, key, value, e);
        if (size >= threshold) resize(2 * table.length);
        size++;
    }

    //Similar to addEntry, but does not resize the table
    public void createEntry(int hash, Long key, String value, int bucketIndex){
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        size++;
    }

    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        return getKey(value) != null;
    }

    @Override
    public void put(Long key, String value) {
        int hash = hash(key);
        addEntry(hash, key, value, indexFor(hash, table.length));
    }

    @Override
    public Long getKey(String value) {
        if (value == null) return null;

        for (Entry e : table) {
            for (Entry e1 = e; e1 != null; e1 = e1.next) {
                if (value.equals(e1.value)) return e1.getKey();
            }
        }
        return null;
    }

    @Override
    public String getValue(Long key) {
        return getEntry(key).getValue();
    }
}

