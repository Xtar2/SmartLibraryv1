package es.upv.a3c.smartlibrary;

public class Foro {
    private String link;
    private String descripcion;
    private String categoria;

    public Foro() {}

    public Foro(String link, String descripcion, String categoria) {
        this.link = link;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
