package com.jay.nearbysample.view

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.jay.nearbysample.BuildConfig
import com.jay.nearbysample.R
import com.jay.nearbysample.base.DataBindingActivity
import com.jay.nearbysample.databinding.ActivityWardrobeBinding
import com.jay.nearbysample.enums.WardrobeType
import com.jay.nearbysample.extension.addFragment
import com.jay.nearbysample.room.model.Liked
import com.jay.nearbysample.room.model.Wardrobe
import com.jay.nearbysample.utilities.BitmapUtils
import com.jay.nearbysample.utilities.Utility.getScaledBitmap
import com.jay.nearbysample.viewmodel.WardrobeViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity to display Wardrobe
 */
@AndroidEntryPoint
class WardrobeActivity : DataBindingActivity() {

    private val activity = this

    companion object {
        val TAG = WardrobeActivity::class.java.simpleName
        private const val REQUEST_IMAGE_CAPTURE_CAMERA = 5727
        private const val REQUEST_IMAGE_CAPTURE_GALLERY = 1789
        private const val IMAGE_WIDTH = 480
        private const val IMAGE_HEIGHT = 640
        private const val SEPERATOR = "-"
        private const val IMAGE_FORMAT = ".jpg"
        private const val DEFAULT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private var pictureFilePath: String? = null
    }

    private val binding: ActivityWardrobeBinding by binding(R.layout.activity_wardrobe)
    private val viewModel by viewModels<WardrobeViewModel>()
    private var wardrobeType: WardrobeType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@WardrobeActivity
            vm = viewModel
        }
        attachObserver()
        initViewAndListeners()
        viewModel.getWardrobeLiveData()
        viewModel.getLikedLiveData()
    }

    /**
     * Initialize view and click listeners
     */
    private fun initViewAndListeners() {
        val shirtFragment = ShirtFragment.newInstance()
        val jeansFragment = JeansFragment.newInstance()
        setUpFragment(shirtFragment, jeansFragment)

        binding.btnAddShirt.setOnClickListener {
            wardrobeType = WardrobeType.SHIRT
            showImagePickerOptions(activity)
        }
        binding.btnAddJeans.setOnClickListener {
            wardrobeType = WardrobeType.JEANS
            showImagePickerOptions(activity)
        }
        binding.btnFavorite.setOnClickListener {
            viewModel.wardrobeLiveData.apply {
                this.value?.let {
                    updateWardrobeFavorite(
                        shirtPosition = viewModel.currentSelectedShirtPosition.value ?: 0,
                        jeansPosition = viewModel.currentSelectedJeansPosition.value ?: 0
                    )
                } ?: kotlin.run {
                    Toast.makeText(
                        activity,
                        "Nothing to retrieve from Wardrobe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnShuffle.setOnClickListener {
            val shirtPosition = shirtFragment.onItemsShuffled()
            val jeansPosition = jeansFragment.onItemsShuffled()
            //Also make sure to assign to observers,avoiding stale state
            viewModel.currentSelectedShirtPosition.postValue(shirtPosition)
            viewModel.currentSelectedJeansPosition.postValue(jeansPosition)
        }
    }

    /**
     * Method to set Fragment
     */
    private fun setUpFragment(shirtFragment: ShirtFragment, bottomFragment: JeansFragment) {
        addFragment(shirtFragment, R.id.shirtFragment)
        addFragment(bottomFragment, R.id.jeansFragment)
    }

    /**
     * Method to attach observer
     */
    private fun attachObserver() {
        viewModel.wardrobeLiveData.observe(
            this,
            androidx.lifecycle.Observer { wardrobeList ->
                val shirtWardrobe =
                    wardrobeList.firstOrNull { wardrobe -> wardrobe.type == WardrobeType.SHIRT.type }
                val isShirtPresent = shirtWardrobe != null

                val jeansWardrobe =
                    wardrobeList.firstOrNull { wardrobe -> wardrobe.type == WardrobeType.JEANS.type }
                val isJeansPresent = jeansWardrobe != null

                viewModel.setShuffleVisibility(isShirtPresent && isJeansPresent)
                viewModel.setFavoriteVisibility(isShirtPresent && isJeansPresent)

                //Defensive check,if app was relaunched after inserting images in DB
                //Check if both the first items were marked favorite, Since onPageSelected won't be called
                //In which case make sure to set it as Favorite if it was...
                checkIsMarkedFavorite(
                    shirtPosition = viewModel.currentSelectedShirtPosition.value ?: 0,
                    jeansPosition = viewModel.currentSelectedJeansPosition.value ?: 0
                )
            })

        viewModel.currentSelectedShirtPosition.observe(
            this,
            androidx.lifecycle.Observer {
                checkIsMarkedFavorite(
                    shirtPosition = viewModel.currentSelectedShirtPosition.value ?: 0,
                    jeansPosition = viewModel.currentSelectedJeansPosition.value ?: 0
                )
            })

        viewModel.currentSelectedJeansPosition.observe(
            this,
            androidx.lifecycle.Observer {
                checkIsMarkedFavorite(
                    shirtPosition = viewModel.currentSelectedShirtPosition.value ?: 0,
                    jeansPosition = viewModel.currentSelectedJeansPosition.value ?: 0
                )
            })
    }

    /**
     * @param shirtPosition item position of shirt wardrobe
     * @param jeansPosition item position of jeans wardrobe
     */
    private fun checkIsMarkedFavorite(shirtPosition: Int, jeansPosition: Int) {
        val likedId = "$shirtPosition$SEPERATOR$jeansPosition"
        val isAlreadyLiked = viewModel.likedLiveData.value?.firstOrNull() {
            it.likedID == likedId
        }
        viewModel.setIsMarkedFavorite(isAlreadyLiked != null)
    }

    /**
     * @param shirtPosition item position of shirt wardrobe
     * @param jeansPosition item position of jeans wardrobe
     */
    private fun updateWardrobeFavorite(shirtPosition: Int, jeansPosition: Int) {
        val likedId = "$shirtPosition$SEPERATOR$jeansPosition"
        val isAlreadyLiked = viewModel.likedLiveData.value?.firstOrNull() {
            it.likedID == likedId
        }
        if (isAlreadyLiked == null) {
            viewModel.insertLike(Liked(likedID = likedId))
            viewModel.setIsMarkedFavorite(true)
        } else {
            viewModel.deleteLike(likedID = likedId)
            viewModel.setIsMarkedFavorite(false)
        }
    }

    /**
     * @param uri path of image
     * Inserts image to Wardrobe
     */
    private fun insertWardrobe(uri: Uri) {
        //Make sure to reset Favorite
        viewModel.setIsMarkedFavorite(false)
        //Every time view pager is updated check
        //Check if both the first items were marked favorite...
        viewModel.currentSelectedShirtPosition.postValue(0)
        viewModel.currentSelectedJeansPosition.postValue(0)

        val bitmap = getScaledBitmap(activity, uri)
        bitmap?.let { bm ->
            val compressedBitmap: Bitmap? =
                BitmapUtils.getResizedBitmap(bm, IMAGE_WIDTH, IMAGE_HEIGHT)
            compressedBitmap?.let { compBitmap ->
                val byteArray: ByteArray =
                    BitmapUtils.convertBitmapToByteArray(compBitmap)
                viewModel.insertWardrobe(
                    Wardrobe(
                        image = byteArray,
                        type = wardrobeType?.type
                    )
                )
            }
        }
    }

    /**
     * @param context object to access resource
     */
    private fun showImagePickerOptions(context: Context) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.choose))

        // add a list
        val items = arrayOf(
            context.getString(R.string.lbl_take_camera_picture),
            context.getString(R.string.lbl_choose_from_gallery)
        )
        builder.setItems(items) { dialog, which ->
            when (which) {
                0 -> launchCameraIntent()
                1 -> launchGalleryIntent()
            }
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            getString(android.R.string.cancel)
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    /**
     * navigating user to app settings
     */
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    /**
     * Method to launch Gallery Intent
     */
    private fun launchGalleryIntent() {
        Dexter.withActivity(this)
            .withPermissions(
                CAMERA,
                WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showGallery()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    /**
     * Method to show Gallery for choosing images
     */
    private fun showGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_IMAGE_CAPTURE_GALLERY
        )
    }

    /**
     * Method to launch Camera Intent
     */
    private fun launchCameraIntent() {
        Dexter.withActivity(this)
            .withPermissions(
                CAMERA,
                WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        captureImage()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    /**
     * Method to click Camera Captured images
     */
    private fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val pictureFile: File?
            try {
                pictureFile = getPictureFile()
            } catch (ex: IOException) {
                Toast.makeText(
                    this,
                    "Photo file can't be created, please try again",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (pictureFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this, BuildConfig.APPLICATION_ID + ".provider",
                    pictureFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    cameraIntent, REQUEST_IMAGE_CAPTURE_CAMERA
                )
            }
        }
    }

    /**
     * Method to get Picture File object
     */
    @Throws(IOException::class)
    private fun getPictureFile(): File? {
        val timeStamp = SimpleDateFormat(DEFAULT_TIMESTAMP, Locale.US).format(Date())
        val pictureFile = "IMG_$timeStamp"
        val root: String = Environment.getExternalStorageDirectory().toString()
        val folder = File(root + File.separator + "WardrobeSample")
        val directory = File(folder.toString())
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val image = File.createTempFile(pictureFile, IMAGE_FORMAT, folder)
        pictureFilePath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE_GALLERY -> if (resultCode == RESULT_OK) {
                if (data != null) {
                    val uri = data.data
                    uri?.let { uri ->
                        insertWardrobe(uri)
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE_CAMERA -> if (resultCode == RESULT_OK) {
                try {
                    pictureFilePath?.let { pictureFilePath ->
                        val imgFile = File(pictureFilePath)
                        imgFile?.let {
                            insertWardrobe(Uri.fromFile(it))
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.e(TAG, ex.message ?: "")
                }
            }
        }
    }
}