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
import java.util.HashSet;
import java.util.Set;

import com.aalburquerque.voronoi.struc.impl.Punto2D;
import com.aalburquerque.voronoi.struc.impl.Punto3d;

/**
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Input {

	public static Punto3d[] generalPlano(int total, long xmax, long ymax) {

		return generalPlano(total, 0, 0, xmax, ymax);

	}

	public static Punto3d[] generalPlano(int total, long xmin, long ymin, long xmax, long ymax) {

		if (total < 4)
			throw new RuntimeException("Pocos puntos");

		Punto3d[] a = new Punto3d[total];
		long x, y, z;
		Punto3d temp;

		int j, l;

		long k, m = (xmax - xmin) * (ymax - ymin);

		boolean flag;

		for (int i = 0; i < total; i++) {

			k = m;

			do {

				x = xmin + Math.round((xmax - xmin) * Math.random());
				y = ymin + Math.round((ymax - ymin) * Math.random());
				z = x * x + y * y;

				temp = new Punto3d(x, y, z);

				flag = true;

				for (j = 0; j < i && flag; j++)
					flag = !a[j].igual(temp);

				if (i > 1)
					for (j = 0; j < i && flag; j++)
						for (l = j + 1; l < i && flag; l++)
							flag = GC.area2(a[j].x(), a[j].y(), a[l].x(), a[l].y(), temp.x(), temp.y()) != 0;

				k--;

				if (k == 0)
					throw new RuntimeException("Imposible crear entrada");

			} while (!flag);

			a[i] = temp;

		}

		try {

			FileWriter fw = new FileWriter("input.txt");
			BufferedWriter salida = new BufferedWriter(fw);

			for (int i = 0; i < total; i++) {
				salida.write(i + ".  " + a[i].toString());
				salida.newLine();
			}

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

		return a;
	}

	public static Punto3d[] libre(int total, long xmax, long ymax) {
		return libre(total, 0, 0, xmax, ymax);
	}

	public static Punto3d[] libre(int total, long xmin, long ymin, long xmax, long ymax) {

		if (total < 4)
			throw new RuntimeException("Pocos puntos");

		Punto3d[] a = new Punto3d[total];
		long x, y, z;
		Punto3d temp;

		long k, m = (xmax - xmin) * (ymax - ymin);

		boolean flag;

		int j;

		for (int i = 0; i < total; i++) {

			k = m;

			do {

				x = xmin + Math.round((xmax - xmin) * Math.random());
				y = ymin + Math.round((ymax - ymin) * Math.random());
				z = x * x + y * y;

				temp = new Punto3d(x, y, z);

				flag = true;

				for (j = 0; j < i && flag; j++)
					flag = !a[j].igual(temp);

				k--;

				if (k == 0)
					throw new RuntimeException("Imposible crear entrada");

			} while (!flag);

			a[i] = temp;

		}

		try {

			FileWriter fw = new FileWriter("input.txt");
			BufferedWriter salida = new BufferedWriter(fw);

			for (int i = 0; i < total; i++) {
				salida.write(i + ".  " + a[i].toString());
				salida.newLine();
			}

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

		return a;
	}

	// devolver array de componenetes (x,y) tal que en este array no hay 4
	// componentes
	// cuyos puntos en el espacio segun (x,y,x^2+y^2) sean coplanares

	public static Punto3d[] generalEspacio(int total, long xmax, long ymax) {

		return generalPlano(total, 0, 0, xmax, ymax);

	}

	public static Punto3d[] generalEspacio(int total, long xmin, long ymin, long xmax, long ymax) {

		if (total < 4)
			throw new RuntimeException("Pocos puntos");

		Punto3d[] a = new Punto3d[total];
		long x, y, z;
		Punto3d temp;

		int j, l, r;

		long k, m = (xmax - xmin) * (ymax - ymin);

		boolean flag;

		for (int i = 0; i < total; i++) {

			k = m;

			do {

				x = xmin + Math.round((xmax - xmin) * Math.random());
				y = ymin + Math.round((ymax - ymin) * Math.random());
				z = x * x + y * y;

				temp = new Punto3d(x, y, z);

				flag = true; // suponemos q temp va a pasar la prueba

				for (j = 0; j < i && flag; j++)
					flag = !a[j].igual(temp);

				// no hay 3 en el plano q esten alineados

				if (i > 1)
					for (j = 0; j < i && flag; j++)
						for (l = j + 1; l < i && flag; l++)
							flag = GC.area2(a[j].x(), a[j].y(), a[l].x(), a[l].y(), temp.x(), temp.y()) != 0;

				// no hay 4 coplanares

				if (i > 2)
					for (j = 0; j < i && flag; j++)
						for (l = j + 1; l < i && flag; l++)
							for (r = l + 1; r < i && flag; r++)
								flag = GC.volumen6(a[j].x(), a[j].y(), a[j].z(), a[r].x(), a[r].y(), a[r].z(), a[l].x(),
										a[l].y(), a[l].z(), temp.x(), temp.y(), temp.z()) != 0;

				k--;

				if (k == 0)
					throw new RuntimeException("Imposible crear entrada");

			} while (!flag);

			a[i] = temp;

		}

		try {

			FileWriter fw = new FileWriter("input.txt");
			BufferedWriter salida = new BufferedWriter(fw);

			for (int i = 0; i < total; i++) {
				salida.write(i + ".  " + a[i].toString());
				salida.newLine();
			}

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

		return a;
	}

	public static Punto2D[] sitiosGeneralPlano(int total, long xmax, long ymax) {

		return sitiosGeneralPlano(total, 0, 0, xmax, ymax);

	}

	public static Punto2D[] sitiosGeneralPlano(int total, long xmin, long ymin, long xmax, long ymax) {

		if (total < 4)
			throw new RuntimeException("Pocos puntos");

		Punto3d[] a = new Punto3d[total];

		Punto2D[] resultado = new Punto2D[total];

		long x, y, z;
		Punto3d temp;

		int j, l;

		long k, m = (xmax - xmin) * (ymax - ymin);

		boolean flag;

		for (int i = 0; i < total; i++) {

			k = m;

			do {

				x = xmin + Math.round((xmax - xmin) * Math.random());
				y = ymin + Math.round((ymax - ymin) * Math.random());
				z = x * x + y * y;

				temp = new Punto3d(x, y, z);

				flag = true;

				for (j = 0; j < i && flag; j++)
					flag = !a[j].igual(temp);

				if (i > 1)
					for (j = 0; j < i && flag; j++)
						for (l = j + 1; l < i && flag; l++)
							flag = GC.area2(a[j].x(), a[j].y(), a[l].x(), a[l].y(), temp.x(), temp.y()) != 0;

				k--;

				if (k == 0)
					throw new RuntimeException("Imposible crear entrada");

			} while (!flag);

			a[i] = temp;

		}

		for (int w = 0; w < a.length; w++)

			resultado[w] = new Punto2D(a[w].x(), a[w].y());

		// System.out.println("test general espacio: "+testGeneralEspacio(a));

		return resultado;
	}

	// POSICION GENERAL EN EL ESPACIO

	// posicion general en el espacio: no hay 4 coplanares
	// devuelve un input al azar dentro de un cubo de longitud igual al param.
	// longitud

	public static Punto3d[] general(int total, long longitud) {

		return general(total, longitud, longitud, longitud);

	}

	public static Punto3d[] general(int total, long limx, long limy, long limz) {

		Punto3d[] a = new Punto3d[total];

		// boolean flag;

		do {

			for (int i = 0; i < total; i++)

				a[i] = new Punto3d(Math.round(limx * Math.random()), Math.round(limy * Math.random()),
						Math.round(limz * Math.random()));

			// testGeneralEspacio es cierto si han pasado el test
			// y no hay cuatro coplanares

			if (!testGeneralEspacio(a))
				continue;

			quicksort(a, 0, total - 1);

			// que no se repitan puntos

			for (int i = 1; i < total; i++)
				if (a[i].igual(a[i - 1]))
					continue;

			break;

		} while (true);

		try {

			FileWriter fw = new FileWriter("input.txt");
			BufferedWriter salida = new BufferedWriter(fw);

			for (int i = 0; i < total; i++)
				salida.write(a[i].salidaString() + "\n");

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

		return a;
	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************
	// POSICION GENERAL EN EL ESPACIO VORONOI

	// ES DECIR, PUNTOS EN UN PARABOLOIDE EN POSICION GENERAL

	// posicion general en el espacio: no hay 4 coplanares
	// devuelve un input al azar dentro de un cubo de longitud igual al param.
	// longitud

	public static Punto3d[] generalVoronoi(int total, long longitud) {

		return generalVoronoi(total, longitud, longitud);

	}

	public static Punto3d[] generalVoronoi(int total, long limx, long limy) {

		Punto3d[] a = new Punto3d[total];

		// boolean flag;
		long x = 0;
		long y = 0;

		do {

			for (int w = 0; w < total; w++) {

				x = Math.round(limx * Math.random());
				y = Math.round(limy * Math.random());

				a[w] = new Punto3d(x, y, x * x + y * y);
			}

			// testGeneralEspacio es cierto si han pasado el test
			// y no hay cuatro coplanares

			if (!testGeneralEspacio(a))
				continue;

			quicksort(a, 0, total - 1);

			// que no se repitan puntos

			for (int i = 1; i < total; i++)
				if (a[i].igual(a[i - 1]))
					continue;

			break;

		} while (true);

		try {

			FileWriter fw = new FileWriter("input.txt");
			BufferedWriter salida = new BufferedWriter(fw);

			for (int i = 0; i < total; i++)
				salida.write(a[i].proyeccString() + "\n");

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

		return a;
	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************

	// POSICION LIBRE: PUNTOS EN UN PARABOLOIDE EN POSICION LIBRE

	// posicion general en el espacio: no hay 4 coplanares
	// devuelve un input al azar dentro de un cubo de longitud igual al param.
	// longitud

	public static Punto3d[] libreVoronoi(int total, long longitud) {

		return libreVoronoi(total, longitud, longitud);

	}

	public static Punto3d[] libreVoronoi(int total, long limx, long limy) {

		Punto3d[] a = new Punto3d[total];

		// boolean flag;
		long x = 0;
		long y = 0;

		do {

			Set<String> conjunto = new HashSet<String>();

			for (int w = 0; w < total; w++) {
				boolean exists = false;
				do {
					x = Math.round(limx * Math.random());
					y = Math.round(limy * Math.random());
					exists = conjunto.contains(x + "," + y);
				} while (exists);
				conjunto.add(x + "," + y);

				a[w] = new Punto3d(x, y, x * x + y * y);
			}

			quicksort(a, 0, total - 1);

			break;

		} while (true);

		//for (int i = 0; i < total; i++) {
			//System.out.println(i + ".  [" + a[i].x() + " " + a[i].y() + "]");
		//}

		return a;
	}

	private static void quicksort(Punto3d[] A, int izq, int der) {

		int i = izq, j = der;
		Punto3d central = A[(i + j + 1) / 2];
		Punto3d temp;

		do {
			while (A[i].ordenXYZ(central) == 1)
				i++;
			while (central.ordenXYZ(A[j]) == 1)
				j--;

			if (i <= j) {
				temp = A[i];
				A[i] = A[j];
				A[j] = temp;
				i++;
				j--;
			}

		} while (!(i > j));

		if (izq < j)
			quicksort(A, izq, j);
		if (i < der)
			quicksort(A, i, der);
	}

	// devuelve cierto sii los puntos estan en posicion general

	public static boolean testGeneralEspacio(Punto3d[] a) {

		boolean seguir = true;

		int total = a.length;

		int i, j, k, h;

		for (i = 0; seguir && i < total; i++)
			for (j = 0; j != i && seguir && j < total; j++)
				for (k = 0; k != j && k != i && seguir && k < total; k++)
					for (h = 0; h != k && h != j && h != i && seguir && h < total; h++)

						seguir =

								GC.volumen6(a[i].x(), a[i].y(), a[i].z(), a[j].x(), a[j].y(), a[j].z(), a[k].x(),
										a[k].y(), a[k].z(), a[h].x(), a[h].y(), a[h].z()) != 0;

		return seguir;

	}

	public static boolean testGeneralEspacio2(Punto3d[] a) {

		// devuelve cierto si no hay cuatro puntos coplanares

		boolean noHayCuatroCoplanares = true;

		int total = a.length;

		int i, j, k, h;

		for (i = 0; noHayCuatroCoplanares && i < total; i++)
			for (j = 0; noHayCuatroCoplanares && j < total; j++)
				for (k = 0; noHayCuatroCoplanares && k < total; k++)
					for (h = 0; noHayCuatroCoplanares && h < total; h++)
						if (i != j && i != k && i != h && j != k && j != h && k != h)

							noHayCuatroCoplanares =

									GC.volumen6(a[i].x(), a[i].y(), a[i].z(), a[j].x(), a[j].y(), a[j].z(), a[k].x(),
											a[k].y(), a[k].z(), a[h].x(), a[h].y(), a[h].z()) != 0;

		return noHayCuatroCoplanares;

	}

	/**
	 * Metodo que devuelve un array de tama�o cuatro con los indices de cuatro
	 * puntos no coplanares o null en caso de que no existan cuatro no
	 * coplanares
	 */

	public static int[] cuatroGeneral(Punto3d[] a) {

		int[] indx = new int[4];

		int total = a.length;

		for (indx[0] = 0; indx[0] < total; indx[0]++)
			for (indx[1] = 0; indx[1] < total; indx[1]++)
				for (indx[2] = 0; indx[2] < total; indx[2]++)
					for (indx[3] = 0; indx[3] < total; indx[3]++)
						if (indx[0] != indx[1] && indx[0] != indx[2] && indx[0] != indx[3] && indx[1] != indx[2]
								&& indx[1] != indx[3] && indx[2] != indx[3] &&

								GC.volumen6(a[indx[0]], a[indx[1]], a[indx[2]], a[indx[3]]) != 0) {

							return indx;

						}

		return null;

	}

}
