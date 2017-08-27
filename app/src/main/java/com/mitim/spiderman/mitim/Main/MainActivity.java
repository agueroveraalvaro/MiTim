package com.mitim.spiderman.mitim.Main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitim.spiderman.mitim.Listado.ListaPartidosFragment;
import com.mitim.spiderman.mitim.Logeo.Login;
import com.mitim.spiderman.mitim.Perfil.PerfilFragment;
import com.mitim.spiderman.mitim.Picasso.CropCircleTransformation;
import com.mitim.spiderman.mitim.R;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView txtNombre;
    private ImageView imgPerfil;
    private ConstraintLayout.LayoutParams containerParams;
    private FrameLayout container;
    private LottieAnimationView animation_view;
    private int i=0;
    private static Boolean isPerfil=false;
    private static int flagArrow=0;
    private static ValueAnimator valueAnimatorGlobal;
    private static String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);

        validarLogin();
        cargarControles();
    }

    private void cargarControles()
    {
        imgPerfil=(ImageView)findViewById(R.id.imgPerfil);
        imgPerfil.setOnClickListener(this);
        animation_view=(LottieAnimationView)findViewById(R.id.animation_view);
        container=(FrameLayout) findViewById(R.id.container);

        cargarListaPartidos();
        animationListeners();
    }

    private void animationListeners()
    {
        animation_view.setScaleX(2f);//aumentar tamaño ver ej en lottie app
        animation_view.setScaleY(2f);//aumentar tamaño ver ej en lottie app

        animation_view.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                valueAnimator.setDuration(2000);
                valueAnimatorGlobal=valueAnimator;
                i=(int)(valueAnimator.getAnimatedFraction()*10);
                if (i==5 && flagArrow==0)
                {
                    flagArrow=1;
                    valueAnimator.pause();
                }
                if(i==10)
                {
                    flagArrow=0;
                }
            }
        });
    }

    private void validarLogin()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                System.out.println("Obtiene el usuario para ver si es null o no");
                if (user != null)
                {
                    uid=user.getUid();
                    txtNombre=(TextView)findViewById(R.id.txtNombre);
                    if(user.getDisplayName()==null)//si no tiene nombre, solo email
                    {
                        int a=user.getEmail().indexOf("@");
                        String nombre=user.getEmail().substring(0,a);
                        txtNombre.setText(nombre);
                    }
                    else//si viene con nombre
                    {
                        int a=user.getDisplayName().indexOf(" ");
                        String nombre=user.getDisplayName().substring(0,a);
                        txtNombre.setText(nombre);
                    }
                    cargarFotoPerfil(""+user.getPhotoUrl());
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                }
                else
                {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                    //me voy a login
                    Intent i=new Intent(getApplicationContext(),Login.class);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

    public void cargarFotoPerfil(String photoUrl)
    {
        try
        {
            System.out.println("URL de imagen : "+photoUrl);
            Picasso.with(this).load(photoUrl)
                    .transform(new CropCircleTransformation())
                    .into(imgPerfil);
        }
        catch (Exception ex)
        {
            System.out.println("Error al poner imagen !! "+ex.getMessage());
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.imgPerfil:
                if (isPerfil==false)
                {
                    isPerfil = true;
                    efectoRippple();

                    animation_view.playAnimation();//procede a arrow

                    PerfilFragment f = new PerfilFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, f, "PerfilFragment")
                            .addToBackStack("ListaPartidosFragment")
                            .commit();
                }
                break;
        }
    }

    private void efectoRippple()
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
        anim.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            //............
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ajustarContainerMain()
    {
        if(containerParams!=null) {
            container.setLayoutParams(containerParams);//Le paso los parametros originales
        }
    }

    private void ajustarContainer()
    {
        //Guardo parametros originales
        containerParams= (ConstraintLayout.LayoutParams)container.getLayoutParams();
        //Obtengo tamaño de pantalla actual
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Creo nuevos parametros
        ConstraintLayout.LayoutParams containerNewParams=new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerNewParams.width=width;
        containerNewParams.height=height;
        container.setLayoutParams(containerNewParams);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void cargarListaPartidos()
    {
        ListaPartidosFragment fragment=new ListaPartidosFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.container,fragment,"ListaPartidosFragment")
                .commit();
    }

    public static void animationArrowToHamburger()
    {
        flagArrow=1;//para que pase de largo en update listener
        valueAnimatorGlobal.resume();//para que siga su camino
        isPerfil=false;
    }

    public static String obtenerUID()
    {
        return uid;
    }

    @Override
    public void onBackPressed()
    {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
            if (f instanceof PerfilFragment)
            {
                MainActivity.animationArrowToHamburger();//anima hacia hamburger inmediatamente
            }
            getSupportFragmentManager().popBackStack();//se va para atras si o si
        }
        else
        {
            super.onBackPressed();
        }
    }
}
