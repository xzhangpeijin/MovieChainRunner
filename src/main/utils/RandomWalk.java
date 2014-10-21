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

public class RandomWalk implements Runnable
{
	private final Graph graph;  
	private final List<Integer> initstates;

	private final String filename;
	private final AtomicInteger maxLength;
	private final Lock fileLock;


	public RandomWalk(Graph graph, List<Integer> initstates, String filename, 
			AtomicInteger maxLength, Lock fileLock) {
		this.graph = graph;
		this.initstates = initstates;

		this.filename = filename;
		this.maxLength = maxLength;
		this.fileLock = fileLock;
	}
	
	/**
	 * Walks one random walk
	 */
	public void walkPath(List<Integer> path, long seed) {
		Random random = new Random(seed);
		int start = random.nextInt(initstates.size());

		Set<Integer> visited = new HashSet<Integer>();
		
		int cur = start;
		visited.add(cur);

		List<Integer> outedges = graph.getOutEdges(cur);
		while (outedges.size() > 0) {
			int next = random.nextInt(outedges.size());
			cur = outedges.get(next);
			visited.add(cur);
			path.add(cur);

			outedges = graph.getOutEdges(cur);
			outedges.removeAll(visited);
		}

		List<Integer> inedges = graph.getInEdges(start);
		inedges.removeAll(visited);
		while (inedges.size() > 0) {
			int next = random.nextInt(inedges.size());
			cur = inedges.get(next);
			visited.add(cur);
			path.add(0, cur);

			inedges = graph.getInEdges(cur);
			inedges.removeAll(visited);
		}
	}
	
  /*
   * Result file format
   * 
   * Paths come in 3 lines:
   *   Line 1: Length of the path
   *   Line 2: Seed used to generate the path
   *   Line 3: Space separated vertices for the path
   *   
   * Paths in the file are guaranteed to be in strictly increasing path length
   */
	
	/**
	 * Writes path to file
	 */
	public void writeResult(List<Integer> path, long seed) {
		fileLock.lock();
		
		// Check again in case it changed 
		if (path.size() > maxLength.get()) {
			try {
				System.out.println(path.size());
				
				PrintWriter out = new PrintWriter(new FileWriter(new File(filename), true));
				out.println("Path Length: " + path.size());
				out.println("Seed: " + seed);
				
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
	public void run()
	{
		List<Integer> path = new ArrayList<Integer>();
		while (true) {
			long seed = System.currentTimeMillis();
			
			walkPath(path, seed);
			
			// Check with atomic int so no context switch required
			if (path.size() > maxLength.get()) {
				writeResult(path, seed);
			}
			
			path.clear();
		}
	}
}
