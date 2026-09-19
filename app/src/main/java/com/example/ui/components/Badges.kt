package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IssueSeverity
import com.example.model.IssueStatus
import com.example.model.PriorityLabel
import com.example.ui.theme.*

@Composable
fun StatusBadge(status: IssueStatus, modifier: Modifier = Modifier) {
  val (bgColor, textColor, icon) = when (status) {
    IssueStatus.SUBMITTED -> Triple(Color(0xFFF1F5F9), StatusSubmitted, Icons.Default.Send)
    IssueStatus.UNDER_REVIEW -> Triple(Color(0xFFFEF3C7), StatusUnderReview, Icons.Default.FindInPage)
    IssueStatus.ACCEPTED -> Triple(Color(0xFFDBEAFE), StatusAccepted, Icons.Default.CheckCircleOutline)
    IssueStatus.ASSIGNED -> Triple(Color(0xFFE0E7FF), StatusAssigned, Icons.Default.AssignmentInd)
    IssueStatus.IN_PROGRESS -> Triple(Color(0xFFCCFBF1), StatusInProgress, Icons.Default.Engineering)
    IssueStatus.RESOLVED -> Triple(Color(0xFFD1FAE5), StatusResolved, Icons.Default.Verified)
    IssueStatus.REOPENED -> Triple(Color(0xFFFFEDD5), StatusReopened, Icons.Default.Refresh)
    IssueStatus.ESCALATED -> Triple(Color(0xFFFEE2E2), StatusEscalated, Icons.Default.Warning)
    IssueStatus.REJECTED -> Triple(Color(0xFFF3F4F6), StatusRejected, Icons.Default.Cancel)
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = textColor,
      modifier = Modifier.size(13.dp)
    )
    Text(
      text = " " + status.displayName,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun SeverityBadge(severity: IssueSeverity, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (severity) {
    IssueSeverity.CRITICAL -> Pair(SeverityCriticalBg, SeverityCritical)
    IssueSeverity.HIGH -> Pair(SeverityHighBg, SeverityHigh)
    IssueSeverity.MEDIUM -> Pair(SeverityMediumBg, SeverityMedium)
    IssueSeverity.LOW -> Pair(SeverityLowBg, SeverityLow)
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 7.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(textColor)
    )
    Text(
      text = " ${severity.displayName}",
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun PriorityBadge(priority: PriorityLabel, score: Int? = null, modifier: Modifier = Modifier) {
  val (bgColor, textColor) = when (priority) {
    PriorityLabel.CRITICAL -> Pair(SeverityCriticalBg, SeverityCritical)
    PriorityLabel.HIGH -> Pair(SeverityHighBg, SeverityHigh)
    PriorityLabel.MEDIUM -> Pair(SeverityMediumBg, SeverityMedium)
    PriorityLabel.LOW -> Pair(SeverityLowBg, SeverityLow)
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 7.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Bolt,
      contentDescription = null,
      tint = textColor,
      modifier = Modifier.size(13.dp)
    )
    Text(
      text = " ${priority.displayName}${if (score != null) " ($score)" else ""}",
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
