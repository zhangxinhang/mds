local json = require("cjson")
local parser = require("redis.parser")
local bcpUtil= require("bcp.bcpUtil")
local instance = bcpUtil.getRedisCon()
--所查库存组织ID
local group =json.decode(ngx.req.get_uri_args().group)
--所查产品IDs
local pros = json.decode(ngx.req.get_uri_args(0).keys)
local tab = {}
for i=1,#pros do
  local key_args = pros[i]
  local true_key =group..":".. key_args 
  local key_value = instance:get(true_key)
  if key_value ==nil or key_value==ngx.null then
      key_value = "0.0000"
  else
	  if tonumber(key_value)<0 then
			key_value ="0.0000"
	  end
  end
  tab[key_args]=bcpUtil.format_value(key_value)
end
bcpUtil.close(instance);
ngx.say(json.encode(tab))

