package es.upv.a3c.smartlibrary;

import java.util.Date;

public class ReservaLibrosConstructor {
    private String isbn;
 private Date fecha;

    public ReservaLibrosConstructor() {
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

