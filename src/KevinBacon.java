import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class KevinBacon {
    private AdjacencyMapGraph<String, Set<String>> graph;
    private String centerUniverse = "Kevin Bacon";

    public void setCenterUniverse(String newCenter) {
        this.centerUniverse = newCenter;
    }

    public KevinBacon(String actorsFile, String moviesFile, String movieActorFile) throws IOException {
        GraphBuilder graphy = new GraphBuilder(actorsFile, moviesFile, movieActorFile);
        this.graph = graphy.buildGraph();
        welcome();
    }
    public void welcome() {
        int total = graph.numVertices();
        int missing = GraphLib.missingVertices(graph,  GraphLib.bfs(graph, centerUniverse)).size();
        int unconnected = total - missing;
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n" +
                "\n" +
                "Kevin Bacon is now the center of the acting universe, connected to "+ unconnected +
                "/" + total + " actors with average separation " + GraphLib.averageSeparation(graph, centerUniverse));
        getUserInput();
    }
    public void getUserInput() {
        Scanner input = new Scanner(System.in);
        System.out.println("Kevin Bacon game > ");
        String command = input.nextLine();

    }



    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");

    }
}