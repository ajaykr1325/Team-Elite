package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.Issue
import com.example.model.IssueStatus
import com.example.ui.components.ComplaintTimeline
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyComplaintsScreen(
  onOpenIssue: (String) -> Unit,
  onNavigateToSubmit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()

  // Only logged in student's complaints
  val myIssues = remember(allIssues, currentUser) {
    allIssues.filter { it.reporterId == currentUser.userId }
  }

  var selectedFeedbackIssue by remember { mutableStateOf<Issue?>(null) }
  var selectedReopenIssue by remember { mutableStateOf<Issue?>(null) }
  var feedbackRating by remember { mutableIntStateOf(5) }
  var feedbackComment by remember { mutableStateOf("") }
  var reopenReason by remember { mutableStateOf("") }

  val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceCard)
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "My Reported Issues",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Navy900
        )
        Text(
          text = "Logged in as ${currentUser.name} (${myIssues.size} submitted)",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }

      Button(
        onClick = onNavigateToSubmit,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.height(34.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Report New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    if (myIssues.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.AssignmentLate,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("No Complaints Submitted Yet", fontWeight = FontWeight.Bold, color = Navy900)
          Text(
            text = "Have an issue on campus? Report it to administration.",
            fontSize = 12.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onNavigateToSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
          ) {
            Text("Create First Complaint")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(myIssues, key = { it.issueId }) { issue ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenIssue(issue.issueId) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Top Bar
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = issue.trackingId,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  PriorityBadge(priority = issue.priorityLabel)
                }

                StatusBadge(status = issue.status)
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = issue.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900
              )

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "📍 ${issue.location}",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
                Text(
                  text = "Dept: ${issue.assignedDepartment ?: "Pending Assignment"}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (issue.assignedDepartment != null) TealAccent else TextMuted
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Visual Progress Timeline
              ComplaintTimeline(issue = issue)

              Spacer(modifier = Modifier.height(12.dp))
              Divider(color = SurfaceBorder)
              Spacer(modifier = Modifier.height(8.dp))

              // Actions row for Resolved/Reopen
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Updated: ${dateFormat.format(Date(issue.updatedAt))}",
                  fontSize = 10.sp,
                  color = TextMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  if (issue.status == IssueStatus.RESOLVED) {
                    if (issue.feedback == null) {
                      Button(
                        onClick = { selectedFeedbackIssue = issue },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate Resolution", fontSize = 11.sp)
                      }
                    } else {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Text(
                          text = " ${issue.feedback.rating}/5 Rated",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = TextSecondary
                        )
                      }
                    }

                    OutlinedButton(
                      onClick = { selectedReopenIssue = issue },
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = SeverityHigh),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                      modifier = Modifier.height(28.dp)
                    ) {
                      Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Reopen Issue", fontSize = 11.sp)
                    }
                  } else {
                    TextButton(
                      onClick = { onOpenIssue(issue.issueId) },
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Text("View Details ➔", fontSize = 11.sp, color = Blue600, fontWeight = FontWeight.Bold)
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

  // Feedback Dialog
  selectedFeedbackIssue?.let { issue ->
    AlertDialog(
      onDismissRequest = { selectedFeedbackIssue = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Resolution Feedback", fontWeight = FontWeight.Bold, color = Navy900) },
      text = {
        Column {
          Text(
            text = "Was the issue (${issue.trackingId}) resolved to your satisfaction?",
            fontSize = 13.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(12.dp))

          // Star rating
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            (1..5).forEach { star ->
              IconButton(onClick = { feedbackRating = star }) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = "$star stars",
                  tint = if (star <= feedbackRating) Color(0xFFF59E0B) else SurfaceBorder,
                  modifier = Modifier.size(32.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = feedbackComment,
            onValueChange = { feedbackComment = it },
            label = { Text("Feedback Note", color = TextSecondary) },
            placeholder = { Text("e.g. Excellent quick repair, technician tested water...", color = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            repo.submitFeedback(issue.issueId, feedbackRating, feedbackComment)
            selectedFeedbackIssue = null
            feedbackComment = ""
          },
          colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
          Text("Submit Feedback", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedFeedbackIssue = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }

  // Reopen Dialog
  selectedReopenIssue?.let { issue ->
    AlertDialog(
      onDismissRequest = { selectedReopenIssue = null },
      containerColor = SurfaceCard,
      titleContentColor = Navy900,
      textContentColor = TextSecondary,
      title = { Text("Reopen Complaint", fontWeight = FontWeight.Bold, color = SeverityHigh) },
      text = {
        Column {
          Text(
            text = "Explain why this problem has not actually been solved on campus:",
            fontSize = 13.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = reopenReason,
            onValueChange = { reopenReason = it },
            label = { Text("Reason for Reopening *", color = TextSecondary) },
            placeholder = { Text("e.g. The leak stopped but water is still not cooling down...", color = TextMuted) },
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reopenReason.isNotBlank()) {
              repo.reopenIssue(issue.issueId, reopenReason)
              selectedReopenIssue = null
              reopenReason = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SeverityHigh)
        ) {
          Text("Confirm Reopen", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedReopenIssue = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
