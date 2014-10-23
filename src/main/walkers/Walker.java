package main.walkers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import main.utils.Graph;

/**
 * Abstract class for walkers
 * 
 * Abstract methods subclasses must implement:
 *  chooseStart() - choose a starting vertex at each walk
 *  getOutCandidates() - get all candidates for an out vertex
 *  getInCandidates() - get all candidates for an in vertex
 *  getNext() - choose the next nodes to go to given candidates
 * 
 * @author Peijin Zhang
 */
public abstract class Walker implements Runnable {
  protected boolean doneWalking;

  protected final Graph graph;
  protected final List<Integer> initstates;

  protected final AtomicInteger maxLength;
  
  private final String filename;
  private final Lock fileLock;

  public Walker(Graph graph, List<Integer> initstates, String filename,
      AtomicInteger maxLength, Lock fileLock) {
    this.graph = graph;
    this.initstates = initstates;

    this.maxLength = maxLength;
    this.filename = filename;
    this.fileLock = fileLock;

    doneWalking = false;
  }

  /**
   * Walks one random walk
   */
  protected void walkPath(List<Integer> path) {
    int start = chooseStart();

    Set<Integer> visited = new HashSet<Integer>();
    visited.add(start);

    int head = start;
    int tail = start;

    boolean movedForward = true;
    boolean movedBackward = true;

    List<Candidate> outCandidates = null;
    List<Candidate> inCandidates = null;

    while (movedForward || movedBackward) {
      if (movedForward) {
        outCandidates = getOutCandidates(head, visited);
      }

      if (movedBackward) {
        inCandidates = getInCandidates(tail, visited);
      }

      movedForward = false;
      movedBackward = false;

      CandidatePair next = getNext(outCandidates, inCandidates);

      if (next != null) {
        if (next.forward != null) {
          movedForward = true;
          head = next.forward.node;
          path.add(head);
          visited.add(head);
        }

        if (next.backward != null) {
          movedBackward = true;
          tail = next.backward.node;
          path.add(0, tail);
          visited.add(tail);
        }
      }
    }
  }

  protected abstract int chooseStart();

  protected abstract List<Candidate> getOutCandidates(int head, Set<Integer> visited);

  protected abstract List<Candidate> getInCandidates(int tail, Set<Integer> visited);

  protected abstract CandidatePair getNext(
      List<Candidate> outCandidates, List<Candidate> inCandidates);

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
  private void writeResult(List<Integer> path) {
    fileLock.lock();

    // Check again in case it changed
    if (path.size() > maxLength.get()) {
      try {
        PrintWriter out = new PrintWriter(new FileWriter(new File(filename), true));
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
    long paths = 0;
    while (!doneWalking) {
      walkPath(path);
      paths++;

      // Check with atomic integer so no context switch required
      if (path.size() > maxLength.get()) {
        System.out.println(path.size());
        writeResult(path);
      }

      if (paths % 10000 == 0) {
        System.out.println("Paths walked: " + paths);
      }

      path.clear();
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

  protected static class Candidate {
    public final int node;
    public final Set<Integer> reachable;

    public Candidate(int node, Set<Integer> reachable) {
      this.node = node;
      this.reachable = reachable;
    }
  }
}
