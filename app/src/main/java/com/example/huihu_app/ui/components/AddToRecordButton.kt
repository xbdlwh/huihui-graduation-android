package com.example.huihu_app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AddToRecordButton(
    onAddToRecord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text("添加到记录")
    }

    if (showDialog) {
        MealTypeDialog(
            onDismiss = { showDialog = false },
            onConfirm = { mealType ->
                onAddToRecord(mealType)
                showDialog = false
            }
        )
    }
}

@Composable
fun MealTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择餐次", fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                mealTypes.forEach { mealType ->
                    TextButton(
                        onClick = { onConfirm(mealType) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (mealType) {
                                "breakfast" -> "早餐"
                                "lunch" -> "午餐"
                                "dinner" -> "晚餐"
                                "snack" -> "零食"
                                else -> mealType
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}