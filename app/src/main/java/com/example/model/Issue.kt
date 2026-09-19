package com.example.model

enum class IssueCategory(val displayName: String, val iconName: String) {
  WATER_SUPPLY("Water Supply", "water_drop"),
  CANTEEN_AND_MESS("Canteen and Mess", "restaurant"),
  CLASSROOM_FACILITIES("Classroom Facilities", "school"),
  LABORATORY_EQUIPMENT("Laboratory Equipment", "biotech"),
  CLEANLINESS_AND_WASHROOMS("Cleanliness and Washrooms", "cleaning_services"),
  WIFI_AND_DIGITAL_SERVICES("Wi-Fi and Digital Services", "wifi"),
  HOSTEL_FACILITIES("Hostel Facilities", "hotel"),
  TRANSPORTATION("Transportation", "directions_bus"),
  ACADEMIC_SUPPORT("Academic Support", "menu_book"),
  TEACHING_FEEDBACK("Teaching Feedback", "record_voice_over"),
  CAMPUS_SAFETY("Campus Safety", "security"),
  OTHER("Other", "help_outline");

  companion object {
    fun fromString(name: String): IssueCategory {
      return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
    }
  }
}

enum class ConfidentialCategory(val displayName: String) {
  RAGGING("Ragging"),
  HARASSMENT("Harassment"),
  DISCRIMINATION("Discrimination"),
  PERSONAL_SAFETY("Personal safety concerns"),
  SERIOUS_TEACHER_COMPLAINT("Serious teacher-related complaints")
}

enum class IssueSeverity(val displayName: String, val scoreMultiplier: Int) {
  LOW("Low", 10),
  MEDIUM("Medium", 25),
  HIGH("High", 50),
  CRITICAL("Critical", 85)
}

enum class IssueStatus(val displayName: String) {
  SUBMITTED("Submitted"),
  UNDER_REVIEW("Under Review"),
  ACCEPTED("Accepted"),
  ASSIGNED("Assigned"),
  IN_PROGRESS("In Progress"),
  RESOLVED("Resolved"),
  REOPENED("Reopened"),
  ESCALATED("Escalated"),
  REJECTED("Rejected")
}

enum class PriorityLabel(val displayName: String) {
  CRITICAL("Critical"),
  HIGH("High"),
  MEDIUM("Medium"),
  LOW("Low")
}

data class IssueFeedback(
  val feedbackId: String,
  val issueId: String,
  val studentId: String,
  val rating: Int, // 1 to 5
  val comment: String,
  val createdAt: Long = System.currentTimeMillis()
)

data class IssueUpdate(
  val updateId: String,
  val issueId: String,
  val updatedBy: String,
  val updatedByRole: UserRole,
  val previousStatus: IssueStatus?,
  val newStatus: IssueStatus,
  val note: String,
  val createdAt: Long = System.currentTimeMillis()
)

data class Issue(
  val issueId: String,
  val trackingId: String, // e.g. CV-1001
  val title: String,
  val category: IssueCategory,
  val location: String,
  val description: String,
  val severity: IssueSeverity,
  val imageUrl: String? = null,
  val reporterId: String,
  val reporterName: String,
  val anonymous: Boolean = false,
  val isConfidential: Boolean = false,
  val votes: Int = 0,
  val voterStudentIds: Set<String> = emptySet(),
  val affectedStudents: Int = 1,
  val priorityScore: Int = 0,
  val priorityLabel: PriorityLabel = PriorityLabel.MEDIUM,
  val status: IssueStatus = IssueStatus.SUBMITTED,
  val assignedDepartment: String? = null,
  val assignedStaffId: String? = null,
  val assignedStaffName: String? = null,
  val deadline: Long? = null, // epoch millis
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val resolvedAt: Long? = null,
  val resolutionNote: String? = null,
  val resolutionProofUrl: String? = null,
  val internalNotes: List<String> = emptyList(),
  val updates: List<IssueUpdate> = emptyList(),
  val feedback: IssueFeedback? = null,
  val isDelayed: Boolean = false
)

data class ConfidentialIssue(
  val confidentialIssueId: String,
  val privateTrackingId: String, // e.g. CONF-8092
  val category: ConfidentialCategory,
  val description: String,
  val location: String,
  val evidenceUrl: String? = null,
  val reporterId: String,
  val anonymous: Boolean = true,
  val status: IssueStatus = IssueStatus.UNDER_REVIEW,
  val assignedOfficer: String? = "Dean of Student Welfare & Anti-Ragging Committee",
  val internalNotes: List<String> = emptyList(),
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

data class AppNotification(
  val notificationId: String,
  val userId: String,
  val issueId: String?,
  val trackingId: String?,
  val title: String,
  val message: String,
  val read: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

val CampusDepartments = listOf(
  "Maintenance",
  "IT Support",
  "Hostel Administration",
  "Canteen Management",
  "Academic Department",
  "Transport Department",
  "Student Welfare Committee",
  "Anti-Ragging Committee"
)
