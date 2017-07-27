package example.passwordmanager;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import ernestoyaquello.com.verticalstepperform.*;
//
//import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
//import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddNewCredentialActivity extends AppCompatActivity /*implements VerticalStepperForm*/
{
    private static final int IMG_ID = 2130837622;

    private DatabaseReference mDatabaseReference;
    private Button save;
    private EditText name, description, website, login, password;

    //private VerticalStepperFormLayout verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_credential);

        //TODO: implement textwatcher to make sure edts are not empty before pushing to db

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        name        = (EditText) findViewById(R.id.edt_entry_name);
        description = (EditText) findViewById(R.id.edt_entry_description);
        website     = (EditText) findViewById(R.id.edt_entry_website);
        login       = (EditText) findViewById(R.id.edt_entry_login);
        password    = (EditText) findViewById(R.id.edt_entry_password);
        save        = (Button) findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushKey = mDatabaseReference.push().getKey();
                ListEntry obj = new ListEntry.ListEntryBuilder
                (
                        IMG_ID,
                        name.getText().toString(),
                        description.getText().toString(),
                        pushKey,
                        new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date())
                )
                .setCredentials(new Credentials(website.getText().toString(),
                                                login.getText().toString(),
                                                password.getText().toString()))
                .build();
                mDatabaseReference.child("entries/" + pushKey).setValue(obj);
                finish();
            }
        });

//        String[] mySteps = {"Name", "Email", "Phone Number"};
//        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
//        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
//
//        // Finding the view
//        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
//
//        // Setting up and initializing the form
//        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, (VerticalStepperForm) this, this)
//                .primaryColor(colorPrimary)
//                .primaryDarkColor(colorPrimaryDark)
//                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
//                .init();

    }

//    private View createNameStep()
//    {
//        // Here we generate programmatically the view that will be added by the system to the step content layout
//        name = new EditText(this);
//        name.setSingleLine(true);
//        name.setHint("Your name");
//
//        return name;
//    }
//
//    private View createEmailStep()
//    {
//        // In this case we generate the view by inflating a XML file
//        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
////        LinearLayout emailLayoutContent = (LinearLayout) inflater.inflate(R.layout.email_step_layout, null, false);
////        email = (EditText) emailLayoutContent.findViewById(R.id.email);
//
//        return null;//emailLayoutContent;
//    }
//
//    private View createPhoneNumberStep()
//    {
//        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
////        LinearLayout phoneLayoutContent = (LinearLayout) inflater.inflate(R.layout.phone_step_layout, null, false);
//
//        return null;//phoneLayoutContent;
//    }
//
//    @Override
//    public View createStepContentView(int stepNumber)
//    {
//        View view = null;
//        switch (stepNumber)
//        {
//            case 0:
//                view = createNameStep();
//                break;
//            case 1:
//                view = createEmailStep();
//                break;
//            case 2:
//                view = createPhoneNumberStep();
//                break;
//        }
//        return view;
//    }
//
//    private void checkName()
//    {
//        if(name.length() >= 3 && name.length() <= 40)
//            verticalStepperForm.setActiveStepAsCompleted();
//        else
//        {
//            // This error message is optional (use null if you don't want to display an error message)
//            String errorMessage = "The name must have between 3 and 40 characters";
//            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
//        }
//    }
//
//    private void checkEmail()
//    {
//
//    }
//
//    @Override
//    public void onStepOpening(int stepNumber)
//    {
//        switch (stepNumber)
//        {
//            case 0:
//                checkName();
//                break;
//            case 1:
//                checkEmail();
//                break;
//            case 2:
//                // As soon as the phone number step is open, we mark it as completed in order to show the "Continue"
//                // button (We do it because this field is optional, so the user can skip it without giving any info)
//                verticalStepperForm.setStepAsCompleted(2);
//                // In this case, the instruction above is equivalent to:
//                // verticalStepperForm.setActiveStepAsCompleted();
//                break;
//        }
//    }
//
//    @Override
//    public void sendData()
//    {
//
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState)
//    {
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState)
//    {
//        super.onRestoreInstanceState(savedInstanceState);
//    }
}
