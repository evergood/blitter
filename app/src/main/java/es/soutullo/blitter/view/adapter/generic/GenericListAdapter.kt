package es.soutullo.blitter.view.adapter.generic

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder
import es.soutullo.blitter.BR
import es.soutullo.blitter.view.adapter.handler.IListHandler
import es.soutullo.blitter.view.util.BlitterUtils

/**
 * Abstract RecyclerView adapter for all the recycler views of the app
 * @param items The list of generic items to be represented on the recycler view
 * @param handler The handler. Gets called when the user performs interactions with a recycler view item,
 *                such as a click
 */
abstract class GenericListAdapter<Item>(val items: MutableList<Item> = mutableListOf(), var handler: IListHandler? = null, var fab: FloatingActionButton? = null)
        : RecyclerView.Adapter<GenericListAdapter<Item>.GenericListViewHolder>() {
    companion object {
        private val VIEW_TYPE_STANDARD = 0
        private val VIEW_TYPE_NULL = 1
    }

    var recyclerView: RecyclerView? = null
    private var isLoading = false

    override fun getItemCount(): Int = this.items.size
    override fun getItemViewType(position: Int): Int = if(this.items[position] != null) VIEW_TYPE_STANDARD else VIEW_TYPE_NULL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericListViewHolder =
            GenericListViewHolder(LayoutInflater.from(parent.context).inflate(this.getActualItemLayout(viewType), parent, false))

    override fun onBindViewHolder(holder: GenericListViewHolder, position: Int) {
        holder.binding!!.setVariable(BR.item, this.items[position])
        holder.binding.setVariable(BR.utils, BlitterUtils)
        holder.binding.executePendingBindings()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        val layoutManager = LinearLayoutManager(recyclerView?.context)

        recyclerView?.layoutManager = layoutManager
        if (this.showSeparators()) {
            recyclerView?.addItemDecoration(DividerItemDecoration(recyclerView.context, layoutManager.orientation))
        }

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, diffX: Int, diffY: Int) {
                this@GenericListAdapter.onScroll(diffX, diffY)
            }
        })
    }

    /**
     * Returns a item object of the adapter, given its index
     * @param index The index
     * @return The item object
     */
    fun get(index: Int): Item = this.items[index]

    /**
     * Tells if there is any item present
     * @return True if there is any item present
     */
    fun isEmpty(): Boolean = this.items.isEmpty()

    /**
     * Adds a new item to the recycler view
     * @param item The item object to add
     */
    fun add(item: Item) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }

    /**
     * Adds a new item to the recycler view, positioning it at the given index
     * @param index The index
     * @param item The item object to add
     */
    fun add(index: Int, item: Item) {
        this.items.add(index, item)
        this.notifyDataSetChanged()
    }

    /**
     * Adds a new array of items to the recycler view
     * @param items The items objects to add
     */
    fun addAll(items: Collection<Item>) {
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    /**
     * Replaces a item by other given its position and the new item
     * @param index The index
     * @param newItem The new item
     */
    fun update(index: Int, newItem: Item) {
        this.items[index] = newItem
        this.notifyDataSetChanged()
    }

    /**
     * Removes a item from the recycler view, given its index
     * @param index The index
     */
    fun remove(index: Int) {
        this.items.removeAt(index)
        this.notifyDataSetChanged()
    }

    /** Removes all the items from the recycler view */
    fun clear() {
        this.items.clear()
        this.notifyDataSetChanged()
    }

    /** @return The correct item layout depending on the given view type */
    protected fun getActualItemLayout(viewType: Int): Int = if(viewType == VIEW_TYPE_STANDARD) this.getItemLayout() else this.getNullItemLayout()

    private fun onScroll(diffX: Int, diffY: Int) {
        if(diffY > 0) {
            this.fab?.hide()
            if((this.recyclerView?.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: -10 >= this.itemCount - 1) {
                if(!this.isLoading) {
                    this.isLoading = true
                    this.onLoadMore()
                    this.isLoading = false
                }
            }
        } else if (diffY < 0) {
            this.fab?.show()
        }
    }

    open fun onLoadMore() { }

    /** @return True if separators must be drawn between each element of the list */
    open protected fun showSeparators(): Boolean = true

    /** @return The IDs of the clickable views who live inside each item of the list */
    open protected fun clickableChildren(): Array<Int> = arrayOf()

    /** @return The ID of the layout of the items of the recycler view */
    protected abstract fun getItemLayout(): Int

    /** @return The ID of the layout for the null item */
    open protected fun getNullItemLayout(): Int = 0

    /** Generic ViewHolder for each item of the RecyclerView */
    open inner class GenericListViewHolder(protected val view: View, multiSelector: MultiSelector = MultiSelector()): MultiSelectorBindingHolder(view, multiSelector) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(this.view)

        init {
            val clickListener = View.OnClickListener { this.onClick(it.id) }

            this.view.setOnClickListener(clickListener)
            this@GenericListAdapter.clickableChildren().forEach { this.view.findViewById<View>(it).setOnClickListener(clickListener) }
        }

        /**
         * Gets called when the item is clicked
         * @param viewId The clicked view id, which can be the id of the list item itself, or any of
         *        its clickable children
         */
        open protected fun onClick(viewId: Int) {
            this@GenericListAdapter.handler?.onItemClicked(this.adapterPosition, viewId)
        }

        override fun isSelectable(): Boolean = false
        override fun isActivated(): Boolean = false
        override fun setSelectable(selectable: Boolean) {}
        override fun setActivated(activated: Boolean) {}
    }
}