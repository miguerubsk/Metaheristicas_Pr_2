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
public class Individuo {
    private Vector<Integer> cromosoma;
    private int tamCromosoma;
    private double coste;
    private Random aleatorio;
    
    public Individuo(long semilla, int tamMatriz){
        cromosoma = generarCromosomaAleatorio(tamMatriz);
        coste = 0.0;
        aleatorio = new Random(semilla);
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
}
