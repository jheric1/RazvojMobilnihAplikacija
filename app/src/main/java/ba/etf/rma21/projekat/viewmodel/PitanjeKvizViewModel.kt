package ba.etf.rma21.projekat.viewmodel

import android.content.Context
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Pitanje
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.KvizRepository
import ba.etf.rma21.projekat.data.repositories.PitanjeKvizRepository
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PitanjeKvizViewModel(private val pom: ((pitanja: List<Pitanje>) -> Unit)?,
                           private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getPitanja(idKviza : Int) {
        scope.launch {
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = PitanjeKvizRepository.getPitanja1(idKviza)
            when (result) {
                is Result.Success<*> -> pom?.invoke(result.data as List<Pitanje>)
                else -> onError?.invoke()
            }

        }

    }
    fun writeDB(context: Context, pitanje: Pitanje, onSuccess: (movies: String) -> Unit,
                onError: () -> Unit){
        scope.launch{
            val result = PitanjeKvizRepository.writePitanje(context, pitanje)
            when (result) {
                is String -> onSuccess?.invoke(result)
                else-> onError?.invoke()
            }
        }
    }

    }

