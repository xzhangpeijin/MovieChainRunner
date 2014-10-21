package main.runners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import main.utils.Graph;

/**
 * Splits a given graph into its connected components
 * 
 * @author Peijin Zhang
 */
public class GraphSplitter {
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

  public static Set<Graph> splitGraph(String path) throws IOException {
    return splitGraph(Graph.readFromFile(path));
  }
}
