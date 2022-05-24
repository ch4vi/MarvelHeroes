package com.xavi.marvelheroes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xavi.marvelheroes.databinding.FragmentFirstBinding
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.presentation.TestEvent
import com.xavi.marvelheroes.presentation.TestViewModel
import com.xavi.marvelheroes.presentation.TestViewState
import com.xavi.marvelheroes.presentation.utils.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var binding: FragmentFirstBinding? = null
    private val viewModel: TestViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding?.root ?: with(
        FragmentFirstBinding.inflate(inflater, container, false)
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

    private fun initUI() {
        binding?.apply {
            buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                dispatchGetData()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { resolveState(it) })
    }

    private fun dispatchGetData() {
        viewModel.dispatch(TestEvent.GetCharacters)
    }

    private fun resolveState(state: TestViewState) {
        when (state) {
            TestViewState.Loading -> Unit // showProgressBar(true)
            is TestViewState.OnFailure -> Unit // showError()
            is TestViewState.ShowData -> showData(state.data)
        }
    }

    private fun showData(data: CharactersDomainModel) {
        Timber.d("${data.characters.size}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
