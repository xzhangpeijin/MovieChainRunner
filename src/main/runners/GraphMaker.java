package main.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.utils.Graph;


/**
 * Creates the initial full graph from the input file
 * INPUT_PATH and OUTPUT_PATH are the locations of the input movie file and output file for the
 * full graph, respectively
 * 
 * This should only be run once. Runtime is approximately 1 minute
 * 
 * @author Peijin Zhang
 */
public class GraphMaker
{ 
  public static Graph makeGraph(ArrayList<String> vertices) {
    Graph graph = new Graph(vertices);
    
    for (int x = 0; x < vertices.size(); x++) {
      for (int y = 0; y < vertices.size(); y++) {
        if (x != y && hasOverlap(vertices.get(x), vertices.get(y))) {
          graph.addEdge(x, y);
        }
      }
    }
    
    return graph;
  }
  
  public static Graph makeGraph(String input) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(new File(input)));
    ArrayList<String> vertices = new ArrayList<String>();
    
    String nextline;
    while ((nextline = br.readLine()) != null) {
      vertices.add(nextline);
    }
    br.close();
    
    return makeGraph(vertices);
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
