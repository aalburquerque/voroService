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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;

import com.aalburquerque.voronoi.struc.ICoord;

/**
 * 
 * Para controlar como se pintan los objetos dibujables en el plano de dibujo
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Coord implements Serializable, ICoord {
	private static final long serialVersionUID = 1L;
	private double zoom;

	private double[] pasos = { 0.015625, 0.03125, 0.0625, 0.125, 0.2500 };

	private double dzoom; // el paso o incremento del zoom

	public void setPaso(int paso) {

		dzoom = pasos[paso];
	}

	// si se define dzoom por ejemplo con un valor 0.05 surge un problema
	// numerico de precision
	// que hace que los segmentos no acotados del diagrama de Voronoi se dibujen
	// mal

	private double minZoom = 0.001;
	private double maxZoom = 4;

	private int ori[] = { 80, 200 };

	public static final int MANUAL = 0;
	public static final int AUTO = 1;

	private int modo;

	private FileWriter fw;
	private BufferedWriter salida;

	public Coord(int zoom) {

		this.zoom = zoom;
		setPaso(2);
		modo = AUTO;
	}

	public void ponModo(int cual) {
		modo = cual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see voronoi.calculo.util.ICoord#x(long)
	 */
	public int x(long wx) {

		long resultado = modo == AUTO ? ori[0] + (long) (Math.round(wx * zoom)) : wx;

		if (esLog)
			msgLog("\n X: " + new Long(resultado).toString());

		return (int) resultado;
	}

	private void msgLog(String cad) {

		try {

			fw = new FileWriter("LOG_COORD.txt", true);
			salida = new BufferedWriter(fw);
			salida.write(cad);
			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see voronoi.calculo.util.ICoord#y(long)
	 */
	public int y(long wy) {

		long resultado = modo == AUTO ? Util.LIMY - (ori[1] + (long) (Math.round(wy * zoom))) : wy;

		if (esLog)
			msgLog("   Y: " + new Long(resultado).toString());

		return (int) resultado;
	}

	public void zoomIn() {
		if (zoom + dzoom < maxZoom)
			zoom += dzoom;
	}

	public void zoomOut() {
		if (zoom - dzoom > minZoom)
			zoom -= dzoom;
	}

	public void derecha(int d) {
		ori[0] += d;
	}

	public void izquierda(int d) {
		ori[0] -= d;
	}

	public void arriba(int d) {
		ori[1] += d;
	}

	public void abajo(int d) {
		ori[1] -= d;
	}

	public String toString() {
		return "zoom: " + new Double(zoom).toString() + " dzoom: " + new Double(dzoom);
	}

	private boolean esLog = false;

	public void setLog(boolean valor) {
		esLog = valor;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

}