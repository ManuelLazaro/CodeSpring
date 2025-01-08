package com.codeexecutor.CodeTestEx.model;


import java.util.List;

public class FunctionRequest {
    private String type;
    private String archivo;
    private String ruta;
    private String nombrefuncion;
    private String clasetest; // Nueva propiedad
    private List<Object> variable;
    private List<Object> data; // Nueva propiedad para almacenar datos de clasetest


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombrefuncion() {
        return nombrefuncion;
    }

    public void setNombrefuncion(String nombrefuncion) {
        this.nombrefuncion = nombrefuncion;
    }

    public String getClasetest() {
        return clasetest;
    }

    public void setClasetest(String clasetest) {
        this.clasetest = clasetest;
    }

    public List<Object> getVariable() {
        return variable;
    }

    public void setVariable(List<Object> variable) {
        this.variable = variable;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public FunctionRequest(String archivo, String ruta, String type, String nombrefuncion, String clasetest, List<Object> variable, List<Object> data) {
        this.archivo = archivo;
        this.ruta = ruta;
        this.type = type;
        this.nombrefuncion = nombrefuncion;
        this.clasetest = clasetest;
        this.variable = variable;
        this.data = data;
    }
}
