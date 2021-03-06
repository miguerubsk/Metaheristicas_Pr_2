/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

//import java.util.Random;
import java.util.Vector;
import tools.CargaDatos;
import tools.Random;

/**
 *
 * @author miguerubsk
 */
public class Individuo {

    private Vector<Integer> cromosoma;
    private Vector<Boolean> marcados;
    private int tamCromosoma;
    private double coste;
    private Random aleatorio;
    private boolean calculado;
    private boolean elite;
    private CargaDatos datos;

    public Individuo(long semilla, CargaDatos datos) {
        this.datos = datos;
        marcados = new Vector<>();
        this.aleatorio = new Random(semilla);
        for(int i = 0; i < datos.getTamMatriz(); i++){
            marcados.add(Boolean.FALSE);
        }
        this.tamCromosoma = this.datos.getTamSolucion();
        this.cromosoma = generarCromosomaAleatorio(this.datos.getTamMatriz());
        this.coste = coste(this.datos);
        this.calculado = true;
        this.elite = false;
    }

    public void setCromosoma(Vector<Integer> cromosoma) {
        this.cromosoma = cromosoma;
        marcados.removeAllElements();
        for(int i = 0; i < datos.getTamMatriz(); i++){
            marcados.add(Boolean.FALSE);
        }
        for(int i = 0; i < cromosoma.size(); i++){
            marcados.setElementAt(true, this.cromosoma.get(i));
        }
        this.calculado = false;
    }

    public Vector<Integer> getCromosoma() {
        return cromosoma;
    }
    
    public void add(int ele){
        this.cromosoma.add(ele);
        this.marcados.setElementAt(true, ele);
    }

    public int getTamCromosoma() {
        return this.cromosoma.size();
    }

    public double getCoste() {
        return coste;
    }

    public boolean isCalculado() {
        return calculado;
    }

    public void actualizarCoste() {
        if (!calculado) {
            coste = coste(datos);
            calculado = true;
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
//            Integer elemento = aleatorio.nextInt(tamañoMatriz);
            Integer elemento = aleatorio.Randint(0, tamañoMatriz - 1);
            if (!crom.contains(elemento)) {
                crom.add(elemento);
                marcados.setElementAt(true, elemento);
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

    public boolean isElite() {
        return elite;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }

    public void setCalculado(boolean calculado) {
        this.calculado = calculado;
    }
    
    public boolean contains(int elemento){
        return marcados.get(elemento);
    }
    
    /**
     * @brief Operador de intercambio que cambia un elemento por otro en la
     * solucion
     * @param i indice del elemento que se quiere modificar
     * @param j elemento que se quiere añadir en la posicion i
     */
    public void intercambia(int i, int j) {
        this.cromosoma.setElementAt(j, i);
        this.marcados.setElementAt(false, this.cromosoma.get(i));
        this.marcados.setElementAt(true, j);
    }
    
}
