local json = require("cjson")
local parser = require("redis.parser")
local groups =json.decode(ngx.req.get_uri_args().groups)
local pros = json.decode(ngx.req.get_uri_args(0).keys)
local bcpQuery = require("conf.bcpPublicQuery")
local grouparr = bcpQuery.getGroupValue(groups)
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
		local key_value = bcpQuery.getValue(key)
		if key_value==nil then
			key_value = 0
		end
		value = tonumber(key_value)+value
	end
	if tonumber(value)<0 then
		value = 0
	end
	retVals[key_args] = bcpQuery.format_value(string.format("%.4f",value))
end
ngx.say(json.encode(retVals))