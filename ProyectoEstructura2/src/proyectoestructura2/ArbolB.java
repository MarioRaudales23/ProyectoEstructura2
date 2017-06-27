/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author luigy
 */
public class ArbolB implements Serializable{

    private Pagina raiz;

    public ArbolB() {
        raiz = new Pagina();
    }

    public ArbolB(Pagina raiz) {
        this.raiz = raiz;
    }

    public Pagina getRaiz() {
        return raiz;
    }

    public void setRaiz(Pagina raiz) {
        this.raiz = raiz;
    }

    public void insertar(Nodo nuevo) {
        if (raiz.getNodos()[0] == null) {
            raiz.getNodos()[0] = nuevo;
        } else {
            recorrerInsertar(nuevo, raiz, false);
        }
    }

    public void listar() {
        Pagina temporal;
        ArrayList<Nodo> salida = new ArrayList();
        obtenerNodoListar(salida, recorrerHastaHoja(new Nodo(0, 0), raiz, false), 0, new Nodo(), true);

    }

    public boolean Eliminar(long key) {
        Pagina pag = raiz;
        pag = recorrerEliminar(new Nodo(key, -1), pag, false);//busca de manera recursiva la pagina que contiene el registro a eliminar 
        if (pag != null) {//si la pagina retornada es igual a null el registro no extiste
            int posicion = 0;
            for (int i = 0; i < pag.getNodos().length; i++) {
                if (pag.getNodos()[i].getKey() == key) {
                    posicion = i;
                    break;
                }
            }
            if (posicion <= pag.poUltimo() && pag.getNodos()[posicion].getKey() == key) {
                if (!pag.getNodos()[0].tieneHijos()) {
                    casoHoja(posicion, pag, key);
                } else {
                    casoNodoIntermedio(posicion, key, pag);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    //este metodo elimina teniendo de parametro la llava y la pagina que lo contiene
    public boolean Eliminar(long key, Pagina pag) {
        if (pag != null) {//si la pagina retornada es igual a null el registro no extiste
            int posicion = 0;
            for (int i = 0; i < pag.getNodos().length; i++) {
                if (pag.getNodos()[i].getKey() == key) {
                    posicion = i;
                    break;
                }
            }
            if (posicion <= pag.poUltimo() && pag.getNodos()[posicion].getKey() == key) {
                if (!pag.getNodos()[0].tieneHijos()) {
                    casoHoja(posicion, pag, key);
                } else {
                    casoNodoIntermedio(posicion, key, pag);
                }
            }
            return true;
        } else {
            return false;
        }

    }

    //este metodo elimina en caso de estar en un hoja
    public void casoHoja(int posicion, Pagina pag, long key) {
        if (pag.getNodos()[posicion].getKey() == pag.getNodos()[pag.poUltimo()].getKey() && posicion + 1 != ((pag.getNodos().length / 2) - 1)) {
            pag.getNodos()[posicion] = null;
        } else {
            if (posicion + 1 != (pag.getNodos().length / 2) - 1) {//si la posicion del nodo a eliminar es diferente al limite de llaves permitidas
                for (int i = posicion + 1; i <= pag.poUltimo(); i++) {//
                    pag.getNodos()[i - 1] = pag.getNodos()[i];
                    if (i == pag.poUltimo()) {
                        pag.getNodos()[i] = pag.getNodos()[i + 1];
                    }
                }
            } else {//en caso de posicion sea igual al minimo permitido pide prestado un hermano
                //faltan instrucciones
                correrPagina(posicion, pag);
            }
        }
    }

    //ordena una pagina al eliminar un nodo de esta
    public void correrPagina(int posicion, Pagina pag) {
        for (int i = posicion + 1; i <= pag.poUltimo(); i++) {
            pag.getNodos()[i - 1] = pag.getNodos()[i];
            if (i == pag.poUltimo()) {
                pag.getNodos()[i] = pag.getNodos()[i + 1];
            }
        }
    }

    //en caso de que el nodo no sea una hoja
    //verifica a su predecesor o sucesor si estos no entraran en underflow al pedirles prestado un nodo
    //si ambos entran en underflow concatenar
    public void casoNodoIntermedio(int posicion, long key, Pagina pag) {
        Pagina tem = pag;
        Pagina pagPredSuce = recorrerHastaHoja(new Nodo(key + 1, 0), tem, false);
        if (!pagPredSuce.underflow()) {
            Nodo sucesor = pagPredSuce.getNodos()[0];
            boolean bandera = false;
            if (sucesor.tieneHijos()) {
                bandera = true;
            }
            sucesor.setIzq(pag.getNodos()[posicion].getIzq());
            sucesor.setDer(pag.getNodos()[posicion].getDer());
            pag.getNodos()[posicion] = sucesor;
            if (bandera) {
                Eliminar(sucesor.getKey(), pagPredSuce);
            } else {
                pagPredSuce.getNodos()[0] = pagPredSuce.getNodos()[pagPredSuce.poUltimo()];
                pagPredSuce.getNodos()[pagPredSuce.poUltimo()] = null;
                pagPredSuce.ordenar();
            }

        } else {
            tem = pag;
            pagPredSuce = recorrerHastaHoja(new Nodo(key - 1, 0), tem, false);
            if (!pagPredSuce.underflow()) {
                Nodo pred = pagPredSuce.getNodos()[pagPredSuce.poUltimo()];
                boolean bandera = false;
                if (pred.tieneHijos()) {
                    bandera = true;
                }
                pred.setIzq(pag.getNodos()[posicion].getIzq());
                pred.setDer(pag.getNodos()[posicion].getDer());
                pag.getNodos()[posicion] = pred;
                if (bandera) {
                    Eliminar(pred.getKey(), pagPredSuce);
                } else {
                    pagPredSuce.getNodos()[pagPredSuce.poUltimo()] = null;
                }
            } else {
                //faltan instrucciones
                Eliminar(key, pag);
            }
        }
    }

    public Pagina concatenar(Pagina pag) {
        Pagina adyacente = new Pagina();
        int ultiNodo = pag.poUltimo();
        boolean izquierda = true;
        for (int i = 0; i <= pag.getPadre().poUltimo(); i++) {
            if (pag.getPadre().getNodos()[i].getIzq() == pag) {
                adyacente = pag.getPadre().getNodos()[i].getDer();//busca al hermano
                pag.getNodos()[pag.poUltimo() + 1] = pag.getPadre().getNodos()[i];//pasa al padre a la pagina donde se encuentre el nodo a eliminar
                pag.getPadre().getNodos()[i + 1].setIzq(pag.getPadre().getNodos()[i].getIzq());//pasa los hijos del padre anterior al nuevo padre
                correrPagina(i, pag.getPadre());//ordena la pagina
                izquierda = false;
                break;
            } else {
                if (pag.getPadre().getNodos()[i].getDer() == pag) {
                    adyacente = pag.getPadre().getNodos()[i].getIzq();
                    pag.getNodos()[pag.poUltimo() + 1] = pag.getPadre().getNodos()[i];
                    pag.getPadre().getNodos()[i].setDer(pag.getPadre().getNodos()[i + 1].getDer());
                    correrPagina(i, pag.getPadre());
                    break;
                }
            }
        }
        for (int i = 0; i <= adyacente.poUltimo(); i++) {//pasa todos los hijos del hermano a la pagina donde se encuentra el nodo a eliminar
            pag.getNodos()[(pag.getNodos().length / 2) + i] = adyacente.getNodos()[i];
        }
        if (izquierda) {//iguala los hijos de los nodos del hermano 
            pag.ordenar();
            pag.getNodos()[adyacente.poUltimo() + 1].setIzq(pag.getNodos()[adyacente.poUltimo()].getDer());
        } else {
            pag.getNodos()[ultiNodo].setDer(pag.getNodos()[ultiNodo + 1].getIzq());
        }
        return pag;
    }

    //busca de manera recursiva la key solocitada
    public Nodo buscarNodo(int key) {
        return recorrerBusqueda(new Nodo(key, -1), raiz, false);
    }

    //este metodo es una recursiva que finaliza al encontrar la pagina donde sera insertado el nuevo nodo
    public void recorrerInsertar(Nodo nuevo, Pagina pag, boolean bandera) {
        if (!pag.getNodos()[0].tieneHijos()) {
            bandera = true;
        }
        if (bandera) {
            for (int i = 0; i < pag.getNodos().length; i++) {
                if (pag.getNodos()[i] == null) {
                    pag.getNodos()[i] = nuevo;
                    break;
                }
            }
            if (!pag.overflow()) {
                pag.ordenar();
            } else {
                split(pag, new Nodo(), false);
            }
        } else {
            recorrerInsertar(nuevo, siguienteHoja(nuevo, pag), bandera);
        }
    }

    //divide y promuevo recursivamente
    //hasta que no se genere overflow
    public void split(Pagina pag, Nodo promovido, boolean Noverflow) {
        pag.ordenar();
        int promote = (int) Math.ceil(pag.getNodos().length / 2) - 1;
        promovido = pag.getNodos()[promote];//nodo que sera promovido
        if (!promovido.tieneHijos()) {//en caso de que no sea una hoja 
            promovido.setDer(new Pagina());//limpia los hijos del nodo promovido
            promovido.setIzq(new Pagina());
            for (int i = 0; i < promote; i++) {//agrega los nuevos hijos del promovido
                promovido.getIzq().getNodos()[i] = pag.getNodos()[i];
            }
            for (int i = promote + 1; i < pag.getNodos().length; i++) {
                promovido.getDer().getNodos()[i - promote - 1] = pag.getNodos()[i];
            }
        } else {//en caso de ser una hoja
            promovido.setDer(new Pagina()); //inicia sus paginas hijos
            promovido.setIzq(new Pagina());
            for (int i = 0; i < promote; i++) {//agregas los nuevos his al promovido
                promovido.getIzq().getNodos()[i] = pag.getNodos()[i];
            }
            for (int i = promote + 1; i < pag.getNodos().length; i++) {
                promovido.getDer().getNodos()[i - promote - 1] = pag.getNodos()[i];
            }
        }
        if (pag.getPadre() == null) {//si la pagina actual es la raiz inicializa al padre de este
            pag.setPadre(new Pagina());
            raiz = pag.getPadre();
        }
        for (int i = 0; i < pag.getNodos().length; i++) {//agrega al promovido a la pagina que antes era su padre
            if (pag.getPadre().getNodos()[i] == null) {
                pag.getPadre().getNodos()[i] = promovido;
                break;
            }
        }
        pag.getPadre().ordenar();
        //mantiene en orden los hijos de los nodos trabajados
        for (int i = 0; i < pag.getPadre().getNodos().length; i++) {
            if (i + 1 == pag.getPadre().getNodos().length) {
                pag.getPadre().getNodos()[i - 1].setDer(promovido.getIzq());
            } else {
                if (pag.getPadre().getNodos()[i].getKey() == promovido.getKey()) {
                    if (pag.getPadre().getNodos()[i + 1] == null && i > 0) {
                        pag.getPadre().getNodos()[i - 1].setDer(promovido.getIzq());
                    }
                    if (i - 1 >= 0 && pag.getPadre().getNodos()[i + 1] != null) {
                        pag.getPadre().getNodos()[i - 1].setDer(promovido.getIzq());
                        pag.getPadre().getNodos()[i + 1].setIzq(promovido.getDer());
                    }
                    if (i == 0 && pag.getPadre().getNodos()[i + 1] != null) {
                        pag.getPadre().getNodos()[i + 1].setIzq(promovido.getDer());
                    }

                    break;
                }
            }
        }
        if (Noverflow) {//agrega los padres del split realizado anteriormente
            for (int i = 0; i < promovido.getIzq().getNodos().length; i++) {
                if (promovido.getIzq().getNodos()[i] != null) {
                    promovido.getIzq().getNodos()[i].getIzq().setPadre(promovido.getIzq());
                    promovido.getIzq().getNodos()[i].getDer().setPadre(promovido.getIzq());
                } else {
                    break;
                }
            }
            for (int i = 0; i < promovido.getDer().getNodos().length; i++) {
                if (promovido.getDer().getNodos()[i] != null) {
                    promovido.getDer().getNodos()[i].getIzq().setPadre(promovido.getDer());
                    promovido.getDer().getNodos()[i].getDer().setPadre(promovido.getDer());
                } else {
                    break;
                }
            }
        }
        if (!pag.getPadre().overflow()) {//si la pagina a la cual pertenece el promovido no provoca overflow la recursiva finaliza
            promovido.getIzq().setPadre(pag.getPadre());//se le asigna el padre a los hijos del promovido
            promovido.getDer().setPadre(pag.getPadre());
        } else {//la recursiva continua con la actual pagina del promovido
            split(pag.getPadre(), promovido, true);
        }

    }

    public void obtenerNodoListar(ArrayList<Nodo> salida, Pagina pag, int siguiente, Nodo anterior, boolean bandera) {
        if (bandera) {
            anterior = pag.getPadre().getNodos()[0];
            bandera = false;
        }
        if (pag.getNodos()[siguiente].getKey() == 150) {

        } else {
            if (!pag.getNodos()[0].tieneHijos()) {
                for (int i = 0; i < pag.getNodos().length - 1; i++) {
                    if (pag.getNodos()[i] != null) {
                        salida.add(pag.getNodos()[i]);
                    } else {
                        break;
                    }
                }
                if (pag.getNodos()[siguiente] != null) {
                    while (pag.getNodos()[siguiente].getKey() != anterior.getKey()) {
                        pag = pag.getPadre();
                    }
                    obtenerNodoListar(salida, pag, siguiente, anterior, bandera);
                } else {
                    obtenerNodoListar(salida, pag.getPadre(), siguiente, anterior, bandera);
                }
            } else {
                if (pag.getNodos()[siguiente] != null && siguiente < pag.getNodos().length - 2) {
                    salida.add(pag.getNodos()[siguiente]);
                    obtenerNodoListar(salida, recorrerHastaHoja(new Nodo((pag.getNodos()[siguiente].getKey() + 1), 0), pag, false), siguiente + 1, pag.getNodos()[siguiente], bandera);
                } else {
                    obtenerNodoListar(salida, pag.getPadre().getPadre(), 0, anterior, bandera);
                }
            }
        }
    }

    //recorre el arbol recursivamente hasta encontrar la pagina que contene el nodo que se busca
    public Nodo recorrerBusqueda(Nodo buscar, Pagina pag, boolean encontrado) {
        for (int i = 0; i < pag.getNodos().length; i++) {
            if (pag.getNodos()[i] == null) {
                break;
            } else {
                if (buscar.getKey() == pag.getNodos()[i].getKey()) {
                    buscar = pag.getNodos()[i];
                    encontrado = true;
                    break;
                }
            }
        }
        if (!pag.getNodos()[0].tieneHijos() && buscar.getPos() == -1) {
            encontrado = true;
            buscar = null;
        }
        if (!encontrado) {
            buscar = recorrerBusqueda(buscar, siguienteHoja(buscar, pag), encontrado);
        }
        return buscar;
    }

    //retorna la pagina que contiene el nodo a eliminar
    public Pagina recorrerEliminar(Nodo buscar, Pagina pag, boolean encontrado) {
        for (int i = 0; i < pag.getNodos().length; i++) {
            if (pag.getNodos()[i] == null) {
                break;
            } else {
                if (buscar.getKey() == pag.getNodos()[i].getKey()) {
                    buscar = pag.getNodos()[i];
                    encontrado = true;
                    break;
                }
            }
        }
        if (!pag.getNodos()[0].tieneHijos() && buscar.getPos() == -1) {
            encontrado = true;
            pag = null;
        }
        if (!encontrado) {
            pag = recorrerEliminar(buscar, siguienteHoja(buscar, pag), encontrado);
        }
        return pag;
    }

    //recorre el arbol hasta llegar a una hoja
    public Pagina recorrerHastaHoja(Nodo nodoBandera, Pagina pag, boolean bandera) {
        if (!pag.getNodos()[0].tieneHijos()) {
            bandera = true;
        }
        if (bandera) {
            return pag;
        } else {
            pag = recorrerHastaHoja(nodoBandera, siguienteHoja(nodoBandera, pag), bandera);
            return pag;
        }
    }

    //va comparando el nodo que sera insertado y cambiando de paginas
    //hasta encontrar la pagina adecuada para agregar el nuevo
    public Pagina siguienteHoja(Nodo nodo, Pagina pag) { // retorna la hoja donde seguira 
        Pagina salida = new Pagina();
        int ultimo = 2;
        for (int i = 0; i < pag.getNodos().length - 1; i++) {
            if (pag.getNodos()[i] == null) {
                ultimo = i - 1;
                break;
            }
        }
        if (nodo.getKey() > pag.getNodos()[ultimo].getKey()) {
            salida = pag.getNodos()[ultimo].getDer();
        } else {
            if (nodo.getKey() < pag.getNodos()[0].getKey()) {
                salida = pag.getNodos()[0].getIzq();
            } else {
                for (int i = 0; i < pag.getNodos().length - 1; i++) {
                    if (nodo.getKey() < pag.getNodos()[i].getKey()) {
                        salida = pag.getNodos()[i].getIzq();
                        break;
                    }
                    if (nodo.getKey() > pag.getNodos()[i].getKey() && nodo.getKey() < pag.getNodos()[i + 1].getKey()) {
                        salida = pag.getNodos()[i].getDer();
                        break;
                    }

                }
            }
        }
        return salida;
    }
}
