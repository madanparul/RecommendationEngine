package com.pb.am.recommendation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.pb.am.recommendation.algo.AprioriAlgo;
import com.pb.am.recommendation.algo.DICAlgo;
import com.pb.analytics.RecommendationEngine;
import com.pb.analytics.dto.InputDto;
import com.pb.analytics.dto.RecommendationDto;
import com.pb.analytics.dto.RecommendationsDto;
import com.pb.utils.Utils;

public class AssociationMiningApp implements RecommendationEngine {

	private enum algo_type{APRIORI,DIC};

	static AssociationMiningApp obj = null;
	Map<String,String> productMap= new HashMap<String,String>();

	public Map<String, String> getProductMap() {
		return productMap;
	}

	private AssociationMiningApp() {}

	public static AssociationMiningApp getInstance() throws Exception {
		if(obj == null) {
			obj = new AssociationMiningApp();
			obj.init();
		}
		return obj;
	}

	private void init() throws Exception {

		BufferedInputStream fstream=(BufferedInputStream)DICAlgo.class.getResourceAsStream("/ProductList");
		DataInputStream file_in = new DataInputStream(fstream);
		BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));

		String strLine = null;
		while((strLine=data_in.readLine()) != null){
			String [] dArr=strLine.split(",");			
			productMap.put(dArr[0], dArr[1]);
		}
	}
	public RecommendationsDto generateRecommendations(InputDto input) throws Exception{

		String algorithm_type=Utils.getResourceProperty("2.algorithm");
		RecommendationsDto return_obj=new RecommendationsDto();
		
		if(algorithm_type.equalsIgnoreCase(algo_type.APRIORI.toString())){
			AprioriAlgo algoObj= new AprioriAlgo();
			RecommendationDto obj = algoObj.getAssosicationRules(productMap);
			return_obj.getRecommendationList().add(obj);
		} else if(algorithm_type.equalsIgnoreCase(algo_type.DIC.toString())){
			DICAlgo algoObj=new DICAlgo();
			RecommendationDto obj = algoObj.getAssosicationRules(productMap);
			return_obj.getRecommendationList().add(obj);			
		}
		return return_obj;

	}
}
