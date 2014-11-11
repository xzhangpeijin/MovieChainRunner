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

  private final Graph reduced;
  private final Graph full;

  public ExperimentalWalker(Graph reduced, Graph full, List<Integer> initstates, 
      String filename, AtomicInteger maxLength, Lock fileLock) {
    super(reduced, initstates, filename, maxLength, fileLock);

    this.reduced = reduced;
    this.full = full;
        
  }

  public Path walkPath() {
    Path small = PathUtils.convertPath(full, super.walkPath());

    this.graph = full;
    Path result = super.walkPath(small);

    this.graph = reduced;
    return result;
  }
}
