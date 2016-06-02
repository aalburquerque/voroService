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

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ListIterator;

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.struc.ListAdaptor;
import com.aalburquerque.voronoi.util.GC;
import com.aalburquerque.voronoi.util.Util;

/**
 * La clase Poliedro representa un poliedro convexo de un conjunto de puntos en
 * el espacio. Es importante destacar que todo el peso de esta clase procede de
 * heredar todas las funcionalidades de la clase DCEL. Sin embargo, una funcion
 * propia y muy importante de esta clase es la de representar en el plano el
 * diagramas de Voronoi y de Delaunay a partir de la informacion contenida en la
 * DCEL. <br>
 * Es decir, el metodo que se encarga de dibujar este objeto realmente dibuja en
 * el plano el diagrama de Voronoi asociado al mismo y de modo opcional la
 * triangulacion de Delaunay. <br>
 * Conviene tambien se�alar algo acerca del constructor de este objeto. Si se
 * emplea el constructor que tiene por argumento unicamente el array de puntos
 * entonces la manera en que se calcula el cierre convexo es la siguiente. Se
 * usara el metodo "divide y venceras". Este metodo calculara el cierre sin
 * problemas si los puntos estan en posicion general, es decir, no hay cuatro
 * puntos que sean coplanares. En caso de que los puntos no esten en posicion
 * general entonces en algun caso puede producirse una excepcion en cuyo caso
 * procedera a calcularse el cierre mediante el algoritmo "incremental".
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Poliedro extends DCEL implements Dibujable, Serializable {

	/* ------------------------------------------------------------------- */
	/* A T R I B U T O S */
	/* ------------------------------------------------------------------- */

	// hay que dibujar delaunay tambien sii este valor es cierto

	private boolean esDibujarDelaunay = false;

	/* ------------------------------------------------------------------- */
	/* C O N S T R U C T O R E S */
	/* ------------------------------------------------------------------- */

	/**
	 * Construir un nuevo poliedro que sea el cierre convexo del array de puntos
	 * pasado como parametro.
	 * 
	 * @param puntos
	 *            El array de puntos del espacio que definen un cierre convexo
	 * @param cota
	 *            Tama�o del caso base. Se puede optar por el valor
	 *            DivideYvenceras.COTA
	 */

	public Poliedro(Punto3d[] puntos, int cota) {
		super(puntos, cota);
	}

	/**
	 * Construir un nuevo poliedro que sea el cierre convexo del array de puntos
	 * pasado como parametro, especificando cual es el metodo de construccion
	 * deseado. Si porincremental es cierto entonces definimos una preferencia
	 * por este metodo.
	 * 
	 * @param puntos
	 *            El array de puntos del espacio que definen un cierre convexo
	 * @param porincremental
	 *            Valor booleano que especifica una preferencia respecto al
	 *            metodo.
	 */

	public Poliedro(Punto3d[] puntos, boolean porincremental, int cota) {

		super(puntos, porincremental, cota);
	}

	/**
	 * Construir un nuevo poliedro que sea el cierre convexo de los poliedros A
	 * y B. A y B son dos poliedros de interseccion vacia separados por un plano
	 * vertical paralelo a YZ Todos los puntos de A tienen abscisa menor que
	 * cualquier punto de B.
	 * 
	 * @param A
	 *            poliedro con menores abscisas
	 * @param B
	 *            poliedro con mayores abscisas
	 */

	public Poliedro(DCEL A, DCEL B) {

		super(A, B);

	}

	/**
	 * Envolver el DCEL de la entrada en la clase para poder represetarlo
	 * 
	 * @param B
	 *            poliedro con mayores abscisas
	 */

	public Poliedro(DCEL A) {

		super(A);

	}

	public void setDibujarDelaunay(boolean valor) {
		esDibujarDelaunay = valor;
	}

	private boolean esDibujarNoAcotadas = true;

	public void setDibujarNoAcotadas(boolean valor) {
		esDibujarNoAcotadas = valor;
	}

	/**
	 * Metodo para dibujar este objeto
	 * 
	 * @param g
	 *            El objeto de la clase Graphics donde dibujar
	 * @param coord
	 *            El objeto por el que se transforma las coordenadas de este
	 *            objeto a las coordenadas del espacio de representacion
	 */

	public void dibujar(Graphics g, ICoord coord) {

		if (esDibujarDelaunay)
			dibujarDelaunay(g, coord);
		dibujarVoronoi(g, coord);
	}

	private transient Graphics g;
	private transient ICoord coord;

	private void dibujarVoronoi(Graphics g, ICoord coord) {

		System.out.println("Voronoi: ");

		// para conocer el factor por el que multiplicamos el segmento
		// que separa regiones no acotadas

		this.g = g;
		this.coord = coord;

		ListIterator iterador = caras.listIterator();

		g.setColor(Color.blue);

		// System.out.println(metodoString());

		Triangulo3d t;

		// circuncentros

		long[] cc, b1, b2;

		boolean circuncentro_a_la_derecha;

		NodoArista arista;

		// para separar regiones no acotadas
		long ux, uy; // vector de la arista
		long uxorto, uyorto; // vector ortogonal a (ux,uy)

		while (iterador.hasNext()) {

			t = (Triangulo3d) iterador.next();

			if (t.nz() < 0) {

				ListAdaptor iter = caraIterator(t);

				while (iter.hasNext()) {

					arista = iter.next();

					if (arista.caraIzq().nz() < 0 && arista.caraDer().nz() < 0) {

						b1 = arista.caraIzq().circuncentro();
						b2 = arista.caraDer().circuncentro();

						g.drawLine(coord.x(b1[0]), coord.y(b1[1]),

								coord.x(b2[0]), coord.y(b2[1]));

						System.out.println("[ " + coord.x(b1[0]) + " " + coord.y(b1[1]) + " " +

								coord.x(b2[0]) + " " + coord.y(b2[1]) + " ]"

						);

					} else if (arista.caraIzq().nz() < 0 && esDibujarNoAcotadas) {

						// circuncentro

						cc = arista.caraIzq().circuncentro();

						ux = arista.des().x() - arista.ori().x();
						uy = arista.des().y() - arista.ori().y();

						circuncentro_a_la_derecha =

								GC.area2(arista.ori().x(), arista.ori().y(), arista.des().x(), arista.des().y(), cc[0],
										cc[1]) > 0;

						// uyorto = !circuncentro_a_la_derecha ? ux : -ux ;
						// uxorto = !circuncentro_a_la_derecha ? -uy : uy ;

						uyorto = ux;
						uxorto = -uy;

						segmentoNoAcotado(cc, uxorto, uyorto, circuncentro_a_la_derecha);

					} else if (arista.caraDer().nz() < 0 && esDibujarNoAcotadas) {

						// circuncentro

						cc = arista.caraDer().circuncentro();

						ux = arista.des().x() - arista.ori().x();
						uy = arista.des().y() - arista.ori().y();

						circuncentro_a_la_derecha =

								GC.area2(arista.ori().x(), arista.ori().y(), arista.des().x(), arista.des().y(), cc[0],
										cc[1]) > 0;

						// uyorto = circuncentro_a_la_derecha ? ux : -ux ;
						// uxorto = circuncentro_a_la_derecha ? -uy : uy ;

						uyorto = -ux;
						uxorto = uy;

						segmentoNoAcotado(cc, uxorto, uyorto, circuncentro_a_la_derecha);

					}

				}
			}
		}

	}

	private int factor;

	private void segmentoNoAcotado(long p[], long ux, long uy, boolean derecha) {

		if (!Util.dentroMarco(coord.x(p[0]), coord.y(p[1])))
			return;

		for (factor = 0; Util.dentroMarco(coord.x(p[0] + factor * ux), coord.y(p[1] + factor * uy)); factor++)
			;

		g.drawLine(coord.x(p[0]), coord.y(p[1]), coord.x(p[0] + factor * ux), coord.y(p[1] + factor * uy));

		System.out.println("[ " + coord.x(p[0]) + " " + coord.y(p[1]) + " " + coord.x(p[0] + factor * ux) + " "
				+ coord.y(p[1] + factor * uy)

		);

	}

	private void dibujarDelaunay(Graphics g, ICoord coord) {

		/* marcar caras visibles desde p */

		ListIterator iterador = caras.listIterator();

		g.setColor(new Color(247, 230, 178));

		int i, j;

		Triangulo3d t;

		NodoArista arista;

		Vertice3d[] a = new Vertice3d[3];

		while (iterador.hasNext()) {

			t = (Triangulo3d) iterador.next();

			if (t.nz() < 0) {

				a = t.toArray();

				for (i = 0; i < 3; i++) {

					j = (i + 1) % 3;

					g.drawLine(coord.x(a[i].x()), coord.y(a[i].y()), coord.x(a[j].x()), coord.y(a[j].y()));

				}
			}
		}

	}

} // fin clase DCEL
