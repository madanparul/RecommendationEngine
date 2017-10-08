package com.pb.analytics.dto;

import java.util.ArrayList;
import java.util.List;

public class AMRecommendationFrequentSetDto {
	Integer size;
	List<AMRecommendationCommodityDto> commodityList = new ArrayList<AMRecommendationCommodityDto>();
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public List<AMRecommendationCommodityDto> getCommodityList() {
		return commodityList;
	}
	public void setCommodityList(
			List<AMRecommendationCommodityDto> commodityList) {
		this.commodityList = commodityList;
	}
	@Override
	public String toString() {
		return "AMRecommendationFrequentSetDto [size=" + size
				+ ", commodityList=" + commodityList + "]";
	}
	
	
}
