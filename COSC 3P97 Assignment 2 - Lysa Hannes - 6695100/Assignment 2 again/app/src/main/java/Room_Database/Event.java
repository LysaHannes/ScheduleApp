package Room_Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//used documentation: https://developer.android.com/training/data-storage/room
@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int eid;

    @ColumnInfo(name = "eventName")
    public String eventName;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "contact")
    public String contact;
}

