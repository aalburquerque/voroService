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

package com.aalburquerque.voronoi.algorithms;

import java.io.Serializable;
import com.aalburquerque.voronoi.struc.ListAdaptor;
import com.aalburquerque.voronoi.struc.impl.DCEL;
import com.aalburquerque.voronoi.struc.impl.ListaDE;
import com.aalburquerque.voronoi.struc.impl.NodoArista;
import com.aalburquerque.voronoi.struc.impl.Poliedro;
import com.aalburquerque.voronoi.struc.impl.Punto3d;
import com.aalburquerque.voronoi.struc.impl.Triangulo3d;
import com.aalburquerque.voronoi.struc.impl.Vertice3d;
import com.aalburquerque.voronoi.util.GC;
import com.aalburquerque.voronoi.util.Input;

/**
 * <p>
 * Esta clase representa el proceso de calculo del cierre convexo de puntos en
 * el espacio mediante un algoritmo que tiene esquema incremental. 
 * Tiene complejidad en el tiempo de ejecuci�n del orden de O(n^2)
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 * @see Poliedro
 * @see ListaDE
 */

public class Incremental implements Serializable {

	private DCEL dcel;

	private Punto3d[] puntos;

	/**
	 * 
	 * Construir un nuevo proceso que incrementalmente resuelve el problema de
	 * encontrar el cierre convexo de un conjunto de puntos en el espacio.
	 * 
	 * @param dcel
	 *            Estructura que esta vacia previamente al proceso y que va a
	 *            contener el resultado de este proceso.
	 * @param puntos
	 *            Array con los puntos de los que se quiere hallar el cierre
	 *            convexo.
	 * 
	 * 
	 */

	public Incremental(DCEL dcel, Punto3d[] puntos) {

		this.dcel = dcel;

		this.puntos = puntos;

		// busca cuatro puntos no coplanares y los coloca al principio del array

		// organiza puede ser como mucho O(n^2)

		organiza();

		// forma un tetraedro con los cuatro primeros puntos del array

		tetraedro();

		for (int i = 4; i < puntos.length; i++)
			unoMas(puntos[i]);
	}

	private void swap(int i1, int i2) {

		Punto3d temp = puntos[i1];
		puntos[i1] = puntos[i2];
		puntos[i2] = temp;
	}

	// organiza() sirve para asegurar que antes de que se llame a tetraedro()
	// haya cuatro puntos no coplanares
	// al principio del array puntos[]

	private void organiza() {

		// array de indices del array de puntos

		int[] indx = Input.cuatroGeneral(puntos);

		if (indx == null) {

			throw new RuntimeException("Error de prueba: no hay cuatro en posicion general en todo el array");
		}

		if (GC.volumen6(puntos[indx[0]], puntos[indx[1]], puntos[indx[2]], puntos[indx[3]]) == 0) {

			throw new RuntimeException("Error de prueba: organiza() ");
		}

		for (int i = 0; i < 4; i++)
			swap(indx[i], i);

	}

	private void tetraedro() {

		double vol;

		int i3 = 3;
		int i2 = 2;
		int i1 = 1;
		int i0 = 0;

		vol = GC.volumen6(puntos[i0], puntos[i1], puntos[i2], puntos[i3]);

		if (vol == 0)
			throw new RuntimeException("Hay cuatro puntos coplanares: [ " + puntos[i0].toString() + " "
					+ puntos[i1].toString() + " " + puntos[i2].toString() + " " + puntos[i3].toString() + " ]");

		// se supone q los puntos no hay 3 colineares
		// se supone q los puntos no hay 4 coplanares

		// 1. ORIENTAR EL POLIGONO

		if (vol < 0) {

			// 0,1,2, su normal apunta a 3 y no debiera. intercambiamos 1 y 2

			swap(i1, i2);

		}

		// triangulo incial

		Vertice3d a = new Vertice3d(puntos[i0]);
		Vertice3d b = new Vertice3d(puntos[i1]);
		Vertice3d c = new Vertice3d(puntos[i2]);

		NodoArista A = new NodoArista(a, b);
		NodoArista B = new NodoArista(b, c);
		NodoArista C = new NodoArista(c, a);

		// 2. CONOCEMOS YA CUAL f0 de LAS ARISTAR A B C QUE ACABAMOS DE DEFINIR

		Triangulo3d F0 = new Triangulo3d(a, b, c);

		Vertice3d d = new Vertice3d(puntos[i3]);

		// 3. QUE MAS SABEMOS

		Triangulo3d F1 = new Triangulo3d(a, c, d);
		Triangulo3d F2 = new Triangulo3d(b, a, d);
		Triangulo3d F3 = new Triangulo3d(c, b, d);

		// 4. NOS FALTA SOLO e0+ Y e1+ ASI COMO f0 y f1
		// y definir E F y D

		NodoArista D = new NodoArista(a, d);
		NodoArista E = new NodoArista(c, d);
		NodoArista F = new NodoArista(b, d);

		// ya estan creadas todas las aristas

		// asignar una arista a cada vertice para poder recorrer las aristas
		// incidentes

		a.asignaArista(C);
		b.asignaArista(A);
		c.asignaArista(B);
		d.asignaArista(D);

		// asignar una arista a cada cara para poder recorrer las aristas de la
		// cara

		F0.asignaArista(A);
		F1.asignaArista(D);
		F2.asignaArista(F);
		F3.asignaArista(F);

		// asigna a cada arista las aristas siguiente en orden CCW en el vertice
		// origen y destino resp.

		A.asignaCCW(C, F);
		B.asignaCCW(A, E);
		C.asignaCCW(B, D);
		D.asignaCCW(A, E);
		E.asignaCCW(C, F);
		F.asignaCCW(B, D);

		// asigna a cada arista las caras q dejan a izq y der resp. iendo desde
		// el vertice origen al de destino

		A.asignaCaras(F0, F2);
		B.asignaCaras(F0, F3);
		C.asignaCaras(F0, F1);
		D.asignaCaras(F2, F1);
		E.asignaCaras(F1, F3);
		F.asignaCaras(F3, F2);

		// insertar en las listas doblemente enlazadas de vertices

		dcel.insertar(a);
		dcel.insertar(b);
		dcel.insertar(c);
		dcel.insertar(d);

		// insertar en las listas doblemente enlazadas de aristas

		dcel.insertar(A);
		dcel.insertar(B);
		dcel.insertar(C);
		dcel.insertar(D);
		dcel.insertar(E);
		dcel.insertar(F);

		// insertar en las listas doblemente enlazadas de caras

		dcel.insertar(F0);
		dcel.insertar(F1);
		dcel.insertar(F2);
		dcel.insertar(F3);

	}

	/* ------------------------------------------------------------------- */
	/*
	 * M E T O D O unoMas /*
	 * -------------------------------------------------------------------
	 */

	// A este poliedro convexo le a�ade un punto mas: p

	private void unoMas(Punto3d p) {

		if (dcel.testVisibilidadCaras(p))

			return; // volver si no hay ninguna cara visible desde p

		// marcar aristas candidatas para borrar y aquellas q son base del cono
		// cada una apropiadamente a su condicion
		// ademas devuelve una del borde

		NodoArista una_del_borde = dcel.testVisibilidadAristas();

		// volver a recorrer las aristas para construir el nuevo poliedro
		// se marcha por la base del cono. El sentido de la marcha es aquel por
		// el cual
		// segun la regla de la mano derecha el pulgar apunta al nuevo vertice p

		ListAdaptor iterBaseCono = dcel.iteradorBaseCono(una_del_borde);

		Vertice3d v = new Vertice3d(p);

		NodoArista e; // recorre la base del cono

		while (iterBaseCono.hasNext()) {

			e = iterBaseCono.next();

			unaCaraMas(e, v);

		}

		dcel.insertar(v);

		// esto es propio del incremental

		dcel.limpiar();

	}

	/* ------------------------------------------------------------------- */
	// M E T O D O unaCaraMas
	/* ------------------------------------------------------------------- */

	// variables de uso en unaCaraMas
	// ------------------------------

	// arista comun en el paso i de las nuevas caras del paso i y el paso (i-1)

	private NodoArista aristaComun;

	// primera arista que une un punto de la base del cono con el nuevo punto

	private NodoArista primeraAristaNueva;

	private void unaCaraMas(NodoArista e, // arista actual en la marcha por la
											// base

			Vertice3d p) {

		// 1. CONSTRUIR LA CARA

		// antes de ejecutar esto se supone q se ha hecho un test de visibilidad
		// de las caras por el nuevo vertice

		Vertice3d[] caraInvisible = e.caraIzq().getEstado() == Triangulo3d.VISIBLE ?

				e.caraDer().toArray() :

				e.caraIzq().toArray();

		Vertice3d[] ordenNuevaCara = new Vertice3d[3];

		// fijar las cosas un poco

		int i;
		for (i = 0; !caraInvisible[i].equals(e.des()); i++)
			;

		if (caraInvisible[(i + 1) % 3].equals(e.ori())) {

			ordenNuevaCara[0] = e.ori();
			ordenNuevaCara[1] = e.des();

		} else {
			ordenNuevaCara[0] = e.des();
			ordenNuevaCara[1] = e.ori();
		}

		// ordenNuevaCara[0] como origen y
		// ordenNuevaCara[0] como destino seria
		// una arista q indicase el sentido de la marcha
		// ya que e es una arista cuya orientacion
		// no tiene porque coincidir con el sentido de la marcha

		// el tercer vertice de la cara es el nuevo punto

		ordenNuevaCara[2] = p;

		// construir la nueva cara

		// definir la orientacion CCW de la nueva cara

		// La orientacion de la cara nueva debe
		// ser la misma q la de la cara invisible
		// adyacente a e

		Triangulo3d nuevaCara = new Triangulo3d(ordenNuevaCara[0], ordenNuevaCara[1], ordenNuevaCara[2]);
		nuevaCara.asignaArista(e);

		// 2. CONSTRUIR LAS ARISTAS

		NodoArista[] nuevaArista = new NodoArista[2];

		if (ordenNuevaCara[0].getAristaDup() == null) {

			// la nueva arista, incidente en ordenNuevaCara[0] y p, no esta
			// duplicada

			// el nuevo punto es siempre destino

			nuevaArista[0] = new NodoArista(ordenNuevaCara[0], p);
			ordenNuevaCara[0].setAristaDup(nuevaArista[0]);

			p.asignaArista(nuevaArista[0]);

		} else
			nuevaArista[0] = null; // no se inserta esta arista

		if (ordenNuevaCara[1].getAristaDup() == null) {

			// la nueva arista, incidente en ordenNuevaCara[1]) y p, no esta
			// duplicada

			// el nuevo punto es siempre destino

			nuevaArista[1] = new NodoArista(ordenNuevaCara[1], p);
			ordenNuevaCara[1].setAristaDup(nuevaArista[1]);

			p.asignaArista(nuevaArista[1]);
		} else
			nuevaArista[1] = null; // no se inserta esta arista

		// completar informacion de las aristas
		// ------------------------------------

		if (nuevaArista[0] == null && nuevaArista[1] == null) {

			// ultima cara de toda la vuelta
			// =============================

			// para las futuras iteraciones de aristas desde el vertice p

			aristaComun.asignaCCW(null, primeraAristaNueva);

			// para las iteraciones desde el vertice ordenNuevaCara[1]

			primeraAristaNueva.asignaCCW(e, null);

			// asigna las caras

			aristaComun.asignaCaras(null, nuevaCara);
			primeraAristaNueva.asignaCaras(nuevaCara, null);

			// la siguiente desde ordenNuevaCara[0] a e es la aristaComun

			if (e.ori() == ordenNuevaCara[0])
				e.asignaCCW(aristaComun, null);
			else
				e.asignaCCW(null, aristaComun);

		} else if (nuevaArista[0] != null && nuevaArista[1] != null) {

			// primera cara de toda la vuelta
			// ==============================

			// para las futuras iteraciones de aristas desde el vertice p

			nuevaArista[0].asignaCCW(null, nuevaArista[1]);

			// para las futuras iteraciones de aristas desde el vertice
			// ordenNuevaCara[1]

			nuevaArista[1].asignaCCW(e, null);

			// asigna que cara tienen a la izq o der
			// a las aristas relacionadas con la nuevaCara

			nuevaArista[0].asignaCaras(null, nuevaCara);
			nuevaArista[1].asignaCaras(nuevaCara, null);

			// nuevaArista[1] sera en el siguiente paso nuevaArista[0] pero
			// quizas en el siguiente paso nuevaArista[0] sea null
			// porque no queramos duplicar la arista
			// y por tanto debemos guardar cual sera la arista comun
			// a las caras de ambos pasos

			aristaComun = nuevaArista[1];

			// en el ultimo paso es necesaria conocerla

			primeraAristaNueva = nuevaArista[0];

			// la siguiente a e desde ordenNuevaCara[0] es nuevaArista[0]

			// como e puede no estar orientada
			// con la misma orientecion q el sentido de
			// la marcha es necesario comprobar esto

			if (e.ori() == ordenNuevaCara[0])
				e.asignaCCW(nuevaArista[0], null);
			else
				e.asignaCCW(null, nuevaArista[0]);

		} else {

			// caras intermedias
			// =================

			// para las futuras iteraciones de aristas desde el vertice p

			aristaComun.asignaCCW(null, nuevaArista[1]);

			// la siguiente a nuevaArista[1] desde ordenNuevaCara[1] es e

			nuevaArista[1].asignaCCW(e, null);

			// asigna que nuevaCara es la q dejan a izquierda y derecha las
			// correspondientes aristas

			aristaComun.asignaCaras(null, nuevaCara);
			nuevaArista[1].asignaCaras(nuevaCara, null);

			// la siguiente a e desde ordenNuevaCara[0] es la aristaComun

			// como e puede no estas orientada igual q la marcha hay q hacer
			// esto

			if (e.ori() == ordenNuevaCara[0])
				e.asignaCCW(aristaComun, null);
			else
				e.asignaCCW(null, aristaComun);

			// para la siguiente iteracion

			aristaComun = nuevaArista[1];

		}

		// las del borden dejan a un lado una cara q va a ser borrada
		// hay que actualizar q la cara q ahora deja a un lado es la nueva cara

		if (e.ori() == ordenNuevaCara[0])

			// e tiene el mismo sentido q nuestra marcha por la base
			e.asignaCaras(nuevaCara, null);
		else
			// e tiene el sentido contrario q nuestra marcha por la base
			e.asignaCaras(null, nuevaCara);

		// insertar los nuevos elementos

		for (int k = 0; k < 2; k++)
			if (nuevaArista[k] != null)
				dcel.insertar(nuevaArista[k]);

		dcel.insertar(nuevaCara);

	}

	/* ------------------------------------------------------------------- */
	/* M E T O D O */
	/* ------------------------------------------------------------------- */

}