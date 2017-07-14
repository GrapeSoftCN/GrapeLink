package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import json.JSONHelper;
import model.flinkModel;
import rpc.execRequest;
import session.session;
import time.TimeHelper;

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
		JSONObject object = model.AddMap(map, JSONHelper.string2json(info));
		return model.addlink(object);
	}

	// 修改友链
	public String UpdateFlink(String mid, String msgInfo) {
		return model.updateflink(mid, JSONHelper.string2json(msgInfo));
	}

	// 删除友链
	public String DeleteFlink(String mid) {
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
