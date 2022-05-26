package com.xavi.marvelheroes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xavi.marvelheroes.databinding.ItemCharacterLoaderBinding
import com.xavi.marvelheroes.ui.getMessage

class CharactersLoaderAdapter(private val onRetry: () -> Unit) :
    LoadStateAdapter<CharactersLoaderAdapter.LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder(
            ItemCharacterLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onRetry
        )
    }

    class LoaderViewHolder(
        private val binding: ItemCharacterLoaderBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        itemLoaderProgress.visibility = View.VISIBLE
                        itemLoaderErrorGroup.visibility = View.GONE
                    }
                    is LoadState.NotLoading -> {
                        itemLoaderProgress.visibility = View.GONE
                        itemLoaderErrorGroup.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        itemLoaderProgress.visibility = View.GONE
                        itemLoaderErrorGroup.visibility = View.VISIBLE
                        itemLoaderMessage.text = loadState.error.getMessage(root.context)
                    }
                }

                binding.itemLoaderRetry.setOnClickListener {
                    retry()
                }
            }
        }
    }
}
