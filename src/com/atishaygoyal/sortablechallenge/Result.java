/**
 * 
 */
package com.atishaygoyal.sortablechallenge;

import java.util.HashSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author atishay
 * to store result objects
 */
public class Result {
	private String productName;
	private HashSet<Listing> listings;
	public Result(String productName, HashSet<Listing> listings) {
		this.productName = productName;
		this.listings = listings;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public HashSet<Listing> getListings() {
		return listings;
	}
	public void setListings(HashSet<Listing> listings) {
		this.listings = listings;
	}
	public int getListSize() {
		return this.listings.size();
	}
	
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		JSONArray listings = new JSONArray();
		listings.addAll(this.listings);
		obj.put("listings", listings);
		obj.put("product_name", this.productName);
		return obj.toJSONString();
	}
}
