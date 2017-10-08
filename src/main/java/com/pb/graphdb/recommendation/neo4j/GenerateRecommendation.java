package com.pb.graphdb.recommendation.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import com.pb.analytics.dto.GraphRecommendationDto;
import com.pb.analytics.dto.RecommendationsDto;
import com.pb.graphdb.recommendation.neo4j.DataIngestion.nodeLabels;

public class GenerateRecommendation {
	GraphDatabaseService di = null; 
	Transaction txn =  null;
	private Node getUserNode(String userName){
		Result r = di.execute("MATCH(n:USER) Where n.Name='"+userName+"' Return n");
		Node node = (Node)r.next().get("n");

		return node;	
	}
	private ArrayList<Node> getAllProducts(){
		ArrayList<Node> productList = new ArrayList<Node>();
		Result r = di.execute("MATCH(n:PRODUCT) Return n");
		while(r.hasNext()){
			Node node = (Node)r.next().get("n");
			productList.add(node);
		}
		return productList;	
	}
	public Result executeQuery(String query){
		Result result = di.execute(query);
		return result;
	} 

	public RecommendationsDto generateRecomm(String userName){
		di = DatabaseInit.getInstance();
		txn = di.beginTx();
		Node usernode =getUserNode(userName);
		// System.out.println(usernode.getProperty("Name"));
		ArrayList<Node> productList = getAllProducts();
		HashMap<String,Double> recommMap =  new HashMap<String,Double>();
		for(Node n:productList){
			recommMap.put((String)n.getProperty("id"), 0D);			
		}
		String prodBasedRecommQuery = "Match(n:USER)-[:BOUGHT]->(p:PRODUCT) Match(p:PRODUCT)-[r:SIMILARTO]->(p1:PRODUCT) Where n.Name='"+ userName +"' AND r.Score >= 0.4 AND NOT (id(p)= id(p1)) Return p1,r";
		Result productBasedResult = executeQuery(prodBasedRecommQuery);
		while(productBasedResult.hasNext()){
			Map<String,Object> productMap = productBasedResult.next();
			Node productNode = (Node)productMap.get("p1");
			Relationship rel = (Relationship)productMap.get("r");
			// System.out.println(productNode.getProperty("id")+"------"+rel.getProperty("Score"));
			double previousScore = recommMap.get(productNode.getProperty("id"));
			recommMap.put((String)productNode.getProperty("Name"), previousScore+(Double)rel.getProperty("Score"));
			
		}
		String userBasedRecommQuery = "Match(n:USER)-[r:SIMILARTO]->(n1:USER) Match(n1:USER)-[:BOUGHT]->(p1:PRODUCT) Where n.Name='"+userName+"' AND r.Score >= 0.3 Return p1,r";
		Result userBasedResult = executeQuery(userBasedRecommQuery);
		while(userBasedResult.hasNext()){
			Map<String,Object> userMap = userBasedResult.next();
			Node userNode = (Node)userMap.get("p1");
			Relationship rel1 = (Relationship)userMap.get("r");
			// System.out.println(userNode.getProperty("id")+"------"+rel1.getProperty("Score"));
			double previousScore = recommMap.get(userNode.getProperty("id"));
			recommMap.put((String)userNode.getProperty("Name"), previousScore+(Double)rel1.getProperty("Score"));
			
		}
		RecommendationsDto retObj = new RecommendationsDto();
		Iterator<String> it = recommMap.keySet().iterator();
		while(it.hasNext()){
			GraphRecommendationDto obj = new GraphRecommendationDto();
			obj.setUserId((String)usernode.getProperty("Name"));
			String productId = it.next();
			Double score = recommMap.get(productId);
			if(score>=0.1){
				obj.setproductName(productId);
				obj.setScore(score);
				retObj.getRecommendationList().add(obj);
			}
		}
		txn.success();
		txn.close();
		return retObj;
	}
}
