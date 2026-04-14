package com.example.bytebar.model

data class Plan(val id: Int,
                val title: String,
                val content: String,
                val startTime: String,
                val duration: Int,
                val isCompleted: Boolean)