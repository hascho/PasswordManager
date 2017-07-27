package example.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button importDatabase;
    private Button exportDatabase;
    private Button chgPass;
    private Button logout;

    private AlertDialog dialog;
    private EditText et_old_password;
    private EditText et_new_password;
    private TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }


    private void initViews()
    {
        importDatabase = (Button) findViewById(R.id.btnImport);
        exportDatabase = (Button) findViewById(R.id.btnExport);
        chgPass = (Button) findViewById(R.id.btnChgPass);
        logout = (Button) findViewById(R.id.btnLogout);

        importDatabase.setOnClickListener(this);
        exportDatabase.setOnClickListener(this);
        chgPass.setOnClickListener(this);
        logout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnChgPass:
                openChgPassDialog();
                break;

            case R.id.btnLogout:
                logout();
                break;

            case R.id.btnImport:
                break;

            case R.id.btnExport:
                break;
        }
    }

    private void openChgPassDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        et_old_password = (EditText)view.findViewById(R.id.et_old_password);
        et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        tv_message = (TextView)view.findViewById(R.id.tv_message);
        builder.setView(view);
        builder.setTitle("Change Password");
        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                if(!old_password.isEmpty() && !new_password.isEmpty()){
//                    progress.setVisibility(View.VISIBLE);
//                    changePasswordProcess(pref.getString(Constants.EMAIL,""),old_password,new_password);
                }else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");
                }
            }
        });
    }


    private void logout()
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void changePasswordProcess()
    {

    }
}
