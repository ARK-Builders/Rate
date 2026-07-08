package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object AmountInputVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = formatAmountInput(text.text)
        return TransformedText(
            AnnotatedString(formatted.text),
            AmountInputOffsetMapping(
                originalToTransformed = formatted.originalToTransformed,
                transformedToOriginal = formatted.transformedToOriginal,
            ),
        )
    }
}

private data class FormattedAmountInput(
    val text: String,
    val originalToTransformed: IntArray,
    val transformedToOriginal: IntArray,
)

private fun formatAmountInput(input: String): FormattedAmountInput {
    val result = StringBuilder()
    val originalCharPositions = IntArray(input.length)
    val decimalSeparatorIndex = input.indexOf('.')
    val integerEnd =
        if (decimalSeparatorIndex == -1)
            input.length
        else
            decimalSeparatorIndex

    input.forEachIndexed { index, char ->
        if (shouldInsertGroupingSeparator(index, integerEnd)) {
            result.append(',')
        }
        originalCharPositions[index] = result.length
        result.append(char)
    }

    return FormattedAmountInput(
        text = result.toString(),
        originalToTransformed = buildOriginalToTransformed(originalCharPositions, input.length),
        transformedToOriginal = buildTransformedToOriginal(originalCharPositions, result.length),
    )
}

private fun shouldInsertGroupingSeparator(
    index: Int,
    integerEnd: Int,
): Boolean {
    return index in 1 until integerEnd && (integerEnd - index) % 3 == 0
}

private fun buildOriginalToTransformed(
    originalCharPositions: IntArray,
    originalLength: Int,
): IntArray {
    return IntArray(originalLength + 1) { offset ->
        if (offset == 0)
            0
        else
            originalCharPositions[offset - 1] + 1
    }
}

private fun buildTransformedToOriginal(
    originalCharPositions: IntArray,
    transformedLength: Int,
): IntArray {
    var originalOffset = 0
    return IntArray(transformedLength + 1) { transformedOffset ->
        while (
            originalOffset < originalCharPositions.size &&
            originalCharPositions[originalOffset] < transformedOffset
        ) {
            originalOffset++
        }
        originalOffset
    }
}

private class AmountInputOffsetMapping(
    private val originalToTransformed: IntArray,
    private val transformedToOriginal: IntArray,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return originalToTransformed[offset.coerceIn(originalToTransformed.indices)]
    }

    override fun transformedToOriginal(offset: Int): Int {
        return transformedToOriginal[offset.coerceIn(transformedToOriginal.indices)]
    }
}
