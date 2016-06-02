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

import com.aalburquerque.voronoi.util.*;

/**
 * Esta clase representa una triangulo en el espacio que sirve para representar
 * caras de un poliedro. Si el poliedro tuviera caras de mas de tres lados
 * entonces un conjunto de objetos de esta clase puede representar dicha cara
 * triangulizandola.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Triangulo3d implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Constante para calificar una cara
	 */
	public static final long NADA = 0;

	/**
	 * Constante para calificar una cara
	 */

	public static final long VISIBLE = 1;

	private Vertice3d a, b, c;

	private NodoArista arista; // para apuntar una de las aristas de la cara

	// se distigue la normal normalizada o no para reducir operaciones en
	// punto flotante

	private long estado = NADA;

	private EstadoColor estadoColor; // rojo, purpura, azul. Para divide y
										// venceras

	private boolean esVisitado;

	/**
	 * Construir una nueva cara que tenga por vertices los pasados como
	 * parametro
	 *
	 * @param a
	 *            Uno de los vertice de la cara
	 * @param b
	 *            Uno de los vertice de la cara
	 * @param c
	 *            Uno de los vertice de la cara
	 */

	public Triangulo3d(Vertice3d a, Vertice3d b, Vertice3d c) {

		this.a = a;
		this.b = b;
		this.c = c;

		estadoColor = new EstadoColor();

		esVisitado = false;
	}

	/**
	 * Devuelve el valor de la coordenada z de la normal de esta cara
	 */

	public long nz() {

		Punto3d normal;

		Punto3d v = b.restar(a);
		Punto3d w = c.restar(a);

		normal = v.prodVect(w);

		return normal.z();
	}

	public long ny() {

		Punto3d normal;

		Punto3d v = b.restar(a);
		Punto3d w = c.restar(a);

		normal = v.prodVect(w);

		return normal.y();
	}

	public long nx() {

		Punto3d normal;

		Punto3d v = b.restar(a);
		Punto3d w = c.restar(a);

		normal = v.prodVect(w);

		return normal.x();
	}

	/**
	 * Devuelve cierto si esta cara es igual a la pasada como argumento
	 * 
	 * @param a
	 *            La cara con la que se quiere comparar esta cara
	 */

	boolean igual(Triangulo3d a) {

		return nz() == a.nz() && ny() == a.ny() && nx() == a.nx();

	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a la normal de
	 * esta cara
	 */

	public String normalString() {
		return " [ " + new Long(nx()).toString() + "," + new Long(ny()).toString() + "," + new Long(nz()).toString()
				+ " ] ";
	}

	/**
	 * Devuelve el producto escalar de la normal de esta cara y la pasada como
	 * argumento
	 * 
	 * @param t
	 *            Una de las caras cuya normal es parte del producto escalar
	 */

	public double prodEsc(Triangulo3d t) {

		return nx() * t.nx() + ny() * t.ny() + nz() * t.nz();
	}

	/**
	 * Devuelve el array de vertices de esta cara
	 */

	public Vertice3d[] toArray() {

		Vertice3d[] temp = new Vertice3d[3];
		temp[0] = a;
		temp[1] = b;
		temp[2] = c;
		return temp;
	}

	/**
	 * Devuelve una cadena con la informacion correspondiente
	 */

	public String estadoString() {
		return new Long(estado).toString();
	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a esta cara
	 */
	public String toString() {
		return "[" + a.toString() + " " + b.toString() + " " + c.toString() + "] estado: " + estadoString() + " nz: "
				+ nz();

	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a los vertices de
	 * la cara
	 */

	public String verticesString() {
		return "[" + a.toString() + " " + b.toString() + " " + c.toString() + "]";

	}

	/**
	 * Devuelve una arista de las que delimitan esta cara
	 */

	public NodoArista arista() {

		return arista;
	}

	/**
	 * Establece la pasada como argumento como una de las arista de las que
	 * delimitan esta cara
	 */
	public void asignaArista(NodoArista arista) {

		this.arista = arista;
	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a la arista
	 * asociada a esta cara. Es decir una de las aristas de la cara
	 */

	public String aristaString() {

		return arista.toString();
	}

	/**
	 * Para obtener el estado de la arista en un momento dado.
	 */

	public long getEstado() {
		return estado;
	}

	/**
	 * Para establecer cual es el estado de la arista en un momento dado.
	 * 
	 * @param est
	 *            El nuevo estado de la arista
	 */

	public void setEstado(long est) {
		estado = est;
	}

	/**
	 * Devuelve el circuncetro de esta cara en un array de long
	 */

	public long[] circuncentro() {

		long b[] = new long[2];

		Vertice3d[] a = toArray();

		double p_0, p_1;

		long a_0 = a[0].x();
		long a_1 = a[0].y();
		long b_0 = a[1].x();
		long b_1 = a[1].y();
		long c_0 = a[2].x();
		long c_1 = a[2].y();

		long D = 2 * (a_1 * c_0 + b_1 * a_0 - b_1 * c_0 - a_1 * b_0 - c_1 * a_0 + c_1 * b_0);

		p_0 =

				(b_1 * a_0 * a_0 - c_1 * a_0 * a_0 - b_1 * b_1 * a_1 + c_1 * c_1 * a_1 + b_0 * b_0 * c_1
						+ a_1 * a_1 * b_1 + c_0 * c_0 * a_1 - c_1 * c_1 * b_1 - c_0 * c_0 * b_1 - b_0 * b_0 * a_1
						+ b_1 * b_1 * c_1 - a_1 * a_1 * c_1) / D;

		p_1 = (a_0 * a_0 * c_0 + a_1 * a_1 * c_0 + b_0 * b_0 * a_0 - b_0 * b_0 * c_0 + b_1 * b_1 * a_0 - b_1 * b_1 * c_0
				- a_0 * a_0 * b_0 - a_1 * a_1 * b_0 - c_0 * c_0 * a_0 + c_0 * c_0 * b_0 - c_1 * c_1 * a_0
				+ c_1 * c_1 * b_0) / D;

		b[0] = (long) Math.round(p_0);
		b[1] = (long) Math.round(p_1);

		return b;

	}

	/**
	 * Funcion que devuelve el color de este elemento
	 */

	public int dameColor() {
		return estadoColor.dameColor();
	}

	/**
	 * Metodo para establecer el color de este elemento
	 */

	public void ponColor(int uncolor) {
		estadoColor.ponColor(uncolor);
	}

	/**
	 * Devuelve una cadena con el color de este elemento
	 */

	public String colorString() {
		return estadoColor.colorString();
	}

	public void setVisitado(boolean valor) {
		esVisitado = valor;
	}

	public boolean esVisitado() {
		return esVisitado;
	}

	public void reset() {

		setEstado(Triangulo3d.NADA);
		setVisitado(false);
		ponColor(EstadoColor.AZUL);
	}

}