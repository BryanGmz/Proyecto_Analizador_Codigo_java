/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador.backed.objetos;

import proyectoanalizador.backed.objetos.analizador.sintactico.Produccion;
import proyectoanalizador.backed.objetos.analizador.sintactico.SimCodSem;

/**
 *
 * @author bryangmz
 */
public class SimboloCodigo {
    
    private SimCodSem simCodSem;
    private Object valor;

    public SimboloCodigo(SimCodSem simCodSem, Object valor) {
        this.simCodSem = simCodSem;
        this.valor = valor;
    }

    public SimCodSem getSimCodSem() {
        return simCodSem;
    }

    public void setSimCodSem(SimCodSem simCodSem) {
        this.simCodSem = simCodSem;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
    
}
