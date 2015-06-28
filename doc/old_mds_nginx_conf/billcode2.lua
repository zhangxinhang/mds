local parser = require("redis.parser")

function setValue(a_key)
        local res = ngx.location.capture("/billcodesetexpire",{
                args = { key = a_key }
        })
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
			return "error"
        end
end
function getValue(a_key)
        local res = ngx.location.capture("/billcodeincr2",{
                args = { key = a_key }
        })
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
			return "error"
        end
end
local incrkey = "bcpbillcode"..os.date("%y%m%d%H%M%S")
setValue(incrkey)
ngx.say(os.date("%Y%m%d%H%M%S")..string.format("%02d",getValue(incrkey)))

