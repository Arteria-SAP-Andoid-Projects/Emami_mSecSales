package com.arteriatech.emami.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.arteriatech.emami.common.Constants;

import java.util.Enumeration;
import java.util.Hashtable;

/** Helper to the database, manages versions and creation */
public class EventDataSqlHelper {
	private static EventDataSqlHelper acc_List_ = null;
	SQLiteDatabase db;
	EventDataSqlHelper database = null;
	private String delSql;

	public EventDataSqlHelper(Context context) {
		db = Constants.EventUserHandler;

	}


	public void crateTableConfig(String tableName, Hashtable hashtable) {

		String[] clms = hashtoString(hashtable);

		String clumsname = "";
		for (int i = 0; i < clms.length; i++) {
			if (clms[i] != null) {
				clumsname = clumsname + clms[i] + " text ";
			}
			if (i < clms.length - 1) {
				clumsname = clumsname + " ,";
			}

		}

		Constants.deleteTable(db,tableName);

		Constants.createTable(db,tableName,clumsname);

	}

	public void inserthistortTable(String tblName, String inspectionLot,
								   String clmname, String value) {

		Constants.insertHistoryDB(db,tblName,clmname,value);
	}

	public void updateStatus(String tblName, String inspectionLot,
							 String clmname, String value) {

		Constants.updateStatus(db,tblName,clmname,value,inspectionLot);


	}

	private String[] hashtoString(Hashtable hashtable) {

		String[] hashtostring = new String[hashtable.size()];
		if (null != hashtable) {
			int size = hashtable.size();
			if (size > 0) {
				Enumeration enumeration = hashtable.keys();
				if (enumeration.hasMoreElements()) {
					int i = 0;

					while (enumeration.hasMoreElements()) {

						hashtostring[i] = (String) enumeration.nextElement();
						i++;
					}
				}
			}
		}

		return hashtostring;

	}

}