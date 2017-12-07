package app.roque.com.studalquilerv2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.models.Inmueble;
import app.roque.com.studalquilerv2.models.Usuario;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInmuebleActivity extends AppCompatActivity {

    private static final String TAG = DetalleInmuebleActivity.class.getSimpleName();

    private Integer id;
    private FloatingActionButton fabMap, fabCall;
    private ImageView fotoImage, fotoProfile;
    private TextView tipoText, detallesText,precioText,
            direccionText, distritoText, departamentoText,
            dormText, bañosText, areaText,
            nombresText, apellidosText, usernameText, correoText, telefonoText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_inmueble);

        showToolbar("Detalles",true);
        fotoImage = (ImageView)findViewById(R.id.imageDetalle);
        fotoProfile = (ImageView)findViewById(R.id.imageProfileDetalle);
        tipoText = (TextView)findViewById(R.id.tipoInmuebleDetalle);
        detallesText = (TextView)findViewById(R.id.detalleInmueble);
        precioText = (TextView)findViewById(R.id.precioDetalle);
        direccionText = (TextView)findViewById(R.id.direccionDetalle);
        distritoText = (TextView)findViewById(R.id.distritoDetalle);
        departamentoText = (TextView)findViewById(R.id.departamentoDetalle);
        dormText = (TextView)findViewById(R.id.numDormiDetalle);
        bañosText = (TextView)findViewById(R.id.numBanioDetalle);
        areaText = (TextView)findViewById(R.id.areaTotalDetalle);
        nombresText = (TextView)findViewById(R.id.nombresProfileDetalle);
        apellidosText = (TextView)findViewById(R.id.apellidosProfileDetalle);
        usernameText = (TextView)findViewById(R.id.usernameProfileDetalle);
        correoText = (TextView)findViewById(R.id.correoProfileDetalle);
        telefonoText = (TextView)findViewById(R.id.telefonoProfileDetalle);
        fabMap = (FloatingActionButton)findViewById(R.id.fabMapa);
        fabCall = (FloatingActionButton)findViewById(R.id.fabLlamar);

        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalleInmuebleActivity.this, MapsInmueblesActivity.class);
                startActivity(intent);
            }
        });

        id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "id:" + id);

        initialize();
    }

    private void initialize() {

        final ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Inmueble> call = service.showInmueble(id);

        call.enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        Inmueble inmueble = response.body();
                        Log.d(TAG, "inmueble: " + inmueble);
                        Log.d(TAG, "inmueble: " + inmueble.getArea_total());

                        String url = ApiService.API_BASE_URL + "images/inmuebles/" + inmueble.getImagen();
                        Picasso.with(DetalleInmuebleActivity.this).load(url).into(fotoImage);

                        tipoText.setText(inmueble.getTipo());
                        detallesText.setText(inmueble.getDescripcion());
                        precioText.setText("Precio: S/. " + inmueble.getPrecio());
                        direccionText.setText(inmueble.getDireccion());
                        distritoText.setText(inmueble.getDistrito());
                        departamentoText.setText(inmueble.getDepartamento());
                        dormText.setText(String.valueOf(inmueble.getNum_dormitorios()));
                        bañosText.setText(String.valueOf(inmueble.getNum_banios()));
                        areaText.setText(String.valueOf(inmueble.getArea_total())+ "m²");

                        String usuario_id = String.valueOf(inmueble.getUser_id());

                        Call<Usuario> call2 = service.showUsuario(usuario_id);

                        call2.enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call2, Response<Usuario> response) {
                                try {

                                    int statusCode = response.code();
                                    Log.d(TAG, "HTTP status code: " + statusCode);

                                    if (response.isSuccessful()) {

                                        Usuario usuario = response.body();
                                        Log.d(TAG, "usuario: " + usuario);

                                        String url = ApiService.API_BASE_URL + "images/usuarios/" + usuario.getImagen();
                                        Picasso.with(DetalleInmuebleActivity.this).load(url).into(fotoProfile);

                                        nombresText.setText(usuario.getNombres());
                                        apellidosText.setText(usuario.getApellidos());
                                        usernameText.setText(usuario.getUsername());
                                        correoText.setText(usuario.getCorreo());
                                        telefonoText.setText(usuario.getTelefono());


                                    } else {
                                        Log.e(TAG, "onError: " + response.errorBody().string());
                                        throw new Exception("Error en el servicio");
                                    }

                                } catch (Throwable t) {
                                    try {
                                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                                        //Toast.makeText(DetalleInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }catch (Throwable x){}
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.toString());
                                Toast.makeText(DetalleInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });


                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        //Toast.makeText(DetalleInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<Inmueble> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DetalleInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void getUsuario() {

    }

    public void callNumber(View view){
        String telefono = telefonoText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+telefono));
        startActivity(intent);
    }

    public void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
