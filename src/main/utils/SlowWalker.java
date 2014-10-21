package main.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Intelligent Random Walker with improved path lengths but decreased speed
 * 
 * Walks two steps at each point, through the head and tail. 
 * 
 * @author Peijin Zhang
 */
public class SlowWalker extends Walker {

  public SlowWalker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);
  }
  
  protected int chooseStart() {
    return random.nextInt(initstates.size());
  }
  
  protected Candidate chooseCandidate(List<Candidate> candidates) {
    return null;
  }

  /**
   * Walks one random walk
   */
  public void walkPath(List<Integer> path) {
//    int start = random.nextInt(initstates.size());
//
//    Set<Integer> visited = new HashSet<Integer>();
//    visited.add(start);
//    
//    int head = start;
//    int tail = start;
//    
//    List<Candidate> candidates = new ArrayList<Candidate>();
//
//    do {
//      candidates.clear();
//      
//      for (int edge : graph.getOutEdges(head)) {
//        if (!visited.contains(edge)) {
//          Set<Integer> reachable = GraphUtils.searchForward(graph, edge, visited);
//          reachable.addAll(visited);
//          if (reachable.size() >= GraphUtils.CANDIDATE_CUTOFF) {
//            candidates.add(new Candidate(edge, true, reachable.size()));
//          }
//        }
//      }
//      for (int edge : graph.getInEdges(tail)) {
//        if (!visited.contains(edge)) {
//          Set<Integer> reachable = GraphUtils.searchBackward(graph, edge, visited);
//          reachable.addAll(visited);
//          if (reachable.size() >= GraphUtils.CANDIDATE_CUTOFF) {
//            candidates.add(new Candidate(edge, false, reachable.size()));
//          }
//        }
//      }
//
//      if (candidates.size() > 0) {
//        int move = random.nextInt(candidates.size());
//        Candidate next = candidates.get(move);
//        
//        int node = next.node;
//        visited.add(node);
//        
//        if (next.forward) {
//          path.add(node);
//          head = node;
//        } else {
//          path.add(0, node);
//          tail = node;
//        }
//      }
//      
//    } while (candidates.size() > 0);
  }

  protected static class Candidate {
    public final int node;
    public final Set<Integer> reachable;

    public Candidate(int node, Set<Integer> reachable) {
      this.node = node;
      this.reachable = reachable;
    }
  }
}
