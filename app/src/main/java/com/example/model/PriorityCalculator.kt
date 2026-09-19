package com.example.model

import kotlin.math.min

object PriorityCalculator {

  fun calculateScore(
    category: IssueCategory,
    severity: IssueSeverity,
    affectedStudents: Int,
    votes: Int,
    createdAt: Long,
    deadline: Long? = null,
    currentTime: Long = System.currentTimeMillis()
  ): Pair<Int, PriorityLabel> {
    // 1. Severity Base (10 - 55)
    val severityPoints = when (severity) {
      IssueSeverity.CRITICAL -> 55
      IssueSeverity.HIGH -> 35
      IssueSeverity.MEDIUM -> 20
      IssueSeverity.LOW -> 10
    }

    // 2. Health & Safety / Campus Impact Boost
    val safetyBoost = when (category) {
      IssueCategory.CAMPUS_SAFETY -> 35
      IssueCategory.WATER_SUPPLY -> 25
      IssueCategory.CLEANLINESS_AND_WASHROOMS -> 20
      IssueCategory.CANTEEN_AND_MESS -> 20
      IssueCategory.HOSTEL_FACILITIES -> 15
      else -> 0
    }

    // 3. Affected Students Impact (up to 30 pts)
    val affectedPoints = min(30, (affectedStudents / 4).coerceAtLeast(1))

    // 4. Upvotes Impact (up to 30 pts, 2 pts per student upvote)
    val votePoints = min(30, votes * 2)

    // 5. Pending & Overdue Days Impact (up to 25 pts)
    val daysPending = ((currentTime - createdAt) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    var pendingPoints = min(15, daysPending * 3)

    if (deadline != null && currentTime > deadline) {
      val overdueDays = ((currentTime - deadline) / (1000 * 60 * 60 * 24)).toInt() + 1
      pendingPoints += min(15, overdueDays * 5)
    }

    val totalScore = severityPoints + safetyBoost + affectedPoints + votePoints + pendingPoints

    // Priority Label Determination
    // Critical safety complaints must receive high or critical priority even if they have few votes.
    val label = when {
      severity == IssueSeverity.CRITICAL || totalScore >= 95 -> PriorityLabel.CRITICAL
      totalScore >= 65 || (severity == IssueSeverity.HIGH && safetyBoost > 0) -> PriorityLabel.HIGH
      totalScore >= 40 -> PriorityLabel.MEDIUM
      else -> PriorityLabel.LOW
    }

    return Pair(totalScore, label)
  }
}
