package com.jian.tracemind.feature.editor.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.EditText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NativeRichTextEditorController {
    var editText: EditText? = null
        internal set

    private var initialHtml: String? = null
    private var initialCoroutineScope: CoroutineScope? = null
    var onContentChanged: ((String) -> Unit)? = null
    var onImageClick: ((String, android.graphics.Rect) -> Unit)? = null
    var onImageBoundsChanged: ((android.graphics.Rect) -> Unit)? = null
    var selectedImageUrl: String? = null

    internal fun init(html: String?, scope: CoroutineScope) {
        initialHtml = html
        initialCoroutineScope = scope
        applyInitialHtml()
    }

    private fun applyInitialHtml() {
        val view = editText ?: return
        val html = initialHtml ?: return
        val scope = initialCoroutineScope ?: return
        
        if (html.isEmpty()) {
            view.setText("")
            return
        }

        val imageGetter = Html.ImageGetter { source ->
            val drawable = CoilImageDrawable()
            scope.launch {
                val context = view.context
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(source)
                    .build()
                val result = imageLoader.execute(request)
                val d = result.drawable
                if (d != null) {
                    val bitmap = (d as? BitmapDrawable)?.bitmap
                    if (bitmap != null) {
                        val maxWidth = context.resources.displayMetrics.widthPixels - 64 // padding
                        val maxHeight = (context.resources.displayMetrics.heightPixels * 0.4).toInt() // 限制最大高度为屏幕高度的40%
                        
                        var scale = maxWidth.toFloat() / bitmap.width
                        if (bitmap.height * scale > maxHeight) {
                            scale = maxHeight.toFloat() / bitmap.height
                        }
                        if (scale > 1f) scale = 1f // 防止将小图放大
                        
                        val finalWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
                        val finalHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
                        
                        val scaledBitmap = if (finalWidth == bitmap.width && finalHeight == bitmap.height) {
                            bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
                        } else {
                            Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
                        }
                        scaledBitmap.density = context.resources.displayMetrics.densityDpi
                        
                        val roundedDrawable = androidx.core.graphics.drawable.RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
                        roundedDrawable.cornerRadius = 12f * context.resources.displayMetrics.density
                        roundedDrawable.setAntiAlias(true)
                        roundedDrawable.setBounds(0, 0, finalWidth, finalHeight)
                        drawable.drawable = roundedDrawable
                        drawable.setBounds(0, 0, finalWidth, finalHeight)
                        
                        // Force redraw
                        withContext(Dispatchers.Main) {
                            view.text = view.text // trigger re-layout
                            view.invalidate()
                        }
                    }
                }
            }
            drawable
        }

        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null)
        view.setText(spanned)
    }

    fun getHtml(): String {
        val view = editText ?: return ""
        return HtmlCompat.toHtml(view.text, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
    }

    fun toggleStyle(style: Int) {
        val view = editText ?: return
        val start = view.selectionStart
        val end = view.selectionEnd
        if (start < 0 || end < 0 || start == end) return

        val spannable = view.text
        val spans = spannable.getSpans(start, end, StyleSpan::class.java)
        var hasStyle = false
        for (span in spans) {
            if (span.style == style) {
                spannable.removeSpan(span)
                hasStyle = true
            }
        }
        if (!hasStyle) {
            spannable.setSpan(StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        onContentChanged?.invoke(getHtml())
    }

    fun undo() {
        editText?.onTextContextMenuItem(android.R.id.undo)
    }

    fun redo() {
        editText?.onTextContextMenuItem(android.R.id.redo)
    }

    fun insertImage(source: String) {
        val view = editText ?: return
        val scope = initialCoroutineScope ?: return
        
        val start = view.selectionStart.takeIf { it >= 0 } ?: view.text.length
        
        // Insert placeholder
        view.text.insert(start, "\n￼\n")
        val imageStart = start + 1
        val imageEnd = imageStart + 1

        scope.launch {
            val context = view.context
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(source)
                .build()
            val result = imageLoader.execute(request)
            val d = result.drawable
            if (d != null) {
                val bitmap = (d as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val maxWidth = context.resources.displayMetrics.widthPixels - 64 // padding
                    val maxHeight = (context.resources.displayMetrics.heightPixels * 0.4).toInt() // 限制最大高度为屏幕高度的40%
                    
                    var scale = maxWidth.toFloat() / bitmap.width
                    if (bitmap.height * scale > maxHeight) {
                        scale = maxHeight.toFloat() / bitmap.height
                    }
                    if (scale > 1f) scale = 1f // 防止将小图放大
                    
                    val finalWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
                    val finalHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
                    
                    val scaledBitmap = if (finalWidth == bitmap.width && finalHeight == bitmap.height) {
                        bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
                    } else {
                        Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
                    }
                    scaledBitmap.density = context.resources.displayMetrics.densityDpi
                    
                    val roundedDrawable = androidx.core.graphics.drawable.RoundedBitmapDrawableFactory.create(context.resources, scaledBitmap)
                    roundedDrawable.cornerRadius = 12f * context.resources.displayMetrics.density
                    roundedDrawable.setAntiAlias(true)
                    roundedDrawable.setBounds(0, 0, finalWidth, finalHeight)
                    
                    val imageSpan = ImageSpan(roundedDrawable, source)
                    
                    withContext(Dispatchers.Main) {
                        view.text.setSpan(imageSpan, imageStart, imageEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        onContentChanged?.invoke(getHtml())
                    }
                }
            }
        }
    }
    fun deleteImage(url: String) {
        val view = editText ?: return
        val t = view.text as? Editable ?: return
        val spans = t.getSpans(0, t.length, android.text.style.ImageSpan::class.java)
        val span = spans.find { it.source == url }
        if (span != null) {
            var start = t.getSpanStart(span)
            var end = t.getSpanEnd(span)
            
            if (end < t.length && t[end] == '\n') {
                end += 1
            } else if (start > 0 && t[start - 1] == '\n') {
                start -= 1
            }
            
            t.delete(start, end)
            onContentChanged?.invoke(getHtml())
        }
    }
}

class CoilImageDrawable : android.graphics.drawable.Drawable() {
    var drawable: Drawable? = null

    override fun draw(canvas: android.graphics.Canvas) {
        drawable?.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        drawable?.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        drawable?.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
}

@Composable
fun rememberNativeRichTextEditorController(): NativeRichTextEditorController {
    return remember { NativeRichTextEditorController() }
}

@Composable
fun NativeRichTextEditor(
    controller: NativeRichTextEditorController,
    initialHtml: String,
    coroutineScope: CoroutineScope,
    onContentChanged: (String) -> Unit,
    onImageBoundsChanged: ((android.graphics.Rect) -> Unit)? = null,
    onImageClick: ((String, android.graphics.Rect) -> Unit)? = null,
    modifier: Modifier = Modifier,
    textColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    indicatorColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    hint: String = ""
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            object : EditText(ctx) {
                var lastSelStart = -1

                // ── Drag state ──
                var isDraggingImage = false
                var dragImageSpan: ImageSpan? = null
                var dragImageSource: String? = null
                var dragImageDrawable: Drawable? = null
                var dragOriginalStart: Int = -1
                var dragOriginalEnd: Int = -1
                var dragTouchX: Float = 0f
                var dragTouchY: Float = 0f
                var dragDropOffset: Int = -1
                var isSuppressingTextWatcher = false

                // ── Edge auto-scroll ──
                val edgeScrollIntervalMs = 16L // ~60fps
                val edgeScrollMaxSpeed = 20 // px per frame
                val maxOverflowPx = (150 * resources.displayMetrics.density).toInt() // speed ramps over this distance
                var autoScrollRunnable: Runnable? = null

                fun startEdgeScroll(scrollSpeed: Int) {
                    stopEdgeScroll()
                    val runnable = object : Runnable {
                        override fun run() {
                            if (!isDraggingImage) return
                            scrollBy(0, scrollSpeed)
                            // Recalculate drop offset after scroll
                            val dx = dragTouchX.toInt() - totalPaddingLeft + scrollX
                            val dy = dragTouchY.toInt() - totalPaddingTop + scrollY
                            val l = layout
                            if (l != null) {
                                val line = l.getLineForVertical(dy)
                                dragDropOffset = l.getOffsetForHorizontal(line, dx.toFloat())
                            }
                            invalidate()
                            postDelayed(this, edgeScrollIntervalMs)
                        }
                    }
                    autoScrollRunnable = runnable
                    postDelayed(runnable, edgeScrollIntervalMs)
                }

                fun stopEdgeScroll() {
                    autoScrollRunnable?.let { removeCallbacks(it) }
                    autoScrollRunnable = null
                }

                fun evaluateEdgeScroll(touchY: Float) {
                    val viewHeight = height
                    if (viewHeight <= 0) return

                    when {
                        // Finger dragged ABOVE the view → scroll up
                        touchY < 0 -> {
                            val overflow = (-touchY).coerceAtMost(maxOverflowPx.toFloat())
                            val ratio = overflow / maxOverflowPx
                            val speed = -(ratio * edgeScrollMaxSpeed).toInt().coerceAtLeast(1)
                            if (autoScrollRunnable == null) startEdgeScroll(speed)
                        }
                        // Finger dragged BELOW the view → scroll down
                        touchY > viewHeight -> {
                            val overflow = (touchY - viewHeight).coerceAtMost(maxOverflowPx.toFloat())
                            val ratio = overflow / maxOverflowPx
                            val speed = (ratio * edgeScrollMaxSpeed).toInt().coerceAtLeast(1)
                            if (autoScrollRunnable == null) startEdgeScroll(speed)
                        }
                        // Finger within view bounds → no auto-scroll
                        else -> stopEdgeScroll()
                    }
                }

                fun resetDragState() {
                    stopEdgeScroll()
                    isDraggingImage = false
                    dragImageSpan = null
                    dragImageSource = null
                    dragImageDrawable = null
                    dragOriginalStart = -1
                    dragOriginalEnd = -1
                    dragTouchX = 0f
                    dragTouchY = 0f
                    dragDropOffset = -1
                    invalidate()
                }

                private val dropIndicatorPaint = Paint().apply {
                    color = indicatorColor.toArgb() // Use Compose theme color
                    strokeWidth = 6f
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                    isAntiAlias = true
                }

                /**
                 * Snap a raw character offset to the nearest line boundary.
                 * Since images are block-level (\n\uFFFC\n), we snap to line start/end
                 * to avoid inserting an image in the middle of a text line.
                 */
                fun snapToLineBoundary(rawOffset: Int): Int {
                    val l = layout ?: return rawOffset
                    val t = text ?: return rawOffset
                    val line = l.getLineForOffset(rawOffset)
                    val lineStart = l.getLineStart(line)
                    val lineEnd = l.getLineEnd(line)
                    // If the line contains an ImageSpan, snap to lineEnd (after it)
                    val lineSpans = t.getSpans(lineStart, lineEnd, ImageSpan::class.java)
                    if (lineSpans.isNotEmpty()) {
                        val spanEnd = t.getSpanEnd(lineSpans.first())
                        // Snap after the image's trailing \n if possible
                        return if (spanEnd < t.length && t[spanEnd] == '\n') spanEnd + 1 else spanEnd
                    }
                    // For text lines, snap to whichever boundary is closer
                    return if (rawOffset - lineStart <= lineEnd - rawOffset) lineStart else lineEnd
                }

                fun performDrop(origStart: Int, origEnd: Int, rawDropOffset: Int) {
                    val editable = text as? Editable ?: return
                    val imageSource = dragImageSource ?: return
                    val imageDrawable = dragImageDrawable ?: return

                    val snappedDrop = snapToLineBoundary(rawDropOffset)

                    // If dropping back onto original range, no-op
                    if (snappedDrop in origStart..origEnd) return

                    isSuppressingTextWatcher = true
                    beginBatchEdit()
                    try {
                        val draggedLength = origEnd - origStart

                        // Adjust drop offset if it's after the original position
                        val adjustedDrop = if (snappedDrop > origEnd) {
                            snappedDrop - draggedLength
                        } else {
                            snappedDrop
                        }

                        // Delete original image block
                        editable.delete(origStart, origEnd)

                        // Build insert text: ensure \n before and after the image
                        val safeDrop = adjustedDrop.coerceIn(0, editable.length)
                        val needLeadingNewline = safeDrop > 0 && editable[safeDrop - 1] != '\n'
                        val needTrailingNewline = safeDrop < editable.length && editable[safeDrop] != '\n'

                        val insertBuilder = StringBuilder()
                        if (needLeadingNewline) insertBuilder.append('\n')
                        insertBuilder.append('\uFFFC')
                        if (needTrailingNewline) insertBuilder.append('\n')

                        val insertText = insertBuilder.toString()
                        editable.insert(safeDrop, insertText)

                        // Find the \uFFFC position within the inserted text
                        val uffcOffset = safeDrop + (if (needLeadingNewline) 1 else 0)
                        val newSpanStart = uffcOffset
                        val newSpanEnd = uffcOffset + 1

                        // Attach new ImageSpan
                        val newImageSpan = ImageSpan(imageDrawable, imageSource)
                        editable.setSpan(
                            newImageSpan, newSpanStart, newSpanEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } finally {
                        endBatchEdit()
                        isSuppressingTextWatcher = false
                    }

                    // Sync HTML state
                    controller.onContentChanged?.invoke(controller.getHtml())

                    // Clear focus after drop (consistent with image tap behavior)
                    val imm = context.getSystemService(
                        android.content.Context.INPUT_METHOD_SERVICE
                    ) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                    clearFocus()
                }

                override fun dispatchDraw(canvas: Canvas) {
                    super.dispatchDraw(canvas)
                    if (!isDraggingImage) return

                    val l = layout ?: return
                    val drawable = dragImageDrawable ?: return


                    // 2. Draw drop indicator line at target position
                    if (dragDropOffset >= 0 && dragDropOffset <= text.length) {
                        val snapped = snapToLineBoundary(dragDropOffset)
                        // Don't draw indicator if dropping back to original position
                        if (snapped !in dragOriginalStart..dragOriginalEnd) {
                            val line = l.getLineForOffset(snapped.coerceIn(0, text.length))
                            val lineY = if (snapped == l.getLineStart(line)) {
                                l.getLineTop(line)
                            } else {
                                l.getLineBottom(line)
                            }
                            val drawY = (lineY + totalPaddingTop - scrollY).toFloat()
                            val leftX = totalPaddingLeft.toFloat()
                            val rightX = (width - totalPaddingRight).toFloat()

                            // Draw indicator line with rounded end circles
                            canvas.drawLine(leftX, drawY, rightX, drawY, dropIndicatorPaint)
                            val circlePaint = Paint(dropIndicatorPaint).apply {
                                style = Paint.Style.FILL
                            }
                            canvas.drawCircle(leftX, drawY, 5f, circlePaint)
                            canvas.drawCircle(rightX, drawY, 5f, circlePaint)
                        }
                    }

                    // 3. Draw drag shadow (scaled-down image following finger)
                    val dw = drawable.bounds.width()
                    val dh = drawable.bounds.height()
                    val shadowScale = 0.7f
                    val scaledW = dw * shadowScale
                    val scaledH = dh * shadowScale

                    canvas.save()
                    canvas.translate(
                        dragTouchX - scaledW / 2,
                        dragTouchY - scaledH - 20f // slightly above finger
                    )
                    canvas.scale(shadowScale, shadowScale)
                    drawable.alpha = 180
                    drawable.draw(canvas)
                    drawable.alpha = 255
                    canvas.restore()
                }

                override fun onSelectionChanged(selStart: Int, selEnd: Int) {
                    super.onSelectionChanged(selStart, selEnd)
                    // Short-circuit during drag to prevent cursor-skip logic from interfering
                    if (isDraggingImage) return
                    val t = text ?: return
                    val l = layout
                    if (l != null && selStart == selEnd) {
                        val line = l.getLineForOffset(selStart)
                        val lineStart = l.getLineStart(line)
                        val lineEnd = l.getLineEnd(line)
                        
                        val spans = t.getSpans(lineStart, lineEnd, android.text.style.ImageSpan::class.java)
                        if (spans.isNotEmpty()) {
                            val span = spans.first()
                            val start = t.getSpanStart(span)
                            val end = t.getSpanEnd(span)
                            
                            val isIsolated = (start == 0 || t[start - 1] == '\n') && (end == t.length || t[end] == '\n')
                            
                            if (isIsolated) {
                                val isArrowKey = lastSelStart != -1 && (lastSelStart - selStart == 1 || lastSelStart - selStart == -1)
                                val pushDown = if (isArrowKey) {
                                    selStart > lastSelStart
                                } else {
                                    selStart >= end
                                }
                                
                                val target = if (pushDown) end + 1 else start - 1
                                val safeOffset = target.coerceIn(0, t.length)
                                
                                if (safeOffset != selStart) {
                                    post { setSelection(safeOffset) }
                                    return
                                }
                            }
                        }
                    }
                    lastSelStart = selStart
                }
            }.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setTextColor(textColor.toArgb())
                setHintTextColor(textColor.copy(alpha = 0.5f).toArgb())
                this.hint = hint
                gravity = android.view.Gravity.TOP or android.view.Gravity.START
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                
                isVerticalScrollBarEnabled = true
                isScrollContainer = true
                isSingleLine = false
                
                var isImageLineTap = false
                
                val gestureDetector = android.view.GestureDetector(ctx, object : android.view.GestureDetector.SimpleOnGestureListener() {
                    override fun onScroll(
                        e1: android.view.MotionEvent?,
                        e2: android.view.MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean {
                        if (isImageLineTap && !isDraggingImage) {
                            scrollBy(0, distanceY.toInt())
                            return true
                        }
                        return false
                    }
                    
                    override fun onSingleTapUp(e: android.view.MotionEvent): Boolean {
                        val x = e.x.toInt() - totalPaddingLeft + scrollX
                        val y = e.y.toInt() - totalPaddingTop + scrollY
                        val l = layout ?: return false
                        val line = l.getLineForVertical(y)
                        val off = l.getOffsetForHorizontal(line, x.toFloat())
                        
                        val spans = text.getSpans(off, off, android.text.style.ImageSpan::class.java)
                        if (spans.isNotEmpty()) {
                            val span = spans.first()
                            val spanStart = text.getSpanStart(span)
                            val spanEnd = text.getSpanEnd(span)
                            if (off in spanStart..spanEnd) {
                                val startX = l.getPrimaryHorizontal(spanStart)
                                val endX = startX + span.drawable.bounds.width()
                                if (x.toFloat() in startX..endX) {
                                    span.source?.let { src ->
                                        val lineBottom = l.getLineBottom(line)
                                        val imageTop = lineBottom - span.drawable.bounds.height()
                                        val rect = android.graphics.Rect(
                                            (startX + totalPaddingLeft - scrollX).toInt(),
                                            imageTop + totalPaddingTop - scrollY,
                                            (endX + totalPaddingLeft - scrollX).toInt(),
                                            lineBottom + totalPaddingTop - scrollY
                                        )
                                        controller.onImageClick?.invoke(src, rect)
                                        val imm = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                                        imm.hideSoftInputFromWindow(windowToken, 0)
                                        clearFocus()
                                        return true
                                    }
                                }
                            }
                        }
                        return false
                    }

                    override fun onLongPress(e: android.view.MotionEvent) {
                        // Detect long-press on an isolated image to start drag
                        val x = e.x.toInt() - totalPaddingLeft + scrollX
                        val y = e.y.toInt() - totalPaddingTop + scrollY
                        val l = layout ?: return
                        val line = l.getLineForVertical(y)
                        val off = l.getOffsetForHorizontal(line, x.toFloat())

                        val spans = text.getSpans(off, off, ImageSpan::class.java)
                        if (spans.isEmpty()) return
                        val span = spans.first()
                        val spanStart = text.getSpanStart(span)
                        val spanEnd = text.getSpanEnd(span)

                        // Pixel-level hit test
                        val startX = l.getPrimaryHorizontal(spanStart)
                        val endX = startX + span.drawable.bounds.width()
                        if (x.toFloat() !in startX..endX) return

                        // Only drag isolated images (those on their own line)
                        val t = text
                        val isIsolated = (spanStart == 0 || t[spanStart - 1] == '\n') &&
                                (spanEnd == t.length || t[spanEnd] == '\n')
                        if (!isIsolated) return

                        // Calculate full range including ONE surrounding newline (not both!)
                        // This matches deleteImage() logic — only consume one side to avoid
                        // merging adjacent text lines (e.g., "1\n￼\n2" → deleting both \n
                        // would produce "12" instead of "1\n2")
                        var fullStart = spanStart
                        var fullEnd = spanEnd
                        if (fullEnd < t.length && t[fullEnd] == '\n') {
                            fullEnd++
                        } else if (fullStart > 0 && t[fullStart - 1] == '\n') {
                            fullStart--
                        }

                        // Enter drag mode
                        isDraggingImage = true
                        dragImageSpan = span
                        dragImageSource = span.source
                        dragImageDrawable = span.drawable
                        dragOriginalStart = fullStart
                        dragOriginalEnd = fullEnd
                        dragTouchX = e.x
                        dragTouchY = e.y
                        dragDropOffset = -1
                        isImageLineTap = false // Override scroll hijack

                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        invalidate()
                    }
                })
                
                // Ensure touch events are not intercepted by Compose parents when scrolling
                setOnTouchListener { v, event ->
                    // ── Drag handling (highest priority) ──
                    if (isDraggingImage) {
                        when (event.action and MotionEvent.ACTION_MASK) {
                            MotionEvent.ACTION_MOVE -> {
                                dragTouchX = event.x
                                dragTouchY = event.y
                                // Calculate real-time drop offset
                                val dx = event.x.toInt() - totalPaddingLeft + scrollX
                                val dy = event.y.toInt() - totalPaddingTop + scrollY
                                val l = layout
                                if (l != null) {
                                    val line = l.getLineForVertical(dy)
                                    dragDropOffset = l.getOffsetForHorizontal(line, dx.toFloat())
                                }
                                // Evaluate edge auto-scrolling
                                evaluateEdgeScroll(event.y)
                                invalidate()
                                return@setOnTouchListener true
                            }
                            MotionEvent.ACTION_UP -> {
                                if (dragDropOffset >= 0) {
                                    performDrop(dragOriginalStart, dragOriginalEnd, dragDropOffset)
                                }
                                resetDragState()
                                return@setOnTouchListener true
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                resetDragState()
                                return@setOnTouchListener true
                            }
                        }
                        return@setOnTouchListener true
                    }

                    // ── Normal touch handling ──
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val l = layout
                        if (l != null) {
                            val y = event.y.toInt() - totalPaddingTop + scrollY
                            val line = l.getLineForVertical(y)
                            val lineStart = l.getLineStart(line)
                            val lineEnd = l.getLineEnd(line)
                            val spans = text.getSpans(lineStart, lineEnd, android.text.style.ImageSpan::class.java)
                            if (spans.isNotEmpty()) {
                                val span = spans.first()
                                val start = text.getSpanStart(span)
                                val end = text.getSpanEnd(span)
                                val isIsolated = (start == 0 || text[start - 1] == '\n') && (end == text.length || text[end] == '\n')
                                isImageLineTap = isIsolated
                            } else {
                                isImageLineTap = false
                            }
                        }
                    }
                    
                    val consumed = gestureDetector.onTouchEvent(event)
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            v.parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    
                    if (isImageLineTap) {
                        return@setOnTouchListener true
                    }
                    
                    if (event.action == MotionEvent.ACTION_UP && consumed) {
                        return@setOnTouchListener true
                    }
                    false
                }
                
                controller.editText = this
                controller.onContentChanged = onContentChanged
                controller.onImageClick = onImageClick
                controller.onImageBoundsChanged = onImageBoundsChanged
                controller.init(initialHtml, coroutineScope)
                
                val updateSelectedImageBounds = {
                    controller.selectedImageUrl?.let { url ->
                        val spans = text.getSpans(0, text.length, android.text.style.ImageSpan::class.java)
                        val span = spans.find { it.source == url }
                        if (span != null) {
                            val l = layout
                            if (l != null) {
                                val spanStart = text.getSpanStart(span)
                                val line = l.getLineForOffset(spanStart)
                                val startX = l.getPrimaryHorizontal(spanStart)
                                val endX = startX + span.drawable.bounds.width()
                                val lineBottom = l.getLineBottom(line)
                                val imageTop = lineBottom - span.drawable.bounds.height()
                                val rect = android.graphics.Rect(
                                    (startX + totalPaddingLeft - scrollX).toInt(),
                                    imageTop + totalPaddingTop - scrollY,
                                    (endX + totalPaddingLeft - scrollX).toInt(),
                                    lineBottom + totalPaddingTop - scrollY
                                )
                                controller.onImageBoundsChanged?.invoke(rect)
                            }
                        }
                    }
                }

                viewTreeObserver.addOnGlobalLayoutListener {
                    updateSelectedImageBounds()
                }
                
                viewTreeObserver.addOnScrollChangedListener {
                    updateSelectedImageBounds()
                }
                
                addTextChangedListener(object: android.text.TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        // Skip HTML sync during drag move operation to avoid intermediate states
                        if (isSuppressingTextWatcher) return
                        onContentChanged(controller.getHtml())
                    }
                })
            }
        },
        update = { view ->
            view.setTextColor(textColor.toArgb())
            view.setHintTextColor(textColor.copy(alpha = 0.5f).toArgb())
        }
    )
    
    DisposableEffect(controller) {
        onDispose {
            controller.editText = null
        }
    }
}
