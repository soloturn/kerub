package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.packagemanager.cygwin.CygCheck
import com.github.kerubistan.kerub.utils.junix.packagemanager.cygwin.Wmic
import org.apache.sshd.client.session.ClientSession

class CygwinPackageManager(private val session: ClientSession) : PackageManager {

	companion object {
		val packageMatchers = mapOf(
				"Oracle VM VirtualBox.*".toRegex() to "virtualbox"
		)
	}

	override fun install(vararg pack: String) {
		notImplemented()
	}

	override fun remove(vararg pack: String) {
		notImplemented()
	}

	private fun notImplemented() {
		throw NotImplementedError("Can't install/remove packages in cygwin")
	}

	override fun list(): List<SoftwarePackage> =
			CygCheck.listPackages(session) + consolidate(Wmic.list(session))

	private fun consolidate(list: List<SoftwarePackage>): List<SoftwarePackage> {
		//a registry of known windows applications
		return list.map {
			winSoftware ->
			val pattern = packageMatchers.keys.firstOrNull {
				winSoftware.name.matches(it)
			}
			if (pattern == null) {
				winSoftware
			} else {
				SoftwarePackage(name = requireNotNull(packageMatchers[pattern]), version = winSoftware.version)
			}

		}
	}

}