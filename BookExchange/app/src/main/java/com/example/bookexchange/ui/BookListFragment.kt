package com.example.bookexchange.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookexchange.R
import com.google.android.material.textfield.TextInputEditText

class BookListFragment : Fragment() {
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        booksRecyclerView = view.findViewById(R.id.booksRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        booksRecyclerView.layoutManager = LinearLayoutManager(context)
        // TODO: Set adapter when ready
    }
}
