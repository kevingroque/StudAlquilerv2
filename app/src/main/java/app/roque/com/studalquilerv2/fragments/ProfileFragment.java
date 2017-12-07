package app.roque.com.studalquilerv2.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.services.ApiService;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    private ImageView imgProfile;
    private TextView nomTex, apeText, userText, correoText, tipoText, telfText;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = (ImageView)view.findViewById(R.id.imageProfile);
        nomTex = (TextView)view.findViewById(R.id.nombresProfile);
        apeText = (TextView)view.findViewById(R.id.apellidosProfile);
        userText = (TextView)view.findViewById(R.id.usernameProfile);
        correoText = (TextView)view.findViewById(R.id.correoProfile);
        tipoText = (TextView)view.findViewById(R.id.tipoProfile);
        telfText = (TextView)view.findViewById(R.id.telefonoProfile);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String imagen = sharedPreferences.getString("imagen", null);
        Log.d(TAG, "imagen: " + imagen);
        String url = ApiService.API_BASE_URL + "/images/usuarios/" + imagen;
        Picasso.with(getContext()).load(url).into(imgProfile);

        String nombres = sharedPreferences.getString("nombres", null);
        Log.d(TAG, "nombres: " + nombres);

        String apellidos = sharedPreferences.getString("apellidos", null);
        Log.d(TAG, "aapellidos: " + apellidos);

        String username = sharedPreferences.getString("username", null);
        Log.d(TAG, "username: " + username);

        String tipo = sharedPreferences.getString("tipo", null);
        Log.d(TAG, "tipo: " + tipo);

        String telefono = sharedPreferences.getString("telefono", null);
        Log.d(TAG, "telefono: " + telefono);

        String correo = sharedPreferences.getString("correo", null);
        Log.d(TAG, "correo: " + correo);

        nomTex.setText(nombres);
        apeText.setText(apellidos);
        userText.setText(username);
        correoText.setText(correo);
        tipoText.setText(tipo);
        telfText.setText(telefono);

        return view;
    }

}
