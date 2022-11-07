package ba.etf.rma21.projekat.viewmodel

import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.KvizRepository
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class PredmetViewModel(private val predmetNadjen: ((predmet: Predmet) -> Unit)?,
                       private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    fun getAll():List<Predmet>{
        return emptyList()
    }
    fun getUpisani() : List<Predmet>{
        return emptyList()
    }
    fun getPredmetNaziv(naziv: String ){
        scope.launch {
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = PredmetIGrupaRepository.getPredmetNaziv1(naziv)
            when (result) {
                is Result.Success<*> -> predmetNadjen?.invoke(result.data as Predmet)
                else -> onError?.invoke()
            }

        }
    }
}