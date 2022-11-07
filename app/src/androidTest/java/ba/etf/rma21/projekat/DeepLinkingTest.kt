package ba.etf.rma21.projekat

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import ba.etf.rma21.projekat.data.repositories.AccountRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeepLinkingTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule<MainActivity>(MainActivity::class.java,false,false)
    @Test
    fun otvaraSeAktivnost() = runBlocking{
        var intent = Intent(Intent.ACTION_VIEW)
        intent.data= Uri.parse("intent://login/#Intent")
        intent.putExtra("payload","test")
        intentsTestRule.launchActivity(intent)

        AccountRepository.setContext(intentsTestRule.activity)
        assertThat(AccountRepository.getHash(),CoreMatchers.`is`(equalTo("test")))
    }

}