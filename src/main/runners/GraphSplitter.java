package main.runners;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import main.utils.Graph;

/**
 * Splits a given graph into its connected components
 * 
 * @author Peijin Zhang
 */
public class GraphSplitter {
  
  /**
   * Splits a given graph into strongly connected components
   * 
   * Tarjan's SCC Algorithm
   */
  public static Set<Graph> splitSCC(Graph graph) {
    Set<Graph> graphs = new HashSet<Graph>();
    
    int size = graph.size();
    boolean[] marked = new boolean[size];
    Stack<Integer> stack = new Stack<Integer>();
    int[] low = new int[size];
    int pre = 0;
    
    for (int x = 0; x < size; x++) {
      if (!marked[x]) {
        pre = dfs(graph, marked, stack, low, pre, x, graphs);
      }
    }
    
    return graphs;
  }
  
  private static int dfs(Graph graph, boolean[] marked, Stack<Integer> stack, int[] low, 
      int pre, int v, Set<Graph> graphs) { 
    marked[v] = true;
    low[v] = pre++;
    int min = low[v];
    stack.push(v);
    
    for (int w : graph.getOutEdges(v)) {
        if (!marked[w]) pre = dfs(graph, marked, stack, low, pre, w, graphs);
        if (low[w] < min) min = low[w];
    }
    if (min < low[v]) { low[v] = min; return pre; }
    
    
    List<String> vertices = graph.getVertices();
    List<String> component = new ArrayList<String>();
    int w;
    do {
        w = stack.pop();
        component.add(vertices.get(w));
        low[w] = graph.size();
    } while (w != v);
    graphs.add(GraphMaker.makeGraph(component, "Component" + graphs.size()));
    return pre;
}
  
  public static Set<Graph> splitGraph(Graph graph) {
    Set<Graph> graphs = new HashSet<Graph>();
    List<String> vertexset = graph.getVertices();

    int size = graph.size();
    Set<Integer> remain = new HashSet<Integer>();
    for (int x = 0; x < size; x++) {
      remain.add(x);
    }

    int components = 0;
    while (remain.size() > 0) {
      List<String> vertices = new ArrayList<String>();

      int init = remain.iterator().next();
      remain.remove(init);

      Queue<Integer> bfs = new LinkedList<Integer>();
      bfs.add(init);

      while (bfs.size() > 0) {
        int cur = bfs.poll();
        vertices.add(vertexset.get(cur));

        List<Integer> edges = graph.getBothEdges(cur);

        for (int edge : edges) {
          if (remain.contains(edge)) {
            remain.remove(edge);
            bfs.add(edge);
          }
        }
      }
      components++;

      Graph component = GraphMaker
          .makeGraph(vertices, "Component" + components);
      graphs.add(component);
    }

    return graphs;
  }

  public static Set<Graph> splitGraph(URL url) throws IOException {
    return splitGraph(Graph.readGraph(url));
  }
}
