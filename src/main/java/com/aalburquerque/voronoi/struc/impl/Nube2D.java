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
import java.util.ListIterator;

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.util.GC;

/**
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Nube2D extends Pila implements Dibujable, Serializable {
	private static final long serialVersionUID = 1L;

	public Nube2D() {
		super();
	}

	public Nube2D(Punto2D[] A) {
		super();
		for (int i = 0; i < A.length; i++)
			apilar(A[i]);
	}

	public Nube2D(Punto3d[] A) {
		super();
		for (int i = 0; i < A.length; i++)
			apilar(new Punto2D(A[i].x(), A[i].y()));
	}

	public Nube2D(InputPoints inputPoints) {
		if (inputPoints!=null)
		for (int i = 0; i < inputPoints.getPoints().size(); i++)
			apilar(new Punto2D(inputPoints.getPoints().get(i).x(),inputPoints.getPoints().get(i).y()));
	}

	public void unoMas(long x, long y) {
		apilar(new Punto2D(x, y));
	}

	/* ------------------------------------------------------------------- */
	/* C O N S T R U C T O R E S */
	/* ------------------------------------------------------------------- */

	/* ------------------------------------------------------------------- */
	/* M E T O D O S */
	/* ------------------------------------------------------------------- */

	private void quicksortXY(Object[] A, int izq, int der) {

		int i = izq, j = der;
		Punto2D central = (Punto2D) A[(i + j + 1) / 2];
		Object temp;

		do {
			while (((Punto2D) A[i]).ordenXY(central) > 0)
				i++;
			while (central.ordenXY((Punto2D) A[j]) > 0)
				j--;

			if (i <= j) {
				temp = A[i];
				A[i] = A[j];
				A[j] = temp;
				i++;
				j--;
			}

		} while (!(i > j));

		if (izq < j)
			quicksortXY(A, izq, j);
		if (i < der)
			quicksortXY(A, i, der);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private void quicksortAD(Object[] A, int izq, int der, Punto2D Punto2D_de_referencia) {

		int i = izq, j = der;

		Object central = A[(i + j + 1) / 2], temp;

		do {
			while (((Punto2D) A[i]).ordenAD((Punto2D) central, Punto2D_de_referencia) > 0)
				i++;
			while (((Punto2D) central).ordenAD((Punto2D) A[j], Punto2D_de_referencia) > 0)
				j--;

			if (i <= j) {
				temp = A[i];
				A[i] = A[j];
				A[j] = temp;
				i++;
				j--;
			}

		} while (!(i > j));

		if (izq < j)
			quicksortAD(A, izq, j, Punto2D_de_referencia);
		if (i < der)
			quicksortAD(A, i, der, Punto2D_de_referencia);

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void ordenarXY() {

		if (vacia())
			return; // si esta vacia la Nube2D

		// es mucho mas rapido ordenar un array que ordenar una lista
		// sobre todo cuando hay muchos elementos

		// 1. convertir a array la lista de Punto2Ds

		Object[] a = toArray();

		// 2. ordenar el array por quicksort

		quicksortXY(a, 0, a.length - 1);

		// esto se puede mejorar

		vaciar(); // vaciar la Nube2D

		for (int j = 0; j < a.length; j++)
			insertarFinal(a[j]);

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void ordenarAD() {

		/* hace el Punto2D de menor abscisa el primero y ordena angularmente */

		if (vacia())
			return;

		// convertir a array la lista de Punto2Ds

		Object[] a = toArray();

		// buscar el de menor abscisa y hacerlo el primero

		Object temp = a[0];

		int indice = 0, k = 1, menor = 0;

		while (k < a.length) {

			if (((Punto2D) a[k]).ordenXY((Punto2D) temp) > 0) {
				temp = a[k];
				menor = k;
			}
			k++;
		}

		Punto2D minXY = ((Punto2D) temp).clonar();

		if (menor > 0) {
			temp = a[0];
			a[0] = a[menor];
			a[menor] = temp;
		}

		quicksortAD(a, 1, a.length - 1, minXY);

		vaciar(); // vaciar la Nube2D

		for (int j = 0; j < a.length; j++)
			insertarFinal(a[j]);

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Poligono scan() {

		if (get_total() < 4) {
			return null;
		}

		this.ordenarAD();

		Pila SOL = new Pila();

		ListIterator iteracion = listIterator();

		SOL.apilar((Punto2D) iteracion.next()); // el primer Punto2D

		SOL.apilar((Punto2D) iteracion.next()); // el segundo Punto2D

		Punto2D vi; // el nuevo Punto2D actual

		while (iteracion.hasNext()) {

			vi = (Punto2D) iteracion.next();

			while (GC.area2((Punto2D) SOL.leerItem(2), (Punto2D) SOL.leerItem(1), vi) <= 0) {
				SOL.desapilar();
			}

			SOL.apilar(vi);

		}

		// poner todos los puntos de la pila en el poligono

		Poligono poligono = new Poligono();
		iteracion = SOL.listIterator();
		while (iteracion.hasNext()) {

			poligono.add((Punto2D) iteracion.next());
		}

		return poligono;

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
			return; // vacia la Nube2D

		ListIterator iteracion = listIterator();
		g.setColor(Color.red);
		Punto2D p;

		while (iteracion.hasNext()) {

			p = (Punto2D) iteracion.next();
			p.dibujar(g, coord);

		}

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Punto3d[] virgenMaria() {

		Object[] a = toArray();

		Punto3d[] resultado = new Punto3d[a.length];

		Punto2D temp;

		long[] coordenadas = new long[3];

		for (int i = 0; i < a.length; i++) {

			temp = (Punto2D) a[i];

			coordenadas[0] = temp.x();
			coordenadas[1] = temp.y();
			coordenadas[2] = coordenadas[0] * coordenadas[0] + coordenadas[1] * coordenadas[1];

			resultado[i] = new Punto3d(coordenadas[0], coordenadas[1], coordenadas[2]);

		}

		return resultado;

	}

}