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

    public void infSeparation(String actor){
        GraphLib.missingVertices(graph, tree);
    }

    public void newCenter(String name){
        if(!graph.hasVertex(name)) {
            System.out.println("Please enter a valid actor");
            return;
        }
        this.centerUniverse = name;
        this.tree = GraphLib.bfs(graph, name);
        System.out.println(actor + " is now the center of the acting universe, connected to "+ unconnected +
                        "/" + total + " actors with average separation " + GraphLib.averageSeparation(graph, centerUniverse));
        );
    }


    /**
     * prints the list of actors—and the movies they costarred in—that connect the
     * provided actor and the center of the universe
     * @param name name of the selected actor
     */
    public void findPath(String name){
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
                "\n" );
        newCenter(centerUniverse);
        getUserInput();
    }
    public void getUserInput() {
        Scanner input = new Scanner(System.in);
        System.out.println("Kevin Bacon game > ");
        String command = input.nextLine();

    }

    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        //KevinBacon game = new KevinBacon("actors.txt", "movies.txt", "movie-actors.txt");
    }
}