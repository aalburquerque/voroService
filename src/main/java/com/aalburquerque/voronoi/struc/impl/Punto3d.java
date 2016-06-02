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

import java.io.Serializable;

/**
 * La clase Punto3d sirve para representar un punto de coordenadas enteras en el
 * espacio
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Punto3d implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Coordenadas enteras del punto
	 * 
	 */

	protected long x, y, z;

	/**
	 * Constructor de un punto
	 * 
	 * @param p
	 *            Construye un punto con este
	 */

	public Punto3d(Punto3d p) {

		this.x = p.x();
		this.y = p.y();
		this.z = p.z();

	}

	public Punto3d(long x, long y) {

		this.x = x;
		this.y = y;
		this.z = x * x + y * y;

	}

	/**
	 * Constructor de un punto
	 * 
	 * @param x
	 *            coord x
	 * @param y
	 *            coord y
	 * @param z
	 *            coord z
	 */

	public Punto3d(long x, long y, long z) {

		this.x = x;
		this.y = y;
		this.z = z;

	}

	/**
	 * para comparar dos puntos por la coordenada x
	 * 
	 * @param p
	 *            punto con el q se compara este objeto
	 * @return -1 si this es menor q p, 0 si igual y +1 si this es mayor q p
	 */

	public int ordenX(Punto3d p) {

		/*
		 * devuelve -1 si this < p
		 * 
		 * 0 si this = p
		 * 
		 * +1 si this > p
		 */

		return this.x() < p.x() ? -1 : this.x() > p.x() ? 1 : 0;

	}

	/**
	 * Retorno de una coordenada
	 * 
	 * @return coord x
	 */

	public long x() {
		return x;
	}

	/**
	 * Retorno de una coordenada
	 * 
	 * @return coord y
	 */

	public long y() {
		return y;
	}

	/**
	 * Retorno de una coordenada
	 * 
	 * @return coord z
	 */

	public long z() {
		return z;
	}

	/**
	 * Retorno de las coordenadas
	 * 
	 * @return Cadena con las coordenadas
	 */

	public String salidaString() {

		return new Long(x()).toString() + " " + new Long(y()).toString() + " " + new Long(z()).toString();

	}

	public String proyeccString() {

		return new Long(x()).toString() + " " + new Long(y()).toString();

	}

	public String coordString() {

		return "[" + salidaString() + "]";

	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a este punto
	 * tridimensional
	 */

	public String toString() {

		return coordString();

	}

	/**
	 * Producto vetorial de el vector con coordenadas las de este punto con otro
	 * vector
	 * 
	 * @param w
	 *            El segundo vector del producto
	 * @return Un punto que representa al vector producto vectorial
	 */
	public Punto3d prodVect(Punto3d w) {

		return new Punto3d(this.y() * w.z() - this.z() * w.y(), w.x() * this.z() - this.x() * w.z(),
				this.x() * w.y() - w.x() * this.y());
	}

	/**
	 * Producto escalar de el vector con coordenadas las de este punto con otro
	 * vector
	 * 
	 * @param w
	 *            El segundo vector del producto
	 * @return Un punto que representa el producto escalar
	 */

	public long prodEsc(Punto3d w) {

		return this.x() * w.x() + this.y() * w.y() + this.z() * w.z();
	}

	/**
	 * Resta vetorial de dos vectores
	 * 
	 * @param w
	 *            El segundo vector de la resta
	 * @return Un punto que representa al vector resta
	 */

	public Punto3d restar(Punto3d w) {

		return new Punto3d(w.x() - this.x(), w.y() - this.y(), w.z() - this.z());

	}

	public boolean igual(Punto3d v) {
		return v.x() == x && v.y() == y && v.z() == z;
	}

	/**
	 * Para saber el orden relativo de dos puntos en el espacio devuelve <BR>
	 * <BR>
	 * 1 si this < p <BR>
	 * 
	 * 0 si this = p <BR>
	 * 
	 * -1 si this > p
	 * 
	 * @param p
	 *            El punto que se quiere comparar
	 */

	public int ordenXYZ(Punto3d p) {

		return this.x() < p.x() ? 1 : this.x() > p.x() ? -1 : this.y() < p.y() ? 1
				: this.y() > p.y() ? -1 : this.z() < p.z() ? 1 : this.z() > p.z() ? -1 : 0;
	}

}