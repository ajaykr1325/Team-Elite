package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CampusVoiceRepository
import com.example.model.Issue
import com.example.model.IssueStatus
import com.example.ui.components.ComplaintTimeline
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class StaffStatusFilter(val displayName: String) {
  ALL("All Assigned"),
  PENDING("Pending / Assigned"),
  IN_PROGRESS("In Progress"),
  RESOLVED("Resolved")
}

@Composable
fun StaffDashboardScreen(
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()

  // Filter complaints assigned to this department or staff
  val departmentIssues = remember(allIssues, currentUser) {
    allIssues.filter {
      it.assignedDepartment?.contains(currentUser.department ?: "", ignoreCase = true) == true ||
        it.assignedStaffName?.contains(currentUser.name, ignoreCase = true) == true
    }
  }

  var selectedFilter by remember { mutableStateOf(StaffStatusFilter.ALL) }
  var issueToWorkOn by remember { mutableStateOf<Issue?>(null) }
  var issueToResolve by remember { mutableStateOf<Issue?>(null) }

  val assignedCount = departmentIssues.size
  val inProgressCount = departmentIssues.count { it.status == IssueStatus.IN_PROGRESS }
  val resolvedCount = departmentIssues.count { it.status == IssueStatus.RESOLVED }

  val displayedIssues = remember(departmentIssues, selectedFilter) {
    when (selectedFilter) {
      StaffStatusFilter.ALL -> departmentIssues
      StaffStatusFilter.PENDING -> departmentIssues.filter { it.status == IssueStatus.ASSIGNED }
      StaffStatusFilter.IN_PROGRESS -> departmentIssues.filter { it.status == IssueStatus.IN_PROGRESS }
      StaffStatusFilter.RESOLVED -> departmentIssues.filter { it.status == IssueStatus.RESOLVED }
    }
  }

  val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
  ) {
    // Staff Header Banner with Custom Logo and Tagline
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Navy900),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E3A8A))
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Image(
              painter = painterResource(id = R.drawable.campus_voice_logo),
              contentDescription = "CampusVoice Logo",
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFD4AF37), CircleShape),
              contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "${currentUser.department ?: "Maintenance"} Department",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Staff Officer: ${currentUser.name}",
                fontSize = 11.sp,
                color = Color(0xFF93C5FD)
              )
            }
          }

          Icon(
            imageVector = Icons.Default.Engineering,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          color = Navy800.copy(alpha = 0.7f),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "A platform where student voices shape campus change",
              fontSize = 11.sp,
              color = Color.White
            )
          }
        }
      }
    }

    // KPI Stat row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      StatCard(
        title = "Assigned",
        count = assignedCount,
        icon = Icons.Default.Assignment,
        accentColor = Blue600,
        modifier = Modifier.weight(1f),
        onClick = { selectedFilter = StaffStatusFilter.ALL }
      )
      StatCard(
        title = "In Progress",
        count = inProgressCount,
        icon = Icons.Default.PlayCircleFilled,
        accentColor = Color(0xFFD97706),
        modifier = Modifier.weight(1f),
        onClick = { selectedFilter = StaffStatusFilter.IN_PROGRESS }
      )
      StatCard(
        title = "Resolved",
        count = resolvedCount,
        icon = Icons.Default.Verified,
        accentColor = TealAccent,
        modifier = Modifier.weight(1f),
        onClick = { selectedFilter = StaffStatusFilter.RESOLVED }
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      StaffStatusFilter.entries.forEach { filter ->
        val isSel = selectedFilter == filter
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSel) Blue600 else SurfaceCard)
            .border(1.dp, if (isSel) Blue600 else SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { selectedFilter = filter }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = filter.displayName,
            fontSize = 11.sp,
            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
            color = if (isSel) Color.White else TextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // List of Department Assigned Issues
    if (displayedIssues.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = TealAccent, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No issues in this queue", fontWeight = FontWeight.Bold, color = Navy900)
          Text("Great job! All assigned tasks are up to date.", fontSize = 12.sp, color = TextMuted)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(displayedIssues, key = { it.issueId }) { issue ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenIssue(issue.issueId) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = issue.trackingId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Blue600)
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

              Text(
                text = "📍 ${issue.location} • ${issue.affectedStudents} students impacted",
                fontSize = 11.sp,
                color = TextSecondary
              )

              if (issue.deadline != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (issue.isDelayed) SeverityCritical else TextMuted,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Deadline: ${dateFormat.format(Date(issue.deadline))}${if (issue.isDelayed) " (OVERDUE)" else ""}",
                    fontSize = 11.sp,
                    fontWeight = if (issue.isDelayed) FontWeight.Bold else FontWeight.Normal,
                    color = if (issue.isDelayed) SeverityCritical else TextMuted
                  )
                }
              }

              // Visual Stepped Timeline
              Spacer(modifier = Modifier.height(10.dp))
              ComplaintTimeline(issue = issue)

              Spacer(modifier = Modifier.height(10.dp))
              Divider(color = SurfaceBorder)
              Spacer(modifier = Modifier.height(8.dp))

              // Staff Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                TextButton(onClick = { onOpenIssue(issue.issueId) }) {
                  Text("Details", fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (issue.status == IssueStatus.ASSIGNED) {
                  Button(
                    onClick = {
                      repo.updateIssueStatus(
                        issue.issueId,
                        IssueStatus.IN_PROGRESS,
                        "Staff accepted task and initiated repairs"
                      )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Work", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                } else if (issue.status == IssueStatus.IN_PROGRESS) {
                  Button(
                    onClick = { issueToResolve = issue },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Submit Proof & Resolve", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Staff Submit Proof & Resolve Dialog
  issueToResolve?.let { issue ->
    var note by remember { mutableStateOf("Replaced damaged fittings, tested water pressure and cooling efficiency. Verified operational.") }
    var proofImage by remember { mutableStateOf("resolution_proof_${issue.trackingId.lowercase()}.jpg") }
    var estimatedHours by remember { mutableStateOf("1.5") }

    AlertDialog(
      onDismissRequest = { issueToResolve = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Complete Resolution: ${issue.trackingId}", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Resolution Notes *", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = proofImage,
            onValueChange = { proofImage = it },
            label = { Text("Resolution Proof Image Filename *", color = TextSecondary) },
            trailingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Blue600) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = estimatedHours,
            onValueChange = { estimatedHours = it },
            label = { Text("Actual Completion Time (Hours)", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.staffUpdateProgress(
              issueId = issue.issueId,
              progressNote = "Resolution verified by ${currentUser.name}. Labor time: $estimatedHours hrs.",
              resolutionDescription = note,
              resolutionProofUrl = proofImage,
              markResolved = true
            )
            issueToResolve = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Mark Resolved & Submit", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { issueToResolve = null }) { Text("Cancel", color = TextSecondary) }
      }
    )
  }
}
