package com.example.ui.screens

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
import com.example.model.IssueStatus
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*

@Composable
fun AdminAnalyticsScreen(
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val issues by repo.issues.collectAsState()

  val total = issues.size
  val resolved = issues.count { it.status == IssueStatus.RESOLVED }
  val open = total - resolved
  val resolutionPercentage = if (total > 0) (resolved * 100 / total) else 0

  // Category counts
  val categoryCounts = remember(issues) {
    IssueCategory.entries.map { cat ->
      Pair(cat, issues.count { it.category == cat })
    }.filter { it.second > 0 }.sortedByDescending { it.second }
  }

  // Location counts (hotspots)
  val locationCounts = remember(issues) {
    issues.groupBy { it.location }
      .mapValues { it.value.size }
      .toList()
      .sortedByDescending { it.second }
  }

  // Most upvoted
  val mostUpvoted = remember(issues) {
    issues.sortedByDescending { it.votes }.take(4)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Header
    Text(
      text = "Campus Analytics & Insights",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = Navy900
    )
    Text(
      text = "Smart campus health, recurring hotspot analysis, and resolution velocity",
      fontSize = 12.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Open vs Resolved Ratio Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceCard),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Resolution Performance", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
            Text("$resolved of $total total complaints completed", fontSize = 11.sp, color = TextMuted)
          }
          Text(
            text = "$resolutionPercentage%",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Blue600
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Multi-segment progress bar
        LinearProgressIndicator(
          progress = { if (total > 0) resolved.toFloat() / total.toFloat() else 0f },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = Blue600,
          trackColor = Blue100
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(StatusResolved))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Resolved: $resolved", fontSize = 11.sp, color = TextSecondary)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Blue600))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open / Active: $open", fontSize = 11.sp, color = TextSecondary)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = Blue600, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Avg SLA: 1.8 days", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Recurring Problem Areas by Category (Bar charts)
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceCard),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Complaints by Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
          Icon(Icons.Default.BarChart, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))

        val maxCount = (categoryCounts.firstOrNull()?.second ?: 1).coerceAtLeast(1)

        categoryCounts.forEach { (category, count) ->
          Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = category.displayName, fontSize = 11.sp, color = Navy900, fontWeight = FontWeight.Medium)
              Text(text = "$count issues", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Blue600)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { count.toFloat() / maxCount.toFloat() },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = Blue600,
              trackColor = SurfaceBorder
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Campus Hotspots / Recurring Locations
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceCard),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Campus Hotspot Locations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
          Icon(Icons.Default.Place, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))

        locationCounts.take(5).forEachIndexed { idx, (loc, cnt) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Text(
                text = "${idx + 1}.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.width(20.dp)
              )
              Text(
                text = loc,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Navy900,
                maxLines = 1
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Blue50)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(text = "$cnt reports", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Blue600)
            }
          }
          if (idx < locationCounts.take(5).size - 1) {
            Divider(color = SurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Most Upvoted Student Concerns (Highest Student Sentiment)
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceCard),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Most Upvoted Student Concerns", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
          Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))

        mostUpvoted.forEach { issue ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { onOpenIssue(issue.issueId) }
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = issue.trackingId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Blue600)
                Spacer(modifier = Modifier.width(6.dp))
                PriorityBadge(priority = issue.priorityLabel)
              }
              Text(text = issue.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy900, maxLines = 1)
              Text(text = issue.location, fontSize = 10.sp, color = TextMuted)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Blue50)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "${issue.votes} Votes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Blue600)
            }
          }
        }
      }
    }
  }
}
