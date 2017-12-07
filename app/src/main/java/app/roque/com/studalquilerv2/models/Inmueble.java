package app.roque.com.studalquilerv2.models;

/**
 * Created by keving on 20/11/2017.
 */

public class Inmueble {

    private Integer id;
    private String tipo;
    private String direccion;
    private String distrito;
    private String departamento;
    private float latitud;
    private float longitud;
    private float precio;
    private int num_dormitorios;
    private int num_banios;
    private float area_total;
    private String estado;
    private String descripcion;
    private String imagen;
    private Integer usuario_id;
    private Integer centro_educativo_id;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getNum_dormitorios() {
        return num_dormitorios;
    }

    public void setNum_dormitorios(int num_dormitorios) {
        this.num_dormitorios = num_dormitorios;
    }

    public int getNum_banios() {
        return num_banios;
    }

    public void setNum_banios(int num_banios) {
        this.num_banios = num_banios;
    }

    public float getArea_total() {
        return area_total;
    }

    public void setArea_total(float area_total) {
        this.area_total = area_total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getUser_id() {
        return usuario_id;
    }

    public void setUser_id(Integer user_id) {
        this.usuario_id = user_id;
    }

    public Integer getCentro_educativo_id() {
        return centro_educativo_id;
    }

    public void setCentro_educativo_id(Integer centro_educativo_id) {
        this.centro_educativo_id = centro_educativo_id;
    }

    @Override
    public String toString() {
        return "CentroEducativo{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", distrito='" + distrito + '\'' +
                ", departamento='" + departamento + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", precio=" + precio +
                ", num_dormitorios=" + num_dormitorios +
                ", num_banios=" + num_banios +
                ", area_total=" + area_total +
                ", estado='" + estado + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", imagen='" + imagen + '\'' +
                ", user_id=" + usuario_id +
                ", centro_educativo_id=" + centro_educativo_id +
                '}';
    }
}
