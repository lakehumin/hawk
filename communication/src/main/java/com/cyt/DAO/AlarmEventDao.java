package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.cyt.Bean.AlarmEventBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class AlarmEventDao {
	//Ôö
	public boolean add(AlarmEventBean aeb)
	{
		boolean b=false;
		String sql="insert into alarm(terminal_id,event,eventdate) values(?,?,?)";
		String []parameters={aeb.getTerminal_id(),aeb.getEvent(),aeb.getEventdate()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//²éÑ¯
	public  ArrayList<AlarmEventBean>Search(String terminal_id,String eventstate)
	{
		ArrayList<AlarmEventBean> aeblst=new ArrayList<AlarmEventBean>();
		String sql="select * from alarm where terminal_id="+"'"+terminal_id+"'"+"and state="+eventstate;
		System.out.println("sql="+sql);
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				AlarmEventBean temp=new AlarmEventBean();
				temp.setTerminal_id(rs.getString("terminal_id"));
				temp.setEvent(rs.getString("event"));
				temp.setEventdate(rs.getString("eventdate"));
				temp.setEventstate(rs.getString("state"));
				aeblst.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return aeblst;
	}
}
