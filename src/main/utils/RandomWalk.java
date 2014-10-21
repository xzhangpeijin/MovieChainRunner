package main.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Random walker for trying to find the longest path
 * 
 * @author Peijin Zhang
 */
public class RandomWalk implements Runnable {
  private final Graph graph;
  private final List<Integer> initstates;
  private final Random random;

  private final Set<Integer>[] fReachable;
  private final Set<Integer>[] bReachable;

  private final String filename;
  private final AtomicInteger maxLength;
  private final Lock fileLock;

  public RandomWalk(Graph graph, List<Integer> initstates,
      Set<Integer>[] fReachable, Set<Integer>[] bReachable,  String filename,
      AtomicInteger maxLength, Lock fileLock) {
    this.graph = graph;
    this.initstates = initstates;
    this.random = new Random(System.nanoTime());

    this.fReachable = fReachable;
    this.bReachable = bReachable;

    this.filename = filename;
    this.maxLength = maxLength;
    this.fileLock = fileLock;
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
    public int node;
    public boolean forward;

    public Candidate(int node, boolean forward) {
      this.node = node;
      this.forward = forward;
    }

  }

  /*
   * Result file format
   * 
   * Paths come in 3 lines: Line 1: Length of the path Line 2: Seed used to
   * generate the path Line 3: Space separated vertices for the path
   * 
   * Paths in the file are guaranteed to be in strictly increasing path length
   */

  /**
   * Writes path to file
   */
  public void writeResult(List<Integer> path) {
    fileLock.lock();
    
    // Check again in case it changed
    if (path.size() > maxLength.get()) {
      try {
        System.out.println(path.size());

        PrintWriter out = new PrintWriter(new FileWriter(new File(filename),
            true));
        out.println("Path Length: " + path.size());

        StringBuffer buf = new StringBuffer();
        for (int vertex : path) {
          buf.append(vertex);
          buf.append(" ");
        }
        out.println(buf.toString().trim());

        out.flush();
        out.close();

        maxLength.set(path.size());
      } catch (IOException e) {
        System.err.println("Error writing to " + filename);
        e.printStackTrace();
      }
    }

    fileLock.unlock();
  }

  @Override
  public void run() {
    List<Integer> path = new ArrayList<Integer>();
    while (true) {
      walkPath(path);
      
      // Check with atomic integer so no context switch required
      if (path.size() > maxLength.get()) {
        writeResult(path);
      }

      path.clear();
    }
  }
}
