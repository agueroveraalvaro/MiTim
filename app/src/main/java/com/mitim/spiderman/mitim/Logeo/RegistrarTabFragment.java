package com.mitim.spiderman.mitim.Logeo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mitim.spiderman.mitim.Main.MainActivity;
import com.mitim.spiderman.mitim.R;

public class RegistrarTabFragment extends Fragment implements View.OnClickListener {

    private EditText edtEmail,edtContraseña,edtRepetir;
    private Button btnRegistrarse;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private OnFragmentInteractionListener mListener;

    public RegistrarTabFragment() {
        // Required empty public constructor
    }

    public static IngresarTabFragment newInstance(String param1, String param2) {
        IngresarTabFragment fragment = new IngresarTabFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.registrar_tab, container, false);
        cargarElementos(v);
        return v;
    }

    private void cargarElementos(View v)
    {
        mAuth = FirebaseAuth.getInstance();
        edtEmail=(EditText)v.findViewById(R.id.edtEmail);
        edtContraseña=(EditText)v.findViewById(R.id.edtContraseña);
        edtRepetir=(EditText)v.findViewById(R.id.edtRepetirContraseña);
        btnRegistrarse=(Button)v.findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnRegistrarse:
                final String email=edtEmail.getText().toString();
                final String contraseña=edtContraseña.getText().toString();
                String repetir=edtRepetir.getText().toString();
                if (contraseña.compareTo(repetir)==0)
                {
                    mAuth.createUserWithEmailAndPassword(email,contraseña)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    System.out.println("Nombre:"+email+" Contraseña:"+contraseña);
                                    System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());
                                    if (!task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(),"Usuario no registrado :(",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(),"Usuario registrado :)",Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(getContext(),MainActivity.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getContext(),"Debe escribir la misma clave",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

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