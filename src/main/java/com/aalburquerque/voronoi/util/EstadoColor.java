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

package com.aalburquerque.voronoi.util;

import java.io.Serializable;

/**
 * Este objeto contiene un peque�o componente que representa el color que es en
 * definitiva un estado particular de una cara, o una arista, o un vertice, en
 * un momento dado del proceso de construccion del poliedro
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 * 
 */

public class EstadoColor implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Color AZUL de una cara o un vertice o una arista en un momento dado
	 */

	public static final int AZUL = 0;

	/**
	 * Color ROJO de una cara o un vertice o una arista en un momento dado
	 */

	public static final int ROJO = 1;

	/**
	 * Color PURPURA de una cara o un vertice o una arista en un momento dado
	 */

	public static final int PURPURA = 2;

	private int colorEstado;

	/**
	 * Constructor para iniciar el Color de una cara, un vertice o una arista
	 */

	public EstadoColor() {

		colorEstado = 0; // estado inicial

	}

	/**
	 * Metodo que devuelve el Color de una cara, un vertice o una arista
	 */

	public int dameColor() {
		return colorEstado;
	}

	/**
	 * Metodo para definir el Color de una cara, un vertice o una arista
	 */

	public void ponColor(int uncolor) {
		colorEstado = uncolor;
	}

	/**
	 * Metodo que devuelve una cadena con el Color de una cara, un vertice o una
	 * arista
	 */

	public String colorString() {
		return colorEstado == 0 ? " AZUL " : colorEstado == 1 ? " ROJO " : " PURPURA ";
	}

}