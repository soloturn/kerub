package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.security.Roles
import org.apache.shiro.authz.annotation.RequiresAuthentication
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON) interface LoginService {
	data class UserData(
			val name: String,
			val roles: List<Roles>
	)

	data class UsernamePassword(
			val username: String,
			val password: String
	)

	@Path("login")
	@POST
	fun login(authentication: UsernamePassword)

	@RequiresAuthentication
	@GET
	@Path("user")
	fun getUser(): UserData

	@Path("logout")
	@POST
	fun logout()
}