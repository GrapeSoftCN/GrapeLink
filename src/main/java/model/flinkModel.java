package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class flinkModel {
	private static DBHelper flink;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();
	
	static {
		flink = new DBHelper("mongodb", "flink");
		_form = flink.getChecker();
	}

	public flinkModel() {
		_form.putRule("name", formdef.notNull);
		_form.putRule("url", formdef.notNull);
	}

	public String addlink(JSONObject object) {
		if (!_form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		if (object.containsKey("email")) {
			String email = object.get("email").toString();
			if (!checkEmail(email)) {
				return resultMessage(2, ""); // email格式错误
			}
		}
		String info = flink.data(object).insertOnce().toString();
		return FindByID(info).toString();
	}

	public int updateflink(String mid, JSONObject object) {
		return flink.eq("_id", new ObjectId(mid)).data(object).update() != null ? 0 : 99;
	}

	public int deleteflink(String mid) {
		return flink.eq("_id", new ObjectId(mid)).delete() != null ? 0 : 99;
	}

	public int deleteflink(String[] mids) {
		flink.or();
		for (int i = 0; i < mids.length; i++) {
			flink.eq("_id", new ObjectId(mids[i]));
		}
		return flink.deleteAll() == mids.length ? 0 : 99;
	}

	public JSONArray find(JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			flink.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		return flink.limit(10).select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = flink.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) flink.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject Info) {
		for (Object object2 : Info.keySet()) {
			flink.eq(object2.toString(), Info.get(object2.toString()));
		}
		JSONArray array = flink.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) flink.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	/**
	 * 通过唯一标识符_id,查询友链网站信息
	 * 
	 * @param mid
	 * @return
	 */
	public JSONObject FindByID(String mid) {
		return flink.eq("_id", new ObjectId(mid)).find();
	}
	public JSONArray FindByWBID(String wbid){
		return flink.eq("wbid", wbid).limit(20).select();
	}

	@SuppressWarnings("unchecked")
	public boolean checkEmail(String email) {
		_form.putRule("email", formdef.email);
		JSONObject _obj = new JSONObject();
		_obj.put("email", email);
		return _form.checkRule(_obj);
	}
	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map, JSONObject object) {
		if (map.entrySet() != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}
	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}
	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填项没有填";
			break;
		case 2:
			msg = "email格式错误";
			break;
		case 3:
			msg = "没有创建数据权限，请联系管理员进行权限调整";
			break;
		case 4:
			msg = "没有修改数据权限，请联系管理员进行权限调整";
			break;
		case 5:
			msg = "没有删除数据权限，请联系管理员进行权限调整";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
