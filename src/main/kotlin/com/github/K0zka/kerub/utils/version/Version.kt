package com.github.K0zka.kerub.utils.version

import com.github.K0zka.kerub.utils.elemOrNull
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.util.regex.Pattern

public data class Version (Field val major : String,
                           Field val minor: String?,
                           Field val build : String?) : Serializable, Comparable<Version> {
	override fun compareTo(other: Version): Int {
		return -1
	}
	companion object {

		private val empty = ""

		private val versionSplitter = Pattern.compile("\\.|_|\\-")

		public fun fromVersionString(versionStr : String) : Version {
			val split = versionSplitter.split(versionStr)
			return Version(split[0], split.elemOrNull(1), split.elemOrNull(2))
		}
	}
	private fun dot(component : String?) : String? {
		if(component == null) {
			return ".${component}"
		} else {
			return null
		}
	}
	override fun toString() : String {
		return "$major${dot(minor) ?: empty}${dot(build) ?: empty}"
	}
}