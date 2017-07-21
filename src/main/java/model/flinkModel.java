package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;
import apps.appsProxy;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;
import nlogger.nlogger;
import security.codec;

public class flinkModel {
	private DBHelper flink;
	private formHelper _form;
	private JSONObject _obj = new JSONObject();

	private db bind() {
		return flink.bind(String.valueOf(appsProxy.appid()));
	}

	public flinkModel() {
		flink = new DBHelper(appsProxy.configValue().get("db").toString(), "flink");
		_form = flink.getChecker();
		_form.putRule("name", formdef.notNull);
		// _form.putRule("url", formdef.notNull);
	}

	public String addlink(JSONObject object) {
		String info = "";
		String url;
		if (object != null) {
			if (!_form.checkRuleEx(object)) {
				return resultMessage(1); // 必填字段没有填
			}
			if (object.containsKey("email")) {
				String email = object.get("email").toString();
				if (!("").equals(email) && !checkEmail(email)) {
					return resultMessage(2); // email格式错误
				}
			}
			if (object.containsKey("url")) {
				url = object.getString("url");
				object.puts("url", url.equals("") ? "" : codec.DecodeHtmlTag(url));
			}
			info = bind().data(object).insertOnce().toString();
		}
		if (("").equals(info)) {
			return resultMessage(99);
		}
		JSONObject obj = FindByID(info);
		return resultMessage(obj);
	}

	public String updateflink(String mid, JSONObject object) {
		String url;
		if (object.containsKey("url")) {
			url = object.getString("url");
			object.puts("url", url.equals("") ? "" : codec.DecodeHtmlTag(url));
		}
		JSONObject obj = bind().eq("_id", new ObjectId(mid)).data(object).update();
		return obj != null ? resultMessage(0, "修改成功") : resultMessage(99);
	}

	public String deleteflink(String mid) {
		if (mid.contains(",")) {
			return resultMessage(99);
		}
		JSONObject obj = bind().eq("_id", new ObjectId(mid)).delete();
		return obj != null ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String deleteflink(String[] mids) {
		bind().or();
		for (int i = 0; i < mids.length; i++) {
			bind().eq("_id", new ObjectId(mids[i]));
		}
		return bind().deleteAll() == mids.length ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String find(JSONObject fileInfo) {
		JSONArray array = null;
		if (fileInfo != null) {
			try {
				array = new JSONArray();
				for (Object object2 : fileInfo.keySet()) {
					bind().eq(object2.toString(), fileInfo.get(object2.toString()));
				}
				array = bind().limit(10).select();
			} catch (Exception e) {
				nlogger.logout(e);
				array = null;
			}
		}
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = bind().page(idx, pageSize);
			object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize, JSONObject Info) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			for (Object object2 : Info.keySet()) {
				bind().eq(object2.toString(), Info.get(object2.toString()));
			}
			JSONArray array = bind().dirty().page(idx, pageSize);
			object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
			bind().clear();
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);

		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	/**
	 * 通过唯一标识符_id,查询友链网站信息
	 * 
	 * @param mid
	 * @return
	 */
	public JSONObject FindByID(String mid) {
		JSONObject object = bind().eq("_id", new ObjectId(mid)).find();
		return object != null ? object : null;
	}

	public String FindByWBID(String wbid) {
		JSONArray array = null;
		try {
			array = new JSONArray();
			array = bind().eq("wbid", wbid).limit(20).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public boolean checkEmail(String email) {
		_form.putRule("email", formdef.email);
		JSONObject _obj = new JSONObject();
		_obj.put("email", email);
		return _form.checkRule(_obj);
	}

	public String set(String id, String fid) {
		int code = 99;
		String string = "{\"fatherid\":\"" + fid + "\"}";
		code = bind().eq("_id", new ObjectId(id)).data(string).update() != null ? 0 : 99;
		return resultMessage(code, "设置父链接成功");
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
		if (object != null) {
			if (map.entrySet() != null) {
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
					if (!object.containsKey(entry.getKey())) {
						object.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return object;
	}

	private String resultMessage(int num) {
		return resultMessage(num, "");
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONObject object) {
		if (object == null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
		if (array == null) {
			array = new JSONArray();
		}
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}

	private String resultMessage(int num, String message) {
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
