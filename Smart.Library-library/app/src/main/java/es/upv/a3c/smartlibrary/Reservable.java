package es.upv.a3c.smartlibrary;

import java.util.ArrayList;
import java.util.List;

public abstract class Reservable {
    // ATRIBUTOS

    public static final int NO_ESTA = -1;
    protected List<Reserva> listaReservas;
    private String id;


    // MÉTODOS

    // Constructor por defecto.
    public Reservable(String id) {
        this.id = id;
        this.listaReservas = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    // Buscar reserva por fecha.
    public int buscarReserva(long fecha) {
        for (int i = 0; i < this.listaReservas.size(); i++) {
            if (this.listaReservas.get(i).getFecha() == fecha) {
                return i;
            }
        }
        return NO_ESTA;
    }

    // Cantidad de reservas
    public int numReservas() {
        return this.listaReservas.size();
    }

    // Insertar reserva si no coincide con la fecha.
    // Devuelve si la inserción ha sido realizada.
    public boolean insertarReserva(Reserva reserva) {
        int pos = buscarReserva(reserva.getFecha());
        if (pos == NO_ESTA) {
            this.listaReservas.add(reserva);
            return true;
        }
        return false;
    }

    // Eliminar reserva si coincide con la fecha.
    // Devuelve si la eliminación ha sido realizada.
    public boolean eliminarReserva(Reserva reserva) {
        int pos = buscarReserva(reserva.getFecha());
        if (pos != NO_ESTA) {
            this.listaReservas.remove(pos);
            return true;
        }
        return false;
    }

    // Obtener una reserva por su posicion
    public Reserva getReserva (int pos){
        return this.listaReservas.get (pos);
    }

}
