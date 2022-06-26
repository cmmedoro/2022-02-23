package it.polito.tdp.yelp.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<Review, DefaultWeightedEdge> grafo;
	private List<String> cities;
	private List<Business> locale;
	private List<Review> vertici;
	private Map<String, Review> idMap;
	private int maxDegree;
	//strutture dati per la ricorsione
	private List<Review> best;
	private double diffGiorni;
	
	public Model() {
		this.dao = new YelpDao();
		this.cities = new ArrayList<>();
		this.locale = new ArrayList<>();
	}
	
	public List<String> getAllCities(){
		this.cities = this.dao.getAllCities();
		return this.cities;
	}
	
	public List<Business> getBusinessesCity(String city){
		this.locale = this.dao.getBusinessesCity(city);
		return this.locale;
	}
	
	public void creaGrafo(String c, Business b) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		this.idMap = new HashMap<>();
		this.vertici = new ArrayList<>(this.dao.getVertici(c, b, idMap));
		Graphs.addAllVertices(this.grafo, this.vertici);
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(c, b, idMap)) {
			if(a.getD1().isBefore(a.getD2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getR1(), a.getR2(), a.getPeso());
			}
		}
		
	}
	
	public boolean isGraphCreated() {
		if(this.grafo == null) {
			return false;
		}
		return true;
	}
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Review> getMaxDegreeOf(){
		List<Review> maxDegree = new ArrayList<>();
		int max = 0;
		for(Review vertice : this.grafo.vertexSet()) {
			int uscenti = this.grafo.outDegreeOf(vertice);
			if(uscenti > max) {
				max = uscenti;
			}
		}
		this.maxDegree = max;
		for(Review vertice : this.grafo.vertexSet()) {
			int uscenti = this.grafo.outDegreeOf(vertice);
			if(uscenti == max) {
				maxDegree.add(vertice);
			}
		}
		return maxDegree;
	}
	
	public int maxDegree() {
		return this.maxDegree;
	}

	public List<Review> cercaMiglioramento(){
		List<Review> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		this.diffGiorni = 0;
		Review partenza = this.getPartenza();
		parziale.add(partenza);
		trovaMiglioramento(parziale, stars(partenza));
		return this.best;
	}
	
	private void trovaMiglioramento(List<Review> parziale, double stars) {
		
		if(parziale.size() > this.best.size()) {
			this.best = new ArrayList<>(parziale);
		}
		Review ultima = parziale.get(parziale.size()-1);
		for(Review r : this.grafo.vertexSet()) {
			if(this.isConsecutiva(ultima, r) && this.hasMinusStars(ultima, r) && !parziale.contains(r)) {
				parziale.add(r);
				double star = stars(r);
				trovaMiglioramento(parziale, star );
				parziale.remove(r);
			}
		}
	}

	public Review getPartenza() {
		LocalDate iniziale = null;
		Review partenza = null;
		for(Review r : this.grafo.vertexSet()) {
			if(iniziale == null) {
				iniziale = r.getDate();
			}else {
				if(r.getDate().isBefore(iniziale)) {
					iniziale = r.getDate();
				}
			}
		}
		for(Review r : this.grafo.vertexSet()) {
			if(r.getDate().isEqual(iniziale)) {
				partenza = r;
				break;
			}
		}
		return partenza;
	}
	
	private double stars(Review r) {
		return r.getStars();
	}
	
	private boolean isConsecutiva(Review r1, Review r2) {
		if(r1.getDate().isBefore(r2.getDate())) {
			return true;
		}
		return false;
	}
	private boolean hasMinusStars(Review r1, Review r2) {
		if(stars(r2) <= stars(r1)) {
			return true;
		}
		return false;
	}
	
	public double getDiffGiorni() {
		Review r1 = this.best.get(0);
		Review r2 = this.best.get(this.best.size()-1);
		this.diffGiorni = ChronoUnit.DAYS.between(r1.getDate(), r2.getDate());
		return this.diffGiorni;
	}
}
