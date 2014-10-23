package main.walkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;
import main.utils.GraphUtils;

/**
 * Intelligent Random Walker with improved path lengths but decreased speed
 * 
 * Walks two steps at each point, through the head and tail. 
 * 
 * @author Peijin Zhang
 */
public class SlowWalker extends Walker {
  
  private final Random random;
  private List<CandidatePair> pairs;

  public SlowWalker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);
    
    this.random = new Random(System.nanoTime());
    this.pairs = new ArrayList<CandidatePair>();
  }
  
  protected int chooseStart() {
    return initstates.get(random.nextInt(initstates.size()));
  }

  protected List<Candidate> getOutCandidates(int head, Set<Integer> visited) {
    List<Candidate> outCandidates = new ArrayList<Candidate>();
    outCandidates.add(null);
    for (int edge : graph.getOutEdges(head)) {
      if (!visited.contains(edge)) {
        Set<Integer> reachable = GraphUtils.searchForward(graph, edge, visited);
        outCandidates.add(new Candidate(edge, reachable));
      }
    }
    return outCandidates;
  }

  protected List<Candidate> getInCandidates(int tail, Set<Integer> visited) {
    List<Candidate> inCandidates = new ArrayList<Candidate>();
    inCandidates.add(null);
    for (int edge : graph.getInEdges(tail)) {
      if (!visited.contains(edge)) {
        Set<Integer> reachable = GraphUtils.searchBackward(graph, edge, visited);
        inCandidates.add(new Candidate(edge, reachable));
      }
    }
    return inCandidates;
  }

  protected CandidatePair getNext(List<Candidate> outCandidates, List<Candidate> inCandidates) {
    pairs.clear();
    for (int x = 0; x < outCandidates.size(); x++) {
      for (int y = 0; y < inCandidates.size(); y++) {
        Set<Integer> reachable = new HashSet<Integer>();
        
        if (x != 0) {
          reachable.addAll(outCandidates.get(x).reachable);
        }
        
        if (y != 0) {
          reachable.addAll(inCandidates.get(y).reachable);
        }
        
        if (reachable.size() >= maxLength.get()) {
          pairs.add(new CandidatePair(outCandidates.get(x), inCandidates.get(y)));
        }
      }
    }
    
    if (pairs.size() > 0) {
      int move = random.nextInt(pairs.size());
      return pairs.get(move);
    } else {
      return null;
    }
  }
}
