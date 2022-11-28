package es.upv.a3c.smartlibrary;

public class LibrosVo {

    String isbn,descripcion,img,nombre;

    public LibrosVo() {
    }

    public LibrosVo(String ISBN, String descripcion, String img, String nombre) {
        this.isbn = ISBN;
        this.descripcion = descripcion;
        this.img = img;
        this.nombre = nombre;
    }

    public String getISBN() {
        return isbn;
    }

    public void setISBN(String ISBN) {
        this.isbn = ISBN;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;

    }
}
