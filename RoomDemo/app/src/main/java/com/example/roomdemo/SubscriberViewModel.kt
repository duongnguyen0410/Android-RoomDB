package com.example.roomdemo

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {
    val subscribers = repository.subscribers

    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete : Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val btnSaveOrUpdateText = MutableLiveData<String>()
    val btnClearOrDeleteText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    init {
        btnSaveOrUpdateText.value = "Save"
        btnClearOrDeleteText.value = "Clear all"
    }

    fun saveOrUpdate(){
        if(inputName.value.isNullOrBlank()){
            statusMessage.value = Event("Please enter subscriber's name")
        } else if(inputEmail.value.isNullOrBlank()){
            statusMessage.value = Event("Please enter subscriber's email")
        } else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("Invalid email format")
        } else {
            if(isUpdateOrDelete){
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }else{
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    fun clearOrDelete(){
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else {
            deleteAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val newRowId = repository.insert(subscriber)
        withContext(Dispatchers.Main){
            if(newRowId > -1){
                statusMessage.value = Event("Insert subscriber successfully!. New row id: $newRowId")
            } else {
                statusMessage.value = Event("Insert failed.")
            }
        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numOfRows = repository.update(subscriber)
        withContext(Dispatchers.Main){
            if(numOfRows > 0){
                statusMessage.value = Event("$numOfRows Update subscriber successfully!")
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear all"
            } else {
                statusMessage.value = Event("Update subscriber failed!")
            }
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numOfRows = repository.delete(subscriber)
        withContext(Dispatchers.Main){
            if(numOfRows > 0){
                statusMessage.value = Event("$numOfRows Delete subscriber successfully!")
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear all"
            } else {
                statusMessage.value = Event("Delete subscriber failed!")
            }
        }
    }

    private fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        val numOfRows = repository.deleteAll()
        withContext(Dispatchers.Main){
            if (numOfRows > 0){
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear all"
                statusMessage.value = Event("$numOfRows Delete subscribers successfully!")
            } else {
                statusMessage.value = Event("Delete all subscriber failed!")
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        btnSaveOrUpdateText.value = "Update"
        btnClearOrDeleteText.value = "Delete"
    }
}