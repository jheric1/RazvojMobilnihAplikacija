package ba.etf.rma21.projekat.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.repositories.KvizRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class KvizListViewModel(private val searchDone: ((kvizovi: List<Kviz>) -> Unit)?,
                        private val searchDone2: ((kvizovi: List<Kviz>) -> Unit)?,
                        private val kvizoviPronadjeni: ((kvizovi: List<Kviz>) -> Unit)?,
                        private val onError: (()->Unit)?) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    /*
    fun getFavorites(context: Context, onSuccess: (movies: List<Kviz>) -> Unit,
                     onError: () -> Unit){
        scope.launch{
            val result = KvizRepository.getAll(context)
            when (result) {
                is List<Kviz> -> onSuccess?.invoke(result)
                else-> onError?.invoke()
            }
        }
    }
    */
    fun getAll() {
        scope.launch {
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = KvizRepository.getSve()
            when (result) {
                is Result.Success<*> -> searchDone?.invoke(result.data as List<Kviz>)
                else -> onError?.invoke()
            }

        }

    }
        @RequiresApi(Build.VERSION_CODES.O)
        fun getMojiKvizovi()  {
            scope.launch {
                // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
                val result = KvizRepository.getMyKvizes()
                when (result) {
                    is Result.Success<*> -> searchDone2?.invoke(result.data as List<Kviz>)
                }
            }
        }
        fun getFuture() : List<Kviz>{
            return emptyList()
        }
        fun getDone() :List<Kviz>{
            return emptyList()
        }
        fun getNotTaken() : List<Kviz>{
            return emptyList()
        }
    fun getAl1l() : List<Kviz>{
        return emptyList()
    }
    fun writeDB3(context: Context, kvizovi: Kviz, onSuccess: (movies: String) -> Unit,
                onError: () -> Unit){
        scope.launch {
                val result = KvizRepository.writeKviz(context, kvizovi)
                when (result) {
                    is String -> onSuccess?.invoke(result)
                    else -> onError?.invoke()
                }

        }
    }
    fun getKvizoviZaGrupu(naziv : String)  {
        scope.launch {
            System.out.println("KVIIIIZ")
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = KvizRepository.getKvizoviZaGrupu3(naziv)
            System.out.println("KVIIIIZ")
            when (result) {
                is Result.Success<*> -> kvizoviPronadjeni?.invoke(result.data as List<Kviz>)
                else -> onError?.invoke()
            }
        }
    }

}
