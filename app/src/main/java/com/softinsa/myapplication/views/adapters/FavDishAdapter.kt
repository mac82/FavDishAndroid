package com.softinsa.myapplication.views.adapters

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.R
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.databinding.ItemDishLayoutBinding
import com.softinsa.myapplication.views.activities.AddUpdateDishActivity
import com.softinsa.myapplication.views.fragments.AllDishesHomeFragment
import com.softinsa.myapplication.views.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment:Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>(){

    private var dishes: List<FavDishEntity> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root){
        val ivDishImage = view.ivDishImage
        val tvDishTitle = view.tvDishTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]

        Glide
            .with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.tvDishTitle.text = dish.title

        holder.itemView.setOnClickListener {
            if (fragment is AllDishesHomeFragment){
                fragment.showDishDetails(dish)
            } else if (fragment is FavoriteDishesFragment) {
                fragment.showDishDetails(dish)
            }
        }


        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter_dish_item, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_dish -> {
                        val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                        intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                        fragment.requireActivity().startActivity(intent)

                        Log.d(Constants.TAG, "Add dish")
                    }
                    R.id.action_delete_dish -> {
                        Log.d(Constants.TAG, "Delete dish")

                        if (fragment is AllDishesHomeFragment){
                            fragment.deleteDish(dish)
                        }
                    }
                }
                true
            }
            popup.show()
        }

        if (fragment is AllDishesHomeFragment){
            holder.ibMore.visibility = View.VISIBLE
        } else if (fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    // Update UI when list changes
    fun dishesList(list:List<FavDishEntity>){
        dishes = list
        notifyDataSetChanged()
    }
}