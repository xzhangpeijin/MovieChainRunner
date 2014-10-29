package main.walkers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;

/**
 * Two way walker
 * 
 * This walker walks both forwards and backwards, thus optimizing the path lengths
 * at each walk step. This also doesn't kill off any paths which are say part of
 * cycles as a walker that only starts on source nodes would.
 * 
 * Subclasses must implement:
 *  chooseStart() - choose a starting vertex at each walk
 *  getOutCandidates() - get all candidates for an out vertex
 *  getInCandidates() - get all candidates for an in vertex
 *  getNext() - choose the next nodes to go to given candidates
 * 
 * @author Peijin Zhang
 */
public abstract class TwoWayWalker extends Walker {

  protected List<Integer> initstates;

  public TwoWayWalker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, filename, maxLength, fileLock);

    this.initstates = initstates;
  }

  /**
   * Walks one random walk
   */
  protected void walkPath(List<Integer> path) {
    int start = chooseStart();

    Set<Integer> visited = new HashSet<Integer>();
    visited.add(start);
    path.add(start);

    int head = start;
    int tail = start;

    boolean movedForward = true;
    boolean movedBackward = true;

    List<Candidate> outCandidates = null;
    List<Candidate> inCandidates = null;

    while (movedForward || movedBackward) {
      if (movedForward) {
        outCandidates = getOutCandidates(head, visited);
      } else {
        filterCandidates(outCandidates, visited);
      }
      if (movedBackward) {
        inCandidates = getInCandidates(tail, visited);
      } else {
        filterCandidates(inCandidates, visited);
      }

      movedForward = false;
      movedBackward = false;

      CandidatePair next = getNext(outCandidates, inCandidates);

      if (next != null) {
        if (next.forward != null) {
          movedForward = true;
          head = next.forward.node;
          if (visited.contains(head)) {
            System.out.println("H:" + movedForward + " " + head + " " + movedBackward + " " + tail);
          }
          path.add(head);
          visited.add(head);
        }

        if (next.backward != null) {
          movedBackward = true;
          tail = next.backward.node;
          if (visited.contains(tail)) {
            System.out.println("H:" + movedForward + " " + head + " " + movedBackward + " " + tail);
          }
          path.add(0, tail);
          visited.add(tail);
        }
      }
    }

    if (visited.size() != path.size()) {
      System.out.println("ERR: " + visited.size() + " " + path.size());
    }
  }
  
  private void filterCandidates(List<Candidate> candidates, Set<Integer> visited) {
    Set<Candidate> newCandidates = new HashSet<Candidate>(candidates.size());
    for (Candidate candidate : candidates) {
      if (candidate == null || !visited.contains(candidate.node)) {
        newCandidates.add(candidate);
      }
    }
    candidates.clear();
    candidates.addAll(newCandidates);
  }

  protected abstract int chooseStart();

  protected abstract List<Candidate> getOutCandidates(int head, Set<Integer> visited);

  protected abstract List<Candidate> getInCandidates(int tail, Set<Integer> visited);

  protected abstract CandidatePair getNext(
      List<Candidate> outCandidates, List<Candidate> inCandidates);

  protected static class Candidate {
    public final int node;
    public final Set<Integer> reachable;

    public Candidate(int node, Set<Integer> reachable) {
      this.node = node;
      this.reachable = reachable;
    }
  }

  protected static class CandidatePair {
    public final Candidate forward;
    public final Candidate backward;

    public CandidatePair(Candidate forward, Candidate backward) {
      this.forward = forward;
      this.backward = backward;
    }
  }
}
