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

package com.aalburquerque.voronoi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.aalburquerque.voronoi.gui.Pizarra;
import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.struc.impl.InstanciaVoronoi;
import com.aalburquerque.voronoi.struc.impl.Poliedro;

/**
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class VoronoiGUIApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pizarra pizarra;
	private ICoord coord;
	static private int totalpuntos = 125;
	static private int tambase = 30;
	static private int xmax = 620, ymax = 480;

	/**
	 * 
	 */
	public VoronoiGUIApp() {
		coord = new MyCoord();
		Toolkit kit = Toolkit.getDefaultToolkit();
		setSize(xmax, ymax);
		pizarra = new Pizarra(coord);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		pizarra.setSize(xmax, ymax);
		contentPane.add(pizarra, BorderLayout.CENTER);
	}

	public void dibujarVoronoiLocal(InstanciaVoronoi instanciaCalculo) {

		Pizarra anterior = pizarra;
		pizarra = new Pizarra(coord, false);

		pizarra.incluye(instanciaCalculo.nube());

		Poliedro elbicho = instanciaCalculo.poliedro();

		elbicho.setDibujarDelaunay(false);
		elbicho.setDibujarNoAcotadas(true);

		pizarra.incluye(elbicho);

		Container contentPane = getContentPane();

		contentPane.remove(anterior);

		contentPane.add(pizarra, BorderLayout.CENTER);

		setContentPane(contentPane); // esto sobraria

	}

	/**
	 * Aplicacion para probar el algoritmo de Voronoi
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		VoronoiGUIApp testFrame = new VoronoiGUIApp();
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		boolean validacionPuntosEnPosicionGeneral = false;

		InstanciaVoronoi unaInstancia = new InstanciaVoronoi(totalpuntos, xmax, ymax, validacionPuntosEnPosicionGeneral,
				tambase);

		unaInstancia.calculaVoronoi();
		testFrame.dibujarVoronoiLocal(unaInstancia);

		testFrame.pack();
		testFrame.setVisible(true);
	}

	/**
	 * @author usuario
	 *
	 */
	private class MyCoord implements ICoord {

		public int x(long wx) {
			// TODO Auto-generated method stub
			return (int) wx + 20;
		}

		public int y(long wy) {
			// TODO Auto-generated method stub
			return (int) wy + 20;
		}
	}
}
