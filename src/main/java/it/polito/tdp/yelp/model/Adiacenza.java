package it.polito.tdp.yelp.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Adiacenza {

	private Review r1;
	private Review r2;
	private LocalDate d1;
	private LocalDate d2;
	private double peso;
	public Adiacenza(Review r1, Review r2, LocalDate d1, LocalDate d2) {
		super();
		this.r1 = r1;
		this.r2 = r2;
		this.d1 = d1;
		this.d2 = d2;
		this.peso = ChronoUnit.DAYS.between(d1, d2);
		
	}
	public double getPeso() {
		return this.peso;
	}
	public void setPeso(double p) {
		this.peso = p;
	}
	public Review getR1() {
		return r1;
	}
	public void setR1(Review r1) {
		this.r1 = r1;
	}
	public Review getR2() {
		return r2;
	}
	public void setR2(Review r2) {
		this.r2 = r2;
	}
	public LocalDate getD1() {
		return d1;
	}
	public void setD1(LocalDate d1) {
		this.d1 = d1;
	}
	public LocalDate getD2() {
		return d2;
	}
	public void setD2(LocalDate d2) {
		this.d2 = d2;
	}
	
}
