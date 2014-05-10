package com.hengxuan.ehealthplatform.http.utils;

import java.io.File;
import java.util.Date;


public class CacheFileItem {

	  private Date cleanTime;
	  private FileService.Directory directory;
	  private File file;
	  private String firstName;
	  private String lastName;
	  private String name;

	  public CacheFileItem()
	  {
	  }
	  
	  public CacheFileItem(File inputfile)
	  {
	    setFile(inputfile);
	  }

	  public CacheFileItem(String name, long timeLen)
	  {
	    setName(name);
	    this.cleanTime = new Date(new Date().getTime() + timeLen);
	  }

	  public Date getCleanTime()
	  {
	    return this.cleanTime;
	  }

	  public FileService.Directory getDirectory()
	  {
	    return this.directory;
	  }

	  public File getFile()
	  {
	    if ((this.file == null) && (getDirectory() != null))
	    {
	    	this.file = new File(getDirectory().getDir(), getName());
	    }
	    return this.file;
	  }

	  public String getFirstName()
	  {
	    return this.firstName;
	  }

	  public String getLastName()
	  {
	    return this.lastName;
	  }

	  public String getName()
	  {
	    if (this.name == null)
	    {
	      StringBuilder localStringBuilder = new StringBuilder(this.firstName).append(".");
	      this.name = localStringBuilder.append(this.lastName).toString();
	    }
	    return this.name;
	  }

	  public void setCleanTime(Date date)
	  {
	    this.cleanTime = date;
	  }

	  public void setDirectory(FileService.Directory dir)
	  {
	    this.directory = dir;
	  }

	  public void setFile(File paramFile)
	  {
	    String str = paramFile.getName();
	    setName(str);
	    this.file = paramFile;
	  }

	  public void setFirstName(String fName)
	  {
	    this.firstName = fName;
	  }

	  public void setLastName(String lName)
	  {
	    this.lastName = lName;
	  }

	  public void setName(String name)
	  {
	    this.name = name;
	    int i = name.lastIndexOf(".");
	    String str1 = name.substring(0, i);
	    this.firstName = str1;
	    int j = i + 1;
	    String str2 = name.substring(j);
	    this.lastName = str2;
	  }

}
