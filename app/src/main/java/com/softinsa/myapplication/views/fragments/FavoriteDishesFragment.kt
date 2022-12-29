package com.softinsa.myapplication.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.FavDishApplication
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.databinding.FragmentFavoriteDishesBinding
import com.softinsa.myapplication.views.activities.MainActivity
import com.softinsa.myapplication.views.adapters.FavDishAdapter
import com.softinsa.myapplication.views.base.FavDishViewModelFactory

import com.softinsa.myapplication.views.viewmodel.FavDishViewModel

class FavoriteDishesFragment : Fragment() {

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application as FavDishApplication)).repository)
    }

    private var _binding: FragmentFavoriteDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val favDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = favDishAdapter

        mFavDishViewModel.favoriteDishesList.observe(viewLifecycleOwner){ dishes ->
            dishes?.let {
                buildFavDishesList(favDishAdapter, it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildFavDishesList(favDishAdapter: FavDishAdapter, favDishesList: List<FavDishEntity>){
        if (favDishesList.isNotEmpty()){
            mBinding.rvDishesList.visibility = View.VISIBLE
            mBinding.tvNoDishesAddedYet.visibility = View.GONE
            // Display item
            favDishAdapter.dishesList(favDishesList)
        } else {
            mBinding.rvDishesList.visibility = View.GONE
            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
        }

        for (item in favDishesList){
            Log.d(Constants.TAG, "Favorite Dish Id -> ${item.id}")
        }
    }

    fun showDishDetails(dish: FavDishEntity){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(dish))
        if(activity is MainActivity) {
            (activity as MainActivity).hideBottomNavigationView()
        }
    }
}