package com.pb.graphdb.recommendation.neo4j;

import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class DatabaseInit {
    private static GraphDatabaseFactory graphDatabaseFactory = new GraphDatabaseFactory();
    private static GraphDatabaseService graphDatabaseService = null;
    private static String dbPath = null;
    public static void setDbPath(String dbPathName){
    	dbPath = dbPathName;
    }
    public static GraphDatabaseService getInstance() {
        if(graphDatabaseService == null) {
        	if(dbPath == null){
        		return null;
        	}
            graphDatabaseService = graphDatabaseFactory.newEmbeddedDatabase(new File(dbPath));
        }
        return graphDatabaseService;
    }
    
    public static void closeInstance() {
        graphDatabaseService.shutdown();
    }
    
    public static void main(String [] args) throws Exception {
        DatabaseInit.getInstance();
    }
}
