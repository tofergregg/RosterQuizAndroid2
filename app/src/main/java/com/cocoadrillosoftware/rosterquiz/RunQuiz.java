package com.cocoadrillosoftware.rosterquiz;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class RunQuiz extends AppCompatActivity {
    static Roster roster;
    static boolean multipleChoice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Multiple Choice",
                        "Free Response",
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                if (position == 0) {
                    multipleChoice = true;
                }
                else {
                    multipleChoice = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Intent i = getIntent();
        String rosterFilename = i.getStringExtra("rosterFilename");
        // read roster from storage
        roster = Roster.load(getApplicationContext(),rosterFilename);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        View currentView;
        Context currentContext;
        int buttonCount = 7; // initial count
        ArrayList<TextView> buttonList;
        Random randGen;
        ImageView picture;
        Student actualChoice;
        ArrayList<Student> allChoices;
        EditText freeResponseAnswer;
        TextView continueText,hintText;
        int correct,incorrect; // for quiz stats
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_run_quiz, container, false);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            currentView = getView();
            currentContext = getContext();
            RelativeLayout quizLayout = (RelativeLayout) currentView.findViewById(R.id.multChoiceRelativeLayout);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            int imgId = currentView.findViewById(R.id.studentQuizPic).getId();
            rlp.addRule(RelativeLayout.BELOW,imgId);
            LinearLayout linLayout = new LinearLayout(currentContext);
            linLayout.setOrientation(LinearLayout.VERTICAL);
            linLayout.setLayoutParams(rlp);
            if (roster.size() < buttonCount) {
                buttonCount = roster.size();
            }
            randGen = new Random(); // for randomizing the quiz
            // set quiz stats to 0
            correct = 0;
            incorrect = 0;

            if (multipleChoice) {
                buttonList = new ArrayList<TextView>();
                for (int i = 0; i < buttonCount ; i++) {
                    TextView tv = new TextView(currentContext);
                    tv.setId(i);
                    tv.setTextSize(20);
                    tv.setPadding(0,20,0,0);
                    linLayout.addView(tv);
                    buttonList.add(tv);
                }
                quizLayout.addView(linLayout);
                this.runMultipleChoiceQuiz();
            }
            else {
                TextView tv = new TextView(currentContext);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(20);
                tv.setPadding(0,10,0,0);
                tv.setText("First Name Guess:");
                linLayout.addView(tv);

                // add the edit text box for the answer
                final EditText answerBox = new EditText(currentContext);
                freeResponseAnswer = answerBox;
                answerBox.setGravity(Gravity.CENTER);
                answerBox.setSingleLine();
                answerBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
                answerBox.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            // Perform action on key press
                            checkAnswer();
                            return true;
                        }
                        return false;
                    }
                });

                // for onscreen keyboard
                answerBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            checkAnswer();
                            handled = true;
                        }
                        return handled;
                    }
                });
                linLayout.addView(answerBox);

                // add text about a hint
                hintText = new TextView(currentContext);
                hintText.setGravity(Gravity.CENTER);
                hintText.setTextSize(18);
                hintText.setPadding(0,20,0,0);
                hintText.setText("Click for next letter hint");
                // clickListener to provide hint
                hintText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        provideHint();
                    }
                });
                linLayout.addView(hintText);

                // add text for continuing
                continueText = new TextView(currentContext);
                continueText.setGravity(Gravity.CENTER);
                continueText.setTextSize(18);
                continueText.setPadding(0,20,0,0);

                // no actual text yet
                linLayout.addView(continueText);

                quizLayout.addView(linLayout);
                this.runFreeResponseQuiz();
            }
        }
        void runMultipleChoiceQuiz()
        {
            // choose buttonCount students
            allChoices = new ArrayList<Student>();

            for (int i=0;i<buttonCount;i++) {
                // chose a student who hasn't already been chosen
                // (including first name overlaps!)
                // TODO: fix issue if there aren't enough names to go around
                // (i.e., infinite loop)
                boolean choiceOk;
                Student s = null;
                do {
                    choiceOk = true; // assume we're okay
                    int nextChoice = randGen.nextInt(roster.size());
                    s = roster.get(nextChoice);
                    for (int j = 0; j < i; j++) {
                        if (allChoices.get(j).firstName.equalsIgnoreCase(s.firstName)) {
                            choiceOk = false;
                            break; // not a good choice
                        }
                    }
                }  while (!choiceOk);
                allChoices.add(s);
                TextView button = buttonList.get(i);
                button.setText(s.firstName);
                // unset strikethrough in case we set it previously
                button.setPaintFlags(button.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                // set onClickListener so the user can click on the text
                final int idNum = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Clicked "+idNum);
                        userMadeChoice(idNum);
                    }
                });
            }
            // now, randomly chose among the list
            actualChoice = allChoices.get(randGen.nextInt(buttonCount));
            picture = (ImageView) getView().findViewById(R.id.studentQuizPic);
            picture.setImageDrawable(new BitmapDrawable(getResources(),actualChoice.picture.getBitmap()));
        }

        void runFreeResponseQuiz() {
            int nextChoice = randGen.nextInt(roster.size());
            actualChoice = roster.get(nextChoice);
            picture = (ImageView) getView().findViewById(R.id.studentQuizPic);
            picture.setImageDrawable(new BitmapDrawable(getResources(),actualChoice.picture.getBitmap()));
            // remove text from continue box, and remove the listener if present
            continueText.setText("");
            continueText.setOnClickListener(null);
            // remove text from answer
            freeResponseAnswer.setText("");
            // set the hint text
            hintText.setText("Click for next letter hint");
            freeResponseAnswer.setEnabled(true);
        }

        void userMadeChoice(int choice)
        {
            // check the choice
            if (allChoices.get(choice) == actualChoice) {
                System.out.println("Correct!");
                correct++;
                updateStats();
                runMultipleChoiceQuiz();
            }
            else {
                System.out.println("Incorrect!");
                TextView button = buttonList.get(choice);
                button.setPaintFlags(button.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                // remove listener so the user can't chose it again
                button.setOnClickListener(null);
                incorrect++;
                updateStats();
            }
        }
        void updateStats()
        {
            // update score
            float percent;
            int totalAnswered = correct + incorrect;
            if (totalAnswered != 0) {
                percent = Math.round(correct / (float) totalAnswered * 10000) / (float)100.0;
                System.out.println("Score: " + percent);
                // update text on screen
                TextView scoreView = (TextView) getView().findViewById(R.id.scoreText);
                scoreView.setText("Score: "+ Integer.toString(correct) + "/" +
                        Integer.toString(totalAnswered) + " (" +
                        Float.toString(percent) + "%)");
            }
        }
        void provideHint()
        {
            System.out.println("Providing hint");

        }
        void checkAnswer()
        {
            String answer = freeResponseAnswer.getText().toString();
            System.out.println("Checking answer: " + answer);
            InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            if (answer.equalsIgnoreCase(actualChoice.firstName)) {
                correct++;
                hintText.setText("Correct! " + actualChoice.commaName());
            }
            else {
                incorrect++;
                hintText.setText("Incorrect! " + actualChoice.commaName());
            }
            updateStats();
            continueText.setText("Click to Continue");
            // set up listener when continuing
            freeResponseAnswer.setEnabled(false); // don't allow text input until continue is clicked
            continueText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runFreeResponseQuiz();
                }
            });
        }
    }
}
