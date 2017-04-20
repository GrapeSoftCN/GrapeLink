package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import model.MCompanyModel;

public class MCompany {
	private MCompanyModel model = new MCompanyModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject _obj = new JSONObject();

	public MCompany() {
		map.put("companyDesp", "");
		map.put("companyURL", "");
		map.put("companyTele", "");
		map.put("companyMob", 0);
		map.put("companyEmail", "0");
		map.put("companyQQ", 0);
		map.put("companyPerson", 0);
		map.put("type", 0);
		map.put("wbid", 0);
	}

	@SuppressWarnings("unchecked")
	public String AddComp(String compInfo) {
		JSONObject object = model.AddMap(map, JSONHelper.string2json(compInfo));
		_obj.put("records", JSONHelper.string2json(model.addComp(object)));
		return model.resultMessage(0, _obj.toString());
	}

	public String updateComp(String cid, String comInfo) {
		return model.resultMessage(model.updateComp(cid, JSONHelper.string2json(comInfo)),
				"维护单位信息修改成功");
	}

	public String DeleteComp(String cid) {
		return model.resultMessage(model.deleteComp(cid), "维护单位信息删除成功");
	}

	public String DeleteBatchComp(String cid) {
		return model.resultMessage(model.deleteCompe(cid.split(",")), "批量删除成功");
	}

	@SuppressWarnings("unchecked")
	public String SearchComp(String Info) {
		_obj.put("records", model.find(JSONHelper.string2json(Info)));
		return model.resultMessage(0, _obj.toString());
	}

	// 分页
	@SuppressWarnings("unchecked")
	public String PageMComp(int idx, int pageSize) {
		_obj.put("records", model.page(idx, pageSize));
		return model.resultMessage(0, _obj.toString());
	}

	// 条件分页
	@SuppressWarnings("unchecked")
	public String PageByMComp(int idx, int pageSize, String msgInfo) {
		_obj.put("records", model.page(idx, pageSize,JSONHelper.string2json(msgInfo)));
		return model.resultMessage(0, _obj.toString());
	}
}
