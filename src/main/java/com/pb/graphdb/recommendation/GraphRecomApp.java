package com.pb.graphdb.recommendation;

import java.io.IOException;

import com.pb.am.recommendation.AssociationMiningApp;
import com.pb.analytics.RecommendationEngine;
import com.pb.analytics.dto.InputDto;
import com.pb.analytics.dto.RecommendationsDto;
import com.pb.graphdb.recommendation.neo4j.DataIngestion;
import com.pb.graphdb.recommendation.neo4j.DatabaseInit;
import com.pb.graphdb.recommendation.neo4j.GenerateRecommendation;
import com.pb.utils.Utils;

public class GraphRecomApp implements RecommendationEngine{
	static GraphRecomApp obj = null;
	private boolean isDBLoaded = false;
	private void init() throws Exception {
		ingestData();
		isDBLoaded = true;
	}
	
	private GraphRecomApp(){
		
	}
	
	public static GraphRecomApp getInstance() throws Exception {
		if(obj == null) {
			obj = new GraphRecomApp();
			obj.init();
		}
		return obj;
	}
	private void ingestData() throws Exception {
		DatabaseInit.setDbPath(Utils.getResourceProperty("1.DBpath"));		
		DataIngestion di = new DataIngestion();
		di.addNodes(Utils.getResourceProperty("1.productDetailsFile"),Utils.nodeType.PRODUCT.toString());
		di.addNodes(Utils.getResourceProperty("1.userDetailsFile"), Utils.nodeType.USER.toString());

		 System.out.println("Added All Nodes");

		di.addRelation(Utils.getResourceProperty("1.relationDetailsFile"));
		di.userRelation();
		di.prodRelation();

		 System.out.println("Added All RELATIONSHIP");

	}

	public RecommendationsDto generateRecommendations(InputDto input) throws IOException{
		if(!isDBLoaded) {
			System.out.println("Graph DB Not Initialized");
			return null;
		}
		DatabaseInit.setDbPath(Utils.getResourceProperty("1.DBpath"));
		GenerateRecommendation gr = new GenerateRecommendation();
		RecommendationsDto obj = gr.generateRecomm(input.getInputMap().get("name"));
		return obj;

	}
}
