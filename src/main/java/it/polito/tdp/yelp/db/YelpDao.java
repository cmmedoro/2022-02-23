package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {
	
	
	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllCities(){
		String sql = "SELECT DISTINCT city "
				+ "FROM business";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getBusinessesCity(String city){
		String sql = "SELECT * "
				+ "FROM business "
				+ "WHERE city = ?";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Business business = new Business(res.getString("business_id"), res.getString("full_address"),res.getString("active"),
						res.getString("categories"),res.getString("city"),res.getInt("review_count"),
						res.getString("business_name"),	res.getString("neighborhoods"),res.getDouble("latitude"),
						res.getDouble("longitude"),	res.getString("state"),res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getVertici(String city, Business locale, Map<String, Review> idMap){
		String sql = "SELECT r.* "
				+ "FROM business b, reviews r "
				+ "WHERE b.business_name = ? AND b.city = ? AND b.business_id = r.business_id";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, locale.getBusinessName());
			st.setString(2, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Review review = new Review(res.getString("review_id"), res.getString("business_id"),res.getString("user_id"),
						res.getDouble("stars"),res.getDate("review_date").toLocalDate(),res.getInt("votes_funny"),
						res.getInt("votes_useful"),res.getInt("votes_cool"),res.getString("review_text"));
				result.add(review);
				idMap.put(res.getString("review_id"), review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(String city, Business locale, Map<String, Review> idMap){
		String sql = "SELECT r1.review_id AS r1, r2.review_id AS r2, r1.review_date AS d1, r2.review_date AS d2 "
				+ "FROM business b, reviews r1, reviews r2 "
				+ "WHERE b.business_name = ? AND b.city = ? AND b.business_id = r1.business_id AND r2.business_id = r1.business_id AND r1.review_date < r2.review_date AND r1.review_id <> r2.review_id";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, locale.getBusinessName());
			st.setString(2, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Review r1 = idMap.get(res.getString("r1"));
				Review r2 = idMap.get(res.getString("r2"));
				if(r1 != null && r2 != null) {
					result.add(new Adiacenza(r1, r2, res.getDate("d1").toLocalDate(), res.getDate("d2").toLocalDate()));
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
