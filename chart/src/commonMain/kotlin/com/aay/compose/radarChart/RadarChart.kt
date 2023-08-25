package com.aay.compose.radarChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon

@OptIn(ExperimentalTextApi::class)
@Composable
fun RadarChart(
    radarLabels: List<String>,
    labelsStyle: TextStyle,
    netLinesStyle: NetLinesStyle,
    scalarSteps: Int,
    scalarValue: Double,
    scalarValuesStyle: TextStyle,
    polygon: List<Polygon>
) {


    validatePolygons(radarLabels, scalarValue, polygon, scalarSteps)

    val numLines = radarLabels.size
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = (size.minDimension / 2) - 50.toDp().toPx()
        val radarChartConfig = getRadarConfig(radius, size, numLines, scalarSteps)
        drawRadarNet(netLinesStyle, radarChartConfig)

        polygon.forEach {
            drawPolygonShape(this, it, radius, scalarValue, Offset(size.width / 2, size.height / 2))
        }

        drawAxisData(
            labelsStyle,
            scalarValuesStyle,
            textMeasurer,
            radarChartConfig,
            radarLabels,
            scalarValue,
            scalarSteps,
            polygon[0].unit
        )
    }
}

private fun validatePolygons(
    radarLabels: List<String>,
    scalarValue: Double,
    polygons: List<Polygon>,
    scalarSteps: Int
) {

    if (scalarSteps < 0) {
        throw Exception("Scalar steps must be greater than 0")
    }

    if (scalarValue < 0.0) {
        throw Exception("Scalar value must be greater than 0")
    }

    if (radarLabels.size < 3) {
        throw Exception("Number of radar labels must be greater than 2")
    }

    for (polygon in polygons) {
        if (polygon.values.size != radarLabels.size) {
            throw Exception("Number of polygon values must equal to number of radar labels")
        }
        polygon.values.forEach { value ->
            if (value > scalarValue || value < 0.0) {
                throw Exception("Polygon values must be between 0 and scalar value ($scalarValue)")
            }
        }
    }
}

private fun DrawScope.drawPolygonShape(
    drawScope: DrawScope,
    polygon: Polygon,
    radius: Float,
    scalarValue: Double,
    center: Offset
) {
    val polygonEndPoints =
        getPolygonShapeEndPoints(polygon.values, radius, scalarValue, center)
    val path = Path().apply {
        drawPolygon(polygonEndPoints)
    }

    drawScope.apply {
        drawPath(
            path,
            color = polygon.style.borderColor,
            style = Stroke(polygon.style.borderStrokeWidth),
            alpha = polygon.style.borderColorAlpha
        )
        drawPath(path, color = polygon.style.fillColor, style = Fill, alpha = polygon.style.fillColorAlpha)
    }
}



