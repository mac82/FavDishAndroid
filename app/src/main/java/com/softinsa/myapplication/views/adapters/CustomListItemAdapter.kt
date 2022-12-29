package com.softinsa.myapplication.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.softinsa.myapplication.databinding.ItemCustomListBinding
import com.softinsa.myapplication.views.activities.AddUpdateDishActivity
import com.softinsa.myapplication.views.fragments.AllDishesHomeFragment

class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems:List<String>,
    private val selection: String
): RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

    class ViewHolder(view: ItemCustomListBinding): RecyclerView.ViewHolder(view.root){
        val tvText = view.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListBinding = ItemCustomListBinding.inflate(LayoutInflater.from(activity))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item
        holder.itemView.setOnClickListener {
            if (activity is AddUpdateDishActivity) {
                activity.selectedListItem(item, selection)
            }

            fragment?.let {
                if (fragment is AllDishesHomeFragment){
                    fragment.filterSelection(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}