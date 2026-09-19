package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CampusVoiceRepository
import com.example.model.ConfidentialCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfidentialReportScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  var selectedCategory by remember { mutableStateOf(ConfidentialCategory.RAGGING) }
  var categoryExpanded by remember { mutableStateOf(false) }
  var location by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var isAnonymous by remember { mutableStateOf(true) }
  var hasEvidence by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var submittedId by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .imePadding()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Top Security Banner
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA))),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = SeverityCritical,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Confidential Safe Reporting Portal",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF991B1B)
          )
          Text(
            text = "Encrypted submission. Hidden from public board. Accessible only by the Anti-Ragging Committee & Dean.",
            fontSize = 11.sp,
            color = Color(0xFF7F1D1D)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 1. Protected Sensitive Category Section
    ConfidentialSectionCard(
      title = "Protected Category *",
      subtitle = "Specialized escalation to campus integrity authorities",
      icon = Icons.Default.Security,
      borderColor = Color(0xFFDC2626) // Crimson / Critical
    ) {
      ExposedDropdownMenuBox(
        expanded = categoryExpanded,
        onExpandedChange = { categoryExpanded = it },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = selectedCategory.displayName,
          onValueChange = {},
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
          shape = RoundedCornerShape(10.dp),
          colors = appTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .menuAnchor()
        )
        ExposedDropdownMenu(
          expanded = categoryExpanded,
          onDismissRequest = { categoryExpanded = false }
        ) {
          ConfidentialCategory.entries.forEach { cat ->
            DropdownMenuItem(
              text = { Text(cat.displayName, fontSize = 13.sp, color = Navy900) },
              onClick = {
                selectedCategory = cat
                categoryExpanded = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Incident Location Section
    ConfidentialSectionCard(
      title = "Location of Incident *",
      subtitle = "Specify hostel, department, sports ground, or campus area",
      icon = Icons.Default.Place,
      borderColor = Color(0xFF0284C7) // Sky / Ocean Blue
    ) {
      OutlinedTextField(
        value = location,
        onValueChange = { location = it },
        placeholder = { Text("e.g. Hostel C Courtyard, Sports Complex", color = TextMuted, fontSize = 13.sp) },
        shape = RoundedCornerShape(10.dp),
        colors = appTextFieldColors(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Incident Details Section
    ConfidentialSectionCard(
      title = "Incident Details *",
      subtitle = "Describe what occurred, dates, persons involved, or safety threats",
      icon = Icons.Default.Description,
      borderColor = Color(0xFF7C3AED) // Purple
    ) {
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        placeholder = { Text("Describe what occurred, dates, persons involved, or any safety threats...", color = TextMuted, fontSize = 13.sp) },
        shape = RoundedCornerShape(10.dp),
        colors = appTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Evidence & Anonymous Protection Section
    ConfidentialSectionCard(
      title = "Encrypted Evidence & Anonymous Protection",
      subtitle = "Attach encrypted evidence files and protect your identity",
      icon = Icons.Default.Lock,
      borderColor = Color(0xFF0D9488) // Deep Teal
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { hasEvidence = !hasEvidence },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (hasEvidence) Color(0xFF0D9488) else TextSecondary
          ),
          border = BorderStroke(1.dp, if (hasEvidence) Color(0xFF0D9488) else Color(0xFFCBD5E1))
        ) {
          Icon(
            imageVector = if (hasEvidence) Icons.Default.Check else Icons.Default.Attachment,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (hasEvidence) "Evidence Encrypted" else "Attach Evidence File", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(
            checked = isAnonymous,
            onCheckedChange = { isAnonymous = it },
            colors = CheckboxDefaults.colors(checkedColor = SeverityCritical)
          )
          Text(
            text = "Anonymous Report",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Navy900
          )
        }
      }
    }

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = errorMessage ?: "",
        color = SeverityCritical,
        fontSize = 12.sp
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = {
        if (location.isBlank() || description.isBlank()) {
          errorMessage = "Please provide incident location and description."
          return@Button
        }
        errorMessage = null

        val conf = repo.submitConfidentialIssue(
          category = selectedCategory,
          description = description,
          location = location,
          anonymous = isAnonymous,
          evidenceUrl = if (hasEvidence) "encrypted_evidence.dat" else null
        )

        submittedId = conf.privateTrackingId
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = SeverityCritical),
      shape = RoundedCornerShape(10.dp)
    ) {
      Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Submit Confidential Report Securely",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    TextButton(
      onClick = onNavigateBack,
      modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
      Text("Back to Standard Reporting", color = TextSecondary, fontSize = 12.sp)
    }
  }

  // Confirmation Dialog
  submittedId?.let { trackingId ->
    AlertDialog(
      onDismissRequest = {},
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      confirmButton = {
        Button(
          onClick = {
            submittedId = null
            onNavigateBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Navy900)
        ) {
          Text("Return to Student Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = SeverityCritical)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Confidential Report Logged", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }
      },
      text = {
        Column {
          Text(
            text = "Your confidential complaint has been recorded in the administrator vault. No student peers can see this issue.",
            fontSize = 13.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(12.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF2F2))
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Private Confidential Tracking ID", fontSize = 11.sp, color = TextMuted)
              Text(
                text = trackingId,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SeverityCritical
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Save this ID for private resolution inquiries. The Anti-Ragging and Welfare Committee has been alerted immediately.",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }
    )
  }
}

@Composable
private fun ConfidentialSectionCard(
  title: String,
  subtitle: String? = null,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  borderColor: Color,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    border = BorderStroke(1.5.dp, borderColor)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(borderColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = borderColor,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
      content()
    }
  }
}
