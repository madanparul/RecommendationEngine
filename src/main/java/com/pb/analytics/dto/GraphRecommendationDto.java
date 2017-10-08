package com.pb.analytics.dto;

public class GraphRecommendationDto extends RecommendationDto{
	String userId= null;
	String productName= null;
	Double score= null;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getproductName() {
		return productName;
	}
	public void setproductName(String productName) {
		this.productName = productName;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "RecommendationDto [userId=" + userId + ", productName=" + productName + ", score=" + score + "]";
	}
	
}
