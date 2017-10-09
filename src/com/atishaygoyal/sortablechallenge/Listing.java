/**
 * 
 */
package com.atishaygoyal.sortablechallenge;

import org.json.simple.JSONObject;

/**
 * @author atishay
 * to store listings.txt
 */
public class Listing {
	private String title;
	private String manufacturer;
	private String currency;
	private String price;
	
	public Listing(String title, String manufacturer, String currency, String price) {
		this.title = title;
		this.manufacturer = manufacturer;
		this.currency = currency;
		this.price = price;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("price", this.price);
		obj.put("currency", this.currency);
		obj.put("manufacturer", this.manufacturer);
		obj.put("title", this.title);
		return obj.toJSONString();
	}
}
