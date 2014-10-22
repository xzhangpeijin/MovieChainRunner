package main.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.utils.Graph;

/**
 * Creates a graph using a set of input vertices
 * 
 * @author Peijin Zhang
 */
public class GraphMaker {
  /**
   * Makes a graph from a list of vertex names
   */
  public static Graph makeGraph(List<String> vertices, String name) {
    Graph graph = new Graph(vertices, name);

    for (int x = 0; x < vertices.size(); x++) {
      for (int y = 0; y < vertices.size(); y++) {
        if (x != y && hasOverlap(vertices.get(x), vertices.get(y))) {
          graph.addEdge(x, y);
        }
      }
    }

    return graph;
  }

  /**
   * Makes a graph from a file containing vertex names
   */
  public static Graph makeGraph(String input, String name) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(new File(input)));
    List<String> vertices = new ArrayList<String>();

    String nextline;
    while ((nextline = br.readLine()) != null) {
      vertices.add(nextline.trim());
    }
    br.close();

    return makeGraph(vertices, name);
  }

  private static boolean hasOverlap(String a, String b) {
    String[] atoks = a.split(" ");
    String[] btoks = b.split(" ");
    for (int x = 0; x < Math.min(atoks.length, btoks.length); x++) {
      boolean overlap = true;
      int start = atoks.length - 1 - x;
      for (int y = 0; y <= x; y++) {
        if (!atoks[start + y].equals(btoks[y])) {
          overlap = false;
          break;
        }
      }
      if (overlap) {
        return true;
      }
    }
    return false;
  }
}
