package app.roque.com.studalquilerv2.services;

import java.util.List;

import app.roque.com.studalquilerv2.models.CentroEducativo;
import app.roque.com.studalquilerv2.models.Inmueble;
import app.roque.com.studalquilerv2.models.Usuario;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    String API_BASE_URL = "https://proyecto-alquiler-api-kevinghanz.c9users.io/";

    @GET("api/v1/inmuebles")
    Call<List<Inmueble>> getImuebles();

    @GET("api/v1/centros_educativos")
    Call<List<CentroEducativo>> getCentrosEducativos();

    @GET("api/v1/inmuebles/{id}")
    Call<Inmueble> showInmueble(@Path("id") Integer id);

    @GET("api/v1/centros_educativos/{id}")
    Call<Inmueble> showCentroEducativo(@Path("id") Integer id);

    @GET("api/v1/centros_educativos/{id}/inmuebles")
    Call<List<Inmueble>> getCentrosEducativosInmuebles(@Path("id") Integer id);

    @GET("api/v1/usuarios/{id}/inmuebles")
    Call<List<Inmueble>> getUsuariosInmuebles(@Path("id") String id);

    @GET("api/v1/usuarios/{id}")
    Call<Usuario> showUsuario(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/v1/login")
    Call<Usuario> loginUser
            (@Field("username") String username,
             @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/v1/usuarios")
    Call<ResponseMessage> createUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("nombres") String nombre,
            @Field("apellidos") String apellidos,
            @Field("correo") String correo,
            @Field("tipo") String tipo,
            @Field("telefono") String telefono,
            @Field("estado") String estado);

    @Multipart
    @POST("/api/v1/usuarios")
    Call<ResponseMessage> createUserWithImage(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("nombres") RequestBody nombres,
            @Part("apellidos") RequestBody apellidos,
            @Part("correo") RequestBody correo,
            @Part("tipo") RequestBody tipo,
            @Part("telefono") RequestBody telefono,
            @Part("estado") RequestBody estado,
            @Part MultipartBody.Part imagen
    );


    @FormUrlEncoded
    @POST("/api/v1/usuarios/{id}/inmuebles")
    Call<ResponseMessage> createInmueble(
            @Path("id") String id,
            @Field("tipo") String tipo,
            @Field("direccion") String direccion,
            @Field("distrito") String distrito,
            @Field("departamento") String departamento,
            @Field("longitud") String longitud,
            @Field("latitud") String latitud,
            @Field("precio") String precio,
            @Field("num_dormitorios") String num_dormitorios,
            @Field("num_banios") String num_banios,
            @Field("area_total") String area_total,
            @Field("estado") String estado,
            @Field("descripcion") String descripcion,
            @Field("centro_educativo_id") String centro_educativo_id);

    @Multipart
    @POST("/api/v1/usuarios/{id}/inmuebles")
    Call<ResponseMessage> createInmuebleWithImage(
            @Path("id") String id,
            @Part("tipo") RequestBody tipo,
            @Part("direccion") RequestBody direccion,
            @Part("distrito") RequestBody distrito,
            @Part("departamento") RequestBody departamento,
            @Part("longitud") RequestBody longitud,
            @Part("latitud") RequestBody latitud,
            @Part("latitud") RequestBody precio,
            @Part("num_dormitorios") RequestBody num_dormitorios,
            @Part("num_banios") RequestBody num_banios,
            @Part("area_total") RequestBody area_total,
            @Part("estado") RequestBody estado,
            @Part("descripcion") RequestBody descripcion,
            @Part("centro_educativo_id") RequestBody centro_educativo_id,
            @Part MultipartBody.Part imagen
    );

    @PUT("/api/v1/usuarios/{id}")
    @FormUrlEncoded
    Call<Usuario> updateUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("nombres") String nombre,
            @Field("apellidos") String apellidos,
            @Field("correo") String correo,
            @Field("tipo") String tipo,
            @Field("telefono") String telefono,
            @Field("estado") String estado);

}