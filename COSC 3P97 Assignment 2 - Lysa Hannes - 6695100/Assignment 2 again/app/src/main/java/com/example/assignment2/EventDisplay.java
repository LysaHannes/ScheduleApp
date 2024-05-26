package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Room_Database.AppDatabase;
import Room_Database.Event;

public class EventDisplay extends AppCompatActivity {
    String name;
    String date;
    String time;
    String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);

        //sets the fields used this to help - https://stackoverflow.com/questions/43703359/how-to-pass-variables-to-another-activity-in-android
        if( getIntent().getExtras() != null)
        {
            name = getIntent().getStringExtra("name");
            date= getIntent().getStringExtra("date");
            time = getIntent().getStringExtra("time");
            contact = getIntent().getStringExtra("contact");
        }

        TextView nameTextview = findViewById(R.id.name);
        TextView dateTextview = findViewById(R.id.datePickerField);
        TextView timeTextview = findViewById(R.id.timePickerField);
        TextView contactTextview = findViewById(R.id.contactToAdd);

        nameTextview.setText(name);
        dateTextview.setText(date);
        timeTextview.setText(time);
        contactTextview.setText(contact);
    }

    public void EditEvent(View view) {
        Intent intent = new Intent(this, EditEvent.class);
        intent.putExtra("name", name);// if its string type
        intent.putExtra("date", date);// if its int type
        intent.putExtra("time", time);// if its int type
        intent.putExtra("contact", contact);// if its int type
        startActivity(intent);
        finish();
    }

    public void DeleteEvent(View view) {
        //rooms api
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        Event event = db.eventDao().findEventByNameDateAndTime(name, date, time);
        db.eventDao().delete(event);
        finish();
    }
}