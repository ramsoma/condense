package newshog.preprocess;

import java.sql.*;

public class HistoryCorpusWordWeight implements WordWeight{

	boolean init = false;
	int max = 0;
	String connURL = "jdbc:mysql://localhost:3306/newshog";
	Connection conn;
	
	static
	{
		try{
		  Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		}catch(Exception ex){}
	}
	
	private void init()
	{		
		String sql = "select max(totFreq) as max from tblTotalCnt";		
		try{
			Connection c = getConnection();
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				max = rs.getInt("max");
				init = true;
			}			
		}catch(Exception ex)
		{
			
		}
	}
	
	private Connection getConnection()
	{
		try{
			if(conn==null || conn.isClosed())
			{
				conn = DriverManager.getConnection(connURL, "root","WelJRBS");
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			conn = null;				
		}
		return conn;
	}
	
	@Override
	public double getWordWeight(String s) {
		if(!init) init();
		if(s.trim().length()==1) return 0;
		String sql = "select log( "+ max+"/ totFreq) as wordWt from tblTotalCnt where word='"+s.toLowerCase()+"'";
		double ret = Math.log(max/7);
		try{
			Connection c = getConnection();
			if(c!=null)
			{
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				if(rs.next())
				{
					ret = rs.getDouble("wordWt");
				}				
			}
		}catch(Exception ex)
		{
			//do nothing
			//ex.printStackTrace();
		}
	//	System.out.println(s+" "+ ret);
		return ret;
	}

}
