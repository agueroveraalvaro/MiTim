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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mitim.spiderman.mitim.Main.MainActivity;
import com.mitim.spiderman.mitim.R;
import com.facebook.FacebookSdk;

public class IngresarTabFragment extends Fragment implements View.OnClickListener {

    private EditText edtEmail,edtContraseña;
    private Button btnLogin;
    SignInButton btnGoogle;
    private FirebaseAuth mAuth;
    private View v;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;

    private OnFragmentInteractionListener mListener;

    public IngresarTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getContext());
        mCallbackManager = CallbackManager.Factory.create();
        v=inflater.inflate(R.layout.ingresar_tab, container, false);
        cargarElementos();
        cargarLoginFB();
        cargarGoogle();
        return v;
    }

    private void cargarGoogle()
    {
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    private void cargarLoginFB()
    {
        // Initialize Facebook Login button
        LoginButton loginButton = (LoginButton)v.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        // If using in a fragment
        System.out.println("Entra al metodo");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                System.out.println("facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                System.out.println("facebook:onCancel");
                System.out.println("CANCELED");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("ERROR");
                System.out.println("facebook:onError "+error);
                Toast.makeText(getContext(),"Error logeo de Facebook :(",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarElementos()
    {
        mAuth = FirebaseAuth.getInstance();
        edtEmail=(EditText)v.findViewById(R.id.edtEmail);
        edtContraseña=(EditText)v.findViewById(R.id.edtContraseña);
        btnLogin=(Button)v.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnGoogle = (SignInButton)v.findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnLogin:
                String email=edtEmail.getText().toString();
                String contraseña=edtContraseña.getText().toString();
                mAuth.signInWithEmailAndPassword(email,contraseña)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful())
                                {
                                    System.out.println("signInWithEmail:failed "+task.getException());
                                    Toast.makeText(getContext(),"Usuario no registrado :(",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //Se dirije hacia mainActivity
                                    Toast.makeText(getContext(),"Ingreso correctamente :)",Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(getContext(),MainActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            }
                        });
                break;
            case R.id.btnGoogle:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        System.out.println("Entra en Activity Result !");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            System.out.println("Entra en RC SIGN !");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                System.out.println("SUCCES GOOGLE+ !");
                Toast.makeText(getContext(),"Ingreso correctamente :)",Toast.LENGTH_SHORT).show();

            } else {
                // Google Sign In failed
                System.out.println("FALLO !! Google Sign In failed.");
                Toast.makeText(getContext(),"Fallo en logeo :(",Toast.LENGTH_SHORT).show();

            }
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        System.out.println("firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getActivity().finish();
                            Intent i=new Intent(getActivity(),MainActivity.class);
                            getActivity().startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure"+task.getException());
                            Toast.makeText(getContext(), "Autenticacion falló :(",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token)
    {
        System.out.println("handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            getActivity().finish();
                            Intent i=new Intent(getActivity(),MainActivity.class);
                            getActivity().startActivity(i);
                            System.out.println("Nos vamos a MainActivity");

                        } else
                            {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure "+task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
