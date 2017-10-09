package com.atishaygoyal.sortablechallenge;

/**
 * @author atishay
 * to store products.txt
 */

public class Product {
	private String productName;
	private String productManufacturer;
	private String productFamily;
	private String productModel;
	
	public Product(String productName, String productManufacturer, Object productFamily, String productModel) {
		this.productName = productName;
		this.productManufacturer = productManufacturer;
		if(productFamily != null) {
			this.productFamily = (String) productFamily;
		} else {
			this.productFamily = null;
		}
		
		this.productModel = productModel;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductManufacturer() {
		return productManufacturer;
	}
	public void setProductManufacturer(String productManufacturer) {
		this.productManufacturer = productManufacturer;
	}
	public String getProductFamily() {
		return productFamily;
	}
	public void setProductFamily(String productFamily) {
		this.productFamily = productFamily;
	}
	public String getProductModel() {
		return productModel;
	}
	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}
	
}
