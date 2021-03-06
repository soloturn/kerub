package com.github.kerubistan.kerub.host

import org.junit.Test
import kotlin.test.assertNotNull

class KeyPairFactoryTest {
	@Test
	fun createKeyPair() {
		val factory = KeyPairFactory()
		factory.keyStorePath = "testkeystore.jks"
		factory.certificatePassword = "password"
		factory.keyStorePassword = "password"
		factory.alias = "kerub.testkey"
		assertNotNull(factory.createKeyPair())
	}

	@Test(expected= IllegalArgumentException::class)
	fun createKeyPairFail() {
		val factory = KeyPairFactory()
		factory.keyStorePath = "notexisting.jks"
		factory.createKeyPair()
	}

}