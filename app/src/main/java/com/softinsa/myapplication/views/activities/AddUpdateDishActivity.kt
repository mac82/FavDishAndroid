package com.softinsa.myapplication.views.activities


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.FavDishApplication
import com.softinsa.myapplication.R
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.databinding.ActivityAddUpdateDishBinding
import com.softinsa.myapplication.databinding.DialogCustomImageSelectionBinding
import com.softinsa.myapplication.databinding.DialogCustomListBinding
import com.softinsa.myapplication.views.adapters.CustomListItemAdapter
import com.softinsa.myapplication.views.base.FavDishViewModelFactory
import com.softinsa.myapplication.views.viewmodel.FavDishViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog

    private var mFavDishDetails:FavDishEntity? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setUpActionBar()
        setUpClickListeners()

        mFavDishDetails?.let {
            fillViewWithDishDetails(it)
        }

    }


    private fun setUpClickListeners(){
        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    private fun setUpActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        // If origin is edit click
        if (mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        } else {
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

        // Enable Back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.iv_add_dish_image -> {
                    Toast.makeText(this,"Image View Clicked!!", Toast.LENGTH_SHORT).show()
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE)
                    return
                }
                R.id.et_category -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_cooking_time -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookTime(),
                        Constants.DISH_COOKING_TIME)
                    return
                }

                R.id.btn_add_dish -> {
                    val title = mBinding.etTitle.text.toString().trim { it <= ' '}
                    val type = mBinding.etType.text.toString().trim { it <= ' '}
                    val category = mBinding.etCategory.text.toString().trim { it <= ' '}
                    val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' '}
                    val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim { it <= ' '}
                    val cookingDirection = mBinding.etDirectionToCook.text.toString().trim { it <= ' '}

                    // VALIDATIONS
                    when {
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_image),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_title),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_type),
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_category),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_ingredients),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_cooking_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            var dishId = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false

                            mFavDishDetails?.let {
                                if(it.id != 0){
                                    dishId = it.id
                                    imageSource = it.imageSource
                                    favoriteDish = it.favoriteDish
                                }
                            }

                            val favDishDetails: FavDishEntity = FavDishEntity(
                                mImagePath,
                                imageSource = imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTimeInMinutes,
                                cookingDirection,
                                favoriteDish = favoriteDish,
                                dishId
                            )
                            // Se Id = 0, significa que criei um dish novo
                            if (dishId == 0) {
                                mFavDishViewModel.insertDish(favDishDetails)

                                Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    "You successfully added your favorite dish details.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d(Constants.TAG, "Insertion Success ---")
                            } else {
                                mFavDishViewModel.updateDishDetails(favDishDetails)

                                Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    "You successfully updated your favorite dish details.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d(Constants.TAG, "Insertion Success ---")
                            }

                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            //Toast.makeText(this,"Camera Clicked!!", Toast.LENGTH_SHORT).show()

            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
              //  Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,

            ).withListener(object:MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()) {
                            openCamIntent()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }
            ).onSameThread().check()

            dialog.dismiss()
        }


        binding.tvGallery.setOnClickListener {
            // TODO Step 7: Ask for the permission while selecting the image from Gallery using Dexter Library. And Remove the toast message.
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                //    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        openGalleryIntent()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            "You have denied the storage permission.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

            // END
            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("No permissions for this feature")
            .setPositiveButton("Open SETTINGS")
                {_,_ ->
                    try {
                        // Abrir ecrã de settings da app no tele
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e:ActivityNotFoundException) {
                        e.stackTrace
                    }
                }

            .setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }.show()

    }

    private fun openCamIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //startActivityForResult(intent, CAMERA)
        launchCameraIntent.launch(intent)
    }



    private var launchCameraIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            data?.extras?.let {
                val thumbNail: Bitmap = data.extras!!.get("data") as Bitmap
                //mBinding.ivDishImage.setImageBitmap(thumbNail)

                Glide.with(this)
                    .load(thumbNail)
                    .centerCrop()
                    .into(mBinding.ivDishImage)

                mImagePath = saveImageToInternalStorage(bitmap = thumbNail)
                Log.d(Constants.TAG, mImagePath)

                mBinding.ivAddDishImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                )
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.e(Constants.TAG, "Task canceled by user")
        }
    }


    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //startActivityForResult(intent, CAMERA)
        launchGalleryIntent.launch(intent)
    }



    private var launchGalleryIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            data?.let {
                val selectedPhotoUri = data.data
                //mBinding.ivDishImage.setImageURI(selectedPhotoUri)
                Glide.with(this)
                    .load(selectedPhotoUri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //Store Image
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(Constants.TAG, "Error Loading image -> $e")
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
                                val bitmap:Bitmap = resource.toBitmap()
                                mImagePath = saveImageToInternalStorage(bitmap)
                                Log.d(Constants.TAG, mImagePath)
                            }
                            return false
                        }

                    })
                    .into(mBinding.ivDishImage)

                mBinding.ivAddDishImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                )
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.e(Constants.TAG, "Task canceled by user")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) //100 top - 0 min quality
            stream.flush()
            stream.close()
        } catch (e:IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsListDialog(title:String, itemsList:List<String>, selection:String){
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, null, itemsList, selection)
        binding.rvList.adapter = adapter

        mCustomListDialog.show()
    }

    fun selectedListItem(item:String, selection: String){
        when (selection) {
            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    private fun fillViewWithDishDetails(dish: FavDishEntity){
        if (dish.id != 0) {
            mImagePath = dish.image

            Glide.with(this)
                .load(dish.image)
                .centerCrop()
                .into(mBinding.ivDishImage)

            mBinding.etTitle.setText(dish.title)
            mBinding.etType.setText(dish.type)
            mBinding.etCategory.setText(dish.category)
            mBinding.etIngredients.setText(dish.ingredients)
            mBinding.etCookingTime.setText(dish.cookingTime)
            mBinding.etDirectionToCook.setText(dish.directionToCook)

            mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
        }
    }

    companion object {
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}