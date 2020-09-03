/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoanalizador.backed.objetos.analizador.sintactico;

/**
 *
 * @author bryangmz
 */
public class SimCodSem {
    
    private Produccion p;
    private Object tnt;
    private String valorDevuelto;

    public SimCodSem(Produccion p, Object tnt, String valorDevuelto) {
        this.p = p;
        this.tnt = tnt;
        this.valorDevuelto = valorDevuelto;
    }

    public Produccion getP() {
        return p;
    }

    public void setP(Produccion p) {
        this.p = p;
    }

    public Object getTnt() {
        return tnt;
    }

    public void setTnt(Object tnt) {
        this.tnt = tnt;
    }

    public String getValorDevuelto() {
        return valorDevuelto;
    }

    public void setValorDevuelto(String valorDevuelto) {
        this.valorDevuelto = valorDevuelto;
    }
    
    
}
