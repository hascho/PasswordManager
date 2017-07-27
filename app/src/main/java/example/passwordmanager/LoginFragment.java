package example.passwordmanager;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends     Fragment
                           implements  View.OnClickListener,
                                       GoogleApiClient.OnConnectionFailedListener
{
    private Button btn_login;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressBar progress;
    private LoginActivity loginActivity;

    private static final int RC_SIGN_IN = 9001;
    SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String prof_name;
    String prof_email;
    String prof_img_url;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    public void onAttach(Context activity)
    {
        super.onAttach(activity);
        loginActivity = (LoginActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        loginActivity.setActivityBackgroundColor(ContextCompat.getColor(loginActivity, R.color.bg_login));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this.loginActivity)
                .enableAutoManage(this.loginActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                else
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

//                updateUI(user);

            }
        };

        return view;
    }

    private void initViews(View view)
    {
//        pref = getActivity().getPreferences(0);

        progress = (ProgressBar) view.findViewById(R.id.progress);

        inputEmail = (EditText) view.findViewById(R.id.emailInput);
        inputPassword = (EditText) view.findViewById(R.id.passwordInput);

        btn_login = (Button) view.findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) view.findViewById(R.id.btnLinkToRegisterScreen);

        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        setGooglePlusButtonText(signInButton, getString(R.string.common_signin_button_text_long));

        btn_login.setOnClickListener(this);
        btnLinkToRegister.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnLogin :
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty())
                {
                    progress.setVisibility(View.VISIBLE);
                    loginProcess(email,password);
                }
                else
                    Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();

                break;

            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.btnLinkToRegisterScreen :
                goToRegister();
                break;
        }
    }

    private void loginProcess(String email, String password)
    {

        goToMain();
    }

    private void goToMain()
    {
        Intent i = new Intent(getContext(), MainActivity2.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        i.putExtra("prof_name", prof_name);
        i.putExtra("prof_email", prof_email);
        i.putExtra("prof_img", prof_img_url);

        startActivity(i);
        loginActivity.finish();
    }

    private void goToRegister()
    {
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,register);
        ft.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
//            String name = account.getDisplayName();
//            String email = account.getEmail();
//            String img_url = account.getPhotoUrl().toString();
//            Name.setText(name);
//            Email.setText(email);
//            Glide.with(this).load(img_url).into(Prof_Pic);

            prof_name = account.getDisplayName();
            prof_email = account.getEmail();
            prof_img_url = account.getPhotoUrl().toString();

            firebaseAuthWithGoogle(account);
            updateUI(true);
        }
        else
            updateUI(false);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void updateUI(boolean isLoggedIn)
    {
        if (isLoggedIn)
        {
            goToMain();
        }
        else
        {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText)
    {
        for (int i = 0; i < signInButton.getChildCount(); i++)
        {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                tv.setText(buttonText);
                return;
            }
        }
    }
}
