package main;

import main.strategy.StorageStrategy;

public class Shortener {
    //Holds identifier of last string added to the repository
    private Long lastId = 0L;
    private StorageStrategy storageStrategy;

    public Shortener(StorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    /**
     * This method will work with threads
     * If passed string is not in the storage,
     * it will be added as new entry
     */
    public synchronized Long getId(String string){
        if (storageStrategy.containsValue(string)) return storageStrategy.getKey(string);
        else {
            lastId++;
            storageStrategy.put(lastId, string);
            return lastId;
        }
    }

    /**
     * This method returns String for given key
     * It will work with threads
     */
    public synchronized String getString(Long id){
        return storageStrategy.getValue(id);
    }
}
