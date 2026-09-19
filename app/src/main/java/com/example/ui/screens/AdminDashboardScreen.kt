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
import com.example.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
  onNavigateToManagement: () -> Unit,
  onNavigateToAnalytics: () -> Unit,
  onNavigateToConfidential: () -> Unit,
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()
  val confidentialIssues by repo.confidentialIssues.collectAsState()

  val totalComplaints = allIssues.size
  val pendingComplaints = remember(allIssues) {
    allIssues.count { it.status != IssueStatus.RESOLVED && it.status != IssueStatus.REJECTED }
  }
  val criticalComplaints = remember(allIssues) {
    allIssues.count { it.severity == IssueSeverity.CRITICAL || it.priorityLabel == PriorityLabel.CRITICAL }
  }
  val highPriorityComplaints = remember(allIssues) {
    allIssues.count { it.priorityLabel == PriorityLabel.HIGH }
  }
  val resolvedComplaints = remember(allIssues) {
    allIssues.count { it.status == IssueStatus.RESOLVED }
  }
  val delayedComplaints = remember(allIssues) {
    allIssues.filter { it.isDelayed || (it.deadline != null && System.currentTimeMillis() > it.deadline && it.status != IssueStatus.RESOLVED) }
  }
  val confidentialCount = confidentialIssues.size

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Admin Header Banner with Custom Logo and Tagline
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Navy900),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E3A8A))
      ),
      modifier = Modifier.fillMaxWidth()
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
                text = "Dean & Administration Portal",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Lead: ${currentUser.name} • Master Control",
                fontSize = 11.sp,
                color = Color(0xFF93C5FD)
              )
            }
          }

          IconButton(onClick = onNavigateToAnalytics) {
            Icon(Icons.Default.Insights, contentDescription = "Analytics", tint = Color(0xFF38BDF8))
          }
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

    Spacer(modifier = Modifier.height(16.dp))

    // 7 Required Summary KPI Cards
    Text(
      text = "Complaint Overview & Status Summary",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = Navy900
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Row 1
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      StatCard(
        title = "Total Complaints",
        count = totalComplaints,
        icon = Icons.Default.AllInbox,
        accentColor = Blue600,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
      StatCard(
        title = "Pending Active",
        count = pendingComplaints,
        icon = Icons.Default.PendingActions,
        accentColor = Color(0xFFD97706),
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
      StatCard(
        title = "Resolved",
        count = resolvedComplaints,
        icon = Icons.Default.Verified,
        accentColor = TealAccent,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 2: Priority & Delay breakdown
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      StatCard(
        title = "Critical",
        count = criticalComplaints,
        icon = Icons.Default.PriorityHigh,
        accentColor = SeverityCritical,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
      StatCard(
        title = "High Priority",
        count = highPriorityComplaints,
        icon = Icons.Default.Bolt,
        accentColor = SeverityHigh,
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
      StatCard(
        title = "Delayed (SLA)",
        count = delayedComplaints.size,
        icon = Icons.Default.AlarmOff,
        accentColor = Color(0xFFDC2626),
        modifier = Modifier.weight(1f),
        onClick = onNavigateToManagement
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row 3: Confidential
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Confidential & Anti-Ragging Complaints", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SeverityCritical)
            Text("Restricted administrator view only ($confidentialCount report)", fontSize = 11.sp, color = TextSecondary)
          }
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SeverityCritical)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(text = "$confidentialCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Delayed Complaints Immediate Attention Section
    if (delayedComplaints.isNotEmpty()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = SeverityCritical, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Overdue & Delayed Complaints",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SeverityCritical
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))

      delayedComplaints.forEach { delayed ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA)))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${delayed.trackingId}: ${delayed.title}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                modifier = Modifier.weight(1f)
              )
              StatusBadge(status = delayed.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Assigned: ${delayed.assignedDepartment ?: "Unassigned"} • Past resolution deadline",
              fontSize = 11.sp,
              color = SeverityCritical
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              TextButton(onClick = { onOpenIssue(delayed.issueId) }) {
                Text("Inspect", fontSize = 11.sp, color = Blue600)
              }
              Spacer(modifier = Modifier.width(6.dp))
              Button(
                onClick = { repo.escalateIssue(delayed.issueId) },
                colors = ButtonDefaults.buttonColors(containerColor = SeverityCritical),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp),
                shape = RoundedCornerShape(6.dp)
              ) {
                Icon(Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Escalate to Principal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Quick Action Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = onNavigateToManagement,
        modifier = Modifier
          .weight(1f)
          .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue600)
      ) {
        Icon(Icons.Default.ViewList, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Manage Complaints Table", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = onNavigateToAnalytics,
        modifier = Modifier
          .weight(1f)
          .height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
      ) {
        Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Campus Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
