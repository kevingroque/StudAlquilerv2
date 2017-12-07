package app.roque.com.studalquilerv2.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.fragments.CentrosEducativosFragment;
import app.roque.com.studalquilerv2.fragments.ListaDeInmueblesFragment;
import app.roque.com.studalquilerv2.models.Usuario;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrincipalActivity extends AppCompatActivity {

    private static final String TAG = PrincipalActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_account_edit
    };
    private EditText inputUsername;
    private EditText inputPassword;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal) ;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CentrosEducativosFragment(), "");
        adapter.addFrag(new ListaDeInmueblesFragment(), "");
        //  adapter.addFrag(new LoginFragment(), "");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.login_menu:
                showDialog(getApplication());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public  void showDialog(Context view){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_login);

        Button btnRegister = (Button) dialog.findViewById(R.id.registerDialog_button);
        Button btnLogin = (Button) dialog.findViewById(R.id.loginDialog_button);
        ImageButton btnClose = (ImageButton)dialog.findViewById(R.id.closeBtn);
        inputUsername = (EditText) dialog.findViewById(R.id.usernameDialog_input);
        inputPassword = (EditText) dialog.findViewById(R.id.passwordDialog_input);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // username remember
        String username = sharedPreferences.getString("username", null);
        if(username != null){
            inputUsername.setText(username);
            inputPassword.requestFocus();
        }

        // islogged remember
        if(sharedPreferences.getBoolean("islogged", false)){
            // Go to Dashboard
            goDashboard();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Cancel process!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!username.isEmpty() && !password.isEmpty()) {
                    // login user
                    pDialog.setMessage("Ingresando ...");
                    showDialog();
                    checkLogin();

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(view.getContext(),
                            "Campos incompletos!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(PrincipalActivity.this,CrearCuentaActivity.class);
                startActivity(i);
            }
        });


        dialog.show();
    }

    private void checkLogin() {

        final String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(PrincipalActivity.this, "Debe completar todo los campos", Toast.LENGTH_SHORT).show();
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<Usuario> call = null;
        call = service.loginUser(username, password);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                try {
                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    hideDialog();
                    if (response.isSuccessful()) {

                        Usuario responseMessage = response.body();
                        assert responseMessage !=null;
                        Integer usuario_id = responseMessage.getId();
                        String nombres = responseMessage.getNombres();
                        String apellidos = responseMessage.getApellidos();
                        String tipo = responseMessage.getTipo();
                        String telefono = responseMessage.getTelefono();
                        String correo = responseMessage.getCorreo();
                        String imagen = responseMessage.getImagen();
                        String estado = responseMessage.getEstado();

                        Log.d(TAG, "responseMessage: " + responseMessage);
                        Log.d(TAG, "correo: " + correo);
                        Log.d(TAG, "usuario_id: " + usuario_id);
                        Log.d(TAG, "imagen: " + imagen);

                        if(tipo.equals("arrendador") && estado.equals("Habilitado")){

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            boolean success = editor
                                    .putString("usuario_id", String.valueOf(usuario_id))
                                    .putString("username", username)
                                    .putString("nombres", nombres)
                                    .putString("apellidos", apellidos)
                                    .putString("tipo", tipo)
                                    .putString("telefono", telefono)
                                    .putString("correo", correo)
                                    .putString("imagen", imagen)
                                    .putBoolean("islogged", true)
                                    .commit();


                            goDashboard();
                        }else {
                            Log.e(TAG, "onError: " + response.errorBody().string());
                            Toast.makeText(PrincipalActivity.this, "Tipo de usuario incorrecto", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        //progressDialog.dismiss();
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        Toast.makeText(PrincipalActivity.this, "Username o contraseña incorrectos!", Toast.LENGTH_SHORT).show();
                        //throw new Exception();
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                    hideDialog();
                    Toast.makeText(PrincipalActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                hideDialog();
                Toast.makeText(PrincipalActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private  void goDashboard(){
        Intent intent = new Intent(this, NavDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
