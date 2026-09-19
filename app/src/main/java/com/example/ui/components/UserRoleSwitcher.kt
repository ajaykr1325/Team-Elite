package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.CampusVoiceRepository
import com.example.model.DemoAccounts
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun UserRoleSwitcher(
  onRoleChanged: (UserRole) -> Unit,
  modifier: Modifier = Modifier
) {
  val repo = CampusVoiceRepository.instance
  val currentUser by repo.currentUser.collectAsState()
  var showDialog by remember { mutableStateOf(false) }

  // Top-right pill button explicitly displaying "Login / Sign Up"
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(Navy800)
      .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
      .clickable { showDialog = true }
      .padding(horizontal = 10.dp, vertical = 5.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(
          when (currentUser.role) {
            UserRole.STUDENT -> Blue500
            UserRole.ADMINISTRATOR -> Navy600
            UserRole.STAFF -> Color(0xFFD97706)
          }
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (currentUser.role == UserRole.STUDENT) Icons.Default.School else Icons.Default.AccountBalance,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(13.dp)
      )
    }
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(
        text = "Login / Sign Up",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        maxLines = 1
      )
      Text(
        text = "Active: ${currentUser.role.displayName}",
        fontSize = 9.sp,
        color = Color(0xFF93C5FD),
        maxLines = 1
      )
    }
    Spacer(modifier = Modifier.width(4.dp))
    Icon(
      imageVector = Icons.Default.ExpandMore,
      contentDescription = "Open Login Dialog",
      tint = Color.White.copy(alpha = 0.9f),
      modifier = Modifier.size(16.dp)
    )
  }

  // Polished Login Dialog matching the user-uploaded image specification
  if (showDialog) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(currentUser.role) }
    var username by remember { mutableStateOf(currentUser.name) }
    var password by remember { mutableStateOf("••••••••") }
    var email by remember { mutableStateOf(currentUser.email) }
    var departmentOrYear by remember { mutableStateOf(currentUser.department ?: currentUser.year ?: "Campus Member") }

    Dialog(
      onDismissRequest = { showDialog = false },
      properties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
      )
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .widthIn(max = 440.dp)
          .imePadding()
          .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 12.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          // Top Curved Illustration Area (matching image reference)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFE9EEF4))
              .padding(top = 12.dp, bottom = 14.dp, start = 16.dp, end = 16.dp)
          ) {
            IconButton(
              onClick = { showDialog = false },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Navy900,
                modifier = Modifier.size(18.dp)
              )
            }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = "Login Illustration",
                modifier = Modifier
                  .height(145.dp)
                  .fillMaxWidth(0.85f)
                  .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
              )
            }
          }

          // Main Login Content Area (Pure crisp white matching reference)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Main Bold Title
            Text(
              text = if (isSignUpMode) "Create Account" else "Login",
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF4F46E5) // Modern purple-blue matching image
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tagline
            Text(
              text = "A platform where student voices shape campus change",
              fontSize = 11.sp,
              color = TextSecondary,
              lineHeight = 15.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Toggle Pill (Student vs Faculty)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF1F5F9))
                .padding(3.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              val isStudent = selectedRole == UserRole.STUDENT
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(20.dp))
                  .background(if (isStudent) Color.White else Color.Transparent)
                  .clickable { selectedRole = UserRole.STUDENT }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "🎓 Student",
                  fontSize = 12.sp,
                  fontWeight = if (isStudent) FontWeight.Bold else FontWeight.Medium,
                  color = if (isStudent) Navy900 else TextSecondary
                )
              }

              val isFaculty = selectedRole != UserRole.STUDENT
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(20.dp))
                  .background(if (isFaculty) Color.White else Color.Transparent)
                  .clickable { selectedRole = UserRole.ADMINISTRATOR }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "🏛️ Faculty",
                  fontSize = 12.sp,
                  fontWeight = if (isFaculty) FontWeight.Bold else FontWeight.Medium,
                  color = if (isFaculty) Navy900 else TextSecondary
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Username Pill Field (Exact pill shape from image)
            OutlinedTextField(
              value = username,
              onValueChange = { username = it },
              placeholder = { Text("Username", color = TextMuted, fontSize = 14.sp) },
              shape = RoundedCornerShape(28.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Navy900,
                unfocusedTextColor = Navy900
              ),
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            if (isSignUpMode) {
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Campus Email", color = TextMuted, fontSize = 14.sp) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color(0xFFFAFAFA),
                  focusedBorderColor = Color(0xFF6366F1),
                  unfocusedBorderColor = Color(0xFFE2E8F0),
                  focusedTextColor = Navy900,
                  unfocusedTextColor = Navy900
                ),
                leadingIcon = {
                  Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Password Pill Field (Exact pill shape from image)
            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              placeholder = { Text("Password", color = TextMuted, fontSize = 14.sp) },
              shape = RoundedCornerShape(28.dp),
              visualTransformation = PasswordVisualTransformation(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Navy900,
                unfocusedTextColor = Navy900
              ),
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = {
                val chosenAccount = when (selectedRole) {
                  UserRole.STUDENT -> DemoAccounts.studentAlex
                  UserRole.ADMINISTRATOR -> DemoAccounts.adminDean
                  UserRole.STAFF -> DemoAccounts.staffMaintenance
                }
                repo.switchUser(chosenAccount)
                onRoleChanged(chosenAccount.role)
                showDialog = false
              }),
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Pill Action Button: Sign In (Matching pill button in image)
            Button(
              onClick = {
                val targetUser = if (isSignUpMode) {
                  User(
                    userId = UUID.randomUUID().toString(),
                    name = if (username.isNotBlank()) username else "Campus Member",
                    email = if (email.isNotBlank()) email else "${username.lowercase().replace(" ", ".")}@campus.edu",
                    role = selectedRole,
                    department = if (selectedRole != UserRole.STUDENT) departmentOrYear else null,
                    year = if (selectedRole == UserRole.STUDENT) departmentOrYear else null,
                    avatarInitial = (if (username.isNotBlank()) username.first() else 'C').uppercaseChar().toString()
                  )
                } else {
                  when (selectedRole) {
                    UserRole.STUDENT -> DemoAccounts.studentAlex
                    UserRole.ADMINISTRATOR -> DemoAccounts.adminDean
                    UserRole.STAFF -> DemoAccounts.staffMaintenance
                  }
                }
                repo.switchUser(targetUser)
                onRoleChanged(targetUser.role)
                showDialog = false
              },
              shape = RoundedCornerShape(28.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1) // Vibrant purple-blue matching image
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ) {
              Text(
                text = if (isSignUpMode) "Create Account" else "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Instant 1-Tap Quick Sign In
            OutlinedButton(
              onClick = {
                val targetUser = if (selectedRole == UserRole.STUDENT) {
                  DemoAccounts.studentAlex
                } else {
                  DemoAccounts.adminDean
                }
                repo.switchUser(targetUser)
                onRoleChanged(targetUser.role)
                showDialog = false
              },
              shape = RoundedCornerShape(28.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Navy900
              ),
              border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Instant 1-Tap Sign In",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle between Login & Sign Up
            Row(
              modifier = Modifier.clickable { isSignUpMode = !isSignUpMode },
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = if (isSignUpMode) "Sign In" else "Sign Up",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F46E5)
              )
            }
          }
        }
      }
    }
  }
}
