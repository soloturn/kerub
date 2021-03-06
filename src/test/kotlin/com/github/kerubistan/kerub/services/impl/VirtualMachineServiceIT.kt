package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.createServiceClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VirtualMachineServiceIT {
	@Test
	fun crud() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
				password = "password"
		))

		val vmService = createServiceClient(VirtualMachineService::class, client)

		val vmToSave = VirtualMachine(
				id = UUID.randomUUID(),
				name = "test",
				memory = Range(
						min = BigInteger("1024"),
						max = BigInteger("2048")
				),
				nrOfCpus = 1,
				expectations = listOf(
						ClockFrequencyExpectation(
								level = ExpectationLevel.DealBreaker,
								minimalClockFrequency = 1700
						),
						MemoryClockFrequencyExpectation(

								level = ExpectationLevel.Want,
								min = 1700
						),
						CacheSizeExpectation(
								level = ExpectationLevel.Wish,
								minL1 = "1024 KB".toSize().toLong()
						)
				)
		)
		vmService.add(
				vmToSave
		)

		val saved = vmService.getById(vmToSave.id)
		Assert.assertEquals(vmToSave, saved)

		val update = vmToSave.copy(
				nrOfCpus = 2
		)
		vmService.update(update.id, update)
		val updated = vmService.getById(update.id)
		Assert.assertEquals(update, updated)

		vmService.delete(updated.id)
	}

	@Test
	fun security() {
		val client = createClient()
		val vmService = createServiceClient(VirtualMachineService::class, client)

		expect(RestException::class,
				action = { vmService.startVm(UUID.randomUUID()) },
				check = { assertEquals("AUTH1", it.code) }
		)

		expect(RestException::class,
				action = { vmService.delete(UUID.randomUUID()) },
				check = { assertEquals("AUTH1", it.code) }
		)

		expect(RestException::class,
				action = { vmService.stopVm(UUID.randomUUID()) },
				check = { assertEquals("AUTH1", it.code) }
		)

		expect(RestException::class,
				action = { vmService.listByVirtualDisk(UUID.randomUUID()) },
				check = { assertEquals("AUTH1", it.code) }
		)

	}

	@Test
	fun autoName() {
		val client = createClient()
		val loginService = createServiceClient(LoginService::class, client)
		loginService.login(LoginService.UsernamePassword(
				username = "admin",
				password = "password"
		))

		val vmService = createServiceClient(VirtualMachineService::class, client)

		val name = vmService.autoName()

		assertTrue { name.matches("vm-.*".toRegex()) }
	}
}