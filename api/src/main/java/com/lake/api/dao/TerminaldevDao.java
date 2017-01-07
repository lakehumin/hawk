package com.lake.api.dao;

import java.util.List;
import com.lake.api.model.Terminaldev;

/**
 * @author LakeHm
 *
 * 2016��12��13������6:10:55
 */
public interface TerminaldevDao {
	
	Terminaldev searchById(String id);
	
	List<Terminaldev> searchAllInfo();
	
	void deleteById(String id);
	
	void update(Terminaldev t);
	
	void insert(Terminaldev t);
}
