package com.github.K0zka.kerub.model

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hibernate.search.annotations.DocumentId

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
JsonTypeInfo(use=JsonTypeInfo.Id.NAME , include=JsonTypeInfo.As.PROPERTY, property="@type")
data interface Entity<T> : Serializable {
	DocumentId
	val id : T
}