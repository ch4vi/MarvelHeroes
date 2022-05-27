package com.xavi.marvelheroes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.android.material.snackbar.Snackbar
import com.xavi.marvelheroes.R
import com.xavi.marvelheroes.databinding.FragmentListCharacterBinding
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.presentation.CharacterListEvent
import com.xavi.marvelheroes.presentation.CharacterListViewModel
import com.xavi.marvelheroes.presentation.CharacterListViewState
import com.xavi.marvelheroes.presentation.utils.EventObserver
import com.xavi.marvelheroes.ui.adapter.CharactersAdapter
import com.xavi.marvelheroes.ui.adapter.CharactersLoaderAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CharacterListFragment : Fragment() {

    private var binding: FragmentListCharacterBinding? = null
    private val viewModel: CharacterListViewModel by viewModel()

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var loaderAdapter: CharactersLoaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding?.root ?: with(
        FragmentListCharacterBinding.inflate(inflater, container, false)
    ) {
        binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initUI()
        dispatchGetData()
    }

    private fun resolveState(state: CharacterListViewState) {
        when (state) {
            is CharacterListViewState.OnFailure -> onFailure(state.error)
            is CharacterListViewState.ShowData -> showData(state.data)
        }
    }

    private fun initUI() {
        binding?.apply {
            charactersAdapter = CharactersAdapter()
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

    private fun onFailure(t: Throwable) {
        Timber.e(t)
    }

    private fun showData(data: PagingData<CharacterDomainModel>) {
        lifecycleScope.launch {
            binding?.apply {
                charactersAdapter.submitData(data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
