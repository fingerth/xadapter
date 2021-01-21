package com.fingerth.xadapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


class Xadapter2<T>(private val con: Context) {
    fun data(mData: List<T>): WithData<T> = WithData(con, mData)

    class WithData<T>(private val con: Context, private val mData: List<T>) {
        fun layoutId(layoutId: Int): WithLayout<T> = ViewTypeBuiLder().typeItem(layoutId).build()
        fun layoutId2spanBuilder(layoutId: Int): WithSpan<T> = ViewTypeBuiLder().typeItem(layoutId).build2spanBuilder()

        fun ViewTypeBuiLder(): ItemStyleBuilder<T> = ItemStyleBuilder(con, mData)

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

            fun build(): WithLayout<T> = WithLayout(con = con,mData = mData, type = type, list = list, defaultLayout = defaultLayout)

            fun build2spanBuilder(): WithSpan<T> = WithSpan(con, mData, type, list, defaultLayout)
        }

    }

    /**
     * StaggeredGridLayoutManager  下 只有FullSpan
     * GridLayoutManager
     */
    class WithSpan<T>(private val con: Context, private val mData: List<T>, private val type: (T) -> Any = {}, private val list: ArrayList<Pair<Any, Int>>, private val defaultLayout: Int) {
        private var typeSpan: (T) -> Any = {}
        private val listSpan by lazy { ArrayList<Pair<Any, Int>>() }
        fun spanBy(typeSpan: (T) -> Any): WithSpan<T> {
            this.typeSpan = typeSpan
            return this
        }

        fun spanItem(vararg p: Pair<Any, Int>): WithSpan<T> {
            listSpan.addAll(p)
            return this
        }

        fun build(): WithLayout<T> = WithLayout(con, mData, type, list, typeSpan, listSpan, defaultLayout)
    }

    class WithLayout<T>(private val con: Context, private val mData: List<T>,
                        private val type: (T) -> Any = {}, private val list: ArrayList<Pair<Any, Int>>,
                        private val typeSpan: (T) -> Any = {}, private val listSpan: ArrayList<Pair<Any, Int>> = arrayListOf(),
                        private val defaultLayout: Int) {
        private val mapBind = HashMap<Any, (Context, XViewHolder, List<T>, T, Int) -> Unit>()
        private var defaultBind: (Context, XViewHolder, List<T>, T, Int) -> Unit = { _, _, _, _, _ -> }
        private var clickable = false
        private var clickListener: (Context, XViewHolder, List<T>, T, Int) -> Unit = { _, _, _, _, _ -> }
        private var mAnimation: ImAnimation? = null

        //context, holder,mList, bean, p ->
        fun bind(any: Any? = null, black: (Context, XViewHolder, List<T>, T, Int) -> Unit): WithLayout<T> {
            if (any == null) this.defaultBind = black else mapBind[any] = black
            return this
        }

        fun itemClickListener(listener: (Context, XViewHolder, List<T>, T, Int) -> Unit): WithLayout<T> {
            clickable = true
            this.clickListener = listener
            return this
        }

        fun itemAnimation(mAnimation: ImAnimation? = object : ImAnimation {}): WithLayout<T> {
            this.mAnimation = mAnimation
            return this
        }

        fun create(): XRecyclerAdapter<T> = object : XRecyclerAdapter<T>(con, mData, type, list, typeSpan, listSpan, defaultLayout, mAnimation) {
            override fun onBind(holder: XViewHolder, p: Int, t: T) {
                if (mapBind[type(t)] != null) mapBind[type(t)]!!(con, holder, mList, t, p) else defaultBind(con, holder, mList, t, p)
                if (clickable) holder.itemView.setOnClickListener { clickListener(con, holder, mList, t, p) }
            }
        }
    }


    abstract class XRecyclerAdapter<T>(private val context: Context, var mList: List<T>,
                                       private val type: (T) -> Any = {}, private val list: ArrayList<Pair<Any, Int>>,
                                       private val typeSpan: (T) -> Any = {}, private val listSpan: ArrayList<Pair<Any, Int>>,
                                       private val defaultLayout: Int, private val mAnimation: ImAnimation? = null) : RecyclerView.Adapter<XViewHolder>() {


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
            (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.let {
                if (mList.size > holder.layoutPosition) {
                    it.isFullSpan = false
                    for (t in listSpan) {
                        if (t.first == typeSpan(mList[holder.layoutPosition])) {
                            it.isFullSpan = true
                            break
                        }
                    }
                }
            }
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


        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            (recyclerView.layoutManager as? GridLayoutManager)?.let {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        if (mList.size > position) {
                            for (t in listSpan) {
                                if (t.first == typeSpan(mList[position])) return it.spanCount / (t.second.coerceAtLeast(1))
                            }
                        }
                        return 1
                    }
                }
            }
        }


        private fun addAnimation(holder: XViewHolder, anim: ImAnimation) = anim.getAnimators(holder.itemView).forEach { it.start() }

    }

}

