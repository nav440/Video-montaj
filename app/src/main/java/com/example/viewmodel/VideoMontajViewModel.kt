package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiService
import com.example.model.SampleVideo
import com.example.model.VideoProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Home : AppScreen()
    object Processing : AppScreen()
    object Result : AppScreen()
    object SavedProjects : AppScreen()
    object AiDirector : AppScreen()
}

class VideoMontajViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedVideoTitle = MutableStateFlow("Mening Videom.mp4")
    val selectedVideoTitle: StateFlow<String> = _selectedVideoTitle.asStateFlow()

    private val _selectedVideoUri = MutableStateFlow<Uri?>(null)
    val selectedVideoUri: StateFlow<Uri?> = _selectedVideoUri.asStateFlow()

    private val _userPrompt = MutableStateFlow("")
    val userPrompt: StateFlow<String> = _userPrompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow("Kinematografik")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()

    private val _processingStep = MutableStateFlow("AI videoni o'qimoqda...")
    val processingStep: StateFlow<String> = _processingStep.asStateFlow()

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _savedProjects = MutableStateFlow<List<VideoProject>>(emptyList())
    val savedProjects: StateFlow<List<VideoProject>> = _savedProjects.asStateFlow()

    val sampleVideos = listOf(
        SampleVideo("1", "Shahar Sayohati Vlog", "0:45", "Vlog", "sample_vlog"),
        SampleVideo("2", "Tabiat va Sharshara", "0:60", "Nature", "sample_nature"),
        SampleVideo("3", "Sport va Harakat", "0:35", "Action", "sample_sport"),
        SampleVideo("4", "Kofe va Kayfiyat", "0:30", "Cinematic", "sample_cafe")
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectVideo(title: String, uri: Uri?) {
        _selectedVideoTitle.value = title
        _selectedVideoUri.value = uri
    }

    fun updatePrompt(prompt: String) {
        _userPrompt.value = prompt
    }

    fun updateStyle(style: String) {
        _selectedStyle.value = style
    }

    fun startAiMontage() {
        viewModelScope.launch {
            _currentScreen.value = AppScreen.Processing

            _processingStep.value = "Kadrlar tahlil qilinmoqda va shovqinlar tozalanmoqda..."
            kotlinx.coroutines.delay(1000)

            _processingStep.value = "AI avtomatik kesish va ritm rejasini tuzmoqda..."
            kotlinx.coroutines.delay(1200)

            _processingStep.value = "Rang korreksiyasi va kinematografik filtrlar tanlanmoqda..."
            kotlinx.coroutines.delay(1000)

            _processingStep.value = "Fon musiqasi va avtomatik subtitrlar sinxronlanmoqda..."
            val project = GeminiService.generateMontagePlan(
                videoTitle = _selectedVideoTitle.value,
                userPrompt = _userPrompt.value,
                style = _selectedStyle.value
            )

            kotlinx.coroutines.delay(800)
            _currentProject.value = project
            
            // Add to saved projects
            _savedProjects.update { list -> listOf(project) + list }

            _currentScreen.value = AppScreen.Result
        }
    }

    fun saveProject(project: VideoProject) {
        if (!_savedProjects.value.any { it.id == project.id }) {
            _savedProjects.update { listOf(project) + it }
        }
    }
}
