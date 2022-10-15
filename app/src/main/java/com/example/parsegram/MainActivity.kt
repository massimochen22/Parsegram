package com.example.parsegram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File


class MainActivity : AppCompatActivity() {
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
//    late init va pb :ProgressBar = findViewById(R.id.pbLoading)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pb :ProgressBar = findViewById(R.id.pbLoading)

        queryPost()

        findViewById<Button>(R.id.logout_button).setOnClickListener{
            logout()
        }
        findViewById<Button>(R.id.bt_takePicture).setOnClickListener{

            onLaunchCamera()
        }
        findViewById<Button>(R.id.btn_submit).setOnClickListener{

            pb.visibility =  ProgressBar.VISIBLE
            pb.postDelayed(
                java.lang.Runnable { pb.visibility =  ProgressBar.INVISIBLE},
                2000
            )
            val description = findViewById<EditText>(R.id.et_writeDescription).text.toString()
            val user= ParseUser.getCurrentUser()
            if (photoFile!=null){
                if (description == ""){
                    Toast.makeText(this, "Please provide description", Toast.LENGTH_SHORT).show()
                }
                submitPost(description,user,photoFile!!)
            }
            else{
                Toast.makeText(this, "Error submitting photo", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                val ivPreview:ImageView = findViewById(R.id.image_post)
                ivPreview!!.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Error taking picture", Toast.LENGTH_SHORT).show()
            }

    }

    fun submitPost(description:String,user:ParseUser,file:File){
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground{ exception ->
            if (exception!= null){
                Log.i(TAG,"")
                exception.printStackTrace()
//                TODO:Show a toast with error
            }else{
                 Log.i(TAG,"Succesfully posted!")
//                TODO:Reset editText blank
//                TODO: Reset imageview
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
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
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
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun queryPost(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.findInBackground(object :FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e!=null){
                    Log.e(TAG,"Error Fetching posts")
                }
                else{
                    if (posts!=null){
                        for (post in posts){
                            Log.i(TAG,"Post:"+ post.getDescription()+" username: "+ post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }
    private fun logout(){
        ParseUser.logOut()
        val currentUser = ParseUser.getCurrentUser()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val TAG = "MainActivity"
    }
}