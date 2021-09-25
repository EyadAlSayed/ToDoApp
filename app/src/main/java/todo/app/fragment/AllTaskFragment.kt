package todo.app.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import todo.app.R
import todo.app.adapter.TasksAdapter
import todo.app.broadcast.AlarmBroadcastReceiver
import todo.app.data.ItemList
import todo.app.database.FireStoreDB
import todo.app.message.InfoMessage
import todo.app.model.TaskModel
import javax.inject.Inject

@AndroidEntryPoint
class AllTaskFragment @Inject constructor() : Fragment() {

    private lateinit var v: View
    private lateinit var rc: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @Inject
    lateinit var singleTaskFragment:SingleTaskFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        v = inflater.inflate(R.layout.all_task_fragment, container, false)

        initViews()
        changeTitle()
        initRc()
        startAnimatedProgressBar()
        eventChangeListner()
        return v
    }

    private val onFloatClick = View.OnClickListener {
        val bundle = Bundle()
        bundle.putInt("FLAG",0)
        singleTaskFragment.arguments = bundle
        showFragment(singleTaskFragment)
    }

    private val onRefreshSwipe = SwipeRefreshLayout.OnRefreshListener {
        eventChangeListner()
        swipeRefreshLayout.isRefreshing = false
    }

    private var itemTouchHelper: ItemTouchHelper.SimpleCallback? =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.absoluteAdapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        deleteItem(pos)
                        if(ItemList.getList().isEmpty()) visibleEmptyView()
                        showSnackBar(InfoMessage.DELETE_TASK.message)
                    }
                    ItemTouchHelper.LEFT -> {
                        val bundle = Bundle()
                        bundle.putInt("FLAG",1)
                        bundle.putSerializable("TASK",ItemList.getList()[pos])
                        singleTaskFragment.arguments = bundle
                        showFragment(singleTaskFragment)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                setupRightSwipe(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                setupLeftSwipe(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    private fun deleteItem(pos:Int):Boolean{
        val id = ItemList.getList()[pos].id
        FireStoreDB.deleteDoc(id)
        cancelAlarm(pos)
        ItemList.removeItemByPosition(pos)
        rc.adapter?.notifyItemRemoved(pos)
        return true
    }

    private fun cancelAlarm(reqCode:Int) {
        val alarmManager = context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(requireActivity(), AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireActivity(), reqCode+1, intent, 0)
        // the reqCode is plus one because the alarm request code set according to size of ItemList
        alarmManager!!.cancel(pendingIntent)
        pendingIntent.cancel()
    }



    private fun initViews() {
        v.findViewById<FloatingActionButton>(R.id.fb_btn).setOnClickListener(onFloatClick)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refreshL)
        swipeRefreshLayout.setOnRefreshListener(onRefreshSwipe)
        swipeRefreshLayout.setColorSchemeResources(R.color.orange)
    }

    private fun initRc() {
        rc = v.findViewById(R.id.recyclerView)
        rc.setHasFixedSize(true)
        rc.layoutManager = LinearLayoutManager(context)
        rc.adapter = TasksAdapter()
        ItemTouchHelper(itemTouchHelper!!).attachToRecyclerView(rc)
    }

    private fun changeTitle() {
        val appCompatActivity: AppCompatActivity = activity as AppCompatActivity
        appCompatActivity.title = "All Tasks"
    }


    private fun startAnimatedProgressBar() {
        val progressBar = v.findViewById<ProgressBar>(R.id.double_bounce_bar)
        progressBar.visibility = View.VISIBLE
        val doubleBounce: Sprite = DoubleBounce()
        progressBar.indeterminateDrawable = doubleBounce
    }

    private fun stopAnimatedProgressBar() {
        val progressBar = v.findViewById<ProgressBar>(R.id.double_bounce_bar)
        progressBar.visibility = View.GONE
        progressBar.isIndeterminate = false
    }

    private fun setupLeftSwipe(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.orange_400
                )
            )
            .addSwipeLeftActionIcon(R.drawable.ic_update)
            .addSwipeLeftLabel("Update")
            .setSwipeLeftLabelColor(ContextCompat.getColor(v.context, R.color.white))
            .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_PT, 10f)
            .create()
            .decorate()

    }

    private fun setupRightSwipe(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeRightBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.orange_400
                )
            )
            .addSwipeRightActionIcon(R.drawable.ic_delete_sweep)
            .addSwipeRightLabel("Delete")
            .setSwipeRightLabelColor(ContextCompat.getColor(v.context, R.color.white))
            .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_PT, 10f)
            .create()
            .decorate()
    }

    private fun eventChangeListner() {
        FireStoreDB.getCollectionRef().get().addOnSuccessListener {
            if (it.documents.isEmpty()) {
                visibleEmptyView()
            } else {
                visibleRc()
                ItemList.removeAllItem()
                for (q in it.documents) {
                    val task = q.toObject(TaskModel::class.java)
                    ItemList.addItem(task!!)
                    rc.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun visibleEmptyView() {
        v.findViewById<ImageView>(R.id.empty_item_img).visibility = View.VISIBLE
        v.findViewById<RecyclerView>(R.id.recyclerView).visibility = View.GONE
        stopAnimatedProgressBar()
    }

    private fun visibleRc() {
        v.findViewById<RecyclerView>(R.id.recyclerView).visibility = View.VISIBLE
        v.findViewById<ImageView>(R.id.empty_item_img).visibility = View.GONE
        stopAnimatedProgressBar()
    }

    private fun showFragment(fragment: Fragment) {

        activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )?.replace(R.id.main_fragment,fragment,null)?.commit()

    }
    private fun showSnackBar(message:String){
        val sb = Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
        sb.view.setBackgroundResource(R.drawable.rounded_shape_snackbar)
        sb.show()
    }

}