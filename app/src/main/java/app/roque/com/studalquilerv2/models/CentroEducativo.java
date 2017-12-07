package app.roque.com.studalquilerv2.models;

/**
 * Created by keving on 20/11/2017.
 */

public class CentroEducativo {

    private Integer id;
    private String tipo;
    private String nombre;
    private String direccion;
    private String logo;
    private String imagen;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "CentroEducativo{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", logo='" + logo + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
