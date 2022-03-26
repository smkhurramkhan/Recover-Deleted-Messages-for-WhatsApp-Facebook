package com.test.recovermessages.activities

import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.test.recovermessages.R

class Tutorial : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isButtonBackVisible = false
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(R.layout.fragment_intro2, R.style.AppTheme)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(R.layout.fragment_intro, R.style.AppTheme)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(R.layout.fragment_intro3, R.style.AppTheme)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(R.layout.fragment_intro4, R.style.AppTheme)
                .build()
        )
    }
}