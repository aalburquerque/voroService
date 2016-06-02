/*
  This file is part of com.aalburquerque.voronoi API.

    com.aalburquerque.voronoi API is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    com.aalburquerque.voronoi API is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with com.aalburquerque.voronoi API.  If not, see <http://www.gnu.org/licenses/>
*/

package com.aalburquerque.voronoi.struc.impl;

/**
 * El objeto Punto2D representa un punto en el plano con coordenadas enteras.
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Coord2d implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private long x, y;

	/**
	 * para construir un punto en el plano por sus dos coordenadas enteras
	 * 
	 * @param x
	 *            abscisa
	 * @param y
	 *            ordenada
	 */

	public Coord2d(long x, long y) {

		this.x = x;
		this.y = y;

	}

	/**
	 * Devuelve la coordenada x de este punto
	 */

	public long x() {
		return x;
	}

	/**
	 * Devuelve la coordenada y de este punto
	 */

	public long y() {
		return y;
	}

	public String toString() {

		return " [ " + new Long(x).toString() + " " + new Long(y).toString() + " ] ";
	}

}