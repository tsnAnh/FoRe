package dev.tsnanh.fore.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import dev.tsnanh.fore.R
import dev.tsnanh.fore.customview.MainToolbar.OnActionClick
import dev.tsnanh.fore.databinding.MainToolbarBinding

class MainToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var _binding: MainToolbarBinding? = null
    private val binding get() = _binding!!

    var isLeftButtonEnabled = false
        set(value) {
            field = value
            binding.leftBtn.isVisible = value
        }
    var isSecondaryButtonEnabled = false
        set(value) {
            field = value
            binding.secondaryButton.isVisible = value
        }

    var onNavigateUp = OnActionClick {}
        set(value) {
            field = value
            binding.leftBtn.setOnClickListener {
                value.onClick()
            }
        }

    var onSecondaryClick = OnActionClick { }
        set(value) {
            field = value
            binding.secondaryButton.setOnClickListener {
                value.onClick()
            }
        }

    init {
        _binding = MainToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        attrs?.also {
            with(context.theme) {
                obtainStyledAttributes(attrs, R.styleable.MainToolbar, 0, 0).apply {
                    try {
                        binding.primaryButton.setImageResource(
                            getResourceId(
                                R.styleable.MainToolbar_actionDrawable,
                                0
                            )
                        )
                    } finally {
                        recycle()
                    }
                }
                obtainStyledAttributes(attrs, R.styleable.MainToolbar, 0, 0).apply {
                    try {
                        binding.secondaryButton.setImageResource(
                            getResourceId(
                                R.styleable.MainToolbar_secondActionDrawable,
                                0
                            )
                        )
                    } finally {
                        recycle()
                    }
                }
            }
        }
        with(binding) {
            leftBtn.isVisible = isLeftButtonEnabled
            leftBtn.setOnClickListener {
                onNavigateUp.onClick()
            }
            secondaryButton.isVisible = isSecondaryButtonEnabled
            secondaryButton.setOnClickListener { onSecondaryClick.onClick() }
        }
    }

    fun interface OnActionClick {
        fun onClick()
    }

    fun setOnActionClick(listener: OnActionClick) {
        binding.primaryButton.setOnClickListener {
            listener.onClick()
        }
    }

    fun setTitle(title: String) = title.let { binding.title.text = title }

}