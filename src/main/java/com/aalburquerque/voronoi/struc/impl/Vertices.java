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

/**
 * Esta clase implementa los atributo y metodos propios de una lista de 
 * vertices de una DCEL. 
 * 
 * @author  Antonio Alburquerque Oliva
 * @version 1.00
 * @see DCEL
 * @see Aristas
 * @see Caras
 */


public class Vertices extends ListaDE implements Serializable{

	private static final long serialVersionUID = 1L;
    /**
      * Para inicializar el conjunto de todos los vertices
      */    

    public void reset(){
             
      
      ListIterator iterador = listIterator();
      
      Vertice3d v;
      
      while(iterador.hasNext()) {
      		v = (Vertice3d)iterador.next();
      		
      		v.reset();
      		
      }
 
    }
    
    /**
      * Para construir una lista de vertices 
      */    

    public Vertices(){
    	super();
    }

    // se pasa como parametro el conjunto de las aristas una vez que
    // estas han sido limpiadas
    
    /**
      * Para eliminar los vertices que no van a formar parte del poliedro final
      * en una paso iterativo de proceso incremental
      * @param aristas La lista de aristas un vez depurada esta
      */    
    

    public void limpiar(Aristas aristas){
 
 	 // en este punto hemos borrado las aristas q ya no estan en el cierre
 	 // entonces hay q saber q puntos ahora no van a estar en el cierre
  
         // para esto hacemos una pasada por las aristas 
         // y marcamos como no borrables sus vertices extremos
         
         // los vertices no marcados seran borrados
         
      ListIterator iterador = aristas.listIterator();
      
      NodoArista e;
      
      while(iterador.hasNext()) { 
      	
      	   e = (NodoArista)iterador.next();
      	   
      	   e.ori().setEstado(Vertice3d.NOBORRABLE);
      	   e.des().setEstado(Vertice3d.NOBORRABLE);
      	 	
      }
      
      // borrar los vertices q no tiene el estado de no borrable
      
      iterador = listIterator();
      
      Vertice3d v;
      
      while (iterador.hasNext() ){
  		
  		v = (Vertice3d)iterador.next();
  		
  		if (v.getEstado()!=Vertice3d.NOBORRABLE) iterador.remove();
  		
      }
  	
    }
      
    /**
      * Para poner todos los vertices de colo rojo excepto los vertices de color purpura
      */    
      
      
    public void prepararVerticesMarcado(){
  		
  		
  		// ponemos rojos todos los vertices
  		// excepto los purpura
  		
  		ListIterator iterador = listIterator();
      
      		Vertice3d v;
      
      		while (iterador.hasNext() ){
  		
  			v = (Vertice3d)iterador.next();
  		
  			if (v.dameColor()!=EstadoColor.PURPURA)
  			
  			       v.ponColor(EstadoColor.ROJO);
  		
      		}	
      		
      	}
  		
  		
    /**
      * Para incluir aquellos vertices de la lista de vertices pasada como argumento que no son rojos 
      * @param otros La lista de los vertices
      */    
  		
      public void incluir(Vertices otros){
      
      		
      	
      		ListIterator iterador = otros.listIterator();
      
      		Vertice3d v;
      
      		while (iterador.hasNext() ){
  		
  			v = (Vertice3d)iterador.next();
  		
  			if (v.dameColor()!=EstadoColor.ROJO) {
  				
  				insertarFinal(v);
  			}
  		
      		}	
      }	
      
 
  		
    /**
      * Devuelve cierto si el vertice esta incluido en la lista de vertices
      * @param comparable El vertice a comprobar si esta en la lista
      */    
 
 
   public boolean estaIncluida(Vertice3d comparable){
  	
  	
  	ListIterator iterador = listIterator();
      
      	Vertice3d elemento;
      
      	while (iterador.hasNext() ){
  		
  			elemento = (Vertice3d)iterador.next();
  		
  			if ( elemento.igual(comparable) ) return true;
  		
      	}	

	return false;  	
  	
  }     
  
  /*
  
  public boolean checkColor(){
   	
   	ListIterator iterador = listIterator();
      
      	Vertice3d elemento;
      
      	while (iterador.hasNext()) {
  		
  			elemento = (Vertice3d)iterador.next();
  		
  			if (elemento.dameColor()!=EstadoColor.AZUL) 
  			
  				return false;
  		
      	}	
  	
  	return true;	
   	
   }
   
   */
   
  		
    /**
      * Inicializar el color de todos los vertices
      */    
  
   
   public void resetColor(){
   	
   	ListIterator iterador = listIterator();
      
      	Vertice3d elemento;
      
      	while (iterador.hasNext()) {
  		
  			elemento = (Vertice3d)iterador.next();
  		
  			elemento.ponColor(EstadoColor.AZUL);
      	}	
  	
   	
   }

}