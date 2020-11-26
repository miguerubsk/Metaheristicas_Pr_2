/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AGGeneracional;

import java.util.Collections;
import java.util.Random;
import java.util.Vector;
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

    /**
     * @brief Constructor
     * @param datos archivo de datos
     * @param config archivo de configuracion
     * @param semilla semilla que se usara
     * @param operadorCruce operador de cruce a usar
     * @param numElite numero de elementos para la elite
     */
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
        int evaluaciones = 0;
        int iteracion = 0;
        long start = 0, stop = 0;
        try {
            start = System.currentTimeMillis();
            switch (operadorCruce) {
                case "2P":
                    int contador;
                    /*Ejecutamos el algoritmo hasta que se complete el numero de iteraciones*/
                    while (evaluaciones < config.getEvaluaciones()) {
                        log.escribir("NUMERO DE ITERACION: " + iteracion);
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
                                if(nuevaPoblacion.getIndividuo(contador).getCromosoma().size() != 50 || nuevaPoblacion.getIndividuo(contador + 1).getCromosoma().size() != 50){
                                    System.out.println("AGGeneracional.Genetico.ejecutar()");
                                }
                                reparar2Puntos(nuevaPoblacion.getIndividuo(contador), datos.getMatriz());
                                reparar2Puntos(nuevaPoblacion.getIndividuo(contador + 1), datos.getMatriz());
                                contador += 2;
                            } else {
                                nuevaPoblacion.addIndividuo(seleccion.get(i));
                                log.escribir("AÑADIDO INDIVIDUO SIN CRUCE: " + seleccion.get(i).getCromosoma());

                                nuevaPoblacion.addIndividuo(seleccion.get(i + 1));
                                log.escribir("AÑADIDO INDIVIDUO SIN CRUCE: " + seleccion.get(i + 1).getCromosoma());
                            }
                        }

                        /*Realizamos la mutacion para cada elemento de los cromosomas de cada individuo.
                        Esta mutacion tiene una probabilidad de 0.05.*/
                        for (int i = 0; i < nuevaPoblacion.getTamPoblacion(); i++) {
                            for (int j = 0; j < nuevaPoblacion.getIndividuo(i).getTamCromosoma(); j++) {
                                if (aleatorio.nextDouble() < config.getProb_Mutacion()) {
                                    mutacion(nuevaPoblacion.getIndividuo(i), j, datos.getTamMatriz());
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

                                evaluaciones++; //Sumamos una iteracion por cada calculo del coste
                            }
                        }

                        eliminarIndividuosSobrantes(poblacion); //Eliminamos los elementos que sobren de la solucion

                        log.escribir("NUMERO DE EVALUACIONES REALIZADAS: " + evaluaciones);
                        iteracion++;
                    }
                    break;

                case "MPX":
                    while (evaluaciones < config.getEvaluaciones()) {
                        log.escribir("NUMERO DE ITERACION: " + iteracion);
                        Vector<Individuo> elite = generarElite(); //Generamos la elite de la actual generacion 

                        /*Realizamos el cruce y su reparacion para cada uno de los elementos de la seleccion.+
                        Se realizará el cruce y reparacion con probabilidad 0.7.
                        Si no se realiza cruce, el individuo es añadido sin realizar su cruce.*/
                        for (int i = 0; i < poblacion.getTamPoblacion(); i += 2) {
                            cruceMPX(poblacion.getIndividuo(i).getCromosoma(), poblacion.getIndividuo(i + 1).getCromosoma(), (int) config.getProb_Cruce() * 100); //Realizamos el cruce

                            repararMPX(poblacion.getIndividuo(i).getCromosoma(), datos.getMatriz(), datos.getTamSolucion()); //Reparamos el primer elemento cruzado
                            repararMPX(poblacion.getIndividuo(i + 1).getCromosoma(), datos.getMatriz(), datos.getTamSolucion()); //Reparamos el segundo elemento cruzado
                        }

                        /*Realizamos la mutacion para cada elemento de los cromosomas de cada individuo.
                        Esta mutacion tiene una probabilidad de 0.05.*/
                        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                            for (int j = 0; j < poblacion.getIndividuo(i).getTamCromosoma(); j++) {
                                if (aleatorio.nextDouble() < config.getProb_Mutacion()) {
                                    mutacion(poblacion.getIndividuo(i), j, datos.getTamMatriz());
                                    poblacion.getIndividuo(i).setCalculado(false);
                                }
                            }
                        }

                        /*Añadimos los individuos elite*/
                        for (Individuo individuo : elite) {
                            poblacion.addIndividuo(individuo);
                        }

                        /*Calculamos los costes de cada individuo si este no los tiene actualizados*/
                        for (int i = 0; i < poblacion.getTamPoblacion(); i++) {
                            if (!poblacion.getIndividuo(i).isCalculado()) {
                                poblacion.getIndividuo(i).actualizarCoste();

                                evaluaciones++; //Sumamos una iteracion por cada calculo del coste
                            }
                        }

                        eliminarIndividuosSobrantes(poblacion); //Eliminamos los elementos que sobren de la solucion

                        log.escribir("NUMERO DE EVALUACIONES REALIZADAS: " + evaluaciones);
                        iteracion++;
                    }
                    break;
            }
            stop = System.currentTimeMillis();
            
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

        String info = "\n\n\n\n[EJECUCION TERMINADA]"
                + "\nArchivo: " + datos.getNombreFichero()
                + "\nSemilla: " + semilla
                + "\nOperadorCruce: " + operadorCruce
                + "\nNumElite: " + numElite
                + "\nMejor individuo: " + mejorIndividuo.getCromosoma().toString()
                + "\nCoste: " + mejorIndividuo.getCoste()
                + "\nTamañoSolucion: " + datos.getTamSolucion()
                + "\nTiempo: " + (stop-start) + " ms";

        log.escribirFinal(info);
        System.out.println(info);
    }

    //Funciones auxiliares
    /**
     * @brief Realiza una seleccion por torneo con k = 2
     * @return vector con la seleccion de los individuos
     */
    private Vector<Individuo> seleccionTorneo() {
        log.escribir("SELECCION POR TORNEO INICIADA (k=2)");

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

        log.escribirNoInfo("SELECCION TERMINADA\nELEMENTOS SELECCIONADOS:");
        for (Individuo individuo : seleccion) {
            log.escribirNoInfo("Cromosoma: " + individuo.getCromosoma().toString() + " Coste: " + individuo.getCoste());
        }

        return seleccion;
    }

    /**
     * @brief Cruza dos cromosomas usando el cruce en 2 puntos
     * @param individuoA Primer cromosoma a cruzar
     * @param individuoB Segundo cromosoma a cruzar
     */
    private void cruce2P(Individuo individuoA, Individuo individuoB) {
        try {
            Vector<Integer> nuevo1 = new Vector<>(), nuevo2 = new Vector<>();
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
                nuevo1.add(individuoB.getCromosoma().get(i));
                nuevo2.add(individuoA.getCromosoma().get(i));
            }
            for (int i = p1; i < p2; i++) {
                nuevo1.add(individuoA.getCromosoma().get(i));
                nuevo2.add(individuoB.getCromosoma().get(i));
            }
            for (int i = p2; i < individuoA.getTamCromosoma(); i++) {
                nuevo1.add(individuoB.getCromosoma().get(i));
                nuevo2.add(individuoA.getCromosoma().get(i));
            }

            Individuo individuoAux1 = new Individuo(semilla, datos);
            individuoAux1.setCromosoma(nuevo1);

            Individuo individuoAux2 = new Individuo(semilla, datos);
            individuoAux2.setCromosoma(nuevo2);

            nuevaPoblacion.addIndividuo(individuoAux1);
            nuevaPoblacion.addIndividuo(individuoAux2);

            log.escribirNoInfo("CRUCE TERMINADO\n" + "Cromosoma1: " + individuoAux1.getCromosoma().toString() + "\nCromosoma2: " + individuoAux2.getCromosoma().toString());
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.cruce2P(): " + e.toString());
        }
    }

    /**
     * @brief funcion que cruza dos cromosomas usando MPX
     * @param individuoA primer cromosoma a cruzar
     * @param individuoB segundo cromosoma a cruzar
     * @param prob probabilidad de que se realize el cruce
     */
    private void cruceMPX(Vector<Integer> individuoA, Vector<Integer> individuoB, int prob) {

        log.escribir("CRUCE INICIADO\n" + "Cromosoma padre 1: " + individuoA.toString() + "\nCromosoma padre 2: " + individuoB.toString());

        Vector<Integer> crom1 = obtenerHijoMPX(individuoA, individuoB, prob);
        Vector<Integer> crom2 = obtenerHijoMPX(individuoA, individuoB, prob);

        log.escribirNoInfo("CRUCE TERMINADO"
                + "\nHIJO 1: " + crom1.toString()
                + "\nHIJO 2: " + crom2.toString());

        individuoA = crom1;
        individuoB = crom2;
    }

    /**
     * @brief Funcion que muta un gen de un cromosoma
     * @param crom Cromosoma que se quiere mutar
     * @param pos posicion del gen que se quiere mutar
     * @param n rango de valores de la mutacion
     */
    private void mutacion(Individuo crom, int pos, int n) {
        log.escribir("MUTACION INICIADA" + "\nPosicion a mutar: " + pos + " Valor antes de mutacion: " + crom.getCromosoma().get(pos));

        int x = 0;
        do {
            x = aleatorio.nextInt(n);
        } while (crom.contains(x));

        crom.intercambia(pos, x);

        log.escribirNoInfo("MUTACION TERMINADA" + "\nPosicion mutada: " + pos + " Valor tras la mutacion: " + crom.getCromosoma().get(pos));
    }

    /**
     * @brief Funcion que repara una solucion no factible de 2P
     * @param individuo Solucion a reparar
     * @param dist matriz de distancias
     */
    private void reparar2Puntos(Individuo individuo, double dist[][]) {
        log.escribir("REPARACION INICIADA\n" + "Cromosoma a reparar: " + individuo.toString());
        try {
            Vector<Integer> r = new Vector<Integer>();

            for (int i = 0; i < individuo.getCromosoma().size(); i++) {
                if (!r.contains(individuo.getCromosoma().get(i))) {
                    r.add(individuo.getCromosoma().get(i));
                }
            }
//            Collections.sort(r);
            individuo.setCromosoma(r);
            int x = datos.getTamSolucion() - r.size();
            for (int i = 0; i < x; i++) {
                int ele = masAporta(dist, individuo);
                individuo.add(ele);
            }
        } catch (Exception e) {
            System.err.println("AGGeneracional.Genetico.reparar2Puntos(): " + e.toString());
        }

        log.escribirNoInfo("REPARACION TERMINADA\n" + "Cromosoma reparado: " + individuo.toString());
    }

    /**
     * @brief Funcion que repara una solucion no factible de MPX
     * @param crom solucion a reparar
     * @param dist matriz de distancias
     * @param m tamaño de la solucion
     */
    private void repararMPX(Vector<Integer> crom, double dist[][], int m) {
        log.escribir("REPARACION INICIADA\n" + "Cromosoma a reparar: " + crom.toString());

        int dif = crom.size() - m;
        for (int i = 0; i < dif; i++) {
            int p = menorAporte(crom.size(), dist, crom);
            crom.remove(p);
        }

        Vector<Integer> r = new Vector<Integer>();

        for (int i = 0; i < crom.size(); i++) {
            boolean enc = false;
            for (int j = 0; j < r.size(); j++) {
                if (crom.get(i) == r.get(j)) {
                    enc = true;
                    break;
                }

            }
            if (!enc) {
                r.add(crom.get(i));
            }
        }

        crom = r;

        log.escribirNoInfo("REPARACION TERMINADA\n" + "Cromosoma reparado: " + crom.toString());
    }

    /**
     * @brief función que devuelve un hijo al cruzar dos cromosomas usando MPX
     * @param padreA primer cromosoma a cruzar
     * @param padreB segundo cromosoma a cruzar
     * @param prob probabilidad de que se realize el cruce
     * @return un cromosoma resultado de cruzar dos cromosomas
     */
    private Vector<Integer> obtenerHijoMPX(Vector<Integer> padreA, Vector<Integer> padreB, int prob) {
        int p;
        Vector<Integer> aa = padreA, bb = padreB, r = new Vector<>();
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
                r.add(padreB.get(i));
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
     * @param individuo una solucion
     * @param m tamaño de la solucion
     * @return posicion de mayor aporte
     */
    private int masAporta(double dist[][], Individuo individuo) {
        double peso = 0.0;
        double mayor = 0.0;
        int posMayor = 0;
        for (int i = 0; i < datos.getTamMatriz(); i++) {
            if (!individuo.contains(i)) {
                for (int j = 0; j < individuo.getCromosoma().size(); j++) {
                    peso += dist[i][individuo.getCromosoma().get(j)];
                }

                if (peso > mayor) {
                    mayor = peso;
                    posMayor = i;
                }
                peso = 0.0;
            }
        }

        return posMayor;
    }

    

    /**
     * @brief Genera la elite de la poblacion actual con numElite
     * @return vector de individuos elite
     */
    private Vector<Individuo> generarElite() {
        Integer generados = 0;
        Vector<Individuo> elite = new Vector<>();
        Vector<Integer> posiciones = new Vector<>();
        log.escribir("GENERANDO ELITE");
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
            log.escribirNoInfo("ELITE " + generados + ". Cromosoma: " + poblacion.getIndividuo(mejor).getCromosoma() + " Coste: " + poblacion.getIndividuo(mejor).getCoste());
        } while (generados < numElite);

        for (Integer posicion : posiciones) {
            poblacion.getIndividuo(posicion).setElite(false);
        }

        for (Individuo individuo : elite) {
            individuo.setElite(false);
        }

        log.escribirNoInfo("ELITE GENERADA");
        return elite;
    }

    /**
     * @brief Obtiene el mejor individuo
     * @return mejor individuo de la poblacion
     */
    public Individuo getMejorIndividuo() {
        return mejorIndividuo;
    }

    /**
     * @brief Devuelve el tamaño de la poblacion actual
     * @return tamaño de la poblacion actual
     */
    public int getTamPoblacion() {
        return poblacion.getTamPoblacion();
    }

    /**
     * @brief Elimina los individuos que menor coste tienen y que sobran en la
     * solucion
     * @param poblacion una poblacion
     */
    private void eliminarIndividuosSobrantes(Poblacion poblacion) {
        log.escribir("ELIMINAR ELEMENTOS SOBRANTES DE LA POBLACION");

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

            log.escribirNoInfo("ELEMENTO ELIMINADO: " + poblacion.getIndividuo(pos).getCromosoma().toString() + " Coste: " + poblacion.getIndividuo(pos).getCoste());
            poblacion.removeIndividuo(pos);
        }
    }
}
