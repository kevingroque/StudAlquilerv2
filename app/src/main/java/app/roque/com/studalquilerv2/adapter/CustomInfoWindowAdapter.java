package app.roque.com.studalquilerv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.models.Inmueble;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater = null;
    private List<Inmueble> inmuebles;

    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents( Marker marker) {

        View infoWindows = inflater.inflate(R.layout.infowindow_layout, null);

        TextView tipoText = (TextView)infoWindows.findViewById(R.id.info_window_tipo);
        TextView descripcionText = (TextView)infoWindows.findViewById(R.id.info_window_descripicion);
        TextView precioText = (TextView)infoWindows.findViewById(R.id.info_window_precio);

        tipoText.setText(marker.getTitle());
        descripcionText.setText(marker.getSnippet());
        precioText.setText("S/. " +(int) marker.getAlpha());


        return(infoWindows);
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}