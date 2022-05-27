package com.xavi.marvelheroes.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.xavi.marvelheroes.R
import com.xavi.marvelheroes.databinding.ItemCharacterBinding
import com.xavi.marvelheroes.domain.model.CharacterDomainModel

class CharactersAdapter(
    private val onItemClickListener: (CharacterDomainModel) -> Unit = {}
) : PagingDataAdapter<CharacterDomainModel, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CharacterDomainModel>() {
            override fun areItemsTheSame(
                oldItem: CharacterDomainModel,
                newItem: CharacterDomainModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CharacterDomainModel,
                newItem: CharacterDomainModel
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as? CharacterViewHolder)?.bind(item = it, onItemClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CharacterViewHolder(
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CharacterDomainModel,
            onItemClickListener: (CharacterDomainModel) -> Unit
        ) {
            item.thumbnail?.url?.let {
                val dimen = binding.root.context.resources.getDimension(R.dimen.space_s)
                binding.itemCharacterPreview.load(it) {
                    placeholder(R.drawable.placeholder_character)
                    error(ColorDrawable(Color.RED))
                    transformations(RoundedCornersTransformation(dimen))
                    scale(Scale.FILL)
                }
            }

            item.name?.let {
                binding.itemCharacterName.text = it
            }

            binding.root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }
}
