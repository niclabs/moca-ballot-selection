package three_part_vote.ballotSelection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayCandidatesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_candidates);

        // Retrieve ListView where the candidates will be display
        ListView listView = (ListView) findViewById(R.id.candidates_listview);

        // Retrieve number of candidates in the election, and create an array of Strings (name of the candidates)
        String[] candidates = null;
        try {

            // Set title using information on candidatesList.json
            String question = createCandidatesList().question;
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(question);

            candidates = new String[createCandidatesList().number_of_candidates];

            // Set-up of the String[], putting the names of every candidate
            for (int i = 0; i < candidates.length; i++)
                candidates[i] = createCandidatesList().candidates[i].name;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create an ArrayList from the String[] to store the candidates and display them
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < candidates.length; ++i) {
            list.add(idOfCandidate(i + 1) + " - " + candidates[i]);
        }

        // Add the last "candidate", a blank vote
        // TODO: Change this to support multi language (add blank vote on candidates list?)
        list.add("Voto Blanco");

        // Create an adapter to show the candidates in a nice way
        final CandidatesArrayAdapter adapter = new CandidatesArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

    }

    private String idOfCandidate(int i) {
        if (i >= 10) {
            return "" + i;
        }
        else {
            return "0" + i;
        }
    }

    //Function now retrieves candidate info from local app storage
    private Election createCandidatesList() throws IOException, ClassNotFoundException {
        File candidateListDir = getApplicationContext().getDir("candidateList", Context.MODE_PRIVATE);
        File candidateListFile = new File(candidateListDir, "candidateList.json");

        BufferedReader reader = new BufferedReader(new FileReader(candidateListFile));
        String candidatesListJson = reader.readLine();
        Gson gson = new Gson();

        Election election = gson.fromJson(candidatesListJson, Election.class);

        return election;
    }

    // Class of Adapter
    private class CandidatesArrayAdapter extends ArrayAdapter<String> {

        // Global variables of the Adapter Class
        List<String> list = new ArrayList<String>();
        Context context;

        // Constructor used before
        public CandidatesArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.list = objects;
        }

        // Set-up of each view (candidate) which will be replicate it in the rest
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Retrieve current activity
            Activity activity = (Activity) this.context;

            // Retrieve inflater to display every candidate
            LayoutInflater inflater = activity.getLayoutInflater();

            // Create each view for the candidates, using the inflater previously retrieved
            View view = inflater.inflate(R.layout.candidates_list_item_layout, parent, false);

            // Create each button for candidates, and setting the text for every single one of them
            final Button button = (Button) view.findViewById(R.id.candidates_button);
            String candidate = list.get(position);
            button.setText(candidate);

            // Listener for the click in each button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create intent to initialize the next activity (BallotConfirmation)
                    Intent intent = new Intent(DisplayCandidatesActivity.this, ConfirmationAndEncryptionActivity.class);

                    // Retrieve candidate which is being pressed and pass it to the next activity
                    CharSequence candidateSelected = button.getText();
                    intent.putExtra(ConfirmationAndEncryptionActivity.EXTRA_SELECTED_CANDIDATE, candidateSelected);

                    // Start BallotConfirmation
                    startActivity(intent);
                }
            });

            // Return the view to display it
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.candidates, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
