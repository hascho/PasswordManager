package example.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EntryInfoActivity extends AppCompatActivity
{
    private FirebaseDatabase database;
    private DatabaseReference reference;

    //private TextView website, login;
    private TextView date;
//    private EditText website, login, password;
//    private EditText secNote;
    private Button save;
    private Switch edit;

    private ListEntry entry;
    private int category;

    private static final String TAIL_CREDENTIALS  = "/credentials";
    private static final String TAIL_SECURE_NOTES = "/secureNote";

    private List<EditText> editTextList;
    private List<String>   initialData;
    private List<String>   tailList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_info);

        entry = (ListEntry) getIntent().getSerializableExtra("entry");

        setTitle(entry.getName());

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        setUpFields();

        resetValues();

        setUpTextWatcherForEdts();

        edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch(category)
//                {
//                    case ListEntry.LOGIN_CREDENTIALS:
//                        if (isChecked)
//                        {
//                            for (int i = 0; i < editTextList.size(); i++)
//                                setEditable(editTextList.get(i), true);
//
//                            //password.requestFocus();
//                            editTextList.get(2).requestFocus();
//                        }
//                        else
//                        {
//                            for (int i = 0; i < editTextList.size(); i++)
//                                setEditable(editTextList.get(i), false);
//                        }
//                        break;
//
//                    case ListEntry.SECURE_NOTE:
//                        break;
//                }

                if (isChecked)
                {
                    for (int i = 0; i < editTextList.size(); i++)
                        setEditable(editTextList.get(i), true);

                    //password.requestFocus();
                    //editTextList.get(2).requestFocus();
                }
                else
                {
                    for (int i = 0; i < editTextList.size(); i++)
                        setEditable(editTextList.get(i), false);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date());
                date.setText(d);
                // push updated values to firebase database
                pushUpdatesToDB(d);

                resetValues();
            }
        });

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

    private List<String> getDataBetweenQuotes()
    {
        List<String> parts = new ArrayList<String>();
        Pattern p = Pattern.compile("'([^']*)'");
        Matcher m = p.matcher(entry.getOptionalParams());
        while (m.find())
            parts.add(m.group(1));
        return parts;
    }

    private void setUpFields()
    {
        editTextList = new ArrayList<EditText>();
        initialData  = new ArrayList<String>();
        tailList     = new ArrayList<String>();

        View v = null;
        switch (category = entry.getCategory())
        {
            case ListEntry.LOGIN_CREDENTIALS:
                tailList.add(TAIL_CREDENTIALS + "/website");
                tailList.add(TAIL_CREDENTIALS + "/username");
                tailList.add(TAIL_CREDENTIALS + "/password");
                v = findViewById(R.id.credentials_layout);
                v.setVisibility(View.VISIBLE);
                break;

            case ListEntry.SECURE_NOTE:
                tailList.add(TAIL_SECURE_NOTES);
                v = findViewById(R.id.sec_note_layout);
                v.setVisibility(View.VISIBLE);
                break;
        }

        addEdtsToList((ViewGroup) v, true);

        List<String> parts = getDataBetweenQuotes();
        for (int i = 0; i < editTextList.size(); i++)
        {
            editTextList.get(i).setText(parts.get(i));
            setEditable(editTextList.get(i), false);
        }

        edit = (Switch) findViewById(R.id.switch_edit);
        save = (Button) findViewById(R.id.btn_save);
        date = (TextView) findViewById(R.id.tv_entry_modified);

        date.setText(entry.getDate());
    }

    /*
     resets initial values so that listeners for edts can work as intended
     */
    private void resetValues()
    {
        if (initialData.isEmpty())
            for (int i = 0; i < editTextList.size(); i++)
                initialData.add(editTextList.get(i).getText().toString());
        else
            for (int i = 0; i < editTextList.size(); i++)
                initialData.set(i, editTextList.get(i).getText().toString());

        save.setEnabled(false);
    }

    private void setUpTextWatcherForEdts()
    {
        TextWatcher tw = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (hasValuesHaveChanged())
                    save.setEnabled(true);
                else
                    save.setEnabled(false);
                //Log.d("Test", "ontextchanged triggered " + hasValuesHaveChanged());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        };
        for (int i = 0; i < editTextList.size(); i++)
            editTextList.get(i).addTextChangedListener(tw);
    }

    private boolean hasValuesHaveChanged()
    {
        for (int i = 0; i < editTextList.size(); i++)
            if (!initialData.get(i).equals(editTextList.get(i).getText().toString()))
                return true;

        return false;
    }

    private void pushUpdatesToDB(final String date)
    {
        // search for values associated with key
        reference.child("entries")
                .orderByChild("key")
                .equalTo(entry.getKey())
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            // update values at the address associated with key
                            final String address = "entries/" + child.getKey();
                            reference.child(address + "/date").setValue(date);

                            for (int i = 0; i < editTextList.size(); i++)
                                reference.child(address + tailList.get(i)).setValue(editTextList.get(i).getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                })
        ;
    }

    private void setEditable(EditText edt, boolean isEditable)
    {
        edt.setClickable(isEditable);
        edt.setCursorVisible(isEditable);
        edt.setFocusable(isEditable);
        edt.setFocusableInTouchMode(isEditable);
    }
}
