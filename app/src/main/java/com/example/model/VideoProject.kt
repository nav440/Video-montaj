package com.example.model

data class VideoProject(
    val id: String,
    val title: String,
    val sourceUri: String,
    val userPrompt: String,
    val style: String,
    val durationSec: Int,
    val createdAt: Long,
    val aiSummary: String,
    val cutsCount: Int,
    val selectedFilter: String,
    val backgroundMusic: String,
    val cutScript: List<CutItem>
)

data class CutItem(
    val startTime: String,
    val endTime: String,
    val description: String,
    val effect: String
)

data class SampleVideo(
    val id: String,
    val title: String,
    val duration: String,
    val category: String,
    val uri: String
)
