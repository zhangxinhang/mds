
local json = require("cjson")
local parser = require("redis.parser")
local group_id =json.decode(ngx.req.get_uri_args().group_id)
local formulas = json.decode(ngx.req.get_uri_args(0).formulas)
local querydates = json.decode(ngx.req.get_uri_args(0).querydates)
local bcpQuery = require("conf.bcpPublicQuery")

local retVals = {} 
for i=1,#formulas do
	local value = 0
	local formula_name = formulas[i]
	local valueObj = {}
	for j=1,#querydates do
		local datetoday = querydates[j];
		local key = datetoday..":"..group_id..":"..formula_name
		local key_value = bcpQuery.getValue(key)
		if key_value==nil then
			key_value = 0
		end
		valueObj[datetoday] = string.format("%.4f",key_value)
		value = tonumber(key_value)+value
	end
	if tonumber(value)<0 then
		value = 0
	end
	valueObj["sum_day"] = string.format("%.4f",value)
	retVals[formula_name] = valueObj
end
ngx.say(json.encode(retVals))