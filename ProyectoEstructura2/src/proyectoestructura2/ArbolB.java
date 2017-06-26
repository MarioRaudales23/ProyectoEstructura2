/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.util.ArrayList;

/**
 *
 * @author luigy
 */
public class ArbolB {

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
        obtenerNodoListar(salida, recorrerListar(new Nodo(0, 0), raiz, false), 0, new Nodo());

    }

    public void obtenerNodoListar(ArrayList<Nodo> salida, Pagina pag, int actual, Nodo anterior) {
        for (Nodo nodo : salida) {
            System.out.print(nodo.getKey() + ",");
        }
        if (!true) {

        } else {
            if (!pag.getNodos()[0].tieneHijos()) {
                for (int i = 0; i < pag.getNodos().length - 1; i++) {
                    if (pag.getNodos()[i] != null) {
                        salida.add(pag.getNodos()[i]);
                    } else {
                        break;
                    }
                }
                do {
                    System.out.println(pag);
                    if (pag.getNodos()[actual] != null) {
                        if (pag.getNodos()[actual].getKey() == anterior.getKey()) {
                            obtenerNodoListar(salida, pag, actual, anterior);
                        } else {
                            pag = pag.getPadre();
                        }
                    } else {
                        pag = pag.getPadre();
                    }
                } while (true);

            } else {
                if (pag.getNodos()[actual] != null) {
                    salida.add(pag.getNodos()[actual]);
                    obtenerNodoListar(salida, recorrerListar(new Nodo((pag.getNodos()[actual].getKey() + 1), 0), pag, false), actual + 1, pag.getNodos()[actual]);
                } else {
                    obtenerNodoListar(salida, pag.getPadre().getPadre(), 0, anterior);
                }
            }
        }
    }

    public Nodo buscarNodo(int key) {
        return recorrerBusqueda(new Nodo(key, -1), raiz, false);
    }

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

    public void split(Pagina pag, Nodo promovido, boolean Noverflow) {
        pag.ordenar();
        int promote = (int) Math.ceil(pag.getNodos().length / 2) - 1;
        promovido = pag.getNodos()[promote];
        if (!promovido.tieneHijos()) {
            promovido.setDer(new Pagina());
            promovido.setIzq(new Pagina());
            for (int i = 0; i < promote; i++) {
                promovido.getIzq().getNodos()[i] = pag.getNodos()[i];
            }
            for (int i = promote + 1; i < pag.getNodos().length; i++) {
                promovido.getDer().getNodos()[i - promote - 1] = pag.getNodos()[i];
            }
        } else {
            promovido.setDer(new Pagina());
            promovido.setIzq(new Pagina());
            for (int i = 0; i < promote; i++) {
                promovido.getIzq().getNodos()[i] = pag.getNodos()[i];
            }
            for (int i = promote + 1; i < pag.getNodos().length; i++) {
                promovido.getDer().getNodos()[i - promote - 1] = pag.getNodos()[i];
            }
        }
        if (pag.getPadre() == null) {
            pag.setPadre(new Pagina());
            raiz = pag.getPadre();
        }
        for (int i = 0; i < pag.getNodos().length; i++) {
            if (pag.getPadre().getNodos()[i] == null) {
                pag.getPadre().getNodos()[i] = promovido;
                break;
            }
        }
        pag.getPadre().ordenar();
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

        if (!pag.getPadre().overflow()) {
            promovido.getIzq().setPadre(pag.getPadre());
            promovido.getDer().setPadre(pag.getPadre());
        } else {
            split(pag.getPadre(), promovido, true);
        }

    }

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

    public Pagina recorrerListar(Nodo nodoBandera, Pagina pag, boolean bandera) {
        if (!pag.getNodos()[0].tieneHijos()) {
            bandera = true;
        }
        if (bandera) {
            return pag;
        } else {
            pag = recorrerListar(nodoBandera, siguienteHoja(nodoBandera, pag), bandera);
            return pag;
        }
    }

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
