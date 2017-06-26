/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoestructura2;

/**
 *
 * @author mario
 */
public class Persona {

    private int id;
    private String nombre;
    private String FechaNacimiento;
    private float salario;
    private char borrar;
    private int next;

    public Persona(int id, String nombre, String FechaNacimiento, float salario, char borrar, int next) {
        this.id = id;
        this.nombre = nombre;
        this.FechaNacimiento = FechaNacimiento;
        this.salario = salario;
        this.borrar = borrar;
        this.next = next;
    }

    public Persona(int id, String nombre, String FechaNacimiento, float salario) {
        this.id = id;
        this.nombre = nombre;
        this.FechaNacimiento = FechaNacimiento;
        this.salario = salario;
    }

    public Persona() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setFechaNacimiento(String FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }

    public float getSalario() {
        return salario;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }

    public char getBorrar() {
        return borrar;
    }

    public void setBorrar(char borrar) {
        this.borrar = borrar;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int sizeofRecord() {
        return 68;
    }

}
