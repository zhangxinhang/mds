package et.mds.util;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResponseBuilder {

	public static Response success(String msg){
		return Response.status(200).entity(msg).build();
	}
	
	public static Response error(String msg){
		return Response.status(500).entity(msg).header(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON + ";charset=utf8").build();
	}
}
