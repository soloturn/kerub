package com.github.kerubistan.kerub.utils.junix.fw.iptables

import com.github.kerubistan.kerub.host.executeOrDie
import org.apache.sshd.client.session.ClientSession

object IpTables {
	fun open(session: ClientSession, port: Int, proto: String = "tcp") {
		session.executeOrDie("iptables -I INPUT -p $proto --dport $port -j ACCEPT")
	}

	fun close(session: ClientSession, port: Int, proto: String = "tcp") {
		session.executeOrDie("iptables -D INPUT -p $proto --dport $port -j ACCEPT")
	}
}