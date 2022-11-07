package ba.etf.rma21.projekat.viewmodel

import android.content.Context
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Odgovor
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.OdgovorRepository
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OdgovorViewModel(private val searchDone: ((predmeti: Int) -> Unit)?,
                       private val searchDone2: ((predmeti: List<Odgovor>) -> Unit)?,
                       private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun postaviOdgovorKviz(idKvizTaken: Int, idPitanje: Int, odgovor: Int) {
        scope.launch{
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = OdgovorRepository.postaviOdgovorKviz1(idKvizTaken, idPitanje, odgovor)
            when(result){
                is Result.Success<*> -> searchDone?.invoke(result.data as Int)
                else-> onError?.invoke()
            }

        }

    }

    /*
    fun getOdgovoriKviz(idKviza: Int?) {
        scope.launch {
            val result = OdgovorRepository.getOdgovoriKviz1(idKviza)
            when (result) {
                is Result.Success<*> -> searchDone2?.invoke(result.data as List<Odgovor>)
                else -> onError?.invoke()
            }
        }

     */


}