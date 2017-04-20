package interfaceApplication;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import model.OCompanyModel;

public class OCompany {
	private OCompanyModel model = new OCompanyModel();
	private JSONObject _obj = new JSONObject();

	// 分页
	@SuppressWarnings("unchecked")
	public String OCompPage(int ids, int pageSize) {
		_obj.put("records", model.page(ids, pageSize));
		return model.resultMessage(0, _obj.toString());
	}

	// 按条件分页（条件格式 [k:v]）
	@SuppressWarnings("unchecked")
	public String OCompPageBy(int ids, int pageSize, String json) {
		_obj.put("records", model.page(ids, pageSize, JSONHelper.string2json(json)));
		return model.resultMessage(0, _obj.toString());
	}

	// 修改运行单位信息
	public String OCompUpdate(String id, String info) {
		return model.resultMessage(model.update(id, JSONHelper.string2json(info)), "修改成功");
	}
}
