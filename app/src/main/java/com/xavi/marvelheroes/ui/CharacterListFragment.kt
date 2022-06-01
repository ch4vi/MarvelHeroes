package com.xavi.marvelheroes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.android.material.snackbar.Snackbar
import com.xavi.marvelheroes.R
import com.xavi.marvelheroes.databinding.FragmentCharacterListBinding
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.presentation.CharacterListEvent
import com.xavi.marvelheroes.presentation.CharacterListViewModel
import com.xavi.marvelheroes.presentation.CharacterListViewState
import com.xavi.marvelheroes.presentation.utils.EventObserver
import com.xavi.marvelheroes.ui.CharacterDetailFragment.Companion.ARG_CHARACTER
import com.xavi.marvelheroes.ui.adapter.CharactersAdapter
import com.xavi.marvelheroes.ui.adapter.CharactersLoaderAdapter
import com.xavi.marvelheroes.ui.utils.getMessage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterListFragment : Fragment() {

    private var binding: FragmentCharacterListBinding? = null
    private val viewModel: CharacterListViewModel by viewModel()

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var loaderAdapter: CharactersLoaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return (
            binding ?: run {
                val binding = FragmentCharacterListBinding.inflate(layoutInflater)
                this.binding = binding
                binding
            }
            ).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initUI()
        dispatchGetData()
    }

    private fun resolveState(state: CharacterListViewState) {
        when (state) {
            is CharacterListViewState.ShowData -> showData(state.data)
            is CharacterListViewState.ShowQueryData -> showQueryData(state.data)
        }
    }

    private fun initUI() {
        initCharacterListUI()
        binding?.apply {
            searchField.editText?.addTextChangedListener { text ->
                viewModel.dispatch(CharacterListEvent.SearchCharacters(text.toString()))
            }
        }
    }

    private fun initCharacterListUI() {
        binding?.apply {
            charactersAdapter = CharactersAdapter { goToDetail(it) }
            loaderAdapter = CharactersLoaderAdapter { charactersAdapter.retry() }
            charactersAdapter.addLoadStateListener {
                when (val loadState = it.refresh) {
                    is LoadState.Error -> {
                        val message = loadState.error.getMessage(root.context)
                        Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.app_global_retry) {
                                charactersAdapter.retry()
                            }.show()
                    }
                    else -> Unit
                }
            }

            characterList.adapter =
                charactersAdapter.withLoadStateFooter(loaderAdapter)
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { resolveState(it) })
    }

    private fun dispatchGetData() {
        viewModel.dispatch(CharacterListEvent.GetCharacters)
    }

    private fun goToDetail(character: CharacterDomainModel) {
        findNavController().navigate(
            R.id.action_to_character_detail,
            bundleOf(ARG_CHARACTER to character)
        )
    }

    private fun showData(data: PagingData<CharacterDomainModel>) {
        lifecycleScope.launch {
            binding?.apply {
                charactersAdapter.submitData(data)
            }
        }
    }

    private fun showQueryData(data: PagingData<CharacterDomainModel>) {
        lifecycleScope.launch {
            charactersAdapter.submitData(data)
            binding?.characterList?.scrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
