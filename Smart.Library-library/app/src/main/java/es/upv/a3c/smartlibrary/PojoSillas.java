package es.upv.a3c.smartlibrary;

public class PojoSillas {
    private String  numero;
    private String estado;

public  PojoSillas(){}

    public PojoSillas(String numero , String estado) {
        this.numero = numero;
        this.estado = estado;
    }
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
