package uy.edu.um.doors;

import uy.edu.um.tad.list.MyList;

public class Evento {

    private final TipoEvento tipo;
    private final MyList<String> instrucciones;

    public Evento(TipoEvento tipo, MyList<String> instrucciones) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de evento inválido");
        }

        if (instrucciones == null || instrucciones.isEmpty()) {
            throw new IllegalArgumentException("El evento debe tener al menos una instrucción");
        }

        this.tipo = tipo;
        this.instrucciones = instrucciones;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public MyList<String> getInstrucciones() {
        return instrucciones;
    }

    @Override
    public String toString() {
        return "EVENT: " + tipo;
    }
}