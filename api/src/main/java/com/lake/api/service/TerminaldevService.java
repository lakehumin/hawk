package com.lake.api.service;

import java.util.List;

import com.lake.api.model.Terminaldev;

/**
 * @author LakeHm
 *
 * 2016��12��13������6:12:12
 */
public interface TerminaldevService {

	Terminaldev getTerminalInfo(String id);
	
	List<Terminaldev> getAllTerminalInfo();
}
