package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class MCompanyModel {
	private static DBHelper comp;
	private static formHelper form;

	static {
		comp = new DBHelper("mongodb", "ManageComp");
		form = comp.getChecker();
	}

	public MCompanyModel() {
		form.putRule("companyName", formdef.notNull);
	}

	public String addComp(JSONObject object) {
		if (!form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		String info = comp.data(object).insertOnce().toString();
		return FindcomByID(info).toString();
	}

	public int updateComp(String mid, JSONObject object) {
		return comp.eq("_id", new ObjectId(mid)).data(object).update() != null ? 0 : 99;
	}

	public int deleteComp(String mid) {
		return comp.eq("_id", new ObjectId(mid)).delete() != null ? 0 : 99;
	}

	public int deleteCompe(String[] mids) {
		comp = (DBHelper) comp.or();
		for (int i = 0; i < mids.length; i++) {
			comp.eq("_id", new ObjectId(mids[i]));
		}
		return comp.delete() != null ? 0 : 99;
	}

	public JSONArray find(JSONObject Info) {
		for (Object object2 : Info.keySet()) {
			comp.eq(object2.toString(), Info.get(object2.toString()));
		}
		return comp.select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = comp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) comp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			comp.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		JSONArray array = comp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) comp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	public JSONObject FindcomByID(String mid) {
		return comp.eq("_id", new ObjectId(mid)).find();
	}

	public JSONObject FindcomBytype(int type) {
		return comp.eq("type", type).find();
	}

	public String getID() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
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

	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填项没有填";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
