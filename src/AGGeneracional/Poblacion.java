/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Vector;
import tools.CargaDatos;

/**
 *
 * @author miguerubsk
 */
public class Poblacion {

    private Vector<Individuo> poblacion;
    private int tamPoblacion;
    private long semilla;
    private CargaDatos datos;

    public Poblacion(int tam, long semilla, CargaDatos datos, boolean generar) {
        this.poblacion = new Vector<Individuo>();
        this.tamPoblacion = tam;
        this.semilla = semilla;
        this.datos = datos;
        if (generar) {
            generarPoblacion();
        }
    }

    @Override
    public String toString() {
        return "Poblacion{" + "poblacion=" + poblacion + ", tamPoblacion=" + tamPoblacion + ", semilla=" + semilla + ", datos=" + datos + '}';
    }

    public void addIndividuo(Individuo individuo) {
        poblacion.add(individuo);
    }

    public Individuo getIndividuo(int i) {
        return poblacion.get(i);
    }

    public int getTamPoblacion() {
        return tamPoblacion;
    }

    private void generarPoblacion() {
        for (int i = 0; i < tamPoblacion; i++) {
            poblacion.add(new Individuo(semilla, datos));
        }
    }
}
