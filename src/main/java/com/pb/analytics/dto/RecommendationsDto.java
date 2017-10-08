package com.pb.analytics.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsDto {
	List<RecommendationDto> recommendationList = new ArrayList<RecommendationDto>();

	public List<RecommendationDto> getRecommendationList() {
		return recommendationList;
	}

	@Override
	public String toString() {
		return "RecommendationsDto [recommendationList=" + recommendationList + "]";
	}
	
}
