package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainAndroidTestCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @get:Rule
    var mainCoroutineRule = MainAndroidTestCoroutineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = database.reminderDao()
        repository =
            RemindersLocalRepository(
                remindersDAO,
                Dispatchers.Main
            )
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun saveReminderAndGetByID() = mainCoroutineRule.runBlockingTest {
        //GIVEN save reminder
        val reminder = ReminderDTO(
            title = "soccer game with friends",
            description = "playing a soccer game with some friends",
            location = "",
            latitude = 51.022,
            longitude = 25433.12
        )
        repository.saveReminder(reminder)
        //WHEN - Get the reminder by id from the database
        val reminderLoaded = repository.getReminder(reminder.id) as Result.Success<ReminderDTO>
        val loaded = reminderLoaded.data
        //THEN the loaded data contains the expected value

        assertThat(loaded, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllRemindersAndReminders() = mainCoroutineRule.runBlockingTest {
        //GIVEN a saved reminder
        val reminder = ReminderDTO(
            title = "soccer game with friends",
            description = "playing a soccer game with some friends",
            location = "",
            latitude = 51.022,
            longitude = 25433.12
        )
        repository.saveReminder(reminder)
        //WHEN deleting all reminders
        repository.deleteAllReminders()
        //Then the list is empty
        val reminders = repository.getReminders() as Result.Success<List<ReminderDTO>>
        val data = reminders.data
        assertThat(data.isEmpty(), `is`(true))

    }
    @Test
    fun noRemindersfound_getReminderById() = mainCoroutineRule.runBlockingTest {
        //GIVEN a reminder ID to search
        //WHEN searching the reminder
        val reminder = repository.getReminder("3") as Result.Error
        //THEN Reminder is not found
        assertThat(reminder.message, notNullValue())
       assertThat(reminder.message, `is`("Reminder not found!"))
    }
}