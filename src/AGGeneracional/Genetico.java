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
     * @brief Funcion que muta un gen de un cromosoma
     * @param v Cromosoma que se quiere mutar
     * @param p posicion del gen que se quiere mutar
     * @param n rango de valores de la mutacion
     */
    private void Mutacion(Vector<Integer> v, int p, int n){
        
        int x = 0;
        do{
            x = aleatorio.nextInt(n);
        }while(v.contains(x));
        
        intercambia(p, x, v);
    }
    
    void reparar2Puntos(Vector<Integer> a, int Dist[][], int n){
        Vector<Integer> r = new Vector<Integer>();
        
        int m = a.size();
        for (int i = 0; i < m; i++) {
            boolean enc=false;
            for (int j = 0; j < r.size(); j++) {
                if(a.get(i) == r.get(j)){
                    enc = true;
                    break;
                }
                
            }
            if(!enc)
                r.add(a.get(i));
        }
        int x = a.size() - r.size();
        for (int i = 0; i < x; i++) {
            int ele = MasAporta(Dist, a, n);
        }
    }
    
    private void repararMPX(Vector<Integer> a, double dist[][], int m){
        Vector<Integer> r;
        int dif = a.size()-m;
        for (int i = 0; i < dif; i++) {
            int p = menorAporte(a.size(), dist, a);
            
        }
    }
    
    /**
     * @brief Funcion que obtiene la posicion de menor aporte de la solucion
     * @param m tamaño de la solucion
     * @param dist matriz de distancias
     * @param vector una solucion
     * @return posicion de menor aporte
     */
    private Integer menorAporte(int m, double[][] dist, Vector<Integer> vector) {
        double peso = 0.0;
        Integer posMenor = 0;
        double menor = 999999999;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (vector.get(i) != vector.get(j)) {
                    peso += dist[vector.get(i)][vector.get(j)];
                }
            }

            if (peso < menor) {
                menor = peso;
                posMenor = i;
            }
            peso = 0.0;
        }

        return posMenor;
    }
    
    private int MasAporta(int dist[][], Vector<Integer> a, int n){
        double mayorDist = 0.0;
        double d = 0.0;
        int punto = 0;
        for (long i = 0; i < n; i++) {
            //TODO        
        }
        return punto;
    }
    
    /**
     * @brief Cruza dos cromosomas usando el cruce en 2 puntos
     * @param a Primer cromosoma a cruzar
     * @param b Segundo cromosoma a cruzar
     * 
     */
    private void cruce2P(Vector<Integer> a, Vector<Integer> b){
        Vector<Integer> r1 = new Vector<>(),r2 = new Vector<>();
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
        
        for (int i = 0; i < p1; i++){
            r1.add(b.get(i));
            r2.add(a.get(i));
        }
        for (int i = p1; i < p2; i++){
            r1.add(a.get(i));
            r2.add(b.get(i));
        }
        for (int i = p2; i < a.size(); i++){
            r1.add(b.get(i));
            r2.add(a.get(i));
        }
        
        a.clear();
        b.clear();
        a=r1;
        b=r2;
        
    }
    
    /**
     * @brief función que devuelve un hijo al cruzar dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param por probabilidad de que se realize el cruce
     * @return un cromosoma resultado de cruzar dos cromosomas
     */
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
    
    /**
     * @brief funcion que cruza dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param por probabilidad de que se realize el cruce
     */
    private void cruceMPX(Vector<Integer> a, Vector<Integer> b, int por){
        
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
