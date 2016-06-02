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
import java.util.ListIterator;

/**
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class ListaDE implements Serializable {
	private static final long serialVersionUID = 1L;
	/* ------------------------------------------------------------------- */
	/* A T R I B U T O S */
	/* ------------------------------------------------------------------- */

	private Nodo primero, ultimo;

	private Nodo actualNodo;

	private String nombre;

	private int total;

	private int numIteraciones = 0;

	/* ------------------------------------------------------------------- */
	/* C O N S T R U C T O R E S */
	/* ------------------------------------------------------------------- */

	public ListaDE() {

		primero = ultimo = null;
		total = 0;

	}

	/* ------------------------------------------------------------------- */
	/* M E T O D O S */
	/* ------------------------------------------------------------------- */

	public void vaciar() {
		primero = ultimo = null;
		total = 0;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public boolean vacia() {
		return (primero == null);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public int get_total() {
		return total;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private Nodo get_primero() {
		return primero;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private Nodo get_ultimo() {
		return ultimo;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			return -1;
		}

		for (Nodo e = primero; e != null; e = e.siguiente()) {
			if (o.equals(e.info()))
				return index;
			index++;
		}

		return -1;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void insertarInicio(Object o) {

		if (vacia())

			primero = ultimo = new Nodo(o, null, null);

		else {

			Nodo nuevo = new Nodo(o, primero, null);

			primero.ant = nuevo;

			primero = nuevo;

		}

		total++;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void insertarFinal(Object o) {

		if (vacia())

			primero = ultimo = new Nodo(o, null, null);

		else {

			Nodo nuevo = new Nodo(o, null, ultimo);

			ultimo.sig = nuevo;

			ultimo = nuevo;

		}

		total++;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void suprimirNodo(Nodo nodoBorrar) {

		if (total == 1) {
			primero = ultimo = null;
			total--;
			return;
		}

		if (nodoBorrar == primero) {
			primero = primero.sig;
			primero.ant = null;
			total--;
			return;

		}

		if (nodoBorrar == ultimo) {
			ultimo = ultimo.ant;
			ultimo.sig = null;
			total--;
			return;

		}

		nodoBorrar.ant.sig = nodoBorrar.sig;
		nodoBorrar.sig.ant = nodoBorrar.ant;
		total--;

	}

	public Object suprimirNodo(int cual) {

		if (vacia())
			throw new ListaDEException(nombre);

		if (cual > total)
			throw new FueraDeLimitesException(nombre);

		Nodo nodoBorrar = primero;

		for (int i = 1; i < cual; i++, nodoBorrar = nodoBorrar.sig)
			;

		Object resultado = nodoBorrar.info();

		if (total == 1) {
			primero = ultimo = null;
			total--;
			return resultado;
		}

		if (nodoBorrar == primero) {
			primero = primero.sig;
			primero.ant = null;
			total--;
			return resultado;

		}

		if (nodoBorrar == ultimo) {
			ultimo = ultimo.ant;
			ultimo.sig = null;
			total--;
			return resultado;

		}

		nodoBorrar.ant.sig = nodoBorrar.sig;
		nodoBorrar.sig.ant = nodoBorrar.ant;
		total--;
		return resultado;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Object leerItem(int cual) {

		// el primero es cual = 1; el ultimo es cual = total

		if (vacia())
			throw new ListaDEException(nombre);

		if (cual > total)
			throw new FueraDeLimitesException(nombre);

		Nodo nodoLeer;

		if (cual < total / 2) {

			nodoLeer = primero;

			for (int i = 1; i < cual; i++, nodoLeer = nodoLeer.siguiente())
				;

		} else {

			nodoLeer = ultimo;

			for (int i = 1; i <= total - cual; i++, nodoLeer = nodoLeer.anterior())
				;

		}

		return (nodoLeer.info());

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void prepararIteraciones() {

		actualNodo = primero;

		numIteraciones = 0;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public void prepararIteraciones(Nodo nodo) {

		actualNodo = nodo;

		numIteraciones = 0;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public boolean tieneSiguienteItem() {

		return (actualNodo != null) && (numIteraciones <= total);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Object itera_siguienteItem() {

		Object o = actualNodo.info();

		actualNodo = actualNodo.siguiente();

		numIteraciones++;

		return o;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Nodo itera_nodo_siguienteItem() {

		Nodo o = actualNodo;

		actualNodo = nodo_siguiente();

		numIteraciones++;

		return o;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Nodo itera_nodo_anteriorItem() {

		Nodo o = actualNodo;

		actualNodo = nodo_anterior();

		numIteraciones++;

		return o;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Nodo nodo_actual() {
		return actualNodo;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Nodo nodo_siguiente() {
		return actualNodo.siguiente() == null ? primero : actualNodo.siguiente();
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Nodo nodo_anterior() {
		return actualNodo.anterior() == null ? ultimo : actualNodo.anterior();
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public Object[] toArray() {

		Object[] resultado = new Object[total];
		int i = 0;
		for (Nodo e = primero; e != null; e = e.siguiente())
			resultado[i++] = e.info();
		return resultado;
	}

	public String toString() {

		StringBuffer s = new StringBuffer(nombre + ": ");

		for (Nodo a = primero; a != null; a = a.siguiente())

			s.append("< " + a.info().toString() + "> ");

		return s.toString();

	}

	/* ------------------------------------------------------------------- */
	/* E X C E P C I O N E S */
	/* ------------------------------------------------------------------- */

	private class ListaDEException extends RuntimeException implements Serializable {

		public ListaDEException(String name) {
			super("La " + name + "esta vacia");
		}
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class FueraDeLimitesException extends RuntimeException implements Serializable {

		public FueraDeLimitesException(String name) {
			super(name + ": Fuera de limites");
		}
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R */
	/* ------------------------------------------------------------------- */

	public ListIterator listIterator() {
		return new ItrListaDE();
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	public ListIterator listIterator(int index) {
		return new ItrListaDE(index);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class ItrListaDE implements ListIterator, Serializable {

		private Nodo actual = primero;
		private Nodo anterior = ultimo;
		private int total_iteraciones = 0;
		private int indice_actual = 0;
		private int indice_anterior = 0;

		private int totalSalvado = total;

		public void add(Object p) {
			return;
		}

		public void set(Object p) {
			return;
		}

		ItrListaDE() {
			this(0);
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		// SEMANTICA DE ItrListaDE:

		// situa el cursor en index

		// a continuacion una operacion de next devuelve el siguiente a este
		// index

		// o a continuacion una operacion de previous devuelve el anterior a
		// este index

		ItrListaDE(int index) {

			if (index < 0 || index > total)
				throw new IndexOutOfBoundsException("Index: " + index + ", Total: " + total);

			if (index < total / 2) {

				actual = primero;
				for (int i = 0; i < index; i++, actual = actual.siguiente())
					;

			} else {

				actual = ultimo;
				for (int i = total - 1; i > index; i--, actual = actual.anterior())
					;

			}

			indice_actual = index;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {
			return total_iteraciones < totalSalvado;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Object next() {

			Object temp = actual.info();

			indice_anterior = indice_actual;

			anterior = actual;

			actual = actual.siguiente() == null ? primero : actual.siguiente();

			++indice_actual;
			indice_actual %= total;

			total_iteraciones++;

			return temp;
		}

		// ------------------------------------------------------
		// remove se debe llamar siempre despues de un next !!!!!
		// ------------------------------------------------------

		// borra el recien consultado
		// el proximo next devuelve el q seguia al recien consultado

		public void remove() {

			suprimirNodo(anterior);

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasPrevious() {

			return total_iteraciones != totalSalvado;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Object previous() {

			Object temp = actual.info();

			indice_anterior = indice_actual;

			anterior = actual;

			actual = actual.anterior() == null ? ultimo : actual.anterior();

			--indice_actual;
			indice_actual %= total;

			total_iteraciones++;

			return temp;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public int nextIndex() {
			return indice_actual;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public int previousIndex() {
			return indice_anterior;
		}

	} // fin de clase ItrListaDE

	/* ------------------------------------------------------------------- */
	/* C L A S E S P R I V A D A S */
	/* ------------------------------------------------------------------- */

	private class Nodo implements Serializable {

		public Object infoNodo;

		public Nodo sig;

		public Nodo ant;

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Nodo(Object o) {

			infoNodo = o;

			sig = null;

			ant = null;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Nodo(Object o, Nodo siguienteNodo, Nodo anteriorNodo) {

			infoNodo = o;

			sig = siguienteNodo;

			ant = anteriorNodo;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Object info() {
			return infoNodo;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Nodo siguiente() {
			return sig;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public Nodo anterior() {
			return ant;
		}

	} // fin clase Nodo

} // fin clase ListaDE
