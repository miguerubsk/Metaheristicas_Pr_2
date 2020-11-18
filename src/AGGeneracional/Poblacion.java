/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Vector;
import java.util.concurrent.*;
import tools.CargaDatos;
import tools.Configurador;
import tools.Random;

/**
 *
 * @author miguerubsk
 */
public class Poblacion {

    private Vector<Individuo> poblacion;
    private int tamPoblacion;
    private long semilla;
    private CargaDatos datos;
    private final Configurador config;
    private Random aleatorio;

    public Poblacion(long semilla, CargaDatos datos, boolean generar, Configurador config) {
        this.poblacion = new Vector<Individuo>();
        this.tamPoblacion = 0;
        this.semilla = semilla;
        this.datos = datos;
        this.config = config;
        this.aleatorio = new Random(1231123112);
        if (generar) {
            generarPoblacion();
        }
    }

    @Override
    public String toString() {
        return "Poblacion{" + "poblacion=" + poblacion + ", tamPoblacion=" + tamPoblacion + ", semilla=" + semilla + ", datos=" + datos + '}';
    }

    public void addIndividuo(Individuo individuo) throws Exception {
        poblacion.add(individuo);
        tamPoblacion++;
    }

    public Individuo getIndividuo(int i) {
        return poblacion.get(i);
    }

    public int getTamPoblacion() {
        return tamPoblacion;
    }

    public void removeIndividuo(int i) {
        poblacion.remove(i);
        tamPoblacion--;
    }
    
    private void generarPoblacion() {
        for (int i = 0; i < config.getTamPoblacion(); i++) {
            Individuo nuevoIndividuo = new Individuo(semilla + aleatorio.Randint(0, datos.getTamMatriz()), datos);
            poblacion.add(nuevoIndividuo);
            tamPoblacion++;
        }
    }
}
