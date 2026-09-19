package com.example.ui.screens

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CampusVoiceRepository
import com.example.model.*
import com.example.ui.components.ComplaintTimeline
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SeverityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailDialog(
  issueId: String,
  onDismiss: () -> Unit
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()

  val issue = allIssues.find { it.issueId == issueId } ?: return

  var showAssignSheet by remember { mutableStateOf(false) }
  var showStatusSheet by remember { mutableStateOf(false) }
  var showStaffResolveSheet by remember { mutableStateOf(false) }

  val dateFormat = remember { SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(16.dp)),
      color = CanvasBackground
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = issue.trackingId,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Blue600
            )
            Spacer(modifier = Modifier.width(8.dp))
            PriorityBadge(priority = issue.priorityLabel, score = issue.priorityScore)
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Divider(color = SurfaceBorder)

        // Scrollable Body
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
        ) {
          // Status & Category
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusBadge(status = issue.status)
            SeverityBadge(severity = issue.severity)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Title
          Text(
            text = issue.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Metadata Grid
          Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder(),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Category:", fontSize = 11.sp, color = TextMuted)
                Text(text = issue.category.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Location:", fontSize = 11.sp, color = TextMuted)
                Text(text = issue.location, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Affected Students:", fontSize = 11.sp, color = TextMuted)
                Text(text = "${issue.affectedStudents} students", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealAccent)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Upvotes:", fontSize = 11.sp, color = TextMuted)
                Text(text = "${issue.votes} upvotes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Blue600)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Reported By:", fontSize = 11.sp, color = TextMuted)
                Text(
                  text = if (issue.anonymous) "Anonymous Student" else issue.reporterName,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextSecondary
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Assigned Dept:", fontSize = 11.sp, color = TextMuted)
                Text(
                  text = "${issue.assignedDepartment ?: "Unassigned"}${if (issue.assignedStaffName != null) " (${issue.assignedStaffName})" else ""}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (issue.assignedDepartment != null) TealAccent else TextMuted
                )
              }
              if (issue.deadline != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "Deadline:", fontSize = 11.sp, color = TextMuted)
                  Text(
                    text = dateFormat.format(Date(issue.deadline)),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (issue.isDelayed) SeverityCritical else Navy900
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Description
          Text(text = "Description", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = issue.description,
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp
          )

          // Evidence preview if attached
          if (issue.imageUrl != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              colors = CardDefaults.cardColors(containerColor = Blue50),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Blue600)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Attached Evidence Proof", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
                  Text(issue.imageUrl, fontSize = 10.sp, color = TextSecondary)
                }
              }
            }
          }

          // Resolution Proof Card if resolved
          if (issue.status == IssueStatus.RESOLVED && issue.resolutionNote != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBF7D0))),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Verified, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Resolution Proof & Verification", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = issue.resolutionNote, fontSize = 12.sp, color = Color(0xFF15803D))
                if (issue.resolutionProofUrl != null) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(text = "Proof: ${issue.resolutionProofUrl}", fontSize = 11.sp, color = TextMuted)
                }
              }
            }
          }

          // Student Feedback Card if submitted
          if (issue.feedback != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Student Feedback: ${issue.feedback.rating}/5 Stars", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "\"${issue.feedback.comment}\"", fontSize = 11.sp, color = Color(0xFF78350F))
              }
            }
          }

          // Internal Notes (Admins & Staff)
          if ((currentUser.role == UserRole.ADMINISTRATOR || currentUser.role == UserRole.STAFF) && issue.internalNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Internal Administrative Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            issue.internalNotes.forEach { note ->
              Text(text = "• $note", fontSize = 11.sp, color = TextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Full Stepped Visual Progress Timeline
          ComplaintTimeline(issue = issue)

          Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Action Bar
        Divider(color = SurfaceBorder)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          when (currentUser.role) {
            UserRole.ADMINISTRATOR -> {
              Button(
                onClick = { showAssignSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Assign / Deadline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { showStatusSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Update Status", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              if (issue.status != IssueStatus.ESCALATED) {
                OutlinedButton(
                  onClick = { repo.escalateIssue(issue.issueId) },
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = SeverityCritical),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Escalate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            UserRole.STAFF -> {
              if (issue.status == IssueStatus.ASSIGNED) {
                Button(
                  onClick = {
                    repo.updateIssueStatus(
                      issue.issueId,
                      IssueStatus.IN_PROGRESS,
                      "Work initiated by ${currentUser.name}"
                    )
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Accept & Begin Repair", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              } else if (issue.status == IssueStatus.IN_PROGRESS) {
                Button(
                  onClick = { showStaffResolveSheet = true },
                  colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Post Proof & Resolve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            UserRole.STUDENT -> {
              val hasVoted = issue.voterStudentIds.contains(currentUser.userId)
              Button(
                onClick = { repo.toggleUpvote(issue.issueId, currentUser.userId) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (hasVoted) TealAccent else Blue600
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(
                  imageVector = if (hasVoted) Icons.Default.ThumbUp else Icons.Default.ThumbUpAlt,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (hasVoted) "Upvoted (${issue.votes})" else "Upvote Issue (${issue.votes})",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }

  // Admin Assignment Dialog
  if (showAssignSheet) {
    var selectedDept by remember { mutableStateOf(CampusDepartments.first()) }
    var staffName by remember { mutableStateOf("Officer R. Sharma") }
    var deadlineDays by remember { mutableIntStateOf(2) }
    var internalNote by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAssignSheet = false },
      title = { Text("Assign Department & Deadline", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(text = "Department", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
          CampusDepartments.take(4).forEach { dept ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              RadioButton(
                selected = selectedDept == dept,
                onClick = { selectedDept = dept },
                colors = RadioButtonDefaults.colors(selectedColor = Blue600)
              )
              Text(text = dept, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = staffName,
            onValueChange = { staffName = it },
            label = { Text("Assigned Officer / Staff", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Resolution Deadline: $deadlineDays Day(s)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Blue600)
          Slider(
            value = deadlineDays.toFloat(),
            onValueChange = { deadlineDays = it.toInt() },
            valueRange = 1f..7f,
            steps = 5,
            colors = SliderDefaults.colors(thumbColor = Blue600, activeTrackColor = Blue600)
          )

          OutlinedTextField(
            value = internalNote,
            onValueChange = { internalNote = it },
            label = { Text("Internal Instructions", color = TextSecondary) },
            placeholder = { Text("e.g. Expedite inspection before 3 PM", color = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.assignIssue(issue.issueId, selectedDept, staffName, deadlineDays, internalNote)
            showAssignSheet = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Confirm Assignment", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAssignSheet = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }

  // Admin Status Change Dialog
  if (showStatusSheet) {
    var newStatus by remember { mutableStateOf(IssueStatus.IN_PROGRESS) }
    var updateNote by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showStatusSheet = false },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Change Complaint Status", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          IssueStatus.entries.forEach { status ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              RadioButton(
                selected = newStatus == status,
                onClick = { newStatus = status },
                colors = RadioButtonDefaults.colors(selectedColor = Blue600)
              )
              Text(text = status.displayName, fontSize = 12.sp, color = Navy900)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = updateNote,
            onValueChange = { updateNote = it },
            label = { Text("Status Update Note", color = TextSecondary) },
            placeholder = { Text("e.g. Maintenance inspection in progress", color = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.updateIssueStatus(issue.issueId, newStatus, updateNote)
            showStatusSheet = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Update Status", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showStatusSheet = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }

  // Staff Progress / Resolve Dialog
  if (showStaffResolveSheet) {
    var progressNote by remember { mutableStateOf("Replaced damaged components and completed functional test.") }
    var proofDescription by remember { mutableStateOf("Work complete. Facility verified operational.") }
    var markResolved by remember { mutableStateOf(true) }

    AlertDialog(
      onDismissRequest = { showStaffResolveSheet = false },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Submit Resolution Proof", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          OutlinedTextField(
            value = progressNote,
            onValueChange = { progressNote = it },
            label = { Text("Work Completed Note *", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = proofDescription,
            onValueChange = { proofDescription = it },
            label = { Text("Resolution Verification Description", color = TextSecondary) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
              checked = markResolved,
              onCheckedChange = { markResolved = it },
              colors = CheckboxDefaults.colors(checkedColor = Blue600)
            )
            Text(text = "Mark Complaint as Fully Resolved", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Blue600)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.staffUpdateProgress(
              issueId = issue.issueId,
              progressNote = progressNote,
              resolutionDescription = proofDescription,
              resolutionProofUrl = "proof_verified_${issue.trackingId.lowercase()}.jpg",
              markResolved = markResolved
            )
            showStaffResolveSheet = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Submit to Administration", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showStaffResolveSheet = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
