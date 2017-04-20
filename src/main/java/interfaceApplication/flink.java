package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.flinkModel;


public class flink {
	private flinkModel model = new flinkModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject _obj = new JSONObject();
	
	public flink(){
		map.put("logo", "");
		map.put("desp", "");
		map.put("email", "");
		map.put("time", TimeHelper.nowMillis()+"");
	}
	@SuppressWarnings("unchecked")
	public String flinkAdd(String info) {
		JSONObject object = model.AddMap(map, JSONHelper.string2json(info));
		_obj.put("records", model.addlink(object));
		return model.resultMessage(0, _obj.toString());
	}
//修改友链
	public String UpdateFlink(String mid, String msgInfo) {
		return model.resultMessage(model.updateflink(mid, JSONHelper.string2json(msgInfo)),
				"友链修改成功");
	}

	// 删除友链
	public String DeleteFlink(String mid) {
		return model.resultMessage(model.deleteflink(mid), "删除友链成功");
	}

	// 批量删除友链
	public String DeleteBatchFlink(String mids) {
		return model.resultMessage(model.deleteflink(mids.split(",")), "批量删除友链成功");
	}

	// 搜索友链
	@SuppressWarnings("unchecked")
	public String SearchFlink(String msgInfo) {
		_obj.put("records", model.find(JSONHelper.string2json(msgInfo)));
		return model.resultMessage(0, _obj.toString());
	}

	// 分页
	@SuppressWarnings("unchecked")
	public String PageFlink(int idx, int pageSize) {
		_obj.put("records", model.page(idx, pageSize));
		return model.resultMessage(0, _obj.toString());
	}

	// 条件分页
	@SuppressWarnings("unchecked")
	public String PageByFlink(int idx, int pageSize, String msgInfo) {
		_obj.put("records", model.page(idx, pageSize, JSONHelper.string2json(msgInfo)));
		return model.resultMessage(0, _obj.toString());
	}

}
