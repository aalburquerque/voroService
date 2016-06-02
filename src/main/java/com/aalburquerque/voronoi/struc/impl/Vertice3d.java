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
   *  	Paquete voronoi.calculo.estructura
   *  	
   */
import java.util.ListIterator;

import com.aalburquerque.voronoi.util.EstadoColor;
import com.aalburquerque.voronoi.util.GC;

/**
 * La clase Vertice3d sirve para representar un vertice de un poliedro
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Vertice3d extends Punto3d implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Estado corriente de un vertice
	 * 
	 */

	public static final long NADA = 0;

	/**
	 * Estado en situacion de borrable de un vertice
	 * 
	 */

	public static final long BORRABLE = 1;

	/**
	 * Incide en una arista del nuevo cierre
	 * 
	 */

	public static final long NOBORRABLE = 2;

	/* ------------------------------------------------------------------- */
	/* A T R I B U T O S */
	/* ------------------------------------------------------------------- */

	/**
	 * Una de las aristas de la cual este vertice es extremo
	 * 
	 */

	private NodoArista arista;

	/**
	 * Para indicar el estado de este vertice
	 * 
	 */

	private long estado; // para incremental

	/**
	 * El valor es distinto de null cuando ya se ha insertado una arista durante
	 * el metodo incremental con extremos el nuevo vertice y este
	 */

	private NodoArista aristaDup;

	/**
	 * El valor es distinto de null cuando ya se ha insertado una arista durante
	 * el metodo incremental para apuntar las aristas de la base incidentes en
	 * este vertice
	 */

	private NodoArista[] aristasBase = new NodoArista[2];
	private int totalAristasBorde;

	private ListaDE aristasProyectables;

	private EstadoColor estadoColor; // rojo, purpura, azul. Para divide y
										// venceras

	/* ------------------------------------------------------------------- */
	/* C O N S T R U C T O R E S */
	/* ------------------------------------------------------------------- */

	/**
	 * para construir de un vertice por sus tres coordenadas
	 * 
	 * @param x
	 *            abscisa
	 * @param y
	 *            ordenada
	 * @param z
	 *            altura
	 */
	public Vertice3d(long x, long y, long z) {

		super(x, y, z);
		arista = null;

		estadoColor = new EstadoColor();

		resetAristaDup();
		resetAristasBorde();
		resetAristasProyectables();

	}

	/**
	 * para construir de un vertice
	 * 
	 * @param p
	 *            Punto que constituye este vertice
	 */

	public Vertice3d(Punto3d p) {

		super(p);
		arista = null;

		estadoColor = new EstadoColor();

		resetAristaDup();
		resetAristasBorde();
		resetAristasProyectables();

	}

	/* ------------------------------------------------------------------- */
	/* M E T O D O S */
	/* ------------------------------------------------------------------- */

	/**
	 * para asignar una arista asociada a este vertice con extremos el nuevo
	 * punto y este en el metodo incremental
	 * 
	 * @param arista
	 *            Arista a la que se asocia el vertice
	 */

	public void asignaArista(NodoArista arista) {

		this.arista = arista;
	}

	/**
	 * para asignar una arista asociada a este vertice y que pertenece a la base
	 * del cono en el proceso incremental
	 * 
	 * @param arista
	 *            Arista a la que se asocia el vertice
	 */

	public void asignaAristaBorde(NodoArista arista) {

		if (totalAristasBorde == 2)

			throw new RuntimeException("Demasiadas aristas. \n La base del cono no podria ser un ciclo");

		aristasBase[totalAristasBorde] = arista;

		totalAristasBorde++;

	}

	public void asignaAristaProyeccion(NodoArista arista) {

		aristasProyectables.insertarInicio(arista);

	}

	public void reset() {

		setEstado(Vertice3d.NADA);
		resetAristasBorde();
		resetAristaDup();
		resetAristasProyectables();
		ponColor(EstadoColor.AZUL);
	}

	public void resetAristasBorde() {

		totalAristasBorde = 0;

		// estoy podria sobrar pero sirve para sacar mas inteligible archivo de
		// debug

		aristasBase[0] = null;
		aristasBase[1] = null;

	}

	public void resetAristasProyectables() {

		aristasProyectables = new ListaDE();

	}

	public void resetAristaDup() {

		// arista para no duplicar al dar una vuelta a la base del cono

		aristaDup = null;

	}

	public boolean igual(Vertice3d v) {
		return ((v.x() == x) && (v.y() == y) && (v.z() == z));
	}

	/**
	 * Para conocer la siguiente arista en la marcha por la base del cono en el
	 * proceso incremental
	 * 
	 * @param e
	 *            Arista actual en la marcha
	 * @return La siguiente arista
	 */

	public NodoArista obtenerDistintaBaseCono(NodoArista e) {

		if (totalAristasBorde != 2)
			throw new RuntimeException("Fuera de rango aristas base del cono");

		return (e != aristasBase[0] ? aristasBase[0] : aristasBase[1]);

	}

	public NodoArista siguienteProyectable(Vertice3d[] orientada) {

		ListIterator i = aristasProyectables.listIterator();
		NodoArista una, resultado = null;

		// escoger si es posible un borde seguro antes q otro tipo

		Vertice3d nocomun;

		double valor;

		double k;

		while (i.hasNext()) {

			una = (NodoArista) i.next();

			// aqui falta parchear para q no vaya para atras cuando hay 4
			// posibles

			nocomun = una.ori().igual(orientada[1]) ? una.des() : una.ori();

			valor = GC.area2(orientada[0].x(), orientada[0].y(), orientada[1].x(), orientada[1].y(), nocomun.x(),
					nocomun.y());

			if (valor > 0) {

				resultado = una;

				if (una.getEstado() == NodoArista.BORDESEGURO)
					break;

			}

			if (valor == 0) {

				k = orientada[1].x() - orientada[0].x() != 0 ?

						(nocomun.x() - orientada[0].x()) / (orientada[1].x() - orientada[0].x()) :

						orientada[1].y() - orientada[0].y() != 0 ?

								(nocomun.y() - orientada[0].y()) / (orientada[1].y() - orientada[0].y()) :

								0;

				// // System.out.print(" k :"+k);

				if (k >= 1) {

					resultado = una;

					if (una.getEstado() == NodoArista.BORDESEGURO)
						break;
				}

			}

		}

		return resultado;

	}

	public NodoArista obtenerDistintaProyectable(NodoArista e) {

		ListIterator i = aristasProyectables.listIterator();
		NodoArista una, resultado = null;

		// escoger si es posible un borde seguro antes q otro tipo

		while (i.hasNext()) {

			una = (NodoArista) i.next();

			// aqui falta parchear para q no vaya para atras cuando hay 4
			// posibles

			if (!una.igual(e)) {

				resultado = una;

				if (una.getEstado() == NodoArista.BORDESEGURO)
					break;

			}
		}

		return resultado;

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * para obtener la arista asociada a este vertice
	 * 
	 * @return arista asociada
	 */

	public NodoArista arista() {
		return arista;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Retorno de una cadena con la informacion de la arista asociada
	 * 
	 * @return Cadena con la arista asociada
	 */

	public String aristaString() {

		return arista == null ? " null " : arista.verticesString();
	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a las aristas
	 * proyectables
	 */

	public String aristasProyectablesString() {

		return aristasProyectables.toString();
	}

	/**
	 * Devuelve una cadena con la informacion correspondiente a las dos aristas
	 * asociadas a un recorrido por la base del cono de visibilidad cuando este
	 * punto forma parte de la misma. Las aristas son asociadas a este punto
	 * cuando forman parte del recorrido y tienen como un extremo este punto.
	 */

	public String aristasBaseString() {

		String s1 = aristasBase[0] == null ? " null " : aristasBase[0].verticesString();
		String s2 = aristasBase[1] == null ? " null " : aristasBase[1].verticesString();

		return "aristaBase[0] = " + s1 + "aristaBase[1] = " + s2 + " " + new Long(totalAristasBorde).toString();
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * para conocer cual es el estado de este vertice
	 * 
	 * @return el estado de este vertice
	 */

	public long getEstado() {
		return estado;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * para establecer el estado de este vertice
	 * 
	 * @param est
	 *            Nuevo estado del vertice
	 */

	public void setEstado(long est) {
		estado = est;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * para conocer cual es la arista que podria duplicarse en el metodo
	 * incremental
	 * 
	 * @return Arista que se podria duplicar
	 */

	public NodoArista getAristaDup() {
		return aristaDup;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * para establecer cual es la arista que podria duplicarse en el metodo
	 * incremental
	 * 
	 * @param e
	 *            Arista que se podria duplicar
	 */

	public void setAristaDup(NodoArista e) {
		aristaDup = e;
	}

	public Vertice3d restar(Vertice3d w) {

		return new Vertice3d(w.x() - this.x(), w.y() - this.y(), w.z() - this.z());

	}

	public Vertice3d sumar(Vertice3d w) {

		return new Vertice3d(w.x() + this.x(), w.y() + this.y(), w.z() + this.z());

	}

	/**
	 * Funcion que devuelve el color de este elemento
	 */

	public int dameColor() {
		return estadoColor.dameColor();
	}

	/**
	 * Metodo para establecer el color de este elemento
	 */

	public void ponColor(int uncolor) {
		estadoColor.ponColor(uncolor);
	}

	/**
	 * Devuelve una cadena con el color de este elemento
	 */

	public String colorString() {
		return estadoColor.colorString();
	}

}