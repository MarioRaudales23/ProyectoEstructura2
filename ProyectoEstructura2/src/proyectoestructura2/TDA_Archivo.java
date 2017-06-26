/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        this.archivo = new File("./Registro.data");
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
                    if (availist.getHeader() != -1) {
                        availist.AddAvail(actual);
                        Persona lectura = new Persona();
                        //ManejoArchivo.seek(0 + availist.headerSize());
                        while (true) {
                            ManejoArchivo.seek((actual - 1) * lectura.sizeofRecord() + availist.headerSize());
                            ManejoArchivo.readChar();
                            actual = ManejoArchivo.readInt();
                            System.out.println(actual);
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
                ManejoArchivo.writeInt(-1);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                int temprrn = (int) ((archivo.length() - availist.getHeaderSize()) / record.sizeofRecord());
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
                ManejoArchivo.writeInt(-1);
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
                if (rrn == 0) {
                    ManejoArchivo.seek(record.sizeofRecord() * (rrn) + availist.getHeaderSize());
                    System.out.println(rrn);
                    record.setBorrar(ManejoArchivo.readChar());
                    record.setNext(ManejoArchivo.readInt());
                    record.setId(ManejoArchivo.readInt());
                    record.setNombre(ManejoArchivo.readUTF());
                    record.setFechaNacimiento(ManejoArchivo.readUTF());
                    record.setSalario(ManejoArchivo.readFloat());
                } else {
                    ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                    record.setBorrar(ManejoArchivo.readChar());
                    record.setNext(ManejoArchivo.readInt());
                    record.setId(ManejoArchivo.readInt());
                    record.setNombre(ManejoArchivo.readUTF());
                    record.setFechaNacimiento(ManejoArchivo.readUTF());
                    record.setSalario(ManejoArchivo.readFloat());
                }
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
                ManejoArchivo.seek(0 + availist.getHeaderSize());
                while (true) {
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
            e.printStackTrace();
        }
        return model;
    }

    public ArbolB getArbol() {
        return arbol;
    }

    public void cargar() {
        try {
            ManejoArchivo.seek(0 + availist.getHeaderSize());
            File archivos = null;
            FileReader fr = null;
            BufferedReader br = null;
            try {
                // Apertura del fichero y creacion de BufferedReader para poder
                // hacer una lectura comoda (disponer del metodo readLine()).
                archivos = new File("./datos.txt");
                fr = new FileReader(archivos);
                br = new BufferedReader(fr);
                Persona record = new Persona();
                // Lectura del fichero
                String linea;
                File data = new File("./Registro.data");
                RandomAccessFile ManejoArchivo = new RandomAccessFile(data, "rw");
                ManejoArchivo.writeInt(-1);
                while ((linea = br.readLine()) != null) {
                    String[] r = linea.split("[|]");
                    String[] numero = r[0].split("[-]");
                    String num = numero[0] + numero[2];
                    String[] fecha = r[3].split("[/]");
                    String nombre = r[1] + " " + r[2];
                    while (nombre.length() != 40) {
                        nombre += " ";
                    }
                    String fecha1;
                    String fecha2;
                    String fecha3;
                    fecha1 =fecha[2];
                    if (fecha[0].length()==1) {                        
                      fecha2 = "0"+ fecha[0];
                    }else{
                        fecha2 = fecha[0];
                    }
                    if (fecha[1].length()==1) {
                      fecha3 = "0"+ fecha[1]; 
                    }else{
                        fecha3 = fecha[1];
                    }
                    fecha1 = fecha[2] + "/" + fecha2 + "/" + fecha3;
                    float dinero = Float.parseFloat(r[4]) * 1000 / 3;
                    int id = Integer.parseInt(num);
                    while(arbol.getRaiz().getNodos()[0]!=null&&arbol.buscarNodo(id)!=null){
                        id++;
                    }
                    record.setBorrar('-');
                    record.setNext(-1);
                    record.setId(id);
                    record.setNombre(nombre);
                    record.setFechaNacimiento(fecha1);
                    record.setSalario(dinero);
                    Insertar(record);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // En el finally cerramos el fichero, para asegurarnos
                // que se cierra tanto si todo va bien como si salta 
                // una excepcion.
                try {
                    if (null != fr) {
                        fr.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void guardarArbol(){
         try 
        {
            File arbolfile = new File("./arbol.b");
            FileOutputStream ficheroSalida = new FileOutputStream(arbolfile);
            ObjectOutputStream objetoSalida = new ObjectOutputStream(ficheroSalida);
            // se escriben dos objetos de la clase Persona
            objetoSalida.writeObject(this.arbol);
            objetoSalida.close();
        }
        catch (FileNotFoundException e) 
        {
            System.out.println("¡El fichero no existe!");
        } catch (IOException e) 
        {
            e.printStackTrace();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    public void cargarArbol(){
        try {
            File arbolfile = new File("./arbol.b");
            FileInputStream ficheroEntrada = new FileInputStream(arbolfile);
            ObjectInputStream objetoEntrada = new ObjectInputStream(ficheroEntrada);
            // se leen dos objetos de la clase Persona
            arbol = (ArbolB)objetoEntrada.readObject();
            // se cierra el flujo de objetos objetoEntrada
            objetoEntrada.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            };
    }
}
