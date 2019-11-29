package land.face.foods.managers;

import land.face.foods.FoodsPlugin;
import land.face.strife.data.buff.LoadedBuff;

public class BuffManager {

  private FoodsPlugin plugin;

  public BuffManager(FoodsPlugin plugin) {
    this.plugin = plugin;
  }

  public LoadedBuff getStrifeBuff(String buffId) {
    LoadedBuff buff = plugin.getStrifePlugin().getBuffManager().getBuffFromId(buffId);
    return buff;
  }

}
