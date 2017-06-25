/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author mario
 */
public class TDA_Archivo {
    private Availist availist;
    private final File archivo;
    private RandomAccessFile ManejoArchivo;

    public TDA_Archivo(File archivo) {
        this.archivo = archivo;
    }

    public TDA_Archivo() {
        this.archivo = new File("./Registros.data");
        if (archivo.exists() && !archivo.isFile()) {
            JOptionPane.showMessageDialog(null, "El archivo no existe");
        }else{
            try {
                ManejoArchivo = new RandomAccessFile(archivo, "rw");
                availist = new Availist();
                ManejoArchivo.seek(0);
                if (archivo.length()>0) {
                    int actual = ManejoArchivo.readInt();
                    availist.setHeader(actual);
                    if (availist.getHeader() != -1) {
                        availist.AddAvail(actual);
                        Persona lectura = new Persona();
                        ManejoArchivo.seek(0+availist.headerSize());
                        while (true) {                            
                            ManejoArchivo.seek((actual-1)*lectura.sizeofRecord()+availist.headerSize());
                            ManejoArchivo.readChar();
                            actual = ManejoArchivo.readInt();
                            if (actual != -1) {
                                availist.AddAvail(actual);
                            }else{
                                break;
                            }
                        }
                    }else{
                        availist.setHeader(-1);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean Insertar(Persona record){
        try {
            ManejoArchivo.seek(0+availist.headerSize());
            if (availist.getAvailist().isEmpty()) {
                ManejoArchivo.seek(archivo.length());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(-1);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                return true;
            }else{
                ManejoArchivo.seek(0);
                int rrnAvail = availist.getSiguiente();
                ManejoArchivo.seek(record.sizeofRecord()*(rrnAvail-1)+availist.getHeaderSize());
                char prueba = ManejoArchivo.readChar();
                if (prueba == '*') {
                    int newheader = ManejoArchivo.readInt();
                    ManejoArchivo.seek(0);
                    ManejoArchivo.writeInt(newheader);
                }
                ManejoArchivo.seek(record.sizeofRecord()*(rrnAvail-1)+availist.getHeaderSize());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(-1);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
    public boolean Borrar(){
        return true;
    }
    public Persona Buscar(){
        Persona record =  new Persona();
        
        return record;
    }
    public boolean Modificar(){
        return true;
    }
}
