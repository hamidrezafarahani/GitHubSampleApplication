package com.example.githubsampleapplication.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.githubsampleapplication.R
import com.example.githubsampleapplication.databinding.FragmentSearchReposBinding
import com.example.githubsampleapplication.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchReposFragment : Fragment(R.layout.fragment_search_repos) {

    private val binding by viewBindings {
        FragmentSearchReposBinding.bind(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}