/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador.backed.analizador.manejadores;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

/**
 *
 * @author bryangmz
 */
public class ManejadorGeneradorClases implements Serializable{
    
    private static ManejadorGeneradorClases generadorClases;
    
    private ManejadorGeneradorClases(){}
    
    public static ManejadorGeneradorClases getInstancia(){
        if (generadorClases == null) {
            generadorClases = new ManejadorGeneradorClases();
        } return generadorClases;
    }
    
    public Object pruebaJanino(String codigo, String nombreClase, String nombreMetodoInicar) throws CompileException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        SimpleCompiler simpleCompiler = new SimpleCompiler();
        simpleCompiler.cook(new StringReader(codigo));
        Class clase  = simpleCompiler.getClassLoader().loadClass(nombreClase);
        Object carne = clase.newInstance();
        java.lang.reflect.Method method = clase.getDeclaredMethod(nombreMetodoInicar);
        Object resultado = method.invoke(carne, new Object[0]);
        return resultado;
    }
}
