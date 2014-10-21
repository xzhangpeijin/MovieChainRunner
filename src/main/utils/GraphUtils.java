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
  
  /**
   * Searches using outgoing edges from a vertex and given a current set of visited nodes
   * 
   * Returns the set of nodes reachable from this vertex
   */
  public static Set<Integer> searchForward(Graph graph, int vertex, Set<Integer> visited) {
    Set<Integer> reached = (visited == null) ? 
        new HashSet<Integer>() : new HashSet<Integer>(visited);
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
  
  public static Set<Integer> searchForward(Graph graph, int vertex) {
    return searchForward(graph, vertex, null);
  }

  /**
   * Searches using ingoing edges from a vertex and given a current set of visited nodes
   * 
   * Returns the set of nodes reachable from this vertex
   */
  public static Set<Integer> searchBackward(Graph graph, int vertex, Set<Integer> visited) {
    Set<Integer> reached = (visited == null) ? 
        new HashSet<Integer>() : new HashSet<Integer>(visited);
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
  
  public static Set<Integer> searchBackward(Graph graph, int vertex) {
    return searchBackward(graph, vertex, null);
  }
}
