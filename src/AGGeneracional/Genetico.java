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

    private final CargaDatos datos; //Datos para realizar la ejecucion
    private final Configurador config; //Archivo de configuracion
    private final Integer m; //Tamaño de la solucion
    private final Long semilla; //Semilla para inicializacion del aleatorio
    private final Integer numElite; //Numero de elementos que formaran la elite
    
    private final Random aleatorio; //Genera aleatorios
    private int t = 0, conte = 0, peorCo1, peorCo2, mejorCo1, mejorCo2, posPeor1, posPeor2;
    float mejorCosteGlobal = -1;
    private Poblacion poblacion, nuevaPoblacion; //Poblacion actual, y nueva poblacion con la que iremos trabajando
    private Vector<Integer> posi, mejor1, mejor2, mejorActual;
    private final String operadorCruce; //Operador de cruce que se usara en la ejecucion
    
    
    
    

    public Genetico(CargaDatos datos, Configurador config, Integer m, Long semilla, String operadorCruce, Integer numElite) {
        this.poblacion = new Poblacion(semilla, datos, true, config);
        this.aleatorio = new Random(semilla);
        this.operadorCruce = operadorCruce;
        this.datos = datos;
        this.nuevaPoblacion = null;
        this.config = config;
        this.m = m;
        this.semilla = semilla;
        this.numElite = numElite;
    }

    /**
     * @brief Realiza la ejecucion del algoritmo
     */
    public void ejecutar() {

        switch (operadorCruce) {
            case "2P":
                int iteracion = 0;
                int contador;
                /*Ejecutamos el algoritmo hasta que se complete el numero de iteraciones*/
                while (iteracion < config.getEvaluaciones()) {
                    nuevaPoblacion = new Poblacion(semilla, datos, false, config); //Creamos una nueva poblacion para trabajar sobre ella
                    contador = 0;

                    Vector<Individuo> elite = generarElite(); //Generamos la elite de la actual generacion 

                    Vector<Individuo> seleccion = seleccionTorneo(); //Realizamos la seleccion por torneo

                    /*Realizamos el cruce y su reparacion para cada uno de los elementos de la seleccion.+
                    Se realizará el cruce y reparacion con probabilidad 0.7.
                    Si no se realiza cruce, el individuo es añadido sin realizar su cruce.*/
                    for (int i = 0; i < seleccion.size(); i += 2) {
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

                    /*Realizamos la mutacion para cada elemento de los cromosomas de cada individuo.
                    Esta mutacion tiene una probabilidad de 0.05.*/
                    for (int i = 0; i < nuevaPoblacion.getTamPoblacion(); i++) {
                        for (int j = 0; j < nuevaPoblacion.getIndividuo(i).getTamCromosoma(); j++) {
                            if (aleatorio.nextDouble() < config.getProb_Mutacion()) {
                                Mutacion(nuevaPoblacion.getIndividuo(i).getCromosoma(), j, datos.getTamMatriz());
                                nuevaPoblacion.getIndividuo(i).setCalculado(false);
                            }
                        }
                    }

                    /*Añadimos los individuos elite*/
                    for (Individuo individuo : elite) {
                        nuevaPoblacion.addIndividuo(individuo);
                    }

                    /*Sustituimos la poblacion anterior con la nueva poblacion*/
                    poblacion = nuevaPoblacion;

                    /*Calculamos los costes de cada individuo si este no los tiene actualizados*/
                    for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                        if (!poblacion.getIndividuo(i).isCalculado()) {
                            poblacion.getIndividuo(i).actualizarCoste();
                            
                            iteracion++; //Sumamos una iteracion por cada calculo del coste
                        }
                    }
                }

                break;

            case "MPX":
                break;
        }

        System.out.println("Tamaño poblacion: " + poblacion.getTamPoblacion() + "\n");
        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
            System.out.println("Coste: " + nuevaPoblacion.getIndividuo(i).getCoste() + "\nCromosoma: " + nuevaPoblacion.getIndividuo(i).getCromosoma().toString());
        }
    }
    
    //Funciones auxiliares

    /**
     * @brief Realiza una seleccion por torneo con k = 2
     * @return vector con la seleccion de los individuos
     */
    private Vector<Individuo> seleccionTorneo() {

        Vector<Individuo> seleccion = new Vector<>();
        int p1, p2;

        do {
            do {
                p1 = aleatorio.nextInt(poblacion.getTamPoblacion());
                p2 = aleatorio.nextInt(poblacion.getTamPoblacion());
            } while (p2 == p1 && seleccion.contains(poblacion.getIndividuo(p1)) && seleccion.contains(poblacion.getIndividuo(p2)));

            if (poblacion.getIndividuo(p1).getCoste() > poblacion.getIndividuo(p2).getCoste()) {
                seleccion.add(poblacion.getIndividuo(p1));
            } else {
                seleccion.add(poblacion.getIndividuo(p2));
            }
        } while (seleccion.size() < m);

        return seleccion;
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
     * @brief funcion que cruza dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param por probabilidad de que se realize el cruce
     */
    private void cruceMPX(Vector<Integer> a, Vector<Integer> b, int por) {
        //@TODO
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

    /**
     * @brief Funcion que repara una solucion no factible
     * @param a Solucion a reparar
     * @param dist matriz de distancias
     * @param n tamaño solucion
     */
    private void reparar2Puntos(Vector<Integer> a, double dist[][], int n) {
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

    /**
     * @brief Funcion que obtiene la posicion de mayor aporte de la solucion
     * @param dist matriz de distancias
     * @param vector una solucion
     * @param m tamaño de la solucion
     * @return posicion de menor aporte
     */
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
     * @brief Operador de intercambio que cambia un elemento por otro en la
     * solucion
     * @param i indice del elemento que se quiere modificar
     * @param j elemento que se quiere añadir en la posicion i
     */
    private void intercambia(int i, int j, Vector<Integer> solucion) {
        solucion.setElementAt(j, i);
    }

    private Vector<Individuo> generarElite() {
        Integer generados = 0;
        Vector<Individuo> elite = new Vector<>();
        Vector<Integer> posiciones = new Vector<>();
        do {
            double mayor = 0;
            Integer mejor = null;

            for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                if (poblacion.getIndividuo(i).getCoste() > mayor && !poblacion.getIndividuo(i).isElite()) {
                    mayor = poblacion.getIndividuo(i).getCoste();
                    mejor = i;

                }
            }

            elite.add(poblacion.getIndividuo(mejor));
            poblacion.getIndividuo(mejor).setElite(true);
            posiciones.add(mejor);
            generados++;
        } while (generados < numElite);

        for (Integer posicion : posiciones) {
            poblacion.getIndividuo(posicion).setElite(false);
        }

        for (Individuo individuo : elite) {
            individuo.setElite(false);
        }
        return elite;
    }
}
