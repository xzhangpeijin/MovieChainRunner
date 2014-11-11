package main.walkers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;

/**
 * Deterministic Walker that ends execution after traversing the "longest path" from each node
 * 
 * At each node, the walker picks the candidate with the largest reachability set in both
 * forward and backwards directions instead of just a random one
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
    CandidatePair move = null;
    int size = -1;
    for (int x = 0; x < outCandidates.size(); x++) {
      for (int y = 0; y < inCandidates.size(); y++) {
        if (x == 0 || y == 0 || outCandidates.get(x).node != inCandidates.get(y).node) {
          Set<Integer> reachable = new HashSet<Integer>();

          if (x != 0) {
            reachable.addAll(outCandidates.get(x).reachable);
          }

          if (y != 0) {
            reachable.addAll(inCandidates.get(y).reachable);
          }

          if (reachable.size() >= size) {
            size = reachable.size();
            move = new CandidatePair(outCandidates.get(x), inCandidates.get(y));
          }
        }
      }
    }
    return move;
  }

}
