package com.aay.compose.donutChart

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.donutChart.component.PieChartDescriptionComposable
import com.aay.compose.donutChart.component.draPieCircle
import com.aay.compose.donutChart.component.drawCenterText
import com.aay.compose.donutChart.component.drawPedigreeChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.donutChart.model.ChartTypes
import com.aay.compose.utils.checkIfDataIsNegative
import kotlin.math.min

@OptIn(ExperimentalTextApi::class)
@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    pieChartData: List<PieChartData>,
    centerTitle: String = "",
    centerTitleStyle: TextStyle = TextStyle.Default,
    animation: AnimationSpec<Float> = TweenSpec(durationMillis = 3000),
    descriptionStyle: TextStyle = TextStyle.Default,
    textRatioStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
    outerCircularColor: Color = Color.Gray,
    innerCircularColor: Color = Color.Gray,
    ratioLineColor: Color = Color.Gray,
) {
    var totalSum = 0.0f
    val pieValueWithRatio = mutableListOf<Float>()
    pieChartData.forEach {
        totalSum += it.data.toFloat()
    }
    pieChartData.forEachIndexed { index, part ->
        pieValueWithRatio.add(index, 360 * part.data.toFloat() / totalSum)
    }


    val textMeasure = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult = textMeasure.measure(
        text = AnnotatedString(centerTitle.take(10))
    )
    val textSize = textLayoutResult.size

    checkIfDataIsNegative(data = pieChartData.map { it.data })
    val transitionProgress = remember(pieValueWithRatio) { Animatable(initialValue = 0F) }

    LaunchedEffect(pieChartData) {
        transitionProgress.animateTo(1F, animationSpec = animation)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier.fillMaxSize().weight(1.5f)
                .drawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val minValue = min(canvasWidth, canvasHeight)
                        .coerceAtMost(canvasHeight / 2)
                        .coerceAtMost(canvasWidth / 2)
                    val arcWidth = (size.minDimension.dp.toPx() * 0.13f).coerceAtMost(minValue / 4)


                    drawCenterText(
                        textMeasure = textMeasure,
                        centerTitle = centerTitle,
                        centerTitleStyle = centerTitleStyle,
                        canvasHeight = canvasHeight,
                        canvasWidth = canvasWidth,
                        textSize = textSize
                    )

                    drawPedigreeChart(
                        pieValueWithRatio = pieValueWithRatio,
                        pieChartData = pieChartData,
                        totalSum = totalSum,
                        transitionProgress = transitionProgress,
                        textMeasure = textMeasure,
                        textRatioStyle = textRatioStyle,
                        ratioLineColor = ratioLineColor,
                        arcWidth = arcWidth,
                        minValue = minValue,
                        pieChart = ChartTypes.DONUT_CHART
                    )
                    //draw outer circle
                    draPieCircle(
                        circleColor = outerCircularColor,
                        radiusRatioCircle = (minValue / 2) + (arcWidth / 1.5f)
                    )
                    //draw inner circle
                    draPieCircle(
                        circleColor = innerCircularColor,
                        radiusRatioCircle = (minValue / 2) - (arcWidth / 1.5f)
                    )


                }
        )

        PieChartDescriptionComposable(
            pieChartData = pieChartData,
            descriptionStyle = descriptionStyle,
            modifier = Modifier.fillMaxWidth().weight(0.5f)
        )

    }

}