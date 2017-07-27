package example.passwordmanager;

import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class LoginActivity extends AppCompatActivity
{
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);
        initFragment();
    }

    private void initFragment()
    {

//        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
//            fragment = new ProfileFragment();
//        }else {
//            fragment = new LoginFragment();
//        }

        //temp
        fragment = new LoginFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }

    public void setActivityBackgroundColor(int color)
    {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
