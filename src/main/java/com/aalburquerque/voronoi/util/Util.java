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
import java.util.Date;

import com.aalburquerque.voronoi.struc.impl.Coord2d;
import com.aalburquerque.voronoi.struc.impl.Punto3d;

/**
 * El objeto Util contiene algunos atributos y metodos estaticos de utilidad
 * para algunas otras clases
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Util {

	/**
	 * Ancho maximo del area de dibujo
	 */

	public static final int LIMX = 1024;

	/**
	 * Alto maximo del area de dibujo
	 */

	public static final int LIMY = 768;

	/**
	 * Metodo para saber si una coordenada esta dentro del area de dibujo
	 */

	public static boolean dentroMarco(long x, long y) {
		return x > 0 && x < LIMX && y > 0 && y < LIMY;
	}

	public static void msgLog(String fichName, String cad) {

		try {

			FileWriter fw = new FileWriter(fichName + ".LOG", true);
			BufferedWriter salida = new BufferedWriter(fw);

			String cabecera = "[ " + new Date(System.currentTimeMillis()).toString() + " ]: 	";

			salida.write(cabecera + cad + "\n");
			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}
	}

	public static Punto3d[] elevarSitios(Coord2d[] sitiosPlano) {

		Punto3d[] sitiosElevados = new Punto3d[sitiosPlano.length];

		for (int j = 0; j < sitiosPlano.length; j++)

			sitiosElevados[j] = new Punto3d(sitiosPlano[j].x(), sitiosPlano[j].y());

		return sitiosElevados;

	}

	public static Coord2d[] proyectarElevados(Punto3d[] sitiosElevados) {

		Coord2d[] sitiosPlano = new Coord2d[sitiosElevados.length];

		for (int j = 0; j < sitiosElevados.length; j++)

			sitiosPlano[j] = new Coord2d(sitiosElevados[j].x(), sitiosElevados[j].y());

		return sitiosPlano;

	}

}