import java.io.*;
import java.util.*;

public class KevinBacon {
    private AdjacencyMapGraph<String, Set<String>> graph;
    private  String centerUniverse = "Kevin Bacon";
    private  Graph<String, Set<String>> tree;


    public KevinBacon(String actorsFile, String moviesFile, String movieActorFile) throws IOException {
        GraphBuilder graphy = new GraphBuilder(actorsFile, moviesFile, movieActorFile);
        this.graph = graphy.buildGraph();
        welcome();
    }


    /**
     * prints the list of actors—and the movies they costarred in—that connect the
     * provided actor and the center of the universe
     * @param name name of the selected actor
     */
    public void findPath(String name){
        tree = GraphLib.bfs(graph, centerUniverse);
        // Name must be a known vertex in the whole graph
        if(!graph.hasVertex(name)){
            System.out.println("Please enter a valid actor");
            return;
        }
        // If there is no path between the provided actor and the center of the universe, they are not connected
        List<String> pathList = GraphLib.getPath(tree, name);
        if(pathList == null){
            System.out.println(centerUniverse + " and " + name + " are not connected");
        }
        // Print the degree of separation of the provided actor
        System.out.println(name + "'s number is " + (pathList.size()-1));
        // Print the names of actors connecting the provided actor and the center of the universe, and the films that connect them
        for(int i=0; i<pathList.size()-1; i++){
            // Get the name of the current actor being examined
            String curr = pathList.get(i);
            // Get the name of the next connecting actor
            String next = pathList.get(i+1);
            // Get the list of movies that both actors starred in together
            Set<String> movies = graph.getLabel(curr, next);
            System.out.println(curr + " appeared in " + movies + " with " + next);
        }
    }



    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        //KevinBacon game = new KevinBacon("actors.txt", "movies.txt", "movie-actors.txt");
    }
}