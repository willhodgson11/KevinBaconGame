import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class KevinBacon {
    private AdjacencyMapGraph<String, Set<String>> graph;

    public KevinBacon(String actorsFile, String moviesFile, String movieActorFile) throws IOException {
        GraphBuilder graphy = new GraphBuilder(actorsFile, moviesFile, movieActorFile);
        this.graph = graphy.buildGraph();
        welcome();
    }





    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");

    }
}