package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SeverityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class AdminTab(val title: String) {
  ALL_COMPLAINTS("Complaints Table"),
  CONFIDENTIAL("Confidential Vault"),
  DELAYED("Delayed (SLA)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagementScreen(
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()
  val confidentialIssues by repo.confidentialIssues.collectAsState()

  var currentTab by remember { mutableStateOf(AdminTab.ALL_COMPLAINTS) }
  var searchQuery by remember { mutableStateOf("") }
  var filterCategory by remember { mutableStateOf<IssueCategory?>(null) }
  var filterDepartment by remember { mutableStateOf<String?>(null) }
  var filterStatus by remember { mutableStateOf<IssueStatus?>(null) }
  var filterSeverity by remember { mutableStateOf<IssueSeverity?>(null) }
  var showFilters by remember { mutableStateOf(false) }

  // Quick Action Dialog States
  var issueToAssign by remember { mutableStateOf<Issue?>(null) }
  var issueToVerify by remember { mutableStateOf<Issue?>(null) }
  var issueToMerge by remember { mutableStateOf<Issue?>(null) }

  val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

  val filteredIssues = remember(allIssues, searchQuery, filterCategory, filterDepartment, filterStatus, filterSeverity, currentTab) {
    var list = when (currentTab) {
      AdminTab.ALL_COMPLAINTS -> allIssues
      AdminTab.DELAYED -> allIssues.filter { it.isDelayed || (it.deadline != null && System.currentTimeMillis() > it.deadline && it.status != IssueStatus.RESOLVED) }
      AdminTab.CONFIDENTIAL -> emptyList()
    }

    if (searchQuery.isNotBlank()) {
      list = list.filter {
        it.trackingId.contains(searchQuery, ignoreCase = true) ||
          it.title.contains(searchQuery, ignoreCase = true) ||
          it.location.contains(searchQuery, ignoreCase = true)
      }
    }
    if (filterCategory != null) list = list.filter { it.category == filterCategory }
    if (filterDepartment != null) list = list.filter { it.assignedDepartment == filterDepartment }
    if (filterStatus != null) list = list.filter { it.status == filterStatus }
    if (filterSeverity != null) list = list.filter { it.severity == filterSeverity }

    list.sortedByDescending { it.priorityScore }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
  ) {
    // Navigation Tabs
    TabRow(
      selectedTabIndex = currentTab.ordinal,
      containerColor = SurfaceCard,
      contentColor = Blue600
    ) {
      AdminTab.entries.forEach { tab ->
        Tab(
          selected = currentTab == tab,
          onClick = { currentTab = tab },
          text = {
            Text(
              text = if (tab == AdminTab.CONFIDENTIAL) "${tab.title} (${confidentialIssues.size})" else tab.title,
              fontSize = 12.sp,
              fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal
            )
          }
        )
      }
    }

    // Top Filter & Search Controls (for complaints and delayed)
    if (currentTab != AdminTab.CONFIDENTIAL) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SurfaceCard)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ID (CV-1001), Title, Location...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier
              .weight(1f)
              .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )

          IconButton(onClick = { showFilters = !showFilters }) {
            Icon(
              imageVector = if (showFilters) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
              contentDescription = "Filters",
              tint = if (showFilters) Blue600 else TextSecondary
            )
          }
        }

        AnimatedVisibility(visible = showFilters) {
          Column(modifier = Modifier.padding(top = 8.dp)) {
            // Category Filter
            Text("Filter by Department:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (filterDepartment == null) Blue600 else Blue50)
                  .clickable { filterDepartment = null }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("All Depts", fontSize = 10.sp, color = if (filterDepartment == null) Color.White else Blue600)
              }
              CampusDepartments.forEach { dept ->
                val isSel = filterDepartment == dept
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) Blue600 else Blue50)
                    .clickable { filterDepartment = if (isSel) null else dept }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(dept, fontSize = 10.sp, color = if (isSel) Color.White else Blue600)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Status Filter
            Text("Filter by Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              IssueStatus.entries.forEach { st ->
                val isSel = filterStatus == st
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) TealAccent else Color(0xFFF1F5F9))
                    .clickable { filterStatus = if (isSel) null else st }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(st.displayName, fontSize = 10.sp, color = if (isSel) Color.White else TextPrimary)
                }
              }
            }
          }
        }
      }
    }

    // Content Display
    when (currentTab) {
      AdminTab.CONFIDENTIAL -> {
        // Protected Confidential Section
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(confidentialIssues, key = { it.confidentialIssueId }) { conf ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceCard),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA)))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = conf.privateTrackingId,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = SeverityCritical
                    )
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(SeverityCriticalBg)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(text = conf.category.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SeverityCritical)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = conf.description,
                  fontSize = 13.sp,
                  color = Navy900,
                  lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Location: ${conf.location}", fontSize = 11.sp, color = TextSecondary)
                  Text(text = if (conf.anonymous) "Anonymous Student" else "Identified", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Assigned Officer: ${conf.assignedOfficer ?: "Dean of Student Welfare"}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Blue600
                )

                if (conf.internalNotes.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  conf.internalNotes.forEach { note ->
                    Text(text = "• $note", fontSize = 10.sp, color = TextSecondary)
                  }
                }
              }
            }
          }
        }
      }

      AdminTab.ALL_COMPLAINTS, AdminTab.DELAYED -> {
        if (filteredIssues.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No complaints matching filters", color = TextMuted)
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filteredIssues, key = { it.issueId }) { issue ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onOpenIssue(issue.issueId) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  // Top info
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = issue.trackingId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue600
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      PriorityBadge(priority = issue.priorityLabel, score = issue.priorityScore)
                    }
                    StatusBadge(status = issue.status)
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = issue.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = "📍 ${issue.location}", fontSize = 11.sp, color = TextSecondary)
                    Text(
                      text = "${issue.votes} Votes • ${issue.affectedStudents} Affected",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = TealAccent
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Dept: ${issue.assignedDepartment ?: "Unassigned"}",
                      fontSize = 11.sp,
                      color = if (issue.assignedDepartment != null) Blue600 else TextMuted,
                      fontWeight = FontWeight.Medium
                    )
                    if (issue.deadline != null) {
                      Text(
                        text = "Due: ${dateFormat.format(Date(issue.deadline))}",
                        fontSize = 10.sp,
                        color = if (issue.isDelayed) SeverityCritical else TextMuted,
                        fontWeight = if (issue.isDelayed) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))
                  Divider(color = SurfaceBorder)
                  Spacer(modifier = Modifier.height(8.dp))

                  // Quick Action Buttons on Admin Card
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    TextButton(
                      onClick = { issueToAssign = issue },
                      contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Assign", fontSize = 11.sp, color = Blue600, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(
                      onClick = { issueToVerify = issue },
                      contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Verify/Reject", fontSize = 11.sp, color = TealAccent, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(
                      onClick = { issueToMerge = issue },
                      contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Icon(Icons.Default.CallMerge, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Duplicate", fontSize = 11.sp, color = TextSecondary)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Quick Assign Dialog
  issueToAssign?.let { issue ->
    var chosenDept by remember { mutableStateOf(issue.assignedDepartment ?: CampusDepartments.first()) }
    var staffName by remember { mutableStateOf(issue.assignedStaffName ?: "Officer R. Sharma") }
    var deadlineDays by remember { mutableIntStateOf(2) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { issueToAssign = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Assign ${issue.trackingId}", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          Text("Department:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
          CampusDepartments.take(4).forEach { dept ->
            Row(verticalAlignment = Alignment.CenterVertically) {
              RadioButton(
                selected = chosenDept == dept,
                onClick = { chosenDept = dept },
                colors = RadioButtonDefaults.colors(selectedColor = Blue600)
              )
              Text(dept, fontSize = 12.sp, color = Navy900)
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = staffName,
            onValueChange = { staffName = it },
            label = { Text("Staff Member", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text("Target Deadline: $deadlineDays days", fontSize = 12.sp, color = Blue600, fontWeight = FontWeight.Bold)
          Slider(
            value = deadlineDays.toFloat(),
            onValueChange = { deadlineDays = it.toInt() },
            valueRange = 1f..7f,
            steps = 5,
            colors = SliderDefaults.colors(thumbColor = Blue600, activeTrackColor = Blue600)
          )
          OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Internal Instructions", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.assignIssue(issue.issueId, chosenDept, staffName, deadlineDays, note)
            issueToAssign = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Assign & Notify", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { issueToAssign = null }) { Text("Cancel", color = TextSecondary) }
      }
    )
  }

  // Quick Verify / Reject Dialog
  issueToVerify?.let { issue ->
    var rejectionReason by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { issueToVerify = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Review Complaint: ${issue.trackingId}", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          Text(text = issue.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
          Text(text = issue.description, fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = rejectionReason,
            onValueChange = { rejectionReason = it },
            label = { Text("Rejection Reason (if rejecting)", color = TextSecondary) },
            placeholder = { Text("e.g. Non-campus issue or outside college perimeter", color = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              repo.updateIssueStatus(issue.issueId, IssueStatus.ACCEPTED, "Admin verified and accepted complaint")
              issueToVerify = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Blue600)
          ) {
            Text("Verify & Accept", color = Color.White, fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = {
              repo.updateIssueStatus(
                issue.issueId,
                IssueStatus.REJECTED,
                rejectionReason.ifBlank { "Administrative verification failed or out of scope." }
              )
              issueToVerify = null
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SeverityCritical)
          ) {
            Text("Reject", fontWeight = FontWeight.Bold)
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { issueToVerify = null }) { Text("Cancel", color = TextSecondary) }
      }
    )
  }

  // Merge / Mark Duplicate Dialog
  issueToMerge?.let { issue ->
    val otherIssues = allIssues.filter { it.issueId != issue.issueId }
    var selectedCanonicalId by remember { mutableStateOf(otherIssues.firstOrNull()?.issueId ?: "") }

    AlertDialog(
      onDismissRequest = { issueToMerge = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Mark Duplicate & Merge", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          Text("Select the primary canonical complaint to merge ${issue.trackingId} into:", fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(8.dp))
          otherIssues.take(4).forEach { target ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedCanonicalId = target.issueId }
                .padding(vertical = 4.dp)
            ) {
              RadioButton(
                selected = selectedCanonicalId == target.issueId,
                onClick = { selectedCanonicalId = target.issueId },
                colors = RadioButtonDefaults.colors(selectedColor = Blue600)
              )
              Column {
                Text(text = "${target.trackingId}: ${target.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                Text(text = target.location, fontSize = 10.sp, color = TextMuted)
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.markAsDuplicate(issue.issueId, selectedCanonicalId)
            issueToMerge = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Confirm Merge", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { issueToMerge = null }) { Text("Cancel", color = TextSecondary) }
      }
    )
  }
}
