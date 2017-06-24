/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.util.LinkedList;

/**
 *
 * @author mario
 */
public class Availist {
    private LinkedList availist;
    private static int header;
    private static final int headerSize = Integer.BYTES;

    public Availist() {
    }

    public Availist(int NuevoRRn) {
        this.availist = availist;
    }

    public int getSiguiente() {
        int borrado = (int)availist.removeFirst();
        return borrado;
    }

    public boolean setAvailist(int NuevoRRn) {
        this.availist.add(NuevoRRn);
        return true;
    }

    public static int getHeader() {
        return header;
    }

    public static void setHeader(int header) {
        Availist.header = header;
    }
    
    
}
