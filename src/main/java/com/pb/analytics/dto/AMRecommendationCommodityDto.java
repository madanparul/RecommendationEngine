package com.pb.analytics.dto;

public class AMRecommendationCommodityDto {
	String id;
	String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "AMRecommendationCommodityDto [id=" + id + ", name=" + name
				+ "]";
	}
}
