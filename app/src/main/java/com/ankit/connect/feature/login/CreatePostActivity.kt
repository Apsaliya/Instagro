@file:Suppress("MemberVisibilityCanBePrivate")

package com.ankit.connect.feature.login

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.ankit.connect.util.Cache
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.ankit.connect.R
import com.ankit.connect.data.model.Post
import com.ankit.connect.extensions.*
import com.ankit.connect.feature.login.profile.PostDetailActivity
import com.ankit.connect.feature.login.profile.PostsAdapter
import com.ankit.connect.store.FirebaseDbHelper
import com.ankit.connect.util.ItemAnimator
import com.ankit.connect.util.managers.PostManager
import com.google.firebase.auth.FirebaseAuth
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.sangcomz.fishbun.define.Define
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.post.*
import kotlinx.android.synthetic.main.toolbar_main.*
import timber.log.Timber
import java.util.ArrayList

/**
 * Created by ankit on 14/04/18.
 */
class CreatePostActivity : AppCompatActivity() {
  
  private var uris = ArrayList<Uri>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.post)
    
    list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    list.itemAnimator = ItemAnimator()
    fab.setOnClickListener {
      if (checkStoragePermission()) {
        showGallery()
      }
    }
    
    FirebaseDbHelper.getInstance().getPostList(0)
        .map { t -> t.posts }
        .subscribeOn(Schedulers.io())
        .subscribe({
          val adapter = PostsAdapter(this@CreatePostActivity, it)
          list.adapter = adapter
          adapter.setOnItemClickListener(listener)
        }, {
          it.printStackTrace()
        })
    
  }
  
  internal val listener = object : PostsAdapter.OnItemClickListener {
    override fun onItemClick(view: View, post: Post) {
      val i = Intent(this@CreatePostActivity, PostDetailActivity::class.java)
      Cache.put("data", post)
      startActivity(i)
    }
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      1 -> {
        if (grantResults.isNotEmpty()) {
          if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
            showGallery()
          } else {
            Toast.makeText(this, R.string.permission_deny_msg, Toast.LENGTH_SHORT).show()
            finish()
          }
        }
      }
    }
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
        uris = data?.getParcelableArrayListExtra<Uri>(Define.INTENT_PATH) as ArrayList<Uri>
        savePost()
      }
    }
  }
  
  private fun savePost() {
    upLoader.show()
    val post = Post(FirebaseAuth.getInstance().currentUser!!.uid)
    PostManager.getInstance().createOrUpdatePostWithImage(uris[0], post)
        .subscribeOn(Schedulers.io())
        .subscribe({
          upLoader.hide()
          Timber.d("Post created and uploaded successfully? $it")
        }, {
          upLoader.hide()
          it.printStackTrace()
        })
  }
  
  private fun showGallery() {
    uris.clear()
    FishBun.with(this)
        .setImageAdapter(GlideAdapter())
        .setSelectedImages(uris)
        .prepareActionBar(this)
        .setCounts()
        .setDefaults()
        .setDrawables(this)
        .setTexts(this)
        .startAlbum()
  }
}