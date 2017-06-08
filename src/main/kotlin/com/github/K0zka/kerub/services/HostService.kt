package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostPubKey
import com.github.K0zka.kerub.security.admin
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Api("s/r/host", description = "Host service")
@Path("/host")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiresAuthentication
@RequiresRoles(admin)
interface HostService : RestCrud<Host>, RestOperations.List<Host>, RestOperations.SimpleSearch<Host> {

	@ApiOperation("Add new object")
	@ApiResponses(
			ApiResponse(code = 200, message = "OK"),
			ApiResponse(code = 403, message = "Security error")
	)
	@PUT
	@Path("/join")
	fun join(@ApiParam(value = "New host with password", required = true) hostPwd: HostAndPassword): Host

	@PUT
	@Path("/join-pubkey")
	fun joinWithoutPassword(details: HostJoinDetails): Host

	@ApiOperation("Get the public key of the server", httpMethod = "GET")
	@GET
	@Path("/helpers/pubkey")
	fun getHostPubkey(@QueryParam("address") address: String): HostPubKey

	@ApiOperation("Find host by address", httpMethod = "GET")
	@GET
	@Path("/byaddress/{address}")
	fun getByAddress(@PathParam("address") address: String): List<Host>

	@ApiOperation("Get the public key of kerub", httpMethod = "GET", produces = MediaType.TEXT_PLAIN)
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/helpers/controller-pubkey")
	fun getPubkey(): String
}
