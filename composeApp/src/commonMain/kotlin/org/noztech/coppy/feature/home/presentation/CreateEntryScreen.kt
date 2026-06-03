package org.noztech.coppy.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import org.noztech.coppy.feature.home.presentation.composables.CreateGroupBottomSheet
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.feature.home.domain.model.EntryFieldInput
import org.noztech.coppy.feature.home.presentation.composables.CreateListTopBar
import org.noztech.coppy.feature.home.presentation.viewmodels.CreateListViewModel
import org.noztech.coppy.navigation.AuthRoutes

private enum class EntryType(val value: String, val displayName: String) {
    Id("ID", "ID"),
    Card("CARD", "Debit / Credit Card"),
    BankAccount("BANK_ACCOUNT", "Bank Account"),
    Policy("POLICY", "Insurance / Policy"),
    Vehicle("VEHICLE", "Vehicle"),
    Account("ACCOUNT", "Login / Account"),
    Wifi("WIFI", "Wi-Fi Password"),
    SecureNote("SECURE_NOTE", "Secure Note"),
}

private data class CustomFieldUi(
    val label: String = "",
    val value: String = "",
    val required: Boolean = false,
)

private fun templateFieldsFor(type: EntryType): List<CustomFieldUi> = when (type) {
    EntryType.Id -> listOf(
        CustomFieldUi(label = "ID Number", required = true),
        CustomFieldUi(label = "Full Name"),
        CustomFieldUi(label = "Issuer"),
        CustomFieldUi(label = "Expiration Date"),
    )
    EntryType.Card -> listOf(
        CustomFieldUi(label = "Card Number", required = true),
        CustomFieldUi(label = "Cardholder Name"),
        CustomFieldUi(label = "Expiry"),
        CustomFieldUi(label = "CVV"),
        CustomFieldUi(label = "Issuer"),
    )
    EntryType.BankAccount -> listOf(
        CustomFieldUi(label = "Account Number", required = true),
        CustomFieldUi(label = "Account Name"),
        CustomFieldUi(label = "Routing / Sort / Branch Code"),
        CustomFieldUi(label = "SWIFT / IBAN"),
    )
    EntryType.Policy -> listOf(
        CustomFieldUi(label = "Policy Number", required = true),
        CustomFieldUi(label = "Provider"),
        CustomFieldUi(label = "Holder Name"),
        CustomFieldUi(label = "Coverage Type"),
        CustomFieldUi(label = "Expiration Date"),
    )
    EntryType.Vehicle -> listOf(
        CustomFieldUi(label = "Plate Number", required = true),
        CustomFieldUi(label = "Registration Number"),
        CustomFieldUi(label = "VIN / Chassis Number"),
        CustomFieldUi(label = "Owner Name"),
        CustomFieldUi(label = "Expiration Date"),
    )
    EntryType.Account -> listOf(
        CustomFieldUi(label = "Username / Email", required = true),
        CustomFieldUi(label = "Password"),
        CustomFieldUi(label = "Website / App"),
        CustomFieldUi(label = "Recovery Email"),
    )
    EntryType.Wifi -> listOf(
        CustomFieldUi(label = "Password", required = true),
        CustomFieldUi(label = "Security Type"),
        CustomFieldUi(label = "Router Admin URL"),
    )
    EntryType.SecureNote -> listOf(
        CustomFieldUi(label = "Note", required = true),
    )
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

    var entryType by remember { mutableStateOf(EntryType.Id) }
    var name by remember { mutableStateOf("") }
    var customFields by remember { mutableStateOf(templateFieldsFor(EntryType.Id)) }
    var showCreateFolderSheet by remember { mutableStateOf(false) }

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
        if (existingItem == null) {
            viewModel.clearSelectedGroup()
            return@LaunchedEffect
        }

        existingItem.let { item ->
            name = item.title
            entryType = EntryType.entries.firstOrNull { it.value == item.entryType } ?: EntryType.Id
            val templateFields = templateFieldsFor(entryType)
            val savedFields = viewModel.getFieldsByEntryId(item.id)
            val mappedTemplateFields = templateFields.map { templateField ->
                    val savedField = savedFields.firstOrNull { it.label == templateField.label }
                    templateField.copy(value = savedField?.value_.orEmpty())
                }
            val extraSavedFields = savedFields
                .filterNot { savedField -> templateFields.any { it.label == savedField.label } }
                .map { savedField -> CustomFieldUi(label = savedField.label, value = savedField.value_) }
            customFields = mappedTemplateFields + extraSavedFields
            viewModel.selectGroup(item.groupId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CreateListTopBar(
                    navController,
                    onSaveClick = {
                        val fieldInputs = customFields
                            .map { field ->
                                EntryFieldInput(
                                    label = field.label.trim(),
                                    value = field.value.trim(),
                                )
                            }
                            .filter { field -> field.value.isNotBlank() }
                        val validationError = validateEntry(
                            name = name,
                            fields = customFields,
                        )
                        if (validationError != null) {
                            viewModel.showValidationError(validationError)
                            return@CreateListTopBar
                        }
                        viewModel.saveItem(
                            title = name,
                            entryType = entryType.value,
                            groupId = selectedGroupId,
                            existingItemId = existingItem?.id,
                            fields = fieldInputs,
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
                            onClick = {
                                if (entryType != type) {
                                    entryType = type
                                    customFields = templateFieldsFor(type)
                                }
                            },
                            label = { Text(type.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("${entryType.displayName} Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    customFields.forEachIndexed { index, field ->
                        OutlinedTextField(
                            value = field.value,
                            onValueChange = { input ->
                                customFields = customFields.toMutableList().also {
                                    it[index] = field.copy(value = input)
                                }
                            },
                            label = {
                                Text(if (field.required) "${field.label} *" else "${field.label} (optional)")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Folder",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { showCreateFolderSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Plus,
                                contentDescription = "Create Folder",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
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

            CreateGroupBottomSheet(
                showSheet = showCreateFolderSheet,
                onDismiss = { showCreateFolderSheet = false },
                onSave = { name ->
                    viewModel.createGroup(name)
                    showCreateFolderSheet = false
                }
            )
        }
    }
}

private fun validateEntry(
    name: String,
    fields: List<CustomFieldUi>,
): String? {
    if (name.isBlank()) return "Name is required."
    val missingRequired = fields.firstOrNull { it.required && it.value.isBlank() }
    return if (missingRequired != null) "${missingRequired.label} is required." else null
}
