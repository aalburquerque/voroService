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
import java.util.ListIterator;

import com.aalburquerque.voronoi.exception.BucleEnvolviendoException;
import com.aalburquerque.voronoi.exception.MezclaDCELException;
import com.aalburquerque.voronoi.exception.PuntosNoOrdenadosException;
import com.aalburquerque.voronoi.struc.impl.DCEL;
import com.aalburquerque.voronoi.struc.impl.ListaDE;
import com.aalburquerque.voronoi.struc.impl.NodoArista;
import com.aalburquerque.voronoi.struc.impl.Poliedro;
import com.aalburquerque.voronoi.struc.impl.Poligono;
import com.aalburquerque.voronoi.struc.impl.Punto3d;
import com.aalburquerque.voronoi.struc.impl.Segmento;
import com.aalburquerque.voronoi.struc.impl.Triangulo3d;
import com.aalburquerque.voronoi.struc.impl.Vertice3d;
import com.aalburquerque.voronoi.util.EstadoColor;
import com.aalburquerque.voronoi.util.GC;

/**
 * Esta clase representa el proceso de calculo del cierre convexo de puntos en
 * el espacio mediante un algoritmo que tiene esquema de divide y venceras. Este
 * algoritmo fue propuesto por Preparata y Hong en un articulo tecnologico
 * aparecido en el a�o 1977. Se basa en dividir un problema en dos subproblemas
 * del mismo tama�o, y una vez que se tenga la solucion de los subproblemas
 * aplicar un proceso de mezcla. Tiene complejidad en el tiempo de ejecuci�n del
 * orden de O(n log n) Es importante observar que esta implementacion funciona
 * correctamente si los puntos estan en posicion general, es decir, que no haya
 * cuatro puntos coplanares. En otro caso faltan algunos cambios para asegurar
 * que se calcule correctamente el cierre convexo. Es decir, esta implementacion
 * no es completa y es posible que para el caso en el que existan puntos
 * coplanares no se logre calcular el cierre convexo.
 * 
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 * @see Poliedro
 * @see ListaDE
 */

public class DivideYvenceras implements Serializable {

	// donde se almacena el resultado

	private DCEL C;

	// el conjunto de puntos de los que se quiere obtener el cierre convexo

	private Punto3d[] puntos;

	// poliedros de proceso de mezcla.
	// Cualquier punto de A tiene abscisa menor que cualquier punto de B.

	private DCEL A, B;

	/**
	 * 
	 * Construir un nuevo proceso que por medio de "Divide y Venceras" resuelve
	 * el problema de encontrar el cierre convexo de un conjunto de puntos en el
	 * espacio.
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

	public DivideYvenceras(DCEL dcel, Punto3d[] puntos, int cota) {

		// se supone que los puntos estan ordenados por abscisa

		setCota(cota);

		for (int i = 1; i < puntos.length; i++)

			if (puntos[i - 1].ordenX(puntos[i]) == 1)

				throw new PuntosNoOrdenadosException();

		this.C = dcel; // la dcel que almacenara la mezcla

		this.puntos = puntos;

		int total = puntos.length;

		int corte = total / 2;

		this.A = construye(0, corte - 1);
		this.B = construye(corte, total - 1);

		// DCEL porincremental = new DCEL(puntos,true);
		// porincremental.aDisco("porincremental");

		mezclar();
	}

	/**
	 * 
	 *
	 *
	 * Construir un nuevo proceso que por medio de "Divide y Venceras" resuelve
	 * el problema de encontrar el cierre convexo de dos poliedros representados
	 * por sus DCEL mediante un proceso de mezcla
	 * 
	 * @param dcel
	 *            Estructura que esta vacia previamente al proceso y que va a
	 *            contener el resultado de este proceso.
	 * @param A
	 *            La DCEL cuyos puntos tienen menores abscisas
	 * @param B
	 *            La DCEL cuyos puntos tienen mayores abscisas
	 * 
	 * 
	 */

	public DivideYvenceras(DCEL dcel, DCEL A, DCEL B) {

		this.C = dcel; // la dcel que almacenara la mezcla
		this.A = A;
		this.B = B;
		mezclar();
	}

	// proceso de mezcla

	private void mezclar() {

		// para poner como no visitadas las caras preparando el recorrido en
		// profundidad
		// y que todo este azul antes de empezar

		A.resetMezcla();
		B.resetMezcla();

		// todo en A y B debe ser Azul

		// if (!A.checkColor() || !B.checkColor()) throw new
		// RuntimeException("FALLO COLOR");

		// mezclar A y B y el resultado a dcel

		ejecutarMezcla();

	}

	private DCEL construye(int izq, int der) {

		int total = der - izq + 1;

		Punto3d[] temp = new Punto3d[total];

		for (int i = 0; i < total; i++)
			temp[i] = puntos[izq + i];

		DCEL resultado = new DCEL(temp, getCota());

		return resultado;

	}

	private void ejecutarMezcla() {

		Object[] items;
		ListIterator iter;

		// vertices de la actual arista de A y B resp.

		Vertice3d A1, B1;

		// vertices candidatos

		Vertice3d A2, B2;

		// Caras candidatas

		Triangulo3d caraIzq;
		Triangulo3d caraDer;

		// aristas candidatas

		NodoArista aristaSigIz = null;
		NodoArista aristaSigDe = null;

		Vertice3d verticeSigIz = null;
		Vertice3d verticeSigDe = null;

		// cara inicial

		Triangulo3d inicial, caraAnterior;

		// empezar buscando el ganador en las dos

		boolean buscarIz = true;
		boolean buscarDe = true;

		// para el test

		boolean[] tst = new boolean[4];

		boolean ganaA, ganaB;

		// 1. encontrar arista puente

		Segmento puente = hallarPuente();

		// devuelve el punto en el espacio asociado a este punto en el plano

		A1 = puente.ori().asociado();
		B1 = puente.des().asociado();

		caraAnterior = inicial = new Triangulo3d(A1, new Vertice3d(A1.x(), A1.y(), A1.z() + 1), B1);

		// 2. encontrar los ganadores de cada poliedro

		boolean esinicio = true;

		// al inicio comenzar por una cualquiera

		NodoArista aristaComienzoIzq;
		NodoArista aristaComienzoDer;

		aristaComienzoIzq = A1.arista();
		aristaComienzoDer = B1.arista();

		ListaDE listaVertices = new ListaDE();
		ListaDE listaFormacionCaras = new ListaDE();
		listaVertices.insertarInicio(new NodoArista(A1, B1));

		// ojo con esta cota, porque se puede quedar escasa
		// debe ser tal que nos garantice que se da una vuelta completa
		// envolviendo a los dos poliedros

		// se puso porque hay veces que la primera arista puente no
		// vuelve a repetirse (sucede en el caso no general)

		// ATENCION: si esta cota no es suficientemente grande la cosa no
		// funcionara
		// porque no se completara una vuelta entera

		int cota = 3 * (A.totalVertices() + B.totalVertices());

		Vertice3d primerA1 = A1;
		Vertice3d primerB1 = B1;
		boolean condicionFin = false; // esto es lo que deberia usarse

		// condicionFin = A1.igual(primerA1) && B1.igual(primerB1);

		// RECORRIDO A
		// =========================================================================

		// en este recorrido se definen las aristas puente que dan lugar a las
		// nuevas caras

		boolean esInicio = true, seRepite = false, seRepitio = false;

		for (int h = 0; h < cota; h++) {

			// buscamos los candidatos

			if (buscarIz)
				aristaSigIz = A.ganadoraIzq(A1, B1, aristaComienzoIzq);
			if (buscarDe)
				aristaSigDe = B.ganadoraDer(B1, A1, aristaComienzoDer);

			// TEST de las caras candidatas. Una de las dos sera la nueva cara.
			// Por eso una de las dos
			// y solo una define un plano soporte de todos los puntos de ambos
			// poliedros

			verticeSigIz = A1.igual(aristaSigIz.ori()) ? aristaSigIz.des() : aristaSigIz.ori();
			verticeSigDe = B1.igual(aristaSigDe.ori()) ? aristaSigDe.des() : aristaSigDe.ori();

			caraIzq = new Triangulo3d(verticeSigIz, B1, A1);
			caraDer = new Triangulo3d(verticeSigDe, B1, A1);

			// 3. comparar a los dos ganadores para conocer el siguiente vertice

			Vertice3d temp = verticeSigDe.restar(B1);
			temp = temp.sumar(A1);

			ganaA = GC.volumen6(A1, B1, temp, verticeSigIz) < 0;
			// ganaB = GC.volumen6(A1,B1,temp,verticeSigIz)>0;

			items = new Object[5]; // conjunto de informaci�n para una nueva
									// cara

			// 4. avanzar al siguiente vertice

			if (ganaA) {

				A2 = verticeSigIz;

				caraAnterior = new Triangulo3d(B1, A1, A2);

				items[0] = aristaSigIz;
				items[1] = B1;
				items[2] = new Boolean(false); // la arista es del lado derecho
												// : falso
				items[3] = new Boolean(aristaSigIz.ori().igual(A1) ? false : true);
				// la orientacion de la arista de la nueva cara

				// listaVertices.insertarInicio(A1);

				A1 = A2;
				aristaComienzoIzq = aristaSigIz;
				aristaComienzoDer = aristaSigDe;

				buscarIz = true;
				buscarDe = true;

			} else {

				B2 = verticeSigDe;

				caraAnterior = new Triangulo3d(B1, A1, B2);

				// listaVertices.insertarInicio(B1);

				items[0] = aristaSigDe;
				items[1] = A1;
				items[2] = new Boolean(true); // la arista es del lado derecho :
												// cierto
				items[3] = new Boolean(aristaSigDe.ori().igual(B1) ? false : true);
				// la orientacion de la arista de la nueva cara

				B1 = B2;
				aristaComienzoDer = aristaSigDe;
				aristaComienzoIzq = aristaSigIz;

				buscarIz = true;
				buscarDe = true;

			}

			// la arista puente siempre tiene origen en C1 y detino en C2

			items[4] = new NodoArista(A1, B1);

			listaFormacionCaras.insertarInicio(items);

			listaVertices.insertarInicio(new NodoArista(A1, B1));

			// test

			seRepite = A1.igual(primerA1) && B1.igual(primerB1);

			if (esInicio)
				esInicio = false;

			else {

				if (seRepite) {

					seRepitio = true;
					break;
				}

			}

			// fin test

		} // for

		// -----------------------------------------------------------------------------------
		// ==== Lista de aristas puente
		// ======================================================

		if (!seRepitio) {
			throw new BucleEnvolviendoException();
		}

		NodoArista item;
		int total = 0;
		iter = listaVertices.listIterator();

		NodoArista primera = (NodoArista) iter.next();

		do {
			item = (NodoArista) iter.next();
			total++;

		} while (!primera.igual(item));

		// ---------------------------------------------------------------------------------
		// test de marcado de caras. ESTE TEST SE DEBE SUPRIMIR DESPUES

		// RECORRIDO B
		// =========================================================================
		// ==== Recorrido de formacion de caras
		// ==================================================

		// en este recorrido se forman las nuevas caras y las nuevas aristas
		// se itera tantas veces como aristas puente hay (variable total)

		/*
		 * por que se recorre en sentido contrario al habitual? En la
		 * listaFormacionCaras esta la informacion necesaria para crear las
		 * caras. Se creo en el recorrido inicial. Se fue apilando cada vez un
		 * elemento de informaci�n para generar una cara. Como en este recorrido
		 * se ha iterado |C0|+|C1| veces entonces podemos asegurar que
		 * desapilando de esta lista vamos a encontrar el elemento que estaba en
		 * la cima en el instante inicial. Si en lugar de un for tuvieramos un
		 * while con la condicion de que hasta que se repita la primera cara no
		 * funcionaria. (Contraejemplo: C1 y C0 ambos cubos)
		 * 
		 */

		// listaNuevasCaras :
		// lista con las nuevas caras que han de insertarse
		// en la DCEL resultante

		ListaDE listaNuevasCaras = new ListaDE();

		// listaAristasPuente:
		// lista con las nuevas aristas (las creadas por ser puente)
		// que han de insertarse en la DCEL resultante

		ListaDE listaAristasPuente = new ListaDE();

		// nos preparamos para el recorrido B por la listaFormacionCaras que
		// contiene informaci�n
		// de las caras, para que en cada iteraci�n creemos una nuevaCara y

		iter = listaFormacionCaras.listIterator();
		items = (Object[]) iter.next();

		NodoArista aristaRecorrido = (NodoArista) items[0];

		Vertice3d verticeRecorrido = (Vertice3d) items[1];

		// la incializacion de estas variables booleanas no tiene importancia
		// solo es para evitar warnings del compilador

		boolean antesSeAvanzoDerecha = false;

		// cierto sii hemos avanzado en el poliedro derecho

		boolean esAvanceDerecho = false;

		// cierto sii la arista puente est� bien orientada

		boolean esOrientacionOk;

		// UNA NUEVA CARA

		Triangulo3d nuevaCara = null;

		// UNA NUEVA ARISTA

		NodoArista aristaNuevaPuente = null;

		// para el recorrido en profundidad marcando caras. Son las aristas
		// iniciales

		Triangulo3d primeraCaraRojaDer = null;
		Triangulo3d primeraCaraRojaIzq = null;

		// para asignaciones de atributos de objetos de la clase NodoArista

		Triangulo3d anteriorNuevaCara = null;

		// primera arista puente.
		// para asignaciones de atributos de objetos de la clase NodoArista

		NodoArista primeraAristaNuevaPuente = null;

		NodoArista anteriorAristaRecorrido = null;

		// variable auxiliar para mejorar la legibilidad

		Triangulo3d caraRoja;

		NodoArista aristaAnteriorPuente = null;

		esInicio = true;

		/*
		 *** sobre como actualizar cual es la siguiente en sentido CCW despues de
		 * la mezcla:
		 * 
		 * hay varios tipos de asignaciones:
		 * 
		 * 
		 * 
		 * tipo 0: la siguiente a la arista puente de la iteracion actual es la
		 * arista del recorrido de la iteracion actual
		 * 
		 * tipo 1: la siguiente a la arista del recorrido de la iteracion actual
		 * es la arista puente de la iteracion actual
		 * 
		 * tipo 2: la siguiente a la arista puente de la iteracion anterior es
		 * la arista puente de la iteracion actual
		 * 
		 * tipo 3: la siguiente a la arista puente de la iteracion actual es la
		 * arista puente de la iteracion anterior
		 * 
		 * tipo 4: la siguiente a la arista del recorrido de la iteracion
		 * anterior es la arista puente de la iteracion actual
		 * 
		 * tipo 5: la siguiente la arista puente de la iteracion anterior es la
		 * arista puente de la iteracion actual
		 * 
		 * 
		 */

		// do {
		for (int j = 0; j < total; j++) {

			// guardar la cara anterior si nuevaCara no es la primera cara nueva

			if (nuevaCara != null) {
				anteriorNuevaCara = nuevaCara;
				aristaAnteriorPuente = aristaNuevaPuente;
				antesSeAvanzoDerecha = esAvanceDerecho;

			}

			// asignar cuales son las aristas y vertices purpura

			aristaRecorrido.ponColor(EstadoColor.PURPURA);
			aristaRecorrido.ori().ponColor(EstadoColor.PURPURA);
			aristaRecorrido.des().ponColor(EstadoColor.PURPURA);
			verticeRecorrido.ponColor(EstadoColor.PURPURA);

			// esto lo hacemos matener la integridad referencial de vertices
			// sobre aristas
			// Solo los vertices que pertenecen a aristas purpura pueden suponer
			// problemas
			// referenciales, es decir, que apunten a una arista que va a ser
			// borrada

			// por otra parte, una cara azul que se conserve de A o de B, no va
			// a suponer
			// problemas de integridad referencial ya que nunca va a apuntar a
			// una arista
			// que va a ser borrada, ya que una arista que va a ser borrada
			// tiene a ambos
			// lados caras rojas

			aristaRecorrido.ori().asignaArista(aristaRecorrido);
			aristaRecorrido.des().asignaArista(aristaRecorrido);

			// esAvanceDerecho sirve para distinguir en cual cierre avanzamos si
			// C1 o C2

			esAvanceDerecho = ((Boolean) items[2]).booleanValue();

			// esOrientacionOk sirve para saber si aristaRecorrido tiene el
			// sentido adecuado

			esOrientacionOk = ((Boolean) items[3]).booleanValue();
			aristaRecorrido.ponOrientacionOk(esOrientacionOk);

			// aristaNuevaPuente es la arista de la nueva cara q es arista
			// puente

			aristaNuevaPuente = (NodoArista) items[4];

			if (esAvanceDerecho) {

				// avanza la arista en el lado derecho

				caraRoja = esOrientacionOk ? aristaRecorrido.caraIzq() : aristaRecorrido.caraDer();

				caraRoja.ponColor(EstadoColor.ROJO);

				// guardar una cara para el recorrido en profundidad para
				// marcar caras rojas

				if (primeraCaraRojaDer == null)
					primeraCaraRojaDer = caraRoja;

				nuevaCara = esOrientacionOk ?

						new Triangulo3d(aristaRecorrido.ori(), aristaRecorrido.des(), verticeRecorrido) :

						new Triangulo3d(aristaRecorrido.des(), aristaRecorrido.ori(), verticeRecorrido);

				if (esOrientacionOk)

					aristaRecorrido.asignaSigCaras(nuevaCara, null);

				else
					aristaRecorrido.asignaSigCaras(null, nuevaCara);

				/*
				 * if (aristaRecorrido.caraIzq().dameColor()==EstadoColor.ROJO)
				 * 
				 * // la nueva cara se encuentra a la izquierda de esta arista
				 * 
				 * aristaRecorrido.asignaSigCaras(nuevaCara,null);
				 * 
				 * // la nueva cara se encuentra a la derecha de esta arista
				 * 
				 * else aristaRecorrido.asignaSigCaras(null,nuevaCara);
				 */

				// tipo 1

				if (esOrientacionOk)
					aristaRecorrido.asignaSigCCW(aristaNuevaPuente, null);
				else
					aristaRecorrido.asignaSigCCW(null, aristaNuevaPuente);

			} else {

				// avanza la arista en el lado izquierdo

				caraRoja = esOrientacionOk ? aristaRecorrido.caraDer() : aristaRecorrido.caraIzq();

				caraRoja.ponColor(EstadoColor.ROJO);

				if (primeraCaraRojaIzq == null)
					primeraCaraRojaIzq = caraRoja;

				nuevaCara = !esOrientacionOk ?

						new Triangulo3d(aristaRecorrido.ori(), aristaRecorrido.des(), verticeRecorrido) :

						new Triangulo3d(aristaRecorrido.des(), aristaRecorrido.ori(), verticeRecorrido);

				if (esOrientacionOk)

					aristaRecorrido.asignaSigCaras(null, nuevaCara);

				else

					aristaRecorrido.asignaSigCaras(nuevaCara, null);

				/*
				 * if (aristaRecorrido.caraIzq().dameColor()==EstadoColor.ROJO)
				 * 
				 * // la nueva cara se encuentra a la izquierda de esta arista
				 * 
				 * aristaRecorrido.asignaSigCaras(nuevaCara,null);
				 * 
				 * // la nueva cara se encuentra a la derecha de esta arista
				 * 
				 * else aristaRecorrido.asignaSigCaras(null,nuevaCara);
				 * 
				 */

				// tipo 0
				aristaNuevaPuente.asignaCCW(aristaRecorrido, null);

			}

			if (!esInicio) {

				if (antesSeAvanzoDerecha) { // anteriormente se avanzo a la
											// derecha

					// tipo 2

					aristaAnteriorPuente.asignaCCW(aristaNuevaPuente, null);

					// tipo 4

					aristaNuevaPuente.asignaCCW(null, anteriorAristaRecorrido);

				} else { // anteriormente se avanzo a la izquierda

					// tipo 3

					aristaNuevaPuente.asignaCCW(null, aristaAnteriorPuente);

					// tipo 5

					if (anteriorAristaRecorrido.dameOrientacionOk())
						anteriorAristaRecorrido.asignaSigCCW(null, aristaNuevaPuente);
					else
						anteriorAristaRecorrido.asignaSigCCW(aristaNuevaPuente, null);

				}

			} else
				esInicio = false;

			if (anteriorNuevaCara == null) {

				primeraAristaNuevaPuente = aristaNuevaPuente;

			}

			aristaNuevaPuente.asignaCaras(nuevaCara, anteriorNuevaCara);

			nuevaCara.asignaArista(aristaNuevaPuente);

			// insertamos en las lista de nuevas caras y nuevas aristas

			listaNuevasCaras.insertarInicio(nuevaCara);
			listaAristasPuente.insertarInicio(aristaNuevaPuente);

			// iterar nuevamente

			items = (Object[]) iter.next();

			anteriorAristaRecorrido = aristaRecorrido;

			aristaRecorrido = (NodoArista) items[0];

			verticeRecorrido = (Vertice3d) items[1];

		} // for RECORRIDO B

		primeraAristaNuevaPuente.asignaCaras(null, nuevaCara);

		// ya se han insertado todas las caras y aristas puente
		// esto quedo por hacer: a la ultima arista puente y aquellas
		// relacionadas con ella
		// definir algunos atributos como por ejemplo cual es la siguiente CCW

		if (esAvanceDerecho) { // anteriormente se avanzo a la derecha

			/*
			 * // System.out.println("antes de avanzo derecha. \n"+
			 * aristaNuevaPuente.verticesString()+
			 * "\n"+primeraAristaNuevaPuente.verticesString());
			 */

			// tipo 2

			aristaNuevaPuente.asignaCCW(primeraAristaNuevaPuente, null);

			// tipo 4

			primeraAristaNuevaPuente.asignaCCW(null, anteriorAristaRecorrido);

		} else { // anteriormente se avanzo a la izquierda

			// tipo 3

			/*
			 * // System.out.println("antes de avanzo izquierda. \n"+
			 * aristaNuevaPuente.verticesString()+
			 * "\n"+primeraAristaNuevaPuente.verticesString());
			 */

			primeraAristaNuevaPuente.asignaCCW(null, aristaNuevaPuente);

			// tipo 5

			if (anteriorAristaRecorrido.dameOrientacionOk())
				anteriorAristaRecorrido.asignaSigCCW(null, primeraAristaNuevaPuente);
			else
				anteriorAristaRecorrido.asignaSigCCW(primeraAristaNuevaPuente, null);
		}

		// A.aDisco("testColorA.txt");
		// B.aDisco("testColorB.txt");

		// marcado de las aristas ===========================================

		// Marcado de las caras

		// A.aDisco("antesMarcadoA");
		// B.aDisco("antesMarcadoB");

		A.marcarCaras(primeraCaraRojaIzq);
		B.marcarCaras(primeraCaraRojaDer);

		A.marcarAristas();
		B.marcarAristas();

		A.marcarVertices();
		B.marcarVertices();

		// listado de caras nuevas
		// ==============================================

		Triangulo3d caraTiraCilindrica;
		iter = listaNuevasCaras.listIterator();
		while (iter.hasNext()) {

			caraTiraCilindrica = (Triangulo3d) iter.next();

			// incluir en dcel

			C.insertar(caraTiraCilindrica);

		}

		// listado de las arista puente
		// ===========================================

		NodoArista aristaTiraCilindrica;
		iter = listaAristasPuente.listIterator();
		while (iter.hasNext()) {

			aristaTiraCilindrica = (NodoArista) iter.next();

			// incluir en dcel

			C.insertar(aristaTiraCilindrica);

		}

		// actualizacion de las aristas del recorrido
		// ==================================
		// (aquellas que son de A o de B y hay que modificar)

		// aqui actualizamos las aristas de los cierres previos
		// porque si hubiesemos hecho este proceso antes del proceso de marcado
		// el recorrido en profundidad seria defectuoso

		iter = listaFormacionCaras.listIterator();

		for (int i = 0; i < total; i++) {

			items = (Object[]) iter.next();

			aristaRecorrido = (NodoArista) items[0];

		}

		iter = listaFormacionCaras.listIterator();

		for (int i = 0; i < total; i++) {

			items = (Object[]) iter.next();

			aristaRecorrido = (NodoArista) items[0];

			// asigna los valores correspondientes para actualizar las aristas
			// purpura
			// mediante los metodos de las aristas para actualizacion
			// correspondientes
			// Los valores con los cuales se actualizan fueron definidos
			// anteriormente

			aristaRecorrido.actualizaSigCaras();
			aristaRecorrido.actualizaSigCCW();

		}

		if (C.totalVertices() != 0)
			throw new MezclaDCELException();

		C.fundir(A, B);

		// C.check();

	}// fin mezcla

	private Segmento hallarPuente() {

		Poligono PA, PB;

		PA = A.proyecOptima();
		PB = B.proyecOptima();

		return PA.soporte(PB);
	}

	/**
	 * La cota que define el tama�o maximo del caso base.
	 */
	public static final int COTA = 30;

	private static int cota = 30;

	/**
	 * 
	 * Definir la cota del tama�o del caso base en Divide y venceras
	 * 
	 * @param valor
	 *            Numero de puntos que define el tama�o del caso base
	 * 
	 * 
	 */

	public void setCota(int valor) {

		cota = valor;
	}

	/**
	 * 
	 * Definir la cota del tama�o del caso base en Divide y venceras
	 * 
	 * @param valor
	 *            Numero de puntos que define el tama�o del caso base
	 * 
	 * 
	 */

	public int getCota() {

		return cota;
	}

}