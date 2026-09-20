package com.egorov.workout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var listView: ListView
    private lateinit var historyLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var clearHistoryButton: Button
    private lateinit var emptyTextView: TextView

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var prefs: android.content.SharedPreferences

    private val exercises = listOf("Отжимания", "Приседания")
    private val history = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        listView = findViewById(R.id.listView)
        historyLayout = findViewById(R.id.historyLayout)
        progressBar = findViewById(R.id.progressBar)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        emptyTextView = findViewById(R.id.emptyTextView)

        prefs = getSharedPreferences("search_history", MODE_PRIVATE)
        loadHistory()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString("query", "")
            val savedList = savedInstanceState.getStringArrayList("list") ?: arrayListOf()

            editText.setText(savedQuery)

            adapter.clear()
            adapter.addAll(savedList)


            if (savedList.isEmpty() && savedQuery.isNotEmpty()) {
                emptyTextView.visibility = View.VISIBLE
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        clearButton.setOnClickListener {
            editText.text.clear()
            adapter.clear()
            emptyTextView.visibility = View.GONE
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && history.isNotEmpty()) {
                adapter.clear()
                adapter.addAll(history)
                historyLayout.visibility = View.VISIBLE
            } else {
                historyLayout.visibility = View.GONE
            }
        }

        editText.setOnEditorActionListener { _, _, _ ->
            performSearch(editText.text.toString())
            true
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position) ?: return@setOnItemClickListener


            if (selected == "Ничего не найдено") return@setOnItemClickListener

            history.remove(selected)
            history.add(0, selected)
            if (history.size > 10) history.removeAt(history.size - 1)

            saveHistory()

            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("exercise", selected)
            startActivity(intent)
        }

        clearHistoryButton.setOnClickListener {
            history.clear()
            prefs.edit().remove("history").apply()
            adapter.clear()
            Toast.makeText(this, "История очищена", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("query", editText.text.toString())

        val currentList = ArrayList<String>()
        for (i in 0 until listView.count) {
            currentList.add(listView.getItemAtPosition(i).toString())
        }

        outState.putStringArrayList("list", currentList)
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        progressBar.visibility = View.VISIBLE
        emptyTextView.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({

            val results = exercises.filter {
                it.contains(query, ignoreCase = true)
            }

            progressBar.visibility = View.GONE
            adapter.clear()

            if (results.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
            } else {
                emptyTextView.visibility = View.GONE
                adapter.addAll(results)
            }

        }, 500)
    }

    private fun saveHistory() {
        val set = history.toSet()
        prefs.edit().putStringSet("history", set).apply()
    }

    private fun loadHistory() {
        val set = prefs.getStringSet("history", emptySet()) ?: emptySet()
        history.clear()
        history.addAll(set)
    }
}