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
    private final int headerSize = Integer.BYTES;

    public Availist() {
    }

    public Availist(LinkedList availist) {
        this.availist = availist;
    }

    public int getSiguiente() {
        int borrado = (int)availist.removeFirst();
        return borrado;
    }

    public boolean AddAvail(int NuevoRRn) {
        this.availist.add(NuevoRRn);
        return true;
    }

    public static int getHeader() {
        return header;
    }

    public static void setHeader(int header) {
        Availist.header = header;
    }
    
    public int headerSize(){
        return headerSize;
    }

    public LinkedList getAvailist() {
        return availist;
    }

    public int getHeaderSize() {
        return headerSize;
    }
    
}
