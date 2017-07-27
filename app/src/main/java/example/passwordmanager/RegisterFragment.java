package example.passwordmanager;


import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener
{
    private Button btnRegister;
    private Button btnLinkToLoginScreen;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressBar progress;

    private LoginActivity loginActivity;

    public void onAttach(Context activity)
    {
        super.onAttach(activity);
        loginActivity = (LoginActivity) activity;
    }

    public RegisterFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        loginActivity.setActivityBackgroundColor(ContextCompat.getColor(loginActivity, R.color.bg_registration));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
//        pref = getActivity().getPreferences(0);

        progress = (ProgressBar) view.findViewById(R.id.progress);

        inputName = (EditText) view.findViewById(R.id.nameInput);
        inputEmail = (EditText) view.findViewById(R.id.emailInput);
        inputPassword = (EditText) view.findViewById(R.id.passwordInput);

        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnLinkToLoginScreen = (Button) view.findViewById(R.id.btnLinkToLoginScreen);

        btnRegister.setOnClickListener(this);
        btnLinkToLoginScreen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnRegister :
                break;

            case R.id.btnLinkToLoginScreen :
                goToLogin();
                break;
        }
    }

    private void goToLogin()
    {
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }
}
