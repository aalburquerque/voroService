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

import com.aalburquerque.voronoi.struc.Dibujable;
import com.aalburquerque.voronoi.struc.ICoord;

/**
 * @author Antonio Alburquerque Oliva
 * @version 1.00
 *
 */
public class Segmento implements Dibujable{
   
  /* ------------------------------------------------------------------- */
  /*                  A T R I B U T O S                                  */
  /* ------------------------------------------------------------------- */

   private Punto2D o,d;
       
  /* ------------------------------------------------------------------- */
  /*             C O N S T R U C T O R E S                               */
  /* ------------------------------------------------------------------- */
       
   public Segmento(Punto2D o, Punto2D d){
   
       this.o = o;
       this.d = d;       	   	
   }

    /**
      * Devuelve una cadena con la informacion correspondiente a este segmento
      */   
      
   public String toString(){
   	
   	return o.toString()+"<->"+d.toString()+" ";
   	
   }
   
   public Punto2D ori() { return o;}
   
   public Punto2D des() { return d;}
   

 /**
   * Metodo para dibujar este objeto
   * @param g El objeto de la clase Graphics donde dibujar
   * @param coord El objeto por el que se transforma las coordenadas de este objeto 
   * 		  a las coordenadas del espacio de representacion
   */      
   
   
   public void dibujar(Graphics g, ICoord coord){
     
   
      g.setColor( Color.green);
                  
      
      g.drawLine(coord.x((int) o.x()) ,
      	         coord.y((int) o.x()) , 
      	         coord.x((int) d.y()) ,
      	         coord.y((int) d.y()) );
      
   }
   


   
}
      
