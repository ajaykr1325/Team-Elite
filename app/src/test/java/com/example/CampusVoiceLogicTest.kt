package com.example

import com.example.data.CampusVoiceRepository
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class CampusVoiceLogicTest {

  @Test
  fun testPriorityCalculation_SafetyCategoryBoost() {
    val now = System.currentTimeMillis()
    val safetyResult = PriorityCalculator.calculateScore(
      category = IssueCategory.CAMPUS_SAFETY,
      severity = IssueSeverity.HIGH,
      affectedStudents = 50,
      votes = 10,
      createdAt = now
    )

    val otherResult = PriorityCalculator.calculateScore(
      category = IssueCategory.OTHER,
      severity = IssueSeverity.HIGH,
      affectedStudents = 50,
      votes = 10,
      createdAt = now
    )

    // Safety category gets 35 pts boost vs 0 for other
    assertTrue(safetyResult.first > otherResult.first)
  }

  @Test
  fun testDemoWorkflow_FullLifecycle() {
    val repo = CampusVoiceRepository.instance
    repo.switchUser(DemoAccounts.studentAlex)

    // Step 1: Submit issue
    val issue = repo.submitIssue(
      title = "Water cooler not working in Block B",
      category = IssueCategory.WATER_SUPPLY,
      location = "Block B, Ground Floor Hallway",
      description = "Leaking and dispensing warm water.",
      severity = IssueSeverity.HIGH,
      affectedStudents = 85,
      anonymous = false,
      imageUrl = null,
      forcedTrackingId = "CV-TEST-1"
    )

    assertEquals("CV-TEST-1", issue.trackingId)
    assertEquals(IssueStatus.SUBMITTED, issue.status)
    val initialPriority = issue.priorityScore

    // Step 2: Upvote by peer student
    repo.switchUser(DemoAccounts.studentPriya)
    repo.toggleUpvote(issue.issueId, DemoAccounts.studentPriya.userId)

    val upvoted = repo.issues.value.find { it.issueId == issue.issueId }!!
    assertEquals(2, upvoted.votes)
    assertTrue("Priority score should increase after upvotes", upvoted.priorityScore >= initialPriority)

    // Step 3: Admin assigns to department
    repo.switchUser(DemoAccounts.adminDean)
    repo.assignIssue(
      issueId = issue.issueId,
      department = "Maintenance",
      staffName = "Marcus Vance",
      deadlineDays = 2,
      internalNote = "Urgent sanitation repair"
    )

    val assigned = repo.issues.value.find { it.issueId == issue.issueId }!!
    assertEquals(IssueStatus.ASSIGNED, assigned.status)
    assertEquals("Maintenance", assigned.assignedDepartment)

    // Step 4: Staff accepts and resolves
    repo.switchUser(DemoAccounts.staffMaintenance)
    repo.staffUpdateProgress(
      issueId = issue.issueId,
      progressNote = "Replaced parts",
      resolutionDescription = "Fixed cooler valve and verified cold water.",
      resolutionProofUrl = "proof.jpg",
      markResolved = true
    )

    val resolved = repo.issues.value.find { it.issueId == issue.issueId }!!
    assertEquals(IssueStatus.RESOLVED, resolved.status)
    assertNotNull(resolved.resolutionNote)

    // Step 5: Original student provides feedback
    repo.switchUser(DemoAccounts.studentAlex)
    repo.submitFeedback(issue.issueId, rating = 5, comment = "Excellent quick resolution!")

    val finalIssue = repo.issues.value.find { it.issueId == issue.issueId }!!
    assertNotNull(finalIssue.feedback)
    assertEquals(5, finalIssue.feedback?.rating)
  }

  @Test
  fun testConfidentialComplaint_DoesNotAppearInPublicList() {
    val repo = CampusVoiceRepository.instance
    repo.switchUser(DemoAccounts.studentAlex)

    val conf = repo.submitConfidentialIssue(
      category = ConfidentialCategory.RAGGING,
      description = "Confidential test incident",
      location = "Hostel Courtyard",
      anonymous = true,
      evidenceUrl = null
    )

    assertTrue(conf.privateTrackingId.startsWith("CONF-"))
    val inPublicIssues = repo.issues.value.any { it.issueId == conf.confidentialIssueId || it.trackingId == conf.privateTrackingId }
    assertFalse("Confidential complaint must NEVER leak into public issues list", inPublicIssues)

    val inVault = repo.confidentialIssues.value.any { it.confidentialIssueId == conf.confidentialIssueId }
    assertTrue("Confidential complaint must be stored in confidential vault", inVault)
  }
}
