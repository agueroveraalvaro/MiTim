package com.mitim.spiderman.mitim.Perfil;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitim.spiderman.mitim.Picasso.CropCircleTransformation;
import com.mitim.spiderman.mitim.R;
import com.squareup.picasso.Picasso;

public class PerfilFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View v;
    private ImageView imgPerfil;
    private OnFragmentInteractionListener mListener;
    private Spinner spinner,spinnerCamisetas;
    private TextView txtNombre;

    public PerfilFragment() {
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
        v=inflater.inflate(R.layout.fragment_perfil, container, false);

        cargarElementos();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)//es para que funcione efecto riple en imgperfil
        {
            imgPerfil.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    cargarInformacionUsuario();
                    cargarSpinner();
                }
            });
        }

        return v;
    }

    private void cargarElementos()
    {
        imgPerfil=(ImageView)v.findViewById(R.id.imgPerfil);
        spinner = (Spinner)v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinnerCamisetas = (Spinner)v.findViewById(R.id.spinnerCamiseta);
        spinnerCamisetas.setOnItemSelectedListener(this);
        txtNombre=(TextView)v.findViewById(R.id.txtNombre);
    }

    private void cargarSpinner()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.camisetas, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCamisetas.setAdapter(adapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.posiciones, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter2);
    }

    private void cargarInformacionUsuario()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            Uri photoUrl = user.getPhotoUrl();
            String nombre;
            //String uid = user.getUid();

            if(user.getDisplayName()==null)//si no tiene nombre, solo email
            {
                nombre=user.getEmail();
                txtNombre.setText(nombre);
            }
            else//si viene con nombre
            {
                //Algoritmo para sacar los dos primeros nombres los dos primeros palabras del string
                String userName=user.getDisplayName();
                int a=userName.indexOf(" ");
                nombre=userName.substring(0,a)+" ";
                userName=userName.substring((a+1),userName.length());
                a=userName.indexOf(" ");
                if (a==-1)
                {
                    nombre=nombre+userName;
                }
                else
                {
                    nombre=nombre+userName.substring(0,a);
                }
                txtNombre.setText(nombre);
            }

            cargarFotoPerfil(photoUrl+"");
        }
    }

    public void cargarFotoPerfil(String photoUrl)
    {
        try
        {
            Picasso.with(getContext()).load(photoUrl)
                    .transform(new CropCircleTransformation())
                    .into(imgPerfil);
        }
        catch (Exception ex)
        {
            System.out.println("Error al poner imagen !! "+ex.getMessage());
        }
        efectoRipple();
    }

    private void efectoRipple()
    {
        //a continuacion se carga la animacion en la imagen efecto ripple
        // get the center for the clipping circle
        int cx = imgPerfil.getWidth() / 2;
        int cy = imgPerfil.getHeight() / 2;
        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);
        // create the animator for this view (the start radius is zero)
        Animator anim=ViewAnimationUtils.createCircularReveal(imgPerfil, cx, cy, 0, finalRadius);
        // make the view visible and start the animation
        imgPerfil.setVisibility(View.VISIBLE);//probar si sirve para pantalla completa
        anim.setDuration(700);
        anim.start();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {

        }
    }

    //@Override
    //public void onBackPressed(){
    //    FragmentManager fm = getFragmentManager();
    //    if (fm.getBackStackEntryCount() > 0) {
    //        System.out.println("MainActivity"+"popping backstack");
    //        fm.popBackStack();
    //
    //    } else {
    //        System.out.println("MainActivity"+"nothing on backstack, calling super");
    //        super.onBackPressed();
    //    }
    //}

    private void signOut() {
        //    // Firebase sign out
        //    mAuth.signOut();
//
        //    // Google sign out
        //    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        //            new ResultCallback<Status>() {
        //                @Override
        //                public void onResult(@NonNull Status status) {
        //                    updateUI(null);
        //                }
        //            });
    }

    @Override
    public void onDetach()
    {
        //MainActivity.animationArrowToHamburger();//anima hacia hamburger
        super.onDetach();
        mListener = null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
        //System.out.println("Selecciono : " + parent.getSelectedItem().toString() + " Posicion : " + position);
        //Toast.makeText(getContext(), "Ahora es un " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
