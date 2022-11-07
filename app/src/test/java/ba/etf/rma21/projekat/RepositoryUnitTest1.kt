package ba.etf.rma21.projekat


import ba.etf.rma21.projekat.data.models.*
import ba.etf.rma21.projekat.data.repositories.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.OrderingComparison.lessThan
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.net.URL


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RepositoryUnitTest1 {
    suspend fun obrisi(){
        var client: OkHttpClient = OkHttpClient()
        var builder: Request.Builder = Request.Builder()
            .url(URL(ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/upisugrupeipokusaji"))
            .delete()
        var request: Request = builder.build()
        withContext(Dispatchers.IO) {
            var response: Response = client.newCall(request).execute()
            var odgovor: String = response.body().toString()
        }
    }
    @Test
    fun a0_pripremiPocetak() = runBlocking {
        obrisi()
    }

    @Test
    fun a1_getPredmete() = runBlocking {
        var predmeti = PredmetIGrupaRepository.getPredmeti()
        assertThat(predmeti,CoreMatchers.notNullValue())
        assertThat(predmeti?.size,CoreMatchers.equalTo(2))

    }

    @Test
    fun a2_getGrupe() = runBlocking {
        var grupe = PredmetIGrupaRepository.getGrupe()
        assertThat(grupe,CoreMatchers.notNullValue())
        assertThat(grupe?.size,CoreMatchers.equalTo(4))
    }
    @Test
    fun a3_getUpisaneGrupe() = runBlocking {
        var upisane = PredmetIGrupaRepository.getUpisaneGrupe()
        assertThat(upisane?.size,CoreMatchers.equalTo(0))
    }

    @Test
    fun a4_upisiIProvjeri() = runBlocking {
        var grupe = PredmetIGrupaRepository.getGrupe()
        PredmetIGrupaRepository.upisiUGrupu(grupe!![0]?.id)
        var upisane = PredmetIGrupaRepository.getUpisaneGrupe()
        assertThat(upisane?.size,CoreMatchers.equalTo(1))
        assertThat(upisane?.intersect(grupe)?.size,CoreMatchers.equalTo(1))
    }

    @Test
    fun a5_zapocniUpisaniKviz() = runBlocking {
        var upisaniKvizovi = KvizRepository.getUpisani()
        var prije = TakeKvizRepository.getPocetiKvizovi()
        TakeKvizRepository.zapocniKviz(upisaniKvizovi!![0]?.id)
        var poslije = TakeKvizRepository.getPocetiKvizovi()
        assertThat(prije,CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(poslije!!.size,CoreMatchers.equalTo(1))
    }

    @Test
    fun a6_zapocniNemoguciKviz() = runBlocking {
        TakeKvizRepository.zapocniKviz(999)
        assertThat(TakeKvizRepository.getPocetiKvizovi()!!.size,CoreMatchers.equalTo(1))
    }

    @Test
    fun a7_provjeriBezOdgovora() = runBlocking {
        var poceti = TakeKvizRepository.getPocetiKvizovi()
        assertThat(OdgovorRepository.getOdgovoriKviz(poceti!![poceti.size-1]?.KvizId)!!.size,CoreMatchers.equalTo(0))
    }
    @Test
    fun a8_provjeriOdgovor() = runBlocking {
        var poceti = TakeKvizRepository.getPocetiKvizovi()
        var pitanja = PitanjeKvizRepository.getPitanja(poceti!![poceti.size-1]?.KvizId)
        var result = OdgovorRepository.postaviOdgovorKviz(poceti!![poceti.size-1]?.id,pitanja!![0]?.id,pitanja!![0]?.tacan)
        assertThat(result,CoreMatchers.notNullValue())
        assertThat(result,CoreMatchers.equalTo(50))
        assertThat(OdgovorRepository.getOdgovoriKviz(poceti!![poceti.size-1]?.KvizId)!!.size,CoreMatchers.equalTo(1))
    }
    @Test
    fun a9_provjeriKvizove() = runBlocking {
        assertThat(KvizRepository.getAll()!!.size,CoreMatchers.equalTo(3))
    }

    @Test
    fun a9a_provjeriPitanja() = runBlocking {
        var kvizovi = KvizRepository.getAll()
        assertThat(kvizovi,CoreMatchers.notNullValue())
        var pitanja = PitanjeKvizRepository.getPitanja(kvizovi!![0]?.id)
        assertThat(pitanja,CoreMatchers.notNullValue())
        assertThat(pitanja!!.size,CoreMatchers.equalTo(2))
    }

    fun checkProperties(propA:Collection<String>,propB:Collection<String>){
        for(trazeniProperty in propA){
            assertThat(propB,hasItem(trazeniProperty))
        }
    }
    @Test
    fun sveKlaseIspravne() {
        var pitanjeProperties = Pitanje::class.java.kotlin.members.map { it.name }
        var pitanjeTProperties = listOf("id","naziv","tekstPitanja","opcije","tacan")
        checkProperties(pitanjeTProperties,pitanjeProperties)

        var kvizProperties = Kviz::class.java.kotlin.members.map {it.name}
        var kvizTProperties = listOf("id","naziv","datumPocetka","datumKraj","trajanje")
        checkProperties(kvizTProperties,kvizProperties)

        var kvizTakenProperties = KvizTaken::class.java.kotlin.members.map { it.name }
        var kvizTakenTProperties = listOf("id","student","datumRada","osvojeniBodovi")
        checkProperties(kvizTakenTProperties,kvizTakenProperties)

        var grupaProperties = Grupa::class.java.kotlin.members.map { it.name }
        var grupaTProperties = listOf("id","naziv")
        checkProperties(grupaTProperties,grupaProperties)

        var predmetProperties = Predmet::class.java.kotlin.members.map { it.name }
        var predmetTProperties = listOf("id","naziv","godina")
        checkProperties(predmetTProperties,predmetProperties)

        var odgovorProperties = Odgovor::class.java.kotlin.members.map { it.name }
        var odgovorTProperties = listOf("id","odgovoreno")
        checkProperties(odgovorTProperties,odgovorProperties)
    }

}
