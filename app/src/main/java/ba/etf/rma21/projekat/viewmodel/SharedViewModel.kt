package ba.etf.rma21.projekat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    var id=MutableLiveData<Int>()
    fun sendMessage(text: String) {
        message.value = text
    }
    fun kvizID(idK : Int){
        id.value=idK
    }
}