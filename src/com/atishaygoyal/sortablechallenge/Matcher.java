package com.atishaygoyal.sortablechallenge;

import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.simple.parser.JSONParser;

/**
 * @author atishay
 * Main file to perform matching
 */

public class Matcher {
	
	/* TODO: Load these from properties file */
	public static String INPUT_FOLDER = "input/";
	public static String OUTPUT_FOLDER = "output/";
	public static String LISTINGS_FILENAME = INPUT_FOLDER + "listings.txt";
	public static String PRODUCTS_FILENAME = INPUT_FOLDER + "products.txt";
	public static String RESULT_FILENAME = OUTPUT_FOLDER + "results.txt";
	public static String TOKEN_PATTERN = " |-|(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		JSONParser parser = new JSONParser();	
		
		// Set storing all the products
		HashSet<Product> productsSet = new HashSet<Product>();
		
		// Set storing all the listings
		HashSet<Listing> listingsSet = new HashSet<Listing>();
		
		// Set storing all the results
		HashSet<Result> resultSet = new HashSet<Result>();
		
		System.out.println("Processing...");
		
	    try{
	    	
	    	/* Reading the set of products */
	    	  	BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_FILENAME));
			    for(String line; (line = br.readLine()) != null; ) {
			        JSONObject product = (JSONObject) parser.parse(line);
			        Product p = new Product(
			        		(String) product.get("product_name"), 
			        		(String) product.get("manufacturer"), 
			        		product.get("family"),
			        		(String) product.get("model"));
			        		
			        productsSet.add(p);
			    }
			    br.close();
	    	  
			 /* Reading the set of listings */
				  br = new BufferedReader(new FileReader(LISTINGS_FILENAME));
					    for(String line; (line = br.readLine()) != null; ) {
					        JSONObject product = (JSONObject) parser.parse(line);
					        Listing l = new Listing(
					        		(String) product.get("title"), 
					        		(String) product.get("manufacturer"), 
					        		(String) product.get("currency"), 
					        		(String) product.get("price"));
					        listingsSet.add(l);
					    }
				  br.close();
		  	  
			  /* Iterate through each product to find its matching listings */
		  	  for(Product product : productsSet) {
		  		  
		  		  /* All the listings in eligibleListingSet match the product manufacturer*/
		  		  HashSet<Listing> eligibleListingSet = new HashSet<Listing>();
		  		  /* We try to build matchingListingSet by applying our filter functions */ 
		  		  HashSet<Listing> matchingListingSet = new HashSet<Listing>();
		  		  
		  		  /* Initialize eligibleListingSet*/
		  		  for(Listing rawListing: listingsSet) {
		  			  if(containsIgnoreCase(rawListing.getManufacturer(),product.getProductManufacturer())) {
		  				eligibleListingSet.add(rawListing);
		  			  }
		  		  }	  
		  		  
		  		  if(eligibleListingSet.size()!=0) {
		  			  
		  			  // Filter 1
		  			  matchingListingSet = familyAndModelMatch(eligibleListingSet, product);
		  			 listingsSet.removeAll(matchingListingSet);
		  			
		  			
		  			 
			  		  //Filter 2
			  		  if(matchingListingSet.size()==0) {
			  			matchingListingSet = noFamilyAndModelMatch(eligibleListingSet, product);
				  		listingsSet.removeAll(matchingListingSet);
			  		  }
			  		  
			  		  
			  		 //Filter 3
			  		  if(matchingListingSet.size()==0) {
			  			matchingListingSet = familyAndTokenizedModelMatch(eligibleListingSet, product);
				  		listingsSet.removeAll(matchingListingSet);
			  		  }
			  		  
			  		 //Filter 4
				  		if(matchingListingSet.size()==0) {
				  			matchingListingSet = noFamilyAndTokenizedModelMatch(eligibleListingSet, product);
					  		listingsSet.removeAll(matchingListingSet);
				  		}
			  		 
		  		  } 
		  		  
		  		  //Add the result to result set
		  		  Result result = new Result(product.getProductName(), matchingListingSet);
		  		  resultSet.add(result);
		  	  }
		  	  
			   
		  	PrintStream fileStream = new PrintStream(new File(RESULT_FILENAME));
 		    int totalListings = 0;
 		    int totalProducts = 0;
 		    
 		    /* Create results file */
			   for(Result result : resultSet) {
				   totalListings = totalListings + result.getListSize();
				   if(result.getListSize()!=0) {
					   totalProducts++;
				   }
				   fileStream.println(result.toJSONString());
			   }
			 fileStream.close();
			 long endTime   = System.currentTimeMillis();
			 long totalTime = (endTime - startTime) / 1000;

			 System.out.println("Processing time: " + totalTime + " seconds.");
			 System.out.println("Results file at: " + RESULT_FILENAME);
			 System.out.println("Total listings matched: " + totalListings);
			 System.out.println("Total products with non-empty listings: " + totalProducts);
		  	 
	      } catch(Exception e) {
	    	 /* TODO: More specific exception handling */
	    	  e.printStackTrace();
	      }
	}
	
	/**
	 * returns true if a string consists of only numbers
	 * @param s
	 * @return
	 */
	public static boolean isNumeric(String s) {
		return s.matches("\\d+") ? true : false;
	}
	
	/**
	 * returns string with spaces and hyphens removed
	 * @param s
	 * @return
	 */
	public static String cleanDelimiters(String s) {
		return s.replace(" ","").replace("-", "");
	}
	
	/**
	 * returns the tokenized model string appropriately padded
	 * @param s
	 * @return
	 */
	public static String[] getTokens(String s) {
		String[] tokens = s.split(TOKEN_PATTERN);
		ArrayList<String> paddedTokens = new ArrayList<String>();
		// Add padding to tokens appropriately
		for(String token : tokens) {
			//Add padding if not numeric and length is < 3
			if (!isNumeric(token) && token.length() < 3) {
				token = " " + token + " ";
			} else if(isNumeric(token)) { //Add padding if strictly numeric
				token = " " + token + " ";
			}
			paddedTokens.add(token);
		}
		return paddedTokens.toArray(new String[paddedTokens.size()]);
	}
	
	/**
	 * Filter based on family and model number with variations like hyphenated model, demiliters removed model, etc.
	 * @param listingSet
	 * @param product
	 * @return
	 */
	public static HashSet<Listing> familyAndModelMatch(HashSet<Listing> listingSet, Product product) {
		HashSet<Listing> matchingListingSet = new HashSet<Listing>();
		if(product.getProductFamily()!=null) {
			String pModel = product.getProductModel();
			String delimiterCleanedProductModel = cleanDelimiters(product.getProductModel());
			String hyphenatedProductModel = product.getProductModel().replace(" ", "-");
			String pFamily = product.getProductFamily();
			for(Listing listing : listingSet) {	
				String lTitle = listing.getTitle();
				if(containsIgnoreCase(lTitle, pFamily)) {
					if(containsModel(lTitle,pModel) || containsModel(lTitle, delimiterCleanedProductModel) || containsModel(lTitle, hyphenatedProductModel)) {
						matchingListingSet.add(listing);
					}
				}
			}
		}
		return matchingListingSet;

	}
	
	/**
	 * Filter based on family and tokenized Model number
	 * @param listingSet
	 * @param product
	 * @return
	 */
	public static HashSet<Listing> familyAndTokenizedModelMatch(HashSet<Listing> listingSet, Product product) {
		HashSet<Listing> matchingListingSet = new HashSet<Listing>();
		if(product.getProductFamily()!=null) {
			String pModel = product.getProductModel();
			String pFamily = product.getProductFamily();
			for(Listing listing : listingSet) {
				String lTitle = listing.getTitle();
				int noOfMatches = 0;
				String[] productModelTokens = getTokens(pModel);
				for(String productModelToken : productModelTokens) {
				 if(containsIgnoreCase(lTitle,productModelToken) && containsIgnoreCase(lTitle, pFamily)) {
					noOfMatches++;
				 }
				}
				//Make sure all tokens are present
				if(noOfMatches==productModelTokens.length) {
					matchingListingSet.add(listing);
				}
			}
		}
		return matchingListingSet;
	}
	
	/**
	 * Filters based on just model number with variations like hyphenated model, demiliters removed model, etc.
	 * @param listingSet
	 * @param product
	 * @return
	 */
	public static HashSet<Listing> noFamilyAndModelMatch(HashSet<Listing> listingSet, Product product) {
		HashSet<Listing> matchingListingSet = new HashSet<Listing>();
			String pModel = product.getProductModel();
			String delimiterCleanedProductModel = cleanDelimiters(product.getProductModel());
			String hyphenatedProductModel = product.getProductModel().replace(" ", "-");

			if(!(product.getProductFamily()!=null && isNumeric(pModel))) //We dont need just numeric model match where family is available
			{
				for(Listing listing : listingSet) {	
					String lTitle = listing.getTitle();
					if(containsModel(lTitle,pModel) || containsModel(lTitle, delimiterCleanedProductModel) || containsModel(lTitle, hyphenatedProductModel)) {
						matchingListingSet.add(listing);
					}
				}
			}
		return matchingListingSet;

	}
	
	/**
	 * Filter based on tokenized model number without family check
	 * @param listingSet
	 * @param product
	 * @return
	 */
	public static HashSet<Listing> noFamilyAndTokenizedModelMatch(HashSet<Listing> listingSet, Product product) {
		HashSet<Listing> matchingListingSet = new HashSet<Listing>();
			String pModel = product.getProductModel();
			if(!(product.getProductFamily()!=null && isNumeric(pModel)))
			{
				for(Listing listing : listingSet) {
					String lTitle = listing.getTitle();
					int noOfMatches = 0;
					String[] productModelTokens = getTokens(pModel);
					for(String productModelToken : productModelTokens) {
					 if(containsIgnoreCase(lTitle,productModelToken)) {
						noOfMatches++;
					 }
					}
					if(noOfMatches==productModelTokens.length) {
						matchingListingSet.add(listing);
					}
				}
			}
		return matchingListingSet;

	}
	
	
	public static boolean containsIgnoreCase(String src, String what) {
	    final int length = what.length();
	    if (length == 0)
	        return true;

	    final char firstLo = Character.toLowerCase(what.charAt(0));
	    final char firstUp = Character.toUpperCase(what.charAt(0));

	    for (int i = src.length() - length; i >= 0; i--) {
	        // Quick check before calling the more expensive regionMatches() method:
	        final char ch = src.charAt(i);
	        if (ch != firstLo && ch != firstUp)
	            continue;

	        if (src.regionMatches(true, i, what, 0, length))
	            return true;
	    }

	    return false;
	}

	/**
	 * special contains check for model string. Takes care of numbers in the model matching uniquely. 
	 * Eg: A90 will not match A900
	 * @param src
	 * @param what
	 * @return
	 */
	public static boolean containsModel (String src, String what) {
		if(what.matches("(\\D.*\\D)|(\\D*)")) {
			return containsIgnoreCase(src, what);
		} else if(what.matches("\\d+")) {
			if(src.matches("(?i).*\\D" + what + "\\D.*")) {
				return true;
			}
		} else if(what.matches(".*\\D\\d+")) {
			if(src.matches("(?i).*" + what + "\\D.*")) {
				return true;
			}
		} else if(what.matches("\\d+\\D.*")){
			if(src.matches("(?i).*\\D" + what + ".*")) {
				return true;
			}
		} 
		return false;
	}
}
