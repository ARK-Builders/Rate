package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class AmountInputVisualTransformationTest {
    @Test
    fun formatsIntegerPartWithGroupingSeparators() {
        val cases =
            mapOf(
                "" to "",
                "." to ".",
                "1000" to "1,000",
                "1000000" to "1,000,000",
                "1000.50" to "1,000.50",
                "1000." to "1,000.",
            )

        cases.forEach { (input, expected) ->
            assertEquals(expected, transform(input).text.text)
        }
    }

    @Test
    fun mapsOriginalOffsetsToTransformedOffsets() {
        val mapping = transform("1000.50").offsetMapping
        val expectedOffsets = listOf(0, 1, 3, 4, 5, 6, 7, 8)

        expectedOffsets.forEachIndexed { originalOffset, transformedOffset ->
            assertEquals(transformedOffset, mapping.originalToTransformed(originalOffset))
        }
    }

    @Test
    fun mapsTransformedOffsetsToOriginalOffsets() {
        val mapping = transform("1000.50").offsetMapping
        val expectedOffsets = listOf(0, 1, 1, 2, 3, 4, 5, 6, 7)

        expectedOffsets.forEachIndexed { transformedOffset, originalOffset ->
            assertEquals(originalOffset, mapping.transformedToOriginal(transformedOffset))
        }
    }

    private fun transform(input: String) =
        AmountInputVisualTransformation.filter(AnnotatedString(input))
}
