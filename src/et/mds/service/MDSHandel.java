package et.mds.service;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import et.mds.bc.HandelReleaseChange;
import et.mds.bc.InitPlaceOrder;
import et.mds.util.ResponseBuilder;
import et.mds.vo.ReleaseVO;

/**
 * 发布量对外服务
 * 
 * 创建日期：(2014-5-21)
 * 
 * @author ZXH
 * 
 */
@Path("mds")
public class MDSHandel {
	private static Logger log = Logger.getLogger(MDSHandel.class);

	@PUT
	@Path("test")
	public String test(@FormParam("name") final String name) {
		return name + ":testPUT";
	}
	@GET
	@Path("test")
	public String testget(@QueryParam("name") final String name) {
		return name + ":testGET";
	}
	/**
	 * 
	 * 处理对键值的查询，可以直接访问nginx
	 * Handel query value fo key ,can direct to visit nginx
	 * */
	@GET
	@Path("keysValue")
	public String getCountUseKeys(@QueryParam("keys") final String keys) {
		JSONArray arr = JSONArray.fromObject(keys);
		ArrayList<String> proids = new ArrayList<String>();
		for (Object pro_id : arr) {
			proids.add(pro_id.toString());
		}
		log.info("手机端刷新可下单量");
		@SuppressWarnings("unchecked")
		ArrayList<String> ret = HandelReleaseChange.getDiffQuantityArr(proids);
		return JSONArray.fromObject(ret).toString();
	}

	/**
	 * Handle change request,main useage palceorder or out-put and in-put of warehouse
	 * 处理修改请求，主要用于下单、或出入库对发布量的修改。
	 * 
	 * */
	@PUT
	@Path("handelChange")
	public Response HandelChangeService(@FormParam("rMap") final String rMap) {
		JSONObject reqMsg = JSONObject.fromObject(rMap);
		String reg_key = reqMsg.get("reg_key") == null ? null : reqMsg.get(
				"reg_key").toString();
		if (!ResourceBundle.getBundle("regmds").containsKey(reg_key)) {
			log.error(reg_key + "此类尚未注册-----------------------");
			return ResponseBuilder.error(reg_key + "此类尚未注册-----------------------");
		}
		String vosString = reqMsg.get("vos") == null ? null : reqMsg.get("vos")
				.toString();
		ReleaseVO[] vos = (ReleaseVO[]) JSONArray.toArray(
				JSONArray.fromObject(vosString), ReleaseVO.class);

		String group_id = reqMsg.get("group_id") == null ? null : reqMsg.get(
				"group_id").toString();

		String isAdd = reqMsg.get("isAdd") == null ? null : reqMsg.get("isAdd")
				.toString();
		String isNegative = reqMsg.get("isNegative") == null ? "false" : reqMsg
				.get("isNegative").toString();
		String isNoKeyToAdd = reqMsg.get("isNoKeyToAdd") == null ? "false"
				: reqMsg.get("isNoKeyToAdd").toString();
		int expire = reqMsg.get("expire") == null ? -1 : Integer
				.parseInt(reqMsg.get("expire").toString());
		@SuppressWarnings("rawtypes")
		ArrayList list = HandelReleaseChange.modifiDiffQuantitySuperVOS(vos,
				group_id, Boolean.parseBoolean(isAdd),
				Boolean.parseBoolean(isNegative),
				Boolean.parseBoolean(isNoKeyToAdd),expire);
		return ResponseBuilder.success(JSONArray.fromObject(list).toString());
	}

	/**
	 * 
	 * 处理其他平台发布式请求，用于重置发布量
	 * */
	@POST
	@Path("initRelease")
	public void initRelease(@FormParam("rvos") final String rvos,
			@FormParam("group") final String group) {
		ReleaseVO[] vos = (ReleaseVO[]) JSONArray.toArray(
				JSONArray.fromObject(rvos), ReleaseVO.class);
		InitPlaceOrder.InitReleaseFromGroup(vos, group);

	}
	/**
	 * 删除 FIXME add content
	 * */
	@DELETE
	@Path("")
	public String deleteKey(){
		return null;
		
	}

	public static void main(String[] args) {
		String str = "{\"isNoKeyToAdd\":\"true\",\"isNegative\":\"true\",\"group_id\":\"\",\"vos\":[{\"main_key\":\"a7882829-61d8-402c-8c97-e9068bde6860\",\"ret_msg\":\"\",\"value\":\"10\"}],\"isadd\":false}";
		JSONObject reqMsg = JSONObject.fromObject(str);
		System.out.println(reqMsg.get("isNoKeyToAdd"));
	}
}
