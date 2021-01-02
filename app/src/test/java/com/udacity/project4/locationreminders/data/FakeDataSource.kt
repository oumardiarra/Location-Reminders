package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError)
            return Result.Error("Test Exception")
        reminders?.let {
            return Result.Success(ArrayList(it))
        }
        return Result.Error("No reminders found")
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError)
            return Result.Error("Test Exception")
        val finded = reminders?.first { it.id == id }
        if (finded != null)
            return Result.Success(finded!!)
        else
            return Result.Error("Reminder ${id} not found")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}