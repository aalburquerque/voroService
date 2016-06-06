package com.aalburquerque.voronoi;

import org.junit.Test;

import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.struc.impl.InstanciaVoronoi;
import com.aalburquerque.voronoi.struc.impl.VoronoiOutput;

import org.junit.Assert;

public class VoronoiServiceTest {

	@Test
	public void test() {
		int totalpuntos = 125;
		int tambase = 30;
		int xmax = 620, ymax = 480;
		boolean validacionPuntosEnPosicionGeneral = false;
		InstanciaVoronoi unaInstancia = new InstanciaVoronoi(totalpuntos, xmax, ymax, validacionPuntosEnPosicionGeneral,
				tambase);
		unaInstancia.calculaVoronoi();
		
		VoronoiOutput result=unaInstancia.getPoliedro().getVoronoiJSON(new MyCoord());
		
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getLines());
		Assert.assertFalse(result.getLines().isEmpty());
	}
	
	private class MyCoord implements ICoord {
		public int x(long wx) {
			return (int) wx + 20;
		}

		public int y(long wy) {
			return (int) wy + 20;
		}
	}

}
