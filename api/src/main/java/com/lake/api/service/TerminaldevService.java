package com.lake.api.service;

import java.util.List;

import com.lake.api.model.Terminaldev;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午6:12:12
 */
public interface TerminaldevService {

	Terminaldev getTerminalInfo(String id);
	
	List<Terminaldev> getAllTerminalInfo();

	void deleteTerminalById(String id);
	
	void updateTerminalInfo(Terminaldev t);
	
	void insertTerminal(Terminaldev t);
}
