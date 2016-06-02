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

package com.aalburquerque.voronoi.struc;

import java.awt.Graphics;

/**
 * Interfaz que debe implementar un objeto que que quiera ser mostrado en el
 * area de dibujo. En un momento dado un objeto potencialmente dibujable tiene
 * unas coordenadas particulares que deben ser convertidas segun una escala
 * particular para ser mostradas en el area de dibujo. Mediante el metodo de
 * esta interfaz se hace posible que las coordenadas del objeto que quiere ser
 * dibujado se transformen apropiadamente a traves de un objeto Coord para ser
 * representado en el area de dibujo
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public interface Dibujable {

	/**
	 * Este metodo
	 * 
	 * @param g
	 *            El objeto de la libreria java.awt encargado del dibujo
	 *            propiamente dicho.
	 * @param coord
	 *            El objeto encargado de transformar apropiadamente las
	 *            coordenadas del objeto a las coordenadas del area de dibujo
	 */

	public void dibujar(Graphics g, ICoord coord);

}