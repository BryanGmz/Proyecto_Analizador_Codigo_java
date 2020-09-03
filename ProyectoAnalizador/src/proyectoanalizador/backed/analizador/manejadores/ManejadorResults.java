/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador.backed.analizador.manejadores;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;
import proyectoanalizador.backed.objetos.SimboloCodigo;
import proyectoanalizador.backed.objetos.analizador.lexico.Token;
import proyectoanalizador.backed.objetos.analizador.sintactico.NoTerminal;
import proyectoanalizador.backed.objetos.analizador.sintactico.Produccion;
import proyectoanalizador.backed.objetos.analizador.sintactico.Terminal;

/**
 *
 * @author bryangmz
 */
public class ManejadorResults implements Serializable{
    public static final String ENTERO = "Integer";
    public static final String CADENA = "String";
    public static final String REAL = "Double";
    public static final String OBJECT = "Object";
    public static final String RESULT = "Object RESULT = null;";
    public static final String CORCHETE_A = "{";
    public static final String CORCHETE_C = "}";
    private static ManejadorResults mr;
    private List<Terminal> listaTeminales;
    private List<NoTerminal> listaNoTeminales;
    private List<Produccion> producciones;
    private Stack<Token> tokenActualesTerminales;
    private Stack<Token> tokenActualesNoTerminales;
    
    private ManejadorResults(){} 
    
    public static ManejadorResults getInstancia(){
        if (mr == null) {
            mr = new ManejadorResults();
        } return mr;
    }
    
    public void constructor(List<Terminal> listaTerminales, List<NoTerminal> listaNoTerminales, List<Produccion> producciones){
        this.listaNoTeminales = listaNoTerminales;
        this.listaTeminales = listaTerminales;
        this.producciones = producciones;
        this.tokenActualesNoTerminales = new Stack<>();
        this.tokenActualesTerminales = new Stack<>();
    }
    
    //Agrega lso token terminales a una pila para cuando se realiza un reduce lo despila
    public void addPilatokenTerminales(Token token){
        if (!tokenActualesTerminales.contains(token)) {
            this.tokenActualesTerminales.push(token);
        }
    }
    
    public void addPilatokenNoTerminales(Token token){
        if (!tokenActualesNoTerminales.contains(token)) {
            this.tokenActualesNoTerminales.push(token);
        }
    }
    
    public NoTerminal regresaNoTerminal(String id){
        for (NoTerminal tnt : listaNoTeminales) {
            if (tnt.getId().equals(id)) {
                return tnt;
            }
        } return null;
    }
    
    public Terminal regresarTerminal(String id){
//        System.out.println("Lista " + listaNoTeminales);
//        System.out.println("Lista " + listaTeminales);
        for (Terminal tnt : listaTeminales) {
            if (tnt.getId().equals(id)) {
                return tnt;
            }
        } return null;
    }
    
    public Object valor(String tipo, Object val){
        switch (tipo) {
            case ENTERO:
                return "(" + ENTERO + ")" + " " + val;
            case CADENA:
                return "(" + CADENA + ")" + " \"" + val + "\"";
            case REAL:
                return "(" + REAL + ")" + " " + val;
            default:
                return val;
        }
    }
    
    public String generarCodigo(List<SimboloCodigo> listaTNT, String actionCode, String reglaSemantica){
        String codigoSalida = "";
        codigoSalida += "public class Reduce {";
        codigoSalida += "\n" + actionCode;
        codigoSalida += "\n\n\tpublic Object metodoAcciones() {";
        codigoSalida += "\n\t\tObject RESULT = null;";
        for (SimboloCodigo o : listaTNT) {
            if (o != null && o.getSimCodSem().getValorDevuelto() != null) {
                Terminal t = regresarTerminal(o.getSimCodSem().getTnt().toString());
//                System.out.println("T : " + t);
                if (o.getSimCodSem().getTnt() != null && t != null) {
//                    System.out.println("Terminal " + t.getId() + " Valor: " + o.getSimCodSem().getValorDevuelto() +  " - " + o.getValor());
                        codigoSalida += "\n\t\t" + getTipo(t.getId()) + 
                                " " + o.getSimCodSem().getValorDevuelto() + 
                                " = "  + valor(getTipo(t.getId()), o.getValor())+ ";";
                } else {
                    NoTerminal nt = regresaNoTerminal(o.getSimCodSem().getTnt().toString());
                    if (nt.isAceptacion() || nt.isLambda()) {
                        
                    } else {
//                        System.out.println("NoTerminal " + nt.getId() + " Valor: " + o.getSimCodSem().getValorDevuelto() +  " - " + o.getValor());
                            codigoSalida += "\n\t\t" +  getTipo(nt.getId())+ 
                                    " " + o.getSimCodSem().getValorDevuelto() + 
                                    " = "  + valor(getTipo(nt.getId()), o.getValor()) + ";";
                    }
                }
            }
        }
        if (reglaSemantica != null) {
            codigoSalida += "\n\t\t" + reglaSemantica;
        }
        codigoSalida += "\n\t\treturn RESULT;";
        codigoSalida += "\n\t}";
        codigoSalida += "\n}";
        return codigoSalida;
    }
    
    
    public String getTipo(String id){
        for (Terminal lt : listaTeminales){
            if (lt.getId().equals(id) && lt.getTipo() != null) {
                switch (lt.getTipo()) {
                    case "entero":
                        return ENTERO;
                    case "real":
                        return REAL;
                    case "cadena":
                        return CADENA;
                    default:
                        break;
                }
            }
        }
        for (NoTerminal lnt : listaNoTeminales) {
            if (lnt.getId().equals(id)) {
                if (lnt.getTipo() != null) {
                    switch (lnt.getTipo()) {
                    case "entero":
                        return ENTERO;
                    case "real":
                        return REAL;
                    case "cadena":
                        return CADENA;
                    default:
                        break;
                }
                }
            }
        } 
        return OBJECT;
    }
    
//    public String getMetodo(Produccion produccion){
//        String codigo = "";
//        
//    }
    
}