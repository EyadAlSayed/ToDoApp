package todo.app.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import todo.app.R
import todo.app.database.FireStoreDB
import todo.app.fragment.*
import todo.app.enumValue.Keys
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var allTaskFragment: AllTaskFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreenMode()
        setContentView(R.layout.activity_main)
        initActionBar()
        initSharedPreferences()
        initFireStoreUserId()
        showFragment(allTaskFragment)
    }

    private fun initFireStoreUserId(){
        val id = this.getSharedPreferences(Keys.SHARED_KEY.value, MODE_PRIVATE).getString(Keys.USER_ID.value,null)
        FireStoreDB.userId = id!!
    }
    private fun initSharedPreferences(){
        val ref = this.getSharedPreferences(Keys.SHARED_KEY.value, MODE_PRIVATE)
        if(!ref.contains(Keys.USER_ID.value)){
            ref.edit().putString(Keys.USER_ID.value,FireStoreDB.getNewTaskCollectionId()).apply()
        }
    }

    private fun setFullScreenMode() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6701")))
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        ).replace(R.id.main_fragment, fragment, null).commit()
    }

    private fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (val currentFragment = supportFragmentManager.findFragmentById(R.id.main_fragment)) {
            is AllTaskFragment -> {
                removeFragment(currentFragment)
                finish()
                return true
            }
            is SingleTaskFragment -> {
                removeFragment(currentFragment)
                showFragment(AllTaskFragment())
                return true
            }
            is DatePickerFragment -> {
                removeFragment(currentFragment)
                showFragment(SingleTaskFragment())
                return true
            }
            is TimePickerFragment -> {
                removeFragment(currentFragment)
                showFragment(SingleTaskFragment())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.main_fragment) is AllTaskFragment) {
            finish()
        }
    }
}
