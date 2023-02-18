import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphBuilder {
    public String actorsFile;
    public String moviesFile;
    public String movieActorsFile;



    public GraphBuilder(String actorsFile, String moviesFile, String movieActorsFile){
        this.actorsFile = actorsFile;
        this.moviesFile = moviesFile;
        this.movieActorsFile = movieActorsFile;
    }

    public HashMap<String, String> buildMap(String fileName) throws IOException{
        HashMap<String, String> map = new HashMap<String, String>();
        BufferedReader file = null;
        try{
            file = new BufferedReader(new FileReader(fileName));
        }catch (FileNotFoundException e){
            System.err.println("Unable to open file.\n" + e.getMessage());
        }
        try {
            String line;
            while ((line = file.readLine()) != null) {
                String[] temp = line.split("\\|");
                map.put(temp[0], temp[1]);
            }
        }catch (IOException e){
            System.err.println("Error reading file.\n" + e.getMessage());
        }
        try{
            file.close();
        } catch (IOException e){
            System.err.println("Error closing file.\n" + e.getMessage());
        }
        return map;
    }

    public AdjacencyMapGraph<String, Set<String>> buildGraph() throws IOException {
        AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
        HashMap<String, String> actorsID = buildMap(actorsFile);
        HashMap<String, String> moviesID = buildMap(moviesFile);
        HashMap<String, Set<String>> movieActors = new HashMap<String, Set<String>>();
        BufferedReader file = null;

        for (String actor : actorsID.values()) {
            graph.insertVertex(actor);
        }

        try {
            file = new BufferedReader(new FileReader(movieActorsFile));
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open file.\n" + e.getMessage());
        }

        try {
            String line;
            while ((line = file.readLine()) != null) {
                String[] temp = line.split("\\|");
                String actor = actorsID.get(temp[1]);
                String movie = moviesID.get(temp[0]);
                if (movieActors.containsKey(movie)) {
                    movieActors.get(movie).add(actor);
                } else {
                    Set<String> set = new HashSet<String>();
                    set.add(actor);
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

    private String baconTest(Graph graph, String actor1, String actor2) {
        if (graph.hasEdge(actor1, actor2)) {
            return (actor1 + " starred in " + graph.getLabel(actor1, actor2) + " with " + actor2);
        }
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
