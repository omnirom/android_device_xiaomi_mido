package org.omnirom.device;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import java.util.List;
import java.util.*;
import org.omnirom.device.MiSoundAudioEnhancer;

public class MiSoundAudioEnhancerUtils
{
  private MiSoundAudioEnhancer mAudEnhncr;
  private boolean mInitialized;


  public int getHeadsetType(Context paramContext)
  {
    return mAudEnhncr.getHeadsetType();
  }

  public boolean hasInitialized()
  {
    return mInitialized;
  }

  public void initialize()
  {
    boolean enabled;
    int iEnabled;
    if (!mInitialized)
    {
      mInitialized = true;
      mAudEnhncr = new MiSoundAudioEnhancer(0, 0);
      iEnabled = mAudEnhncr.getMusic();
      if (iEnabled == 1) {
        enabled = true;
      }else {
        enabled = false;
      }
      mAudEnhncr.setEnabled(enabled);
    }
  }

  public boolean isEnabled(Context paramContext)
  {
    int i =1;
    int j = 0;
    j = mAudEnhncr.getMusic();
    if (i == j)
    {
      return true;
    } else {
      return false;
    }
  }

  public void release()
  {
    if (mInitialized)
    {
      mAudEnhncr.release();
      mAudEnhncr = null;
      mInitialized = false;
    }
  }

  public void setEnabled(Context paramContext, boolean paramBoolean)
  {
    int i = 1;
    if (paramBoolean)
    {
      i = 1;
    } else {
      i = 0;
    }
    mAudEnhncr.setEnabled(paramBoolean);
    mAudEnhncr.setMusic(i);
    return;
  }

  public void setHeadsetType(Context paramContext, int paramInt)
  {
    mAudEnhncr.setHeadsetType(paramInt);
  }


  public void setMode(Context paramContext, int paramInt)
  {
    mAudEnhncr.setMode(paramInt);
  }

}

