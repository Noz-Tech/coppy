package org.noztech.coppy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.CopyCheck
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.core.util.CopyToClipboard
import org.noztech.coppy.feature.home.presentation.viewmodels.EntryDetailViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@Composable
fun EntryDetailScreen(
    navController: NavController,
    id: Long
) {
    val viewModel = koinViewModel<EntryDetailViewModel>()
    val entry by viewModel.entry.collectAsState()
    val customFields by viewModel.fields.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var copiedAll by remember { mutableStateOf(false) }

    LaunchedEffect(id) { viewModel.load(id) }

    Scaffold(
        topBar = { EntryTopBar(navController, entry?.title ?: "Entry") },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val item = entry
        if (item == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Entry not found")
            }
            return@Scaffold
        }

        val fields = buildList {
            add("Title" to item.title)
            add("Type" to item.entryType.toEntryTypeDisplayName())
            customFields.forEach { field ->
                add(field.label to field.value_)
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fields) { (label, value) ->
                EntryFieldRow(
                    label = label,
                    value = value,
                    onCopy = { CopyToClipboard(value) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val text = buildString {
                                fields.forEach { (label, value) ->
                                    appendLine("$label: $value")
                                }
                            }
                            CopyToClipboard(text.trim())
                            copiedAll = true
                            coroutineScope.launch {
                                delay(3000)
                                copiedAll = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(
                            imageVector = if (copiedAll) Lucide.CopyCheck else Lucide.Copy,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Copy All")
                    }

                    Button(
                        onClick = { viewModel.toggleVisibility() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(if (item.hidden == 1L) Lucide.Eye else Lucide.EyeOff, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (item.hidden == 1L) "Unhide" else "Hide")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryTopBar(
    navController: NavController,
    title: String
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {}
    )
}

@Composable
private fun EntryFieldRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var copied by remember(label, value) { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$label: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = {
                    onCopy()
                    copied = true
                    coroutineScope.launch {
                        delay(3000)
                        copied = false
                    }
                }
            ) {
                Icon(
                    imageVector = if (copied) Lucide.CopyCheck else Lucide.Copy,
                    contentDescription = if (copied) "Copied" else "Copy",
                    tint = if (copied)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
