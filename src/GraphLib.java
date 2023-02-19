import java.util.*;

/**
 * Library for graph analysis
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author Tim Pierson, Dartmouth CS 10, Spring 2019 - provided basis BFS method
 * @author willhodgson Dartmouth CS 10, Winter 2023. Added bfs, getPath, missingVertices,
 * AverageSeparation, totalSeparatioh and verticesByInDegree methods
 * 
 */
public class GraphLib {

	/**
	 * BFS to find shortest path tree for a current center of the universe. Return a path tree as a Graph.
	 * Based on BFS method from GraphTraversal.java code from class.
	 * @param g
	 * @param source
	 * @return graph of vertices (all with outDegree 1) connecting a given vertex to the center of universe
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
		Graph backTrack = new AdjacencyMapGraph<V, V>(); //initialize backTrack
		backTrack.insertVertex(source); //load start vertex
		Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
		Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

		queue.add(source); //enqueue start vertex
		visited.add(source); //add start to visited Set
		while (!queue.isEmpty()) { //loop until no more vertices
			V u = queue.remove(); //dequeue
			for (V v : g.outNeighbors(u)) { //loop over out neighbors
				if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
					visited.add(v); //add neighbor to visited Set
					queue.add(v); //enqueue neighbor
					backTrack.insertVertex(v); //add neighbor to graph
					backTrack.insertDirected(v, u, g.getLabel(v, u)); //save that this vertex was discovered from prior vertex
				}
			}
		}
		return backTrack;
	}

	/**
	 * Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe.
	 * Assumes all vertices in shortestPathTree have only one neighbor.
	 * @param tree
	 * @param v
	 * @return list of vertices that connect given vertex to center of universe
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
		if(!tree.hasVertex(v)){return null;}
		List<V> path = new ArrayList<>();	// initialize new list of vertices
		path.add(v);						// add given vertex to list
		// while the current vertex has a neighbor to visit, create an iterator for the neighbors of the current vertex
		for (Iterator<V> neighbor = tree.outNeighbors(v).iterator(); neighbor.hasNext(); ) {
			v = neighbor.next();	//
			path.add(v);
		}
		return path;
	}

	/**
	 * Given a graph and a subgraph (here shortest path tree), determine which vertices
	 * are in the graph but not the subgraph (here, not reached by BFS).
	 * @param graph
	 * @param subgraph
	 * @return set of vertices not included in shortestPath tree
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
		Set<V> missing = new HashSet<>();
		for(V v : graph.vertices()){
			if(!subgraph.hasVertex(v)){
				missing.add(v);
			}
		}
		return missing;
	}

	/**
	 * Find the average distance-from-root in a shortest path tree, without enumerating all the paths!
	 * @param tree
	 * @param root
	 * @return average degree of separation
	 * @param <V>
	 * @param <E>
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
		// call recursive function with an initial separation of zero (root is the same as itself)
		int total = totalSeparation(tree, root, 0);
		// define denominator as the total number of elements in the tree
		int numEle = tree.numVertices();
		// return total degree of separation over number of elements in tree, excluding the root
		return (double) total/ (double) numEle - 1;
	}

	/**
	 * Recursive function to determine total degree of separation within tree
	 * @param tree
	 * @param node
	 * @param degree
	 * @return total degree of separation of all vertices from root, as int
	 * @param <V>
	 * @param <E>
	 */
	private static <V,E> int totalSeparation(Graph<V, E> tree, V node, int degree){
		int sum = degree;	// initialize sum to current degree of separation
		List<V> visited = new ArrayList<>();
		visited.add(node);
		for (V v : tree.outNeighbors(node)){	// for each neighbor, add their degree of separation to sum
			sum += totalSeparation(tree, v, degree+1);	// recursively call totalSeparation, incrementing the degree of separation by one
		}
		return sum;
	}

	/**
	 * from SA5(?)
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		ArrayList<V> vertices = new ArrayList<>();

		// Adds vertices in set to a list
		for(V vertex : g.vertices()){
			vertices.add(vertex);
		}

		// Sort list in descending order of inDegree
		vertices.sort((V v1, V v2)-> g.inDegree(v2) - g.inDegree(v1));

		return vertices;
	}
}
