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
                        val screenWidth = context.resources.displayMetrics.widthPixels - 64 // padding
                        val scale = screenWidth.toFloat() / bitmap.width
                        val height = (bitmap.height * scale).toInt()
                        
                        val scaledDrawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, screenWidth, height, true))
                        scaledDrawable.setBounds(0, 0, screenWidth, height)
                        drawable.drawable = scaledDrawable
                        drawable.setBounds(0, 0, screenWidth, height)
                        
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
                    val screenWidth = context.resources.displayMetrics.widthPixels - 64 // padding
                    val scale = screenWidth.toFloat() / bitmap.width
                    val height = (bitmap.height * scale).toInt()
                    
                    val scaledDrawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, screenWidth, height, true))
                    scaledDrawable.setBounds(0, 0, screenWidth, height)
                    
                    val imageSpan = ImageSpan(scaledDrawable, source)
                    
                    withContext(Dispatchers.Main) {
                        view.text.setSpan(imageSpan, imageStart, imageEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
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
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    hint: String = ""
) {
    val context = LocalContext.current
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            EditText(ctx).apply {
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
                
                controller.editText = this
                controller.init(initialHtml, coroutineScope)
                
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
