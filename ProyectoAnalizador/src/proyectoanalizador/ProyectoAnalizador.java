/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador;

import proyectoanalizador.gui.Frame;

/**
 *
 * @author bryan
 */
public class ProyectoAnalizador {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        GeneradorArchivo archivos = GeneradorArchivo.getGeneradorArchivos();
//        archivos.generador();
            Frame frame = new Frame();
            frame.setVisible(true);
    }
    
}
