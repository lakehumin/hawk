package com.lake.api.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lake.api.dao.TerminaldevDao;
import com.lake.api.model.Terminaldev;
import com.lake.api.service.TerminaldevService;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午6:13:24
 */

@Service("terminaldevService") 
public class TerminaldevServiceImpl implements TerminaldevService {
	
	@Resource
	private TerminaldevDao terminaldevDao;

	@Override
	public Terminaldev getTerminalInfo(String id) {
		return terminaldevDao.searchById(id);
	}

	@Override
	public List<Terminaldev> getAllTerminalInfo() {
		return terminaldevDao.searchAllInfo();
	}
	
	@Override
	public void deleteTerminalById(String id) {
		terminaldevDao.deleteById(id);
	}
	
	@Override
	public void updateTerminalInfo(Terminaldev t) {
		terminaldevDao.update(t);
	}
	
	@Override
	public void insertTerminal(Terminaldev t) {
		terminaldevDao.insert(t);
	}
}
