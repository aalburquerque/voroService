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

import com.aalburquerque.voronoi.struc.impl.Punto2D;
import com.aalburquerque.voronoi.struc.impl.Punto3d;
import com.aalburquerque.voronoi.struc.impl.Triangulo3d;
import com.aalburquerque.voronoi.struc.impl.Vertice3d;

/**
 * Este objeto contiene algunos metodos estaticos de utilidad que son
 * operaciones basicas muy usadas en geometria computacional y evitan el uso de
 * operaciones en coma flotante
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class GC {
	/**
	 * Devuelve el doble del area signada del triangulo con las coordenadas
	 * pasadas como parametro
	 * 
	 * @param x1
	 *            coordenada x de un punto del triangulo
	 * @param y1
	 *            coordenada y de un punto del triangulo
	 * @param x2
	 *            coordenada x de un punto del triangulo
	 * @param y2
	 *            coordenada y de un punto del triangulo
	 * @param x3
	 *            coordenada x de un punto del triangulo
	 * @param y3
	 *            coordenada y de un punto del triangulo
	 */

	public static double area2(long x1, long y1, long x2, long y2, long x3, long y3) {

		return (x2 * y3 + x3 * y1 + x1 * y2 - y1 * x2 - y2 * x3 - x1 * y3);

	}

	/**
	 * Devuelve el doble del area signada del triangulo cuyos puntos son pasados
	 * como parametro
	 * 
	 * @param p
	 *            un punto del triangulo
	 * @param q
	 *            un punto del triangulo
	 * @param r
	 *            un punto del triangulo
	 */

	public static double area2(Punto2D p, Punto2D q, Punto2D r) {

		long x1 = p.x();
		long x2 = q.x();
		long x3 = r.x();

		long y1 = p.y();
		long y2 = q.y();
		long y3 = r.y();

		return (x2 * y3 + x3 * y1 + x1 * y2 - y1 * x2 - y2 * x3 - x1 * y3);

	}

	/**
	 * Devuelve cierto si y solamente si el triangulo de coordenadas espaciales
	 * es visto desde el exterior por el vertice que representa un punto en el
	 * espacio
	 * 
	 * @param t
	 *            El triangulo en el espacio
	 * @param d
	 *            El vertice que representa un punto en el espacio
	 */

	public static boolean visible(Triangulo3d t, Vertice3d d) {

		return volumen6(t, d) < 0;

	}

	/**
	 * Devuelve cierto si y solamente si el triangulo de coordenadas espaciales
	 * es visto desde el exterior por el punto en el espacio
	 * 
	 * @param t
	 *            El triangulo en el espacio
	 * @param d
	 *            El punto en el espacio
	 */

	public static boolean visible(Triangulo3d t, Punto3d d) {

		return volumen6(t, new Vertice3d(d)) < 0;

	}

	/**
	 * Devuelve cierto si y solamente si los tres puntos en el espacio estan
	 * alineados
	 * 
	 * @param v0
	 *            Un punto en el espacio
	 * @param v1
	 *            Un punto en el espacio
	 * @param v2
	 *            Un punto en el espacio
	 */

	public static boolean alineados(Vertice3d v0, Vertice3d v1, Vertice3d v2) {

		// tres lo estan si lo estan sus proyecciones

		Punto2D p0, p1, p2;
		Punto2D q0, q1, q2;
		Punto2D r0, r1, r2;

		p0 = new Punto2D(v0.x(), v0.y());
		p1 = new Punto2D(v1.x(), v1.y());
		p2 = new Punto2D(v2.x(), v2.y());

		q0 = new Punto2D(v0.x(), v0.z());
		q1 = new Punto2D(v1.x(), v1.z());
		q2 = new Punto2D(v2.x(), v2.z());

		r0 = new Punto2D(v0.y(), v0.z());
		r1 = new Punto2D(v1.y(), v1.z());
		r2 = new Punto2D(v2.y(), v2.z());

		return (area2(p0, p1, p2) == 0) && (area2(q0, q1, q2) == 0) && (area2(r0, r1, r2) == 0);
	}

	public static double volumen6(Triangulo3d t, Vertice3d d) {

		Punto3d[] p = t.toArray();

		return volumen6(p[0].x(), p[0].y(), p[0].z(), p[1].x(), p[1].y(), p[1].z(), p[2].x(), p[2].y(), p[2].z(), d.x(),
				d.y(), d.z());
	}

	/**
	 * Devuelve el sextuplo del volumen signado del tetraedro cuyos puntos son
	 * pasados como parametro
	 * 
	 * @param a
	 *            un punto del tetraedro
	 * @param b
	 *            un punto del tetraedro
	 * @param c
	 *            un punto del tetraedro
	 * @param d
	 *            un punto del tetraedro
	 */

	public static double volumen6(Punto3d a, Punto3d b, Punto3d c, Punto3d d) {
		return volumen6(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z(), d.x(), d.y(), d.z());
	}

	/**
	 * Devuelve el sextuplo del volumen signado del tetraedro cuyas coordenadas
	 * de los puntos son pasados como parametro
	 * 
	 * @param ax
	 *            coordenada x de un punto del tetraedro
	 * @param ay
	 *            coordenada y de un punto del tetraedro
	 * @param az
	 *            coordenada z de un punto del tetraedro
	 * @param bx
	 *            coordenada x de un punto del tetraedro
	 * @param by
	 *            coordenada y de un punto del tetraedro
	 * @param bz
	 *            coordenada z de un punto del tetraedro
	 * @param cx
	 *            coordenada x de un punto del tetraedro
	 * @param cy
	 *            coordenada y de un punto del tetraedro
	 * @param cz
	 *            coordenada z de un punto del tetraedro
	 * @param dx
	 *            coordenada x de un punto del tetraedro
	 * @param dy
	 *            coordenada y de un punto del tetraedro
	 * @param dz
	 *            coordenada z de un punto del tetraedro
	 */

	public static double volumen6(long ax, long ay, long az, long bx, long by, long bz, long cx, long cy, long cz,
			long dx, long dy, long dz) {

		return (-az * by * cx + ay * bz * cx + az * bx * cy - ax * bz * cy - ay * bx * cz + ax * by * cz + az * by * dx
				- ay * bz * dx - az * cy * dx + bz * cy * dx + ay * cz * dx - by * cz * dx - az * bx * dy + ax * bz * dy
				+ az * cx * dy - bz * cx * dy - ax * cz * dy + bx * cz * dy + ay * bx * dz - ax * by * dz - ay * cx * dz
				+ by * cx * dz + ax * cy * dz - bx * cy * dz

		);

	}

}
