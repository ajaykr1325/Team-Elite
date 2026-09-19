package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CampusVoiceRepository private constructor() {

  companion object {
    val instance: CampusVoiceRepository by lazy { CampusVoiceRepository() }
  }

  private val _currentUser = MutableStateFlow<User>(DemoAccounts.studentAlex)
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  private val _issues = MutableStateFlow<List<Issue>>(emptyList())
  val issues: StateFlow<List<Issue>> = _issues.asStateFlow()

  private val _confidentialIssues = MutableStateFlow<List<ConfidentialIssue>>(emptyList())
  val confidentialIssues: StateFlow<List<ConfidentialIssue>> = _confidentialIssues.asStateFlow()

  private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
  val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  private var nextTrackingNum = 1008

  init {
    seedInitialData()
  }

  private fun seedInitialData() {
    val now = System.currentTimeMillis()
    val dayMillis = 24L * 60 * 60 * 1000

    val initialIssues = listOf(
      createSeededIssue(
        trackingId = "CV-1001",
        title = "Water cooler not working in Block B, First Floor",
        category = IssueCategory.WATER_SUPPLY,
        location = "Block B, First Floor",
        description = "Water cooler unit has stopped chilling and has a persistent leaking puddle creating slipping hazard near lecture hall 12.",
        severity = IssueSeverity.HIGH,
        reporterId = DemoAccounts.studentAlex.userId,
        reporterName = DemoAccounts.studentAlex.name,
        votes = 14,
        voterStudentIds = setOf(DemoAccounts.studentPriya.userId),
        affectedStudents = 65,
        status = IssueStatus.IN_PROGRESS,
        assignedDepartment = "Maintenance",
        assignedStaffName = DemoAccounts.staffMaintenance.name,
        deadline = now + dayMillis * 1,
        createdOffset = dayMillis * 2,
        notes = listOf("Technician inspected compressor", "Replacement valve ordered")
      ),
      createSeededIssue(
        trackingId = "CV-1002",
        title = "Projector not working in Room 204",
        category = IssueCategory.CLASSROOM_FACILITIES,
        location = "Academic Block A, Room 204",
        description = "HDMI cable input flickering and projector bulb dim during Data Structures lecture.",
        severity = IssueSeverity.MEDIUM,
        reporterId = DemoAccounts.studentPriya.userId,
        reporterName = DemoAccounts.studentPriya.name,
        votes = 8,
        affectedStudents = 50,
        status = IssueStatus.ASSIGNED,
        assignedDepartment = "IT Support",
        assignedStaffName = DemoAccounts.staffIT.name,
        deadline = now + dayMillis * 2,
        createdOffset = dayMillis * 1
      ),
      createSeededIssue(
        trackingId = "CV-1003",
        title = "Wi-Fi unavailable in the library",
        category = IssueCategory.WIFI_AND_DIGITAL_SERVICES,
        location = "Central Library, 2nd Floor Study Hall",
        description = "Campus SSID Wi-Fi drops every 5 minutes on the second floor. Cannot access research IEEE journals.",
        severity = IssueSeverity.HIGH,
        reporterId = DemoAccounts.studentAlex.userId,
        reporterName = DemoAccounts.studentAlex.name,
        votes = 29,
        voterStudentIds = setOf(DemoAccounts.studentAlex.userId),
        affectedStudents = 140,
        status = IssueStatus.UNDER_REVIEW,
        createdOffset = dayMillis * 3
      ),
      createSeededIssue(
        trackingId = "CV-1004",
        title = "Poor hygiene in the main canteen",
        category = IssueCategory.CANTEEN_AND_MESS,
        location = "Main Campus Canteen",
        description = "Uncovered food containers and dirty utensil washing area. Multiple students reported stomach aches.",
        severity = IssueSeverity.CRITICAL,
        reporterId = DemoAccounts.studentPriya.userId,
        reporterName = DemoAccounts.studentPriya.name,
        votes = 42,
        affectedStudents = 320,
        status = IssueStatus.ACCEPTED,
        assignedDepartment = "Canteen Management",
        deadline = now + dayMillis * 1,
        createdOffset = dayMillis * 4
      ),
      createSeededIssue(
        trackingId = "CV-1005",
        title = "Broken benches in Room 101",
        category = IssueCategory.CLASSROOM_FACILITIES,
        location = "Mechanical Wing, Room 101",
        description = "Three wooden desk benches have broken screws with exposed metal edges that tear bags.",
        severity = IssueSeverity.LOW,
        reporterId = DemoAccounts.studentAlex.userId,
        reporterName = DemoAccounts.studentAlex.name,
        votes = 4,
        affectedStudents = 25,
        status = IssueStatus.SUBMITTED,
        createdOffset = dayMillis * 1
      ),
      createSeededIssue(
        trackingId = "CV-1006",
        title = "Hostel water shortage in Hostel B",
        category = IssueCategory.HOSTEL_FACILITIES,
        location = "Hostel B, East Wing",
        description = "Water pump failure since yesterday evening. No running water in washrooms on floors 2 to 4.",
        severity = IssueSeverity.CRITICAL,
        reporterId = DemoAccounts.studentPriya.userId,
        reporterName = DemoAccounts.studentPriya.name,
        votes = 58,
        affectedStudents = 210,
        status = IssueStatus.IN_PROGRESS,
        assignedDepartment = "Hostel Administration",
        assignedStaffName = "Hostel Warden Office",
        deadline = now + (dayMillis / 2),
        createdOffset = dayMillis * 2,
        notes = listOf("Emergency water tankers scheduled")
      ),
      createSeededIssue(
        trackingId = "CV-1007",
        title = "Street light not working near the back gate",
        category = IssueCategory.CAMPUS_SAFETY,
        location = "Campus Back Gate, South Perimeter Road",
        description = "Pathway between library parking and the rear pedestrian gate is completely dark after 7:30 PM.",
        severity = IssueSeverity.HIGH,
        reporterId = DemoAccounts.studentAlex.userId,
        reporterName = DemoAccounts.studentAlex.name,
        votes = 23,
        affectedStudents = 180,
        status = IssueStatus.ASSIGNED,
        assignedDepartment = "Maintenance",
        deadline = now - (dayMillis / 4), // slightly delayed
        isDelayed = true,
        createdOffset = dayMillis * 5
      )
    )

    _issues.value = initialIssues

    val initialConfidential = listOf(
      ConfidentialIssue(
        confidentialIssueId = "conf-101",
        privateTrackingId = "CONF-8092",
        category = ConfidentialCategory.RAGGING,
        description = "Senior students demanding junior hostel residents to assemble at basketball court at 11 PM.",
        location = "Hostel C Courtyard",
        reporterId = DemoAccounts.studentAlex.userId,
        anonymous = true,
        status = IssueStatus.UNDER_REVIEW,
        assignedOfficer = "Dean of Student Welfare & Anti-Ragging Committee",
        internalNotes = listOf("Anti-ragging squad patrol deployed for nightly verification.", "Hostel warden notified.")
      )
    )
    _confidentialIssues.value = initialConfidential

    _notifications.value = listOf(
      AppNotification(
        notificationId = "notif-1",
        userId = DemoAccounts.studentAlex.userId,
        issueId = initialIssues[0].issueId,
        trackingId = initialIssues[0].trackingId,
        title = "Status Update: In Progress",
        message = "Maintenance department has commenced repair on Water cooler (CV-1001)."
      ),
      AppNotification(
        notificationId = "notif-2",
        userId = DemoAccounts.studentAlex.userId,
        issueId = initialIssues[6].issueId,
        trackingId = initialIssues[6].trackingId,
        title = "Delayed Notice: Escalation Alert",
        message = "Street light complaint (CV-1007) is past due. Administrator is escalating."
      )
    )
  }

  private fun createSeededIssue(
    trackingId: String,
    title: String,
    category: IssueCategory,
    location: String,
    description: String,
    severity: IssueSeverity,
    reporterId: String,
    reporterName: String,
    votes: Int,
    affectedStudents: Int,
    status: IssueStatus,
    voterStudentIds: Set<String> = emptySet(),
    assignedDepartment: String? = null,
    assignedStaffName: String? = null,
    deadline: Long? = null,
    createdOffset: Long = 0,
    isDelayed: Boolean = false,
    notes: List<String> = emptyList()
  ): Issue {
    val created = System.currentTimeMillis() - createdOffset
    val (score, label) = PriorityCalculator.calculateScore(
      category = category,
      severity = severity,
      affectedStudents = affectedStudents,
      votes = votes,
      createdAt = created,
      deadline = deadline
    )

    val updates = mutableListOf(
      IssueUpdate(
        updateId = UUID.randomUUID().toString(),
        issueId = trackingId,
        updatedBy = reporterName,
        updatedByRole = UserRole.STUDENT,
        previousStatus = null,
        newStatus = IssueStatus.SUBMITTED,
        note = "Issue reported via CampusVoice mobile portal",
        createdAt = created
      )
    )

    if (status != IssueStatus.SUBMITTED) {
      updates.add(
        IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = trackingId,
          updatedBy = DemoAccounts.adminDean.name,
          updatedByRole = UserRole.ADMINISTRATOR,
          previousStatus = IssueStatus.SUBMITTED,
          newStatus = if (status == IssueStatus.ASSIGNED || status == IssueStatus.IN_PROGRESS) IssueStatus.ASSIGNED else status,
          note = "Administrator verified complaint and assigned to $assignedDepartment",
          createdAt = created + (1000 * 60 * 60 * 2)
        )
      )
    }

    if (status == IssueStatus.IN_PROGRESS) {
      updates.add(
        IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = trackingId,
          updatedBy = assignedStaffName ?: "Staff Lead",
          updatedByRole = UserRole.STAFF,
          previousStatus = IssueStatus.ASSIGNED,
          newStatus = IssueStatus.IN_PROGRESS,
          note = "Work order initiated. Technician on-site.",
          createdAt = created + (1000 * 60 * 60 * 5)
        )
      )
    }

    return Issue(
      issueId = UUID.randomUUID().toString(),
      trackingId = trackingId,
      title = title,
      category = category,
      location = location,
      description = description,
      severity = severity,
      reporterId = reporterId,
      reporterName = reporterName,
      votes = votes,
      voterStudentIds = voterStudentIds,
      affectedStudents = affectedStudents,
      priorityScore = score,
      priorityLabel = label,
      status = status,
      assignedDepartment = assignedDepartment,
      assignedStaffName = assignedStaffName,
      deadline = deadline,
      createdAt = created,
      updatedAt = created + (1000 * 60 * 60 * 6),
      internalNotes = notes,
      updates = updates,
      isDelayed = isDelayed
    )
  }

  fun switchUser(user: User) {
    _currentUser.value = user
  }

  fun submitIssue(
    title: String,
    category: IssueCategory,
    location: String,
    description: String,
    severity: IssueSeverity,
    affectedStudents: Int,
    anonymous: Boolean,
    imageUrl: String?,
    forcedTrackingId: String? = null
  ): Issue {
    val user = _currentUser.value
    val trackingId = forcedTrackingId ?: "CV-${nextTrackingNum++}"
    val now = System.currentTimeMillis()

    val (score, label) = PriorityCalculator.calculateScore(
      category = category,
      severity = severity,
      affectedStudents = affectedStudents,
      votes = 1,
      createdAt = now
    )

    val newIssue = Issue(
      issueId = UUID.randomUUID().toString(),
      trackingId = trackingId,
      title = title.trim(),
      category = category,
      location = location.trim(),
      description = description.trim(),
      severity = severity,
      imageUrl = imageUrl,
      reporterId = user.userId,
      reporterName = if (anonymous) "Anonymous Student" else user.name,
      anonymous = anonymous,
      votes = 1,
      voterStudentIds = setOf(user.userId),
      affectedStudents = affectedStudents,
      priorityScore = score,
      priorityLabel = label,
      status = IssueStatus.SUBMITTED,
      createdAt = now,
      updatedAt = now,
      updates = listOf(
        IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = trackingId,
          updatedBy = if (anonymous) "Anonymous Student" else user.name,
          updatedByRole = UserRole.STUDENT,
          previousStatus = null,
          newStatus = IssueStatus.SUBMITTED,
          note = "Issue reported with initial upvote",
          createdAt = now
        )
      )
    )

    _issues.value = listOf(newIssue) + _issues.value

    // Notify admins
    addNotification(
      userId = DemoAccounts.adminDean.userId,
      issueId = newIssue.issueId,
      trackingId = newIssue.trackingId,
      title = "New Issue Submitted: $trackingId",
      message = "${newIssue.title} in ${newIssue.location} ($label Priority)"
    )

    return newIssue
  }

  fun submitConfidentialIssue(
    category: ConfidentialCategory,
    description: String,
    location: String,
    anonymous: Boolean,
    evidenceUrl: String?
  ): ConfidentialIssue {
    val user = _currentUser.value
    val randomId = (8000 + (100..999).random()).toString()
    val privateTrackingId = "CONF-$randomId"
    val now = System.currentTimeMillis()

    val conf = ConfidentialIssue(
      confidentialIssueId = UUID.randomUUID().toString(),
      privateTrackingId = privateTrackingId,
      category = category,
      description = description.trim(),
      location = location.trim(),
      evidenceUrl = evidenceUrl,
      reporterId = user.userId,
      anonymous = anonymous,
      status = IssueStatus.UNDER_REVIEW,
      assignedOfficer = "Dean of Student Affairs & Anti-Ragging Committee",
      internalNotes = listOf("Report recorded securely with restricted administrator access."),
      createdAt = now,
      updatedAt = now
    )

    _confidentialIssues.value = listOf(conf) + _confidentialIssues.value

    addNotification(
      userId = DemoAccounts.adminDean.userId,
      issueId = null,
      trackingId = privateTrackingId,
      title = "URGENT: Confidential Complaint Received",
      message = "Private tracking: $privateTrackingId ($category) requires prompt administrative review."
    )

    return conf
  }

  fun toggleUpvote(issueId: String, studentId: String) {
    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val hasVoted = issue.voterStudentIds.contains(studentId)
        val newVoters = if (hasVoted) {
          issue.voterStudentIds - studentId
        } else {
          issue.voterStudentIds + studentId
        }
        val newVoteCount = newVoters.size

        val (score, label) = PriorityCalculator.calculateScore(
          category = issue.category,
          severity = issue.severity,
          affectedStudents = issue.affectedStudents,
          votes = newVoteCount,
          createdAt = issue.createdAt,
          deadline = issue.deadline
        )

        val updated = issue.copy(
          votes = newVoteCount,
          voterStudentIds = newVoters,
          priorityScore = score,
          priorityLabel = label,
          updatedAt = System.currentTimeMillis()
        )

        // Notify reporter on milestone upvotes
        if (!hasVoted && issue.reporterId != studentId) {
          addNotification(
            userId = issue.reporterId,
            issueId = issue.issueId,
            trackingId = issue.trackingId,
            title = "New Upvote on ${issue.trackingId}",
            message = "A campus peer upvoted your report. Total votes: $newVoteCount."
          )
        }

        updated
      } else {
        issue
      }
    }
  }

  fun assignIssue(
    issueId: String,
    department: String,
    staffName: String?,
    deadlineDays: Int,
    internalNote: String
  ) {
    val admin = _currentUser.value
    val now = System.currentTimeMillis()
    val deadlineMillis = now + (deadlineDays.toLong() * 24 * 60 * 60 * 1000)

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val newNotes = if (internalNote.isNotBlank()) issue.internalNotes + internalNote.trim() else issue.internalNotes
        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = admin.name,
          updatedByRole = admin.role,
          previousStatus = issue.status,
          newStatus = IssueStatus.ASSIGNED,
          note = "Assigned to $department (${staffName ?: "Lead Staff"}). Resolution deadline set to $deadlineDays day(s). ${if (internalNote.isNotBlank()) "Note: $internalNote" else ""}",
          createdAt = now
        )

        val (score, label) = PriorityCalculator.calculateScore(
          category = issue.category,
          severity = issue.severity,
          affectedStudents = issue.affectedStudents,
          votes = issue.votes,
          createdAt = issue.createdAt,
          deadline = deadlineMillis
        )

        val updated = issue.copy(
          status = IssueStatus.ASSIGNED,
          assignedDepartment = department,
          assignedStaffName = staffName ?: "Department Lead",
          deadline = deadlineMillis,
          internalNotes = newNotes,
          updates = issue.updates + update,
          priorityScore = score,
          priorityLabel = label,
          updatedAt = now,
          isDelayed = false
        )

        // Notify reporter
        addNotification(
          userId = issue.reporterId,
          issueId = issue.issueId,
          trackingId = issue.trackingId,
          title = "Issue Assigned: ${issue.trackingId}",
          message = "Your issue has been assigned to $department. Target resolution in $deadlineDays days."
        )

        updated
      } else {
        issue
      }
    }
  }

  fun updateIssueStatus(
    issueId: String,
    newStatus: IssueStatus,
    note: String
  ) {
    val actor = _currentUser.value
    val now = System.currentTimeMillis()

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = actor.name,
          updatedByRole = actor.role,
          previousStatus = issue.status,
          newStatus = newStatus,
          note = note.ifBlank { "Status updated to ${newStatus.displayName}" },
          createdAt = now
        )

        val resolvedTime = if (newStatus == IssueStatus.RESOLVED) now else issue.resolvedAt

        val updated = issue.copy(
          status = newStatus,
          resolvedAt = resolvedTime,
          updates = issue.updates + update,
          updatedAt = now
        )

        addNotification(
          userId = issue.reporterId,
          issueId = issue.issueId,
          trackingId = issue.trackingId,
          title = "Status Changed: ${newStatus.displayName}",
          message = "Complaint ${issue.trackingId} is now ${newStatus.displayName}."
        )

        updated
      } else {
        issue
      }
    }
  }

  fun staffUpdateProgress(
    issueId: String,
    progressNote: String,
    resolutionDescription: String?,
    resolutionProofUrl: String?,
    markResolved: Boolean
  ) {
    val staff = _currentUser.value
    val now = System.currentTimeMillis()

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val newStatus = if (markResolved) IssueStatus.RESOLVED else IssueStatus.IN_PROGRESS
        val actionText = if (markResolved) {
          "Completed & marked Resolved. Proof: ${resolutionDescription ?: "Resolution verification complete"}."
        } else {
          "Work in progress update: $progressNote"
        }

        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = staff.name,
          updatedByRole = UserRole.STAFF,
          previousStatus = issue.status,
          newStatus = newStatus,
          note = actionText,
          createdAt = now
        )

        val updated = issue.copy(
          status = newStatus,
          resolutionNote = if (markResolved) resolutionDescription ?: progressNote else issue.resolutionNote,
          resolutionProofUrl = if (markResolved) resolutionProofUrl else issue.resolutionProofUrl,
          resolvedAt = if (markResolved) now else issue.resolvedAt,
          updates = issue.updates + update,
          updatedAt = now
        )

        addNotification(
          userId = issue.reporterId,
          issueId = issue.issueId,
          trackingId = issue.trackingId,
          title = if (markResolved) "Issue Resolved: ${issue.trackingId}" else "Staff Update on ${issue.trackingId}",
          message = if (markResolved) {
            "Department resolved ${issue.title}. Please verify and provide feedback."
          } else {
            "Staff posted an update: $progressNote"
          }
        )

        updated
      } else {
        issue
      }
    }
  }

  fun submitFeedback(
    issueId: String,
    rating: Int,
    comment: String
  ) {
    val student = _currentUser.value
    val fb = IssueFeedback(
      feedbackId = UUID.randomUUID().toString(),
      issueId = issueId,
      studentId = student.userId,
      rating = rating,
      comment = comment.trim()
    )

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        issue.copy(feedback = fb, updatedAt = System.currentTimeMillis())
      } else {
        issue
      }
    }

    addNotification(
      userId = DemoAccounts.adminDean.userId,
      issueId = issueId,
      trackingId = _issues.value.find { it.issueId == issueId }?.trackingId,
      title = "Student Feedback Received",
      message = "Student rated resolution $rating/5 stars: \"$comment\""
    )
  }

  fun reopenIssue(
    issueId: String,
    reason: String
  ) {
    val student = _currentUser.value
    val now = System.currentTimeMillis()

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = student.name,
          updatedByRole = UserRole.STUDENT,
          previousStatus = issue.status,
          newStatus = IssueStatus.REOPENED,
          note = "Student marked issue as unresolved: $reason",
          createdAt = now
        )

        val updated = issue.copy(
          status = IssueStatus.REOPENED,
          resolvedAt = null,
          updates = issue.updates + update,
          updatedAt = now
        )

        addNotification(
          userId = DemoAccounts.adminDean.userId,
          issueId = issue.issueId,
          trackingId = issue.trackingId,
          title = "Issue Reopened by Student",
          message = "${issue.trackingId} was reopened: $reason"
        )

        updated
      } else {
        issue
      }
    }
  }

  fun escalateIssue(issueId: String) {
    val actor = _currentUser.value
    val now = System.currentTimeMillis()

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == issueId) {
        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = actor.name,
          updatedByRole = actor.role,
          previousStatus = issue.status,
          newStatus = IssueStatus.ESCALATED,
          note = "Administrator triggered priority escalation due to deadline delay or safety alert",
          createdAt = now
        )

        val updated = issue.copy(
          status = IssueStatus.ESCALATED,
          priorityLabel = PriorityLabel.CRITICAL,
          priorityScore = (issue.priorityScore + 30).coerceAtMost(100),
          isDelayed = true,
          updates = issue.updates + update,
          updatedAt = now
        )

        addNotification(
          userId = issue.reporterId,
          issueId = issue.issueId,
          trackingId = issue.trackingId,
          title = "Issue Escalated: ${issue.trackingId}",
          message = "Your issue has been escalated directly to Principal / Senior Executive Board."
        )

        updated
      } else {
        issue
      }
    }
  }

  fun markAsDuplicate(duplicateIssueId: String, canonicalIssueId: String) {
    val actor = _currentUser.value
    val now = System.currentTimeMillis()
    val canonical = _issues.value.find { it.issueId == canonicalIssueId }

    _issues.value = _issues.value.map { issue ->
      if (issue.issueId == duplicateIssueId) {
        val update = IssueUpdate(
          updateId = UUID.randomUUID().toString(),
          issueId = issue.trackingId,
          updatedBy = actor.name,
          updatedByRole = actor.role,
          previousStatus = issue.status,
          newStatus = IssueStatus.REJECTED,
          note = "Marked as duplicate of ${canonical?.trackingId ?: "primary issue"} and merged into thread",
          createdAt = now
        )
        issue.copy(
          status = IssueStatus.REJECTED,
          internalNotes = issue.internalNotes + "Duplicate of ${canonical?.trackingId}",
          updates = issue.updates + update,
          updatedAt = now
        )
      } else if (issue.issueId == canonicalIssueId) {
        // Merge upvotes & affected count
        val dup = _issues.value.find { it.issueId == duplicateIssueId }
        val addedVotes = dup?.votes ?: 0
        val addedStudents = dup?.affectedStudents ?: 0
        issue.copy(
          votes = issue.votes + addedVotes,
          affectedStudents = issue.affectedStudents + addedStudents,
          updatedAt = now
        )
      } else {
        issue
      }
    }
  }

  fun findPotentialDuplicates(category: IssueCategory, location: String): List<Issue> {
    if (location.isBlank()) return emptyList()
    val locWords = location.lowercase().split(" ", ",", "-").filter { it.length > 2 }
    return _issues.value.filter { issue ->
      issue.status != IssueStatus.REJECTED &&
        (issue.category == category ||
          locWords.any { word -> issue.location.lowercase().contains(word) })
    }.take(3)
  }

  fun markAllNotificationsRead() {
    val uid = _currentUser.value.userId
    _notifications.value = _notifications.value.map {
      if (it.userId == uid) it.copy(read = true) else it
    }
  }

  private fun addNotification(
    userId: String,
    issueId: String?,
    trackingId: String?,
    title: String,
    message: String
  ) {
    val notif = AppNotification(
      notificationId = UUID.randomUUID().toString(),
      userId = userId,
      issueId = issueId,
      trackingId = trackingId,
      title = title,
      message = message,
      read = false,
      createdAt = System.currentTimeMillis()
    )
    _notifications.value = listOf(notif) + _notifications.value
  }
}
