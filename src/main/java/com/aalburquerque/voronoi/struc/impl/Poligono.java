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
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ListIterator;

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.util.GC;

/**
 * La clase Poligono almacena la informacion propia de un poligono. Esta clase
 * hereda de la clase Pila, que hereda de la clase ListaDE. Es en definitiva una
 * lista de puntos bidimensionales cuyo orden definen los lados que constituyen
 * al poligono.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Poligono extends Pila implements Dibujable, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Construir un nuevo poligono.
	 */
	public Poligono() {
		super();
	}

	public void add(Punto2D p) {
		apilar(p);
	}

	public Punto2D max_X() {

		if (get_total() < 3)
			throw new RuntimeException("Error: se trato un poligon de menos de 3 puntos");

		Punto2D actual, xmax;

		ListIterator a = listIterator();

		xmax = (Punto2D) a.next();

		while (a.hasNext()) {

			actual = (Punto2D) a.next();
			if (actual.x() > xmax.x())
				xmax = actual;

		}

		return xmax;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Punto2D min_X() {

		if (get_total() < 3)
			throw new RuntimeException("Error: se trato un poligon de menos de 3 puntos");

		Punto2D actual, xmin;

		ListIterator a = listIterator();

		xmin = (Punto2D) a.next();

		while (a.hasNext()) {

			actual = (Punto2D) a.next();
			if (actual.x() < xmin.x())
				xmin = actual;

		}

		return xmin;

	}

	public Segmento soporte(Poligono poli) {

		// se supone q a los poligonos les separa una recta vertical

		// this es el poligono con menor abscisa

		// 1. buscamos x-max de this y x-min de P

		Punto2D xmax = this.max_X();

		Punto2D xmin = poli.min_X();

		int ind_xmax = this.indexOf(xmax);

		int ind_xmin = poli.indexOf(xmin);

		// 2. recorrerer las fronteras
		Punto2D asig, bsig; // siguiente en la marcha
		Punto2D a, b;
		Punto2D aAnt, bAnt; // anteriores

		ListIterator A = this.listIterator(ind_xmax);
		a = (Punto2D) A.previous(); // el xmax
		asig = (Punto2D) A.previous(); // el siguiente en CCW a xmax

		ListIterator B = poli.listIterator(ind_xmin);
		b = (Punto2D) B.next(); // el xmin
		bsig = (Punto2D) B.next(); // el siguiente en CW a xmin

		double avalor = GC.area2(b, a, asig);
		double bvalor = GC.area2(bsig, b, a);

		aAnt = a;
		bAnt = b;

		while (avalor <= 0 || bvalor <= 0) {

			if (avalor <= 0) {

				// System.out.println();

				aAnt = a;
				a = asig; // siguiente en la marcha positiva de A
				asig = (Punto2D) A.previous();

			}

			else {

				bAnt = b;
				b = bsig; // siguiente en la marcha negativa de B
				bsig = (Punto2D) B.next();

			}

			bvalor = GC.area2(bsig, b, a);
			avalor = GC.area2(b, a, asig);

		}

		if (GC.area2(a, aAnt, b) == 0)
			a = aAnt;
		if (GC.area2(b, bAnt, a) == 0)
			b = bAnt;

		return new Segmento(a, b);

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

		if (vacia())
			return;

		ListIterator iteracion = listIterator();
		int i = 1;
		Punto2D p;

		Polygon poly = new Polygon();

		g.setColor(Color.pink);

		while (iteracion.hasPrevious()) {

			p = (Punto2D) iteracion.previous();

			poly.addPoint(coord.x(p.x()), coord.y(p.y()));

			// if (con_numeros) g.drawString( new Integer(i).toString(),
			// Coord.x( p.x() -15 ) ,Coord.y( p.y() +15 ) ); i++;

		}
		g.drawPolygon(poly);

	}

}
