package ba.etf.rma21.projekat.viewmodel


import android.content.Context
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.KvizRepository
import ba.etf.rma21.projekat.data.repositories.PredmetIGrupaRepository
import ba.etf.rma21.projekat.data.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PredmetIGrupaViewModel(private val searchDone: ((predmeti: List<Predmet>) -> Unit)?,
                             private val onError: (()->Unit)?,
                             private val searchDone3: ((bool: Boolean) -> Unit)?,
                             private val searchDone2: ((grupe: List<Grupa>) -> Unit)){
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun getPredmeti() {
        scope.launch{
            // Vrši se poziv servisa i suspendira se rutina dok se `withContext` ne završi
            val result = PredmetIGrupaRepository.getSviPredmeti()
            when(result){
                is Result.Success<*> -> searchDone?.invoke(result.data as List<Predmet>)
                else-> onError?.invoke()
            }

        }

    }
    fun getPredmeteZaGodinu(godina :Int) {
        scope.launch {
            val result=PredmetIGrupaRepository.getPredmeteZaGodinu1(godina)
            when(result){
                is Result.Success<*> -> searchDone?.invoke(result.data as List<Predmet>)
                else-> onError?.invoke()
            }
        }
    }
    fun getGrupeZaPredmet(PredmetId :Int) {
        scope.launch {
            val result=PredmetIGrupaRepository.getGrupeZaPredmet1(PredmetId)
            when(result){
                is Result.Success<*> -> searchDone2?.invoke(result.data as List<Grupa>)
                else-> onError?.invoke()
            }
        }
    }
    fun getGrupePredmet(naziv : String) {
        scope.launch {
            val result=PredmetIGrupaRepository.getGrupePredmet1(naziv)
            when(result){
                is Result.Success<*> -> searchDone2?.invoke(result.data as List<Grupa>)
                else-> onError?.invoke()
            }
        }
    }
    fun UpisiUGrupu(naziv : String) {
        scope.launch {
            val result=PredmetIGrupaRepository.upisiUGrupu1(naziv)
            when(result){
                is Result.Success<*> -> searchDone3?.invoke(result.data as Boolean)
                else-> onError?.invoke()
            }
        }
    }
    fun writeDB(context: Context, movie:Predmet, onSuccess: (movies: String) -> Unit,
                onError: () -> Unit){
        scope.launch{
            val result = PredmetIGrupaRepository.writePredmet(context,movie)
            when (result) {
                is String -> onSuccess?.invoke(result)
                else-> onError?.invoke()
            }
        }
    }

    fun writeDB2(context: Context, movie:Grupa, onSuccess: (movies: String) -> Unit,
                onError: () -> Unit){
        scope.launch{
            val result = PredmetIGrupaRepository.writeGrupa(context,movie)
            when (result) {
                is String -> onSuccess?.invoke(result)
                else-> onError?.invoke()
            }
        }
    }
    fun getPredmetNaziv(naziv : String) : Predmet?{
        return PredmetIGrupaRepository.getPredmetNaziv(naziv)
    }

}