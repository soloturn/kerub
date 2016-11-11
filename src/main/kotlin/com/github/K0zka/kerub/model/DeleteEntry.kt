package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

@JsonTypeName("audit-delete")
data class DeleteEntry(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		@JsonProperty("date")
		override val date: Long = System.currentTimeMillis(),
		@Field
		@JsonProperty("user")
		override val user: String?,
		@Field
		@JsonProperty("old")
		val old: Entity<*>
) : AuditEntry {
	override val idStr: String
		@Field(analyze = Analyze.NO)
		@JsonIgnore
		get() = old.id.toString()
}