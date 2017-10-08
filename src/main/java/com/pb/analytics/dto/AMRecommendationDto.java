package com.pb.analytics.dto;

import java.util.ArrayList;
import java.util.List;

public class AMRecommendationDto extends RecommendationDto{
	
	List<AMRecommendationFrequentSetDto> frequentSetList = new ArrayList<AMRecommendationFrequentSetDto>();

	public List<AMRecommendationFrequentSetDto> getFrequentSetList() {
		return frequentSetList;
	}

	@Override
	public String toString() {
		return "AMRecommendationDto [frequentSetList=" + frequentSetList + "]";
	}

	public void setFrequentSetList(
			List<AMRecommendationFrequentSetDto> frequentSetList) {
		this.frequentSetList = frequentSetList;
	}

}
