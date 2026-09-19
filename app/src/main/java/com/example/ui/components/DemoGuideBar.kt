package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import com.example.model.DemoAccounts
import com.example.model.IssueCategory
import com.example.model.IssueSeverity
import com.example.ui.theme.*

@Composable
fun DemoGuideBar(
  onNavigateTo: (String) -> Unit,
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  var isExpanded by remember { mutableStateOf(false) }
  var currentDemoStep by remember { mutableIntStateOf(1) }

  val demoSteps = listOf(
    "1. Student Login",
    "2. Submit 'Water cooler Block B'",
    "3. Gen CV-1024",
    "4. Public Board",
    "5. Upvote as Peer",
    "6. Admin: Priority Jump",
    "7. Assign Maintenance",
    "8. Set Deadline",
    "9. Staff Login",
    "10. In Progress",
    "11. Resolution Note",
    "12. Mark Resolved",
    "13. Original Student",
    "14. Updated Timeline",
    "15. Submit Feedback"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Navy900)
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { isExpanded = !isExpanded }
      ) {
        Icon(
          imageVector = Icons.Default.PlayCircleFilled,
          contentDescription = null,
          tint = Teal500,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Hackathon Demo Flow: Step $currentDemoStep of 15",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = Color(0xFF93C5FD),
          modifier = Modifier.size(16.dp)
        )
      }

      // Quick Step Trigger Button
      Button(
        onClick = {
          when (currentDemoStep) {
            1 -> {
              repo.switchUser(DemoAccounts.studentAlex)
              onNavigateTo("student_dashboard")
              currentDemoStep = 2
            }
            2, 3 -> {
              repo.switchUser(DemoAccounts.studentAlex)
              // Auto pre-populate or trigger demo submission CV-1024
              val created = repo.submitIssue(
                title = "Water cooler not working in Block B",
                category = IssueCategory.WATER_SUPPLY,
                location = "Block B, Ground Floor Hallway",
                description = "Water cooler is dispensing room temperature water and leaking on corridor floor.",
                severity = IssueSeverity.HIGH,
                affectedStudents = 85,
                anonymous = false,
                imageUrl = null,
                forcedTrackingId = "CV-1024"
              )
              onOpenIssue(created.issueId)
              currentDemoStep = 4
            }
            4 -> {
              onNavigateTo("public_board")
              currentDemoStep = 5
            }
            5 -> {
              // Switch to student Priya to upvote CV-1024
              repo.switchUser(DemoAccounts.studentPriya)
              val cv1024 = repo.issues.value.find { it.trackingId == "CV-1024" }
              if (cv1024 != null) {
                repo.toggleUpvote(cv1024.issueId, DemoAccounts.studentPriya.userId)
              }
              onNavigateTo("public_board")
              currentDemoStep = 6
            }
            6 -> {
              repo.switchUser(DemoAccounts.adminDean)
              onNavigateTo("admin_management")
              currentDemoStep = 7
            }
            7, 8 -> {
              repo.switchUser(DemoAccounts.adminDean)
              val cv1024 = repo.issues.value.find { it.trackingId == "CV-1024" }
              if (cv1024 != null) {
                repo.assignIssue(
                  issueId = cv1024.issueId,
                  department = "Maintenance",
                  staffName = DemoAccounts.staffMaintenance.name,
                  deadlineDays = 2,
                  internalNote = "Urgent sanitation priority. Replace cooling coil."
                )
                onOpenIssue(cv1024.issueId)
              }
              currentDemoStep = 9
            }
            9, 10 -> {
              repo.switchUser(DemoAccounts.staffMaintenance)
              onNavigateTo("staff_dashboard")
              currentDemoStep = 11
            }
            11, 12 -> {
              repo.switchUser(DemoAccounts.staffMaintenance)
              val cv1024 = repo.issues.value.find { it.trackingId == "CV-1024" }
              if (cv1024 != null) {
                repo.staffUpdateProgress(
                  issueId = cv1024.issueId,
                  progressNote = "Replaced thermostat and fixed drain tray.",
                  resolutionDescription = "Cooler running at 8°C. Slip hazard eliminated.",
                  resolutionProofUrl = "proof_cooler_fixed.jpg",
                  markResolved = true
                )
                onOpenIssue(cv1024.issueId)
              }
              currentDemoStep = 13
            }
            13, 14 -> {
              repo.switchUser(DemoAccounts.studentAlex)
              onNavigateTo("my_complaints")
              val cv1024 = repo.issues.value.find { it.trackingId == "CV-1024" }
              if (cv1024 != null) {
                onOpenIssue(cv1024.issueId)
              }
              currentDemoStep = 15
            }
            15 -> {
              val cv1024 = repo.issues.value.find { it.trackingId == "CV-1024" }
              if (cv1024 != null) {
                repo.submitFeedback(
                  issueId = cv1024.issueId,
                  rating = 5,
                  comment = "Verified! Cold water working well and puddle cleaned up promptly. Great work!"
                )
                onOpenIssue(cv1024.issueId)
              }
              currentDemoStep = 1
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Teal500),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        modifier = Modifier.height(26.dp)
      ) {
        Text(
          text = if (currentDemoStep == 15) "Complete & Reset" else "Next Step ➔",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = NavyDark
        )
      }
    }

    AnimatedVisibility(visible = isExpanded) {
      Column(modifier = Modifier.padding(top = 6.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          demoSteps.forEachIndexed { idx, label ->
            val stepNum = idx + 1
            val isActive = currentDemoStep == stepNum
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isActive) TealAccent else Navy800)
                .border(0.5.dp, if (isActive) Color.White else Navy700, RoundedCornerShape(4.dp))
                .clickable { currentDemoStep = stepNum }
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) Color.White else Color(0xFFCBD5E1)
              )
            }
          }
        }
      }
    }
  }
}
