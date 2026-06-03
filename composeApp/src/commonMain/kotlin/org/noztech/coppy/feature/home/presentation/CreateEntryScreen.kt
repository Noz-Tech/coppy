package org.noztech.coppy.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.feature.home.presentation.composables.CreateListTopBar
import org.noztech.coppy.feature.home.presentation.viewmodels.CreateListViewModel
import org.noztech.coppy.navigation.AuthRoutes

private enum class EntryType(val value: String, val displayName: String) {
    IdCard("ID_CARD", "ID Card"),
    AtmCard("ATM_CARD", "ATM Card"),
    Policy("POLICY", "Policy"),
    Plate("PLATE", "Plate"),
    Custom("CUSTOM", "Custom")
}

@Composable
fun CreateListScreen(
    navController: NavController,
    id: Long? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel = koinViewModel<CreateListViewModel>()
    val groups by viewModel.groups.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    val existingItem = id?.let { viewModel.getItemById(it) }

    var entryType by remember { mutableStateOf(EntryType.Custom) }
    var name by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var expiresAt by remember { mutableStateOf("") }
    var securityCode by remember { mutableStateOf("") }

    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Success -> {
                navController.navigate(AuthRoutes.Home) {
                    popUpTo(AuthRoutes.Home) {
                        inclusive = true
                    }
                }
            }

            is SaveState.Error -> snackbarHostState.showSnackbar((saveState as SaveState.Error).error)
            else -> Unit
        }
    }

    LaunchedEffect(existingItem) {
        existingItem?.let { item ->
            name = item.title
            value = item.value_.orEmpty()
            issuer = item.issuer.orEmpty()
            expiresAt = item.expiresAt.orEmpty()
            securityCode = item.securityCode.orEmpty()
            entryType = EntryType.entries.firstOrNull { it.value == item.entryType } ?: EntryType.Custom
            viewModel.selectGroup(item.groupId)
        }
    }

    fun primaryLabelFor(type: EntryType): String = when (type) {
        EntryType.IdCard -> "ID Number"
        EntryType.AtmCard -> "Card Number"
        EntryType.Policy -> "Policy Number"
        EntryType.Plate -> "Plate Number"
        EntryType.Custom -> "Value"
    }

    fun valueKeyboardType(type: EntryType): KeyboardType = when (type) {
        EntryType.Custom -> KeyboardType.Text
        else -> KeyboardType.Number
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CreateListTopBar(
                    navController,
                    onSaveClick = {
                        val validationError = validateEntry(
                            type = entryType,
                            name = name,
                            value = value,
                            expiresAt = expiresAt,
                            securityCode = securityCode
                        )
                        if (validationError != null) {
                            viewModel.showValidationError(validationError)
                            return@CreateListTopBar
                        }
                        viewModel.saveItem(
                            title = name,
                            value = value,
                            entryType = entryType.value,
                            issuer = issuer.ifBlank { null },
                            expiresAt = expiresAt.ifBlank { null },
                            securityCode = securityCode.ifBlank { null },
                            existingItemId = existingItem?.id
                        )
                    },
                    title = if (existingItem != null) "Edit Entry" else "Create Entry"
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EntryType.entries) { type ->
                        FilterChip(
                            selected = entryType == type,
                            onClick = { entryType = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (entryType == EntryType.IdCard) "ID Card Name" else "Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(primaryLabelFor(entryType)) },
                    singleLine = entryType != EntryType.Custom,
                    maxLines = if (entryType == EntryType.Custom) 3 else 1,
                    keyboardOptions = KeyboardOptions(keyboardType = valueKeyboardType(entryType)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (entryType == EntryType.Custom) {
                                Modifier.height(100.dp)
                            } else {
                                Modifier.heightIn(min = 64.dp)
                            }
                        )
                )

                if (entryType == EntryType.Policy || entryType == EntryType.IdCard) {
                    OutlinedTextField(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = { Text("Issuer / Provider") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (entryType == EntryType.AtmCard) {
                    OutlinedTextField(
                        value = expiresAt,
                        onValueChange = { input ->
                            expiresAt = input
                                .filter { it.isDigit() }
                                .take(4)
                                .let {
                                    when {
                                        it.length <= 2 -> it
                                        else -> "${it.take(2)}/${it.drop(2)}"
                                    }
                                }
                        },
                        label = { Text("Expiry (MM/YY)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = securityCode,
                        onValueChange = { securityCode = it.filter(Char::isDigit).take(4) },
                        label = { Text("CVV") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Folder",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(groups) { group ->
                            FilterChip(
                                selected = selectedGroupId == group.id,
                                onClick = { viewModel.selectGroup(group.id) },
                                label = { Text(group.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun validateEntry(
    type: EntryType,
    name: String,
    value: String,
    expiresAt: String,
    securityCode: String
): String? {
    if (name.isBlank() || value.isBlank()) return "Name and value are required."

    return when (type) {
        EntryType.AtmCard -> {
            when {
                value.filter(Char::isDigit).length !in 12..19 -> "Card number should be 12 to 19 digits."
                !Regex("^(0[1-9]|1[0-2])/[0-9]{2}$").matches(expiresAt) -> "Expiry must be MM/YY."
                securityCode.length !in 3..4 -> "CVV should be 3 or 4 digits."
                else -> null
            }
        }

        EntryType.Plate -> if (value.length < 4) "Plate number looks too short." else null
        EntryType.Custom -> null
        else -> if (value.filter(Char::isDigit).length < 6) "Please enter a valid number." else null
    }
}
