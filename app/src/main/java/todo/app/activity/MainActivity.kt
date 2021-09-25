package todo.app.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import todo.app.R
import todo.app.fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var allTaskFragment: AllTaskFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_main)
        initActionBar()
        showFragment(allTaskFragment)
    }

    private fun initActionBar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6701")))
    }

    private fun showFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        ).replace(R.id.main_fragment,fragment,null).commit()
        }

    private fun removeFragment(fragment: Fragment){
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
        if(supportFragmentManager.findFragmentById(R.id.main_fragment) is AllTaskFragment){
            finish()
        }
    }
}
