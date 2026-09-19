package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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

enum class SortOption(val displayName: String) {
  LATEST("Latest"),
  MOST_VOTED("Most Voted"),
  HIGHEST_PRIORITY("Highest Priority")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicIssueBoardScreen(
  onOpenIssue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  val allIssues by repo.issues.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf<IssueCategory?>(null) }
  var selectedSeverity by remember { mutableStateOf<IssueSeverity?>(null) }
  var selectedStatus by remember { mutableStateOf<IssueStatus?>(null) }
  var selectedSort by remember { mutableStateOf(SortOption.HIGHEST_PRIORITY) }
  var showFilters by remember { mutableStateOf(false) }

  // Filter public issues (exclude confidential)
  val publicIssues = remember(allIssues, searchQuery, selectedCategory, selectedSeverity, selectedStatus, selectedSort) {
    var filtered = allIssues.filter { !it.isConfidential }

    if (searchQuery.isNotBlank()) {
      filtered = filtered.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
          it.trackingId.contains(searchQuery, ignoreCase = true) ||
          it.location.contains(searchQuery, ignoreCase = true)
      }
    }

    if (selectedCategory != null) {
      filtered = filtered.filter { it.category == selectedCategory }
    }

    if (selectedSeverity != null) {
      filtered = filtered.filter { it.severity == selectedSeverity }
    }

    if (selectedStatus != null) {
      filtered = filtered.filter { it.status == selectedStatus }
    }

    when (selectedSort) {
      SortOption.LATEST -> filtered.sortedByDescending { it.createdAt }
      SortOption.MOST_VOTED -> filtered.sortedByDescending { it.votes }
      SortOption.HIGHEST_PRIORITY -> filtered.sortedByDescending { it.priorityScore }
    }
  }

  val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBackground)
  ) {
    // Top Bar with Search & Filters
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceCard)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .border(0.dp, SurfaceBorder)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Campus Issue Board",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900
          )
          Text(
            text = "${publicIssues.size} verified active complaints",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        IconButton(onClick = { showFilters = !showFilters }) {
          Icon(
            imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
            contentDescription = "Toggle Filters",
            tint = if (showFilters) Blue600 else TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Search field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by title, location or CV ID (e.g. CV-1001)...", fontSize = 12.sp, color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
            }
          }
        },
        colors = appTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
      )

      // Sorting chips
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(text = "Sort:", fontSize = 11.sp, color = TextMuted, modifier = Modifier.align(Alignment.CenterVertically))
        SortOption.entries.forEach { sort ->
          val isSelected = selectedSort == sort
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) Blue600 else Blue50)
              .clickable { selectedSort = sort }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = sort.displayName,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Blue600
            )
          }
        }
      }

      // Filter Drawer
      AnimatedVisibility(visible = showFilters) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Divider(color = SurfaceBorder)
          Spacer(modifier = Modifier.height(8.dp))

          Text(text = "Category Filter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
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
                .background(if (selectedCategory == null) Blue600 else Color(0xFFF1F5F9))
                .clickable { selectedCategory = null }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("All", fontSize = 10.sp, color = if (selectedCategory == null) Color.White else TextPrimary)
            }
            IssueCategory.entries.forEach { cat ->
              val isSel = selectedCategory == cat
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) Blue600 else Color(0xFFF1F5F9))
                .clickable { selectedCategory = if (isSel) null else cat }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(cat.displayName, fontSize = 10.sp, color = if (isSel) Color.White else TextPrimary)
            }
          }
        }

          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Severity Filter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            IssueSeverity.entries.forEach { sev ->
              val isSel = selectedSeverity == sev
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSel) Blue600 else Color(0xFFF1F5F9))
                  .clickable { selectedSeverity = if (isSel) null else sev }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(sev.displayName, fontSize = 10.sp, color = if (isSel) Color.White else TextPrimary)
              }
            }
          }
        }
      }
    }

    // Complaints Cards List
    if (publicIssues.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No complaints found", fontWeight = FontWeight.Bold, color = Navy900)
          Text("Try clearing filters or search terms", fontSize = 12.sp, color = TextMuted)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        itemsIndexed(publicIssues, key = { _, issue -> issue.issueId }) { index, issue ->
          val isTopmost = index == 0
          val hasVoted = issue.voterStudentIds.contains(currentUser.userId)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenIssue(issue.issueId) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isTopmost) Color(0xFFE0F2FE) else SurfaceCard
            ),
            border = if (isTopmost) BorderStroke(2.dp, Blue600) else CardDefaults.outlinedCardBorder()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Topmost Highlight Banner (only on index 0)
              if (isTopmost) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Blue600)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = null,
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "TOPMOST #1 HIGHLIGHTED ISSUE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.weight(1f))
                  Text(
                    text = "Top Priority",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                  )
                }
                Spacer(modifier = Modifier.height(10.dp))
              }

              // Top row: Tracking ID + Badges
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
                    color = if (isTopmost) BluePrimary else Blue600
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  PriorityBadge(priority = issue.priorityLabel, score = issue.priorityScore)
                }

                StatusBadge(status = issue.status)
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Title
              Text(
                text = issue.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Description preview
              Text(
                text = issue.description,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Location & Category
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isTopmost) BluePrimary else TextMuted,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(text = issue.location, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                }

                Spacer(modifier = Modifier.weight(1f))
                SeverityBadge(severity = issue.severity)
              }

              Spacer(modifier = Modifier.height(12.dp))
              Divider(color = if (isTopmost) Color(0xFFBAE6FD) else SurfaceBorder)
              Spacer(modifier = Modifier.height(10.dp))

              // Bottom footer: Affected count, Date & Upvote Action
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = TealAccent,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${issue.affectedStudents} affected",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "• ${dateFormat.format(Date(issue.createdAt))}",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }

                // Upvote Button
                Button(
                  onClick = {
                    repo.toggleUpvote(issue.issueId, currentUser.userId)
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasVoted) TealAccent else if (isTopmost) Blue600 else Blue50,
                    contentColor = if (hasVoted) Color.White else if (isTopmost) Color.White else Blue600
                  ),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.height(32.dp)
                ) {
                  Icon(
                    imageVector = if (hasVoted) Icons.Default.ThumbUp else Icons.Default.ThumbUpAlt,
                    contentDescription = "Upvote",
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${issue.votes} Upvotes",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
