import java.io.*;
import java.util.*;

/**
 * Kevin Bacon Game. Starting with an actor, see if they have been in a movie with
 * someone who has been in a movie with someone who has been in a movie ... who has
 * been in a movie with Kevin Bacon.
 * @author willhodgson, Dartmouth CS10, Winter 2023
 * @author cullumtwiss, Dartmouth CS10, Winter 2023
 */

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
        System.out.println(GraphLib.missingVertices(graph, tree));
        getUserInput();
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
                "/" + total + " actors with average separation " + GraphLib.averageSeparation(tree, name) +1);

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
        getUserInput();
    }

    /**
     * list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
     * @param n number of centers to print
     */
    public void bestCenters(int n) {
        // number of actors must be less than or equal to total number of actors
        if(n > tree.numVertices() - GraphLib.missingVertices(graph,  tree).size()+1) {
            System.out.println("You input a number larger than the amount of actors. Try again.\n");
            getUserInput();
        } else {
            // create a new map that holds an actor and their average degree of separation
            Map<Double, String> averages = new TreeMap<>();
            // For each actor
            for (String actor : tree.vertices()) {
                // get their average degree of separation to all other actors and add to map
                Double avg = GraphLib.averageSeparation(tree, actor)+1;
                averages.put(avg, actor);
            }
            Map<Double, String> sortedAverages = new TreeMap<>(averages);
            List<String> sortedNames = new ArrayList<>();
            List<Double> sortedSeps = new ArrayList<>();
            for (Double key : sortedAverages.keySet()) {
                sortedNames.add(sortedAverages.get(key));
                sortedSeps.add(key);
            }
            // if n is positive,
            if (n > 0) {
                int i = 0;
                // print the first n elements in the sorted list with their separation
                while (i < n) {
                    System.out.println(sortedNames.get(i) + " has average separation " + sortedSeps.get(i) + "\n");
                    i += 1;
                }
            }
            // if n is negative
            else {
                int i = sortedNames.toArray().length - 1;
                // print the last n elements in the sorted list with their separation
                for (int j = 0; j < Math.abs(n); j++) {
                    System.out.println(sortedNames.get(i) + " has average separation " + sortedSeps.get(i) + "\n");
                    i -= 1;
                }
            }
            getUserInput();
        }
    }

    /**
     * print a list of actors with degree between two provided valus
     * @param low threshold for inclusion in list
     * @param high maximum degree for inclusion in list
     */
    public void sortDegree(int low, int high) {
        // create a list of vertices sorted by inDegree
        List<String> vertices = GraphLib.verticesByInDegree(tree);
        // for each actor in the list
        for(String vert: vertices) {
            // get their inDegree
            int degree = tree.inDegree(vert);
            // if their indegree falls between the provided goalposts
            if((degree >= low) && (degree <= high)) {
                // print their name and inDegree
                System.out.println(vert + " has degree " + degree);
            }
        }
        getUserInput();
    }

    /**
     * print list actors sorted by non-infinite separation from the current center,
     * with separation between low and high
     * @param low minimum degree of separation
     * @param high maximum degree of separation
     */
    public void sortSeparation(int low, int high) {
        // create a map to associate an actor with their degree of separation
        Map<String, Integer> separationMap = new HashMap<>();
        // create a list of vertices sorted by separation
        List<String> vertices = new ArrayList<String>();
        // for each actor
        for(String vertex : graph.vertices()){
            // if actor is connected to center
            if(tree.hasVertex(vertex)){
                // get that actors separation, associate it with their name
                int separation = GraphLib.getPath(tree, vertex).size();
                if(separation >= low && separation <= high){
                    separationMap.put(vertex, separation);
                    // and add their name to the list
                    vertices.add(vertex);
                }
            }
        }
        // sort list in ascending order of separation
        vertices.sort((String v1, String v2)-> separationMap.get(v1) - separationMap.get(v2));
        // print each actor in list and their separation
        for (String v : vertices){
            System.out.println(v + " has separation " + separationMap.get(v));
        }
        getUserInput();
    }

    /**
     * method to start game.Prints valid commands, sets center of universe to default (Kevin Bacon)
     * and begin prompting user input
     */
    public void welcome() {
        // Print list of valid commands
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n" +
                "\n" );
        // set center of universe to default
        newCenter(centerUniverse);
        // begin prompting user input
        getUserInput();
    }

    /**
     * Method to take user input from the command line and determine
     */
    public void getUserInput() {
        // Create a scanner to receive input
        Scanner input = new Scanner(System.in);
        System.out.println(centerUniverse + "game > \n");
        String line = input.nextLine();
        String func;
        String param;
        // Split line by first space character
        if(line.contains(" ")) {
            // get command character
            func = line.substring(0, line.indexOf(' '));
            // get provided parameter - name, integers, etc.
            param = line.substring(line.indexOf(' ') + 1);
        }
        // if there is no space character, then just a command call
        else {
            func = line;
            param = "";
        }
        // user input must not be empty
        if (func.length() == 0){
            System.out.println("Please enter a name.\n");
            getUserInput();
        }
        switch (func) {
            // function c - top/bottom centers of universe
            case "c" -> {
                try {
                    // get the provided number of centers and print top or bottom n centers
                    int n = Integer.parseInt(param);
                    bestCenters(n);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid number. Try again\n");
                    getUserInput();
                }
            }
            // function d - actors sorted by inDegree
            case "d" -> {
                // split the provided parameter into two inputs
                String[] pair = param.split("\\ ");
                // must contain exactly two inputs
                if (pair.length == 2){
                    try {
                        // determine the goalposts and print a sorted list of actors with indegree between them
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
            // function i - list actors with infinite separation
            case "i" -> infSeparation(centerUniverse);
            // function p - find path from provided actor to center of universe
            case "p" -> {
                // graph must contain provided actor
                if (graph.hasVertex(param)) {
                    findPath(param);
                } else {
                    System.out.println("Please enter a valid actor");
                    getUserInput();
                }
            }
            // function s - list actors sorted by separation between two goalposts
            case "s" -> {
                // split parameter into two inputs
                String[] pair = param.split("\\ ");
                // must contain exactly two inputs
                if (pair.length == 2){
                    try {
                        // parse integer goalposts and print sorted list of separation between them
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
            // function u - set a new center of universe
            case "u" -> {
                // graph must contain actor
                if (graph.hasVertex(param)) {
                    newCenter(param);
                } else {
                    System.out.println("Please enter a valid actor");
                }
                getUserInput();
            }
            // function q - quit program
            case "q" -> System.exit(0);
            default -> {
                System.out.println("Invalid entry. Try again\n");
                getUserInput();
            }
        }
    }


    /**
     * Main method with test case and complete game
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        KevinBacon test0 = new KevinBacon("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        //KevinBacon game = new KevinBacon("actors.txt", "movies.txt", "movie-actors.txt");
    }
}