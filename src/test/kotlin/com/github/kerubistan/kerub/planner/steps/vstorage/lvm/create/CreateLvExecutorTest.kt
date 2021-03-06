package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Ignore
import org.junit.Test
import java.util.UUID

class CreateLvExecutorTest {

	val hostCommandExecutor : HostCommandExecutor = mock()
	val virtualDiskDynDao: VirtualStorageDeviceDynamicDao = mock()
	var session : ClientSession = mock()
	var execChannel : ChannelExec = mock()
	var openFuture : OpenFuture = mock()


	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "system disk",
			size = "16 GB".toSize()
	)

	val lv = LogicalVolume(
			id = UUID.randomUUID().toString(),
			layout = "",
			name = "test-lv",
			path = "/dev/test/test-lv",
			size = "128 MB".toSize(),
			dataPercent = null,
			maxRecovery = 0,
			minRecovery = 0
	)

	@Test
	@Ignore("not functional")
	fun execute() {

		whenever(hostCommandExecutor.execute<LogicalVolume>(eq(host), any())).thenReturn(lv)

		CreateLvExecutor(hostCommandExecutor, virtualDiskDynDao).execute(
				CreateLv(
						host = host,
						disk = vDisk,
						capability = testLvmCapability
				)
		)
	}

}