package es.upv.a3c.smartlibrary;

public class ReservaSC extends Reserva{
    private String idSC;
    private Reserva reserva;

    //METODOS
    public ReservaSC(String id, Reserva r){
        this.idSC = id;
        this.reserva = r;
    }

    public String getIdSC() {
        return idSC;
    }

    public void setIdSC(String idSC) {
        this.idSC = idSC;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}
