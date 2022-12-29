package com.softinsa.myapplication.views.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.FavDishApplication
import com.softinsa.myapplication.R
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.databinding.FragmentDishDetailsBinding
import com.softinsa.myapplication.views.activities.AddUpdateDishActivity
import com.softinsa.myapplication.views.base.FavDishViewModelFactory
import com.softinsa.myapplication.views.viewmodel.FavDishViewModel
import java.io.IOException
import java.util.*


class DishDetailsFragment : Fragment() {

    private var mFavDishDetails: FavDishEntity? = null

    private var _binding: FragmentDishDetailsBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aparecer o menu na barra de topo
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()
        Log.d(Constants.TAG, "Detalhes de -> ${args.dishDetails.title}")

        // Used In Share Menu
        mFavDishDetails = args.dishDetails

        args.let { dish ->
            val dishDetails = dish.dishDetails

            try {
                // Alterar o fundo do layout conforme a cor predominante da imagem.
                    // Criar listener para a imagem e aplicar a palette
                Glide.with(this)
                    .load(dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(Constants.TAG, "ERROR loading image")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                Palette.from(resource.toBitmap()).generate() { palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    mBinding.rlDishDetailMain.setBackgroundColor(intColor)
                                }
                            }
                            return false
                        }

                    })
                    .into(mBinding.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mBinding.tvTitle.text = dishDetails.title
            mBinding.tvType.text =
                    // Used to make first letter capital
                    dishDetails.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            mBinding.tvCategory.text = dishDetails.category
            mBinding.tvIngredients.text = dishDetails.ingredients
            mBinding.tvCookingDirection.text = dishDetails.directionToCook

            // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding.tvCookingDirection.text = Html.fromHtml(
                    dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                @Suppress("DEPRECATION")
                mBinding.tvCookingDirection.text = Html.fromHtml(dishDetails.directionToCook)
            }
            // END






            mBinding.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, dishDetails.cookingTime)

            // Set Favorite Status
            updateDishFavoriteStatus(dishDetails)
        }

        // Set favorite Click
        updateDishFavoriteClick(args.dishDetails)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                mFavDishDetails?.let {
                    var image = ""
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE){
                        image = it.image
                    }

                    var cookingInstructions = ""
                    // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cookingInstructions = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    } else {
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(it.directionToCook).toString()
                    }

                    extraText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun updateDishFavoriteStatus(dishDetails: FavDishEntity){
        if (dishDetails.favoriteDish) {
            mBinding.ivFavoriteDish.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_selected)
        } else {
            mBinding.ivFavoriteDish.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_unselected)
        }
    }

    private fun updateDishFavoriteClick(dishDetails: FavDishEntity){
        mBinding.ivFavoriteDish.setOnClickListener {
            mFavDishViewModel.updateDishDetails(dishDetails)

            // Inverter o valor quando clicar
            dishDetails.favoriteDish = !dishDetails.favoriteDish

            if (dishDetails.favoriteDish) {
                mBinding.ivFavoriteDish.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_selected)
            } else {
                mBinding.ivFavoriteDish.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_unselected)
            }
        }
    }

}