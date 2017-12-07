package app.roque.com.studalquilerv2.fragments;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.adapter.CentrosEducativosAdapter;
import app.roque.com.studalquilerv2.models.CentroEducativo;
import app.roque.com.studalquilerv2.models.Inmueble;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CentrosEducativosFragment extends Fragment {

    private static final String TAG = CentrosEducativosFragment.class.getSimpleName();
    private RecyclerView centroList;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeLayout;
    List<CentroEducativo> centroEducativos;
    CentrosEducativosAdapter adapter;

    public CentrosEducativosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_centros_educativos, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        centroList = (RecyclerView) view.findViewById(R.id.recyclerviewCentros);
        //swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshCentros);

        centroList.setLayoutManager(new GridLayoutManager(getContext(),2));
        centroList.setItemAnimator(new DefaultItemAnimator());
        centroList.setAdapter(new CentrosEducativosAdapter(getActivity()));
        initialize();
//
//        swipeLayout.setColorSchemeResources(R.color.Screen1,R.color.Screen2, R.color.Screen3, R.color.Screen4);
//
//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new HackingBackgroundTask().execute();
//            }
//        });

        return view;
    }

    private int dpToPx(int dp){
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,r.getDisplayMetrics()));
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<CentroEducativo>> call = service.getCentrosEducativos();

        call.enqueue(new Callback<List<CentroEducativo>>() {
            @Override
            public void onResponse(Call<List<CentroEducativo>> call, Response<List<CentroEducativo>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        centroEducativos = response.body();
                        Log.d(TAG, "centrosEducativos: " + centroEducativos);

                        adapter = (CentrosEducativosAdapter) centroList.getAdapter();
                        adapter.setCentrosEducativos(centroEducativos);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<CentroEducativo>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }


    private class HackingBackgroundTask extends AsyncTask<Void, Void, List<Inmueble>> {

        static final int DURACION = 2500;

        @Override
        protected List doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            return centroEducativos;
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adapter.clear();

            // Añadir elementos nuevos
            initialize();
            adapter.addAll(result);

            // Parar la animación del indicador
            swipeLayout.setRefreshing(false);
        }

    }

}
