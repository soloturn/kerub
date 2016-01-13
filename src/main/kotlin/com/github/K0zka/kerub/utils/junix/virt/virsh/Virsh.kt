package com.github.K0zka.kerub.utils.junix.virt.virsh

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.dmi.substringBetween
import com.github.K0zka.kerub.utils.rows
import com.github.K0zka.kerub.utils.silent
import com.github.K0zka.kerub.utils.toBigInteger
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import java.io.OutputStream
import java.io.StringReader
import java.util.Properties
import java.util.UUID

object Virsh {

	val logger = getLogger(Virsh::class)

	fun create(session: ClientSession, id: UUID, domainDef: String) {
		val domainDefFile = "/tmp/$id.xml"
		logger.info("creating domain: \n {}", domainDef)
		session.createSftpClient().use {
			sftp ->
			try {
				sftp.write(domainDefFile).use {
					file ->
					file.write(domainDef.toByteArray("UTF-8"))
				}
				session.executeOrDie("virsh create $domainDefFile")
			} finally {
				silent { sftp.remove(domainDefFile) }
			}
		}
	}

	fun destroy(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh destroy $id  --graceful")
	}

	fun list(session: ClientSession): List<UUID>
			= session.executeOrDie("virsh list --uuid").rows().map { UUID.fromString(it) }

	fun suspend(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh suspend $id")
	}

	fun resume(session: ClientSession, id: UUID) {
		session.executeOrDie("virsh resume $id")
	}

	private val domstatsCommand = "virsh domstats --raw"

	fun domStat(session: ClientSession): List<DomainStat> {
		return parseDomStats(session.executeOrDie(domstatsCommand))
	}

	internal fun parseDomStats(output: String) = output.split("\n\n").filter { it.isNotBlank() }.map { toDomStat(it.trim()) }

	internal class DomStatsOutputHandler(private val handler: (List<DomainStat>) -> Unit) : OutputStream() {

		private val buff = StringBuilder()

		override fun write(data: Int) {
			buff.append(data.toChar())
			if (buff.length > 2 && buff.endsWith("\n\n\n")) {
				handler(parseDomStats(buff.toString()))
				buff.setLength(0)
			}
		}
	}

	fun domStat(session: ClientSession, callback: (List<DomainStat>) -> Unit) {
		val channel = session.createExecChannel("""bash -c "while true; do $domstatsCommand; sleep 1; done" """)
		channel.out = DomStatsOutputHandler(callback)
		channel.`in` = NullInputStream(0)
		channel.open()
	}


	internal fun toDomStat(virshDomStat: String): DomainStat {
		val header = virshDomStat.substringBefore('\n')
		val propertiesSource = virshDomStat.substringAfter('\n')
		val props = StringReader(propertiesSource).use {
			val tmp = Properties()
			tmp.load(it)
			tmp
		}
		val vcpuMax = requireNotNull(props.getProperty("vcpu.maximum")).toInt()
		val netCount = props.getProperty("net.count")?.toInt() ?: 0
		return DomainStat(
				name = header.substringBetween("Domain: '", "'"),
				balloonMax = props.getProperty("balloon.maximum")?.toBigInteger(),
				balloonSize = props.getProperty("balloon.current")?.toBigInteger(),
				vcpuMax = vcpuMax,
				netStats = (0..netCount - 1).map {
					netId ->
					toNetStat(
							props.filter { it.key.toString().startsWith("net.$netId.") }
									.map { it.key.toString() to it.value.toString() }
									.toMap()
							, netId
					)
				},
				cpuStats = (0..vcpuMax - 1).map {
					vcpuid ->
					toCpuStat(props
							.filter { it.key.toString().startsWith("vcpu.$vcpuid.") }
							.map { it.key.toString() to it.value.toString() }
							.toMap(), vcpuid)
				}
		)
	}

	private fun toNetStat(props: Map<String, String>, netId: Int) = NetStat(
			name = requireNotNull(props["net.$netId.name"]),
			received = toNetTrafficStat(props, netId, "rx"),
			sent = toNetTrafficStat(props, netId, "tx")
	)

	fun netToLong(props: Map<String, String>, netId: Int, type: String, prop: String) = requireNotNull(props["net.$netId.$type.$prop"]).toLong()

	private fun toNetTrafficStat(props: Map<String, String>, netId: Int, type: String): NetTrafficStat = NetTrafficStat(
			bytes = netToLong(props, netId, type, "bytes"),
			drop = netToLong(props, netId, type, "drop"),
			errors = netToLong(props, netId, type, "errs"),
			packets = netToLong(props, netId, type, "pkts")
	)

	internal fun toCpuStat(props: Map<String, String>, id: Int): VcpuStat =
			VcpuStat(
					state = VcpuState.running,
					time = props["vcpu.$id.time"]?.toLong()
			)

}
