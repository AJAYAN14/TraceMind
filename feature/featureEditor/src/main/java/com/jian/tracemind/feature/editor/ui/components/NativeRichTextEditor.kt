package com.jian.tracemind.feature.editor.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.StyleSpan
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
    textColor: Color = Color.Black,
    hint: String = ""
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            object : EditText(ctx) {
                var lastSelStart = -1
                override fun onSelectionChanged(selStart: Int, selEnd: Int) {
                    super.onSelectionChanged(selStart, selEnd)
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
                        if (isImageLineTap) {
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
                                        val rect = android.graphics.Rect(
                                            (startX + totalPaddingLeft - scrollX).toInt(),
                                            l.getLineTop(line) + totalPaddingTop - scrollY,
                                            (endX + totalPaddingLeft - scrollX).toInt(),
                                            l.getLineBottom(line) + totalPaddingTop - scrollY
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
                })
                
                // Ensure touch events are not intercepted by Compose parents when scrolling
                setOnTouchListener { v, event ->
                    if (event.action == android.view.MotionEvent.ACTION_DOWN) {
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
                    when (event.action and android.view.MotionEvent.ACTION_MASK) {
                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            v.parent?.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    
                    if (isImageLineTap) {
                        return@setOnTouchListener true
                    }
                    
                    if (event.action == android.view.MotionEvent.ACTION_UP && consumed) {
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
                                
                                val rect = android.graphics.Rect(
                                    (startX + totalPaddingLeft - scrollX).toInt(),
                                    l.getLineTop(line) + totalPaddingTop - scrollY,
                                    (endX + totalPaddingLeft - scrollX).toInt(),
                                    l.getLineBottom(line) + totalPaddingTop - scrollY
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
