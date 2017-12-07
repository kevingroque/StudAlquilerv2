package app.roque.com.studalquilerv2.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import app.roque.com.studalquilerv2.services.ResponseMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearInmuebleActivity extends AppCompatActivity {

    private static final String TAG = CrearInmuebleActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;
    private EditText tipoInput, direccionInput, precioInput, distritoInput, departamentoInput, /*longitudInput, latitudInput,*/ numDormInput, numBanInput, areaInput, descripcionInput, centroIdInput;
    private ImageButton btnMap;
    private ImageView imagePreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_inmueble);

        showToolbar("Crear Inmueble", true);

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        tipoInput = (EditText) findViewById(R.id.tipoInmueble_input);
        direccionInput = (EditText) findViewById(R.id.direccionInmueble_input);
        distritoInput = (EditText) findViewById(R.id.distritoInmueble_input);
        departamentoInput = (EditText) findViewById(R.id.departamentoInmueble_input);
        //longitudInput = (EditText) findViewById(R.id.longitudInmueble_input);
        //latitudInput = (EditText) findViewById(R.id.latitudInmueble_input);
        precioInput = (EditText) findViewById(R.id.precioInmueble_input);
        numDormInput = (EditText) findViewById(R.id.numHabInmueble_input);
        numBanInput = (EditText) findViewById(R.id.numBañosInmueble_input);
        areaInput = (EditText) findViewById(R.id.areaTotalInmueble_input);
        descripcionInput = (EditText) findViewById(R.id.descripcionInmueble_input);
        centroIdInput = (EditText) findViewById(R.id.centroInmueble_input);
        imagePreview = (ImageView)findViewById(R.id.imagenInmueble_preview);
        btnMap = (ImageButton) findViewById(R.id.imgBtnMap);

        btnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(CrearInmuebleActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


    }


    private static final int CAPTURE_IMAGE_REQUEST = 400;

    private Uri mediaFileUri;

    public void takePictureInmueble(View view) {
        try {

            if (!permissionsGranted()) {
                ActivityCompat.requestPermissions(CrearInmuebleActivity.this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
                return;
            }

            // Creando el directorio de imágenes (si no existe)
            File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    throw new Exception("Failed to create directory");
                }
            }

            // Definiendo la ruta destino de la captura (Uri)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            mediaFileUri = Uri.fromFile(mediaFile);

            // Iniciando la captura
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Error en captura: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String address = sharedPreferences.getString("direccion", "Ubicación...");
        direccionInput.setText(address);

        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            // Resultado en la captura de la foto
            if (resultCode == RESULT_OK) {
                try {
                    Log.d(TAG, "ResultCode: RESULT_OK");
                    // Toast.makeText(this, "Image saved to: " + mediaFileUri.getPath(), Toast.LENGTH_LONG).show();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);

                    // Reducir la imagen a 800px solo si lo supera
                    bitmap = scaleBitmapDown(bitmap, 800);

                    imagePreview.setImageBitmap(bitmap);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(this, "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "ResultCode: RESULT_CANCELED");
            } else {
                Log.d(TAG, "ResultCode: " + resultCode);
            }
        }
    }

    public void callRegisterInmueble(View view) {

        String tipo = tipoInput.getText().toString();
        //String direccion = direccionInput.getText().toString();
        String distrito = distritoInput.getText().toString();
        String departamento = departamentoInput.getText().toString();
        //String longitud = longitudInput.getText().toString();
        //String latitud = latitudInput.getText().toString();
        String precio = precioInput.getText().toString();
        String num_dormitorios = numDormInput.getText().toString();
        String num_banios = numBanInput.getText().toString();
        String area_total = areaInput.getText().toString();
        String estado = "Habilitado";
        String descripcion = descripcionInput.getText().toString();
        String centro_educativo_id = centroIdInput.getText().toString();


        // get id from SharedPreferences
        final String usuario_id = sharedPreferences.getString("usuario_id", null);
        Log.d(TAG, "usuario_id: " + usuario_id);


        if (tipo.isEmpty() || /*direccion.isEmpty() || */distrito.isEmpty()
                || departamento.isEmpty() /*||longitud.isEmpty() || latitud.isEmpty()*/
                || precio.isEmpty() || num_dormitorios.isEmpty() || num_banios.isEmpty()
                || area_total.isEmpty() || descripcion.isEmpty() || centro_educativo_id.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<ResponseMessage> call = null;

        String com = sharedPreferences.getString("direccion", null);
        Log.d(TAG, "comcomcom: " + com);

        if (com == null || com == "") {

            Toast.makeText(this, "Ubica el lugar del inmueble.", Toast.LENGTH_LONG).show();

        } else {

            pDialog.setMessage("Registrando inmueble ...");
            showDialog();

            String direccion = sharedPreferences.getString("direccion", null);
            String latitud = sharedPreferences.getString("latitud", null);
            String longitud = sharedPreferences.getString("longitud", null);

            direccionInput.setText(direccion);
            //latitudInput.setText(latitud);
            //longitudInput.setText(longitud);

            Log.d(TAG, "direccion: " + direccion);
            Log.d(TAG, "latitud: " + latitud);
            Log.d(TAG, "longitud: " + longitud);

            if (mediaFileUri == null) {
                // Si no se incluye imagen hacemos un envío POST simple
                call = service.createInmueble(usuario_id, tipo, direccion, distrito, departamento, longitud, latitud, precio, num_dormitorios, num_banios, area_total, estado, descripcion, centro_educativo_id);


            } else {
                // Si se incluye hacemos envió en multiparts

                File file = new File(mediaFileUri.getPath());
                Log.d(TAG, "File: " + file.getPath() + " - exists: " + file.exists());

                // Podemos enviar la imagen con el tamaño original, pero lo mejor será comprimila antes de subir (byteArray)
                // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

                Bitmap bitmap = BitmapFactory.decodeFile(mediaFileUri.getPath());

                // Reducir la imagen a 800px solo si lo supera
                bitmap = scaleBitmapDown(bitmap, 800);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
                MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

                RequestBody tipoPart = RequestBody.create(MultipartBody.FORM, tipo);
                RequestBody direccionPart = RequestBody.create(MultipartBody.FORM, direccion);
                RequestBody distritoPart = RequestBody.create(MultipartBody.FORM, distrito);
                RequestBody departamentoPart = RequestBody.create(MultipartBody.FORM, departamento);
                RequestBody longitudPart = RequestBody.create(MultipartBody.FORM, longitud);
                RequestBody latitudPart = RequestBody.create(MultipartBody.FORM, latitud);
                RequestBody precioPart = RequestBody.create(MultipartBody.FORM, precio);
                RequestBody num_dormitoriosPart = RequestBody.create(MultipartBody.FORM, num_dormitorios);
                RequestBody num_baniosPart = RequestBody.create(MultipartBody.FORM, num_banios);
                RequestBody area_totalPart = RequestBody.create(MultipartBody.FORM, area_total);
                RequestBody estadoPart = RequestBody.create(MultipartBody.FORM, estado);
                RequestBody descripcionPart = RequestBody.create(MultipartBody.FORM, descripcion);
                RequestBody centro_educativo_idPart = RequestBody.create(MultipartBody.FORM, centro_educativo_id);

                call = service.createInmuebleWithImage(usuario_id, tipoPart, direccionPart, distritoPart, departamentoPart, longitudPart, latitudPart, precioPart, num_dormitoriosPart, num_baniosPart, area_totalPart, estadoPart, descripcionPart, centro_educativo_idPart, imagenPart);

            }

            call.enqueue(new Callback<ResponseMessage>() {
                @Override
                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                    try {

                        int statusCode = response.code();
                        Log.d(TAG, "HTTP status code: " + statusCode);

                        if (response.isSuccessful()) {

                            ResponseMessage responseMessage = response.body();
                            Log.d(TAG, "responseMessage: " + responseMessage);

                            Toast.makeText(CrearInmuebleActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();

                            hideDialog();

                            // Save to SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("latitud", null);
                            editor.putString("longitud", null);
                            editor.putString("direccion", null);
                            editor.commit();

                            finish();

                        } else {

                            hideDialog();

                            Log.e(TAG, "onError: " + response.errorBody().string());
                            throw new Exception("Error en el servicio");
                        }

                    } catch (Throwable t) {
                        try {
                            hideDialog();
                            Log.e(TAG, "onThrowable: " + t.toString(), t);
                            Toast.makeText(CrearInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Throwable x) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                    Toast.makeText(CrearInmuebleActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }

    }



    private static final int PERMISSIONS_REQUEST = 200;

    private static String[] PERMISSIONS_LIST = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean permissionsGranted() {
        for (String permission : PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(CrearInmuebleActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "" + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(CrearInmuebleActivity.this, PERMISSIONS_LIST[i] + " permiso rechazado!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(CrearInmuebleActivity.this, "Permisos concedidos, intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Redimensionar una imagen bitmap
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void showToolbar(String title, boolean upButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}