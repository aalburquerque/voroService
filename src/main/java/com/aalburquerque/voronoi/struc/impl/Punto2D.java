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

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.util.GC;

/**
 * El objeto Punto2D representa un punto en el plano con coordenadas enteras
 * 
 * @author Antonio Alburquerque Oliva
 * 
 * @version 1.00
 */

public class Punto2D implements Dibujable, Serializable {
	private static final long serialVersionUID = 1L;
	private long x, y;

	private Vertice3d asociado = null;

	/**
	 * para construir un punto en el plano por sus dos coordenadas enteras
	 * 
	 * @param x
	 *            abscisa
	 * @param y
	 *            ordenada
	 */

	public Punto2D(long x, long y) {

		this.x = x;
		this.y = y;

	}

	/**
	 * para construir de un punto en el plano por sus dos coordenadas y el
	 * vertice en el espacio asociado. La proyeccion ortogonal de este vertice
	 * en el plano es justo el punto del plano que se esta definiendo
	 * 
	 * @param x
	 *            abscisa
	 * @param y
	 *            ordenada
	 * @param v
	 *            el vertice asociado
	 */

	public Punto2D(long x, long y, Vertice3d v) {

		this(x, y);

		asociado = v;

	}

	/**
	 * devuelve un vertice del espacio que esta asociado a este punto. La
	 * asociacion es que punto en el espacio devuelto es la proyeccion ortogonal
	 * de este vertice en el plano
	 * 
	 */

	public Vertice3d getAsociado() {

		return asociado;
	}

	/**
	 * Devuelve el orden lexicografico de este punto comparado con el punto
	 * pasado como parametro <br>
	 * <br>
	 * 1 si this < p <br>
	 * 0 si this = p <br>
	 * -1 si this > p
	 */

	public int ordenXY(Punto2D p) {

		long x1 = this.x(), y1 = this.y(), x2 = p.x(), y2 = p.y();

		return ((x1 == x2) && (y1 == y2)) ? 0 : (x1 < x2) || ((x1 == x2) && (y1 < y2)) ? 1 : -1;

	}

	/**
	 * Devuelve el orden (angulo,distancia) de este punto comparado con el punto
	 * pasado como parametro <br>
	 * <br>
	 * 1 si this < p <br>
	 * 0 si this = p <br>
	 * -1 si this > p
	 */

	public int ordenAD(Punto2D p, Punto2D Punto2D_de_referencia) {

		long x0 = Punto2D_de_referencia.x(), y0 = Punto2D_de_referencia.y();

		long x1 = this.x(), y1 = this.y();

		long x2 = p.x(), y2 = p.y();

		double d1 = new Punto2D(x0, y0).distancia(x1, y1);
		double d2 = new Punto2D(x0, y0).distancia(x2, y2);

		double a = GC.area2(x0, y0, x1, y1, x2, y2);

		return (a == 0) && (d1 == d2) ? 0 : (a > 0) || (a == 0 && d1 > d2) ? 1 : -1;

	}

	private double distancia(long x, long y) {

		double k = (x - this.x());
		k *= k;
		double j = (y - this.y());
		j *= j;
		return Math.sqrt(k + j);
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

	/**
	 * Metodo para dibujar este objeto
	 * 
	 * @param g
	 *            El objeto de la clase Graphics donde dibujar
	 * @param coord
	 *            El objeto por el que se transforma las coordenadas de este
	 *            objeto a las coordenadas del espacio de representacion
	 */

	public void dibujar(Graphics g, ICoord coord) {

		g.setColor(new Color(179, 92, 74));

		g.drawRect(coord.x(x()), coord.y(y()), 1, 1);

	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a este objeto
	 */

	public String toString() {

		return " [ " + new Long(x).toString() + " " + new Long(y).toString() + " ] ";
	}

	/**
	 * Este metodo es para hacer una copia de este objeto
	 */

	public Punto2D clonar() {

		return new Punto2D(x, y);

	}

}