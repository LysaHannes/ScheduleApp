package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import Room_Database.AppDatabase;
import Room_Database.Event;

//I used these videos to help me with the date and time pickers:
    //-https://www.youtube.com/watch?v=qCoidM98zNk
    //-https://www.youtube.com/watch?v=c6c1giRekB4
public class NewEvent extends AppCompatActivity {
    ArrayList<String> contactRows = new ArrayList<>();
    final String[] nocontact = {"No contacts on this device"};
    private static final int PERMISSION_READ_CONTACTS = 777;
    int hour, minute;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            loadContacts();
        else
            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS
            );

        initDatePicker();

        dateButton = findViewById(R.id.datePickerField);
        dateButton.setText(getTodaysDate());

        timeButton = findViewById(R.id.timePickerField);

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format the current time in 12-hour format as a string
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTimeString = sdf.format(currentDate);

        timeButton.setText(currentTimeString);

        Button makeEvent = findViewById(R.id.createEvent);

        makeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewEvent();
            }
        });
    }

    private void saveNewEvent(){

        EditText nameField = findViewById(R.id.name);
        String name = nameField.getText().toString();

        Button dateField = findViewById(R.id.datePickerField);
        String date = dateField.getText().toString();

        Button timeField = findViewById(R.id.timePickerField);
        String time = timeField.getText().toString();

        TextView contactToAdd = findViewById(R.id.contactToAdd);
        String contact = contactToAdd.getText().toString();

        //rooms api
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        Event event = new Event();
        event.eventName = name;
        event.date = date;
        Log.d("Date from List", date);
        event.time = time;
        event.contact = contact;

        db.eventDao().insert(event);

        finish();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {

        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {

        if (month == 1)
            return "JAN";
        else if (month == 2)
            return "FEB";
        else if (month == 3)
            return "MAR";
        else if (month == 4)
            return "APR";
        else if (month == 5)
            return "MAY";
        else if (month == 6)
            return "JUN";
        else if (month == 7)
            return "JLY";
        else if (month == 8)
            return "AUG";
        else if (month == 9)
            return "SEP";
        else if (month == 10)
            return "OCT";
        else if (month == 11)
            return "NOV";
        else
            return "DEC";
    }

    //used the code from class or this part and modified it slightly
    @SuppressLint("Range")
    void loadContacts() {
        final ListView contactsList = (ListView) findViewById(R.id.contactslist);
        Uri contactsUri = Uri.parse("content://contacts/people");
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(this, contactsUri, projection, null, null, null);
        c = cursorLoader.loadInBackground();
        contactRows.clear();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String contactDisplayName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactRows.add(contactDisplayName);
            c.moveToNext();
        }
        if (contactRows.isEmpty()) {
            ArrayAdapter<String> arrayAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nocontact);
            contactsList.setAdapter(arrayAdpt);
        } else {
            ArrayAdapter<String> arrayAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactRows);
            contactsList.setAdapter(arrayAdpt);
        }

        //this helped me with the on click listener for the list view: https://stackoverflow.com/questions/46998802/set-text-of-clicked-listview-item-as-variable
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //makes it so the edit text gets deselected
                EditText nameText = findViewById(R.id.name);
                nameText.clearFocus();
                view.requestFocus();

                // Get the text of the clicked item
                String clickedItemText = ((TextView) view).getText().toString();

                TextView contact = findViewById(R.id.contactToAdd);
                contact.setText(clickedItemText);
                contact.setBackground(new ColorDrawable(Color.parseColor("#e6eded")));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Permission granted
                loadContacts();
            } else {
                Toast.makeText(this, "Then you cannot add contacts to the meeting!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openDatePicker(View view) {
        //makes it so the edit text gets deselected
        EditText nameText = findViewById(R.id.name);
        nameText.clearFocus();
        view.requestFocus();

        datePickerDialog.show();
    }

    public void openTimePicker(View view) {
        //makes it so the edit text gets deselected
        EditText nameText = findViewById(R.id.name);
        nameText.clearFocus();
        view.requestFocus();

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                String formattedTime = sdf.format(calendar.getTime());

                timeButton.setText(formattedTime);
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, false);
        timePickerDialog.show();
    }
}
