package main.walkers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;
import main.utils.Path;
import main.utils.PathUtils;

/**
 * Experimental walker for walking on a reduced graph and expanding into a full one 
 * 
 * @author Peijin Zhang
 */
public class ExperimentalWalker extends SlowWalker {
  // If we're close to 25 from our longest, do extension
  private static final int EXTENSION_CUTOFF = 25;
  // Extend 10 times and take the best one
  private static final int EXTENSION_ITERATIONS = 10;

  private final Graph reduced;
  private final Graph full;

  public ExperimentalWalker(Graph reduced, Graph full, List<Integer> initstates, 
      String filename, AtomicInteger maxLength, Lock fileLock) {
    super(reduced, initstates, filename, maxLength, fileLock);

    this.reduced = reduced;
    this.full = full;
    this.exhaustive = false;
  }

  public Path walkPath() {
    Path small = PathUtils.convertPath(full, super.walkPath());
    this.graph = full;
    
    Path maxresult = small;
    
    if (small.size() > maxLength.get() - EXTENSION_CUTOFF) {
      for (int x = 0; x < EXTENSION_ITERATIONS; x++) {
        Path path = super.walkPath(small);
        if (path.size() > maxresult.size()) {
          maxresult = path;
        }
      }
    }
    this.graph = reduced;
    return maxresult;
  }
}
