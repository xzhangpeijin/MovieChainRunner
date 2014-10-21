package main.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Abstract class for walkers
 */
public abstract class Walker implements Runnable {
  protected boolean doneWalking;
  
  protected final Graph graph;
  protected final List<Integer> initstates;
  protected final Random random;

  private final String filename;
  private final AtomicInteger maxLength;
  private final Lock fileLock;

  public Walker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    this.graph = graph;
    this.initstates = initstates;
    this.random = new Random(System.nanoTime());

    this.filename = filename;
    this.maxLength = maxLength;
    this.fileLock = fileLock;
    
    doneWalking = false;
  }

  /**
   * Walks one random walk
   */
  public abstract void walkPath(List<Integer> path);

  /*
   * Result file format
   * 
   * Paths come in 3 lines: Line 1: Length of the path Line 2: Seed used to
   * generate the path Line 3: Space separated vertices for the path
   * 
   * Paths in the file are guaranteed to be in strictly increasing path length
   */

  /**
   * Writes path to file
   */
  public void writeResult(List<Integer> path) {
    fileLock.lock();
    
    // Check again in case it changed
    if (path.size() > maxLength.get()) {
      try {
        PrintWriter out = new PrintWriter(new FileWriter(new File(filename),
            true));
        out.println("Path Length: " + path.size());

        StringBuffer buf = new StringBuffer();
        for (int vertex : path) {
          buf.append(vertex);
          buf.append(" ");
        }
        out.println(buf.toString().trim());

        out.flush();
        out.close();

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
    List<Integer> path = new ArrayList<Integer>();
    while (!doneWalking) {
      walkPath(path);
      
      // Check with atomic integer so no context switch required
      if (path.size() > maxLength.get()) {
        writeResult(path);
      }

      path.clear();
    }
  }
}
