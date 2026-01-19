package com.example.bookexchange.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookexchange.R

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, container, false)
        
        val prefs = requireActivity().getSharedPreferences("BookExchange", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "用户")
        
        view.findViewById<TextView>(android.R.id.text1).apply {
            text = "个人中心"
            textSize = 24f
        }
        view.findViewById<TextView>(android.R.id.text2).apply {
            text = "用户名: $username"
            textSize = 16f
        }
        
        return view
    }
}
