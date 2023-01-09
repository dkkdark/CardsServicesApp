package com.kseniabl.tasksapp.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import com.kseniabl.tasksapp.R

class TagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paints

    private var textColor = Color.BLACK

    private val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = resources.getDimension(R.dimen.text_size)
        color = textColor
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // Sizes

    private val rowHeight = resources.getDimensionPixelSize(R.dimen.row_height)
    private val tagPadding = resources.getDimensionPixelSize(R.dimen.tag_padding)
    private val textPadding = resources.getDimensionPixelSize(R.dimen.tag_padding)
    private val cornerRadius = resources.getDimension(R.dimen.corner_radius)

    // Colors

    private var gradientStartColor = ContextCompat.getColor(context, R.color.purple)
    private var gradientEndColor = ContextCompat.getColor(context, R.color.blue)

    private var tagsRectList: ArrayList<Rect> = arrayListOf()
    private var textRectList: ArrayList<TextSize> = arrayListOf()
    private var tagsOnRowCount: MutableMap<Int, ArrayList<Int>> = mutableMapOf()

    var sizes: ArrayList<Float> = arrayListOf()

    var tags: ArrayList<TagsModel> = arrayListOf()
        set(value) {
            field = value
            measureTags(value)
            requestLayout()
            invalidate()
        }

    init {
        if (isInEditMode) {
            tags = arrayListOf(TagsModel("Tag123"), TagsModel("Tdfgdfg"), TagsModel("More123"), TagsModel("More123"), TagsModel("More123"), TagsModel("T"), TagsModel("MoreMore123"))
        }
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagView, 0, 0
        ).apply {
            try {
                gradientStartColor = getColor(R.styleable.TagView_firstGradientColor, Color.WHITE)
                gradientEndColor = getColor(R.styleable.TagView_secondGradientColor, Color.WHITE)
                textColor = getColor(R.styleable.TagView_textColor, Color.BLACK)
            } finally {
                recycle()
            }
        }
        textPaint.color = textColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        tagsOnRowCount.clear()
        val finWidth = MeasureSpec.getSize(widthMeasureSpec)

        // Count how many lines needed to put tags
        var rowsCount = 1
        var capacitySize = 0F
        sizes.forEach {
            if (capacitySize+it < finWidth) {
                capacitySize += it
            }
            else {
                capacitySize = it
                rowsCount += 1
            }

            val value = tagsOnRowCount[rowsCount] ?: arrayListOf()
            value.add(it.toInt())
            tagsOnRowCount[rowsCount] = value
        }

        val height = rowHeight * rowsCount
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        /*val finHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> height
            MeasureSpec.EXACTLY -> heightSpecSize
            MeasureSpec.AT_MOST -> heightSpecSize.coerceAtMost(heightSpecSize)
            else -> error("Unreachable")
        }*/

        setMeasuredDimension(finWidth, resolveSize(height, heightSpecSize))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateTagsBorderSizes()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tagPaint.shader = LinearGradient(
            0f,
            0f,
            w.toFloat(),
            0f,
            gradientStartColor,
            gradientEndColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /** Draw Tags **/
        tagsRectList.forEach { tagEl ->
            canvas.drawRoundRect(tagEl.toRectF(), cornerRadius, cornerRadius, tagPaint)
        }

        /** Draw Text in Tags Rect **/
        if (textRectList.size != tags.size)
            return

        textRectList.forEachIndexed { idx, el ->
            canvas.drawText(tags[idx].name, el.left.toFloat(), el.center, textPaint)
        }
    }

    private fun measureTags(tagsList: List<TagsModel>) {
        sizes = arrayListOf()
        tagsList.forEach {
            val textWidth = textPaint.measureText(it.name)
            sizes.add(tagPadding * 2 + textPadding * 2 + textWidth)
        }
    }

    // Sizes of tag
    private fun updateTagsBorderSizes() {
        textRectList = arrayListOf()
        tagsRectList = arrayListOf()
        for (key in tagsOnRowCount.keys) {
            val tagsCount = tagsOnRowCount.getValue(key)
            for (i in 0 until tagsCount.size) {
                val tagBorder = Rect()
                if (i == 0) {
                    tagBorder.set(
                        i + tagPadding, rowHeight * (key-1) + tagPadding,
                        tagsCount[i] - tagPadding / 2, rowHeight * key - tagPadding
                    )

                    // Sizes of text
                    val textLeft = i + tagPadding + textPadding
                    val textCenter = textPaint.getTextBaselineByCenter(((rowHeight * key) + (rowHeight * (key-1))) / 2)
                    textRectList.add(TextSize(textLeft, textCenter))
                }
                else {
                    // add size of previous tag
                    tagsCount[i] = tagsCount[i-1] + tagsCount[i]
                    tagBorder.set(
                        tagsCount[i - 1] + tagPadding, rowHeight * (key-1) + tagPadding,
                        tagsCount[i] - tagPadding / 2, rowHeight * key - tagPadding
                    )

                    // Sizes of text
                    val textLeft = tagsCount[i - 1] + tagPadding + textPadding
                    val textCenter = textPaint.getTextBaselineByCenter(((rowHeight * key) + (rowHeight * (key-1))) / 2)
                    textRectList.add(TextSize(textLeft, textCenter))
                }
                tagsRectList.add(tagBorder)
            }
        }
    }

    private fun Paint.getTextBaselineByCenter(center: Int) = center - (descent() + ascent()) / 2

    data class TextSize(
        val left: Int,
        val center: Float
    )
}