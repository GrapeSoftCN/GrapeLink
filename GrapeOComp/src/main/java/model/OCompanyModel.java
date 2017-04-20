package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.JSONHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;
import rpc.execRequest;

public class OCompanyModel {
	private static DBHelper comp;
	private static formHelper form;

	static {
		comp = new DBHelper("mongodb", "OperateComp");
		form = comp.getChecker();
	}

	public OCompanyModel() {
		form.putRule("companyName", formdef.notNull);
	}

	// public String addOC(JSONObject object) {
	// if (!form.checkRule(object)) {
	// return resultMessage(1, "");
	// }
	// String info = comp.data(object).insertOnce().toString();
	// return find(info).toString();
	// }
	/**
	 * 修改运行单位信息
	 * 
	 * @param OCID
	 * @param object
	 *          type为0表示基本信息，type为1表示组织机构信息，type为2表示人员信息
	 * @return
	 */
	public int update(String OCID, JSONObject object) {
		int code = 99;
		String type = object.get("type").toString();
		// 修改基本信息
		if (type.equals("0")) {
			if (object.containsKey("ownid")) {
				object.remove("ownid");
			}
			code = comp.eq("_id", new ObjectId(OCID)).data(object).update() != null ? 0 : 99;
		}
		// 修改组织机构信息
		if (type.equals("1")) {
			if (!object.containsKey("ownid")) {
				return 2;
			}
			String msg = execRequest._run("GrapeUser/roles/Updates/s:" + OCID
					+ "/s:" + object.toString(), null).toString();
			JSONObject object2 = JSONHelper.string2json(msg);
			code = (int) object2.get("errorcode");
		}
		//修改人员信息
		if (type.equals("2")) {
			if (!object.containsKey("ownid")) {
				return 2;
			}
			String msg = execRequest._run("GrapeUser/user/UserUpdate/s:" + OCID
					+ "/s:" + object.toString(), null).toString();
			JSONObject object2 = JSONHelper.string2json(msg);
			code = (int) object2.get("errorcode");
		}
		return code;
	}

	/**
	 * 新增运行单位机构信息
	 * 
	 * @return
	 */
	public String insert(JSONObject object) {
		if (!object.containsKey("ownid")) {
			return resultMessage(3, ""); // 添加失败，没有绑定的ownid
		}
		String tips = execRequest._run("/GrapeUser/roles/RoleInsert" + object, null)
				.toString();
		return tips;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray arrays = new JSONArray();
		JSONArray array = comp.page(idx, pageSize);
		JSONObject object = new JSONObject();
		for (int i = 0; i < array.size(); i++) {
			JSONObject objects = (JSONObject) array.get(i);
			// 获取组织机构信息
			JSONArray organ = getOgnization(objects);
			if (organ == null) {
				continue;
			}
			// 获取人员信息
			JSONArray person = getPerson(objects);
			objects.put("personnel", person);
			if (person == null) {
				continue;
			}
			objects.put("organization", organ);
			arrays.add(objects);
		}
		object.put("totalSize", (int) Math.ceil((double) comp.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", arrays);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject object) {
		for (Object object2 : object.keySet()) {
			comp.eq(object2.toString(), object.get(object2.toString()));
		}
		JSONArray array = comp.page(idx, pageSize);
		JSONArray arrays = new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			JSONObject objects = (JSONObject) array.get(i);
			// 获取组织机构信息
			JSONArray organ = getOgnization(objects);
			if (organ == null) {
				continue;
			}
			// 获取人员信息
			JSONArray person = getPerson(objects);
			if (person == null) {
				continue;
			}
			objects.put("organization", organ);
			objects.put("personnel", person);
			arrays.add(objects);
		}
		JSONObject _obj = new JSONObject();
		_obj.put("totalSize", (int) Math.ceil((double) comp.count() / pageSize));
		_obj.put("currentPage", idx);
		_obj.put("pageSize", pageSize);
		_obj.put("data", array);
		return _obj;
	}

	/**
	 * 获取组织机构信息
	 * 
	 * @param objects
	 *          运行单位的ownid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getOgnization(JSONObject objects) {
		JSONArray msg = null;
		JSONObject oJsonObject = new JSONObject();
		oJsonObject.put("ownid", objects.get("ownid").toString());
		String orString = execRequest._run("GrapeUser/roles/RoleSearch/s:" + oJsonObject
				.toString(), null).toString();
		if (orString != null) {
			String message = JSONHelper.string2json(orString).get("message").toString();
			String records = JSONHelper.string2json(message).get("records").toString();
			msg = JSONHelper.string2array(records);
		}
		return msg;
	}

	/**
	 * 获取人员信息
	 * 
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getPerson(JSONObject objects) {
		JSONArray msg = null;
		JSONObject oJsonObject = new JSONObject();
		oJsonObject.put("ownid", objects.get("ownid").toString());
		String orString = execRequest._run("GrapeUser/user/UserSearch/s:" + oJsonObject
				.toString(), null).toString();
		if (orString != null) {
			String message = JSONHelper.string2json(orString).get("message").toString();
			String records = JSONHelper.string2json(message).get("records").toString();
			msg = JSONHelper.string2array(records);
		}
		return msg;
	}

	public JSONObject find(String vid) {
		return comp.eq("_id", new ObjectId(vid)).find();
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
		case 2:
			msg = "未设置ownid，未知修改项";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
