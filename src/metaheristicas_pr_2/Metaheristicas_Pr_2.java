/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metaheristicas_pr_2;

import AGGeneracional.Genetico;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.CargaDatos;
import tools.Configurador;

/**
 *
 * @author Miguerubsk
 */
public class Metaheristicas_Pr_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Cargamos el archivo de configuracion
        Configurador config = new Configurador("config.txt");

        //Cargamos los ficheros de datos
        ArrayList<CargaDatos> Datos = new ArrayList<>();
        for (int i = 0; i < config.getFicheros().size(); i++) {
            Datos.add(new CargaDatos(config.getFicheros().get(i)));
        }

        for (int i = 0; i < config.getTipoCruce().size(); i++) {
            for (int j = 0; j < config.getElite().size(); j++) {
                for (int k = 0; k < Datos.size(); k++) {
                    for (int l = 0; l < config.getSemillas().size(); l++) {
                        Genetico genetico = new Genetico(Datos.get(k), config, config.getSemillas().get(l), config.getTipoCruce().get(i), config.getElite().get(j));
                        try {
                            genetico.ejecutar();
                        } catch (Exception ex) {
                            Logger.getLogger(Metaheristicas_Pr_2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}
