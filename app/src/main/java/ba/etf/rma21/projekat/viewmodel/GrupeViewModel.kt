package ba.etf.rma21.projekat.viewmodel

import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GrupeViewModel(private val grupaNadjena: ((grupa: Grupa) -> Unit)?,
                     private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    suspend fun getAll(): List<Grupa>? {
        return emptyList()
    }
    fun getGrupaNaziv(naziv: String ){
        scope.launch {
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = PredmetIGrupaRepository.getGrupaNaziv1(naziv)
            when (result) {
                is Result.Success<*> -> grupaNadjena?.invoke(result.data as Grupa)
                else -> onError?.invoke()
            }

        }
    }
}