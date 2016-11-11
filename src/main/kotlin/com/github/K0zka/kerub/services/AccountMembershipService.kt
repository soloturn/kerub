package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.AccountMembership
import com.github.K0zka.kerub.security.admin
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.authz.annotation.RequiresUser
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/account-membership/")
@RequiresAuthentication
interface AccountMembershipService {

	@PUT
	@Path("{accountId}/{membershipId}")
	@RequiresAuthentication
	fun add(
			@PathParam("accountId") accountId: UUID,
			@PathParam("membershipId") membershipId: UUID,
			accountMembership: AccountMembership
	)

	@DELETE
	@RequiresAuthentication
	@Path("{accountId}/{membershipId}")
	fun remove(@PathParam("accountId") accountId: UUID, @PathParam("membershipId") membershipId: UUID)

	@RequiresAuthentication
	@RequiresUser
	@GET
	@Path("/{accountId}/members")
	fun list(@PathParam("accountId") accountId: UUID): List<AccountMembership>

	@RequiresAuthentication
	@RequiresUser
	@GET
	@Path("/of/{userName}")
	fun listUserAccounts(@PathParam("userName") userName: String): List<AccountMembership>
}