package com.lightnote.dailynotes.notesdisplay

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lightnote.dailynotes.database.NotesDao


class NotesDisplayViewModelFactory(
    private val dataSource: NotesDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesDisplayViewModel::class.java)) {
            return NotesDisplayViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}