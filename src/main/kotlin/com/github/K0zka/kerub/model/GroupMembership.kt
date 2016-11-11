package com.github.K0zka.kerub.model

import java.util.UUID

interface GroupMembership : Entity<UUID> {
	val user : String
	val groupId : UUID
	val quota : Quota?
}