package main.strategy;

import main.ExceptionHandler;
import main.Helper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBucket {
    private Path path;

    /**
     * 9.3. Add a no-argument constructor to the class. It should:
     * 9.3.1. Initialize the path field with a temporary file. The file must be placed in the directory for temporary files and have a random name.
     * Hint: Files.createTempFile.
     *
     * 9.3.2. Use the path field to create a new file. If the file already exists, then replace it.
     * 9.3.3. Ensure the file is deleted when you exit the program.
     * Hint: deleteOnExit().
     */
    public FileBucket() {
        try {
            path = Files.createTempFile(Helper.generateRandomString(), ".tmp");
            Files.deleteIfExists(path);
            Files.createFile(path);
            path.toFile().deleteOnExit();
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
    }

    //Returns the size of the file pointed to by path.
    public long getFileSize() {
        try {
            return Files.size(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
        return 0;
    }

    //Serializes the passed entry to the file.
    public void putEntry(Entry entry) {
        if (entry == null) return;

        try (OutputStream fos = Files.newOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            for (Entry e = entry; e != null; e = e.next) {
                oos.writeObject(e);
            }
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
    }

    public Entry getEntry(){
        if (getFileSize() == 0) return null;
        if (getFileSize() > 0) {
            try (InputStream fis = Files.newInputStream(path);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                return (Entry) ois.readObject();
            }
            catch (IOException | ClassNotFoundException e){
                ExceptionHandler.log(e);
            }
        }
        return null;
    }

    public void remove(){
        try {
            Files.delete(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
    }
}

