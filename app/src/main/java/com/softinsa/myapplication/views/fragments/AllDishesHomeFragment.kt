package com.softinsa.myapplication.views.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.FavDishApplication
import com.softinsa.myapplication.R
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.databinding.DialogCustomListBinding
import com.softinsa.myapplication.databinding.FragmentHomeAllDishesBinding
import com.softinsa.myapplication.views.activities.AddUpdateDishActivity
import com.softinsa.myapplication.views.activities.MainActivity
import com.softinsa.myapplication.views.adapters.CustomListItemAdapter
import com.softinsa.myapplication.views.adapters.FavDishAdapter
import com.softinsa.myapplication.views.base.FavDishViewModelFactory
import com.softinsa.myapplication.views.viewmodel.FavDishViewModel

class AllDishesHomeFragment : Fragment() {

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private var _binding: FragmentHomeAllDishesBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mCustomListDialog: Dialog
    private lateinit var mFavDishAdapter: FavDishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aparecer o menu na barra de topo
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = mFavDishAdapter

        observeAllDishes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_all_dishes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireContext(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeAllDishes(){
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    // Display item
                    mFavDishAdapter.dishesList(it)
                } else {
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }

                // Log each dish
                for (item in it) {
                    Log.d(Constants.TAG, "Id ->  ${item.id} :: Title -> ${item.title}")
                }
            }
        }
    }


    // Função de navegação com argumentos definidos no xml de navigation
    // CHAMADA NO ADAPTER
    fun showDishDetails(dish:FavDishEntity){
        findNavController().navigate(AllDishesHomeFragmentDirections.actionAllDishesToDishDetails(dish))
        if (activity is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    fun deleteDish(favDishEntity: FavDishEntity){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, favDishEntity.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface, _ ->
            mFavDishViewModel.deleteFavDish(favDishEntity)
            dialogInterface
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface, _ ->
            dialogInterface
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesHomeFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter

        mCustomListDialog.show()
    }

    fun filterSelection(filterItemSelection: String){
        mCustomListDialog.dismiss()

        if (filterItemSelection == Constants.ALL_ITEMS) {
            observeAllDishes()
        } else {
            Log.d(Constants.TAG, "filter selected -> $filterItemSelection")
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){ dishes ->
                dishes?.let {
                    if (it.isNotEmpty()) {
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    } else {
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}