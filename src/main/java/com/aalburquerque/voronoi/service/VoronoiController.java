package com.aalburquerque.voronoi.service;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aalburquerque.voronoi.struc.ICoord;
import com.aalburquerque.voronoi.struc.impl.InputPoints;
import com.aalburquerque.voronoi.struc.impl.InstanciaVoronoi;
import com.aalburquerque.voronoi.struc.impl.Nube2D;
import com.aalburquerque.voronoi.struc.impl.VoronoiOutput;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VoronoiController {

	// tamaño base para divide y venceras en el calculo del cierre convexo en el espacio
	private static final int DEFAULT_TAM_BASE = 30;

	/**
	 * 
	 * Input Sample:{"points":[{"x":123,"y":53},{"x":343,"y":33},{"x":563,"y":93},{"x":233,"y":83},{"x":213,"y":63},{"x":23,"y":34},{"x":83,"y":123}]}
	 * 
	 * @param pointsJson
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/voronoi", method = RequestMethod.GET)
	public VoronoiOutput voronoiCalculus(@RequestParam("pointsJson") String pointsJson)
			throws JsonMappingException, JsonParseException, IOException {
		
		InputPoints inputPoints = new ObjectMapper().readValue(pointsJson, InputPoints.class);
		InstanciaVoronoi unaInstancia = new InstanciaVoronoi(new Nube2D(inputPoints), DEFAULT_TAM_BASE);
		unaInstancia.calculaVoronoi();
		return unaInstancia.getPoliedro().getVoronoiJSON(new MyCoord());
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