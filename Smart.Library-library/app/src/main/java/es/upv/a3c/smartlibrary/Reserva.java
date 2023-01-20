package es.upv.a3c.smartlibrary;



import com.google.firebase.firestore.FirebaseFirestore;

public class Reserva {

    // ATRIBUTOS

    private long fecha;
    private String idusuario;


    // MÉTODOS

    // Constructor por defecto.
    public Reserva() {
    }




    // Constructor.
    public Reserva(long fecha, String idusuario) {
        this.fecha = fecha;
        this.idusuario = idusuario;
    }


    // Getters y Setters.

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }
}
//crear metodo Buscar reserva, eliminar
