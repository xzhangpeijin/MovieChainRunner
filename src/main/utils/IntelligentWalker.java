package main.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Intelligent Random Walker with improved path lengths but decreased speed
 * 
 * @author Peijin Zhang
 */
public class IntelligentWalker extends Walker {

  public IntelligentWalker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);
  }

  /**
   * Walks one random walk
   */
  public void walkPath(List<Integer> path) {

  }
}
