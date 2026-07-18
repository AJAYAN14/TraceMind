package com.jian.tracemind.feature.editor.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jian.tracemind.feature.editor.ui.components.EditorTopBar
import com.jian.tracemind.feature.editor.ui.components.FormatToolbar
import com.jian.tracemind.feature.editor.ui.theme.NoteColorPalette
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: EditorViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val localBackdrop = rememberLayerBackdrop()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()

    var showMenu by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var viewingImageUrl by remember { mutableStateOf<String?>(null) }

    val editorController = com.jian.tracemind.feature.editor.ui.components.rememberNativeRichTextEditorController()

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.onEvent(EditorEvent.InsertImage(it.toString()))
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { uri ->
                    viewModel.onEvent(EditorEvent.InsertImage(uri.toString()))
                }
            }
        }
    )

    val resolvedColorInt = viewModel.noteColor.value
    val noteBackgroundAnimatable = remember { Animatable(Color(resolvedColorInt)) }
    val backgroundColor = noteBackgroundAnimatable.value

    val contentColor = if (backgroundColor.luminance() < 0.5f) {
        Color.White
    } else {
        Color.Black
    }

    val noteColors = if (isDarkTheme) {
        NoteColorPalette.Dark
    } else {
        NoteColorPalette.Light
    }

    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    var showColorPicker by remember { mutableStateOf(false) }
    var showFormatToolbar by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LaunchedEffect(resolvedColorInt) {
            noteBackgroundAnimatable.animateTo(Color(resolvedColorInt))
        }

        LaunchedEffect(isDarkTheme) {
            viewModel.applyDefaultColor(isDarkTheme)
        }

        LaunchedEffect(key1 = true) {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is EditorViewModel.UiEvent.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(message = event.message)
                    }
                    is EditorViewModel.UiEvent.SavedNote -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBack()
                    }
                    is EditorViewModel.UiEvent.ImageInserted -> {
                        editorController.insertImage(event.path)
                    }
                }
            }
        }

        BackHandler {
            viewModel.onEvent(EditorEvent.SaveNote)
        }

        val timestamp = viewModel.noteTimestamp.value ?: System.currentTimeMillis()
        val formatter = remember { SimpleDateFormat("EEEE，M月d日", Locale.CHINESE) }
        val dateStr = formatter.format(Date(timestamp))

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Column(
                    modifier = Modifier.background(backgroundColor)
                ) {
                    if (showFormatToolbar) {
                        FormatToolbar(
                            contentColor = contentColor,
                            onFormatClick = { format ->
                                when (format) {
                                    "bold" -> editorController.toggleStyle(android.graphics.Typeface.BOLD)
                                    "italic" -> editorController.toggleStyle(android.graphics.Typeface.ITALIC)
                                    // other formats can be added later if needed
                                }
                            }
                        )
                    }
                    BottomAppBar(
                        containerColor = backgroundColor,
                        contentColor = contentColor,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        FilledIconButton(
                            onClick = { showColorPicker = true },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = contentColor.copy(alpha = 0.15f),
                                contentColor = contentColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Change color",
                                tint = contentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        FilledIconButton(
                            onClick = {
                                showImageSourceDialog = true
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = contentColor.copy(alpha = 0.15f),
                                contentColor = contentColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Add image",
                                tint = contentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        FilledIconButton(
                            onClick = { showFormatToolbar = !showFormatToolbar },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (showFormatToolbar) contentColor.copy(alpha = 0.3f) else contentColor.copy(alpha = 0.15f),
                                contentColor = contentColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = "Format text",
                                tint = contentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box {
                            FilledIconButton(
                                onClick = { showMenu = true },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = contentColor.copy(alpha = 0.15f),
                                    contentColor = contentColor
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = contentColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("日记信息") },
                                    onClick = {
                                        showMenu = false
                                        showInfoDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("分享日记") },
                                    onClick = {
                                        showMenu = false
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TITLE, titleState.text)
                                            putExtra(Intent.EXTRA_TEXT, "${titleState.text}\n\n${contentState.text}")
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "分享日记到")
                                        context.startActivity(shareIntent)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            val isInitializing by viewModel.isInitializing

            if (isInitializing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = contentColor
                    )
                }
            } else {
                CompositionLocalProvider(
                    LocalContentColor provides contentColor,
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = contentColor,
                        backgroundColor = contentColor.copy(alpha = 0.4f)
                    )
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                        EditorTopBar(
                            dateStr = dateStr,
                            onBack = { viewModel.onEvent(EditorEvent.SaveNote) },
                            onSave = { viewModel.onEvent(EditorEvent.SaveNote) },
                            backdrop = localBackdrop
                        )
                        
                        // Title Input
                        androidx.compose.foundation.text.BasicTextField(
                            value = titleState.text,
                            onValueChange = { viewModel.onEvent(EditorEvent.EnteredTitle(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = contentColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(contentColor),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (titleState.text.isEmpty()) {
                                        Text(
                                            text = titleState.hint,
                                            style = androidx.compose.ui.text.TextStyle(
                                                color = contentColor.copy(alpha = 0.5f),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    com.jian.tracemind.feature.editor.ui.components.NativeRichTextEditor(
                        controller = editorController,
                        initialHtml = contentState.text,
                        coroutineScope = scope,
                        onContentChanged = { html ->
                            viewModel.onEvent(EditorEvent.EnteredContent(html))
                        },
                        onImageClick = { imageUrl ->
                            viewingImageUrl = imageUrl
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textColor = contentColor,
                        hint = contentState.hint
                    )
                }
            }
        }

        if (showColorPicker) {
            ModalBottomSheet(
                onDismissRequest = { showColorPicker = false },
                containerColor = backgroundColor
            ) {
                Text(
                    text = "选择背景颜色",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(noteColors) { color ->
                        val colorInt = remember(color) { color.toArgb() }
                        val isSelected = viewModel.noteColor.value == colorInt
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "scale"
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .shadow(if (isSelected) 8.dp else 4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) contentColor else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true)
                                ) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch {
                                        noteBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(durationMillis = 500)
                                        )
                                    }
                                    viewModel.onEvent(EditorEvent.ChangeColor(colorInt))
                                }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = contentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                }
            }
        }

        if (showInfoDialog) {
            val timestamp = viewModel.noteTimestamp.value
            val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
            val dateString = remember(timestamp) {
                if (timestamp != null) formatter.format(Date(timestamp)) else "尚未保存"
            }
            val wordCount = remember(contentState.text) {
                contentState.text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            }
            val charCount = remember(contentState.text) {
                contentState.text.length
            }
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text(text = "日记信息") },
                text = {
                    Column {
                        Text(text = "最后修改: $dateString", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "字数: $wordCount", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "字符数: $charCount", style = MaterialTheme.typography.bodyMedium)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("确定", color = contentColor)
                    }
                },
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                textContentColor = contentColor.copy(alpha = 0.8f)
            )
        }

        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text(text = "添加图片") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showImageSourceDialog = false
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("从相册选择")
                        }
                        OutlinedButton(
                            onClick = {
                                showImageSourceDialog = false
                                val tempFile = java.io.File.createTempFile("photo_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    tempFile
                                )
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("拍照")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImageSourceDialog = false }) {
                        Text("取消", color = contentColor)
                    }
                },
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                textContentColor = contentColor
            )
        }

        viewingImageUrl?.let { imageUrl ->
            com.jian.tracemind.feature.editor.ui.components.ImageViewer(
                imageUrl = imageUrl,
                onDismissRequest = { viewingImageUrl = null },
                onShareClick = {
                    val fileUri = try {
                        val file = java.io.File(imageUrl)
                        androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    
                    fileUri?.let { uri ->
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Image"))
                    }
                }
            )
        }
    }
}
