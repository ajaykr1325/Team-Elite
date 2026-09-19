package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CampusVoiceRepository
import com.example.model.UserRole
import com.example.ui.components.DemoGuideBar
import com.example.ui.components.UserRoleSwitcher
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
  // Student screens
  object StudentDashboard : Screen("student_dashboard", "Dashboard", Icons.Default.Home)
  object PublicBoard : Screen("public_board", "Public Board", Icons.Default.Public)
  object SubmitIssue : Screen("submit_issue", "Report Issue", Icons.Default.AddCircle)
  object MyComplaints : Screen("my_complaints", "My Complaints", Icons.Default.Assignment)
  object ConfidentialReport : Screen("confidential_report", "Confidential", Icons.Default.Lock)

  // Admin screens
  object AdminDashboard : Screen("admin_dashboard", "Admin Portal", Icons.Default.Dashboard)
  object AdminManagement : Screen("admin_management", "Manage Table", Icons.Default.TableChart)
  object AdminAnalytics : Screen("admin_analytics", "Analytics", Icons.Default.BarChart)

  // Staff screens
  object StaffDashboard : Screen("staff_dashboard", "Assigned Tasks", Icons.Default.Engineering)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusVoiceApp() {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()

  var currentRoute by remember(currentUser.role) {
    mutableStateOf(
      when (currentUser.role) {
        UserRole.STUDENT -> Screen.StudentDashboard.route
        UserRole.ADMINISTRATOR -> Screen.AdminDashboard.route
        UserRole.STAFF -> Screen.StaffDashboard.route
      }
    )
  }

  var selectedIssueIdForDetail by remember { mutableStateOf<String?>(null) }

  // Bottom Nav items based on active role
  val bottomNavItems = remember(currentUser.role) {
    when (currentUser.role) {
      UserRole.STUDENT -> listOf(
        Screen.StudentDashboard,
        Screen.PublicBoard,
        Screen.SubmitIssue,
        Screen.MyComplaints
      )
      UserRole.ADMINISTRATOR -> listOf(
        Screen.AdminDashboard,
        Screen.AdminManagement,
        Screen.AdminAnalytics,
        Screen.PublicBoard
      )
      UserRole.STAFF -> listOf(
        Screen.StaffDashboard,
        Screen.PublicBoard
      )
    }
  }

  Scaffold(
    topBar = {
      Column {
        TopAppBar(
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable {
                currentRoute = when (currentUser.role) {
                  UserRole.STUDENT -> Screen.StudentDashboard.route
                  UserRole.ADMINISTRATOR -> Screen.AdminDashboard.route
                  UserRole.STAFF -> Screen.StaffDashboard.route
                }
              }
            ) {
              // Custom Circular App Logo
              Image(
                painter = painterResource(id = R.drawable.campus_voice_logo),
                contentDescription = "CampusVoice Logo",
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .border(1.5.dp, Color(0xFFD4AF37), CircleShape),
                contentScale = ContentScale.Crop
              )
              Spacer(modifier = Modifier.width(9.dp))
              Column {
                Text(
                  text = "CampusVoice",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                // Tagline as requested
                Text(
                  text = "A platform where student voices shape campus change",
                  fontSize = 10.sp,
                  color = Color(0xFF93C5FD),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          },
          actions = {
            UserRoleSwitcher(
              onRoleChanged = { newRole ->
                currentRoute = when (newRole) {
                  UserRole.STUDENT -> Screen.StudentDashboard.route
                  UserRole.ADMINISTRATOR -> Screen.AdminDashboard.route
                  UserRole.STAFF -> Screen.StaffDashboard.route
                }
              }
            )
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Navy900,
            titleContentColor = Color.White
          )
        )

        // Demo Walkthrough Guide Bar
        DemoGuideBar(
          onNavigateTo = { route -> currentRoute = route },
          onOpenIssue = { issueId -> selectedIssueIdForDetail = issueId }
        )
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = SurfaceCard,
        contentColor = Navy900,
        tonalElevation = 6.dp
      ) {
        bottomNavItems.forEach { screen ->
          val isSelected = currentRoute == screen.route
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentRoute = screen.route },
            icon = {
              Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                tint = if (isSelected) Navy900 else TextMuted
              )
            },
            label = {
              Text(
                text = screen.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Navy900 else TextMuted
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = Blue100
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .consumeWindowInsets(innerPadding)
        .imePadding()
    ) {
      // Direct conditional switching eliminates Crossfade transition lag and jank
      when (currentRoute) {
        Screen.StudentDashboard.route -> {
          StudentDashboardScreen(
            onNavigateToReport = { currentRoute = Screen.SubmitIssue.route },
            onNavigateToPublicBoard = { currentRoute = Screen.PublicBoard.route },
            onNavigateToMyComplaints = { currentRoute = Screen.MyComplaints.route },
            onNavigateToConfidential = { currentRoute = Screen.ConfidentialReport.route },
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        Screen.SubmitIssue.route -> {
          SubmitIssueScreen(
            onIssueSubmitted = { issueId ->
              selectedIssueIdForDetail = issueId
              currentRoute = Screen.PublicBoard.route
            },
            onNavigateToConfidential = { currentRoute = Screen.ConfidentialReport.route }
          )
        }
        Screen.ConfidentialReport.route -> {
          ConfidentialReportScreen(
            onNavigateBack = {
              currentRoute = if (currentUser.role == UserRole.ADMINISTRATOR) {
                Screen.AdminManagement.route
              } else {
                Screen.StudentDashboard.route
              }
            }
          )
        }
        Screen.PublicBoard.route -> {
          PublicIssueBoardScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        Screen.MyComplaints.route -> {
          MyComplaintsScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id },
            onNavigateToSubmit = { currentRoute = Screen.SubmitIssue.route }
          )
        }
        Screen.AdminDashboard.route -> {
          AdminDashboardScreen(
            onNavigateToManagement = { currentRoute = Screen.AdminManagement.route },
            onNavigateToAnalytics = { currentRoute = Screen.AdminAnalytics.route },
            onNavigateToConfidential = { currentRoute = Screen.AdminManagement.route },
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        Screen.AdminManagement.route -> {
          AdminManagementScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        Screen.AdminAnalytics.route -> {
          AdminAnalyticsScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        Screen.StaffDashboard.route -> {
          StaffDashboardScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
        else -> {
          PublicIssueBoardScreen(
            onOpenIssue = { id -> selectedIssueIdForDetail = id }
          )
        }
      }

      // Detailed Modal Dialog for any Issue
      selectedIssueIdForDetail?.let { issueId ->
        IssueDetailDialog(
          issueId = issueId,
          onDismiss = { selectedIssueIdForDetail = null }
        )
      }
    }
  }
}
