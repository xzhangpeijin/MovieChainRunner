package main.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Shared static methods for graph functions
 * 
 * @author Peijin Zhang
 */
public class GraphUtils {
  public static final int CANDIDATE_CUTOFF = 315;
  
  public static Set<Integer> searchForward(Graph graph, int vertex) {
    Set<Integer> reached = new HashSet<Integer>();
    reached.add(vertex);

    Queue<Integer> bfs = new LinkedList<Integer>();
    bfs.add(vertex);

    while (bfs.size() > 0) {
      int cur = bfs.poll();

      List<Integer> edges = graph.getOutEdges(cur);

      for (int edge : edges) {
        if (!reached.contains(edge)) {
          reached.add(edge);
          bfs.add(edge);
        }
      }
    }

    return reached;
  }

  public static Set<Integer> searchBackward(Graph graph, int vertex) {
    Set<Integer> reached = new HashSet<Integer>();
    reached.add(vertex);

    Queue<Integer> bfs = new LinkedList<Integer>();
    bfs.add(vertex);

    while (bfs.size() > 0) {
      int cur = bfs.poll();

      List<Integer> edges = graph.getInEdges(cur);

      for (int edge : edges) {
        if (!reached.contains(edge)) {
          reached.add(edge);
          bfs.add(edge);
        }
      }
    }

    return reached;
  }
}
