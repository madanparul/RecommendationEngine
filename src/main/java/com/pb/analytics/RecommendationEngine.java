package com.pb.analytics;

import java.io.IOException;

import com.pb.analytics.dto.InputDto;
import com.pb.analytics.dto.RecommendationsDto;

public interface RecommendationEngine {
	//public void init() throws Exception;
	public RecommendationsDto generateRecommendations(InputDto input) throws Exception;
}
