package com.fingerth.xadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Xadapter<T>(private val con: Context) {
    fun data(mData: List<T>): WithData<T> = WithData(con, mData)

    class WithData<T>(private val con: Context, private val mData: List<T>) {
        fun layoutId(layoutId: Int): WithLayout<T> = ViewTypeBuider().typeItem(layoutId).build()

        fun ViewTypeBuider(): ItemStyleBuilder<T> = ItemStyleBuilder(con, mData)

        class ItemStyleBuilder<T>(private val con: Context, private val mData: List<T>) {
            private var type: (T) -> Any = {}
            private val list by lazy { ArrayList<Pair<Any, Int>>() }
            private var defaultLayout: Int = 0

            fun typeBy(type: (T) -> Any): ItemStyleBuilder<T> {
                this.type = type
                return this
            }

            fun typeItem(p: Pair<Any, Int>): ItemStyleBuilder<T> {
                list.add(p)
                return this
            }

            fun typeItem(p: Int): ItemStyleBuilder<T> {
                this.defaultLayout = p
                return this
            }

            fun build(): WithLayout<T> = WithLayout(con, mData, type, list, defaultLayout)
        }

    }

    class WithLayout<T>(private val con: Context, private val mData: List<T>, private val type: (T) -> Any = {}, private val list: ArrayList<Pair<Any, Int>>, private val defaultLayout: Int) {
        private val mapBind = HashMap<Any, (Context, XRecyclerAdapter.XViewHolder, List<T>, T, Int) -> Unit>()
        private var defaultBind: (Context, XRecyclerAdapter.XViewHolder, List<T>, T, Int) -> Unit = { _, _, _, _, _ -> }
        private var clickable = false
        private var clickListener: (Context, XRecyclerAdapter.XViewHolder, List<T>, T, Int) -> Unit = { _, _, _, _, _ -> }
        private var mAnimation: ImAnimation? = null

        //context, holder,mList, bean, p ->
        fun bind(any: Any? = null, black: (Context, XRecyclerAdapter.XViewHolder, List<T>, T, Int) -> Unit): WithLayout<T> {
            if (any == null) this.defaultBind = black else mapBind[any] = black
            return this
        }

        fun itemClickListener(listener: (Context, XRecyclerAdapter.XViewHolder, List<T>, T, Int) -> Unit): WithLayout<T> {
            clickable = true
            this.clickListener = listener
            return this
        }

        fun itemAnimation(mAnimation: ImAnimation? = object : ImAnimation {}): WithLayout<T> {
            this.mAnimation = mAnimation
            return this
        }

        fun create(): XRecyclerAdapter<T> = object : XRecyclerAdapter<T>(con, mData, type, list, defaultLayout, mAnimation) {
            override fun onBind(holder: XViewHolder, p: Int, t: T) {
                if (mapBind[type(t)] != null) mapBind[type(t)]!!(con, holder, mList, t, p) else defaultBind(con, holder, mList, t, p)
                if (clickable) holder.itemView.setOnClickListener { clickListener(con, holder, mList, t, p) }
            }
        }
    }


    abstract class XRecyclerAdapter<T>(private val context: Context,
                                       var mList: List<T>,
                                       private val type: (T) -> Any = {},
                                       private val list: ArrayList<Pair<Any, Int>>,
                                       private val defaultLayout: Int,
                                       private val mAnimation: ImAnimation? = null) : RecyclerView.Adapter<XRecyclerAdapter.XViewHolder>() {
        private var lookLastIndex = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XViewHolder = XViewHolder(LayoutInflater.from(context).inflate(getLayoutId(viewType), parent, false))

        private fun getLayoutId(viewType: Int): Int = if (viewType >= 0) list[viewType].second else defaultLayout

        override fun getItemCount(): Int = mList.size

        override fun getItemViewType(p: Int): Int {
            for ((type, t) in list.withIndex()) {
                if (t.first == type(mList[p])) return type
            }
            return -1
        }

        override fun onBindViewHolder(p0: XViewHolder, p1: Int) {
            onBind(p0, p1, mList[p1])
        }

        abstract fun onBind(holder: XViewHolder, p: Int, t: T)

        fun notify(list: List<T>) {
            this.mList = list
            notifyDataSetChanged()
        }

        override fun onViewAttachedToWindow(holder: XViewHolder) {
            super.onViewAttachedToWindow(holder)
            mAnimation?.let {
                if (holder.adapterPosition > it.getStart()) {
                    when (it.getAnModel()) {
                        AnModel.NORMAL -> addAnimation(holder, it)
                        AnModel.ONLY_UP -> if (holder.adapterPosition < lookLastIndex) addAnimation(holder, it)
                        AnModel.ONLY_DOWN -> if (holder.adapterPosition > lookLastIndex) addAnimation(holder, it)
                    }
                    lookLastIndex = holder.adapterPosition
                }
            }
        }

        override fun onViewDetachedFromWindow(holder: XViewHolder) {
            super.onViewDetachedFromWindow(holder)
        }

        private fun addAnimation(holder: XViewHolder, anim: ImAnimation) = anim.getAnimators(holder.itemView).forEach { it.start() }

        class XViewHolder(private val view: View) : RecyclerView.ViewHolder(view), ImHolder {

            override fun <T : View> getView(viewId: Int): T = view.findViewById(viewId)

            override fun setText(viewId: Int, text: String) = getView<TextView>(viewId).setText(text)

            override fun setImageResource(viewId: Int, drawableId: Int) = getView<ImageView>(viewId).setImageResource(drawableId)
        }
    }

}

