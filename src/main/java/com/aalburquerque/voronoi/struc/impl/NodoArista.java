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

import com.aalburquerque.voronoi.util.*;

/**
 * La clase NodoArista representa una arista que formara parte de la lista de
 * aristas de la clase DCEL. Esta estructura (Double Connected Edge List) esta
 * orientada a arista. De este modo una arista contiene informacion adicional
 * para representar la informacion necesaria para definir a una DCEL. Por este
 * motivo, un NodoArista ademas de contener la informacion de que vertices
 * constituyen la arista contienen otra informacion no tan evidente, tales como
 * las caras que deja la arista a ambos lados y las arista siguientes girando en
 * orden positivo desde los vertices origen y destino de la arista.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 0.1
 */

public class NodoArista implements Serializable {
	private static final long serialVersionUID = 1L;
	/* ------------------------------------------------------------------- */
	/* C O N S T A N T E S */
	/* ----------------------------------------------------------------- */

	/**
	 * Constante para calificar una arista
	 */

	public static final int NADA = 0;

	/**
	 * Constante para calificar una arista
	 */

	public static final int BORRABLE = 1;
	/**
	 * Constante para calificar una arista
	 */

	public static final int BORDE = 2;

	/**
	 * Constante para calificar una arista
	 */
	public static final int BORDESEGURO = 3;

	/**
	 * Constante para calificar una arista
	 */
	public static final int BORDEPOSIBLE = 4;

	/* ------------------------------------------------------------------- */
	/* A T R I B U T O S */
	/* ------------------------------------------------------------------- */

	// v0: vertice origen
	// v1: vertice destino

	private Vertice3d v0;
	private Vertice3d v1;

	// f0: cara a la izquierda
	// f1: cara a la derecha

	private Triangulo3d f0;
	private Triangulo3d f1;

	// para cambios de cara en la mezcla de preparata-hong

	private Triangulo3d siguientef0;
	private Triangulo3d siguientef1;

	private NodoArista siguientee0;
	private NodoArista siguientee1;

	// e0: arista CCW desde v0
	// e1: arista CCW desde v1

	private NodoArista e0;
	private NodoArista e1;

	private int estado = NADA;

	private EstadoColor estadoColor; // rojo, purpura, azul. Para divide y
										// venceras

	private boolean orientacionOK; // para la mezcla

	/**
	 * Construir una nueva arista que tenga los vertices pasados como parametro
	 * como extremos y las caras pasadas como parametro como caras a ambos lados
	 *
	 * @param v0
	 *            Vertice origen
	 * @param v1
	 *            Vertice origen
	 * @param f0
	 *            Cara a la izquierda
	 * @param f1
	 *            Cara a la derecha
	 */

	public NodoArista(Vertice3d v0, Vertice3d v1, Triangulo3d f0, Triangulo3d f1) {

		this.v0 = v0;
		this.v1 = v1;
		this.f0 = f0;
		this.f1 = f1;
		this.e0 = this.e1 = null;

		estadoColor = new EstadoColor();

		resetSigCaras();
		resetSigCCW();

	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Construir una nueva arista que tenga los vertices pasados como parametro
	 * como extremos
	 *
	 * @param v0
	 *            Vertice origen
	 * @param v1
	 *            Vertice origen
	 */

	public NodoArista(Vertice3d v0, Vertice3d v1) {

		this.v0 = v0;
		this.v1 = v1;
		this.e0 = this.e1 = null;
		this.f1 = this.f0 = null;

		estadoColor = new EstadoColor();

		resetSigCaras();
		resetSigCCW();
	}

	/**
	 * Para establecer nuevos valores en el atributo de las siguientes Si alguno
	 * de los dos parametro es null entonces no se establece un nuevo valor para
	 * la arista en relacion con dicho parametro y permanece el valor antes de
	 * la llamada a este metodo
	 * 
	 * @param e0
	 *            arista siguiente en orden positivo comenzando en el vertice de
	 *            origen de esta arista
	 * @param e1
	 *            arista siguiente en orden positivo comenzando en el vertice de
	 *            destino de esta arista
	 */

	public void asignaCCW(NodoArista e0, NodoArista e1) {

		if (e0 != null)
			this.e0 = e0;
		if (e1 != null)
			this.e1 = e1;

	}

	/**
	 * Para establecer nuevos valores en el atributo de las caras de esta
	 * arista. Si alguno de los dos parametro es null entonces no se establece
	 * un nuevo valor para la arista en relacion con dicho parametro y permanece
	 * el valor antes de la llamada a este metodo
	 * 
	 * @param f0
	 *            cara izquierda
	 * @param f1
	 *            cara derecha
	 */

	public void asignaCaras(Triangulo3d f0, Triangulo3d f1) {

		if (f0 != null)
			this.f0 = f0;
		if (f1 != null)
			this.f1 = f1;

	}

	/**
	 * Para realizar cambios para la mezcla de "divide y venceras"
	 */

	public void asignaSigCaras(Triangulo3d sigf0, Triangulo3d sigf1) {

		if (sigf0 != null)
			this.siguientef0 = sigf0;
		if (sigf1 != null)
			this.siguientef1 = sigf1;

	}

	/**
	 * Para realizar cambios para la mezcla de "divide y venceras"
	 */

	public void resetSigCaras() {

		asignaSigCaras(null, null);
	}

	/**
	 * Para realizar cambios para la mezcla de "divide y venceras"
	 */

	public void actualizaSigCaras() {

		if (this.siguientef0 != null)
			this.f0 = this.siguientef0;
		if (this.siguientef1 != null)
			this.f1 = this.siguientef1;

	}

	/**
	 * Para realizar cambios para la mezcla de "divide y venceras"
	 */

	public void asignaSigCCW(NodoArista sige0, NodoArista sige1) {

		if (sige0 != null)
			this.siguientee0 = sige0;
		if (sige1 != null)
			this.siguientee1 = sige1;

	}

	/**
	 * Para preparar un cambio para la mezcla de "divide y venceras"
	 */

	public void resetSigCCW() {

		asignaSigCCW(null, null);
	}

	/**
	 * Para realizar cambios para la mezcla de "divide y venceras"
	 */

	public void actualizaSigCCW() {

		if (this.siguientee0 != null)
			this.e0 = this.siguientee0;
		if (this.siguientee1 != null)
			this.e1 = this.siguientee1;
		resetSigCCW();

	}

	/**
	 * Devuelve el vertice origen de esta arista
	 */

	public Vertice3d ori() {
		return v0;
	}

	/**
	 * Devuelve el vertice destino de esta arista
	 */

	public Vertice3d des() {
		return v1;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Devuelve la arista siguiente en orden positivo que tiene tiene un vertice
	 * en comun con esta arista: el vertice origen de esta arista
	 */

	public NodoArista aristaOri() {
		return e0;
	}

	/**
	 * Devuelve la arista siguiente en orden positivo que tiene tiene un vertice
	 * en comun con esta arista: el vertice destino de esta arista
	 */

	public NodoArista aristaDes() {
		return e1;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Devuelve la cara que esta a la izquierda de esta arista
	 */

	public Triangulo3d caraIzq() {
		return f0;
	}

	/**
	 * Devuelve la cara que esta a la derecha de esta arista
	 */

	public Triangulo3d caraDer() {
		return f1;
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Devuelve cierto si esta arista tiene los mismo vertices como extremo que
	 * le vertice pasado como parametro
	 * 
	 * @param n
	 *            La arista que se quiere comparar con esta arista
	 */

	public boolean igual(NodoArista n) {

		if (n == null)
			return false;

		return n.ori().igual(this.ori()) && n.des().igual(this.des())
				|| n.ori().igual(this.des()) && n.des().igual(this.ori());
	}

	/**
	 * Devuelve cierto si esta arista cotiene exactamente toda la informacion
	 * identica a la arista que es pasada como argumento
	 * 
	 * @param n
	 *            La arista que se quiere comparar con esta arista
	 */

	public boolean igualEstricto(NodoArista n) {

		return

		this.igual(n) && (this.aristaOri().igual(n.aristaOri()) || this.aristaOri().igual(n.aristaDes()))
				&& (this.aristaDes().igual(n.aristaOri()) || this.aristaDes().igual(n.aristaDes()))
				&& (this.caraIzq().igual(n.caraIzq()) || this.caraIzq().igual(n.caraDer()))
				&& (this.caraDer().igual(n.caraIzq()) || this.caraDer().igual(n.caraDer()));

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String verticesString() {
		return " [ v0: " + v0.toString() + " " + " v1: " + v1.toString() + " ] estado: " + estadoString();
	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String carasString() {

		String s0 = f0 == null ? "f0: null " : " f0: " + f0.toString() + " ";
		String s1 = f1 == null ? "f1: null " : " f1: " + f1.toString() + " ";
		return " [ " + s0 + s1 + " ] ";

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String carasString2() {

		String s0 = siguientef0 == null ? "f0: null " : " f0: " + siguientef0.toString() + " ";
		String s1 = siguientef1 == null ? "f1: null " : " f1: " + siguientef1.toString() + " ";
		return " [ " + s0 + s1 + " ] ";

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String aristasString() {

		String s0 = e0 == null ? "e0: null " : " e0: " + e0.verticesString() + " ";
		String s1 = e1 == null ? "e1: null " : " e1: " + e1.verticesString() + " ";
		return " [ " + s0 + s1 + " ] ";

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String aristasString2() {

		String s0 = siguientee0 == null ? "e0: null " : " e0: " + siguientee0.verticesString() + " ";
		String s1 = siguientee1 == null ? "e1: null " : " e1: " + siguientee1.verticesString() + " ";
		return " [ " + s0 + s1 + " ] ";

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String estadoString() {
		return new Integer(estado).toString();
	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String toString() {
		return verticesString();

	}

	/**
	 * Devuelve una cadena con informacion sobre la arista
	 */

	public String todoString() {
		return verticesString() + "\n" + carasString() + "\n" + aristasString();

	}

	/**
	 * Para obtener el estado de la arista en un momento dado.
	 */

	public int getEstado() {
		return estado;
	}

	/**
	 * Para establecer cual es el estado de la arista en un momento dado.
	 * 
	 * @param est
	 *            El nuevo estado de la arista
	 */

	public void setEstado(int est) {
		estado = est;
	}

	/**
	 * Para obtener el vertice que comparte la arista e con esta arista
	 * 
	 * @param e
	 *            La arista que tiene un vertice en comun con esta arista
	 */

	public Vertice3d comunVertice(NodoArista e) {

		if ((e.ori().igual(this.ori())) || (e.ori().igual(this.des())))
			return e.ori();
		if ((e.des().igual(this.ori())) || (e.des().igual(this.des())))
			return e.des();

		throw new RuntimeException("Aristas sin punto en comun");
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

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

	/**
	 * Funcion para conocer una orientacion dada de una arista
	 */

	public boolean dameOrientacionOk() {
		return orientacionOK;
	}

	/**
	 * Funcion para establefcer una orientacion dada de una arista
	 */

	public void ponOrientacionOk(boolean orientacionOK) {
		this.orientacionOK = orientacionOK;
	}

	/**
	 * Metodo para establecer un estado inicial en una arista
	 */

	public void reset() {

		setEstado(NodoArista.NADA);
		ponColor(EstadoColor.AZUL);
	}

}
