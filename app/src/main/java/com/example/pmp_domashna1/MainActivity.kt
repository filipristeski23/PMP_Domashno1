package com.example.pmp_domashna1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList

class MainActivity : AppCompatActivity() {
    private lateinit var search: EditText
    private lateinit var tag: EditText
    private lateinit var save: Button
    private lateinit var container: LinearLayout
    private lateinit var clear: Button

    private val prefs by lazy {
        getSharedPreferences("TwitterSearches", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search = findViewById(R.id.search)
        tag = findViewById(R.id.tag)
        save = findViewById(R.id.save)
        container = findViewById(R.id.container)
        clear = findViewById(R.id.clear)

        save.setOnClickListener {
            val queryText = search.text.toString()
            val tagText = tag.text.toString()

            if (queryText.isEmpty() || tagText.isEmpty()) {
                Toast.makeText(this, "Please enter both twitter search and search tag", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveSearch(tagText, queryText)
            search.text.clear()
            tag.text.clear()
            updateSearches()
        }

        clear.setOnClickListener {
            clearSearches()
        }

        updateSearches()
    }

    private fun saveSearch(tag: String, query: String) {
        val editor = prefs.edit()
        editor.putString(tag, query)
        editor.apply()
    }

    private fun updateSearches() {
        container.removeAllViews()

        val tags = prefs.all

        for ((tagName, query) in tags) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4
            }

            val tagBtn = Button(this)
            tagBtn.text = tagName
            tagBtn.backgroundTintList = ColorStateList.valueOf(0xFFDDDDDD.toInt())
            tagBtn.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3.0f
            )

            tagBtn.setOnClickListener {
                val searchURL = "https://twitter.com/search?q=${Uri.encode(query.toString())}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchURL))
                startActivity(intent)
            }

            val editBtn = Button(this)
            editBtn.text = "Edit"
            editBtn.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            ).apply {
                marginStart = 8
            }

            editBtn.setOnClickListener {
                tag.setText(tagName)
                search.setText(query.toString())
            }

            row.addView(tagBtn)
            row.addView(editBtn)

            container.addView(row)
        }
    }

    private fun clearSearches() {
        AlertDialog.Builder(this)
            .setTitle("Clear All Searches")
            .setMessage("Are you sure you want to delete all saved searches?")
            .setPositiveButton("Yes") { _, _ ->
                prefs.edit().clear().apply()
                updateSearches()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
