local parser = require("redis.parser")
function getValue()
        local res = ngx.location.capture("/billcodeset")
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
		  return nil;
        end
end
function getincr()
        local res = ngx.location.capture("/billcodeincr")
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
		  return nil;
        end
end
local retset = getValue()
if retset==nil then
	ngx.say("retseterror")
	return 
end
local testmessage = "no use"
if retset==1 then
	testmessage = "use"
	ngx.location.capture("/billcodeexpire")
end
local random_count = getincr()
if random_count==nil then
	ngx.say("randomerror")
	return 
end
ngx.say(testmessage..os.date("%y%m%d%H%M%S")..string.format("%04d",random_count))

