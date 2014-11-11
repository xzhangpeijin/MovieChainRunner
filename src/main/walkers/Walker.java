package main.walkers;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;
import main.utils.Path;

/**
 * Abstract class for walkers
 * 
 * Abstract methods subclasses must implement:
 *  walkPath() - given an list for a path populate it with a walk
 * 
 * @author Peijin Zhang
 */
public abstract class Walker implements Runnable {
  protected boolean doneWalking;

  protected Graph graph;

  protected final AtomicInteger maxLength;
  
  private final String filename;
  private final Lock fileLock;

  public Walker(Graph graph, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    this.graph = graph;

    this.maxLength = maxLength;
    this.filename = filename;
    this.fileLock = fileLock;

    doneWalking = false;
  }

  protected abstract Path walkPath();

  /**
   * Writes path to file
   */
  private void writeResult(Path path) {
    fileLock.lock();

    // Check again in case it changed
    if (path.size() > maxLength.get()) {
      try {
        path.writeToFile(filename);
        maxLength.set(path.size());
      } catch (IOException e) {
        System.err.println("Error writing to " + filename);
        e.printStackTrace();
      }
    }

    fileLock.unlock();
  }

  @Override
  public void run() {
    long paths = 0;
    while (!doneWalking) {
      Path path = walkPath();
      paths++;

      // Check with atomic integer so no context switch required
      if (path.size() > maxLength.get()) {
        System.out.println(path.size());
        writeResult(path);
      }

      if (paths % 10000 == 0) {
        System.out.println("Paths walked: " + paths);
      }
    }
  }
}
