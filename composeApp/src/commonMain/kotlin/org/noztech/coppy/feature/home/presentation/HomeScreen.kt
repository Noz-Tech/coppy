package org.noztech.coppy.feature.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.CopyCheck
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Folders
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Share2
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.StickyNote
import com.composables.icons.lucide.Wifi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.noztech.EntryField
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.common.ConfirmActionType
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.ui.components.AppTopBar
import org.noztech.coppy.core.ui.components.ConfirmActionDialog
import org.noztech.coppy.core.util.BiometricAuthResult
import org.noztech.coppy.core.util.BiometricAuthenticator
import org.noztech.coppy.core.util.CopyToClipboard
import org.noztech.coppy.core.util.QuickHaptic
import org.noztech.coppy.core.util.ShareText
import org.noztech.coppy.feature.home.presentation.viewmodels.HomeViewModel
import org.noztech.coppy.navigation.AuthRoutes
import org.koin.compose.getKoin

@Composable
fun HomeScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = koinViewModel<HomeViewModel>()
    val appSettings: AppSettings = getKoin().get()
    val biometricAuthenticator = remember { BiometricAuthenticator() }
    val groups by viewModel.groups.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val hiddenItems by viewModel.hiddenItems.collectAsState()
    val showHiddenItems by appSettings.showHiddenItems.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val groupNamesById by remember(groups) {
        derivedStateOf { groups.associate { it.id to it.name.toFolderDisplayName() } }
    }
    val listState = rememberLazyListState()
    var showSearchBar by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemId by remember { mutableStateOf<Long?>(null) }
    var selectedItemTitle by remember { mutableStateOf<String?>(null) }
    var selectedItemHidden by remember { mutableStateOf(false) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmActionType by remember { mutableStateOf<ConfirmActionType?>(null) }

    fun runBiometricGuard(
        enabled: Boolean,
        title: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        if (!enabled) {
            onSuccess()
            return
        }
        biometricAuthenticator.authenticate(title, description) { result ->
            if (result == BiometricAuthResult.Success) {
                onSuccess()
            }
        }
    }

    val searchBarHeight by animateDpAsState(
        targetValue = if (showSearchBar) 52.dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic)
    )

    val searchBarAlpha by animateFloatAsState(
        targetValue = if (showSearchBar) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                // Show search only when we're at the very top of the list
                showSearchBar = (index == 0 && offset == 0)
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            AppTopBar(
                navController = navController,
                selectedItemId = selectedItemId,
                selectedItemHidden = selectedItemHidden,
                onCancelSelection = {
                    selectedItemId = null
                    selectedItemTitle = null
                    selectedItemHidden = false
                },
                onEdit = { id ->
                    navController.navigate(AuthRoutes.CreateList(selectedItemId))
                },
                onDelete = { id ->
                    confirmActionType = ConfirmActionType.DELETE
                    showConfirmDialog = true
                },
                onHide = { id ->
                    confirmActionType = ConfirmActionType.HIDE
                    showConfirmDialog = true

                },
                onSettingsClick = {
                    biometricAuthenticator.authenticate(
                        title = "Open settings",
                        description = "Authenticate to change Coppy settings"
                    ) { result ->
                        if (result == BiometricAuthResult.Success) {
                            navController.navigate(AuthRoutes.Settings)
                        }
                    }
                },
                selectedItemTitle = selectedItemTitle
            )
        },
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp) // adjust distance from top bar
                    .wrapContentHeight(Alignment.Top),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(searchBarHeight)
                        .graphicsLayer { alpha = searchBarAlpha }
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            viewModel.updateSearchQuery(newValue)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        placeholder = {
                            Text(
                                "Search",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Surface(
                        onClick = {
                            navController.navigate(AuthRoutes.CreateList())
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        tonalElevation = 2.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .animateContentSize(animationSpec = tween(100))
                    .height(if (showSearchBar) 5.dp else 0.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                item {
                    Surface(
                        onClick = {
                            navController.navigate(AuthRoutes.Group)
                        },
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minWidth = 48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Folders,
                                contentDescription = "Add folder",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    FilterChip(
                        selected = selectedGroupId == null,
                        onClick = {
                            if (selectedGroupId != null) {
                                QuickHaptic()
                                viewModel.selectGroup(null)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        label = { Text("All") }
                    )
                }

                items(groups) { group ->
                    FilterChip(
                        selected = selectedGroupId == group.id,
                        onClick = {
                            if (selectedGroupId != group.id) {
                                QuickHaptic()
                                viewModel.selectGroup(group.id)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        label = { Text(group.name.toFolderDisplayName()) }
                    )
                }
            }

            Spacer(Modifier.height(5.dp))

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredItems.isEmpty()) {
                    item {
                        EmptyState(
                            title = when {
                                searchQuery.isNotBlank() -> "No results found"
                                selectedGroupId != null -> "No entries in this folder"
                                else -> "No entries yet"
                            },
                            message = when {
                                searchQuery.isNotBlank() -> "Try a different name, value, or folder."
                                selectedGroupId != null -> "Add an entry to this folder or choose another folder."
                                else -> "Tap the plus button to save your first item."
                            }
                        )
                    }
                }

                items(filteredItems, key = { it.id }) { item ->
                    var copied by remember(item.id) { mutableStateOf(false) }
                    val isSelected = selectedItemId == item.id
                    val entryFields = viewModel.getEntryFields(item.id)
                    val primaryValue = entryFields.firstOrNull()?.value_.orEmpty()
                    val copyText = buildEntryText(item.title, entryFields)
                    val folderName = groupNamesById[item.groupId]

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    if (selectedItemId != null) {
                                        // if already in selection mode, toggle
                                        selectedItemId = if (isSelected) null else item.id
                                        selectedItemTitle = if (isSelected) null else item.title
                                        selectedItemHidden = false
                                    } else {
                                        navController.navigate(AuthRoutes.EntryDetail(item.id))
                                    }
                                },
                                onLongClick = {
                                    selectedItemId = item.id
                                    selectedItemTitle = item.title
                                    selectedItemHidden = false
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = iconForEntryType(item.entryType),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column() {
                                    Text(
                                        text = item.title.uppercase(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    MaskableText(secretValue = primaryValue.uppercase())
                                    folderName?.let { name ->
                                        Text(
                                            text = name,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-8).dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                IconButton(
                                    onClick = {
                                        runBiometricGuard(
                                            enabled = appSettings.isBiometricOnCopyEnabled(),
                                            title = "Copy protected data",
                                            description = "Authenticate to copy this value"
                                        ) {
                                            CopyToClipboard(copyText)
                                            copied = true
                                            coroutineScope.launch {
                                                delay(3000)
                                                copied = false
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (copied) Lucide.CopyCheck else Lucide.Copy,
                                        contentDescription = if (copied) "Copied" else "Copy",
                                        tint = if (copied)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        runBiometricGuard(
                                            enabled = appSettings.isBiometricOnShareEnabled(),
                                            title = "Share protected data",
                                            description = "Authenticate to share this value"
                                        ) {
                                            val shareText = "From Coppy App:\n$copyText"
                                            ShareText(shareText)
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Lucide.Share2,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

	                val filteredHiddenItems = hiddenItems.filter { item ->
                    val matchesGroup = selectedGroupId == null || item.groupId == selectedGroupId
                    val query = searchQuery.trim().lowercase()
                    val fields = viewModel.getEntryFields(item.id)
                    val matchesSearch = query.isBlank() ||
                        item.title.lowercase().contains(query) ||
                        fields.any { field ->
                            field.label.lowercase().contains(query) ||
                                field.value_.lowercase().contains(query)
                        }

                    matchesGroup && matchesSearch
                }

                if (showHiddenItems) {
                    item {
                        Text(
                            text = "Hidden Items",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }
                }

                if (showHiddenItems && filteredHiddenItems.isEmpty()) {
                    item {
                        EmptyState(
                            title = when {
                                searchQuery.isNotBlank() -> "No hidden results"
                                selectedGroupId != null -> "No hidden entries in this folder"
                                else -> "No hidden items"
                            },
                            message = when {
                                searchQuery.isNotBlank() -> "Try a different search for hidden entries."
                                selectedGroupId != null -> "Hidden entries for this folder will appear here."
                                else -> "Items you hide will appear here when hidden items are enabled."
                            },
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (showHiddenItems && filteredHiddenItems.isNotEmpty()) {
                    items(filteredHiddenItems, key = { "hidden-${it.id}" }) { item ->
                        var copied by remember(item.id) { mutableStateOf(false) }
                        val isSelected = selectedItemId == item.id && selectedItemHidden
                        val entryFields = viewModel.getEntryFields(item.id)
                        val primaryValue = entryFields.firstOrNull()?.value_.orEmpty()
                        val copyText = buildEntryText(item.title, entryFields)
                        val folderName = groupNamesById[item.groupId]

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        if (selectedItemId != null) {
                                            selectedItemId = if (isSelected) null else item.id
                                            selectedItemTitle = if (isSelected) null else item.title
                                            selectedItemHidden = !isSelected
                                        } else {
                                            navController.navigate(AuthRoutes.EntryDetail(item.id))
                                        }
                                    },
                                    onLongClick = {
                                        selectedItemId = item.id
                                        selectedItemTitle = item.title
                                        selectedItemHidden = true
                                    }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = iconForEntryType(item.entryType),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column() {
                                        Text(
                                            text = item.title.uppercase(),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        MaskableText(secretValue = primaryValue.uppercase())
                                        folderName?.let { name ->
                                            Text(
                                                text = name,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy((-8).dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.wrapContentWidth()
                                ) {
                                    IconButton(
                                        onClick = {
                                            runBiometricGuard(
                                                enabled = appSettings.isBiometricOnCopyEnabled(),
                                                title = "Copy protected data",
                                                description = "Authenticate to copy this value"
                                            ) {
                                                CopyToClipboard(copyText)
                                                copied = true
                                                coroutineScope.launch {
                                                    delay(3000)
                                                    copied = false
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (copied) Lucide.CopyCheck else Lucide.Copy,
                                            contentDescription = if (copied) "Copied" else "Copy",
                                            tint = if (copied)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            runBiometricGuard(
                                                enabled = appSettings.isBiometricOnShareEnabled(),
                                                title = "Share protected data",
                                                description = "Authenticate to share this value"
                                            ) {
                                                val shareText = "From Coppy App:\n$copyText"
                                                ShareText(shareText)
                                            }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Share2,
                                            contentDescription = "Share",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        ConfirmActionDialog(
            showDialog = showConfirmDialog,
            onDismiss = { showConfirmDialog = false },
            title = when (confirmActionType) {
                ConfirmActionType.HIDE -> if (selectedItemHidden) "Unhide Item?" else "Hide Item?"
                ConfirmActionType.DELETE -> "Delete Item?"
                else -> ""
            },
            message = when (confirmActionType) {
                ConfirmActionType.HIDE -> if (selectedItemHidden) {
                    "Are you sure you want to show this item in your normal list again?"
                } else {
                    "Are you sure you want to hide this item? You can unhide it later."
                }
                ConfirmActionType.DELETE -> "Are you sure you want to delete this item? This action cannot be undone."
                else -> ""
            },
            confirmText = "Yes",
            dismissText = "No",
            onConfirm = {
                selectedItemId?.let { id ->
                    when (confirmActionType) {
                        ConfirmActionType.HIDE -> {
                            if (selectedItemHidden) viewModel.unhideItem(id)
                            else viewModel.hideItem(id)
                        }
                        ConfirmActionType.DELETE -> viewModel.deleteItem(id)
                        else -> {}
                    }
                }
                selectedItemId = null
                selectedItemTitle = null
                selectedItemHidden = false
                showConfirmDialog = false
            }
        )
    }
}

@Composable
private fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = message,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun buildEntryText(
    title: String,
    fields: List<EntryField>,
): String {
    return buildString {
        appendLine(title.uppercase())
        fields.forEach { field ->
            appendLine("${field.label}: ${field.value_}")
        }
    }.trim()
}

private fun iconForEntryType(entryType: String): ImageVector {
    return when (entryType) {
        "SIMPLE_ENTRY" -> Lucide.FileText
        "ID" -> Lucide.IdCard
        "CARD" -> Lucide.CreditCard
        "BANK_ACCOUNT" -> Lucide.Landmark
        "POLICY" -> Lucide.ShieldCheck
        "VEHICLE" -> Lucide.Car
        "ACCOUNT" -> Lucide.KeyRound
        "WIFI" -> Lucide.Wifi
        "SECURE_NOTE" -> Lucide.StickyNote
        else -> Lucide.FileText
    }
}

@Composable
fun MaskableText(secretValue: String) {
    val appSettings: AppSettings = getKoin().get()
    val biometricAuthenticator = remember { BiometricAuthenticator() }
    var isVisible by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-10).dp),
        modifier = Modifier.height(22.dp)
    ) {
        Text(
            text = if (isVisible) secretValue else maskValue(secretValue),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = {
            if (isVisible) {
                isVisible = false
            } else if (appSettings.isBiometricOnRevealEnabled()) {
                biometricAuthenticator.authenticate(
                    title = "Reveal protected data",
                    description = "Authenticate to reveal this value"
                ) { result ->
                    if (result == BiometricAuthResult.Success) {
                        isVisible = true
                    }
                }
            } else {
                isVisible = true
            }
        }) {
            Icon(
                imageVector = if (isVisible) Lucide.EyeOff else Lucide.Eye,
                contentDescription = if (isVisible) "Hide" else "Show",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

fun maskValue(value: String, visibleCount: Int = 4, maskChar: Char = '•'): String {
    return if (value.length <= visibleCount) value
    else buildString {
        repeat(value.length - visibleCount) { append(maskChar) }
        append(value.takeLast(visibleCount))
    }
}

private fun String.toFolderDisplayName(): String {
    val acronyms = setOf("id", "sss", "gsis", "atm", "cvv", "tin")
    return trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            val lower = word.lowercase()
            if (lower in acronyms) lower.uppercase()
            else lower.replaceFirstChar { it.uppercase() }
        }
}

private fun String.toEntryTypeDisplayName(): String {
    return lowercase()
        .split("_")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            when (part) {
                "id", "sss", "gsis", "atm", "cvv", "tin" -> part.uppercase()
                else -> part.replaceFirstChar { it.uppercase() }
            }
        }
}
