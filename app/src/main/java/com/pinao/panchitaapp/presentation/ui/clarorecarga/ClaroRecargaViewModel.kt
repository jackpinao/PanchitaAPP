package com.pinao.panchitaapp.presentation.ui.clarorecarga

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClaroRecargaViewModel : ViewModel(
) {

    private val _numPhone = MutableLiveData<String>()
    val numPhone: LiveData<String> = _numPhone
    private val _numRec = MutableLiveData<String>()
    val numRec: LiveData<String> = _numRec
    private val _codRec = MutableLiveData<String>()
    val codRec: LiveData<String> = _codRec

//    val numPhone = ""
//    val numRec = ""
//    val codRec = ""

    fun setNumPhone(numPhone: String) {
        _numPhone.value = numPhone
    }

    fun setNumRec(numRec: String) {
        _numRec.value = numRec
    }

//    init {
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse(codRec)
//        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
//                getApplication(),
//                Manifest.permission.CALL_PHONE
//            )
//        ) {
//            return
//        }
//        getApplication<Application>().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//
//    }

    fun getGreeting(): String {
        return "Hello Android!"
    }
}