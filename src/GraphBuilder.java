import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphBuilder {
    public String actorsFile;
    public String moviesFile;
    public String movieActorsFile;


    //constructor that loads sets an empty instance variable for each of the three files we need
    public GraphBuilder(String actorsFile, String moviesFile, String movieActorsFile){
        this.actorsFile = actorsFile;
        this.moviesFile = moviesFile;
        this.movieActorsFile = movieActorsFile;
    }
    //generic method that builds a map out of files with entries separated by |
    public HashMap<String, String> buildMap(String fileName) throws IOException{
        HashMap<String, String> map = new HashMap<String, String>();
        BufferedReader file = null;
        try{//make sure file exists
            file = new BufferedReader(new FileReader(fileName));
        }catch (FileNotFoundException e){
            System.err.println("Unable to open file.\n" + e.getMessage());
        }
        try { //set quantity before | as key, after | as value
            String line;
            while ((line = file.readLine()) != null) {
                String[] temp = line.split("\\|");
                map.put(temp[0], temp[1]);
            }
        }catch (IOException e){
            System.err.println("Error reading file.\n" + e.getMessage());
        }
        try{ //make sure to close file
            file.close();
        } catch (IOException e){
            System.err.println("Error closing file.\n" + e.getMessage());
        }
        return map;
    }

    public AdjacencyMapGraph<String, Set<String>> buildGraph() throws IOException {
        AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
        //map of actors where each key is actor ID, each value is actor name
        HashMap<String, String> actorsID = buildMap(actorsFile);
        //map of movies where each key is movie ID, each value is movie title
        HashMap<String, String> moviesID = buildMap(moviesFile);
        //map of where each key is movie name, each value is set of all actors in the movie
        HashMap<String, Set<String>> movieActors = new HashMap<String, Set<String>>();

        BufferedReader file = null;
        //add a vertex in the graph for each actor
        for (String actor : actorsID.values()) {
            graph.insertVertex(actor);
        }
        //open the file where each line has a movie ID and actor ID (meaning the actor was in that movie)
        try {
            file = new BufferedReader(new FileReader(movieActorsFile));
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open file.\n" + e.getMessage());
        }

        try {//while there's still data left to read
            String line;
            while ((line = file.readLine()) != null) {
                //temp variable to store the line
                String[] temp = line.split("\\|");
                //the quantity after | is the actor ID
                String actor = actorsID.get(temp[1]);
                //the quantity before | is movie ID
                String movie = moviesID.get(temp[0]);
                //if we've already added the movie to the map, add the actor to the set of all actors in the movie
                if (movieActors.containsKey(movie)) {
                    movieActors.get(movie).add(actor);
                } else { //otherwise, make a new set with that actor in it
                    Set<String> set = new HashSet<String>();
                    set.add(actor);
                    //add the movie, actors key-value pair to the map
                    movieActors.put(movie, set);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file.\n" + e.getMessage());
        }
        // For each movie
        for (String movie : movieActors.keySet()) {
            // for each actor in movie, find all costars
            for (String actor : movieActors.get(movie)) {
                for (String costar : movieActors.get(movie)) {
                    if (!actor.equals(costar)) {
                        // If this is the first movie with both actors examined, make a new edge
                        if (!graph.hasEdge(actor, costar)) {
                            // Create a set containing all movies in which both actors appear; at first, just the current
                            Set bothMovie = new HashSet<String>();
                            bothMovie.add(movie);
                            graph.insertUndirected(actor, costar, bothMovie);
                        }
                        // If there is already an edge between actors, add the new movie to the set
                        else {
                            graph.getLabel(actor, costar).add(movie);
                        }
                    }
                }
            }
        }
            return graph;
    }

    /**
     * test method, determines if two actors have starred in a movie together
     * @param graph
     * @param actor1
     * @param actor2
     * @return
     */
    private String baconTest(Graph graph, String actor1, String actor2) {
        if (graph.hasEdge(actor1, actor2)) {
            //if there's an edge between them, they've been in a movie together, so print that set
            return (actor1 + " starred in " + graph.getLabel(actor1, actor2) + " with " + actor2);
        }//otherwise, say that they haven't been in a movie together
        else{
            return (actor1 + " has not been in a film with " + actor2);
        }
    }

    public static void main(String[] args) throws IOException{
        System.out.println("Test 1: graph built with test files");
        GraphBuilder test1 = new GraphBuilder("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        Graph graph1 = test1.buildGraph();
        System.out.println(graph1);
        System.out.println(test1.baconTest(graph1,"Alice", "Kevin Bacon"));

        System.out.println("\nTest 2: graph built with complete files");
        GraphBuilder test2 = new GraphBuilder("actors.txt", "movies.txt", "movie-actors.txt");
        Graph graph2 = test2.buildGraph();
        System.out.println(test2.baconTest(graph2,"Meryl Streep", "Kevin Bacon"));
        System.out.println(test2.baconTest(graph2, "Kevin Bacon", "Joe DiMaggio"));
        System.out.println(test2.baconTest(graph2, "Kevin Bacon", "Kevin Bacon"));

    }
}
