package com.example.assignment2;

//by Lysa Hannes 6695100

//I added links to all the resources when I used one right above where I used it

//please note that this only works in the month of april
//I could have used a datepicker field but I thought making my own would be so much nicer
//but I left the making the calandar dynamic for the last thing to do
//and I really couldnt think of how to do that with everything that I have already done
//hopefully I dont get marks off for that since it would have been easier to use a datepicker field then what i did

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import Room_Database.AppDatabase;
import Room_Database.Event;
import Room_Database.EventDao;

public class MainActivity extends AppCompatActivity {

    //variables for the date
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String Date;
    TextView monthYear;
    TextView fullDate;
    TextView theDay;
    ArrayList<String> eventRows = new ArrayList<>();
    ArrayList<String> eventRows2 = new ArrayList<>();
    final String[] noEvent = {"No Events Today"};
    String dayNumber;
    String Month;
    String Year;
    List<Event> events = new ArrayList<>();
    String dayOfWeek;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gets the month and year for today and sets the month and year on the calendar
        monthYear = findViewById(R.id.monthYear);
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
        Date = simpleDateFormat.format(calendar.getTime());
        monthYear.setText(Date);

        //gets today's day and makes the widget show the meetings for today
        fullDate= findViewById(R.id.fullDate);
        simpleDateFormat = new SimpleDateFormat("MMMM d");
        Date = simpleDateFormat.format(calendar.getTime());
        fullDate.setText(Date);

        //gets the day number for later use
        simpleDateFormat = new SimpleDateFormat("d");
        Date = simpleDateFormat.format(calendar.getTime());
        dayNumber = simpleDateFormat.format(calendar.getTime());

        //gets the month value for later user
        simpleDateFormat = new SimpleDateFormat("MMM");
        Month = simpleDateFormat.format(calendar.getTime());

        //gets the year number for later use
        simpleDateFormat = new SimpleDateFormat("yyyy");
        Year = simpleDateFormat.format(calendar.getTime());

        //gets the day of the week for later use
        simpleDateFormat = new SimpleDateFormat("E"); //this helped me get the E - https://knowm.org/get-day-of-week-from-date-object-in-java/#:~:text=First%2C%20create%20a%20Date%20object,method%20passing%20in%20the%20java.
        dayOfWeek = simpleDateFormat.format(calendar.getTime());

        //loads the events
        loadEvents();

        //makes today's textview in te calendar a different color background
        for (int i = 1; i <= 41; i++){
            @SuppressLint("DiscouragedApi")
            int resID = getResources().getIdentifier("_" + i, "id", getPackageName());
            TextView textView = findViewById(resID);

            if(textView.getText().toString().equals(Date)){
                textView.setBackground(new ColorDrawable(Color.parseColor("#d5dbdb")));
                theDay = textView;
            }
        }

        //clicks today's day so I can save what number the id is


        //I used this video to help me out with this for loop: https://www.youtube.com/watch?v=y1YOmzAMIK8
        //makes an onclick listened for all the text views on the calendar since they are not buttons
        for (int i = 1; i <= 41; i++) {
            @SuppressLint("DiscouragedApi")
            int resID = getResources().getIdentifier("_" + i, "id", getPackageName());
            TextView textView = findViewById(resID);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if user clicks on non numbers it doesn't do anything
                    dayNumber = ((TextView) view).getText().toString();
                    if (!dayNumber.equals(" ")) { // Check if text is not empty
                        simpleDateFormat = new SimpleDateFormat("MMMM");
                        Date = simpleDateFormat.format(calendar.getTime());

                        //for getting date for database queries
                        simpleDateFormat = new SimpleDateFormat("MMM");
                        Month = simpleDateFormat.format(calendar.getTime());

                        //for getting date for database queries
                        simpleDateFormat = new SimpleDateFormat("yyyy");
                        Year = simpleDateFormat.format(calendar.getTime());

                        //change the textview to the clicked on day
                        String day = Date + " " + dayNumber;
                        fullDate.setText(day);
                        view.setBackground(new ColorDrawable(Color.parseColor("#d5dbdb")));
                        theDay.setBackground(new ColorDrawable(Color.parseColor("#e6eded")));
                        theDay = (TextView) view;


                        //for getting day of the week for database queries
                        final String realDayy = Month.toUpperCase() + " " + dayNumber + " " + Year;
                        simpleDateFormat = new SimpleDateFormat("MMM d yyyy");
                        try {//I used this to help me with parsing the date: https://www.geeksforgeeks.org/java-program-to-convert-string-to-date/
                            Date date = simpleDateFormat.parse(realDayy);
                            simpleDateFormat = new SimpleDateFormat("E");
                            dayOfWeek = simpleDateFormat.format(date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    loadEvents();
                }
            });
        }
    }

    //I modified the code from class for listviews for this one
    void loadEvents(){
        final ListView todaysEventsList = (ListView) findViewById(R.id.todaysEventslist);

        eventRows.clear();
        events.clear();

        final String realDayy = Month.toUpperCase() + " " + dayNumber + " " + Year;

        //loads todays events from the database
        events = AccessDatabaseEvents(realDayy);

        //for adding to the listview
        for(int i = 0 ; i < events.size(); i++){
            eventRows.add(events.get(i).eventName);
        }

        if (eventRows.isEmpty()) {
            ArrayAdapter<String> arrayAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noEvent);
            todaysEventsList.setAdapter(arrayAdpt);
        } else {
            ArrayAdapter<String> arrayAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventRows);
            todaysEventsList.setAdapter(arrayAdpt);

        }

        //I modified the code from my NewEvent list view for this
        todaysEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!eventRows.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, EventDisplay.class);
                    Event variables = events.get(i);

                    //this helped me with passing variables to an activity: https://stackoverflow.com/questions/43703359/how-to-pass-variables-to-another-activity-in-android
                    intent.putExtra("name", variables.eventName);
                    intent.putExtra("date", variables.date);
                    intent.putExtra("time", variables.time);
                    intent.putExtra("contact", variables.contact);
                    startActivity(intent);
                }
            }
        });
    }

    //gets the list of events for the selected date
    private List<Event> AccessDatabaseEvents(String day){
        //rooms api
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        EventDao eventDao = db.eventDao();
        List<Event> events = eventDao.dateEquals(day);
        return events;
    }

    public void PreviousMonth(View view) {

    }

    public void NextMonth(View view) {

    }

    public void NewEvent(View view) {
        startActivity(new Intent(this,NewEvent.class));
    }

    //calls a query to the database to clear all the events
    public void ClearAllEvents(View view) {
        //rooms api
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        EventDao eventDao = db.eventDao();
        eventDao.deleteAllEvents();
        loadEvents();
    }

    //after creating an event, when the user gets back to the home screen it updates the listview
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    //calls a query to the database to clear the selected days events
    public void ClearTodaysEvents(View view) {

        //formats the date
        final String realDayy = Month.toUpperCase() + " " + dayNumber + " " + Year;

        //database
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        EventDao eventDao = db.eventDao();
        eventDao.deleteEventsByDate(realDayy);
        loadEvents();
    }

    public void PushTodaysEvents(View view) {
        //formats the date incrementing by one for the new one
        String realDayy = Month.toUpperCase() + " " + dayNumber + " " + Year;
        int newDay;

        Log.d("day", dayOfWeek);

        if(dayOfWeek.equals("Sun")){
            newDay = Integer.parseInt(dayNumber)+6;
        } else if (dayOfWeek.equals("Fri")) {
            newDay = Integer.parseInt(dayNumber)+3;
        }else{
            newDay = Integer.parseInt(dayNumber)+1;
        }

        String realDayy2 = Month.toUpperCase() + " " + newDay + " " + Year;
        Log.d("old", realDayy);
        Log.d("new", realDayy2);

        //database
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        EventDao eventDao = db.eventDao();
        eventDao.updateEventsDate(realDayy, realDayy2);
        loadEvents();
    }
}