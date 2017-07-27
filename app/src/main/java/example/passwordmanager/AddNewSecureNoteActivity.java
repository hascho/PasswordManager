package example.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNewSecureNoteActivity extends AppCompatActivity
{
    private static final int IMG_ID = 2130837626;

    private DatabaseReference mDatabaseReference;
    private EditText name, note;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_secure_note);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        name = (EditText) findViewById(R.id.edt_entry_name);
        note = (EditText) findViewById(R.id.edtInput);

        save = (Button) findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushKey = mDatabaseReference.push().getKey();

                ListEntry obj = new ListEntry.ListEntryBuilder
                (
                    IMG_ID,
                    name.getText().toString(),
                    "SOME DEFAULT DESCRIPTION",
                    pushKey,
                    new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(new Date())
                )
                .setSecureNote(note.getText().toString())
                .build();
                mDatabaseReference.child("entries/" + pushKey).setValue(obj);
                finish();
            }
        });
    }
}
