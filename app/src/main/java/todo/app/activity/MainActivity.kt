package todo.app.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import todo.app.R
import todo.app.R.id.add_fragment
import todo.app.fragment.AddTaskFragment
import todo.app.fragment.AllTaskFragment
import java.sql.DriverManager.println

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment,AllTaskFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF6701")))



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(supportFragmentManager.findFragmentById(R.id.main_fragment))
        {
            is AddTaskFragment -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}