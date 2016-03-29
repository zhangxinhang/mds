local json = require("cjson")
local parser = require("redis.parser")
local key =ngx.req.get_uri_args().key
local bcpUtil= require("bcp.bcpUtil")
local instance = bcpUtil.getRedisCon()
local ret_value =instance:get(key);
bcpUtil.close(instance);
ngx.say(json.encode(ret_value))