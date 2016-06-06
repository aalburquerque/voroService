package com.aalburquerque.voronoi.struc.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author usuario
 *
 */
/**
 * @author usuario
 *
 */
public class InputPoints implements Serializable {
	
	
	public InputPoints(){
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8433252700977991537L;
	List<Punto2D> points=new ArrayList<Punto2D>();

	/**
	 * @return
	 */
	public List<Punto2D> getPoints() {
		return points;
	}

	/**
	 * @param points
	 */
	public void setPoints(List<Punto2D> points) {
		this.points = points;
	}

}
