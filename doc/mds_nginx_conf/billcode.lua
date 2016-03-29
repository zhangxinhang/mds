local bcpUtil= require("bcp.bcpUtil")
local instance = bcpUtil.getRedisCon()
--通过每秒数据自增获取单位秒内的自增值
local incrkey = "bcpbillcode"..os.date("%y%m%d%H%M%S");
instance:setnx(incrkey, 0);
instance:expire(incrkey,1);
instance:incr(incrkey);
local billcode = instance:get(incrkey);

bcpUtil.close(instance);
--生成单据号 e.g 2014122018200801
ngx.say(os.date("%Y%m%d%H%M%S")..string.format("%02d",billcode));