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

import com.aalburquerque.voronoi.struc.ListAdaptor;
import com.aalburquerque.voronoi.util.EstadoColor;

/**
 * El objeto Aristas implementa la lista de aristas de una DCEL. Este objeto
 * hereda del objeto ListaDE y a�ade todas aquellas funciones que deben ser
 * propias de una lista de aristas de una DCEL.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 * @see DCEL
 * @see Caras
 * @see Vertices
 */

public class Aristas extends ListaDE implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DCEL dcel;

	/**
	 * Para construir una lista de aristas. Se le pasa como argumento la DCEL de
	 * la que esta arista forma su conjunto de aristas.
	 * 
	 * @param dcel
	 *            La DCEL de la que forma parte la lista de arista
	 */

	public Aristas(DCEL dcel) {
		super();

		this.dcel = dcel;
	}

	/**
	 * Para inicializar el conjunto de todas las aristas
	 */

	public void reset() {
		ListIterator iterador = listIterator();
		NodoArista n;
		while (iterador.hasNext()) {
			n = (NodoArista) iterador.next();
			n.reset();
		}
	}

	/**
	 * Este metodo sirve para clasificar cada arista de la lista segun si va a
	 * ser una arista descartable,o no, ya que no va a pertenecer al cierre en
	 * el siguiente para iterativo en el proceso incremental. Tambien se
	 * identifica si la arista va a pertenecer a la base del cono de visibilidad
	 * por el punto correspondiente al paso iterativo del proceso incremental.
	 * Es imprescindible de que antes de llamar a este metodo se llame al metodo
	 * Finalmente se devuelve una arista que tenga a un lado una cara visible y
	 * al otro una cara no visible testVisible(Punto3d) de la clase Caras
	 */

	public NodoArista testVisible() {

		ListIterator iterador = listIterator();

		NodoArista una;

		boolean b1, b2;

		// una arista del borde, anotamos para despues
		// inicializar la iteracion por el borde

		NodoArista del_borde = null;

		// una arista del borde, anotamos para depues
		// inicializar la iteracion por el borde

		while (iterador.hasNext()) {

			una = (NodoArista) iterador.next();

			b1 = una.caraIzq().getEstado() == Triangulo3d.VISIBLE;
			b2 = una.caraDer().getEstado() == Triangulo3d.VISIBLE;

			if (b1 && b2)
				una.setEstado(NodoArista.BORRABLE);

			else if (b1 || b2) {
				una.setEstado(NodoArista.BORDE);

				del_borde = una;

				una.ori().asignaAristaBorde(una);
				una.des().asignaAristaBorde(una);
			}
		}

		return del_borde;

	}

	// marca las aristas q son del borde del poligono q resulta al proyectar el
	// poliedro en XY

	public NodoArista testProyeccion() {

		// este test sirve para clasificar las aristas segun sean o no
		// parte del borde la proyeccion de este poliedro sobre el plano XY

		ListIterator iterador = listIterator();

		// variable auxiliar

		NodoArista una;

		long a, b;

		// una arista del borde, anotamos para despues
		// inicializar la iteracion por el borde

		NodoArista alternativa = null, resultado = null;

		// una arista del borde, anotamos para depues
		// inicializar la iteracion por el borde

		while (iterador.hasNext()) {

			una = (NodoArista) iterador.next();

			a = una.caraIzq().nz();
			b = una.caraDer().nz();

			if (a == 0 && b == 0) // es una arista q une dos triangulos
									// coplanares

				continue; // no tratarla

			else if (a == 0 || b == 0 || a * b < 0) {
				/*
				 * if (a>0) // System.out.println(una.caraIzq().toString()+" "
				 * +a+" "+b+una.verticesString()); if (b>0) //
				 * System.out.println(una.caraDer().toString()+" "+a+" "
				 * +b+una.verticesString());
				 */
				if (una.ori().x() == una.des().x() && una.ori().y() == una.des().y())
					continue; // no proyectar un punto

				if (a * b < 0) {

					una.setEstado(NodoArista.BORDESEGURO);

					resultado = una;

				} else {

					una.setEstado(NodoArista.BORDEPOSIBLE);
					alternativa = una;
				}

				una.ori().asignaAristaProyeccion(una);
				una.des().asignaAristaProyeccion(una);

			}
		}

		// if (resultado==null) throw new RuntimeException("No Hay Segura
		// ninguna arista del borde");

		resultado = resultado != null ? resultado : alternativa;

		return resultado;

	}

	/**
	 * Para eliminar los vertices que no van a formar parte del poliedro final
	 * en una paso iterativo de proceso incremental
	 */

	public void limpiar() {

		// ojo: una arista q queramos borrar quizas este apuntada por un vertice
		// o una triangulo
		// para hacer posteriores recorridos

		// se implemento un check en dcel para observar el cumplimiento de la
		// INTEGRIDAD REFERENCIAL

		// PASO 1: afianzar integridad referencial de los vertices

		// es decir, si una arista es borrable entonces

		// debemos asegurar que sus vertices extremos

		// van a estar asociados a aristas no borrables

		ListIterator iter1 = listIterator();
		ListAdaptor iter2;

		NodoArista n;
		NodoArista m;

		while (iter1.hasNext()) {

			n = (NodoArista) iter1.next();

			if (n.getEstado() == NodoArista.BORRABLE) {

				if (n.ori().arista().igual(n)) {

					// la arista asociada a ori() es una borrable

					iter2 = dcel.verticeIterator(n.ori());

					m = null;

					while (iter2.hasNext()) {

						m = (NodoArista) iter2.next();

						if (m.getEstado() != NodoArista.BORRABLE)
							break;

					}

					if (m == null)
						throw new RuntimeException("Fallo: ninguna borrable ");

					n.ori().asignaArista(m);

				}

				if (n.des().arista().igual(n)) {

					// la arista asociada a des() es una borrable

					iter2 = dcel.verticeIterator(n.des());

					m = null;

					while (iter2.hasNext()) {

						m = (NodoArista) iter2.next();

						if (m.getEstado() != NodoArista.BORRABLE)
							break;

					}

					if (m == null)
						throw new RuntimeException("Fallo: ninguna borrable ");

					n.des().asignaArista(m);

				}

			}

		}

		// PASO 2: eliminar aristas

		iter1 = listIterator();

		while (iter1.hasNext()) {

			n = (NodoArista) iter1.next();

			if (n.getEstado() == NodoArista.BORRABLE)
				iter1.remove();

		}
	}

	/**
	 * Este metodo sirve para
	 */

	/**
	 * Este metodo sirve para marcar adecuadamente las arista que van a ser
	 * descartadas durante el proceso de mezcla de "Divide y Venceras"
	 */

	public void marcarAristas() {

		ListIterator iter1 = listIterator();

		NodoArista n;

		while (iter1.hasNext()) {

			// una arista es marcable si tiene dos caras incidentes rojas
			// a no ser que sea purpura, entonces tenemos la certeza de que no
			// es borrable

			n = (NodoArista) iter1.next();

			if ((n.dameColor() != EstadoColor.PURPURA) && (n.caraIzq().dameColor() == EstadoColor.ROJO)
					&& (n.caraDer().dameColor() == EstadoColor.ROJO))

				n.ponColor(EstadoColor.ROJO);

		}
	}

	/**
	 * Este metodo sirve para fijar como no descartables aquellos vertices que
	 * son extremo de aristas azules o purpuras, durante el proceso
	 * "Divide y venceras"
	 */

	public void descartarVertices() {

		ListIterator iter1 = listIterator();

		NodoArista n;

		while (iter1.hasNext()) {

			n = (NodoArista) iter1.next();

			if (n.dameColor() != EstadoColor.ROJO) {

				n.ori().ponColor(EstadoColor.AZUL);
				n.des().ponColor(EstadoColor.AZUL);

			}

		}
	}

	/**
	 * Para incluir aquellos vertices de la lista de vertices pasada como
	 * argumento que no son rojos
	 * 
	 * @param otros
	 *            La lista de los vertices
	 */

	public void incluir(Aristas otros) {

		// incluir aquellos elementos que no son rojos

		ListIterator iterador = otros.listIterator();

		NodoArista elemento;

		while (iterador.hasNext()) {

			elemento = (NodoArista) iterador.next();

			if (elemento.dameColor() != EstadoColor.ROJO) {

				insertarFinal(elemento);
			}

		}
	}

	/**
	 * Devuelve cierto si el vertice esta incluido en la lista de vertices
	 * 
	 * @param comparable
	 *            El vertice a comprobar si esta en la lista
	 */

	public boolean estaIncluida(NodoArista comparable) {

		ListIterator iterador = listIterator();

		NodoArista elemento;

		while (iterador.hasNext()) {

			elemento = (NodoArista) iterador.next();

			if (elemento.igualEstricto(comparable))
				return true;

		}

		return false;
	}

	/**
	 * Inicializar el color de todos los vertices
	 */

	public void resetColor() {

		ListIterator iterador = listIterator();

		NodoArista elemento;

		while (iterador.hasNext()) {

			elemento = (NodoArista) iterador.next();

			elemento.ponColor(EstadoColor.AZUL);
		}

	}

}