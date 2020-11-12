/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Vector;

/**
 *
 * @author miguerubsk
 */
public class Individuo {
    private Vector<Integer> cromosoma;
    private int tamCromosoma;
    private double coste;
    
    public Individuo(){
        cromosoma = new Vector<Integer>();
        coste = 0.0;
    }

    public Vector<Integer> getCromosoma() {
        return cromosoma;
    }

    public int getTamCromosoma() {
        return tamCromosoma;
    }

    public double getCoste() {
        return coste;
    }
}
