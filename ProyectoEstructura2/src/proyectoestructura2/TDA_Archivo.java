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
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mario
 */
public class TDA_Archivo {

    private Availist availist;
    private final File archivo;
    private RandomAccessFile ManejoArchivo;
    private ArbolB arbol;

    public TDA_Archivo(File archivo) {
        this.archivo = archivo;
    }

    public TDA_Archivo() {
        this.archivo = new File("./Registros.data");
        arbol = new ArbolB();
        if (archivo.exists() && !archivo.isFile()) {
            JOptionPane.showMessageDialog(null, "El archivo no existe");
        } else {
            try {
                ManejoArchivo = new RandomAccessFile(archivo, "rw");
                availist = new Availist();
                ManejoArchivo.seek(0);
                if (archivo.length() > 0) {
                    int actual = ManejoArchivo.readInt();
                    availist.setHeader(actual);
                    System.out.println(actual);
                    if (availist.getHeader() != -1) {
                        availist.AddAvail(actual);
                        Persona lectura = new Persona();
                        ManejoArchivo.seek(0 + availist.headerSize());
                        while (true) {
                            ManejoArchivo.seek((actual) * lectura.sizeofRecord() + availist.headerSize());
                            ManejoArchivo.readChar();
                            actual = ManejoArchivo.readInt();
                            if (actual != -1) {
                                availist.AddAvail(actual);
                            } else {
                                break;
                            }
                        }
                    } else {
                        availist.setHeader(-1);
                    }
                } else {
                    ManejoArchivo.seek(0);
                    ManejoArchivo.writeInt(-1);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean Insertar(Persona record) {
        try {
            ManejoArchivo.seek(0 + availist.headerSize());
            if (availist.getAvailist().size() == 0) {
                ManejoArchivo.seek(archivo.length());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(0);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                int temprrn = (int) ((archivo.length() - availist.getHeaderSize()) / record.sizeofRecord());
                System.out.println(archivo.length());
                System.out.println(temprrn);
                Nodo neo = new Nodo(record.getId(), temprrn);
                arbol.insertar(neo);
                return true;
            } else {
                ManejoArchivo.seek(0);
                int rrnAvail = availist.getSiguiente();
                ManejoArchivo.seek(record.sizeofRecord() * (rrnAvail - 1) + availist.getHeaderSize());
                char prueba = ManejoArchivo.readChar();
                if (prueba == '*') {
                    int newheader = ManejoArchivo.readInt();
                    ManejoArchivo.seek(0);
                    ManejoArchivo.writeInt(newheader);
                }
                ManejoArchivo.seek(record.sizeofRecord() * (rrnAvail - 1) + availist.getHeaderSize());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(0);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                Nodo neo = new Nodo(record.getId(), rrnAvail);
                arbol.insertar(neo);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean Borrar(int key) {
        Persona record = new Persona();
        if (arbol.buscarNodo(key) != null) {
            try {
                int rrn = arbol.buscarNodo(key).getPos();
                ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                record.setBorrar(ManejoArchivo.readChar());
                record.setNext(ManejoArchivo.readInt());
                record.setId(ManejoArchivo.readInt());
                record.setNombre(ManejoArchivo.readUTF());
                record.setFechaNacimiento(ManejoArchivo.readUTF());
                record.setSalario(ManejoArchivo.readFloat());
                if (record.getBorrar() != '*') {
                    record.setBorrar('*');
                    ManejoArchivo.seek(0);
                    int header = ManejoArchivo.readInt();
                    if (header == -1) {
                        ManejoArchivo.seek(0);
                        ManejoArchivo.writeInt(rrn - 1);
                        ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                        ManejoArchivo.writeChar(record.getBorrar());
                        ManejoArchivo.writeInt(header);
                        ManejoArchivo.writeInt(record.getId());
                        ManejoArchivo.writeUTF(record.getNombre());
                        ManejoArchivo.writeUTF(record.getFechaNacimiento());
                        ManejoArchivo.writeFloat(record.getSalario());
                        availist.AddAvail(rrn);
                        return true;
                    } else {
                        ManejoArchivo.seek(0);
                        ManejoArchivo.writeInt(rrn);
                        ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                        ManejoArchivo.writeChar(record.getBorrar());
                        ManejoArchivo.writeInt(header);
                        ManejoArchivo.writeInt(record.getId());
                        ManejoArchivo.writeUTF(record.getNombre());
                        ManejoArchivo.writeUTF(record.getFechaNacimiento());
                        ManejoArchivo.writeFloat(record.getSalario());
                        availist.AddAvail(rrn);
                        return true;
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            return false;
        }
        return false;
    }

    public Persona Buscar(int key) {
        Persona record = new Persona();
        if (arbol.buscarNodo(key) != null) {
            try {
                int rrn = arbol.buscarNodo(key).getPos();
                ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                System.out.println(rrn);
                record.setBorrar(ManejoArchivo.readChar());
                record.setNext(ManejoArchivo.readInt());
                record.setId(ManejoArchivo.readInt());
                record.setNombre(ManejoArchivo.readUTF());
                record.setFechaNacimiento(ManejoArchivo.readUTF());
                record.setSalario(ManejoArchivo.readFloat());
            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            return null;
        }
        return record;
    }

    public boolean Modificar(Persona neorecord, int key) {
        if (arbol.buscarNodo(key) != null) {
            try {
                Nodo temp = arbol.buscarNodo(key);
                int rrn = temp.getPos();
                ManejoArchivo.seek(neorecord.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                if (temp.getKey() == neorecord.getId()) {
                    ManejoArchivo.writeChar(neorecord.getBorrar());
                    ManejoArchivo.writeInt(0);
                    ManejoArchivo.writeInt(neorecord.getId());
                    ManejoArchivo.writeUTF(neorecord.getNombre());
                    ManejoArchivo.writeUTF(neorecord.getFechaNacimiento());
                    ManejoArchivo.writeFloat(neorecord.getSalario());
                } else {
                    Borrar(key);
                    Insertar(neorecord);
                }

            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public long filesize() {
        return archivo.length() - availist.getHeaderSize();
    }

    public DefaultTableModel listar(DefaultTableModel model, int rrn) {
        try {
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            Persona record = new Persona();
            if (record.sizeofRecord() * rrn * 50 + availist.getHeaderSize() < archivo.length()) {
                ManejoArchivo.seek(rrn * 50 + availist.getHeaderSize());
                int cont = 0;
                while (cont < 50) {
                    if (ManejoArchivo.getFilePointer() < archivo.length()) {
                        record.setBorrar(ManejoArchivo.readChar());
                        record.setNext(ManejoArchivo.readInt());
                        record.setId(ManejoArchivo.readInt());
                        record.setNombre(ManejoArchivo.readUTF());
                        record.setFechaNacimiento(ManejoArchivo.readUTF());
                        record.setSalario(ManejoArchivo.readFloat());
                        if (record.getBorrar() != '*') {
                            model.addRow(new Object[]{record.getId(), record.getNombre(), record.getFechaNacimiento(), record.getSalario()});
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return model;
    }

    public ArbolB getArbol() {
        return arbol;
    }
}
