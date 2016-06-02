
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ListIterator;

import com.aalburquerque.voronoi.algorithms.DivideYvenceras;
import com.aalburquerque.voronoi.algorithms.Incremental;
import com.aalburquerque.voronoi.exception.SinGanadoraException;
import com.aalburquerque.voronoi.struc.ListAdaptor;
import com.aalburquerque.voronoi.util.EstadoColor;
import com.aalburquerque.voronoi.util.GC;

/**
 * <p>
 * Esta clase implementa una DCEL que son las siglas de Double Connected Edge
 * List. Una estructura orientada a la arista que sirve por ejemplo para
 * representar un poliedro. <br>
 * Esta estructura se presenta en <center>
 * <p>
 * F. Preparata, M. I. Shamos: "Computational Geometry: An Introduction".
 * Springer, 1985 </center>
 * <p>
 * Uno de los aspectos mas interesantes de esta clase es que ofrece la
 * posibilidad de usar una serie de iteradores para recorrer el poliedro.
 * 
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 * @see Poliedro
 * @see ListaDE
 */

public class DCEL implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * El conjunto de aristas del objeto DCEL
	 */

	protected Aristas aristas;

	/**
	 * El conjunto de vertices del objeto DCEL
	 */

	protected Vertices vertices;

	/**
	 * El conjunto de caras del objeto DCEL
	 */

	protected Caras caras;

	private static Incremental procesoIncremental;

	private static DivideYvenceras procesoDivideyvenceras;

	/**
	 * Constante para hacer referencia al proceso incremental de construccion
	 * del cierre convexo
	 */

	public static final boolean INCREMENTAL = true;

	/**
	 * Constante para hacer referencia al proceso por divide y venceras de
	 * construccion del cierre convexo
	 */

	public static final boolean DIVIDE_Y_VENVERAS = false;

	private boolean esIncremental; // para saber por cual metodo se hizo

	/**
	 * Constructor copia
	 */

	public DCEL(DCEL A) {

		this.aristas = A.aristas;
		this.vertices = A.vertices;
		this.caras = A.caras;

	}

	/**
	 * Para construir un poliedro a partir de un array de puntos
	 */

	public DCEL(Punto3d[] puntos, int cota) {

		this(puntos, puntos.length < cota, cota);

	}

	/**
	 * Para construir un poliedro a partir de un array de puntos y forzando que
	 * el proceso de construccion sea el metodo incremental
	 */

	public DCEL(Punto3d[] puntos, boolean porincremental, int cota) {

		if (puntos.length < 4)
			throw new RuntimeException("No se admiten menos de 4 puntos");

		esIncremental = porincremental;

		aristas = new Aristas(this);
		vertices = new Vertices();
		caras = new Caras();

		// construye la DCEL por el metodo correspondiente

		if (esIncremental) {

			procesoIncremental = new Incremental(this, puntos);

		} else {

			procesoDivideyvenceras = new DivideYvenceras(this, puntos, cota);
		}

	}

	/**
	 * Para construir un poliedro a partir de dos poliedros previamente
	 * construidos. La forma en la cual se van a construir el poliedro es a
	 * traves de un proceso de mezcla similiar al usado en el metodo de
	 * construccion de un poliedro por un algoritmo de "Divide y venceras"
	 */

	public DCEL(DCEL A, DCEL B) {

		esIncremental = false;

		aristas = new Aristas(this);
		vertices = new Vertices();
		caras = new Caras();

		procesoDivideyvenceras = new DivideYvenceras(this, A, B);

	}

	/**
	 * Devuelve un cadena que informa sobre el metodo usado para la construccion
	 * del poliedro. Las alternativas son que devuelve " Incremental " o
	 * " Divide y venceras "
	 */

	public String metodoString() {
		return esIncremental ? " Incremental " : " Divide y venceras";
	}

	/**
	 * Devuelve un entero que representa al numero total de vertices del
	 * poliedro
	 */

	public int totalVertices() {
		return vertices.get_total();
	}

	/**
	 * Devuelve un entero que representa al numero total de aristas del poliedro
	 */

	public int totalAristas() {
		return aristas.get_total();
	}

	/**
	 * Devuelve un entero que representa al numero total de caras del poliedro
	 */

	public int totalCaras() {
		return caras.get_total();
	}

	/**
	 * Para introducir un nuevo vertice en el conjunto de vertices de la DCEL
	 */

	public void insertar(Vertice3d v) {

		vertices.insertarFinal(v);
	}

	/**
	 * Para introducir una nueva arista en el conjunto de aristas de la DCEL
	 */

	public void insertar(NodoArista n) {

		aristas.insertarFinal(n);
	}

	/**
	 * Para introducir una nueva cara en el conjunto de caras de la DCEL
	 */

	public void insertar(Triangulo3d t) {

		caras.insertarFinal(t);
	}

	/**
	 * Para poner en un estado inicial las caras de la DCEL
	 */

	public void resetCaras() {

		caras.reset();
	}

	/**
	 * Para eliminar las caras,aristas y vertices sobrantes de la DCEL en un
	 * paso iterativo del proceso incremental. Es importante destacar que solo
	 * es llamado por el proceso incremental
	 */

	public void limpiar() {

		caras.limpiar();

		aristas.limpiar();

		vertices.limpiar(aristas);
	}

	/**
	 * Este metodo sirve para recorrer todas las caras clasificandolas segun
	 * sean vistas por el exterior por el punto p. Ademas se devuelve un valor
	 * de falso si no hay ninguna cara que sea visible por el exterior por p, lo
	 * que significa que el punto esta en el interior del poliedro.
	 * 
	 * @param p
	 *            Punto por el cual son sometidos a test de visibilidad cada una
	 *            de las caras de la DCEL
	 */

	public boolean testVisibilidadCaras(Punto3d p) {

		caras.reset();
		boolean hayAlgunaVisible = caras.testVisible(p);
		return hayAlgunaVisible;
	}

	/**
	 * Este metodo sirve para recorrer todas las aristas clasificandolas segun
	 * sean parte de un recorrido por la base del cono de visibilidad del
	 * poliedro por el punto del paso iterativo del proceso incremental, o bien
	 * sean arista que haya que descartar por estar en la parte del poliedro
	 * visible y que tras el paso iterativo haya que descartar. Es
	 * imprescindible que previamente se haya llamado a testVisibilidadCaras
	 */

	public NodoArista testVisibilidadAristas() {

		vertices.reset();
		aristas.reset();
		NodoArista unaDelBorde = aristas.testVisible();
		return unaDelBorde;

	}

	/**
	 * Devuelve un cadena que informa sobre toda la informacion contenida en la
	 * DCEL
	 */

	public String toString() {
		return " Aristas : " + aristas.toString() + " Vertices: " + vertices.toString() + " Caras   : "
				+ caras.toString();
	}

	/**
	 * Metodo para reflejar toda la informacion de la DCEL en el archivo
	 * "DCEL.txt"
	 */

	public void aDisco() {
		aDisco("DCEL.txt", false);
	}

	/**
	 * Metodo para reflejar toda la informacion de la DCEL en el archivo de
	 * nombre el parametro pasado.
	 * 
	 * @param name
	 *            el nombre del archivo
	 */

	public void aDisco(String name) {
		aDisco(name, true);
	}

	private void aDisco(String name, boolean condetalle) {

		int i;

		try {

			FileWriter fw = new FileWriter((name + ".txt"));
			BufferedWriter salida = new BufferedWriter(fw);

			String s;
			ListIterator iter;

			salida.write("METODO: " + (esIncremental ? " Incremental " : " Divide y venceras "));
			salida.write("\nVERTICES ---------------------------------------------------------------------- ");

			salida.newLine();
			salida.newLine();
			salida.newLine();
			iter = vertices.listIterator();
			Vertice3d v;
			i = 0;
			while (iter.hasNext()) {

				v = (Vertice3d) iter.next();
				i++;
				salida.write(i + ".  ");
				salida.write(v.toString());
				salida.write(v.aristaString());
				salida.write(v.aristasBaseString());
				salida.write(v.colorString());
				salida.newLine();
			}
			salida.newLine();
			salida.newLine();
			salida.newLine();

			if (condetalle) {

				salida.write("detalle:");
				salida.newLine();
				salida.newLine();

				iter = vertices.listIterator();

				i = 0;
				while (iter.hasNext()) {

					v = (Vertice3d) iter.next();
					i++;
					salida.write(i + ".  ");
					salida.write(v.toString());
					salida.newLine();
					salida.write(v.aristasProyectablesString());
					salida.write(v.colorString());
					salida.newLine();
					salida.newLine();
				}
			}

			salida.newLine();
			salida.newLine();
			salida.write("CARAS    ----------------------------------------------------------------------");
			salida.newLine();
			salida.newLine();
			salida.newLine();
			salida.newLine();
			iter = caras.listIterator();
			Triangulo3d t;
			i = 0;
			while (iter.hasNext()) {

				i++;
				salida.write(i + ".  ");

				t = (Triangulo3d) iter.next();
				salida.write(t.toString());
				salida.write(t.colorString());
				// salida.write(t.aristaString()); esto de una excepcion, mirar
				// mas tarde por que
				salida.newLine();
			}

			salida.newLine();
			salida.newLine();
			salida.write("ARISTAS  ----------------------------------------------------------------------");
			salida.newLine();
			salida.newLine();
			salida.newLine();
			iter = aristas.listIterator();
			NodoArista n;
			i = 0;
			while (iter.hasNext()) {

				n = ((NodoArista) iter.next());

				i++;
				salida.write(i + ".  ");
				salida.write(n.verticesString());
				salida.write(n.estadoString());
				salida.write("\n" + n.colorString());
				salida.newLine();
			}
			salida.newLine();
			salida.newLine();

			if (condetalle) {

				salida.write("detallado de aristas");
				salida.newLine();
				salida.newLine();
				iter = aristas.listIterator();
				i = 0;
				while (iter.hasNext()) {

					n = ((NodoArista) iter.next());

					i++;
					salida.write(i + ".  ");
					salida.write(n.estadoString());
					salida.write(n.verticesString());
					salida.newLine();
					salida.write(n.carasString());
					salida.newLine();
					salida.write(n.aristasString());
					salida.write(n.colorString());
					salida.newLine();
					salida.newLine();
					salida.write("=================================================================================");
					salida.newLine();
					salida.newLine();
				}

			}

			salida.close();

		} catch (java.io.FileNotFoundException fnfex) {
			// System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException ioex) {
		}

	}

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R Recorrer las aristas en torno a un vertice */
	/* ------------------------------------------------------------------- */

	/**
	 * Iterador para recorrer las aristas adyacentes al vertice pasado en orden
	 * y sentido positivo.
	 * 
	 * @param vertice
	 *            El vertice que tiene como extremo todas las aristas que se
	 *            recorren
	 * @see ListAdaptor
	 */

	public ListAdaptor verticeIterator(Vertice3d vertice) {
		return new ItrVertice(vertice);
	}

	/**
	 * Iterador para recorrer las aristas adyacentes al vertice pasado en orden
	 * y sentido positivo, comenzando por la arista pasada.
	 * 
	 * @param vertice
	 *            El vertice que tiene como extremo todas las aristas que se
	 *            recorren
	 * @param empiezaEnEsta
	 *            La arista de comienzo
	 * @see ListAdaptor
	 */

	public ListAdaptor verticeIterator(Vertice3d vertice, NodoArista empiezaEnEsta) {
		return new ItrVertice(vertice, empiezaEnEsta);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class ItrVertice implements ListAdaptor {

		private Vertice3d vertice;
		private Vertice3d primer_vertice_iterado;
		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio = true;

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		private ItrVertice(Vertice3d unvertice) {

			vertice = unvertice;

			actual = inicial = vertice.arista();

			primer_vertice_iterado = inicial.ori().igual(unvertice) ? inicial.des() : inicial.ori();

			anterior = null;

		}

		private ItrVertice(Vertice3d unvertice, NodoArista empiezaEnEsta) {

			vertice = unvertice;

			actual = inicial = empiezaEnEsta;

			primer_vertice_iterado = inicial.ori().igual(unvertice) ? inicial.des() : inicial.ori();

			anterior = null;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			boolean resultado = (!primer_vertice_iterado.igual(

					actual.ori().igual(vertice) ? actual.des() : actual.ori()

			) || esinicio);

			return resultado;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			NodoArista resultado = actual;

			anterior = actual;

			actual = actual.ori().igual(vertice) ?

					actual.aristaOri() :

					actual.aristaDes();

			esinicio = false;

			return resultado;
		}

	} // fin clase ItrVertice

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R Recorrer las aristas en torno a un vertice */
	/*
	 * /* en orden negativo o CW /*
	 * -------------------------------------------------------------------
	 */

	/**
	 * Iterador para recorrer las aristas adyacentes al vertice pasado en orden
	 * y sentido negativo.
	 * 
	 * @param vertice
	 *            El vertice que tiene como extremo todas las aristas que se
	 *            recorren
	 * @see ListAdaptor
	 */

	public ListAdaptor verticeIteratorCW(Vertice3d vertice) {
		return new ItrVerticeCW(vertice);
	}

	/**
	 * Iterador para recorrer las aristas adyacentes al vertice pasado en orden
	 * y sentido negativo, comenzando por la arista pasada.
	 * 
	 * @param vertice
	 *            El vertice que tiene como extremo todas las aristas que se
	 *            recorren
	 * @param empiezaEnEsta
	 *            La arista de comienzo
	 * @see ListAdaptor
	 */

	public ListAdaptor verticeIteratorCW(Vertice3d vertice, NodoArista empiezaEnEsta) {
		return new ItrVerticeCW(vertice, empiezaEnEsta);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class ItrVerticeCW implements ListAdaptor {

		private Vertice3d vertice;
		private Vertice3d primer_vertice_iterado;
		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio = true;

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		private ItrVerticeCW(Vertice3d unvertice) {

			vertice = unvertice;

			actual = inicial = vertice.arista();

			primer_vertice_iterado = inicial.ori().igual(unvertice) ? inicial.des() : inicial.ori();

			anterior = null;

		}

		private ItrVerticeCW(Vertice3d unvertice, NodoArista empiezaEnEsta) {

			vertice = unvertice;

			actual = inicial = empiezaEnEsta;

			primer_vertice_iterado = inicial.ori().igual(unvertice) ? inicial.des() : inicial.ori();

			anterior = null;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			boolean resultado = (!primer_vertice_iterado.igual(

					actual.ori().igual(vertice) ? actual.des() : actual.ori()

			) || esinicio);

			return resultado;
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			NodoArista resultado = actual;

			anterior = actual;

			// identificar las dos aristas candidatas a siguiente

			ListAdaptor itercara = caraIterator(actual.caraIzq());

			NodoArista n;

			NodoArista candidata = null;

			while (itercara.hasNext()) {

				n = itercara.next();

				if (actual.igual(n))
					continue;

				if (vertice.igual(n.ori()) || vertice.igual(n.des())) {

					candidata = n;
					break;
				}

			}

			if (candidata == null)
				throw new RuntimeException("Error de iteracion");

			NodoArista siguienteCCW = actual.ori().igual(vertice) ? actual.aristaOri() : actual.aristaDes();

			if (siguienteCCW.igual(candidata)) {

				// la candidata es justo la que no buscabamos

				itercara = caraIterator(actual.caraDer());
				candidata = null;

				while (itercara.hasNext()) {

					n = itercara.next();

					if (actual.igual(n))
						continue;

					if (vertice.igual(n.ori()) || vertice.igual(n.des())) {

						candidata = n;
						break;
					}
				}
				if (candidata == null)
					throw new RuntimeException("Error de iteracion");

			}

			actual = candidata;

			esinicio = false;

			return resultado;
		}

	} // fin clase ItrVertice

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R Recorrer las aristas en torno a una cara */
	/* ------------------------------------------------------------------- */

	/**
	 * Iterador para recorrer las aristas que forman una cara que es pasada como
	 * argumento
	 * 
	 * @param cara
	 *            La cara cuyas aristas se van a recorrer
	 * @see ListAdaptor
	 */

	public ListAdaptor caraIterator(Triangulo3d cara) {
		return new ItrCara(cara);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class ItrCara implements ListAdaptor {

		private Triangulo3d cara;
		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio = true;
		int total;

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		private ItrCara(Triangulo3d cara) {

			this.cara = cara;
			actual = inicial = cara.arista();
			anterior = null;

			total = 0;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			return (total != 3);
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			total++;

			NodoArista resultado = actual;
			anterior = actual;

			actual = actual.aristaDes().caraIzq().equals(cara) || actual.aristaDes().caraDer().equals(cara) ?

					actual.aristaDes() :

					actual.aristaOri();

			esinicio = false;
			return resultado;
		}

	} // fin clase ItrVertice

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R Recorrer la Base de Cono */
	/* ------------------------------------------------------------------- */

	/**
	 * Iterador para recorrer las aristas que forman la base del cono de
	 * visibilidad del poliedro por un punto en un paso parcial iterativo del
	 * metodo incremental. Previamente se debe haber llamado a los metodos
	 * testVisibilidadCaras() y testVisibilidadAristas()
	 * 
	 * @param aristaBorde
	 *            Una arista de la base del cono de visibilidad
	 * @see ListAdaptor
	 */

	// para un vertice dado iterar las aristas adyacentes desde el mismo

	public ListAdaptor iteradorBaseCono(NodoArista aristaBorde) {
		return new ItrAristaBaseCono(aristaBorde);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class ItrAristaBaseCono implements ListAdaptor {

		private Vertice3d vertice;
		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio;

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		private ItrAristaBaseCono(NodoArista aristaBorde) {

			this.vertice = vertice;
			actual = inicial = aristaBorde;

			// PROCESO para saber cual es la anterior

			Vertice3d[] caraInvisible = actual.caraIzq().getEstado() == Triangulo3d.VISIBLE ?

					actual.caraDer().toArray() :

					actual.caraIzq().toArray();

			Vertice3d[] sentidoMarcha = new Vertice3d[2];

			// fijar las cosas un poco

			// es decir. Dar una orientacion a la arista actual en el sentido de
			// la marcha

			// Como se sabe el sentido?: por la orientacion positiva de la cara
			// invisible adyacente a la arista

			int i;
			for (i = 0; !caraInvisible[i].equals(actual.des()); i++)
				;

			if (caraInvisible[(i + 1) % 3].equals(actual.ori())) {

				sentidoMarcha[0] = actual.ori();
				sentidoMarcha[1] = actual.des();

			} else {
				sentidoMarcha[0] = actual.des();
				sentidoMarcha[1] = actual.ori();
			}

			// buscar la anterior iterando aristas desde el vertice origen de la
			// arista actual ya orientada

			NodoArista e = sentidoMarcha[0].obtenerDistintaBaseCono(actual);

			esinicio = true;

			anterior = e;

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			return (actual != inicial || esinicio);
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			NodoArista resultado = actual;
			anterior = actual;

			if (actual == null)
				throw new ArrayIndexOutOfBoundsException();

			Vertice3d[] caraInvisible = actual.caraIzq().getEstado() == Triangulo3d.VISIBLE ?

					actual.caraDer().toArray() :

					actual.caraIzq().toArray();

			Vertice3d[] sentidoMarcha = new Vertice3d[2];

			// fijar las cosas un poco, es decir, establecer cual es el sentido
			// correcto
			// segun la marcha por la base del cono, de la arista actual, ya que
			// esta
			// es guardada en la dcel sin una orientacion definida. Mas
			// llanamente: la
			// orientacion de actual.ori() a acutal.des() no tiene porque
			// coincidir con el sentido
			// de la marcha

			// cabe destacar q el sentido de la marcha es aquel por el cual la
			// base del cono se recorre
			// de forma tal q por la regla de la mano derecha la normal de la
			// base apunta al vertice
			// q se esta insertando en este paso incremental

			int i;

			for (i = 0; !caraInvisible[i].equals(actual.des()); i++)
				;

			if (caraInvisible[(i + 1) % 3].equals(actual.ori())) {

				sentidoMarcha[0] = actual.ori();
				sentidoMarcha[1] = actual.des();

			} else {
				sentidoMarcha[0] = actual.des();
				sentidoMarcha[1] = actual.ori();
			}

			NodoArista e = sentidoMarcha[1].obtenerDistintaBaseCono(actual);

			actual = e;
			esinicio = false;

			return resultado;
		}

	} // fin clase ItrAristaBaseCono

	/* ------------------------------------------------------------------- */
	/* I T E R A D O R Recorrer la sombra del poligono */
	/* ------------------------------------------------------------------- */

	/**
	 * Iterador para recorrer las aristas del poliedro que proyectadas
	 * ortogonalmente en el plano XY forman el cierre convexo plano de los
	 * puntos del poliedro igualmente proyectados. Previamente se debe haber
	 * llamado al metodo testProyeccion() del objeto aristas de la DCEL.
	 * 
	 * @param arista
	 *            Una arista del poliedro que proyectada en XY forma parte del
	 *            cierre convexo plano.
	 * @see ListAdaptor
	 */

	public ListAdaptor sombraIterator(NodoArista arista) {

		return new Itrsombra(arista);
	}

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class Itrsombra implements ListAdaptor {

		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio;
		private Vertice3d destino; // es el vertice destino de la arista actual
									// con la orientacion correcta.
		// Es decir, hace q actual apunte al sentido de la marcha.
		// puede ser actual.ori() o actual.des()

		private Vertice3d[] orientada; // arista actual orientada para recorrer
										// positivamente el poligono

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		private Itrsombra(NodoArista arista) {

			actual = inicial = arista;
			anterior = null;
			esinicio = true;

			orientada = new Vertice3d[2];

			// determinar el sentido de la marcha SIEMPRE VA A SER POSITIVO EL
			// ORDEN EN Q SE VAN
			// INSERTANDO LOS PUNTOS DEL POLIGONO

			if (arista.getEstado() == NodoArista.BORDESEGURO) {

				if (arista.caraIzq().nz() > 0) {
					orientada[0] = arista.ori();
					orientada[1] = arista.des();
				} else {
					orientada[1] = arista.ori();
					orientada[0] = arista.des();
				}

				return;
			}

			if (arista.getEstado() == NodoArista.BORDEPOSIBLE) {

				// caso 1
				if (arista.caraIzq().nz() > 0) {
					orientada[0] = arista.ori();
					orientada[1] = arista.des();

					// caso 2
				} else if (arista.caraDer().nz() > 0) {
					orientada[1] = arista.ori();
					orientada[0] = arista.des();

					// caso 3
				} else if (arista.caraIzq().nz() < 0) {
					orientada[1] = arista.ori();
					orientada[0] = arista.des();

					// caso 4
				} else if (arista.caraDer().nz() < 0) {
					orientada[0] = arista.ori();
					orientada[1] = arista.des();
				} else
					throw new RuntimeException("Error de arista BORDEPOSIBLE");

				return;

			}

			throw new RuntimeException("Error de arista");

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			return (actual != inicial || esinicio);
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			NodoArista resultado = actual;
			anterior = actual;

			actual = orientada[1].siguienteProyectable(orientada);

			// // System.out.println(orientada[0].toString()+"
			// "+orientada[1].toString());

			orientada[0] = orientada[1];
			orientada[1] = orientada[1].igual(actual.ori()) ? actual.des() : actual.ori();

			esinicio = false;

			return resultado;
		}

	} // fin clase ItrSombra

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	private class Itrsombra2 implements ListAdaptor {

		private NodoArista actual;
		private NodoArista inicial; // inicial en la iteracion
		private NodoArista anterior; // anterior a la actual
		private boolean esinicio;
		private Vertice3d destino; // es el vertice destino de la arista actual
									// con la orientacion correcta.
		// Es decir, hace q actual apunte al sentido de la marcha.
		// puede ser actual.ori() o actual.des()

		private Vertice3d[] orientada; // arista actual orientada para recorrer
										// positivamente el poligono

		private Itrsombra2(NodoArista arista) {

			actual = inicial = arista;
			anterior = null;
			esinicio = true;

			// determinar el sentido de la marcha

			NodoArista sig;

			sig = arista.des().obtenerDistintaProyectable(actual);

			// es decir, temp es el vertice q no es comun entre actual y sig
			Vertice3d temp = arista.des() != sig.ori() ? sig.ori() : sig.des();

			destino = GC.area2(actual.ori().x(), actual.ori().y(), actual.des().x(), actual.des().y(), temp.x(),
					temp.y())

			> 0 ? actual.des() : actual.ori();

		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public boolean hasNext() {

			return (actual != inicial || esinicio);
		}

		/*
		 * -------------------------------------------------------------------
		 */
		/*
		 * -------------------------------------------------------------------
		 */

		public NodoArista next() {

			NodoArista resultado = actual;
			anterior = actual;

			actual = (actual.des() == destino) ? actual.des().obtenerDistintaProyectable(actual)
					: actual.ori().obtenerDistintaProyectable(actual);

			destino = actual.ori() == destino ? actual.des() : actual.ori();

			esinicio = false;

			return resultado;
		}

	} // fin clase ItrSombra2

	/* ------------------------------------------------------------------- */
	/* ------------------------------------------------------------------- */

	/**
	 * Este metodo devuelve el poligono que es el cierre convexo de los puntos
	 * del poliedro proyectados en el plano XY. Se hace uso del iterador
	 * sombraIterator para formar el poligono y anteriormente se hace un test de
	 * todas las aristas para localizar aquellas que formaran parte del
	 * recorrido mediante el metodo testProyeccion() del objeto aristas.
	 * 
	 * @see Poligono
	 */

	public Poligono proyecOptima() {

		aristas.reset();
		vertices.reset();

		NodoArista delborde = aristas.testProyeccion();
		NodoArista e;
		NodoArista primera;
		NodoArista anterior;

		// aDisco("DCELtest18 test hecho.txt");

		ListAdaptor j = sombraIterator(delborde);

		Poligono resultado = new Poligono();

		primera = e = j.next();

		Vertice3d comun;

		while (j.hasNext()) {

			anterior = e;

			e = j.next();

			comun = e.comunVertice(anterior);

			// el tercer parametro es para conocer de forma inmediata q vertice
			// del poliedro
			// esta asociado a este punto del plano

			resultado.add(new Punto2D(comun.x(), comun.y(), comun));

		}

		anterior = e;

		e = primera;

		comun = e.comunVertice(anterior);

		resultado.add(new Punto2D(comun.x(), comun.y(), comun));

		return resultado;

	}

	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Este metodo compueba que la DCEL esta bien formada por medio de una seria
	 * de baterias de pruebas. Si alguna de ellas falla se produce una
	 * expcepcion.
	 */

	public String check() {

		String temp = checkReferencial();

		if (temp.compareTo(DCEL.checkOK) != 0)
			return temp;

		temp = checkCierre();

		if (temp.compareTo(DCEL.checkOK) != 0)
			return temp;

		temp = checkEuler();

		if (temp.compareTo(DCEL.checkOK) != 0)
			return temp;

		return DCEL.checkOK;

	}

	/**
	 * Este metodo compueba que vertices y caras referencian aristas existentes
	 */

	public String checkReferencial() {

		Vertice3d v;
		Triangulo3d t;
		NodoArista n;
		boolean flag;

		// En primer lugar comprueba que todo vertice referencia
		// a una arista que esta en el cierre

		ListIterator iter1 = vertices.listIterator();
		ListIterator iter2;

		while (iter1.hasNext()) {

			v = (Vertice3d) iter1.next();

			iter2 = aristas.listIterator();

			flag = false;

			while (iter2.hasNext()) {

				n = (NodoArista) iter2.next();

				if (n.igual(v.arista())) {

					flag = true;

					break;
				}
			}

			if (!flag)
				return new String("Error referencial. Vertice: " + v.toString());

		}

		// En segundo lugar comprueba que toda cara referencia
		// a una arista que esta en el cierre. En otro caso se produce una
		// excepcion

		iter1 = caras.listIterator();

		while (iter1.hasNext()) {

			t = (Triangulo3d) iter1.next();

			iter2 = aristas.listIterator();

			flag = false;

			while (iter2.hasNext()) {

				n = (NodoArista) iter2.next();

				if (n.igual(t.arista())) {

					flag = true;

					break;
				}
			}
			if (!flag)
				return new String("Error referencial. Cara: " + t.toString());
		}

		return DCEL.checkOK;

	}

	private static final String checkOK = "CHECK_OK";

	/**
	 * Este metodo compueba que todas las caras son del cierre, es decir, dejan
	 * en el mismo semiplano a todos los demas puntos del poliedro. En otro caso
	 * se produce una excepcion
	 */

	public String checkCierre() {

		ListIterator iterCaras = caras.listIterator();
		ListIterator iterVertices;
		Triangulo3d c;
		Vertice3d v;

		while (iterCaras.hasNext()) {
			c = (Triangulo3d) iterCaras.next();

			iterVertices = vertices.listIterator();

			while (iterVertices.hasNext()) {

				v = (Vertice3d) iterVertices.next();

				if (GC.visible(c, v)) // es visible la cara desde el exterior
										// por este punto
					return new String("checkCierre: El poligono no es convexo");

			}
		}

		return DCEL.checkOK;

	}

	/**
	 * Este metodo compueba una propiedad de Euler para grafos, es decir,
	 * |F|=2*|V| - 4. En otro caso se produce una excepcion
	 */

	public String checkEuler() {

		int V = vertices.get_total();
		int F = caras.get_total();

		if (F != 2 * V - 4)
			return new String("No cumple la propiedad de Euler: |F|=2*|V| - 4  (F=" + F + " V=" + V + " )  ");

		return DCEL.checkOK;
	}

	private boolean esGanadoraIzq(NodoArista anterior, NodoArista actual, NodoArista siguiente, Vertice3d comun,
			Vertice3d exterior) {

		// devuelve true sii la arista actual es purpura

		Vertice3d a0 = comun.igual(anterior.ori()) ? anterior.des() : anterior.ori();
		Vertice3d a1 = comun.igual(actual.ori()) ? actual.des() : actual.ori();
		Vertice3d a2 = comun.igual(siguiente.ori()) ? siguiente.des() : siguiente.ori();

		// si la arista tiene dos caras coplanares a ambos lados, devolver false

		if (GC.volumen6(a0, a1, a2, comun) == 0) {

			return false;
		}

		// si el candidato esta alienado con comun y exterior entonces no
		// formara cara

		if (GC.alineados(a1, comun, exterior)) {

			return false;
		}

		// en la cara t se supone q sus vertices estan orientados segun un orden
		// tal
		// q su normal apunta hacia afuera de de C1

		Triangulo3d nuevaCara = new Triangulo3d(exterior, comun, a1);

		// resultado : a0 no ve a la nueva cara ni esta alineada y a2 idem

		// seguridad: boolean resultado = (!(GC.visible(nuevaCara,a0) ||
		// GC.visible(nuevaCara,a2)));

		boolean resultado = GC.volumen6(nuevaCara, a0) >= 0 && GC.volumen6(nuevaCara, a2) >= 0;

		// boolean resultado = GC.volumen6(nuevaCara,a0)<=0 &&
		// GC.volumen6(nuevaCara,a2)<=0;

		// if (resultado!=resultado) throw new RuntimeException("FALLO TEST
		// GANADORA");

		return resultado;
	}

	private boolean esGanadoraDer(NodoArista anterior, NodoArista actual, NodoArista siguiente, Vertice3d comun,
			Vertice3d exterior) {

		// devuelve true sii la arista actual es purpura

		Vertice3d a0 = comun.igual(anterior.ori()) ? anterior.des() : anterior.ori();
		Vertice3d a1 = comun.igual(actual.ori()) ? actual.des() : actual.ori();
		Vertice3d a2 = comun.igual(siguiente.ori()) ? siguiente.des() : siguiente.ori();

		// si la arista tiene dos caras coplanares a ambos lados, devolver false

		if (GC.volumen6(a0, a1, a2, comun) == 0) {

			return false;
		}

		// si el candidato esta alienado con comun y exterior entonces no
		// formara cara

		if (GC.alineados(a1, comun, exterior)) {

			return false;
		}

		// en la cara t se supone q sus vertices estan orientados segun un orden
		// tal
		// q su normal apunta hacia afuera de C1

		Triangulo3d nuevaCara = new Triangulo3d(exterior, a1, comun);

		// resultado : a0 no ve a la nueva cara ni esta alineada y a2 idem

		boolean resultado = GC.volumen6(nuevaCara, a0) >= 0 && GC.volumen6(nuevaCara, a2) >= 0;
		// boolean resultado = GC.volumen6(nuevaCara,a0)<=0 &&
		// GC.volumen6(nuevaCara,a2)<=0;

		// if (resultado!=resultado) throw new RuntimeException("FALLO TEST
		// GANADORA");

		return resultado;
	}

	/**
	 * Devuelve la arista ganadora de la DCEL en un paso de Divide y venceras
	 * cuando la DCEL es el poliedro con menores abscisas.
	 * 
	 * @param comun
	 *            es el vertice que tienen como extremo comun todas las arista
	 *            que se comprueban si son ganadoras
	 * @param exterior
	 *            es el vertice de la otra DCEL que formara parte de la nueva
	 *            cara
	 * @param comienzo
	 *            es la arista por la que comienza el test para comprobar si es
	 *            la ganadora
	 */

	public NodoArista ganadoraIzq(Vertice3d comun, Vertice3d exterior, NodoArista comienzo) {

		// vamos a considerar que no habra mas de 50000 vertices unidos por una
		// arista
		// al vertice comun en esta DCEL.
		// Si agotamos todas estas posibilidades significara que no hemos
		// encontrado
		// ninguna candidato que sea el ganador y lanzamos una excepcion

		int total = 50000;

		NodoArista[] ar = new NodoArista[3];
		NodoArista n;

		// comun == vertice de este poliedro
		// exterior == vertice del otro poliedro

		ListAdaptor iter = verticeIterator(comun, comienzo);

		// buscamos la anterior de comienzo

		ListAdaptor iterContrario = verticeIteratorCW(comun, comienzo);
		ar[0] = iterContrario.next(); // a[0] ahora es comienzo
		ar[0] = iterContrario.next(); // a[0] ahora es la anterior de comienzo,
										// es decir la antecesora. Ok

		// buscamos ahora a[1] candidata actual y a[2] la sucesora

		iter = verticeIterator(comun, comienzo);
		ar[1] = iter.next();
		ar[2] = iter.next();

		NodoArista siguienteComienzo = iter.next();
		iter = verticeIterator(comun, siguienteComienzo);

		while (!esGanadoraIzq(ar[0], ar[1], ar[2], comun, exterior)) {

			ar[0] = ar[1];
			ar[1] = ar[2];
			ar[2] = iter.next();

			if (--total == 0) {
				throw new SinGanadoraException("PANICO: no se encontro ganadora");
			}

		}

		return ar[1];

	}

	/**
	 * Devuelve la arista ganadora de la DCEL en un paso de Divide y venceras
	 * cuando la DCEL es el poliedro cuyos puntos tienen mayores abscisas.
	 * 
	 * @param comun
	 *            es el vertice que tienen como extremo comun todas las arista
	 *            que se comprueban si son ganadoras
	 * @param exterior
	 *            es el vertice de la otra DCEL que formara parte de la nueva
	 *            cara
	 * @param comienzo
	 *            es la arista por la que comienza el test para comprobar si es
	 *            la ganadora
	 */

	public NodoArista ganadoraDer(Vertice3d comun, Vertice3d exterior, NodoArista comienzo) {

		// comun == vertice de este poliedro
		// exterior == vertice del otro poliedro

		// vamos a considerar que no habra mas de 50000 vertices unidos por una
		// arista
		// al vertice comun en esta DCEL.
		// Si agotamos todas estas posibilidades significara que no hemos
		// encontrado
		// ninguna candidato que sea el ganador y lanzamos una excepcion

		int total = 50000;

		NodoArista[] ar = new NodoArista[3];

		// buscamos la anterior de comienzo

		ListAdaptor iterContrario = verticeIterator(comun, comienzo);
		ar[0] = iterContrario.next(); // a[0] ahora es comienzo
		ar[0] = iterContrario.next(); // a[0] ahora es la anterior de comienzo,
										// es decir la antecesora. Ok

		// buscamos ahora a[1] candidata actual y a[2] la sucesora

		ListAdaptor iter = verticeIteratorCW(comun, comienzo);
		ar[1] = iter.next();
		ar[2] = iter.next();

		NodoArista siguienteComienzo = iter.next();
		iter = verticeIteratorCW(comun, siguienteComienzo);

		NodoArista n;

		while (!esGanadoraDer(ar[0], ar[1], ar[2], comun, exterior)) {

			ar[0] = ar[1];
			ar[1] = ar[2];
			ar[2] = iter.next();

			if (--total == 0) {
				throw new SinGanadoraException("PANICO: no se encontro ganadora");
			}
		}

		return ar[1];

	}

	/*
	 * 
	 * public boolean checkCara(Triangulo3d cara){
	 * 
	 * ListIterator iterVertices;
	 * 
	 * Vertice3d v;
	 * 
	 * iterVertices = vertices.listIterator();
	 * 
	 * while (iterVertices.hasNext()) {
	 * 
	 * v = (Vertice3d) iterVertices.next();
	 * 
	 * if (GC.visible(cara,v)) // es visible la cara desde el exterior por este
	 * punto? { //// System.out.println(cara.verticesString()+" ... "
	 * +v.toString()); return false; }
	 * 
	 * }
	 * 
	 * return true; }
	 * 
	 * public boolean checkColor(){
	 * 
	 * // que todo este azul antes de empezar a mezclar
	 * 
	 * return caras.checkColor() && vertices.checkColor() &&
	 * aristas.checkColor();
	 * 
	 * }
	 * 
	 * 
	 * 
	 * public void testz(){
	 * 
	 * Vertice3d v = (Vertice3d) vertices.leerItem(2);
	 * 
	 * // System.out.println(v.toString());
	 * 
	 * ListAdaptor iterador = verticeIterator(v);
	 * 
	 * NodoArista n;
	 * 
	 * while (iterador.hasNext()){
	 * 
	 * n = iterador.next();
	 * 
	 * // System.out.println(n.verticesString());
	 * 
	 * }
	 * 
	 * // System.out.println("--------");
	 * 
	 * iterador = verticeIteratorCW(v);
	 * 
	 * //while (iterador.hasNext()){
	 * 
	 * while (iterador.hasNext()){
	 * 
	 * n = iterador.next();
	 * 
	 * // System.out.println(n.verticesString());
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 */

	/**
	 * Para marcar las aristas en el proceso de Divide y venceras de cara a no
	 * contar con ellas porque no formaran parte del nuevo poliedro producto de
	 * la mezcla
	 */

	public void marcarAristas() {

		aristas.marcarAristas();
	}

	private void marcarCarasRec(Triangulo3d actual) {

		// primero recorre en profundidad (iterativamente)las caras de la parte
		// q se va eliminar
		// marcando en rojo las caras que correspondan

		Triangulo3d vecina;

		NodoArista ladoActual;

		ListAdaptor iter;

		actual.setVisitado(true);
		actual.ponColor(EstadoColor.ROJO);

		iter = caraIterator(actual);

		while (iter.hasNext()) {

			ladoActual = iter.next();
			vecina = ladoActual.caraIzq() == actual ? ladoActual.caraDer() : ladoActual.caraIzq();
			if (!vecina.esVisitado()

					&& (ladoActual.dameColor() != EstadoColor.PURPURA)) {

				marcarCarasRec(vecina);
			}

		}
	}

	/**
	 * Para marcar las caras en el proceso de Divide y venceras de cara a no
	 * contar con ellas porque no formaran parte del nuevo poliedro producto de
	 * la mezcla
	 */

	public void marcarCaras(Triangulo3d caraInicio) {

		// primero recorre en profundidad (iterativamente)las caras de la parte
		// q se va eliminar
		// marcando en rojo las caras que correspondan

		ListIterator iterador = caras.listIterator();

		Triangulo3d cara;

		while (iterador.hasNext()) {

			cara = (Triangulo3d) iterador.next();

			cara.setVisitado(false);

		}

		marcarCarasRec(caraInicio);

	}

	/**
	 * Para marcar los vertices en el proceso de Divide y venceras de cara a no
	 * contar con ellos porque no formaran parte del nuevo poliedro producto de
	 * la mezcla. Antes de llamar a esta funcion ya debemos tener marcadas todas
	 * las caras y aristas
	 */

	public void marcarVertices() {

		// Antes de llamar a esta funcion ya debemos tener marcadas todas las
		// caras y arista
		// rojas
		// Para marcar los vertices: cosideramos todos los vertices como
		// posibles a ser borrados
		// Entonces, vamos a recorrer todas las aristas y vamos a desechar como
		// vertices borrables
		// todos aquellos vertices que sean extremos de aristas de color purpura
		// o azul, es decir, que no
		// sean rojas

		// Poner todos los vertices rojos excepto aquellos que sean purpura

		vertices.prepararVerticesMarcado();

		// Recorrer aristas y
		// fijar como no borrables aquellos vertices
		// extremos de aristas azules o purpuras

		aristas.descartarVertices();

	}

	/**
	 * Para inlcuir en esta DCEL los vertices, aristas y caras de las DCEL A y B
	 * pasadas como parametro que no estan marcadas como descartables.
	 */

	public void fundir(DCEL A, DCEL B) {

		// incluir aquellos elementos de A y B que no son rojos

		vertices.incluir(A.vertices);
		vertices.incluir(B.vertices);
		aristas.incluir(A.aristas);
		aristas.incluir(B.aristas);
		caras.incluir(A.caras);
		caras.incluir(B.caras);

	}

	/**
	 * Devuelve un iterador de las caras de esta DCEL
	 */

	public ListIterator carasIterator() {
		return caras.listIterator();
	}

	/**
	 * Devuelve un iterador de los vertices de esta DCEL
	 */

	public ListIterator verticesIterator() {
		return vertices.listIterator();
	}

	/**
	 * Devuelve un iterador de las aristas de esta DCEL
	 */

	public ListIterator aristasIterator() {
		return aristas.listIterator();
	}

	/**
	 * Devuelve cierto si esta DCEL es igual a la pasada como parametro
	 * 
	 * @param a
	 *            la DCEL que queremos comprobar su igualdad
	 */

	public boolean igual(DCEL a) {

		boolean resultado = true;

		ListIterator iter = a.carasIterator();
		Triangulo3d unaCara;
		while (iter.hasNext()) {
			unaCara = (Triangulo3d) iter.next();

			if (!caras.estaIncluida(unaCara)) {

				if (resultado)
					resultado = false;
			}

		}

		iter = a.aristasIterator();
		NodoArista arista;
		while (iter.hasNext()) {
			arista = (NodoArista) iter.next();

			if (!aristas.estaIncluida(arista)) {

				if (resultado)
					resultado = false;
			}

		}

		iter = a.verticesIterator();
		Vertice3d vertice;
		while (iter.hasNext()) {
			vertice = (Vertice3d) iter.next();

			if (!vertices.estaIncluida(vertice)) {

				if (resultado)
					resultado = false;
			}

		}

		return resultado;

	}

	/**
	 * Para poner en el estado inicial aquellos elementos que van a formar parte
	 * del proceso de mezcla
	 */

	public void resetMezcla() {

		// para poner como no visitadas las caras preparando el recorrido en
		// profundidad

		resetCaras();

		// antes de la mezcla todo es azul

		caras.resetColor();
		vertices.resetColor();
		aristas.resetColor();
	}

	/**
	 * Para saber el orden relativo de dos dcel cuyos puntos no estan solapados
	 * segun el orden XYZ devuelve <BR>
	 * <BR>
	 * 1 si this < dcel <BR>
	 * 
	 * -1 si this > dcel
	 * 
	 * @param dcel
	 *            La DCEL que se quiere comparar
	 */

	public int ordenXYZ(DCEL dcel) {

		ListIterator iter1 = vertices.listIterator();
		ListIterator iter2 = dcel.vertices.listIterator();

		Vertice3d v1 = (Vertice3d) iter1.next();
		Vertice3d v2 = (Vertice3d) iter2.next();

		return v1.ordenXYZ(v2);
	}

} // fin clase DCEL
