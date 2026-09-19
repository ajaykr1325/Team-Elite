package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.IssueCategory
import com.example.model.IssueSeverity
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitIssueScreen(
  onIssueSubmitted: (String) -> Unit,
  onNavigateToConfidential: () -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()

  var title by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(IssueCategory.WATER_SUPPLY) }
  var categoryExpanded by remember { mutableStateOf(false) }
  var location by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var severity by remember { mutableStateOf(IssueSeverity.HIGH) }
  var affectedStudents by remember { mutableFloatStateOf(25f) }
  var isAnonymous by remember { mutableStateOf(false) }
  var attachedImageName by remember { mutableStateOf<String?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showSuccessDialog by remember { mutableStateOf<String?>(null) }

  // Simple category & location duplicate matcher
  val potentialDuplicates by remember(selectedCategory, location) {
    derivedStateOf {
      repo.findPotentialDuplicates(selectedCategory, location)
    }
  }

  val quickLocations = listOf(
    "Block B, First Floor",
    "Academic Block A, Room 204",
    "Central Library, 2nd Floor",
    "Main Campus Canteen",
    "Hostel B, East Wing",
    "Back Gate Pedestrian Road"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .imePadding()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Report a Campus Issue",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = Navy900
        )
        Text(
          text = "Direct administrative escalation & tracking",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }

      OutlinedButton(
        onClick = onNavigateToConfidential,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SeverityCritical),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SeverityCritical)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.height(34.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Confidential Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Duplicate Detection Alert Banner
    if (potentialDuplicates.isNotEmpty() && location.isNotBlank()) {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A))),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Possible Existing Issue Detected",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF92400E)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "“This issue may already be reported. You can support the existing issue instead of creating a duplicate complaint.”",
            fontSize = 12.sp,
            color = Color(0xFF78350F)
          )
          Spacer(modifier = Modifier.height(8.dp))

          potentialDuplicates.take(2).forEach { dup ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${dup.trackingId}: ${dup.title}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Navy900,
                  maxLines = 1
                )
                Text(
                  text = "${dup.location} • ${dup.votes} votes",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
              TextButton(
                onClick = {
                  repo.toggleUpvote(dup.issueId, currentUser.userId)
                  onIssueSubmitted(dup.issueId)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = Blue600)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upvote This", fontSize = 11.sp, color = Blue600, fontWeight = FontWeight.Bold)
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Form Sections with Distinct Colored Borders
    // 1. Issue Title Section
    FormSectionCard(
      title = "Issue Title *",
      subtitle = "Provide a clear, brief headline for the problem",
      icon = Icons.Default.EditNote,
      borderColor = Color(0xFF2563EB) // Royal Blue
    ) {
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        placeholder = { Text("e.g. Water cooler leaking near Lecture Hall 12", color = TextMuted, fontSize = 13.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = appTextFieldColors(),
        singleLine = true
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Category Section
    FormSectionCard(
      title = "Category *",
      subtitle = "Routes complaint to the responsible campus department",
      icon = Icons.Default.Category,
      borderColor = Color(0xFF7C3AED) // Vibrant Violet / Purple
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
          IssueCategory.entries.forEach { cat ->
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

    // 3. Specific Campus Location Section
    FormSectionCard(
      title = "Specific Campus Location *",
      subtitle = "Building, wing, floor, room number or campus landmark",
      icon = Icons.Default.Place,
      borderColor = Color(0xFF0284C7) // Sky / Ocean Blue
    ) {
      OutlinedTextField(
        value = location,
        onValueChange = { location = it },
        placeholder = { Text("e.g. Block B, First Floor near Room 104", color = TextMuted, fontSize = 13.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = appTextFieldColors(),
        singleLine = true
      )

      // Quick location chips
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Quick Select:", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        quickLocations.take(3).forEach { loc ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFE0F2FE))
              .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(6.dp))
              .clickable { location = loc }
              .padding(horizontal = 7.dp, vertical = 3.dp)
          ) {
            Text(text = loc, fontSize = 10.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Detailed Description Section
    FormSectionCard(
      title = "Detailed Description *",
      subtitle = "Describe details regarding hazards, frequency, or urgency",
      icon = Icons.Default.Description,
      borderColor = Color(0xFF0D9488) // Deep Teal
    ) {
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        placeholder = { Text("Provide details regarding the nature of the issue, hazards, or frequency...", color = TextMuted, fontSize = 13.sp) },
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = appTextFieldColors()
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 5. Severity Selection Section
    FormSectionCard(
      title = "Severity Level *",
      subtitle = "Indicate urgency and impact on campus safety or operations",
      icon = Icons.Default.WarningAmber,
      borderColor = Color(0xFFEA580C) // Warm Amber / Orange
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IssueSeverity.entries.forEach { sev ->
          val isSelected = severity == sev
          val sevColor = when (sev) {
            IssueSeverity.LOW -> SeverityLow
            IssueSeverity.MEDIUM -> SeverityMedium
            IssueSeverity.HIGH -> SeverityHigh
            IssueSeverity.CRITICAL -> SeverityCritical
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) sevColor else SurfaceCard)
              .border(
                1.5.dp,
                if (isSelected) sevColor else Color(0xFFCBD5E1),
                RoundedCornerShape(8.dp)
              )
              .clickable { severity = sev }
              .padding(vertical = 9.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = sev.displayName,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Navy900
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 6. Impact / Affected Students Section
    FormSectionCard(
      title = "Estimated Affected Students",
      subtitle = "Helps faculty and maintenance gauge campus impact scope",
      icon = Icons.Default.Groups,
      borderColor = Color(0xFF4F46E5) // Indigo
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Student Community Reach:",
          fontSize = 12.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEEF2FF))
            .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "${affectedStudents.toInt()} students",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4F46E5)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Slider(
        value = affectedStudents,
        onValueChange = { affectedStudents = it },
        valueRange = 1f..350f,
        steps = 14,
        colors = SliderDefaults.colors(thumbColor = Color(0xFF4F46E5), activeTrackColor = Color(0xFF4F46E5))
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 7. Evidence & Privacy Settings Section
    FormSectionCard(
      title = "Evidence & Privacy Settings",
      subtitle = "Attach photo/video proof or file confidentially",
      icon = Icons.Default.PhotoCamera,
      borderColor = Color(0xFF059669) // Emerald Green
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = {
            attachedImageName = if (attachedImageName == null) "evidence_photo_water_cooler.jpg" else null
          },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (attachedImageName != null) Color(0xFF059669) else TextSecondary
          ),
          border = BorderStroke(1.dp, if (attachedImageName != null) Color(0xFF059669) else Color(0xFFCBD5E1))
        ) {
          Icon(
            imageVector = if (attachedImageName != null) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (attachedImageName != null) "Evidence Attached" else "Attach Photo/Video",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(
            checked = isAnonymous,
            onCheckedChange = { isAnonymous = it },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF059669))
          )
          Text(
            text = "Submit Anonymously",
            fontSize = 12.sp,
            color = Navy900,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    if (errorMessage != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = errorMessage ?: "",
        color = SeverityCritical,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Submit Button
    Button(
      onClick = {
        if (title.isBlank() || location.isBlank() || description.isBlank()) {
          errorMessage = "Please complete all required fields (Title, Location, Description)."
          return@Button
        }
        errorMessage = null

        // Submit to repository
        val created = repo.submitIssue(
          title = title,
          category = selectedCategory,
          location = location,
          description = description,
          severity = severity,
          affectedStudents = affectedStudents.toInt(),
          anonymous = isAnonymous,
          imageUrl = attachedImageName
        )

        showSuccessDialog = created.trackingId
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Blue600)
    ) {
      Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Submit Complaint to Campus Admin",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }

  // Success Confirmation Dialog
  showSuccessDialog?.let { trackingId ->
    AlertDialog(
      onDismissRequest = {},
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      confirmButton = {
        Button(
          onClick = {
            val issueId = repo.issues.value.find { it.trackingId == trackingId }?.issueId ?: ""
            showSuccessDialog = null
            onIssueSubmitted(issueId)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("View Issue Details & Timeline", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusResolved,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Complaint Successfully Submitted!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }
      },
      text = {
        Column {
          Text(
            text = "Your campus issue has been registered in the smart administration database.",
            fontSize = 13.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Blue50)
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Official Tracking ID", fontSize = 11.sp, color = TextMuted)
              Text(
                text = trackingId,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Blue600
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Students can now view and upvote this issue on the Public Board. Administrators have received a notification.",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }
      }
    )
  }
}

@Composable
private fun FormSectionCard(
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
