package app.roque.com.studalquilerv2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.adapter.InmueblesAdapter;
import app.roque.com.studalquilerv2.models.Inmueble;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CentroEducativoInmuebleActivity extends AppCompatActivity {

    private static final String TAG = CentroEducativoInmuebleActivity.class.getSimpleName();
    private RecyclerView inmuebleList;
    private Integer id;
    private String imagen;
    private ImageView imgHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_educativo_inmueble);

        showToolbar("Inmuebles",true);

        inmuebleList = (RecyclerView)findViewById(R.id.recyclerviewCentrosInmueble);
        imgHead = (ImageView)findViewById(R.id.imageCentroInmueble);

        inmuebleList.setLayoutManager(new LinearLayoutManager(this));
        inmuebleList.setAdapter(new InmueblesAdapter(this));

        id = getIntent().getExtras().getInt("ID");
        Log.e(TAG, "id:" + id);

        imagen = getIntent().getExtras().getString("IMAGEN");
        Log.e(TAG, "imagem:" + imagen);

        String url = ApiService.API_BASE_URL + "/images/centroseducativos/"+ imagen;
        Picasso.with(this).load(url).into(imgHead);


        initialize();
    }

    public void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Inmueble>> call = service.getCentrosEducativosInmuebles(id);

        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Inmueble> inmuebles = response.body();
                        Log.d(TAG, "inmuebles: " + inmuebles);

                        InmueblesAdapter adapter = (InmueblesAdapter) inmuebleList.getAdapter();
                        adapter.setInmuebles(inmuebles);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(CentroEducativoInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(CentroEducativoInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
