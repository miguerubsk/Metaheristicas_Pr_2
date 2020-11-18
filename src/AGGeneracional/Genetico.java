/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import tools.CargaDatos;
import tools.Configurador;
import tools.GuardarLog;

/**
 *
 * @author miguerubsk
 */
public class Genetico {

    private final CargaDatos datos; //Datos para realizar la ejecucion
    private final Configurador config; //Archivo de configuracion
    private final Integer tamSolucion; //Tamaño de la solucion
    private final Long semilla; //Semilla para inicializacion del aleatorio
    private final Integer numElite; //Numero de elementos que formaran la elite

    private final Random aleatorio; //Genera aleatorios
    private Poblacion poblacion, nuevaPoblacion; //Poblacion actual, y nueva poblacion con la que iremos trabajando
    private final String operadorCruce; //Operador de cruce que se usara en la ejecucion
    private Individuo mejorIndividuo;
    private final GuardarLog log;

    public Genetico(CargaDatos datos, Configurador config, Long semilla, String operadorCruce, Integer numElite) {
        this.poblacion = new Poblacion(semilla, datos, true, config);
        this.aleatorio = new Random(semilla);
        this.operadorCruce = operadorCruce;
        this.datos = datos;
        this.nuevaPoblacion = null;
        this.config = config;
        this.tamSolucion = datos.getTamSolucion();
        this.semilla = semilla;
        this.numElite = numElite;

        String ruta = operadorCruce + "_elite" + numElite + "_" + datos.getNombreFichero() + "_" + semilla;
        String info = "[EJECUCION INICIADA]\n"
                + "Archivo: " + datos.getNombreFichero()
                + "\nSemilla: " + semilla
                + "\nOperadorCruce: " + operadorCruce
                + "\nNumElite: " + numElite
                + "\nTamaño matriz: " + datos.getTamMatriz()
                + "\nTamañoSolucion: " + datos.getTamSolucion()
                + "\nTamañoPoblacion: " + config.getTamPoblacion();

        this.log = new GuardarLog(ruta, info, operadorCruce);
    }

    /**
     * @brief Realiza la ejecucion del algoritmo
     */
    public void ejecutar() throws Exception {
        int iteracion = 0;
        try {
            switch (operadorCruce) {
                case "2P":
                    int contador;
                    /*Ejecutamos el algoritmo hasta que se complete el numero de iteraciones*/
                    while (iteracion < 300) {
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
                                    mutacion(nuevaPoblacion.getIndividuo(i).getCromosoma(), j, datos.getTamMatriz());
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

                        eliminarIndividuosSobrantes(poblacion);
                    }

                    break;

                case "MPX":
                    while (iteracion < 300) {
                        Vector<Individuo> elite = generarElite(); //Generamos la elite de la actual generacion 

                        for (int i = 0; i < poblacion.getTamPoblacion(); i += 2) {
                            cruceMPX(poblacion.getIndividuo(i).getCromosoma(), poblacion.getIndividuo(i + 1).getCromosoma(), (int) config.getProb_Cruce() * 100);

                            repararMPX(poblacion.getIndividuo(i).getCromosoma(), datos.getMatriz(), datos.getTamSolucion());
                            repararMPX(poblacion.getIndividuo(i + 1).getCromosoma(), datos.getMatriz(), datos.getTamSolucion());
                        }

                        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                            for (int j = 0; j < poblacion.getIndividuo(i).getTamCromosoma(); j++) {
                                if (aleatorio.nextDouble() < config.getProb_Mutacion()) {
                                    mutacion(poblacion.getIndividuo(i).getCromosoma(), j, datos.getTamMatriz());
                                    poblacion.getIndividuo(i).setCalculado(false);
                                }
                            }
                        }

                        /*Añadimos los individuos elite*/
                        for (Individuo individuo : elite) {
                            poblacion.addIndividuo(individuo);
                        }

                        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                            if (!poblacion.getIndividuo(i).isCalculado()) {
                                poblacion.getIndividuo(i).actualizarCoste();

                                iteracion++; //Sumamos una iteracion por cada calculo del coste
                            }
                        }

                        eliminarIndividuosSobrantes(poblacion);
                        iteracion -= numElite;
                    }

                    break;
            }

        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.ejecutar(): " + e.getMessage());
        }

        /*Guardamos el individuo con mejor coste como el mejor individuo de toda la poblacion*/
        double mayor = 0;
        Integer mejor = null;

        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
            if (poblacion.getIndividuo(i).getCoste() > mayor) {
                mayor = poblacion.getIndividuo(i).getCoste();
                mejor = i;

            }
        }

        mejorIndividuo = poblacion.getIndividuo(mejor);
        Collections.sort(mejorIndividuo.getCromosoma());

        String info = "[EJECUCION TERMINADA]\n"
                + "Archivo: " + datos.getNombreFichero()
                + "\nSemilla: " + semilla
                + "\nOperadorCruce: " + operadorCruce
                + "\nNumElite: " + numElite
                + "\nMejor individuo: " + mejorIndividuo.getCromosoma().toString()
                + "\nCoste: " + mejorIndividuo.getCoste()
                + "\nTamañoSolucion: " + datos.getTamSolucion();

        log.escribirFinal(info);
        System.out.println(info);
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
        } while (seleccion.size() < config.getTamPoblacion());

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

            log.escribir("CRUCE INICIADO\n" + "Cromosoma1: " + individuoA.getCromosoma().toString() + "\nCromosoma2: " + individuoB.getCromosoma().toString()
                    + "\nPuntos: " + p1 + "|" + p2);

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

            Individuo individuoAux1 = new Individuo(semilla, datos);
            individuoAux1.setCromosoma(r1);
            Individuo individuoAux2 = new Individuo(semilla, datos);
            individuoAux2.setCromosoma(r2);
            nuevaPoblacion.addIndividuo(individuoAux1);
            nuevaPoblacion.addIndividuo(individuoAux2);

            log.escribirNoInfo("CRUCE TERMINADO\n" + "Cromosoma1: " + individuoAux1.getCromosoma().toString() + "\nCromosoma2: " + individuoAux2.getCromosoma().toString());
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.cruce2P(): " + e.toString());
        }
    }

    /**
     * @brief funcion que cruza dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param prob probabilidad de que se realize el cruce
     */
    private void cruceMPX(Vector<Integer> a, Vector<Integer> b, int prob) {
        Vector<Integer> crom1 = obtenerHijoMPX(a, b, prob);
        Vector<Integer> crom2 = obtenerHijoMPX(a, b, prob);

        a = crom1;
        b = crom2;
    }

    /**
     * @brief Funcion que muta un gen de un cromosoma
     * @param v Cromosoma que se quiere mutar
     * @param p posicion del gen que se quiere mutar
     * @param n rango de valores de la mutacion
     */
    private void mutacion(Vector<Integer> v, int p, int n) {
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

            for (int i = 0; i < a.size(); i++) {
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
                int ele = masAporta(dist, a, n);
                a.add(a.get(ele));
            }
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.reparar2Puntos(): " + e.toString());
        }
    }

    //@TODO
    private void repararMPX(Vector<Integer> a, double dist[][], int m) {
        int dif = a.size() - m;
        for (int i = 0; i < dif; i++) {
            int p = menorAporte(a.size(), dist, a);
            a.remove(p);
        }

        Vector<Integer> r = new Vector<Integer>();

        for (int i = 0; i < a.size(); i++) {
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

        a = r;
    }

    /**
     * @brief función que devuelve un hijo al cruzar dos cromosomas usando MPX
     * @param a primer cromosoma a cruzar
     * @param b segundo cromosoma a cruzar
     * @param prob probabilidad de que se realize el cruce
     * @return un cromosoma resultado de cruzar dos cromosomas
     */
    private Vector<Integer> obtenerHijoMPX(Vector<Integer> a, Vector<Integer> b, int prob) {
        int p;
        Vector<Integer> aa = a, bb = b, r = new Vector<>();
        int tam = aa.size();
        int elegibles = tam * prob / 100;
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
     * @return posicion de mayor aporte
     */
    private int masAporta(double dist[][], Vector<Integer> vector, int m) {
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

    public Individuo getMejorIndividuo() {
        return mejorIndividuo;
    }

    public int getTamPoblacion() {
        return poblacion.getTamPoblacion();
    }

    private void eliminarIndividuosSobrantes(Poblacion poblacion) {
        int dif = poblacion.getTamPoblacion() - config.getTamPoblacion();
        for (int i = 0; i < dif; i++) {
            double menorCoste = 999999999;
            int pos = 0;
            for (int j = 0; j < poblacion.getTamPoblacion(); j++) {

                if (poblacion.getIndividuo(j).getCoste() < menorCoste) {
                    menorCoste = poblacion.getIndividuo(j).getCoste();
                    pos = j;
                }
            }
            poblacion.removeIndividuo(pos);
        }
    }
}
