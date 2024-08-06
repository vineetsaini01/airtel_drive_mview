package com.visionairtel.drivetest.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Scoped(content: @Composable () -> Unit) = content()

@Composable
fun Int.HSpacer() = Spacer(modifier = Modifier.height(this.dp))

@Composable
fun Int.WSpacer() = Spacer(modifier = Modifier.width(this.dp))