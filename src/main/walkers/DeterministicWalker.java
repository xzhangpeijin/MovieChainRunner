package main.walkers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;

/**
 * Deterministic Walker that ends execution after traversing the "longest path" from each node
 * 
 * At each node, the walker picks the candidate with the largest reachability set in both
 * forward and backwards directions instead of just a random one
 * 
 * Longest path is 266
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
    if (index == initstates.size() - 2) {
      doneWalking = true;
    } 
    return initstates.get(index++);
  }
  
  protected CandidatePair getNext(List<Candidate> outCandidates, List<Candidate> inCandidates) {
    
    Candidate out = null;
    for (int x = 0; x < outCandidates.size(); x++) {
      if (out == null || outCandidates.get(x).reachable.size() > out.reachable.size()) {
        out = outCandidates.get(x);
      }
    }
    
    Candidate in = null;
    for (int x = 0; x < inCandidates.size(); x++) {
      if (in == null || inCandidates.get(x).reachable.size() > in.reachable.size()) {
        in = inCandidates.get(x);
      }
    }
    
    return new CandidatePair(out, in);
  }
  
}
