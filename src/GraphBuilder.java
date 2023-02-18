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
        System.out.println(map);
        return map;
    }

    public AdjacencyMapGraph<String, Set<String>> buildGraph() throws IOException {
        AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
        HashMap<String, String> actorsID = buildMap(actorsFile);
        HashMap<String, String> moviesID = buildMap(moviesFile);
        HashMap<String, Set<String>> movieActors = new HashMap<String, Set<String>>();
        BufferedReader file = null;

        for (String actor : actorsID.keySet()) {
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
            System.out.println(movieActors);

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
                        //TODO actor and costart need to be vertices
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

    public static void main(String[] args) throws IOException{
        GraphBuilder test = new GraphBuilder("actorsTest.txt", "moviesTest.txt", "movie-actorsTest.txt");
        System.out.println(test.buildGraph());

    }
}
