/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.io.Serializable;

/**
 *
 * @author luigy
 */
public class Nodo implements Serializable{
    private long key;
    private int pos;
    private Pagina izq;
    private Pagina der;

    public Nodo() {
       
    }

    public Nodo(long key, int pos, Pagina izq, Pagina der) {
        this.key = key;
        this.pos = pos;
        this.izq = izq;
        this.der = der;
    }
    public Nodo(long key, int pos) {
        this.key = key;
        this.pos = pos;
        this.izq = null;
        this.der = null;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

   
    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Pagina getIzq() {
        return izq;
    }

    public void setIzq(Pagina izq) {
        this.izq = izq;
    }

    public Pagina getDer() {
        return der;
    }

    public void setDer(Pagina der) {
        this.der = der;
    }
    public boolean tieneHijos(){
        if (der==null&&izq==null) {
            return false;
        }else{
            return true;
        }
    }
    
    
}
