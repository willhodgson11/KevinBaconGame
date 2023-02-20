import java.io.*;
import java.util.*;



public class KevinBacon {
    private AdjacencyMapGraph<String, Set<String>> graph;
    private  String centerUniverse = "Kevin Bacon";
    private  Graph<String, Set<String>> tree;

    /**
     * constructor class, initializes a kevinBacon game with the provided files
     * @param actorsFile
     * @param moviesFile
     * @param movieActorFile
     * @throws IOException
     */
    public KevinBacon(String actorsFile, String moviesFile, String movieActorFile) throws IOException {
        GraphBuilder graphy = new GraphBuilder(actorsFile, moviesFile, movieActorFile);
        this.graph = graphy.buildGraph();
        welcome();
    }

    /**
     * prints a list of all vertices with infinite separation - that is, vertices that
     * cannot be reached by bfs
     * @param actor
     */
    public void infSeparation(String actor){
        GraphLib.missingVertices(graph, tree);
    }

    /**
     * resets the center of the universe to the provided actor, recalculating bfs tree
     * @param name valid actor
     */
    public void newCenter(String name){
        // reset center of universe
        this.centerUniverse = name;
        // reset bfs tree with new center
        this.tree = GraphLib.bfs(graph, name);
        // calculate total number of vertices
        int total = graph.numVertices();
        // find number of vertices that cannot be reached by bfs
        int missing = GraphLib.missingVertices(graph,  tree).size();
        // calculate all connected vertices
        int unconnected = total - missing;
        System.out.println(name + " is now the center of the acting universe, connected to "+ unconnected +
                "/" + total + " actors with average separation " + GraphLib.averageSeparation(tree, name));

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
    public void bestCenters(int n) {
        if(n > tree.numVertices() - GraphLib.missingVertices(graph,  tree).size()+1) {
            System.out.println("You input a number larger than the amount of actors. Try again.\n");
            getUserInput();
        } else {
            Map<Double, String> averages = new TreeMap<>();
            for (String actor : tree.vertices()) {
                Double avg = GraphLib.averageSeparation(tree, actor);
                averages.put(avg, actor);
            }
            Map<Double, String> sortedAverages = new TreeMap<>(averages);
            List<String> sortedNames = new ArrayList<>();
            List<Double> sortedSeps = new ArrayList<>();
            for (Double key : sortedAverages.keySet()) {
                sortedNames.add(sortedAverages.get(key));
                sortedSeps.add(key);
            }
            if (n > 0) {
                int i = 0;
                while (i < n) {
                    System.out.println(sortedNames.get(i) + " has average separation " + sortedSeps.get(i) + "\n");
                    i += 1;
                }
            } else {
                int i = sortedNames.toArray().length - 1;
                for (int j = 0; j < Math.abs(n); j++) {
                    System.out.println(sortedNames.get(i) + " has average separation " + sortedSeps.get(i) + "\n");
                    i -= 1;
                }
            }
            getUserInput();
        }
    }
    public void sortDegree(int low, int high) {
        List<String> vertices = GraphLib.verticesByInDegree(tree);
        for(String vert: vertices) {
            int degree = tree.inDegree(vert);
            if((degree >= low) && (degree <= high)) {
                System.out.println(vert + " has degree " + degree);
            }
        }
    }
    public void sortSeparation(int low, int high) {

    }

    public void welcome() {
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
        System.out.println(centerUniverse + "game > \n");
        String line = input.nextLine();
        String func;
        String param;
        if(line.contains(" ")) {
            func = line.substring(0, line.indexOf(' '));
            param = line.substring(line.indexOf(' ') + 1);
        }
        else {
            func = line;
            param = "";
        }
        System.out.println(func);
        if (func.length() == 0){
            System.out.println("Please enter a name.\n");
            getUserInput();
        }
        switch (func) {
            case "c" -> {
                try {
                    int n = Integer.parseInt(param);
                    bestCenters(n);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid number. Try again\n");
                    getUserInput();
                }
            }
            case "d" -> {
                String[] pair = param.split("// ");
                if (pair.length == 3){
                    try {
                        int low = Integer.parseInt(pair[0]);
                        int high = Integer.parseInt(pair[1]);
                        sortDegree(low, high);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid entry. Try again\n");
                        getUserInput();
                    }
                } else {
                    System.out.println("Wrong number of inputs. Try again");
                    getUserInput();
                }
            }
            case "i" -> infSeparation(centerUniverse);
            case "p" -> {
                if (graph.hasVertex(param)) {
                    findPath(param);
                } else {
                    System.out.println("Please enter a valid actor");
                    getUserInput();
                }
            }
            case "s" -> {
                String[] pair = param.split("// ");
                System.out.println(pair[0]);
                if (pair.length == 2){
                    try {
                        int low = Integer.parseInt(pair[0]);
                        int high = Integer.parseInt(pair[1]);
                        sortSeparation(low, high);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid entry. Try again\n");
                        getUserInput();
                    }
                } else {
                    System.out.println("Wrong number of inputs. Try again");
                    getUserInput();
                }
            }
            case "u" -> {
                if (graph.hasVertex(param)) {
                    findPath(param);
                } else {
                    System.out.println("Please enter a valid actor");
                    newCenter(param);
                }
            }
            case "q" -> System.exit(0);
            default -> {
                System.out.println("Invalid entry. Try again\n");
                getUserInput();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        //KevinBacon game = new KevinBacon("actors.txt", "movies.txt", "movie-actors.txt");
    }
}