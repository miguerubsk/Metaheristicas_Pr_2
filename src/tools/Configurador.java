/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Miguerubsk
 */
public class Configurador {

    private ArrayList<String> Ficheros, tipoCruce;
    private ArrayList<Long> Semillas;
    private ArrayList<Integer> Elite;
    private Integer Evaluaciones, TamPoblacion;
    private float Prob_Cruce, Prob_Mutacion;

    public Configurador(String ruta) {
        Ficheros = new ArrayList<>();
        tipoCruce = new ArrayList<>();
        Semillas = new ArrayList<>();
        Elite = new ArrayList<>();

        String linea;
        FileReader f = null;
        try {
            f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);

            while ((linea = b.readLine()) != null) {
                String[] split = linea.split("=");
                switch (split[0]) {
                    case "Archivos":
                        String[] vF = split[1].split(" ");
                        for (int i = 0; i < vF.length; i++) {
                            Ficheros.add(vF[i]);
                        }
                        break;
                    case "Semillas":
                        String[] vS = split[1].split(" ");
                        for (int i = 0; i < vS.length; i++) {
                            Semillas.add(Long.parseLong(vS[i]));
                        }
                        break;
                    case "Cruce":
                        String[] vA = split[1].split(" ");
                        for (int i = 0; i < vA.length; i++) {
                            tipoCruce.add(vA[i]);
                        }
                        break;
                    case "Elite":
                        String[] vE = split[1].split(" ");
                        for (int i = 0; i < vE.length; i++) {
                            Elite.add(Integer.parseInt(vE[i]));
                        }
                        break;
                    case "Evaluaciones":
                        Evaluaciones = Integer.parseInt(split[1]);
                        break;
                    case "Poblacion":
                        TamPoblacion = Integer.parseInt(split[1]);
                        break;
                    case "Prob_Cruce":
                        Prob_Cruce = Float.parseFloat(split[1]);
                        break;
                    case "Prob_Mutacion":
                        Prob_Mutacion = Float.parseFloat(split[1]);
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> getFicheros() {
        return Ficheros;
    }

    public ArrayList<String> getTipoCruce() {
        return tipoCruce;
    }

    public ArrayList<Long> getSemillas() {
        return Semillas;
    }

    public Integer getEvaluaciones() {
        return Evaluaciones;
    }

    public int getTamPoblacion() {
        return TamPoblacion;
    }

    public ArrayList<Integer> getElite() {
        return Elite;
    }

    public float getProb_Cruce() {
        return Prob_Cruce;
    }

    public float getProb_Mutacion() {
        return Prob_Mutacion;
    }

}
