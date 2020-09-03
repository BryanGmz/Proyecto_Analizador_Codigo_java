/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador.backed.objetos.analizador.sintactico;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import proyectoanalizador.backed.objetos.analizador.lexico.AnalizadorLexico;
import proyectoanalizador.backed.objetos.analizador.lexico.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.codehaus.commons.compiler.CompileException;
import proyectoanalizador.backed.analizador.manejadores.ManejadorGeneradorClases;
import proyectoanalizador.backed.analizador.manejadores.ManejadorResults;
import proyectoanalizador.backed.objetos.SimboloCodigo;
import proyectoanalizador.gui.Frame;

/**
 *
 * @author bryan
 */
public class Pila implements Serializable{
     
    private final AnalizadorLexico analizadorLexico;
    private final Stack<Integer> pilaEstados;
    private final Stack<String> pilaSimbolos;
    private final List<Estado> listaEstado;
    private final List<String> registroEstados;
    private final List<String> registroSimbolos;
    private final List<String> registroAcciones;
    private List<Produccion> produccion;
    private int conflicto;
    private List<Terminal> listaTerminales;
    private List<NoTerminal> listaNoTerminales;
    private String actionCode;
    private ManejadorResults manejadorResults;
    private Stack<SimboloCodigo> simbolos;
    private ManejadorGeneradorClases clases;
    
    public Pila(AnalizadorLexico analizadorLexico, List<Estado> estado, String actionCode) {
        this.actionCode = actionCode;
        this.analizadorLexico = analizadorLexico;
        this.pilaEstados = new Stack<>();
        this.pilaSimbolos = new Stack<>();
        this.registroAcciones = new ArrayList<>();
        this.registroEstados = new ArrayList<>();
        this.simbolos = new Stack<>();
        this.registroSimbolos = new ArrayList<>();
        this.listaEstado = estado;
        this.conflicto = -1;
        this.manejadorResults = ManejadorResults.getInstancia();
        this.clases = ManejadorGeneradorClases.getInstancia();
    }

    public List<Produccion> getProduccion() {
        return produccion;
    }

    public void setProduccion(List<Produccion> produccion) {
        this.produccion = produccion;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public List<String> getRegistroEstados() {
        return registroEstados;
    }

    public List<String> getRegistroSimbolos() {
        return registroSimbolos;
    }

    public List<String> getRegistroAcciones() {
        return registroAcciones;
    }

    public List<Terminal> getListaTerminales() {
        return listaTerminales;
    }

    public void setListaTerminales(List<Terminal> listaTerminales) {
        this.listaTerminales = listaTerminales;
    }

    public List<NoTerminal> getListaNoTerminales() {
        return listaNoTerminales;
    }

    public void setListaNoTerminales(List<NoTerminal> listaNoTerminales) {
        this.listaNoTerminales = listaNoTerminales;
    }
    
    public void pilas(){
        Frame.cleanErrores();
        this.manejadorResults.constructor(listaTerminales, listaNoTerminales, produccion);
        registroAcciones.clear();
        registroEstados.clear();
        registroSimbolos.clear();
        simbolos.clear();
        pilaEstados.clear();
        pilaSimbolos.clear();
        int estadoActual = 1;
        boolean fin = true;
        pilaEstados.push(estadoActual);
        Token enTurno = analizadorLexico.getToken();
        TNT gtr = retornarInterseccion (getEstado(estadoActual), enTurno);
        registroEstados.add(getPilaEstados());
        registroSimbolos.add(getPilaSimbolos());
        while(fin){
            if(gtr == null && enTurno.getId().equals("$")) {
                fin = false;
                break;
            }
            if (gtr != null) {
                if(gtr.getShift() != null) {
                    pilaEstados.push(gtr.getShift().getShift());
                    pilaSimbolos.push(gtr.getIdEstado());
                    registroEstados.add(getPilaEstados());
//                    System.out.println(getPilaEstados());
                    registroSimbolos.add(getPilaSimbolos());
                    registroAcciones.add("Shift ( " + estadoActual + ", " + enTurno.getId() + ")");
                    simbolos.push(new SimboloCodigo(new SimCodSem(gtr.getShift().getProduccion(), enTurno.getId(), ""), enTurno.getValor()));
                    enTurno = analizadorLexico.getToken();
                    estadoActual = pilaEstados.peek();
                    gtr = retornarInterseccion(getEstado(pilaEstados.peek()), enTurno);
                } else if(gtr.getGoTo() != null) {
                    pilaEstados.push(gtr.getGoTo().getGoTo());
                    registroEstados.add(getPilaEstados());
//                    System.out.println(getPilaEstados());
                    registroSimbolos.add(getPilaSimbolos());
                    registroAcciones.add("Goto ( " + estadoActual + ", " + pilaSimbolos.peek() + ")");
//                    System.out.println("Goto ( " + estadoActual + ", " + pilaSimbolos.peek() + ")");
                    estadoActual = pilaEstados.peek();
                    gtr = retornarInterseccion(getEstado(pilaEstados.peek()), enTurno);
                } else if(gtr.getReview() != null) {
//                    manejadorResults.addPilatokenTerminales(enTurno);
                    review(gtr);
                    registroEstados.add(getPilaEstados());
                    registroSimbolos.add(getPilaSimbolos());
//                    System.out.println("\n\nReview: " + getReglaSemantica(gtr.getReview().getIdProduccion()));
//                    printds(listaValoresDeclarar(gtr.getReview().getProduccionReview()));
                    SimboloCodigo codigo = new SimboloCodigo(null, null);
                    String aC = "";
                    try {
                        aC = (manejadorResults.generarCodigo(listaParametros(gtr.getReview().getProduccionReview()), actionCode, getReglaSemantica(gtr.getReview().getIdProduccion())));
                        Object cod = clases.pruebaJanino(aC, "Reduce", "metodoAcciones");
                        System.out.println("\n\nTERMINAL:\n");
                        codigo.setSimCodSem(new SimCodSem(gtr.getReview().getProduccionReview(), gtr.getReview().getProduccionReview().getNoTerminal().getId(), ""));
                        codigo.setValor(cod);
                        simbolos.push(codigo);
                    } catch (CompileException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
                        JOptionPane.showMessageDialog(null, "Error, semantico (Codigo Java). Revisa el area de errores para mas inf.", "ERROR", JOptionPane.ERROR_MESSAGE);
                        Frame.addError("ERROR: Codigo Java: ");
                        Frame.addError("Estado actual: " + estadoActual);
                        Frame.addError("Token actual(ID): " + enTurno.getId());
                        Frame.addError("Token actual(Valor): " + enTurno.getValor());
                        Frame.addError("Produccion Reduce: " + gtr.getReview().getProduccionReview().produccion());
                        Frame.addError("\nDESCRIPCION: ");
                        Frame.addError(ex.toString());
                        Frame.addError(ex.getMessage());
                        Frame.addError(ex.getLocalizedMessage());
                        Frame.addError("Codigo Java: \n" + aC);
                        Frame.addError("\n");
                        Frame.addError("\n");
                    }
                    registroAcciones.add("Review ( " + estadoActual + ", " + enTurno.getId() + ") " + gtr.getReview().getProduccionReview().produccion());
                    estadoActual = pilaEstados.peek();
                    gtr = retornarInterseccion(getEstado(pilaEstados.peek()), new Token(pilaSimbolos.peek(), ""));
                } else if(gtr.isAceptar()) {
                    registroAcciones.add("Aceptado");
                    fin = false;
                    pilaEstados.removeAllElements();
                    break;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if(enTurno.getId().equals("$") && pilaEstados.empty()){
            if (!simbolos.empty() && simbolos.peek().getValor() != null) {
                JOptionPane.showMessageDialog(null, "Valor: " + simbolos.peek().getValor());
            } else {
                System.out.println("");
                System.out.println("");
                System.out.println("TERMINAL: ");
            }
            JOptionPane.showMessageDialog(null, "Gramatica Aceptada, puedes ver en el apartado de pila para ver el procedimineto.");
            System.out.println("Aceptado");
     
        } else {
            JOptionPane.showMessageDialog(null, "Error en la entrada, no se pudo reconocer el Token: <" + enTurno.getId() + "> Valor: <" + enTurno.getValor() 
                    + "> en el Estado: " + estadoActual, "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("No aceptado");
        }
//        print();
    }
    
    //Metodo que enviare
    
    private List<SimboloCodigo> listaParametros(Produccion p){
        List<SimboloCodigo> pila = new ArrayList<>();
        Object o = p.getProduccion();
        while (o != null && !simbolos.empty()) {
            if (o instanceof Terminal) {
                simbolos.peek().getSimCodSem().setValorDevuelto(((Terminal) o).getValorDevuelto());
                o = ((Terminal) o).getSiguiente();
                if (!simbolos.isEmpty()) {
                    pila.add(simbolos.pop());
                }
            } else {
                simbolos.peek().getSimCodSem().setValorDevuelto(((NoTerminal) o).getValorDevuelto());
                o = ((NoTerminal) o).getSiguiente();
                if (!simbolos.isEmpty()) {
                    pila.add(simbolos.pop());
                }
            }
        } return pila;
    }
    
    private void review(TNT tnt) {
        Object p = tnt.getReview().getProduccionReview().getProduccion();
        Stack<String> pR = new Stack<>();
        while(p != null){
            pR.push(p.toString());
            if (p instanceof Terminal) {
                p = ((Terminal) p).getSiguiente();
            } else {
                p = ((NoTerminal) p).getSiguiente();   
            }
        }
        while(!pR.isEmpty() && !pilaSimbolos.isEmpty()) {
            pilaSimbolos.pop();
            pR.pop();
            pilaEstados.pop();
        }
        pilaSimbolos.push(tnt.getReview().getProduccionReview().getNoTerminal().getId());
    }

    private TNT retornarInterseccion(Estado estado, Token token){
        for(TNT tnt : estado.getEstados()){
            if(token == null) {
                if (tnt.getIdEstado().equals("$")) {
                    return tnt;
                }
            } else {
                if (tnt.getIdEstado().equals(token.getId())) {
                    return tnt;
                }
            }
        } return null;
    }

    private String getPilaSimbolos(){
        String aux = "";
        for(int i = 0; i < pilaSimbolos.size(); i++){
            aux += pilaSimbolos.get(i) + " ";
        } return aux;
    }

    private String getPilaEstados(){
        String aux = "";
        for(int i = 0; i < pilaEstados.size(); i++){
            aux += Integer.toString(pilaEstados.get(i)) + " ";
        } return aux;
    }
    
    private Estado getEstado(int id){
        for(Estado e : listaEstado){
            if(e.getIdEstado() == id) {
                return e;
            }
        } return null;
    }

    private void print() {
        int mayor = registroEstados.size();
        if(registroSimbolos.size() > mayor){
            mayor = registroSimbolos.size();
            if(mayor < registroAcciones.size()) {
                mayor = registroAcciones.size();
            }
        } else {
            if(mayor < registroAcciones.size()) {
                mayor = registroAcciones.size();
            }
        }
        System.out.print("Estados\t\t\t\t");
        System.out.print("Simbolo\t\t\t\t");
        System.out.print("Acción\t\t\t\t\n");
        for(int i = 0;  i < mayor; i++) {
            if (i < registroEstados.size()){
                System.out.print(registroEstados.get(i) + "\t\t\t\t");
            }
            if (i < registroSimbolos.size()){
                System.out.print(registroSimbolos.get(i) + "\t\t\t\t");
            }
            if (i < registroAcciones.size()){
                System.out.print(registroAcciones.get(i) + "\t\t\t\t");
            }
            System.out.println("");
        }
    }
    
    public String getReglaSemantica(int id){
        for (Produccion p : produccion) {
            if (p.getIdProduccion() == id) {
                return p.getReglaSemantica();
            }
        } return "";
    }
    
    private void printds(List<SimCodSem> s){
        for (SimCodSem simCodSem : s) {
            System.out.println("Produccion: " + simCodSem.getP().getIdProduccion() + " TNT " + simCodSem.getTnt().toString() + " Val: " + simCodSem.getValorDevuelto());
        }
    }
    
    public List<SimCodSem> listaValoresDeclarar(Produccion p){
        List<SimCodSem> lista = new ArrayList<>();
        Object o = p.getProduccion();
        while (o != null) {
            SimCodSem cs;
            if (o instanceof Terminal) {
                cs = new SimCodSem(p, o, ((Terminal) o).getValorDevuelto());
                o = ((Terminal) o).getSiguiente();
            } else {
                cs = new SimCodSem(p, o, ((NoTerminal) o).getValorDevuelto());
                o = ((NoTerminal) o).getSiguiente();
            }
            lista.add(cs);
            
        } return lista;
    }
    
}
