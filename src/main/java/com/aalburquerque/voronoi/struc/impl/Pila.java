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
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Pila extends ListaDE implements Serializable  {
	private static final long serialVersionUID = 1L;
	public Pila() {
		super();
	}

	public void apilar(Object objeto){
		insertarInicio(objeto);
	}
	
	public Object desapilar(){
		return suprimirNodo(get_total());
	}
}