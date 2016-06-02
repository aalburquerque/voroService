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

package com.aalburquerque.voronoi.concurrent;

import com.aalburquerque.voronoi.struc.impl.ListaDE;
import com.aalburquerque.voronoi.util.Util;

/**
 * El objeto ObjProtegido tiene la mision de sincronizar hilos de ejecucion.
 * Consiste en un objeto protegido que ofrece metodos para a�adir y obtener un
 * elemento mediante una politica producto-consumidor. Es decir, los metodos que
 * ofrece este objeto aseguran exclusion mutua a los hilos que los utilizan.
 * <br>
 * Una caracteristica importante a tener en cuenta de este objeto protegido es
 * que los elementos se almacenan y se obtienen de en una cola. No se tiene un
 * maximo numero de elementos para introducir en la cola. Es decir, se supone un
 * cola de tama�o infinita o lo que es lo mismo la operacion de a�adir un nuevo
 * elemento nunca es bloqueante. <br>
 * Sin embargo la operacion de obtener un elemento si es bloqueante si no hay
 * ningun elemento disponible en la cola.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class ObjProtegido {

	// la estructura de este objeto es la de una cola.
	private ListaDE lista;

	/**
	 * Para construir un nuevo objeto protegido.
	 */

	public ObjProtegido() {

		lista = new ListaDE();

	}

	/**
	 * Para a�adir un nuevo elemento al objeto protegido. Este metodo asegura
	 * que se cumple la exclusion mutua cuando varios hilos usan a la vez el
	 * mismo. Una vez que se asegura que la exclusion mutua
	 */

	public synchronized void put(Object a) {

		lista.insertarInicio(a);

		notify();

	}

	/**
	 * Para obtener un elemento del objeto protegido. Este metodo asegura que se
	 * cumple la exclusion mutua cuando varios hilos usan a la vez el mismo.
	 * Esta operacion ademas bloquea al hilo que la llama si no hay ningun
	 * elemento en la cola, y hasta que otro hilo almacene un elemento
	 */

	public synchronized Object get() {

		while (lista.vacia()) {

			try {
				wait();

			} catch (InterruptedException ie) {

				Util.msgLog("LOGEXCEPTION", "Error en clase ObjProtegido: fallo en la llamada a wait");
				ie.printStackTrace();
			}

		}

		return lista.suprimirNodo(lista.get_total());

	}

} // fin clase ObjProtegido
