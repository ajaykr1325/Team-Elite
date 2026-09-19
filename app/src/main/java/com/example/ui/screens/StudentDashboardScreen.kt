package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CampusVoiceRepository
import com.example.model.DemoAccounts
import com.example.model.IssueStatus
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun StudentDashboardScreen(
  onNavigateToReport: () -> Unit,
  onNavigateToPublicBoard: () -> Unit,
  onNavigateToMyComplaints: () -> Unit,
  onNavigateToConfidential: () -> Unit,
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()
  val allNotifs by repo.notifications.collectAsState()

  val myIssues = remember(allIssues, currentUser.userId) {
    allIssues.filter { it.reporterId == currentUser.userId }
  }

  val totalSubmitted = myIssues.size
  val pendingCount = remember(myIssues) {
    myIssues.count { it.status != IssueStatus.RESOLVED && it.status != IssueStatus.REJECTED }
  }
  val resolvedCount = remember(myIssues) {
    myIssues.count { it.status == IssueStatus.RESOLVED }
  }
  val userNotifs = remember(allNotifs, currentUser.userId) {
    allNotifs.filter { it.userId == currentUser.userId }
  }
  val unreadNotifs = remember(userNotifs) {
    userNotifs.count { !it.read }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Student Greeting Card with Custom App Logo and Tagline
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Navy900),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E3A8A))
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(modifier = Modifier.padding(16.dp)) {
        Column {
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
                  .size(46.dp)
                  .clip(CircleShape)
                  .border(2.dp, Color(0xFFD4AF37), CircleShape),
                contentScale = ContentScale.Crop
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = currentUser.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    color = Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "STUDENT",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF93C5FD),
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "${currentUser.year ?: "Student Representative"} • ${currentUser.email}",
                  fontSize = 11.sp,
                  color = Color(0xFF93C5FD)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Tagline highlighted in Card
          Surface(
            color = Navy800.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Campaign,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "A platform where student voices shape campus change",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // KPI Metrics Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatCard(
        title = "Submitted",
        count = totalSubmitted,
        icon = Icons.Default.Send,
        accentColor = Blue600,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToMyComplaints
      )
      StatCard(
        title = "In Progress",
        count = pendingCount,
        icon = Icons.Default.PendingActions,
        accentColor = Color(0xFFD97706),
        modifier = Modifier.weight(1f),
        onClick = onNavigateToMyComplaints
      )
      StatCard(
        title = "Resolved",
        count = resolvedCount,
        icon = Icons.Default.Verified,
        accentColor = TealAccent,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToMyComplaints
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Action CTA Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onNavigateToReport,
        modifier = Modifier
          .weight(1f)
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Navy900),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Report an Issue", fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = onNavigateToPublicBoard,
        modifier = Modifier
          .weight(1f)
          .height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Navy900),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Navy900)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "View Campus Issues", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Confidential Report Callout
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onNavigateToConfidential() },
      shape = RoundedCornerShape(10.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA)))
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text("Confidential Reporting Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SeverityCritical)
          Text("Report ragging, harassment, discrimination or personal safety with anonymity.", fontSize = 11.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SeverityCritical)
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Notifications Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Notifications & Live Updates",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Navy900
        )
        if (unreadNotifs > 0) {
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(SeverityCritical)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(text = "$unreadNotifs new", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      if (userNotifs.isNotEmpty()) {
        TextButton(onClick = { repo.markAllNotificationsRead() }) {
          Text("Mark all read", fontSize = 11.sp, color = TealAccent)
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    if (userNotifs.isEmpty()) {
      Text(
        text = "No updates at this time. When administrative actions take place, you will be notified here.",
        fontSize = 12.sp,
        color = TextMuted
      )
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        userNotifs.take(3).forEach { notif ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                notif.issueId?.let { onOpenIssue(it) }
              },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (notif.read) TextMuted else Blue600)
                  .padding(top = 4.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = notif.title,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = notif.message,
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Trending Campus Issues
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Trending on Campus",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Navy900
      )
      TextButton(onClick = onNavigateToPublicBoard) {
        Text("See all", fontSize = 11.sp, color = Blue600)
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    val trending = allIssues.sortedByDescending { it.votes }.take(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      trending.forEach { issue ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenIssue(issue.issueId) },
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = issue.trackingId,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Blue600
                )
                Spacer(modifier = Modifier.width(6.dp))
                PriorityBadge(priority = issue.priorityLabel)
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = issue.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                maxLines = 1
              )
              Text(
                text = "${issue.location} • ${issue.votes} upvotes",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
            StatusBadge(status = issue.status)
          }
        }
      }
    }
  }
}
