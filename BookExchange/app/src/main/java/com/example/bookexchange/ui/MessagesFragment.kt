package com.example.bookexchange.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookexchange.R

class MessagesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View.inflate(context, android.R.layout.simple_list_item_1, null)
        view.findViewById<TextView>(android.R.id.text1).text = "消息列表 - 待实现"
        return view
    }
}
