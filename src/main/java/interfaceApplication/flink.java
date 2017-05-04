package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.flinkModel;
import rpc.execRequest;

public class flink {
	private flinkModel model = new flinkModel();
	private HashMap<String, Object> map = new HashMap<>();
	private String userid;

	public flink() {
		userid = execRequest.getChannelValue("Userid").toString();

		map.put("logo", "");
		map.put("desp", "");
		map.put("email", "");
		map.put("rplv", 1000);
		map.put("uplv", 2000);
		map.put("dplv", 3000);
		map.put("time", TimeHelper.nowMillis() + "");
	}

	public String flinkAdd(String info) {
		// 该用户是否拥有新增权限
		String tip = execRequest
				._run("GrapeAuth/Auth/InsertPLV/s:" + userid, null).toString();
		if (!"0".equals(tip)) {
			return model.resultMessage(3, "");
		}
		JSONObject object = model.AddMap(map, JSONHelper.string2json(info));
		return model
				.resultMessage(JSONHelper.string2json(model.addlink(object)));
	}

	// 修改友链
	public String UpdateFlink(String mid, String msgInfo) {
		String uPLV = model.FindByID(mid).get("uplv").toString();
		String tip = execRequest
				._run("GrapeAuth/Auth/UpdatePLV/s:" + uPLV + "/s:" + userid,
						null)
				.toString();
		if (!"0".equals(tip)) {
			return model.resultMessage(4, "没有编辑权限");
		}
		return model.resultMessage(
				model.updateflink(mid, JSONHelper.string2json(msgInfo)),
				"友链修改成功");
	}

	// 删除友链
	public String DeleteFlink(String mid) {
		String dPLV = model.FindByID(mid).get("dplv").toString();
		String tip = execRequest
				._run("GrapeAuth/Auth/UpdatePLV/s:" + dPLV + "/s:" + userid,
						null)
				.toString();
		if (!"0".equals(tip)) {
			return model.resultMessage(5, "没有删除权限");
		}
		return model.resultMessage(model.deleteflink(mid), "删除友链成功");
	}

	// 批量删除友链
	public String DeleteBatchFlink(String mids) {
		return model.resultMessage(model.deleteflink(mids.split(",")),
				"批量删除友链成功");
	}

	// 搜索友链
	public String SearchFlink(String msgInfo) {
		return model.resultMessage(model.find(JSONHelper.string2json(msgInfo)));
	}

	// 分页
	public String PageFlink(int idx, int pageSize) {
		return model.resultMessage(model.page(idx, pageSize));
	}

	// 条件分页
	public String PageByFlink(int idx, int pageSize, String msgInfo) {
		return model.resultMessage(
				model.page(idx, pageSize, JSONHelper.string2json(msgInfo)));
	}

	// 查询所属某个网站的flink
	public String findLink(String wbid) {
		return model.resultMessage(model.FindByWBID(wbid));
	}
}
