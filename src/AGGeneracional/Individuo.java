/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Random;
import java.util.Vector;
import tools.CargaDatos;

/**
 *
 * @author miguerubsk
 */
public class Individuo {
    private Vector<Integer> cromosoma;
    private int tamCromosoma;
    private double coste;
    private Random aleatorio;
    private boolean calculado;
    private CargaDatos datos;
    
    public Individuo(long semilla, CargaDatos datos){
        this.datos = datos;
        this.aleatorio = new Random(semilla);
        this.tamCromosoma = this.datos.getTamSolucion();
        this.cromosoma = generarCromosomaAleatorio(this.datos.getTamMatriz());
        this.coste = coste(this.datos);
        this.calculado = true;
    }
    
    public void setCromosoma(Vector<Integer> cromosoma) {
        this.cromosoma = cromosoma;
        actualizarCoste();
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

    public boolean isCalculado() {
        return calculado;
    }
    
    public void actualizarCoste(){
        if(!calculado){
            coste = coste(datos);
        }
    }
    
    /**
     * @brief Genera una solucion aleatoria
     * @param tamañoSolucion tamaño de la solucion
     * @param tamañoMatriz tamaño de la matriz
     */
    private Vector<Integer> generarCromosomaAleatorio(int tamañoMatriz) {
        Integer generados = 0;
        Vector<Integer> crom = new Vector<Integer>();

        while (generados < tamCromosoma) {
            Integer elemento = aleatorio.nextInt(tamañoMatriz);
            if (!crom.contains(elemento)) {
                crom.add(elemento);
                generados++;
            }
        }
        return crom;
    }
    
    /**
     * @brief Función que calcula el coste de la solucion
     * @param matriz matriz de distancias
     * @param tamañoSolucion tamaño de la solucion
     * @return Coste de la solucion
     */
    private double coste(CargaDatos datos) {
        double coste = 0.0;

        for (int i = 0; i < tamCromosoma; i++) {
            for (int j = i + 1; j < tamCromosoma; j++) {
                coste += datos.getMatriz()[cromosoma.get(j)][cromosoma.get(i)];
            }
        }

        return coste;
    }
}
