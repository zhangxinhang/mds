local bcpQuery = { }
local parser = require("redis.parser")

function bcpQuery.format_value(n)
	local left,num,right = string.match(n,'^([^%d]*%d)(%d*)(.-)$')
        return left..(num:reverse():gsub('(%d%d%d)','%1,'):reverse())..right
end

function bcpQuery.getGroupValue(a_key)
        local res = ngx.location.capture("/get",{
                args = { key = a_key }
        })
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
	  return nil
        end
end

function bcpQuery.getValue(a_key)
        local res = ngx.location.capture("/get_redis",{
                args = { key = a_key }
        })
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
	  return nil
        end
end
return bcpQuery 
