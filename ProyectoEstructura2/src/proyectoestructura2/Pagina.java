/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

/**
 *
 * @author luigy
 */
public class Pagina {

    private Nodo[] nodos;
    private Pagina padre;

    public Pagina() {
        nodos = new Nodo[4];

    }

    public Pagina(Nodo[] nodos, Pagina padre) {
        this.nodos = nodos;
        this.padre = padre;
    }

    public Pagina getPadre() {
        return padre;
    }

    public void setPadre(Pagina padre) {
        this.padre = padre;
    }

    public Pagina(Nodo[] nodos) {
        this.nodos = nodos;
    }

    public Nodo[] getNodos() {
        return nodos;
    }

    public void setNodos(Nodo[] nodos) {
        this.nodos = nodos;
    }

    public boolean underflow() {
        int ocupados = 0;
        for (int i = 0; i < nodos.length; i++) {
            if (nodos[i] == null) {
                ocupados = i;
                break;
            }
        }
        if (((nodos.length + 1) / 2) > ocupados) {
            return true;
        } else {
            return false;
        }
    }

    public boolean overflow() {
        if (nodos[nodos.length - 1] == null) {
            return false;
        } else {
            return true;
        }
    }

    public void ordenar() {
        Nodo temp;
        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos.length; j++) {
                if (nodos[i] == null) {
                    i = nodos.length - 1;
                } else {
                    if (nodos[j] == null) {
                        break;
                    } else if ((nodos[i].getKey()) < (nodos[j].getKey())) {
                        temp = nodos[i];
                        nodos[i] = nodos[j];
                        nodos[j] = temp;
                    }
                }
            }
        }
    }

    public void ordenarSplit() {
        Nodo temp;
        for (int i = 0; i < nodos.length; i++) {
            for (int j = 0; j < nodos.length; j++) {
                if ((nodos[i].getKey()) < (nodos[j].getKey())) {
                    temp = nodos[i];
                    nodos[i] = nodos[j];
                    nodos[j] = temp;
                }
            }
        }
    }

    @Override
    public String toString() {
        String hijos = "";
        for (int i = 0; i < nodos.length; i++) {
            if (nodos[i] == null) {
                break;
            } else {
                hijos += "[" + nodos[i].getKey() + "]";
            }
        }
        return hijos;
    }

}
