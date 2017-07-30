package example.passwordmanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddNewListEntryActivity extends AppCompatActivity implements VerticalStepperForm
{
    private static final int IMG_ID_LOGIN    = 2130837623;
    private static final int IMG_ID_SEC_NOTE = 2130837629;

    private DatabaseReference mDatabaseReference;
//    private Button save;
//    private EditText name, description, website, login, password;

    private VerticalStepperFormLayout verticalStepperForm;

    // Information about the steps/fields of the form
    private static final int NAME_STEP_NUM          = 0;
    private static final int DESCRIPTION_STEP_NUM   = 1;
    private static final int CATEGORY_STEP_NUM      = 2;

    // Name step
    private EditText nameEdt;
    private static final int MIN_CHARACTERS_NAME = 3;
    public static final String STATE_NAME = "name";

    // Description step
    private EditText descriptionEdt;
    public static final String STATE_DESCRIPTION = "description";

    // Category step
    private Spinner categorySpnr;
    public static final String STATE_CATEGORY = "category";
    private List<EditText> editTextList;

    //private static final int CATEGORY_DEFAULT = 0;
    private static final int CATEGORY_LOGIN   = 0;
    private static final int CATEGORY_NOTE    = 1;

    private boolean confirmBack = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list_entry);

        //TODO: implement textwatcher to make sure edts are not empty before pushing to db

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        name        = (EditText) findViewById(R.id.edt_entry_name);
//        description = (EditText) findViewById(R.id.edt_entry_description);
//        website     = (EditText) findViewById(R.id.edt_entry_website);
//        login       = (EditText) findViewById(R.id.edt_entry_login);
//        password    = (EditText) findViewById(R.id.edt_entry_password);
//        save        = (Button) findViewById(R.id.btn_save);
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String pushKey = mDatabaseReference.push().getKey();
//                ListEntry obj = new ListEntry.ListEntryBuilder
//                (
//                        IMG_ID_LOGIN,
//                        name.getText().toString(),
//                        description.getText().toString(),
//                        pushKey,
//                        new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date())
//                )
//                .setCredentials(new Credentials(website.getText().toString(),
//                                                login.getText().toString(),
//                                                password.getText().toString()))
//                .build();
//                mDatabaseReference.child("entries/" + pushKey).setValue(obj);
//                finish();
//            }
//        });

        String[] mySteps = {"Name", "Description", "Category"};
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();

        editTextList = new ArrayList<EditText>();
    }

    private void addEdtsToList(ViewGroup parent, boolean visibleHeadings)
    {
        for (int i = parent.getChildCount() - 1; i >= 0; i--)
        {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                addEdtsToList((ViewGroup) child, visibleHeadings);
                // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
            }
            else
            {
                if (child != null)
                {
                    // DO SOMETHING WITH VIEW
                    if (parent.getChildAt(i) instanceof EditText)
                        editTextList.add((EditText) parent.getChildAt(i));
                    else if (!visibleHeadings && parent.getChildAt(i) instanceof TextView)
                        parent.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }
        Collections.reverse(editTextList);
    }

    private void setEditable(EditText edt, boolean isEditable)
    {
        edt.setClickable(isEditable);
        edt.setCursorVisible(isEditable);
        edt.setFocusable(isEditable);
        edt.setFocusableInTouchMode(isEditable);
    }

    private boolean checkNameStep(String title)
    {
        boolean nameIsCorrect = false;

        if(title.length() >= MIN_CHARACTERS_NAME)
        {
            nameIsCorrect = true;
            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);
        }
        else
        {
            String nameErrorString = getResources().getString(R.string.error_name_min_characters);
            String nameError = String.format(nameErrorString, MIN_CHARACTERS_NAME);

            verticalStepperForm.setActiveStepAsUncompleted(nameError);
            // Equivalent to: verticalStepperForm.setStepAsUncompleted(TITLE_STEP_NUM, nameError);
        }

        return nameIsCorrect;
    }

    private boolean checkCategoryStep()
    {
        return false; // TODO: check edts of selected category
    }

    private View createNameStep()
    {
        // This step view is generated programmatically
        nameEdt = new EditText(this);
        nameEdt.setHint(R.string.form_hint_name);
        nameEdt.setSingleLine(true);
        nameEdt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkNameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        nameEdt.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (checkNameStep(v.getText().toString()))
                    verticalStepperForm.goToNextStep();

                return false;
            }
        });
        return nameEdt;

    }

    private View createDescriptionStep()
    {
        descriptionEdt = new EditText(this);
        descriptionEdt.setHint(R.string.form_hint_description);
        descriptionEdt.setSingleLine(true);
        descriptionEdt.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return descriptionEdt;
    }

    private View createCategoryStep()
    {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        final LinearLayout categoryContent =
                (LinearLayout) inflater.inflate(R.layout.step_category_layout, null, false);

        categorySpnr = (Spinner) categoryContent.findViewById(R.id.category_spinner);
        categorySpnr.setSelection( getIntent().getIntExtra("Category", ListEntry.LOGIN_CREDENTIALS) ); // default value is Login_credentials

        categorySpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            View v = null;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (!editTextList.isEmpty())
                    editTextList.clear();

                // make child layouts hidden
                LinearLayout ll = (LinearLayout) categoryContent.findViewById(R.id.container);
                for (int i = 0; i < ll.getChildCount(); i++)
                {
                    v = ll.getChildAt(i);
                    v.setVisibility(View.GONE);
                }

                // make selected layout visible
                switch(position)
                {
                    case CATEGORY_LOGIN:
                        v = categoryContent.findViewById(R.id.credentials_layout);
                        v.setVisibility(View.VISIBLE);
                        break;

                    case CATEGORY_NOTE:
                        v = categoryContent.findViewById(R.id.sec_note_layout);
                        v.setVisibility(View.VISIBLE);
                        break;
                }

                // remove headings from layouts
//                for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++)
//                    if (((ViewGroup) v).getChildAt(i).getClass().equals(TextView.class))
//                        ((ViewGroup) v).getChildAt(i).setVisibility(View.GONE);

                addEdtsToList((ViewGroup) v, false);
                for (int i = 0; i < editTextList.size(); i++)
                {
                    editTextList.get(i).setText("");
                    setEditable(editTextList.get(i), true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return categoryContent;
    }

    @Override
    public View createStepContentView(int stepNumber)
    {
        View view = null;
        switch (stepNumber)
        {
            case NAME_STEP_NUM:
                view = createNameStep();
                break;

            case DESCRIPTION_STEP_NUM:
                view = createDescriptionStep();
                break;

            case CATEGORY_STEP_NUM:
                view = createCategoryStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber)
    {
        switch (stepNumber)
        {
            case NAME_STEP_NUM:
                // When this step is open, we check that the title is correct
                checkNameStep(nameEdt.getText().toString());
                break;

            case DESCRIPTION_STEP_NUM:
                verticalStepperForm.setStepAsCompleted(stepNumber);
                break;

            case CATEGORY_STEP_NUM:
                verticalStepperForm.setStepAsCompleted(stepNumber);
                //checkCategoryStep();
                break;
        }
    }

    private void executeDataSending()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);

                    //Log.d("myapp", "spinner: " + categorySpnr.getSelectedItemPosition());
                    ListEntry obj = null;
                    String pushKey = mDatabaseReference.push().getKey();

                    switch (categorySpnr.getSelectedItemPosition())
                    {
                        case CATEGORY_LOGIN:
                            obj = new ListEntry.ListEntryBuilder
                            (
                                    IMG_ID_LOGIN,
                                    nameEdt.getText().toString(),
                                    descriptionEdt.getText().toString(),
                                    pushKey,
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date())
                            )
                            .setCredentials(new Credentials(editTextList.get(0).getText().toString(),
                                                            editTextList.get(1).getText().toString(),
                                                            editTextList.get(2).getText().toString()))
                            .build();
                            break;

                        case CATEGORY_NOTE:
                            obj = new ListEntry.ListEntryBuilder
                            (
                                    IMG_ID_SEC_NOTE,
                                    nameEdt.getText().toString(),
                                    descriptionEdt.getText().toString(),
                                    pushKey,
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date())
                            )
                            .setSecureNote(editTextList.get(0).getText().toString())
                            .build();
                            break;
                    }
                    mDatabaseReference.child("entries/" + pushKey).setValue(obj);

                    // You must set confirmBack to false before calling finish() to avoid the confirmation dialog
                    confirmBack = false;
                    finish();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void sendData()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));
        executeDataSending();
    }

    // CONFIRMATION DIALOG WHEN USER TRIES TO LEAVE WITHOUT SUBMITTING

    private void confirmBack() {
        if(confirmBack && verticalStepperForm.isAnyStepCompleted())
        {
            BackConfirmationFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    confirmBack = true;
                }
            });
            backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    confirmBack = false;
                    finish();
                }
            });
            backConfirmation.show(getSupportFragmentManager(), null);
        }
        else
        {
            confirmBack = false;
            finish();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home && confirmBack)
        {
            confirmBack();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        confirmBack();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        dismissDialog();
    }

    // SAVING AND RESTORING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Saving name field
        if(nameEdt != null)
            savedInstanceState.putString(STATE_NAME, nameEdt.getText().toString());

        // Saving description field
        if(descriptionEdt != null)
            savedInstanceState.putString(STATE_DESCRIPTION, descriptionEdt.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Restoration of name field
        if(savedInstanceState.containsKey(STATE_NAME))
        {
            String name = savedInstanceState.getString(STATE_NAME);
            nameEdt.setText(name);
        }

        // Restoration of description field
        if(savedInstanceState.containsKey(STATE_DESCRIPTION))
        {
            String description = savedInstanceState.getString(STATE_DESCRIPTION);
            descriptionEdt.setText(description);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}
