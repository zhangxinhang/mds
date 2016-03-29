-- Copyright (C) by zxh

local redis = require "resty.redis"
local config = ngx.shared.config
local host = config:get("host")
local port = config:get("port")
local timeout = config:get("timeout")
local max_idle_timeout = config:get("max_idle_timeout")
local pool_size = config:get("pool_size")

local _M = { _VERSION = '0.01' }

--get connect from redis 
function _M.getRedisCon()
	--https://github.com/openresty/lua-resty-redis
	local instance = redis:new()
	instance:set_timeout(timeout)
	local ok, err = instance:connect(host, port)
	if not ok then
		ngx.log(ngx.ERR, err)
		ngx.exit(ngx.HTTP_SERVICE_UNAVAILABLE)
	end
	return instance
end

--close connect set_keepalive
function _M.close(instance)
	local ok,err = instance:set_keepalive(max_idle_timeout, pool_size)
	if not ok then
		instance:close()
		ngx.log(ngx.ERR,err)
	end
end

--fomat value e.g 1000000 to 1,000,000
function _M.format_value(n)
	local left,num,right = string.match(n,'^([^%d]*%d)(%d*)(.-)$')
        return left..(num:reverse():gsub('(%d%d%d)','%1,'):reverse())..right
end

return _M