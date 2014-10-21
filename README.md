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

####Intelligent walking

Note that when walking, we can see what our in and out vertices are. Consider the nodes reachable from each in and out vertex. If the size of that set plus our currently visited path is not larger than our target path length, then we can see that visiting that vertex would make it impossible to get a path that long anyways. Thus we can eliminate all such vertices at once, so we don’t accidentally say walk 40 nodes in from that vertex and get stuck.

##Improvements

Component generation was the obvious first step. Naive random walks on the 4266 size graph produced longest path of around length 20.

Graph reduction reduced our size to 3240. Naive random walks on this graph produced longest paths of around length 50.

First phase intelligent walking was to pass the walker a map of the reachable nodes from each node in the graph. We only select nodes whose reachable nodes are large enough such that if we were able to hit all those nodes, our final path would be more than 315. This improvement brought our path lengths up to around 175.

Second pass intelligent walking was to generate reachability maps in real time. This means that we account for the fact that we cannot revisit any nodes already in our path. However this also means that we have to calculate reachablility at every step, which is computationally intensive. This slows down our walk speed by a lot.
