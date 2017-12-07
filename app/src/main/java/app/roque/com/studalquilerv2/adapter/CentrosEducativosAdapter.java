package app.roque.com.studalquilerv2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.activity.CentroEducativoInmuebleActivity;
import app.roque.com.studalquilerv2.models.CentroEducativo;
import app.roque.com.studalquilerv2.services.ApiService;


public class CentrosEducativosAdapter extends RecyclerView.Adapter<CentrosEducativosAdapter.ViewHolder> {

    private static final String TAG = CentrosEducativosAdapter.class.getSimpleName();
    private List<CentroEducativo> centroEducativos;
    private Activity activity;

    public CentrosEducativosAdapter(Activity activity){
        this.centroEducativos = new ArrayList<>();
        this.activity = activity;
    }

    public void setCentrosEducativos(List<CentroEducativo> centroEducativos){
        this.centroEducativos = centroEducativos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        //public ImageView logo;
        public TextView nombreText;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.logoCentroEducativo);
            //logo = (ImageView)itemView.findViewById(R.id.logoCentroEducativo);
            nombreText = (TextView)itemView.findViewById(R.id.nombreCentroEducativo);
        }
    }

    @Override
    public CentrosEducativosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_centros_educativos, parent, false);
        return new CentrosEducativosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CentrosEducativosAdapter.ViewHolder viewHolder, int position) {

        final CentroEducativo centroEducativo = this.centroEducativos.get(position);

        viewHolder.nombreText.setText(centroEducativo.getNombre());

        String url = ApiService.API_BASE_URL + "/images/centroseducativos/" + centroEducativo.getImagen();
        Picasso.with(viewHolder.itemView.getContext()).load(url).into(viewHolder.image);

        //String urlLogo = ApiService.API_BASE_URL + "/images/centroseducativos/" + centroEducativo.getLogo();
        //Picasso.with(viewHolder.itemView.getContext()).load(urlLogo).into(viewHolder.logo);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CentroEducativoInmuebleActivity.class);
                intent.putExtra("ID", centroEducativo.getId());
                intent.putExtra("IMAGEN", centroEducativo.getLogo());
                activity.startActivity(intent);
            }
        });
    }

    public void addAll(List<CentroEducativo> lista){
        centroEducativos.addAll(lista);
        notifyDataSetChanged();
    }

    /*
    Permite limpiar todos los elementos del recycler
     */
    public void clear(){
        centroEducativos.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.centroEducativos.size();
    }

}
