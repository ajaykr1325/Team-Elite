package com.example.model

enum class UserRole(val displayName: String) {
  STUDENT("Student"),
  ADMINISTRATOR("Administrator / Dean"),
  STAFF("Staff / Department")
}

data class User(
  val userId: String,
  val name: String,
  val email: String,
  val role: UserRole,
  val department: String? = null,
  val year: String? = null,
  val avatarInitial: String = "E"
)

object DemoAccounts {
  val studentAlex = User(
    userId = "stu-001",
    name = "Alex Rivera",
    email = "alex.rivera@campus.edu",
    role = UserRole.STUDENT,
    year = "Student Representative",
    avatarInitial = "A"
  )

  val studentPriya = User(
    userId = "stu-002",
    name = "Priya Patel",
    email = "priya.patel@campus.edu",
    role = UserRole.STUDENT,
    year = "Student Lead",
    avatarInitial = "P"
  )

  val adminDean = User(
    userId = "adm-001",
    name = "Dr. Robert Vance",
    email = "dean.affairs@campus.edu",
    role = UserRole.ADMINISTRATOR,
    department = "Dean of Student Affairs / Administration",
    avatarInitial = "V"
  )

  val staffMaintenance = User(
    userId = "stf-001",
    name = "Rajesh Sharma",
    email = "rajesh.maintenance@campus.edu",
    role = UserRole.STAFF,
    department = "Campus Maintenance Lead",
    avatarInitial = "S"
  )

  val staffIT = User(
    userId = "stf-002",
    name = "Kevin Lin",
    email = "kevin.it@campus.edu",
    role = UserRole.STAFF,
    department = "Campus Systems Support",
    avatarInitial = "K"
  )

  val all = listOf(studentAlex, studentPriya, adminDean, staffMaintenance, staffIT)
}
