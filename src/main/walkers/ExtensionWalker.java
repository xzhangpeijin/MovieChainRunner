package main.walkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;
import main.utils.Path;
import main.utils.PathUtils;

/**
 * Intelligent Random Walker with improved path lengths but decreased speed
 * 
 * Walks two steps at each point, through the head and tail. 
 * 
 * @author Peijin Zhang
 */
public class ExtensionWalker extends TwoWayWalker {

  private final Graph superGraph;
  private final Random random;
  private List<CandidatePair> pairs;

  public ExtensionWalker(Graph graph, Graph superGraph, List<Integer> initstates, 
      String filename, AtomicInteger maxLength, Lock fileLock) {
    super(graph, initstates, filename, maxLength, fileLock);

    this.random = new Random(System.nanoTime());
    this.pairs = new ArrayList<CandidatePair>();
    this.superGraph = superGraph;
  }

  protected int chooseStart() {
    return initstates.get(random.nextInt(initstates.size()));
  }

  protected List<Candidate> getOutCandidates(int head, Path path) {
    List<Candidate> outCandidates = new ArrayList<Candidate>();
    outCandidates.add(null);
    for (int edge : graph.getOutEdges(head)) {
      if (!path.contains(edge)) {
        outCandidates.add(new Candidate(edge, null));
      }
    }
    return outCandidates;
  }

  protected List<Candidate> getInCandidates(int tail, Path path) {
    List<Candidate> inCandidates = new ArrayList<Candidate>();
    inCandidates.add(null);
    for (int edge : graph.getInEdges(tail)) {
      if (!path.contains(edge)) {
        inCandidates.add(new Candidate(edge, null));
      }
    }
    return inCandidates;
  }

  protected CandidatePair getNext(List<Candidate> outCandidates, List<Candidate> inCandidates) {
    pairs.clear();

    int maxsize = -1;
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

          maxsize = Math.max(maxsize, reachable.size());
          pairs.add(new CandidatePair(outCandidates.get(x), inCandidates.get(y)));
        }
      }
    }

    for (int x = 0; x < pairs.size(); x++) {
      if (pairs.get(x).reachable.size() < maxsize) {
        pairs.remove(x);
        x--;
      }
    }

    if (pairs.size() > 0) {
      int move = random.nextInt(pairs.size());
      return pairs.get(move);
    } else {
      return null;
    }
  }
  
  protected Path walkPath() {
    Path path = super.walkPath();
    PathUtils.convertPath(superGraph, path);
    return path;
  }
}
