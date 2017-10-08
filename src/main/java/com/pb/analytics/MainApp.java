package com.pb.analytics;

import java.io.IOException;

import com.pb.am.recommendation.AssociationMiningApp;
import com.pb.analytics.dto.InputDto;
import com.pb.analytics.dto.RecommendationsDto;
import com.pb.graphdb.recommendation.GraphRecomApp;
import com.pb.utils.Utils;

public class MainApp {
	
	public enum recommendationType{AssociationMining,GraphRecommendation}
	
	public RecommendationsDto generateRecommendation(String name, String type) throws Exception{
		RecommendationEngine engine=null;
		InputDto input = new InputDto();
		if(type.equalsIgnoreCase(recommendationType.AssociationMining.toString())){
			engine=AssociationMiningApp.getInstance();
		}else if(type.equalsIgnoreCase(recommendationType.GraphRecommendation.toString())){
			engine=GraphRecomApp.getInstance();
			input.getInputMap().put("name",name);
		}
		RecommendationsDto retObj = engine.generateRecommendations(input);
		return retObj;
	}
	public static void main(String [] args) throws Exception {
		MainApp obj = new MainApp();
		RecommendationsDto retObj = obj.generateRecommendation(null, "AssociationMining");
		System.out.println(retObj.toString());
	}
}

