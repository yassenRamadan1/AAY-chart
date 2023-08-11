package com.aay.compose.lineChart.lines

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.*
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineShadow

fun DrawScope.drawQuarticLineWithShadow(
    line: LineParameters,
    lowerValue: Float,
    upperValue: Float,
    animatedProgress: Animatable<Float, AnimationVector1D>,
    xAxisSize: Int,
    spacingX:Dp,
    spacingY:Dp,
) {
    val spaceBetweenXes = (size.width.toDp() - spacingX) / xAxisSize
    val strokePathOfQuadraticLine = drawLineAsQuadratic(
        line = line,
        lowerValue = lowerValue,
        upperValue = upperValue,
        spaceBetweenXes = spaceBetweenXes,
        animatedProgress = animatedProgress,
        spacingX=spacingX,
        spacingY=spacingY,
    )

    if (line.lineShadow == LineShadow.SHADOW) {
        val fillPath = strokePathOfQuadraticLine.apply {
            lineTo((size.width.toDp() - spaceBetweenXes).toPx(), (size.height.toDp() - spacingY).toPx())
            lineTo(spacingX.toPx(), (size.height.toDp() - spacingY).toPx())
            close()
        }
        clipRect(right = size.width * animatedProgress.value) {
            drawPath(
                path = fillPath, brush = Brush.verticalGradient(
                    colors = listOf(
                        line.lineColor.copy(alpha = .3f), Color.Transparent
                    ), endY = (size.height.toDp() - spacingY).toPx()
                )
            )
        }
    }
}


private fun DrawScope.drawLineAsQuadratic(
    line: LineParameters,
    lowerValue: Float,
    upperValue: Float,
    spaceBetweenXes: Dp,
    animatedProgress: Animatable<Float, AnimationVector1D>,
    spacingX:Dp,
    spacingY: Dp,
) = Path().apply {
    var medX: Float
    var medY: Float
    val height = size.height.toDp()
    drawPathLineWrapper(
        lineParameter = line,
        strokePath = this,
        animatedProgress = animatedProgress,
    ) { lineParameter, index->

        val info = lineParameter.data[index]
        val nextInfo = lineParameter.data.getOrNull(index + 1) ?: lineParameter.data.last()
        val firstRatio = (info - lowerValue) / (upperValue - lowerValue)
        val secondRatio = (nextInfo - lowerValue) / (upperValue - lowerValue)

        val xFirstPoint = spacingX/2 + index * spaceBetweenXes
        val xSecondPoint = spacingX/2 + (index + 1) * spaceBetweenXes

        val yFirstPoint = (height - spacingY - (firstRatio * (height-spacingY)))
        val ySecondPoint = (height - spacingY - (secondRatio * (height-spacingY)))

        if (index == 0) {
            moveTo(xFirstPoint.toPx(), yFirstPoint.toPx())
        } else {
            medX = ((xFirstPoint + xSecondPoint) / 2f).toPx()
            medY = ((yFirstPoint + ySecondPoint) / 2f).toPx()
            quadraticBezierTo(xFirstPoint.toPx(), yFirstPoint.toPx(), medX, medY)
        }
    }

}