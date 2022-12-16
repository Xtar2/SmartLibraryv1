package es.upv.a3c.smartlibrary;

public class ReservasVo {


        String isbn,descripcion,img,nombre,fecha;

        public ReservasVo() {
        }

    public ReservasVo(String fecha) {
        this.fecha = fecha;
    }

    public ReservasVo(String ISBN, String descripcion, String img, String nombre) {
            this.isbn = ISBN;
            this.descripcion = descripcion;
            this.img = img;
            this.nombre = nombre;
        }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        @Override
        public String toString() {
            return "LibrosVo{" +
                    "isbn='" + isbn + '\'' +
                    ", descripcion='" + descripcion + '\'' +
                    ", img='" + img + '\'' +
                    ", nombre='" + nombre + '\'' +
                    '}';
        }
    }

