package com.example.parsegram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parsegram.Post
import com.example.parsegram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    lateinit var ivPreview:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPreview = view.findViewById(R.id.image_post)
        val pb :ProgressBar = view.findViewById(R.id.pbLoading)
        view.findViewById<Button>(R.id.btn_submit).setOnClickListener{

            pb.visibility =  ProgressBar.VISIBLE
            pb.postDelayed(
                java.lang.Runnable { pb.visibility =  ProgressBar.INVISIBLE},
                1500
            )
            val description = view.findViewById<EditText>(R.id.et_writeDescription).text.toString()
            val user= ParseUser.getCurrentUser()
            if (photoFile!=null){
                if (description == ""){
                    Toast.makeText(requireContext(), "Please provide description", Toast.LENGTH_SHORT).show()
                }
                else {
                    submitPost(view, description, user, photoFile!!)
                    Toast.makeText(requireContext(), "Successfully posted!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(), "Error submitting photo", Toast.LENGTH_SHORT).show()
            }

        }

        view.findViewById<Button>(R.id.bt_takePicture).setOnClickListener{

            onLaunchCamera()
        }

    }
    fun submitPost(view: View, description:String,user:ParseUser,file: File){
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground{ exception ->
            if (exception!= null){
                Toast.makeText(requireContext(), "Error submitting post", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }else{
                Log.i(TAG,"Succesfully posted!")
                val description = view?.findViewById<EditText>(R.id.et_writeDescription)
                if (description != null) {
                    description.text = null
                }
                val image = view?.findViewById<ImageView>(R.id.image_post)
                image?.setImageResource(0)
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Error taking picture", Toast.LENGTH_SHORT).show()
            }

    }
    companion object{
        const val TAG = "ComposeFragment"
    }




}