MovieChainRunner
================

CMU 15221 Project

##Project Specifications

####Usage Info:

```
 * -h                Print usage info
 * -c                Create full graph from movie list
 * -s                Split full graph into components
 * -d [graph]        Reduce a given graph
 * -r [graph]        Run on given graph
 * -p [graph]        Print a given graph to graphml format
```

All files are under /data

* MovieList - Input of all movie titles
* FullGraph - Initial graph 
* Additional graphs - Graphs after processing

Results will be outputted under /results/[Graph]Results.txt

####Graph file format:

First line is the name of the graph

Second line is an integer n, the size of the graph (number of vertices)

Next n lines are the names of vertices, where the ith line is the name of the ith vertex

Next n lines are space separated integers, representing the edges

If an integer x is on the ith line, then there exists an edge from i to x in the graph

##Project Strategy:

####Graph generation:

Graph is generated by looping through all pairs of vertices and checking if they overlap

If there are n vertices and the longest vertex is k words, the generation is O(kn^2)

####Graph format:

Graphs are stored using in adjacency list and out adjacency lists. This allows for O(n) lookup for an edge existing in the worst cast but O(1) random access of edges.

####Component generation:

Connected components are generated by performing BFS while assuming that all edges are undirected. This is only O(E) where E is the number of edges. This allowed us to reduce the largest graph from 6561 to 4266

####Graph reduction

We perform forward bfs and backwards bfs on every node in the reduced graph, and combine all the reachable vertices from the two searches. If the size of the combined vertices is less than 315 (our path cutoff), then we removed the vertex from our graph. This allowed us to reduce our graph from 4266 to 3240.

We can improve this reduction by performing searches one level up, so that we can take the max reachable nodes from each child of a node and added them together pairwise. If the total reachable from two children are less than 315, we can conclude that no longest path can pass through this node. This reduces the graph from 3240 to 2242.