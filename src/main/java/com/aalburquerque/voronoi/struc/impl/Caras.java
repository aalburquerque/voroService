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
import java.util.ListIterator;

import com.aalburquerque.voronoi.util.EstadoColor;
import com.aalburquerque.voronoi.util.GC;
/**
 * Esta clase implementa una lista de caras de una DCEL. La clase hereda directamente de ListaDE y a�ade las
 * funciones que deben ser propias de esta clase.
 * 
 * @author  Antonio Alburquerque Oliva
 * @version 1.00
 * @see DCEL
 * @see Aristas
 * @see Vertices
 */




public class Caras extends ListaDE implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
      * Para construir una lista de caras 
      */    

    public Caras(){
    	super();
    }
   
   /**
      * Para inicializar el conjunto de todas las caras 
      */    
   
    public void reset(){
 
      /* inicializar visibilidad de las caras */
      
      ListIterator iterador = listIterator();
      
      Triangulo3d cara;
      
      while(iterador.hasNext()) {
      	
      	cara = (Triangulo3d)iterador.next();
      	
      	cara.reset();
      	
      }

 
    }
    
  
      
     
    
  /**
     * Este metodo sirve para clasificar cada cara de la lista segun si 
     * es visible y en cuyo caso no va a pertenecer al cierre en el siguiente
     * para iterativo en el proceso incremental. 
     * Si no hay ninguna cara visible entonces el punto p esta dentro del poliedro
     * y en ese caso se devuelve cierto.
     * @param p El punto por el cual se clasifican las caras.
     */
    
    public boolean testVisible(Punto3d p){
    
      /* marcar caras visibles desde p */
      
      ListIterator iterador = listIterator();
      
      Triangulo3d t;
      
      boolean b = true;  /*supongamos q no hay ninguna visible*/
      
      while (iterador.hasNext()) {
      	
      		t = (Triangulo3d) iterador.next();
      		
      		if (GC.visible(t,p)) {
      			
      			b = false; /* al menos hay una visible */
      			
      			t.setEstado(Triangulo3d.VISIBLE);
      		}
      }
      
      return b; // hay alguna cara visible
      
    }
      
      
    
    /**
      * Para eliminar las caras que no van a formar parte del poliedro final
      * en una paso iterativo de proceso incremental
      */    
       
        
      
  public void limpiar(){
  	
  	ListIterator l = listIterator();
  	
  	Triangulo3d c;
  	
  	while (l.hasNext() ){
  		
  		c = (Triangulo3d)l.next();
  		
  		if (c.getEstado()==Triangulo3d.VISIBLE) l.remove();
  		
  	}
  }
 		
    /**
      * Para incluir aquellas caras de la lista de caras pasada como argumento que no son rojas 
      * @param otros La lista de caras
      */    
 
 
 public void incluir(Caras otros){
      
      		// incluir aquellos elementos que no son rojos 
      	
      		ListIterator iterador = otros.listIterator();
      
      		Triangulo3d elemento;
      
      		while (iterador.hasNext() ){
  		
  			elemento = (Triangulo3d)iterador.next();
  		
  			if (elemento.dameColor()!=EstadoColor.ROJO) {
  				
  				insertarFinal(elemento);
  			}
  		
      		}	
      }	 
    
    		
    /**
      * Devuelve cierto si la cara esta incluido en la lista de caras
      * @param caraComparable La cara a comprobar si esta en la lista
      */    
 
    
  public boolean estaIncluida(Triangulo3d caraComparable){
  	
  	
  	ListIterator iterador = listIterator();
      
      	Triangulo3d elemento;
      
      	while (iterador.hasNext()){
  		
  			elemento = (Triangulo3d)iterador.next();
  		
  			if (elemento.igual(caraComparable)) return true;
  		
      	}	
  	
  	return false;
  	
  }
  
  /*
  
  public boolean checkColor(){
   	
   
   	ListIterator iterador = listIterator();
      
      	Triangulo3d elemento;
      
      	while (iterador.hasNext()) {
  		
  			elemento = (Triangulo3d)iterador.next();
  		
  			if (elemento.dameColor()!=EstadoColor.AZUL) 
  			
  				return false;
  		
      	}	
  	
  	return true;	
   		
   	
   }
 
 */
  		
    /**
      * Inicializar el color de todos las caras
      */    
  
   
   public void resetColor(){
   	
   	ListIterator iterador = listIterator();
      
      	Triangulo3d elemento;
      
      	while (iterador.hasNext()) {
  		
  			elemento = (Triangulo3d)iterador.next();
  		
  			elemento.ponColor(EstadoColor.AZUL);
      	}	
  	
   	
   }
    

}