package com.example.githubsampleapplication.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.githubsampleapplication.R
import com.example.githubsampleapplication.data.local.Repo
import com.example.githubsampleapplication.databinding.RepoViewItemBinding
import com.example.githubsampleapplication.databinding.SeparatorViewItemBinding

class ReposAdapter : PagingDataAdapter<UiModel, ViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is UiModel.RepoItem -> (holder as RepoViewHolder).bind(it.repo)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(it.description)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            R.layout.repo_view_item -> RepoViewHolder.create(parent)
            else -> SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.RepoItem -> R.layout.repo_view_item
            is UiModel.SeparatorItem -> R.layout.separator_view_item
            else -> throw UnsupportedOperationException("Unknown!")
        }
    }

    class RepoViewHolder(
        private val binding: RepoViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var repo: Repo? = null

        init {
            itemView.setOnClickListener {
                repo?.url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    it.context.startActivity(intent)
                }
            }
        }

        fun bind(repo: Repo) {
            this.repo = repo

            with(binding) {
                with(repo) {
                    repoName.text = fullName

                    var descriptionVisibility = View.GONE
                    if (description != null) {
                        repoDescription.text = description
                        descriptionVisibility = View.VISIBLE
                    }
                    repoDescription.visibility = descriptionVisibility

                    repoStars.text = stars.toString()
                    repoForks.text = forks.toString()

                    var languageVisibility = View.GONE
                    if (!language.isNullOrEmpty()) {
                        val resource = itemView.context.resources
                        repoLanguage.text = resource.getString(R.string.language, language)
                        languageVisibility = View.VISIBLE
                    }
                    repoLanguage.visibility = languageVisibility
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): RepoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_view_item, parent, false)
                val binding = RepoViewItemBinding.bind(view)
                return RepoViewHolder(binding)
            }
        }
    }

    class SeparatorViewHolder(
        private val binding: SeparatorViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(description: String) = with(binding) {
            separatorDescription.text = description
        }

        companion object {
            fun create(parent: ViewGroup): SeparatorViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.separator_view_item, parent, false)
                val binding = SeparatorViewItemBinding.bind(view)
                return SeparatorViewHolder(binding)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepoItem && newItem is UiModel.RepoItem
                        && oldItem.repo.fullName == newItem.repo.fullName) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem
                                && oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}