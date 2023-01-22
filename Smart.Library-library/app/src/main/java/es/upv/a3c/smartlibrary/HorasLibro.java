package es.upv.a3c.smartlibrary;

public class HorasLibro {

    private  Integer ID;
    private String HorasLibros;
    private String HoraCustom;

    public HorasLibro(int i, String horasLibros) {
        HorasLibros = horasLibros;
    }


    public HorasLibro(Integer ID) {
        this.ID = ID;
    }




    public HorasLibro() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        this.HoraCustom = this.HoraCustom =HorasLibros;
        return HoraCustom;

    }

    public String getHorasLibros() {
        return HorasLibros;
    }

    public void setHorasLibros(String horasLibros) {
        HorasLibros = horasLibros;
    }
}
