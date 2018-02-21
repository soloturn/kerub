package com.github.kerubistan.kerub.model

import java.util.UUID
import kotlin.reflect.KClass

/**
 * A pool of virtual machines based on a template.
 */
data class Pool(
		override val id: UUID,
		override val name: String,
		override val owner: AssetOwner?,
		val templateId: UUID,
		override val expectations: List<PoolExpectation>
) : Entity<UUID>, Named, Asset, Constrained<PoolExpectation> {
	override fun references(): Map<KClass<out Asset>, List<UUID>> =
			mapOf(Template::class to listOf(templateId))
}
