local bcpUtil = {}

--获取redis 连接 get connect from redis 
function bcpUtil.getRedisCon()
	local redis = require "resty.redis"
	local config = ngx.shared.config
	--https://github.com/openresty/lua-resty-redis
	local instance = redis:new()
	local host = config:get("host")
	local port = config:get("port")
	local timeout = config:get("timeout")
	local max_idle_timeout = config:get("max_idle_timeout")
	local pool_size = config:get("pool_size")
	instance:set_timeout(timeout)
	instance:set_keepalive(max_idle_timeout, pool_size)
	local ok, err = instance:connect(host, port)
	if not ok then
		ngx.log(ngx.ERR, err)
		ngx.exit(ngx.HTTP_SERVICE_UNAVAILABLE)
	end
	return instance
end

--格式化value e.g 1000000 to 1,000,000
function bcpUtil.format_value(n)
	local left,num,right = string.match(n,'^([^%d]*%d)(%d*)(.-)$')
        return left..(num:reverse():gsub('(%d%d%d)','%1,'):reverse())..right
end



return bcpUtil 
