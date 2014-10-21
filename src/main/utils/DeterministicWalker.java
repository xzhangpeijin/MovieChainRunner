package main.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Deterministic Walker that ends execution after traversing the "longest path" from each node
 * 
 * At each node, the walker picks the candidate with the largest reachability set in both
 * forward and backwards directions instead of just those whose reachability sets are > 315
 * 
 * @author Peijin Zhang
 */
public class DeterministicWalker extends SlowWalker {
  private int index;
  
  public DeterministicWalker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);
    this.index = 0;
  }
  
  protected int chooseStart() {
    return index++;
  }
  
  protected Candidate chooseCandidate(List<Candidate> candidates) {
    return null;
  }
}
