
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class Neo4J {
    static Driver driver = GraphDatabase.driver("neo4j+s://06d5deaf.databases.neo4j.io", AuthTokens.basic("neo4j", "5Jsnu6Mcr2dABF7YJkOtuSi1aBZm6FSWehJ-yVmrP2E"));
    static Session session = driver.session();

    //Get all the categories in the DB
    public List<String> retrieveCategories()
    {
        int index = 0;
        List<String> categories = new ArrayList<>();

        Result result = session.run("MATCH (g:genre) RETURN g.listed_in as category");
        while (result.hasNext()) {
            Record record = result.next();
           categories.add(index, record.get("category").asString());
            index++;
        }

        Collections.sort(categories);
        return categories;
    }

    public String retrieveDescription(String movieTitle)
    {
        Result result = session.run("MATCH (m:movie {title: \"" + movieTitle + "\"}) RETURN m.description AS description");
        Record record = result.next();

        return record.get("description").asString();
    }

    //Get all the movies in the DB
    public List<String> retrieveMovies()
    {
        int index = 0;
        List<String> movies = new ArrayList<String>();

        Result result = session.run("MATCH (m:movie) RETURN m.title as title");
        while (result.hasNext()) {
            Record record = result.next();
            movies.add(index, record.get("title").asString());
            index++;
        }

        Collections.sort(movies);
        return movies;
    }

    //From a book, get it's category
    public String getMovieCategory(String movieTitle)
    {
        Result result = session.run("MATCH (g:genre)<-[listed_in]-(:movie{title: \"" + movieTitle + "\"}) RETURN g.listed_in AS category");
        Record record = result.next();

        return record.get("category").asString();
    }

    public String getMovieDirector(String movieTitle)
    {
        Result result = session.run("MATCH (d:director)<-[directed_by]-(:movie{title: \"" + movieTitle + "\"}) RETURN d.director AS director");
        Record record=null;
        try {
            record = result.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: no recommendation found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        

        return record.get("director").asString();
    }

    //Query for suggestion based on selection
    public List<String> suggestMovie(String category,String director)
    {
        List<String> movies = new ArrayList<String>();
        List<Integer> rating = new ArrayList<Integer>();

        Result result = session.run("MATCH (f:movie{director: \"" + director + "\"})-[listed_in]->(g:genre {listed_in: \"" + category + "\"}) RETURN f.title AS title, f.rating AS stars ORDER BY f.title ");
        while (result.hasNext()) {
            Record record = result.next();
            movies.add(record.get("title").asString());
            rating.add(record.get("rating", 0));
        }

        //Sorting based on rating of the movies
        final Map<String, Integer> MoviesStarsMap = new HashMap<>();
        for(int i = 0; i < movies.size(); i++) {
            MoviesStarsMap.put(movies.get(i), rating.get(i));
        }
        Collections.sort(movies, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return MoviesStarsMap.get(o1) - MoviesStarsMap.get(o2);
            }
        });

        return movies;
    }



    //Query for suggestion based on selection
    public List<String> suggestMovie(String category)
    {
        List<String> movies = new ArrayList<String>();
        List<Integer> rating = new ArrayList<Integer>();

        Result result = session.run("MATCH (f:movie)-[listed_in]->(g:genre {listed_in: \"" + category + "\"}) RETURN f.title AS title, f.rating AS stars ORDER BY f.director");
        while (result.hasNext()) {
            Record record = result.next();
            movies.add(record.get("title").asString());
            rating.add(record.get("rating", 0));
        }

        //Sorting based on rating of the movies
        final Map<String, Integer> MoviesStarsMap = new HashMap<>();
        for(int i = 0; i < movies.size(); i++) {
            MoviesStarsMap.put(movies.get(i), rating.get(i));
        }
        Collections.sort(movies, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return MoviesStarsMap.get(o1) - MoviesStarsMap.get(o2);
            }
        });

        return movies;
    }

    //Close program
    public static void terminate()
    {
        session.close();
        driver.close();
        System.exit(0);
    }
}