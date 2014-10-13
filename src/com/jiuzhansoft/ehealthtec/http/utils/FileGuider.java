package com.jiuzhansoft.ehealthtec.http.utils;

public class FileGuider
{
  private long availableSize;
  private long totalSize;
  private String childDirName;
  private String fileName;
  private boolean immutable;
  private int internalType;
  private int mode;
  private int space;

  public long getAvailableSize()
  {
    return this.availableSize;
  }

  public String getChildDirName()
  {
    return this.childDirName;
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public int getInternalType()
  {
    return this.internalType;
  }

  public int getMode()
  {
    return this.mode;
  }

  public int getSpace()
  {
    return this.space;
  }

  public long getTotalSize()
  {
    return this.totalSize;
  }

  public boolean isImmutable()
  {
    return this.immutable;
  }

  public void setAvailableSize(long availableSize)
  {
    this.availableSize = availableSize;
  }

  public void setChildDirName(String childDirName)
  {
    this.childDirName = childDirName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public void setImmutable(boolean immutable)
  {
    this.immutable = immutable;
  }

  public void setInternalType(int internalType)
  {
    this.internalType = internalType;
  }

  public void setMode(int mode)
  {
    this.mode = mode;
  }

  public void setSpace(int space)
  {
    this.space = space;
  }

  public void setTotalSize(long totalSize)
  {
    this.totalSize = totalSize;
  }
}
