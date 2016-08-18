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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class RunQuiz extends AppCompatActivity {
    static Roster roster;

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
            RelativeLayout multChoiceLayout = (RelativeLayout) currentView.findViewById(R.id.multChoiceRelativeLayout);
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

            buttonList = new ArrayList<TextView>();
            for (int i = 0; i < buttonCount ; i++) {
                TextView tv = new TextView(currentContext);
                tv.setId(i);
                tv.setTextSize(20);
                tv.setPadding(0,20,0,0);
                //tv.setText(roster.get(i).firstName);
                final int idNum = i;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Clicked "+idNum);
                        userMadeChoice(idNum);
                    }
                });
                linLayout.addView(tv);
                buttonList.add(tv);
            }
            multChoiceLayout.addView(linLayout);
            randGen = new Random(); // for randomizing the quiz
            // set quiz stats to 0
            correct = 0;
            incorrect = 0;
            this.runMultipleChoiceQuiz();

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
            }
            // now, randomly chose among the list
            actualChoice = allChoices.get(randGen.nextInt(buttonCount));
            picture = (ImageView) getView().findViewById(R.id.studentQuizPic);
            picture.setImageDrawable(new BitmapDrawable(getResources(),actualChoice.picture.getBitmap()));
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
    }
}
