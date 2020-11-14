/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Random;
import java.util.Vector;
import tools.CargaDatos;
import tools.Configurador;

/**
 *
 * @author miguerubsk
 */
public class Genetico {

    private Random aleatorio;
    private int t = 0, conte = 0, peorCo1, peorCo2, mejorCo1, mejorCo2, posPeor1, posPeor2;
    float mejorCosteGlobal = -1;
    private Poblacion poblacion, nuevaPoblacion;
    private Vector<Integer> posi, mejor1, mejor2, mejorActual;
    private String operadorCruce;
    private CargaDatos datos;
    private Configurador config;
    private Integer m;
    private Long semilla;

    public Genetico(CargaDatos datos, Configurador config, Integer m, Long semilla, String operadorCruce) {
        this.poblacion = new Poblacion(semilla, datos, true, config);
        this.aleatorio = new Random(semilla);
        this.operadorCruce = operadorCruce;
        this.datos = datos;
        this.nuevaPoblacion = null;
        this.config = config;
        this.m = m;
        this.semilla = semilla;
    }

    private Vector<Individuo> seleccionTorneo() {

        Vector<Individuo> seleccion = new Vector<>();
        int p1, p2;

        do {
            p1 = aleatorio.nextInt(poblacion.getTamPoblacion());

            do {
                p2 = aleatorio.nextInt(poblacion.getTamPoblacion());
            } while (p2 == p1);

            if (poblacion.getIndividuo(p1).getCoste() > poblacion.getIndividuo(p2).getCoste()) {
                seleccion.add(poblacion.getIndividuo(p1));
            } else {
                seleccion.add(poblacion.getIndividuo(p2));
            }
        } while (seleccion.size() < m);

        return seleccion;

    }

    /**
     * @brief Funcion que muta un gen de un cromosoma
     * @param v Cromosoma que se quiere mutar
     * @param p posicion del gen que se quiere mutar
     * @param n rango de valores de la mutacion
     */
    private void Mutacion(Vector<Integer> v, int p, int n) {

        int x = 0;
        do {
            x = aleatorio.nextInt(n);
        } while (v.contains(x));

        intercambia(p, x, v);
    }

    void reparar2Puntos(Vector<Integer> a, double dist[][], int n) {
        try {
            Vector<Integer> r = new Vector<Integer>();

            int m = a.size();
            for (int i = 0; i < m; i++) {
                boolean enc = false;
                for (int j = 0; j < r.size(); j++) {
                    if (a.get(i) == r.get(j)) {
                        enc = true;
                        break;
                    }

                }
                if (!enc) {
                    r.add(a.get(i));
                }
            }
            int x = a.size() - r.size();
            for (int i = 0; i < x; i++) {
                int ele = MasAporta(dist, a, n);
            }
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.reparar2Puntos(): " + e.toString());
        }
    }

    //@TODO
    private void repararMPX(Vector<Integer> a, double dist[][], int m) {
        Vector<Integer> r;
        int dif = a.size() - m;
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

    private int MasAporta(double dist[][], Vector<Integer> vector, int m) {
        double peso = 0.0;
        double mayor = 0.0;
        int posMayor = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (vector.get(i) != vector.get(j)) {
                    peso += dist[vector.get(i)][vector.get(j)];
                }
            }

            if (peso < mayor) {
                mayor = peso;
                posMayor = i;
            }
            peso = 0.0;
        }
        return posMayor;
    }

    /**
     * @brief Cruza dos cromosomas usando el cruce en 2 puntos
     * @param individuoA Primer cromosoma a cruzar
     * @param individuoB Segundo cromosoma a cruzar
     */
    private void cruce2P(Individuo individuoA, Individuo individuoB) {
        try {
            Vector<Integer> r1 = new Vector<>(), r2 = new Vector<>();
            int tam = individuoA.getTamCromosoma();
            int p1, p2, aux;
            p1 = aleatorio.nextInt(tam);

            do {
                p2 = aleatorio.nextInt(tam);
            } while (p2 == p1);

            if (p1 > p2) {
                aux = p1;
                p1 = p2;
                p2 = aux;
            }

            for (int i = 0; i < p1; i++) {
                r1.add(individuoB.getCromosoma().get(i));
                r2.add(individuoA.getCromosoma().get(i));
            }
            for (int i = p1; i < p2; i++) {
                r1.add(individuoA.getCromosoma().get(i));
                r2.add(individuoB.getCromosoma().get(i));
            }
            for (int i = p2; i < individuoA.getTamCromosoma(); i++) {
                r1.add(individuoB.getCromosoma().get(i));
                r2.add(individuoA.getCromosoma().get(i));
            }

            individuoA.setCromosoma(r1);
            individuoA.setCromosoma(r2);
            nuevaPoblacion.addIndividuo(individuoA);
            nuevaPoblacion.addIndividuo(individuoB);
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.cruce2P(): " + e.toString());
        }
    }

    /**
     * @brief función que devuelve un hijo al cruzar dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param por probabilidad de que se realize el cruce
     * @return un cromosoma resultado de cruzar dos cromosomas
     */
    private Vector<Integer> obtenerHijoMPX(Vector<Integer> a, Vector<Integer> b, int por) {
        int p;
        Vector<Integer> aa = a, bb = b, r = new Vector<>();
        int tam = aa.size();
        int elegibles = tam * por / 100;
        int tamaP = tam;

        for (int i = 0; i < elegibles; i++) {
            p = aleatorio.nextInt(tamaP);
            tamaP--;
            r.add(aa.get(p));
            aa.remove(p);
        }

        for (int i = 0; i < tam; i++) {
            if (!r.contains(bb.get(i))) {
                r.add(b.get(i));
            }
        }
        return r;
    }

    /**
     * @brief funcion que cruza dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param por probabilidad de que se realize el cruce
     */
    private void cruceMPX(Vector<Integer> a, Vector<Integer> b, int por) {

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

    public void ejecutar() {

        switch (operadorCruce) {
            case "2P":
                int iteracion = 0;
                int contador = 0;
                while (iteracion < 50000) {
                    System.out.println(iteracion);
                    nuevaPoblacion = new Poblacion(semilla, datos, false, config);
                    contador = 0;

                    //Elite2
                    double mayor = 0;
                    Individuo mejor1 = null;
                    Individuo mejor2 = null;
                    for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                        if (poblacion.getIndividuo(i).getCoste() > mayor) {
                            mayor = poblacion.getIndividuo(i).getCoste();
                            mejor1 = poblacion.getIndividuo(i);
                        }
                    }

                    mayor = 0;
                    for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                        if (poblacion.getIndividuo(i).getCoste() > mayor && poblacion.getIndividuo(i) != mejor1) {
                            mayor = poblacion.getIndividuo(i).getCoste();
                            mejor2 = poblacion.getIndividuo(i);
                        }
                    }
                    //---------

                    Vector<Individuo> seleccion = seleccionTorneo();

                    for (int i = 0; i < seleccion.size(); i += 2) {
//                        System.out.println(i);
                        if (aleatorio.nextDouble() < config.getProb_Cruce()) {
                            cruce2P(seleccion.get(i), seleccion.get(i + 1));
                            reparar2Puntos(nuevaPoblacion.getIndividuo(contador).getCromosoma(), datos.getMatriz(), datos.getTamSolucion());
                            reparar2Puntos(nuevaPoblacion.getIndividuo(contador + 1).getCromosoma(), datos.getMatriz(), datos.getTamSolucion());
                            contador += 2;
                        } else {
                            nuevaPoblacion.addIndividuo(seleccion.get(i));
                            nuevaPoblacion.addIndividuo(seleccion.get(i + 1));
                        }
                    }

                    for (int i = 0; i < nuevaPoblacion.getTamPoblacion(); i++) {
                        for (int j = 0; j < nuevaPoblacion.getIndividuo(i).getTamCromosoma(); j++) {
                            if (aleatorio.nextDouble() < config.getProb_Mutacion()) {
                                Mutacion(nuevaPoblacion.getIndividuo(i).getCromosoma(), j, datos.getTamMatriz());
                                nuevaPoblacion.getIndividuo(i).setCalculado(false);
                            }
                        }
                    }

                    nuevaPoblacion.addIndividuo(mejor1);
                    nuevaPoblacion.addIndividuo(mejor2);
                    poblacion = nuevaPoblacion;

                    for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                        if (!poblacion.getIndividuo(i).isCalculado()) {
                            poblacion.getIndividuo(i).actualizarCoste();
                            iteracion++;
                        }
//                        System.out.println("Coste: " + nuevaPoblacion.getIndividuo(i).getCoste() + "Cromosoma: " + nuevaPoblacion.getIndividuo(i).getCromosoma().toString());
                    }
//                    System.out.println("----------------------------------------------------------------------------------------------------------------------");
                }

                break;
            case "MPX":
                break;
        }
        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
            System.out.println("Coste: " + nuevaPoblacion.getIndividuo(i).getCoste() + "Cromosoma: " + nuevaPoblacion.getIndividuo(i).getCromosoma().toString());
        }
    }
}
