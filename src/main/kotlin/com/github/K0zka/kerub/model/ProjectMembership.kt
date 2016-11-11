package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.Field
import java.util.UUID

data class ProjectMembership(
		override val id: UUID,
		@Field
		override val user: String,
		override val groupId: UUID,
		override val quota: Quota?
) : Entity<UUID>, GroupMembership {
	val groupIdStr: String
		@JsonIgnore
		@Field
		get() = id.toString()
}