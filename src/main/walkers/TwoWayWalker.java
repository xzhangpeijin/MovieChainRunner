package main.walkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;
import main.utils.Path;

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
   * Walks one random walk onto the given path
   */
  protected Path walkPath(Path init) {
    Path path = new Path(init);
    
    int head = path.getHead();
    int tail = path.getTail();

    boolean movedForward = true;
    boolean movedBackward = true;

    List<Candidate> outCandidates = null;
    List<Candidate> inCandidates = null;

    while (movedForward || movedBackward) {
      if (movedForward) {
        outCandidates = getOutCandidates(head, path);
      } else {
        filterCandidates(outCandidates, path);
      }
      if (movedBackward) {
        inCandidates = getInCandidates(tail, path);
      } else {
        filterCandidates(inCandidates, path);
      }

      movedForward = false;
      movedBackward = false;

      CandidatePair next = getNext(outCandidates, inCandidates);

      if (next != null) {
        if (next.forward != null) {
          movedForward = true;
          head = next.forward.node;
          path.appendForward(head);
        }

        if (next.backward != null) {
          movedBackward = true;
          tail = next.backward.node;
          path.appendBackward(tail);
        }
      }
    }

    return path;
  }

  public Path walkPath() {
    Path path = new Path(graph);
    int start = chooseStart();
    path.appendForward(start);
    return walkPath(path);
  }

  private void filterCandidates(List<Candidate> candidates, Path path) {
    List<Candidate> newCandidates = new ArrayList<Candidate>(candidates.size());
    for (Candidate candidate : candidates) {
      if (candidate == null || !path.contains(candidate.node)) {
        newCandidates.add(candidate);
      }
    }
    candidates.clear();
    candidates.addAll(newCandidates);
  }

  protected abstract int chooseStart();

  /**
   * Gets possible outCandidates
   * Requires first index to be null
   */
  protected abstract List<Candidate> getOutCandidates(int head, Path path);

  /**
   * Gets possible inCandidates
   * Requires first index to be null
   */
  protected abstract List<Candidate> getInCandidates(int tail, Path path);

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
    public final Set<Integer> reachable;

    public CandidatePair(Candidate forward, Candidate backward) {
      this.forward = forward;
      this.backward = backward;

      reachable = new HashSet<Integer>();
      if (forward != null) {
        reachable.addAll(forward.reachable);
      } 
      if (backward != null) {
        reachable.addAll(backward.reachable);
      }
    }
  }
}
