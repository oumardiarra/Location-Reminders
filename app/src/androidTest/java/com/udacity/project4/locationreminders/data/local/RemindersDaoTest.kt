package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        //GIVEN save reminder
        val reminder = ReminderDTO(
            title = "soccer game with friends",
            description = "playing a soccer game with some friends",
            location = "",
            latitude = 51.022,
            longitude = 25433.12
        )
        database.reminderDao().saveReminder(reminder)
        //WHEN - Get the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder.id)
        //THEN the loaded data contains the expected value
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllRemindersAndReminders() = runBlockingTest {
        //GIVEN a saved reminder
        val reminder = ReminderDTO(
            title = "soccer game with friends",
            description = "playing a soccer game with some friends",
            location = "",
            latitude = 51.022,
            longitude = 25433.12
        )
        database.reminderDao().saveReminder(reminder)
        //WHEN deleting all reminders
        database.reminderDao().deleteAllReminders()
        //Then the list is empty
        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.isEmpty(), `is`(true))

    }

    @Test
    fun noRemindersfound_getReminderById() = runBlockingTest {
        //GIVEN a reminder ID to search
        //WHEN searching the reminder
        val reminder = database.reminderDao().getReminderById("3")
        //THEN Reminder is not found
        assertThat(reminder, nullValue())

    }

}