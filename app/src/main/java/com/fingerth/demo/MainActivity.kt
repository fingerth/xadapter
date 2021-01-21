package com.fingerth.demo

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fingerth.xadapter.AnModel
import com.fingerth.xadapter.ImAnimation
import com.fingerth.xadapter.Xadapter
import com.fingerth.xadapter.Xadapter2
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var adapter: Xadapter.XRecyclerAdapter<RvDataBean>? = null
    private var adapter2: Xadapter2.XRecyclerAdapter<RvDataBean>? = null
    private val data: ArrayList<RvDataBean> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repeat(50) {
            val type = Random.nextInt(3) + 1
            data.add(RvDataBean(type, "ItemType$type"))
        }
//        initAdapter1()
//        initAdapter2()
//        initAdapter3()
//        initAdapter4()
        rv.layoutManager = GridLayoutManager(this, 4)
//        rv.layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
        initAdapter5()

    }

    /**
     * 简单使用1
     */
    private fun initAdapter1() {
        if (adapter == null) {
            adapter = Xadapter<RvDataBean>(this)
                    .data(data)
                    .layoutId(R.layout.item_rv_text_view)
                    .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .create()
            rv.adapter = adapter
        } else adapter!!.notify(data)

    }

    /**
     * 简单使用2
     */
    private fun initAdapter2() {
        if (adapter == null) {
            adapter = Xadapter<RvDataBean>(this)
                    .data(data)
                    .ViewTypeBuider()
                    .typeBy { it.type }
                    .typeItem(1 to R.layout.item_rv_text_view)
                    .typeItem(2 to R.layout.item_rv_text_view2)
                    .typeItem(3 to R.layout.item_rv_text_view3)
                    .typeItem(R.layout.item_rv_default_view)
                    .build()
                    .bind(1) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .bind(2) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .bind(3) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .bind { _, _, _, _, _ -> }
                    .create()
            rv.adapter = adapter
        } else adapter!!.notify(data)
    }

    /**
     * 使用动画 / 点击事件
     */
    private fun initAdapter3() {
        if (adapter == null) {
            adapter = Xadapter<RvDataBean>(this)
                    .data(data)
                    .layoutId(R.layout.item_rv_text_view)
                    .itemAnimation()
                    .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .itemClickListener { _, _, _, bean, _ ->
                        Toast.makeText(this, bean.text, Toast.LENGTH_SHORT).show()
                    }
                    .create()
            rv.adapter = adapter
        } else adapter!!.notify(data)

    }

    /**
     * 使用动画 / 自定义动画
     */
    private fun initAdapter4() {
        if (adapter == null) {
            adapter = Xadapter<RvDataBean>(this)
                    .data(data)
                    .layoutId(R.layout.item_rv_text_view)
                    .itemAnimation(object : ImAnimation {
                        override fun getAnimators(view: View?): Array<Animator> {
                            return super.getAnimators(view)
                        }

                        override fun getAnModel(): AnModel = AnModel.ONLY_DOWN
                        override fun getStart(): Int = 5
                    })
                    .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .create()
            rv.adapter = adapter
        } else adapter!!.notify(data)

    }

    /**
     * banner
     */
    private fun initAdapter5() {
        if (adapter2 == null) {
            adapter2 = Xadapter2<RvDataBean>(this)
                    .data(data)
                    .layoutId2spanBuilder(R.layout.item_rv_text_view)
                    .spanBy { it.type }
                    .spanItem(3 to 0, 2 to 3)
                    .build()
                    .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                    .create()
            rv.adapter = adapter2
        } else adapter2!!.notify(data)

    }


    data class RvDataBean(val type: Int, val text: String)
}
