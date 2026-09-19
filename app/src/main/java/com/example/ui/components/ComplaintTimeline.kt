package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Issue
import com.example.model.IssueStatus
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComplaintTimeline(issue: Issue, modifier: Modifier = Modifier) {
  val isDelayedOrEscalated = issue.isDelayed || issue.status == IssueStatus.ESCALATED

  val standardStages = listOf(
    Pair("Submitted", IssueStatus.SUBMITTED),
    Pair("Under Review", IssueStatus.UNDER_REVIEW),
    Pair("Assigned", IssueStatus.ASSIGNED),
    Pair("In Progress", IssueStatus.IN_PROGRESS),
    Pair("Resolved", IssueStatus.RESOLVED)
  )

  val delayedStages = listOf(
    Pair("Assigned", IssueStatus.ASSIGNED),
    Pair("Delayed", IssueStatus.IN_PROGRESS), // with delay indicator
    Pair("Escalated", IssueStatus.ESCALATED)
  )

  val stages = if (isDelayedOrEscalated) delayedStages else standardStages

  val currentStepIndex = when (issue.status) {
    IssueStatus.SUBMITTED -> 0
    IssueStatus.UNDER_REVIEW -> 1
    IssueStatus.ACCEPTED -> 1
    IssueStatus.ASSIGNED -> if (isDelayedOrEscalated) 0 else 2
    IssueStatus.IN_PROGRESS -> if (isDelayedOrEscalated) 1 else 3
    IssueStatus.RESOLVED -> if (isDelayedOrEscalated) 2 else 4
    IssueStatus.REOPENED -> if (isDelayedOrEscalated) 1 else 3
    IssueStatus.ESCALATED -> 2
    IssueStatus.REJECTED -> 1
  }

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "Resolution Timeline",
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Bold,
      color = Navy900
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Horizontal stepped indicator
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      stages.forEachIndexed { index, (label, status) ->
        val isPassed = index < currentStepIndex
        val isCurrent = index == currentStepIndex
        val isDelayedStep = isDelayedOrEscalated && (index >= 1)

        val circleBg = when {
          isCurrent && isDelayedStep -> SeverityCritical
          isCurrent -> Blue600
          isPassed -> TealAccent
          else -> SurfaceBorder
        }

        val circleTint = if (isPassed || isCurrent) Color.White else TextMuted

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(58.dp)
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(circleBg),
            contentAlignment = Alignment.Center
          ) {
            if (isPassed) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = circleTint,
                modifier = Modifier.size(16.dp)
              )
            } else if (isCurrent && isDelayedStep) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = circleTint,
                modifier = Modifier.size(16.dp)
              )
            } else {
              Text(
                text = "${index + 1}",
                color = circleTint,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrent) (if (isDelayedStep) SeverityCritical else Blue600) else if (isPassed) TealAccent else TextMuted,
            maxLines = 1
          )
        }

        if (index < stages.size - 1) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(3.dp)
              .padding(bottom = 14.dp)
              .background(if (index < currentStepIndex) TealAccent else SurfaceBorder)
          )
        }
      }
    }

    // Recent activity log items
    if (issue.updates.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        issue.updates.takeLast(3).reversed().forEach { update ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.small)
              .background(Blue50)
              .padding(8.dp),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(TealAccent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "${update.updatedBy} (${update.updatedByRole.name.lowercase().replaceFirstChar { it.uppercase() }})",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
                Text(
                  text = timeFormat.format(Date(update.createdAt)),
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
              Text(
                text = update.note,
                fontSize = 12.sp,
                color = TextSecondary
              )
            }
          }
        }
      }
    }
  }
}
