package com.xavi.marvelheroes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xavi.marvelheroes.R
import com.xavi.marvelheroes.databinding.FragmentCharacterDetailBinding
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.Failure

class CharacterDetailFragment : Fragment() {

    companion object {
        const val ARG_CHARACTER = "arg_character"
    }

    private var binding: FragmentCharacterDetailBinding? = null

    private fun getCharacter(): CharacterDomainModel? = arguments?.getParcelable(ARG_CHARACTER)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return (
                binding ?: run {
                    val binding = FragmentCharacterDetailBinding.inflate(layoutInflater)
                    this.binding = binding
                    binding
                }
                ).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCharacter()?.let {
            initUI(it)
        } ?: showFailure(Failure.GenericError("character not found"))
    }

    private fun initUI(character: CharacterDomainModel) {

    }

    private fun showFailure(f: Throwable) {
        binding?.apply {
            Snackbar.make(root, f.getMessage(root.context), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.app_global_retry) {
                    findNavController().navigateUp()
                }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
