package todo.app.fragment

import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import todo.app.R

class AllTaskFragment : Fragment() {

    var v : View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.all_task_fragment,container,false)

        val button = v?.findViewById<FloatingActionButton>(R.id.fb_btn)
        button?.setOnClickListener(c)
        return v
    }

    val  c = View.OnClickListener{

        val v = activity?.findViewById<View>(android.R.id.content)
        if (v != null) {
            Snackbar.make(v,"eyad",Snackbar.LENGTH_LONG).show()
        }
    }

}