/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metaheristicas_pr_2;

import java.util.Random;
import java.util.Vector;

/**
 *
 * @author miguerubsk
 */
public class Genetico {
    private Random aleatorio;
    int t = 0, conte = 0, peorCo1, peorCo2, mejorCo1, mejorCo2, posPeor1, posPeor2;
    float mejorCosteGlobal = -1;
    Vector<Vector<Integer>> cromosomas, nuevag;
    Vector<Float> costes;
    Vector<Integer> posi, mejor1, mejor2, mejorActual;
    
    
    public Genetico(){
        
    }
    
    
    
    
    /**
     * @brief Genera una solucion aleatoria
     * @param tamañoSolucion tamaño de la solucion
     * @param tamañoMatriz tamaño de la matriz
     */
    private void generarSolucionAleatoria(int tamañoSolucion, int tamañoMatriz, Vector<Integer> solucion) {
        Integer generados = 0;

        while (generados < tamañoSolucion) {
            Integer elemento = aleatorio.nextInt(tamañoMatriz);
            if (!solucion.contains(elemento)) {
                solucion.add(elemento);
                generados++;
            }
        }
    }
    
    private void Mutacion(Vector<Integer> v, int p, int n){
        
        int x = 0;
        do{
            x = aleatorio.nextInt(n);
        }while(v.contains(x));
        
        intercambia(p, n, v);
    }
    
    private void cruceDosPuntos(Vector<Integer> a, Vector<Integer> b){
        Vector<Integer> r1,r2;
        int tam = a.size();
        int p1, p2, aux;
        p1 = aleatorio.nextInt(tam);
        
        
        do{
            p2 = aleatorio.nextInt(tam);
        }while(p2==p1);
        
        if(p1>p2){
            aux=p1;
            p1=p2;
            p2=aux;
        }
        
    }
    
    private Vector<Integer> obtenerHijoMPX(Vector<Integer> a, Vector<Integer> b, int por){
        int p;
        Vector<Integer> aa = a, bb = b, r = new Vector<>();
        int tam = aa.size();
        int elegibles=tam*por/100;
        int tamaP=tam;
        
        for(int i = 0; i < elegibles; i++){
            p = aleatorio.nextInt(tamaP);
            tamaP--;
            r.add(aa.get(p));
            aa.remove(p);
        }
        
        for(int i = 0; i < tam; i++){
            if(!r.contains(bb.get(i)))
                r.add(b.get(i));
        }
        return r;
    }
    
    private void cruceMPX(){
        
    }
    
    /**
     * @brief Operador de intercambio que cambia un elemento por otro en la
     * solucion
     * @param i indice del elemento que se quiere modificar
     * @param j elemento que se quiere añadir en la posicion i
     */
    private void intercambia(int i, int j, Vector<Integer> solucion) {
        solucion.setElementAt(j, i);
    }
}
