package example.passwordmanager;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hassanchowdhury on 21/08/2016.
 */
public class PasswordGenerator
{
    private static final String U_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String L_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private List<Character> allowedCharacters;
    private StringBuilder passwd = new StringBuilder();

    private final int passwdLength;
    private int minDigits;
    private SecureRandom rand;

//    private final static String testString = "I know just69? how to whisper, And{} I know just how:* to cry,I know just where to find the answers 0abc45g75";

    public PasswordGenerator(int passwdLength, int minDigits, List<Boolean> options)
    {
        this.passwdLength = passwdLength;

        this.rand = new SecureRandom();
        this.allowedCharacters = new ArrayList<Character>();

        satisfyMinRequirement(options, minDigits);

        if (options.get(0))
            addToCharList(L_CASE, allowedCharacters);

        if (options.get(1))
            addToCharList(U_CASE, allowedCharacters);

        if (options.get(2))
        {
            this.minDigits = minDigits;
            addToCharList(DIGITS, allowedCharacters);
        }

        if (options.get(3))
            addToCharList(SPECIAL_CHARS, allowedCharacters);
    }

    private List<Character> addToCharList(String s, List<Character> characterList)
    {
        for (char c : s.toCharArray())
            characterList.add(c);

        return characterList;
    }

    private int getRandomNum(int max) {
        return rand.nextInt(max);
    }

    private String charListToString(ArrayList<Character> list)
    {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character c : list)
            builder.append(c);

        return builder.toString();
    }

    private String shuffleCharacters(String s)
    {
        List<Character> charactersList = new ArrayList<Character>();
        charactersList = addToCharList(s, charactersList);
        Collections.shuffle(charactersList);
        return charListToString((ArrayList<Character>) charactersList);
    }

    //must pick minDigits quantity of digits from DIGITS to satisfy requirement if option selected
    private void satisfyMinRequirement(List<Boolean> options, int minDigits)
    {
        if (options.get(0))
            addCharsToPass(L_CASE, 1);

        if (options.get(1))
            addCharsToPass(U_CASE, 1);

        if (options.get(3))
            addCharsToPass(SPECIAL_CHARS, 1);

        int remainingCharsToAdd = passwdLength - passwd.length();
        if (options.get(2))
        {
            if (minDigits < remainingCharsToAdd)
                addCharsToPass(DIGITS, minDigits);
            else
            {
                //clear current password and addCharsToPass(Digits, minDigits);
                passwd.setLength(0);
                addCharsToPass(DIGITS, minDigits);
            }
        }
    }

    private void addCharsToPass(String chars, int minQuantity)
    {
        List<Character> allowedChars = new ArrayList<Character>();
        allowedChars = addToCharList(chars, allowedChars);

        for (int i=0; i<minQuantity; i++)
            passwd.append( allowedChars.get( getRandomNum( allowedChars.size() ) ) );
    }

    public String generatePasswd()
    {
        int remainingCharsToAdd = passwdLength - passwd.length();

        for (int i = remainingCharsToAdd; i>0; i--)
            passwd.append( allowedCharacters.get(getRandomNum( allowedCharacters.size() )) );

        return shuffleCharacters(String.valueOf(passwd));
    }

}
