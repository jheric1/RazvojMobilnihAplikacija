package ba.etf.rma21.projekat.viewmodel

import ba.etf.rma21.projekat.data.models.KvizTaken
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import ba.etf.rma21.projekat.data.repositories.TakeKvizRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TakeKvizViewModel(private val pom2: ((kvizTaken: KvizTaken) -> Unit)?,
                        private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    fun zapocniKviz(idKviza: Int) {
        scope.launch{
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = TakeKvizRepository.zapocniKviz1(idKviza)
            when(result){
                is Result.Success<*> -> pom2?.invoke(result.data as KvizTaken)
                else-> onError?.invoke()
            }

        }

    }
}