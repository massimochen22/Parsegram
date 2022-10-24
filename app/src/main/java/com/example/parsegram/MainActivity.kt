package com.example.parsegram

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parsegram.fragments.ComposeFragment
import com.example.parsegram.fragments.FeedFragment
import com.example.parsegram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar:Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
//        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val curUsername = findViewById<TextView>(R.id.tvUserName3)
        curUsername.text = ParseUser.getCurrentUser().username.toString()

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item->
            var fragmentToShow: Fragment? = null

            when (item.itemId){
                R.id.action_compose->{
                    fragmentToShow = ComposeFragment()
                }
                R.id.action_home->{
                    fragmentToShow = FeedFragment()
                }
                R.id.action_profile->{
                    fragmentToShow = ProfileFragment()
                }
            }
            if (fragmentToShow!=null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }
            true
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btn_logout){
            logout()
        }
        return super.onOptionsItemSelected(item)
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