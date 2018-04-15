package com.ankit.connect.feature.login.profile

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import com.ankit.connect.R
import android.view.inputmethod.InputMethodManager
import com.ankit.connect.data.model.Post
import com.ankit.connect.extensions.showSnackBar
import com.ankit.connect.store.FirebaseDbHelper
import com.ankit.connect.util.Cache
import com.bumptech.glide.Glide
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_feed.*
import timber.log.Timber

/**
 * Created by ankit on 15/04/18.
 */
class PostDetailActivity : AppCompatActivity() {
  lateinit var post: Post
  private var defaultColor: Int = 0
  private lateinit var inputManager: InputMethodManager
  private var isEditTextVisible: Boolean = false
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    post = Cache.get("data") as Post
    sendComment.setOnClickListener {
      val commentText = addComment.text.toString()
      
      if (commentText.isNotEmpty()) {
        FirebaseDbHelper.getInstance().createComment(commentText, post.id!!)
            .subscribeOn(Schedulers.io())
            .subscribe({
              Timber.d("Comment added successfully")
              addComment.text = null
              addComment.clearFocus()
            }, {
              showSnackBar("Something went wrong. Could not add comment.")
            })
      }
    }
  }
  
  private fun setupValues() {
    inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    isEditTextVisible = false
  }
}