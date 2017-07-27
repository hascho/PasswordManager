package example.passwordmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hassanchowdhury on 10/08/2016.
 */
public class PassGenDialog extends DialogFragment
{
    private View rootView;
    private View child;
    private boolean settingsPressed;

    private static int passLenSBValue;
    private static int minDigitsSBValue;
    private List<Boolean> options = Arrays.asList(
        false,
        false,
        false,
        false
    );
    /*
        * options
        * lCaseSelected
        * uCaseSelected
        * digitsSelected
        * specCharsSelected
     */

    private final static int MIN_CHARS_SB_VALUE = 8; // default value
    private final static int MIN_DIGITS_SB_VALUE = 1; // default value
    private final static String DIGIT_PATTERN = "\\d+";
    private final static String SPECIAL_CHARS_PATTERN = "[-!$%^&*()_@#+|~=`{}\\[\\]:\";'<>?,.\\\\/]";
    private final static int DIGITS_COLOUR = Color.BLUE;
    private final static int SPECIAL_CHARS_COLOUR = Color.RED;

    private Bundle savedInstanceState;
    private TextView passTV;
    private SeekBar passLenSB;
    private SeekBar minDigSB;
    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

    public static PassGenDialog newInstance()
    {
        PassGenDialog pgd = new PassGenDialog();
        return pgd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.pass_gen_dialog_layout, container);

        /*
            * must set initial password length otherwise hitting checkbox will do nothing
            * must set initial min digits value because default is 0 when initialised
         */
        passLenSBValue = MIN_CHARS_SB_VALUE;
        minDigitsSBValue = MIN_DIGITS_SB_VALUE;
        Collections.fill(options, Boolean.FALSE);

        displayPass(rootView);

        displayNumCharsInPasswdSeekBarProgress(rootView);

        handleButtonClicks(rootView);
        inflateHiddenSettings(rootView);

        View settingsButton = rootView.findViewById(R.id.dlgBtnSettings);
        // restores the instance state on rotation only
        this.savedInstanceState = savedInstanceState;
        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey("openHiddenSettings") &&
                    savedInstanceState.getBoolean("openHiddenSettings"))
                settingsButton.performClick();

            // note: restoring pass here gets overwritten because onResume() runs load()
//            if (savedInstanceState.containsKey("password")) {
//                passTV.setText(savedInstanceState.getString("password"));
//            }
        }

        return rootView;
    }

    private String generatePass(int passwdLength, int minDigits, List<Boolean> options)
    {
        // generate pass with chosen options
        PasswordGenerator pg = new PasswordGenerator(passwdLength, minDigits, options);

        return pg.generatePasswd();
    }

    private void displayPass(View v)
    {
        passTV = (TextView) v.findViewById(R.id.generatedPassTV);

        if (options.get(0) || options.get(1) || options.get(2) || options.get(3))
            passTV.setText(colourCodePass(generatePass(passLenSBValue, minDigitsSBValue, options)));
        else
            passTV.setText("");
    }

    private Spannable colourCodePass(String pass)
    {
        Spannable wordToSpan = new SpannableString(pass);

        if (checkBoxes.get(2).isChecked())
            colourCodePositions(pass,wordToSpan, DIGIT_PATTERN, DIGITS_COLOUR);

        if (checkBoxes.get(3).isChecked())
            colourCodePositions(pass,wordToSpan, SPECIAL_CHARS_PATTERN, SPECIAL_CHARS_COLOUR);

        return wordToSpan;
    }

    private void colourCodePositions(String pass, Spannable wordToSpan, String pattern, int colour)
    {
        List<ArrayList<Integer>> positions = findPatternPositions(pattern, pass);
        int start=0;
        int end=0;
        for (int i=0; i<positions.size(); i++)
        {
            start = positions.get(i).get(0);
            end = positions.get(i).get(1);
            wordToSpan.setSpan(new ForegroundColorSpan(colour), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private List<ArrayList<Integer>> findPatternPositions(String pattern, String pass)
    {
        List<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(pass);

        while (m.find())
        {
            ArrayList<Integer> row = new ArrayList<Integer>();
            row.add(m.start());
            row.add(m.end());
            positions.add(row);
        }

        return positions;
    }

    /*
        * update password length value displayed in textview
        * change mininimum digits allowed to be picked accordingly when SeekBar progress updated
        * display new pass when seekbar value changes
    */
    private void displayNumCharsInPasswdSeekBarProgress(final View v)
    {
        passLenSB = (SeekBar) v.findViewById(R.id.passLenSeekBar);
        final TextView SBValue = (TextView) v.findViewById(R.id.passLenTV);

        passLenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                passLenSBValue = progress + MIN_CHARS_SB_VALUE;
                SBValue.setText(Integer.toString(passLenSBValue));

                if (child != null)
                {
                    minDigSB = (SeekBar) child.findViewById(R.id.hiddenSettingsNumDigitsSB);
                    minDigSB.setMax(passLenSBValue - 1);
                }

                displayPass(v);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void handleButtonClicks(final View rootView)
    {
        ImageButton refreshButton = (ImageButton) rootView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPass(rootView);
            }
        });

        ImageButton copyButton = (ImageButton) rootView.findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copyPasswd", passTV.getText());
                clipboard.setPrimaryClip(clip);
            }
        });

        Button settingsBtn = (Button) rootView.findViewById(R.id.dlgBtnSettings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHiddenSettings(v);
            }
        });

        Button closeBtn = (Button) rootView.findViewById(R.id.passGenBtnClose);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(passLenSB.getProgress(), minDigSB.getProgress());
                dismiss();
            }
        });

        Button saveBtn = (Button) rootView.findViewById(R.id.dlgBtnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO decide what to do when save button pressed
                //load displayed pass into new site notes activity/fragment???
            }
        });
    }

    private void handleCheckBoxClicks(final View rootView, View hiddenSettings)
    {
        for (int i=0; i<options.size(); i++)
        {
            String chkBxID = "checkBox" + (i+1);
            int resID = getResources().getIdentifier(chkBxID, "id", getContext().getPackageName());
            checkBoxes.add((CheckBox) hiddenSettings.findViewById(resID));
            switch (i)
            {
                case 0:case 1:case 3:
                    checkBoxes.get(i).setOnClickListener(new HSChkBxListener(i, rootView));
                    break;

                case 2:
                    checkBoxes.get(i).setOnClickListener(new HSChkBxListener(i, rootView, hiddenSettings));
                    break;
            }
        }
    }

    private void inflateHiddenSettings(View rootView)
    {
        LinearLayout container = (LinearLayout) rootView.findViewById(R.id.hiddenSettingsContainer);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        child = inflater.inflate(R.layout.pass_gen_dlg_hidden_settings, container);
        child.setVisibility(View.GONE);

        handleCheckBoxClicks(rootView, child);
        displayMinNumDigitsInPasswdSeekBarProgress(rootView, child);

        TextView passLenTV = (TextView) rootView.findViewById(R.id.passLenTV);
        SeekBar dSB = (SeekBar) child.findViewById(R.id.hiddenSettingsNumDigitsSB);
        if (!checkBoxes.get(2).isChecked())
            dSB.setEnabled(false);

        dSB.setMax(Integer.parseInt(passLenTV.getText().toString())-1); // shows max value +1 if -1 is omitted
    }

    private void showHiddenSettings(View v)
    {
        settingsPressed = true;
        v.setVisibility(View.GONE);
        child.setVisibility(View.VISIBLE);
    }

    private void displayMinNumDigitsInPasswdSeekBarProgress(final View rootView, View v)
    {
        final TextView dTV = (TextView) v.findViewById(R.id.numDigitsSBTV);
        minDigSB = (SeekBar) v.findViewById(R.id.hiddenSettingsNumDigitsSB);
        minDigSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                minDigitsSBValue = progress + MIN_DIGITS_SB_VALUE;
                dTV.setText(Integer.toString(minDigitsSBValue));

                //display new pass when SeekBar progress changes
                displayPass(rootView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    // HSChkBxListener = HiddenSettingsCheckBoxListener
    private class HSChkBxListener implements View.OnClickListener
    {
        int optionChosen;
        View rootView;
        View hiddenSettings;

        public HSChkBxListener(int option, View rootView)
        {
            this.optionChosen = option;
            this.rootView = rootView;
        }

        public HSChkBxListener(int option, View rootView, View hiddenSettings)
        {
            this(option, rootView);
            this.hiddenSettings = hiddenSettings;
        }

        @Override
        public void onClick(View v) {
            runChkBxOption(optionChosen, v, rootView, hiddenSettings);
        }
    }

    private void runChkBxOption(int optionChosen, View chkBx, View rootView, View hiddenSettings)
    {
        switch (optionChosen)
        {
            case 0:case 1:case 3 :
                if (((CheckBox) chkBx).isChecked())
                    options.set(optionChosen, true);
                else
                    options.set(optionChosen, false);
                break;

            case 2:
                SeekBar dSB = (SeekBar) hiddenSettings.findViewById(R.id.hiddenSettingsNumDigitsSB);

                if (((CheckBox) chkBx).isChecked())
                {
                    dSB.setEnabled(true);
                    options.set(optionChosen, true);
                }
                else
                {
                    dSB.setEnabled(false);
                    options.set(optionChosen, false);
                }
                break;
        }
        displayPass(rootView);
    }

    private void save(int passLenValue, int minDigitsValue)
    {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("password", passTV.getText().toString());
        editor.putInt("passLength", passLenValue);
        editor.putInt("minDigits", minDigitsValue);
        for (int i=0; i<checkBoxes.size(); i++)
            editor.putBoolean("chkbx" + Integer.toString(i), checkBoxes.get(i).isChecked());

        editor.apply();
    }

    /*
        * method restores the saved stuffs here
        * set saved pass length to SeekBar
        * set saved min digits to SeekBar
        * tick saved check boxes
        * restores password on rotation only
    */
    private void load()
    {
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (sp.contains("passLength") && passLenSB != null)
            passLenSB.setProgress(sp.getInt("passLength", 0));

        if (sp.contains("minDigits") && minDigSB != null)
            minDigSB.setProgress(sp.getInt("minDigits", 0));

        for (int i=0; i<checkBoxes.size(); i++)
        {
            checkBoxes.get(i).setChecked(sp.getBoolean("chkbx"+Integer.toString(i), false));
            if (checkBoxes.get(i).isChecked())
                runChkBxOption(i, checkBoxes.get(i), rootView, child); // this code makes app generate new pass each time

        }
        // this code will restore pass on rotation only
        if (savedInstanceState != null && savedInstanceState.containsKey("password"))
            passTV.setText(colourCodePass(savedInstanceState.getString("password")));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // options are saved here so it can be restored on application restart
        save(passLenSB.getProgress(), minDigSB.getProgress());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        load();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean("openHiddenSettings", settingsPressed);
        outState.putString("password", String.valueOf(passTV.getText()));
    }

}
