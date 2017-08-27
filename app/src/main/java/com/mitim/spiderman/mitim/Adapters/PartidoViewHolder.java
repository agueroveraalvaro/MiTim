package com.mitim.spiderman.mitim.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mitim.spiderman.mitim.R;

/**
 * Created by Spiderman on 8/11/2017.
 */

public class PartidoViewHolder extends RecyclerView.ViewHolder {

    public TextView txtTitulo,txtLugar,txtHora,txtJugadores,txtCreador;

    public PartidoViewHolder(View v)
    {
        super(v);
        txtTitulo=(TextView)v.findViewById(R.id.txtTitulo);
        txtLugar=(TextView)v.findViewById(R.id.txtLugar);
        txtHora=(TextView)v.findViewById(R.id.txtHora);
        txtJugadores=(TextView)v.findViewById(R.id.txtJugadores);
        txtCreador=(TextView)v.findViewById(R.id.txtCreador);
    }
}
