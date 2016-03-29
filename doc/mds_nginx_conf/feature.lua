
local json = require("cjson")
local parser = require("redis.parser")
local group_id =json.decode(ngx.req.get_uri_args().group_id)
local formulas = json.decode(ngx.req.get_uri_args(0).formulas)
local querydates = json.decode(ngx.req.get_uri_args(0).querydates)
local bcpUtil= require("bcp.bcpUtil")
local instance = bcpUtil.getRedisCon()

local retVals = {} 
for i=1,#formulas do
	local value = 0
	local formula_name = formulas[i]
	local valueObj = {}
	for j=1,#querydates do
		local datetoday = querydates[j];
		local key = datetoday..":"..group_id..":"..formula_name
		local key_value = instance:get(key)
		if key_value ==nil or key_value==ngx.null then
			key_value = 0
		end
		value = tonumber(key_value)+value
		valueObj[datetoday] = string.format("%.4f",key_value)
	end
	if tonumber(value)<0 then
		value = 0
	end
	valueObj["sum_day"] = string.format("%.4f",value)
	retVals[formula_name] = valueObj
end

bcpUtil.close(instance);
ngx.say(json.encode(retVals))