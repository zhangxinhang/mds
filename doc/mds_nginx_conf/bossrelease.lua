local json = require("cjson")
local parser = require("redis.parser")
--统计所对应库存组织内所查产品的数量和
--库存组织set结合的key.
local groups =json.decode(ngx.req.get_uri_args().groups)
--产品key值
local pros = json.decode(ngx.req.get_uri_args(0).keys)
local bcpUtil= require("bcp.bcpUtil")
local instance = bcpUtil.getRedisCon()
--获取对应key值的库存组织ID列表
local grouparr = instance:smembers(groups);
if grouparr==nil then
	ngx.say("error")
	return 
end
local retVals = {} 
for i=1,#pros do
	local value = 0
	local key_args = pros[i]
	for j=1,#grouparr do
		local key = grouparr[j]..":"..key_args
		local key_value = instance:get(key)
		if key_value ==nil or key_value==ngx.null then
			key_value = 0
		end
		value = tonumber(key_value)+value
	end
	if tonumber(value)<0 then
		value = 0
	end
	retVals[key_args] = bcpUtil.format_value(string.format("%.4f",value))
end
bcpUtil.close(instance);
ngx.say(json.encode(retVals))