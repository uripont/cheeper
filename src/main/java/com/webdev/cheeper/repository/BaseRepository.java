package com.webdev.cheeper.repository;

import com.webdev.cheeper.util.DBManager;

public abstract class BaseRepository implements AutoCloseable  {
	
	protected DBManager db;

    protected BaseRepository() {
    	try {
    		this.db = new DBManager();
        } catch (Exception e) {
			e.printStackTrace();
        }
    	
    }

    public void close() {
        try {
        	if (db != null) db.close();
        } catch (Exception e) {
			e.printStackTrace();
        }
    }
    
}
