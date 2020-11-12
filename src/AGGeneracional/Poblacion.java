/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Random;
import java.util.Vector;

/**
 *
 * @author miguerubsk
 */
public class Poblacion {
    private Vector<Individuo> poblacion;
    private int tamPoblacion;
    
    public Poblacion(int tam, long semilla){
        poblacion = new Vector<Individuo>();
        tamPoblacion = tam;
    }
    
    

    public Vector<Individuo> getPoblacion() {
        return poblacion;
    }

    public int getTamPoblacion() {
        return tamPoblacion;
    }
}
