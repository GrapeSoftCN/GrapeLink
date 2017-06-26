package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.flinkModel;
import rpc.execRequest;
import session.session;

public class flink {
	private flinkModel model = new flinkModel();
	private HashMap<String, Object> map = new HashMap<>();
	private static session session = new session();

	public flink() {
		JSONObject userInfo = new JSONObject();
		String sid = (String) execRequest.getChannelValue("sid");
		if (sid != null) {
			userInfo = session.getSession(sid);
		}
		map.put("logo", "");
		map.put("desp", "");
		map.put("email", "");
		map.put("r", 1000);
		map.put("u", 2000);
		map.put("d", 3000);
		map.put("fatherid", "0");
		map.put("time", TimeHelper.nowMillis() + "");
		map.put("wbid", (userInfo !=null && userInfo.size() != 0 )? userInfo.get("currentWeb") : "");
	}

	public String flinkAdd(String info) {
		// // 该用户是否拥有新增权限
		// String tip = execRequest
		// ._run("GrapeAuth/Auth/InsertPLV/s:" + userid, null).toString();
		// if (!"0".equals(tip)) {
		// return model.resultMessage(3, "");
		// }
		JSONObject object = model.AddMap(map, JSONHelper.string2json(info));
		return model.addlink(object);
	}

	// 修改友链
	public String UpdateFlink(String mid, String msgInfo) {
		// String uPLV = model.FindByID(mid).get("uplv").toString();
		// String tip = execRequest
		// ._run("GrapeAuth/Auth/UpdatePLV/s:" + uPLV + "/s:" + userid,
		// null)
		// .toString();
		// if (!"0".equals(tip)) {
		// return model.resultMessage(4, "没有编辑权限");
		// }
		return model.updateflink(mid, JSONHelper.string2json(msgInfo));
	}

	// 删除友链
	public String DeleteFlink(String mid) {
		// String dPLV = model.FindByID(mid).get("dplv").toString();
		// String tip = execRequest
		// ._run("GrapeAuth/Auth/UpdatePLV/s:" + dPLV + "/s:" + userid,
		// null)
		// .toString();
		// if (!"0".equals(tip)) {
		// return model.resultMessage(5, "没有删除权限");
		// }
		return model.deleteflink(mid);
	}

	// 批量删除友链
	public String DeleteBatchFlink(String mids) {
		return model.deleteflink(mids.split(","));
	}

	// 搜索友链
	public String SearchFlink(String msgInfo) {
		return model.find(JSONHelper.string2json(msgInfo));
	}

	// 分页
	public String PageFlink(int idx, int pageSize) {
		return model.page(idx, pageSize);
	}

	// 条件分页
	public String PageByFlink(int idx, int pageSize, String msgInfo) {
		return model.page(idx, pageSize, JSONHelper.string2json(msgInfo));
	}

	// 查询所属某个网站的flink
	public String findLink(String wbid) {
		return model.FindByWBID(wbid);
	}
	//设置父对象
	public String SetFather(String id,String fid) {
		return model.set(id, fid);
	}
}
