package com.mitim.spiderman.mitim.Listado;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mitim.spiderman.mitim.Adapters.PartidoViewHolder;
import com.mitim.spiderman.mitim.AnimationRecyclerView.ScaleInAnimationAdapter;
import com.mitim.spiderman.mitim.Crear.CrearPartidoFragment;
import com.mitim.spiderman.mitim.Modelo.Partido;
import com.mitim.spiderman.mitim.R;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class ListaPartidosFragment extends Fragment implements View.OnClickListener {

    private View v;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recycler_lista_partidos;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Partido,PartidoViewHolder> adapter;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    public ListaPartidosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_lista_partidos, container, false);
        cargarElementos();
        return v;
    }

    private void cargarElementos()
    {
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        recycler_lista_partidos=(RecyclerView)v.findViewById(R.id.recycler_lista_partidos);
        fab=(FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //unab-negocio-oportunidad duplicar negocios-volvio hace 2 meses-desups de graduarse
        //app desc
        //mitch marketing and rob program
        //david ron profe univ california - chico-state
        //prototiopo real 30 nov

        DatabaseReference dbPartidos =
                FirebaseDatabase.getInstance().getReference().child("mitim_bd").child("Partidos");
        final Query query = dbPartidos.limitToFirst(50).orderByChild("hora");

        adapter=new FirebaseRecyclerAdapter<Partido,PartidoViewHolder>(
                Partido.class,
                R.layout.card_view_partido,
                PartidoViewHolder.class,
                //referencing the node where we want the database to store the data from our Object
                query
        ) {
            @Override
            protected void populateViewHolder(PartidoViewHolder viewHolder,Partido partido,int position) {
                viewHolder.txtTitulo.setText(partido.getTitulo());
                viewHolder.txtLugar.setText(partido.getLugar());
                viewHolder.txtHora.setText(partido.getHora());
                viewHolder.txtJugadores.setText(partido.getJugadores());
                viewHolder.txtCreador.setText("User");
            }
        };

        recycler_lista_partidos.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(getContext());
        recycler_lista_partidos.setLayoutManager(mLayoutManager);

        final AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setDuration(500);
        //alphaAdapter.setInterpolator(new OvershootInterpolator());
        alphaAdapter.setFirstOnly(false);

        recycler_lista_partidos.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        RecyclerView.OnScrollListener mScrollListener=new RecyclerView.OnScrollListener()//listener para animacion de fab cuando llege a tope bottom
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisible = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItemCount==(lastVisible+1) && totalItemCount>=3)//scroll llego al tope en bottom
                {
                    fab.animate().translationY(-150).setDuration(700);
                }
                else
                {
                    if (fab.getY()!=0)//para que solo lo haga 1 vez
                    {
                        fab.animate().translationY(0).setDuration(700);
                    }
                }
            }
        };
        recycler_lista_partidos.addOnScrollListener(mScrollListener);

        dbPartidos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                adapter.notifyDataSetChanged();
                if (progressBar.getVisibility()==View.VISIBLE)
                    progressBar.invalidate();
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName)
            {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
           }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        dbPartidos.addChildEventListener(childEventListener);
        animarFab();
    }

    private void animarFab()
    {
        fab.setY(150f);
        fab.animate().translationY(0f).setDuration(400);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.fab:
                CrearPartidoFragment f=new CrearPartidoFragment();
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction
                        .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right)
                        .replace(R.id.container,f,"CrearPartidoFragment")
                        .addToBackStack("ListaPartidosFragment")
                        .commit();
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
