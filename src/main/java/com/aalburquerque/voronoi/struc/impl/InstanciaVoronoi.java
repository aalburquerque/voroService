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

import com.aalburquerque.voronoi.algorithms.DivideYvenceras;
import com.aalburquerque.voronoi.util.Input;
import com.aalburquerque.voronoi.util.Util;

/**
 * El objeto InstanciaVoronoi sirve para calcular una instancia de problema de
 * manera local. Este objeto dispone de diversos constructores dependiendo del
 * modo en que se quiera definir la entrada del problema, es decir, el conjunto
 * de puntos de los cuales se quiere calcular el correspondiente diagrama de
 * Voronoi
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class InstanciaVoronoi implements Serializable {

	private static final long serialVersionUID = 1L;

	private Nube2D nubeSitios;
	private Poligono cierrePlano = null;
	private Poliedro poliedro;
	private long[] t = new long[2]; // tiempo de calculo en ms.
	private Punto3d[] sitiosElevados;
	private Object[] subproblemas;
	private int totalpuntos;
	private String metodo;
	private static int tambase = DivideYvenceras.COTA;

	/**
	 * Este constructor sirve para crear una instancia de problema cuyo conjunto
	 * de puntos se genere aleatoriamente en un rango de abscisas de (0,xmax) y
	 * en un rango de ordenadas (0,ymax). El conjunto aleatorio de puntos tendra
	 * un total de puntos igual a la cantidad pasada como primer parametro del
	 * constructor. Ademas se elige medio del ultimo parametro del constructor
	 * la opcion de que los puntos elevados al espacio sobre el paraboloide
	 * unidad esten o no en posicion general, es decir, que no haya cuatro
	 * coplanares.
	 * 
	 * @param totalpuntos
	 *            Numero total de puntos que se quieren generar al azar
	 * @param xmax
	 *            Maximo valor como abscisa de los puntos aleatorios
	 * @param ymax
	 *            Maximo valor como ordenada de los puntos aleatorios
	 * @param esModoGeneral
	 *            Valor booleano. Si es cierto entonces los puntos estan en
	 *            posicion general cuando se proyectan sobre el paraboloide
	 *            unidad en el espacio
	 */

	public InstanciaVoronoi(int totalpuntos, long xmax, long ymax, boolean esModoGeneral, int tambase) {

		this(totalpuntos, 0, 0, xmax, ymax, esModoGeneral, tambase);
	}

	/**
	 * Constructor para crear una instancia de problema cuyo conjunto de puntos
	 * se genere aleatoriamente en un rango de abscisas de (xmin,xmax) y en un
	 * rango de ordenadas (ymin,ymax). El conjunto aleatorio de puntos tendra un
	 * total de puntos igual a la cantidad pasada como primer parametro del
	 * constructor. Ademas se elige medio del ultimo parametro del constructor
	 * la opcion de que los puntos elevados al espacio sobre el paraboloide
	 * unidad esten o no en posicion general, es decir, que no haya cuatro
	 * coplanares.
	 * 
	 * @param totalpuntos
	 *            Numero total de puntos que se quieren generar al azar
	 * @param xmin
	 *            Minimo valor como abscisa de los puntos aleatorios
	 * @param ymin
	 *            Minimo valor como ordenada de los puntos aleatorios
	 * @param xmax
	 *            Maximo valor como abscisa de los puntos aleatorios
	 * @param ymax
	 *            Maximo valor como ordenada de los puntos aleatorios
	 * @param esModoGeneral
	 *            Valor booleano. Si es cierto entonces los puntos estan en
	 *            posicion general cuando se proyectan sobre el paraboloide
	 *            unidad en el espacio
	 */

	public InstanciaVoronoi(int totalpuntos, long xmin, long ymin, long xmax, long ymax, boolean esModoGeneral,
			int tambase) {

		this.totalpuntos = totalpuntos;
		// Punto2D[] sitios = Input.sitiosGeneralPlano(totalpuntos, xmin, ymin,
		// xmax, ymax);

		if (esModoGeneral)

			sitiosElevados = Input.generalVoronoi(totalpuntos, xmax, ymax);

		else

			sitiosElevados = Input.libreVoronoi(totalpuntos, xmax, ymax);

		nubeSitios = new Nube2D(sitiosElevados);

		this.tambase = tambase;

		// sitiosElevados = null;

	}

	/**
	 * Constructor para crear una instancia de problema cuyo conjunto de puntos
	 * queda especificado por la nube de puntos pasada como parametro
	 *
	 * @param nube
	 *            La nube que define el conjunto de puntos que representa a los
	 *            sitios del diagrama de Voronoi.
	 */

	public InstanciaVoronoi(Nube2D nube, int tambase) {

		nubeSitios = nube;
		sitiosElevados = null;
		this.totalpuntos = nubeSitios.get_total();

		this.tambase = tambase;

	}

	/**
	 * Constructor para crear una instancia ya calculada
	 */

	public InstanciaVoronoi(Nube2D nube, Poliedro poliedro) {

		nubeSitios = nube;
		this.poliedro = poliedro;
		sitiosElevados = null;
		this.totalpuntos = 0;
	}

	/**
	 * Constructor para crear una instancia de problema vacia
	 */

	public InstanciaVoronoi() {

		nubeSitios = new Nube2D();
		sitiosElevados = null;
		this.totalpuntos = 0;
	}

	/**
	 * Metodo para a�adir un punto mas a la instancia de problema
	 */

	public void unoMas(long x, long y) {
		nubeSitios.unoMas(x, y);
		totalpuntos++;
	}

	/**
	 * Procedimiento usado para cacular localmente el diagrama de Voronoi
	 * correspondiente a esta instancia
	 * 
	 */

	public void calculaVoronoi() {

		nubeSitios.ordenarXY();
		ampliarDimension(); // elevar los sitios del plano al espacio

		t[0] = System.currentTimeMillis();
		try {

			poliedro = new Poliedro(sitiosElevados, tambase);

			t[1] = System.currentTimeMillis();

			metodo = poliedro.metodoString(); // para saber con que metodo se
												// encontro la solucion
												// finalmente

		} catch (Exception ex) {

			// construye el cierre de los puntos por divide y venceras

			Util.msgLog("LOGEXCEPTION",
					"Excepcion en clase InstanciaVoronoi: NO SE CONSIGUIO OBTENER "
							+ "UNA SOLUCION MEDIANTE PROCESO DIVIDEYVENCERAS Y SE PROCEDE A CALCULAR "
							+ "LA SOLUCION MEDIANTE PROCESO INCREMENTAL\n");

			poliedro = new Poliedro(sitiosElevados, DCEL.INCREMENTAL, tambase);

			metodo = "Incremental";

			t[1] = System.currentTimeMillis();

		}

	}

	/**
	 * Cadena que informa sobre el tiempo de calculo realizado localmente
	 * mediante calculaVoronoi()
	 */

	public String tiempoString() {

		return " milisegundos: " + new Long(t[1] - t[0]).toString();
	}

	/**
	 * Cadena que informa sobre el metodo de calculo realizado localmente
	 * mediante calculaVoronoi()
	 */

	public String metodoString() {

		return metodo;
	}

	public String informacionString() {

		return "Tiempo de calculo: " + tiempoString() + " Metodo utilizado: " + metodoString();
	}

	/**
	 * Devuelve un objeto Nube2D con la nube de sitios con el conjunto de puntos
	 * de esta instancia de problema
	 */

	public Nube2D nube() {
		return nubeSitios;
	}

	/**
	 * Devuelve un objeto Poligono con el cierre convexo de la nube de sitios de
	 * esta instancia de problema
	 */

	public Poligono poligono() {

		// si no se ha calculado ya el cierre, calcularlo

		if (cierrePlano == null && nubeSitios != null) {
			cierrePlano = nubeSitios.scan();
		}

		// si ya se habia calculado el cierre, devolverlo. Si no hay nube
		// devolvera null

		return cierrePlano;
	}

	/**
	 * Devuelve un objeto Poliedro con el poliedro asociado a esta instancia de
	 * problema resuelta
	 */

	public Poliedro poliedro() {
		return poliedro;
	}

	/**
	 * Devuelve un array de objetos Punto3d que son el conjunto de los puntos de
	 * esta instancia de problema
	 */

	public Punto3d[] sitiosElevados() {
		return sitiosElevados;
	}

	/**
	 * Devuelve un array de objetos Coord2d que son el conjunto de los puntos de
	 * esta instancia de problema
	 */

	public Coord2d[] sitiosPlano() {

		if (sitiosElevados == null)
			return null;

		Coord2d[] problemaPlano = Util.proyectarElevados(sitiosElevados);

		return problemaPlano;
	}

	/**
	 * Metodo para devolver un subconjunto de puntos de la instancia. Si
	 * numeramos el conjunto de puntos del 0..n-1 entonces devuelve un array con
	 * el intervalor (iz,de) ambos incluidos con iz y de pertenecientes a
	 * [0..n-1]
	 * 
	 * @param iz
	 *            indice inferior del intervalo de puntos que se va devolver
	 * @param de
	 *            indice superior del intervalo de puntos que se va devolver
	 */

	public Punto3d[] subproblema(int iz, int de) {

		int totalSub = de - iz + 1;
		Punto3d[] parte = new Punto3d[totalSub];
		for (int i = 0; i < totalSub; i++)
			parte[i] = sitiosElevados[iz + i];
		return parte;

	}

	/**
	 * Metodo que devuelve el total de puntos de esta instancia de problema
	 */

	public int totalPuntos() {
		return totalpuntos;
	}

	/**
	 * Metodo para ordenar el conjunto de puntos del plano que son la entrada de
	 * la instacia. Es necesario hacer este proceso antes del calculo del
	 * poliedro.
	 */

	public void ordenarPuntos() {
		nubeSitios.ordenarXY();
	}

	/**
	 * Metodo para proyectar el conjunto de puntos del plano que son la entrada
	 * de la instacia sobre el paraboloide unidad. Es necesario hacer este
	 * proceso antes del calculo del poliedro.
	 */

	public void ampliarDimension() {
		// elevar los puntos de la nube
		if (sitiosElevados == null)
			sitiosElevados = nubeSitios.virgenMaria();
	}
}