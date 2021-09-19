package todo.app.fragment

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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.android.ext.android.inject
import todo.app.R
import todo.app.adapter.TasksAdapter
import todo.app.database.FireStoreDB
import todo.app.model.TaskModel


class AllTaskFragment : Fragment() {

    private lateinit var v: View
    private lateinit var rc: RecyclerView
    private val itemList: ArrayList<TaskModel> = ArrayList()
    private lateinit var adapter: TasksAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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

    private fun initViews(){
        v.findViewById<FloatingActionButton>(R.id.fb_btn).setOnClickListener(onFloatClick)
       swipeRefreshLayout =  v.findViewById(R.id.swipe_refreshL)
        swipeRefreshLayout.setOnRefreshListener(onRefreshSwipe)
    }

    private val onRefreshSwipe = SwipeRefreshLayout.OnRefreshListener{
        // do more stuff
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
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

    private fun changeTitle() {
        val appCompatActivity: AppCompatActivity = activity as AppCompatActivity
        appCompatActivity.title = "All Tasks"
    }

    private val onFloatClick = View.OnClickListener {
        val addTaskFragment: AddTaskFragment by inject()
        showFragment(addTaskFragment)
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
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        itemList.removeAt(viewHolder.absoluteAdapterPosition)
                        adapter.notifyDataSetChanged()
                        Snackbar.make(v, "Task Deleted", Snackbar.LENGTH_LONG).show()
                    }
                    ItemTouchHelper.LEFT -> {
                        Snackbar.make(v, "Update Task", Snackbar.LENGTH_LONG).show()
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

                setupRightSwipe(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                setupLeftSwipe(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
                    R.color.orange_200
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


    private fun initRc() {
        rc = v.findViewById(R.id.recyclerView)
        rc.setHasFixedSize(true)
        rc.layoutManager = LinearLayoutManager(context)
        adapter = TasksAdapter(itemList)
        rc.adapter = adapter
        ItemTouchHelper(itemTouchHelper!!).attachToRecyclerView(rc)
    }

    private fun showFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
            ?.replace(R.id.main_fragment, fragment)?.addToBackStack(null)?.commit()

    }

    private fun eventChangeListner() {
        FireStoreDB.getCollectionRef().get().addOnSuccessListener {
            if (it.documents.isEmpty()) {
                visibleEmptyView()
            } else {
                visibleRc()
                itemList.removeAll(itemList)
                for (q in it.documents) {
                    val task = q.toObject(TaskModel::class.java)
                    itemList.add(task!!)
                    adapter.notifyDataSetChanged()
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

}