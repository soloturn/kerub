package com.github.kerubistan.kerub.utils.junix.acpi

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object Acpi : OsCommand {

	private const val separator = "---end---"

	private fun parseBatteryInfo(input: String): BatteryStatus =
			BatteryStatus(
					batteryId = input.substringBetween("Battery", ":").trim().toInt(),
					batteryState = BatteryState.valueOf(input.substringBetween(":", ",").trim()),
					percent = input.substringBetween(",", "%").trim().toInt()
			)

	fun readBatteryInfo(session: ClientSession): List<BatteryStatus> =
			session.executeOrDie("acpi -b").lines()
					.filterNot(String::isEmpty)
					.map(::parseBatteryInfo)
					.sortedBy { it.batteryId }

	class AcpiBatteryOutputHandler(val handler : (List<BatteryStatus>) -> Unit) : OutputStream() {
		private val buffer = StringBuilder()
		override fun write(data: Int) {
			buffer.append(data.toChar())
			if(buffer.endsWith(separator)) {
				handler(
						buffer.lines().filterNot(String::isEmpty).filter { it != separator }.map(::parseBatteryInfo)
				)
				buffer.clear()
			}
		}
	}

	fun monitorBatteryInfo(session: ClientSession, interval: Int = 1, handler : (List<BatteryStatus>) -> Unit) {
		session.process(
				"""bash -c "while true; do acpi -b; echo $separator; sleep $interval; done" """,
				AcpiBatteryOutputHandler(handler)
		)
	}

}