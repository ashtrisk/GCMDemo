package com.ashutosh.gcmdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String mUsername;
    private SharedPreferences mSharedPreferences;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MESSAGES_CHILD = "messages";

    private Button mSendButton;
    private ListView mMessageListView;
    private EditText mMessageEditText;
    private ProgressBar mProgressBar;

    private ArrayList<String> messages = new ArrayList<>();
    private FirebaseListAdapter<FriendlyMessage> mListAdapter;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Initialize ProgressBar and ListView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView)findViewById(R.id.messageListView);

        // get Database reference
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mListAdapter = new FirebaseListAdapter<FriendlyMessage>(this,
                FriendlyMessage.class,
                R.layout.list_item_layout,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {
            @Override
            protected void populateView(View v, FriendlyMessage friendlyMessage, int position) {
                // Hide Progress Bar
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                // populate view with data (message)
                TextView textViewName = (TextView)v.findViewById(R.id.textview_name);
                textViewName.setText(friendlyMessage.getName());
                TextView textViewMsg = (TextView)v.findViewById(R.id.textview_msg);
                textViewMsg.setText(friendlyMessage.getText());
            }
        };
        // observe change in data set (or any addition or deletion of msgs)
        mListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mMessageListView.setSelection(mListAdapter.getCount() - 1);
            }
        });
        // set list adapter to listview
        mMessageListView.setAdapter(mListAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        // Enable send Button only if there is some message text
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read Username from shared preferences
                mUsername = mSharedPreferences.getString(
                        getString(R.string.pref_username_key),
                        getString(R.string.pref_default_user_name)
                );
                // Send messages on click.
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == R.id.settings_menu) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
