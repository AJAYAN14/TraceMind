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
import androidx.hilt.navigation.compose.hiltViewModel
import com.jian.tracemind.feature.editor.ui.components.EditorTopBar
import com.jian.tracemind.feature.editor.ui.components.FormatToolbar
import com.jian.tracemind.feature.editor.ui.components.markdown.MarkdownField
import com.jian.tracemind.feature.editor.ui.components.markdown.MarkdownFormatter
import com.jian.tracemind.feature.editor.ui.theme.NoteColorPalette
import com.kyant.backdrop.backdrops.LayerBackdrop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    onBack: () -> Unit,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: EditorViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()

    var isPreviewMode by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    var contentTextFieldValue by remember {
        mutableStateOf(
            androidx.compose.ui.text.input.TextFieldValue(text = contentState.text)
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.onEvent(EditorEvent.InsertImage(it.toString()))
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

    val contentFocusRequester = remember { FocusRequester() }
    val titleFocusRequester = remember { FocusRequester() }

    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    var showColorPicker by remember { mutableStateOf(false) }
    var showFormatToolbar by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LaunchedEffect(contentState.text) {
            if (contentState.text != contentTextFieldValue.text) {
                contentTextFieldValue = contentTextFieldValue.copy(text = contentState.text)
            }
        }

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
                                contentTextFieldValue = MarkdownFormatter.injectMarkdown(format, contentTextFieldValue)
                                viewModel.onEvent(EditorEvent.EnteredContent(contentTextFieldValue.text))
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
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
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
                        FilledIconButton(
                            onClick = { isPreviewMode = !isPreviewMode },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isPreviewMode) contentColor.copy(alpha = 0.3f) else contentColor.copy(alpha = 0.15f),
                                contentColor = contentColor
                            )
                        ) {
                            Icon(
                                imageVector = if (isPreviewMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPreviewMode) "Edit mode" else "Preview mode",
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isPreviewMode) {
                                contentFocusRequester.requestFocus()
                            }
                        }
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
                            backdrop = backdrop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MarkdownField(
                        titleText = titleState.text,
                        contentTextFieldValue = contentTextFieldValue,
                        contentColor = contentColor,
                        isPreviewMode = isPreviewMode,
                        interactionSource = interactionSource,
                        contentFocusRequester = contentFocusRequester,
                        titleFocusRequester = titleFocusRequester,
                        onTitleChange = { viewModel.onEvent(EditorEvent.EnteredTitle(it)) },
                        onTitleFocusChange = { viewModel.onEvent(EditorEvent.ChangeTitleFocus(it)) },
                        onContentChange = {
                            contentTextFieldValue = it
                            viewModel.onEvent(EditorEvent.EnteredContent(it.text))
                        },
                        onContentFocusChange = {
                            viewModel.onEvent(EditorEvent.ChangeContentFocus(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
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
    }
}
