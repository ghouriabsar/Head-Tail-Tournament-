package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TeamPointsRow
import com.example.ui.theme.*

@Composable
fun PointsBarChart(
    pointsTable: List<TeamPointsRow>,
    modifier: Modifier = Modifier
) {
    if (pointsTable.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, HighDensityBorder),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "📊 Team Points Chart",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HighDensityGreenHeader
            )
            Spacer(modifier = Modifier.height(16.dp))

            val maxPoints = (pointsTable.maxOfOrNull { it.points } ?: 1).coerceAtLeast(1)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val barWidth = size.width / (pointsTable.size * 2)
                val spacing = barWidth

                pointsTable.forEachIndexed { idx, row ->
                    val barHeight = (row.points.toFloat() / maxPoints.toFloat()) * (size.height - 30.dp.toPx())
                    val x = idx * (barWidth + spacing) + spacing / 2
                    val y = size.height - barHeight - 20.dp.toPx()

                    drawRoundRect(
                        color = when (idx) {
                            0 -> HighDensityGreenHeader
                            1 -> HighDensitySecondaryPill
                            2 -> FourBlue
                            else -> HighDensityTextSecondary
                        },
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                pointsTable.forEach { row ->
                    Text(
                        text = row.teamName.replace("Team ", ""),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityTextPrimary
                    )
                }
            }
        }
    }
}

