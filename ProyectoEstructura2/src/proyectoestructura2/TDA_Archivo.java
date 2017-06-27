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
                        ManejoArchivo.seek(0 + availist.headerSize());
                        while (true) {
                            ManejoArchivo.seek((actual - 1) * lectura.sizeofRecord() + availist.headerSize());
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
            ManejoArchivo.seek(0 + availist.headerSize());//Se mueve al inicio del inicio del archivo
            if (availist.getAvailist().size() == 0) {//Si no hay nada el el availist se agrega al final
                ManejoArchivo.seek(archivo.length());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(-1);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                int temprrn = (int) ((archivo.length() - availist.getHeaderSize()) / record.sizeofRecord());//se obtiene el rrn
                Nodo neo = new Nodo(record.getId(), temprrn);
                arbol.insertar(neo);//Se guarda en el arbol
                return true;
            } else {
                ManejoArchivo.seek(0);//Se mueve al header 
                int rrnAvail = availist.getSiguiente();//obtiene el rrn del availist
                ManejoArchivo.seek(record.sizeofRecord() * (rrnAvail - 1) + availist.getHeaderSize());//Se mueve a la ubicacion
                char prueba = ManejoArchivo.readChar();
                if (prueba == '*') {
                    int newheader = ManejoArchivo.readInt();
                    ManejoArchivo.seek(0);
                    ManejoArchivo.writeInt(newheader);
                }
                //Se agrega el registro en el rrn obtenido del availist
                ManejoArchivo.seek(record.sizeofRecord() * (rrnAvail - 1) + availist.getHeaderSize());
                ManejoArchivo.writeChar(record.getBorrar());
                ManejoArchivo.writeInt(-1);
                ManejoArchivo.writeInt(record.getId());
                ManejoArchivo.writeUTF(record.getNombre());
                ManejoArchivo.writeUTF(record.getFechaNacimiento());
                ManejoArchivo.writeFloat(record.getSalario());
                Nodo neo = new Nodo(record.getId(), rrnAvail);
                arbol.insertar(neo);//Se agrega al arbol el indice
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
                //Se obtiene el rrn del registro de su ubicacion en el archivo
                int rrn = arbol.buscarNodo(key).getPos();
                ManejoArchivo.seek(record.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                record.setBorrar(ManejoArchivo.readChar());
                record.setNext(ManejoArchivo.readInt());
                record.setId(ManejoArchivo.readInt());
                record.setNombre(ManejoArchivo.readUTF());
                record.setFechaNacimiento(ManejoArchivo.readUTF());
                record.setSalario(ManejoArchivo.readFloat());
                //Verifica si el registro ya fue borrado como una verificacion secundaria
                if (record.getBorrar() != '*') {
                    record.setBorrar('*');
                    ManejoArchivo.seek(0);
                    int header = ManejoArchivo.readInt();
                    //Si no hay nada en el header actualiza directamente el header 
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
                        arbol.Eliminar(key);
                        return true;
                    } else {
                        //Caso contrario se paza el header al registro borrado y se coloca el en el header el rrn del registro 
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
                        arbol.Eliminar(key);
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

    //Este metodo busca por medio de indice un registro en particular
    public Persona Buscar(int key) {
        Persona record = new Persona();
        if (arbol.buscarNodo(key) != null) {//se verifica si el registro esta en el arbol
            try {
                int rrn = arbol.buscarNodo(key).getPos();//Se obtiene el rrn del registro
                if (rrn == 0) {
                    //Se lee el registro de la ubicacion
                    ManejoArchivo.seek(record.sizeofRecord() * (rrn) + availist.getHeaderSize());
                    record.setBorrar(ManejoArchivo.readChar());
                    record.setNext(ManejoArchivo.readInt());
                    record.setId(ManejoArchivo.readInt());
                    record.setNombre(ManejoArchivo.readUTF());
                    record.setFechaNacimiento(ManejoArchivo.readUTF());
                    record.setSalario(ManejoArchivo.readFloat());
                } else {
                    //Se lee el registro de la ubicacion
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
            return null;//si no existe retorna null
        }
        return record;//si existe se retorna el registro
    }

    public boolean Modificar(Persona neorecord, int key) {
        if (arbol.buscarNodo(key) != null) {//Se verifica que el registro esta en el arbol
            try {

                Nodo temp = arbol.buscarNodo(key);//Se guarda el nodo que contiene la informacion del registro
                int rrn = temp.getPos();
                ManejoArchivo.seek(neorecord.sizeofRecord() * (rrn - 1) + availist.getHeaderSize());
                if (temp.getKey() == neorecord.getId()) {//Se verifica si se cambio la llave
                    //Si no se cambio se guarda directamente en su posicion
                    ManejoArchivo.writeChar(neorecord.getBorrar());
                    ManejoArchivo.writeInt(0);
                    ManejoArchivo.writeInt(neorecord.getId());
                    ManejoArchivo.writeUTF(neorecord.getNombre());
                    ManejoArchivo.writeUTF(neorecord.getFechaNacimiento());
                    ManejoArchivo.writeFloat(neorecord.getSalario());
                } else {
                    //Caso contrario se hace Borrar-Insertar
                    Borrar(key);
                    Insertar(neorecord);
                }

            } catch (IOException ex) {
                Logger.getLogger(TDA_Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    //Metodo que retorna la longitud del archivo sin header
    public long filesize() {
        return archivo.length() - availist.getHeaderSize();
    }

    //Metodo que retorna un TableModel con los registros
    public DefaultTableModel listar(DefaultTableModel model, int rrn) {
        try {
            //Se reinicia la tabla para que no contenga lineas
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            Persona record = new Persona();
            ManejoArchivo.seek(0 + availist.getHeaderSize());
            while (true) {
                if (ManejoArchivo.getFilePointer() < archivo.length()) {//Se verifica que no sea el final del archivo
                    //se lee el record y se agrega a la tabla
                    record.setBorrar(ManejoArchivo.readChar());
                    record.setNext(ManejoArchivo.readInt());
                    record.setId(ManejoArchivo.readInt());
                    record.setNombre(ManejoArchivo.readUTF());
                    record.setFechaNacimiento(ManejoArchivo.readUTF());
                    record.setSalario(ManejoArchivo.readFloat());
                    if (record.getBorrar() != '*') {
                        model.addRow(new Object[]{record.getId(), record.getNombre(), record.getFechaNacimiento(), record.getSalario()});
                    }
                } else {//Al final del archivo se sale del ciclo
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;//Retorna el modelo
    }

    public ArbolB getArbol() {
        return arbol;
    }

    //Metodo que carga el archivo txt a binario
    public void cargar() {
        try {
            ManejoArchivo.seek(0 + availist.getHeaderSize());
            File archivos = null;
            FileReader fr = null;
            BufferedReader br = null;
            try {
                //Se abre el archivo txt 
                archivos = new File("./datos.txt");
                fr = new FileReader(archivos);
                br = new BufferedReader(fr);
                Persona record = new Persona();
                String linea;
                //Se abre el archivo binario
                File data = new File("./Registro.data");
                RandomAccessFile ManejoArchivo = new RandomAccessFile(data, "rw");
                ManejoArchivo.writeInt(-1);
                //Se hace todos los diferentes cambios para que el txt se pase a binario
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
                    fecha1 = fecha[2];
                    if (fecha[0].length() == 1) {
                        fecha2 = "0" + fecha[0];
                    } else {
                        fecha2 = fecha[0];
                    }
                    if (fecha[1].length() == 1) {
                        fecha3 = "0" + fecha[1];
                    } else {
                        fecha3 = fecha[1];
                    }
                    fecha1 = fecha[2] + "/" + fecha2 + "/" + fecha3;
                    float dinero = Float.parseFloat(r[4]) * 1000 / 3;
                    int id = Integer.parseInt(num);
                    while (arbol.getRaiz().getNodos()[0] != null && arbol.buscarNodo(id) != null) {
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

    //Guarda el arbol en binario
    public boolean guardarArbol() {
        try {
            File arbolfile = new File("./arbol.b");
            FileOutputStream ficheroSalida = new FileOutputStream(arbolfile);
            ObjectOutputStream objetoSalida = new ObjectOutputStream(ficheroSalida);
            // se escriben dos objetos de la clase Persona
            objetoSalida.writeObject(this.arbol);
            objetoSalida.close();
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Carga el arbol al archivo
    public boolean cargarArbol() {
        try {
            File arbolfile = new File("./arbol.b");
            FileInputStream ficheroEntrada = new FileInputStream(arbolfile);
            ObjectInputStream objetoEntrada = new ObjectInputStream(ficheroEntrada);
            // se lee el arbol
            arbol = (ArbolB) objetoEntrada.readObject();
            // se cierra el flujo
            objetoEntrada.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        };
        return false;
    }
}
