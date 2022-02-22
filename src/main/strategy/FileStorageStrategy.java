package main.strategy;

import java.util.Objects;

/**
 * This strategy works similar to OurHashMap
 * We will have doubled our buckets when we reach size limit,
 * not the array size threshold
 */
public class FileStorageStrategy implements StorageStrategy{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final long DEFAULT_BUCKET_SIZE_LIMIT = 10000L;
    FileBucket[] table = new FileBucket[DEFAULT_INITIAL_CAPACITY];
    int size;
    private long bucketSizeLimit = DEFAULT_BUCKET_SIZE_LIMIT;
    long maxBucketSize;

    public long getBucketSizeLimit() {
        return bucketSizeLimit;
    }

    public void setBucketSizeLimit(long bucketSizeLimit) {
        this.bucketSizeLimit = bucketSizeLimit;
    }

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

        FileBucket bucket = table[indexFor(hash, table.length)];

        for (Entry e = bucket.getEntry(); e != null; e = e.next) {
            //We need to have identical hash and key objects
            if (e.hash == hash &&
                    Objects.equals(e.key, key)) return e;
        }
        //If not found = null
        return null;
    }

    public void resize(int newCapacity){
        FileBucket[] newTable = new FileBucket[newCapacity];
        transfer(newTable);
    }

    /**
     * Transfers all entries to newTable from the old one
     * I copypasted this code and went line by line in order to understand what is going on here
     */
    public void transfer(FileBucket[] newTable){
        //Source is current table
        FileBucket[] src = table;
        int newCapacity = newTable.length;
        for (int i = 0; i < src.length; i++) {
            //Take bucket of index i
            FileBucket bucket = src[i];
            if (bucket != null) {
                //Delete file and nullify entry
                src[i].remove();
                src[i] = null;
                Entry e = bucket.getEntry();
                do {
                    //Take next entry from current one
                    Entry next = e.next;
                    //Calculate new index for given hash
                    int j = indexFor(e.hash, newCapacity);
                    //if element is present at j place in array, will be assigned to this element
                    e.next = newTable[j].getEntry();
                    //we serialize entry into bucket of index j
                    newTable[j].putEntry(e);
                    //Go deeper into entry chain
                    e = next;
                } while(e != null);
            }
        }
        //Replace current table with new one
        table = newTable;
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex){
        //Take entry with given index only if bucket is not null
        //If there is no object in the bucket, we create one
        Entry e = null;
        if (table[bucketIndex] != null) e = table[bucketIndex].getEntry();
        else table[bucketIndex] = new FileBucket();
        //Replace it with new Entry, with "next" being current one
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));
        //If our file exceeds size limit, we resize our array
        if (table[bucketIndex].getFileSize() > bucketSizeLimit) resize(2 * table.length);
        size++;
    }

    //Similar to addEntry, but does not resize the table
    public void createEntry(int hash, Long key, String value, int bucketIndex){
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));
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

        for (FileBucket bucket : table) {
            if (bucket == null) continue;
            Entry entry = bucket.getEntry();
            if (entry == null) continue;
            for (Entry e1 = entry; e1 != null; e1 = e1.next) {
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


