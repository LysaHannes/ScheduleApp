package Room_Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//used documentation: https://developer.android.com/training/data-storage/room
//and https://developer.android.com/training/data-storage/room/accessing-data
@Dao
public interface EventDao {
    @Query("SELECT * FROM event WHERE date = :theDate")
    public List<Event> dateEquals(String theDate);

    @Insert
    void insert(Event... event);

    @Delete
    void delete(Event event);

    @Query("DELETE FROM Event")
    void deleteAllEvents();

    @Query("DELETE FROM Event WHERE date = :date")
    void deleteEventsByDate(String date);

    @Query("UPDATE Event SET date = :newDate WHERE date = :oldDate")
    void updateEventsDate(String oldDate, String newDate);

    @Query("SELECT * FROM Event WHERE eventName = :eventName AND date = :eventDate AND time = :eventTime")
    Event findEventByNameDateAndTime(String eventName, String eventDate, String eventTime);

    @Update
    void updateEvent(Event event);
}


