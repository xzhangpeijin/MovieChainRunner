package main.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Random walker for trying to find the longest path
 * 
 * Uses initial memoization to keep track of the reachable nodes from every vertex, this makes
 * state lookups at each walk decision extremely fast. However, not all memoized reachable
 * nodes may actually be reachable since certain paths may include already visited nodes
 * 
 * @author Peijin Zhang
 */
public class FastWalker extends Walker {
  private final Set<Integer>[] fReachable;
  private final Set<Integer>[] bReachable;

  public FastWalker(Graph graph, List<Integer> initstates,
      Set<Integer>[] fReachable, Set<Integer>[] bReachable,  String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);

    this.fReachable = fReachable;
    this.bReachable = bReachable;
  }

  /**
   * Walks one random walk
   */
  public void walkPath(List<Integer> path) {
    int start = random.nextInt(initstates.size());

    Set<Integer> visited = new HashSet<Integer>();
    visited.add(start);
    
    int head = start;
    int tail = start;
    
    List<Candidate> candidates = new ArrayList<Candidate>();

    do {
      candidates.clear();
      
      for (int edge : graph.getOutEdges(head)) {
        if (!visited.contains(edge)) {
          Set<Integer> reachable = new HashSet<Integer>(fReachable[edge]);
          reachable.addAll(visited);
          if (reachable.size() >= GraphUtils.CANDIDATE_CUTOFF) {
            candidates.add(new Candidate(edge, true));
          }
        }
      }
      for (int edge : graph.getInEdges(tail)) {
        if (!visited.contains(edge)) {
          Set<Integer> reachable = new HashSet<Integer>(bReachable[edge]);
          reachable.addAll(visited);
          if (reachable.size() >= GraphUtils.CANDIDATE_CUTOFF) {
            candidates.add(new Candidate(edge, false));
          }
        }
      }

      if (candidates.size() > 0) {
        int move = random.nextInt(candidates.size());
        Candidate next = candidates.get(move);
        
        int node = next.node;
        visited.add(node);
        
        if (next.forward) {
          path.add(node);
          head = node;
        } else {
          path.add(0, node);
          tail = node;
        }
      }
      
    } while (candidates.size() > 0);
  }

  private static class Candidate {
    public final int node;
    public final boolean forward;

    public Candidate(int node, boolean forward) {
      this.node = node;
      this.forward = forward;
    }
  }
}
