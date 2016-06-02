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

package com.aalburquerque.voronoi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ListIterator;

import javax.swing.JPanel;

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.struc.impl.InstanciaVoronoi;
import com.aalburquerque.voronoi.struc.impl.ListaDE;
import com.aalburquerque.voronoi.struc.impl.Punto2D;
import com.aalburquerque.voronoi.util.Util;

/**
 * El objeto Pizarra se encarga de dibujar los elementos que implementan la
 * interfaz Dibujable, tales como, un segmento, un poligono, un poliedro. Un
 * poliedro se dibuja en el plano bajo el contexto de esta aplicacion por el
 * diagrama de Voronoi asociado al mismo o la triangulacion de Delaunay.
 * 
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 */

public class Pizarra extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9221233922877513358L;
	private ListaDE dibujos;
	private ICoord coord;

	private InstanciaVoronoi unaInstancia; // para el modo manual

	private boolean esModoRecogerEventosRaton;

	/**
	 * Construir un nuevo objeto Pizarra definiendo el objeto Coord encargado de
	 * la transformacion de puntos de elementos dibujables a coordenadas del
	 * area de dibujo. Se a�ade un parametro booleano para indicar si este
	 * objeto se debe encargar de recoger los eventos de raton puesto que los
	 * puntos se van a introducir manualmente
	 *
	 * @param coord
	 *            El objeto Coord encargado de la transformacion de puntos de
	 *            elementos dibujables a coordenadas del area de dibujo
	 * @param esModoRecogerEventosRaton
	 *            Booleano que es cierto si queremos que los eventos de rantos
	 *            sean recogidos
	 */

	public Pizarra(ICoord coord, boolean esModoRecogerEventosRaton) {

		this.esModoRecogerEventosRaton = esModoRecogerEventosRaton;
		this.coord = coord;

		dibujos = new ListaDE();

		if (esModoRecogerEventosRaton) {

			addMouseListener(this);
			unaInstancia = new InstanciaVoronoi();
		}

	}

	/**
	 * Construir un nuevo objeto Pizarra definiendo el objeto Coord encargado de
	 * la transformacion de puntos de elementos dibujables a coordenadas del
	 * area de dibujo. No se recogen los eventos de raton
	 *
	 * @param coord
	 *            El objeto Coord encargado de la transformacion de puntos de
	 *            elementos dibujables a coordenadas del area de dibujo
	 */

	public Pizarra(ICoord coord) {

		this(coord, false);
	}

	/**
	 * Metodo para obtener la instancia que contiene los puntos que se han
	 * introducido mediante pulsaciones de raton
	 */

	public InstanciaVoronoi getInstancia() {

		if (!esModoRecogerEventosRaton)
			return null;

		return unaInstancia;
	}

	private void pintar_fondo(Graphics g) {

		g.setColor(new Color(234, 234, 255));
		g.fillRect(0, 0, Util.LIMX, Util.LIMY);

	}

	/**
	 * Incluir un nuevo objeto que implemente la interfaz Dibujable, es decir,
	 * que sea representable en el area de dibujo.
	 *
	 * @param dibujo
	 *            El objeto que implemente la interfaz Dibujable
	 */

	public void incluye(Dibujable dibujo) {
		dibujos.insertarInicio(dibujo);
	}

	/**
	 * Recoger una pulsacion del raton. Se a�ade el punto a la instancia del
	 * problema.
	 *
	 * @param e
	 *            Evento de raton
	 */

	public void mouseClicked(MouseEvent e) {

		unaInstancia.unoMas((long) e.getX(), (long) e.getY());
		//// System.out.println(e.getX()+" "+e.getY()+"
		//// "+unaInstancia.totalPuntos());
		dibujos.insertarInicio(new Punto2D((long) e.getX(), (long) e.getY()));
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		;
	}

	public void mouseReleased(MouseEvent e) {
		;
	}

	public void mouseEntered(MouseEvent e) {
		;
	}

	public void mouseExited(MouseEvent e) {
		;
	}

	/**
	 * Definir cual es el objeto que implemente la interfaz Dibujable
	 */

	public void setCoord(ICoord coord) {

		this.coord = coord;
	}

	public void paint(Graphics g) {

		Graphics2D g2;
		g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Rectangle areadibujo = new Rectangle(0, 0, Util.LIMX, Util.LIMY);

		g2.setClip(areadibujo);

		pintar_fondo(g2);

		ListIterator iter = dibujos.listIterator();

		Dibujable dibujo;

		while (iter.hasNext()) {

			dibujo = (Dibujable) iter.next();

			dibujo.dibujar(g2, coord);

		}

	}

}
